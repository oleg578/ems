package ems;

import java.util.*;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private final String NULL_EMPLOYEE_NAME = "employee name must not be null";
    private final String NULL_EMPLOYEE_LIST = "employee list must not be null";

    // LinkedHashMap keeps insertion order
    private final Map<String, Employee> employeesByName = new LinkedHashMap<>();

    @Override
    public void add(Employee employee) {
        Objects.requireNonNull(employee, NULL_EMPLOYEE_NAME);
        if (employeesByName.putIfAbsent(employee.getName(), employee) != null) {
            throw new IllegalArgumentException("employee already exists: " + employee.getName());
        }
    }

    @Override
    public void addAll(List<Employee> employees) {
        Objects.requireNonNull(employees, NULL_EMPLOYEE_LIST);
        // Validate everything first, so a failure leaves the map untouched
        Set<String> newNames = new HashSet<>();
        for (Employee employee : employees) {
            Objects.requireNonNull(employee, NULL_EMPLOYEE_NAME);
            String name = employee.getName();
            if (employeesByName.containsKey(name) || !newNames.add(name)) {
                throw new IllegalArgumentException("employee already exists: " + name);
            }
        }
        for (Employee employee : employees) {
            employeesByName.put(employee.getName(), employee);
        }
    }

    @Override
    public void update(Employee employee) {
        requireStoredWithSameRole(employee);
        employeesByName.put(employee.getName(), employee);
    }

    @Override
    public void updateAll(List<Employee> employees) {
        Objects.requireNonNull(employees, NULL_EMPLOYEE_LIST);
        // Validate everything first, so a failure leaves the map untouched
        for (Employee employee : employees) {
            requireStoredWithSameRole(employee);
        }
        for (Employee employee : employees) {
            employeesByName.put(employee.getName(), employee);
        }
    }

    // An update must not silently turn, e.g., a Developer into a Manager
    private void requireStoredWithSameRole(Employee employee) {
        Objects.requireNonNull(employee, NULL_EMPLOYEE_NAME);
        Employee stored = employeesByName.get(employee.getName());
        if (stored == null) {
            throw new IllegalArgumentException("employee not found: " + employee.getName());
        }
        if (stored.getClass() != employee.getClass()) {
            throw new IllegalArgumentException("cannot change role of " + employee.getName()
                    + " from " + stored.getRole() + " to " + employee.getRole());
        }
    }

    @Override
    public List<Employee> findAll() {
        return List.copyOf(employeesByName.values());
    }

    @Override
    public Optional<Employee> findByName(String name) {
        return Optional.ofNullable(employeesByName.get(name));
    }
}
