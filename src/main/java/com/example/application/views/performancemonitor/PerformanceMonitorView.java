package com.example.application.views.performancemonitor;

import com.example.application.data.entity.Job;
import com.example.application.data.service.JobService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Performance Monitor")
@Route(value = "permon/:jobID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class PerformanceMonitorView extends Div implements BeforeEnterObserver {

    private final String JOB_ID = "jobID";
    private final String JOB_EDIT_ROUTE_TEMPLATE = "permon/%s/edit";

    private Grid<Job> grid = new Grid<>(Job.class, false);

    private TextField jobNo;
    private TextField jobName;
    private DateTimePicker startDate;
    private DateTimePicker duration;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Job> binder;

    private Job job;

    private final JobService jobService;

    @Autowired
    public PerformanceMonitorView(JobService jobService) {
        this.jobService = jobService;
        addClassNames("performance-monitor-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("jobNo").setAutoWidth(true);
        grid.addColumn("jobName").setAutoWidth(true);
        grid.addColumn("startDate").setAutoWidth(true);
        grid.addColumn("duration").setAutoWidth(true);
        grid.setItems(query -> jobService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(JOB_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PerformanceMonitorView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Job.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(jobNo).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("jobNo");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.job == null) {
                    this.job = new Job();
                }
                binder.writeBean(this.job);

                jobService.update(this.job);
                clearForm();
                refreshGrid();
                Notification.show("Job details stored.");
                UI.getCurrent().navigate(PerformanceMonitorView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the job details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> jobId = event.getRouteParameters().get(JOB_ID).map(UUID::fromString);
        if (jobId.isPresent()) {
            Optional<Job> jobFromBackend = jobService.get(jobId.get());
            if (jobFromBackend.isPresent()) {
                populateForm(jobFromBackend.get());
            } else {
                Notification.show(String.format("The requested job was not found, ID = %s", jobId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PerformanceMonitorView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        jobNo = new TextField("Job No");
        jobName = new TextField("Job Name");
        startDate = new DateTimePicker("Start Date");
        startDate.setStep(Duration.ofSeconds(1));
        duration = new DateTimePicker("Duration");
        duration.setStep(Duration.ofSeconds(1));
        Component[] fields = new Component[]{jobNo, jobName, startDate, duration};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Job value) {
        this.job = value;
        binder.readBean(this.job);

    }
}
