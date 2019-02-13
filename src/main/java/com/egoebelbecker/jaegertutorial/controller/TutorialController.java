package com.egoebelbecker.jaegertutorial.controller;

import com.egoebelbecker.jaegertutorial.model.Employee;
import com.egoebelbecker.jaegertutorial.service.EmployeeService;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RestController
@Api(value = "jaegertutorial", description = "Jaeger Tutorial service")
public class TutorialController {

    private EmployeeService employeeService;
    private Tracer tracer;

    public TutorialController(Tracer tracer, EmployeeService employeeService) {
        this.tracer = tracer;
        this.employeeService = employeeService;
    }


    public void init() {

        // Add some employees
        employeeService.addEmployee(Employee.builder()
                .employeeId(1)
                .email("john@doe.com")
                .firstName("John")
                .lastName("Doe")
                .phone("555-1212")
                .build());

        employeeService.addEmployee(Employee.builder()
                .employeeId(2)
                .email("jenny@doe.com")
                .firstName("Jenny")
                .lastName("Doe")
                .phone("867-5309")
                .build());

        employeeService.addEmployee(Employee.builder()
                .employeeId(3)
                .email("clark@doe.com")
                .firstName("Clark")
                .lastName("Kent")
                .phone("555-1213")
                .build());

    }


    @ApiOperation(value = "Create Employee ", response = ResponseEntity.class)
    @RequestMapping(value = "/api/tutorial/1.0/employees", method = RequestMethod.POST)
    public ResponseEntity createEmployee(@RequestBody Employee employee) {

        Span span = tracer.buildSpan("create employee").start();

        HttpStatus status = HttpStatus.FORBIDDEN;

        log.info("Receive Request to add employee {}", employee);
        if (employeeService.addEmployee(employee)) {
            status = HttpStatus.CREATED;
            span.setTag("http.status_code", 201);
        } else {
            span.setTag("http.status_code", 403);
        }
        span.finish();
        return new ResponseEntity(null, status);
    }


    @ApiOperation(value = "Get Employee ", response = ResponseEntity.class)
    @RequestMapping(value = "/api/tutorial/1.0/employees/{id}", method = RequestMethod.GET)
    public ResponseEntity getEmployee(@PathVariable("id") String idString) {

        Employee employee = null;
        HttpStatus status = HttpStatus.NOT_FOUND;

        Span span = tracer.buildSpan("get employee").start();

        try {
            int id = Integer.parseInt(idString);
            log.info("Received Request for employee {}", id);
            employee = employeeService.getEmployee(id)
                    .orElseThrow(() -> new NoSuchElementException("Employee not found."));

            status = HttpStatus.OK;

        } catch (NumberFormatException | NoSuchElementException nfe) {
            log.error("Error getting employee: ", nfe);
        }
        span.finish();
        return new ResponseEntity<>(employee, status );
    }


    @ApiOperation(value = "Get All Employees ", response = ResponseEntity.class)
    @RequestMapping(value = "/api/tutorial/1.0/employees", method = RequestMethod.GET)
    public ResponseEntity getAllEmployees() {

        Span span = tracer.buildSpan("get employees").start();

        log.info("Receive Request to Get All Employees");
        Collection<Employee> employees = employeeService.loadAllEmployees();

        span.finish();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }


    @ApiOperation(value = "Update Employee ", response = ResponseEntity.class)
    @RequestMapping(value = "/api/tutorial/1.0/employees/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateEmployee(@PathVariable("id") String idString, @RequestBody Employee employee) {

        Span span = tracer.buildSpan("update employee").start();

        HttpStatus status = HttpStatus.NO_CONTENT;


        try {
            int id = Integer.parseInt(idString);
            log.info("Received Request to update employee {}", id);

            if (employeeService.updateEmployee(id, employee)) {
               status = HttpStatus.OK;
            }
        } catch (NumberFormatException | NoSuchElementException nfe) {
            // Fall through
        }
        span.finish();
        return new ResponseEntity(null, status);
    }

    @ApiOperation(value = "Patch Employee ", response = ResponseEntity.class)
    @RequestMapping(value = "/api/tutorial/1.0/employees/{id}", method = RequestMethod.PATCH)
    public ResponseEntity patchEmployee(@PathVariable("id") String idString, @RequestBody Employee employee) {

        Span span = tracer.buildSpan("get employees").start();

        HttpStatus status = HttpStatus.NO_CONTENT;

        try {
            int id = Integer.parseInt(idString);
            log.info("Received Request to patch employee {}", id);

            if (employeeService.patchEmployee(id, employee)) {
                return new ResponseEntity(null, HttpStatus.OK);
            }
        } catch (NumberFormatException | NoSuchElementException nfe) {
            // Fall through
        }

        span.finish();
        return new ResponseEntity(null, status);
    }

    @ApiOperation(value = "Delete Employee ", response = ResponseEntity.class)
    @RequestMapping(value = "/api/tutorial/1.0/employees/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteEmployee(@PathVariable("id") String idString) {

        Span span = tracer.buildSpan("delete employee").start();

        HttpStatus status = HttpStatus.NO_CONTENT;

        try {
            int id = Integer.parseInt(idString);
            log.info("Received Request to delete employee {}", id);
            span.log(ImmutableMap.of("event", "delete-request", "value", idString));
            if (employeeService.deleteEmployee(id, span)) {
                span.log(ImmutableMap.of("event", "delete-success", "value", idString));
                span.setTag("http.status_code", 200);
                status = HttpStatus.OK;
            } else {
                span.log(ImmutableMap.of("event", "delete-fail", "value", "does not exist"));
                span.setTag("http.status_code", 204);
            }
        } catch (NumberFormatException | NoSuchElementException nfe) {
            span.log(ImmutableMap.of("event", "delete-fail", "value", idString));
            span.setTag("http.status_code", 204);
        }

        span.finish();
        return new ResponseEntity(null, status);
    }

}
