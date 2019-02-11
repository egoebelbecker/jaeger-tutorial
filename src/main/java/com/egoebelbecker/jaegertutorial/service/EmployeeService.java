package com.egoebelbecker.jaegertutorial.service;

import com.egoebelbecker.jaegertutorial.model.Employee;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.springframework.stereotype.Service;

import java.util.*;

// This is incredibly optimistic code
@Service
public class EmployeeService {

    //Add jedis
    private Map<Integer, Employee> employeeMap = new HashMap<>();

    private Tracer tracer;

    public EmployeeService(Tracer tracer) {
        this.tracer = tracer;
    }


    // Create
    public boolean addEmployee(Employee employee) {
        if (employeeMap.containsKey(employee.getEmployeeId())) {
            return false;
        } else {
            employeeMap.put(employee.getEmployeeId(), employee);
            return true;
        }
    }

    // Read One
    public Optional<Employee> getEmployee(int id) {
        return Optional.ofNullable(employeeMap.get(id));
    }

    // Read all
    public Collection<Employee> loadAllEmployees() {
        return employeeMap.values();
    }

    // Update
    public boolean updateEmployee(int id, Employee employee) {
        if (employeeMap.containsKey(id)) {
            employeeMap.remove(id);
            employeeMap.put(employee.getEmployeeId(), employee);
            return true;
        } else {
            return false;
        }

    }

    // Update
    public boolean patchEmployee(int id, Employee employee) {

        if (employeeMap.containsKey(id)) {

            Employee origEmployee = employeeMap.remove(id);

            if (employee.getEmployeeId() != 0) {
                origEmployee.setEmployeeId(employee.getEmployeeId());
            }

            if ((employee.getEmail() != null) && (!employee.getEmail().isEmpty())) {
                origEmployee.setEmail(employee.getEmail());
            }

            if ((employee.getFirstName() != null) && (!employee.getFirstName().isEmpty())) {
                origEmployee.setFirstName(employee.getFirstName());
            }

            if ((employee.getLastName() != null) && (!employee.getLastName().isEmpty())) {
                origEmployee.setLastName(employee.getLastName());
            }

            if ((employee.getPhone() != null) && (!employee.getPhone().isEmpty())) {
                origEmployee.setPhone(employee.getPhone());
            }

            employeeMap.put(origEmployee.getEmployeeId(), origEmployee);
            return true;
        } else {
            return false;
        }

    }

    // Delete
    public boolean deleteEmployee(int id, Span rootSpan) {

        Span span = tracer.buildSpan("service delete employee").asChildOf(rootSpan).start();

        boolean result = false;
        if (employeeMap.containsKey(id)) {
            employeeMap.remove(id);
            result = true;
        }
        span.finish();
        return result;
    }

}
