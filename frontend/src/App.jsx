import { useEffect, useState } from "react";
import {
    getEmployees,
    createEmployee,
    updateEmployee,
    deleteEmployee
} from "./services/employeeService";

import { getDepartments } from "./services/departmentService";
import "./App.css";



function App() {

    const [employees, setEmployees] = useState([]);

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        departmentId: ""
    });

    const [selectedEmployeeId, setSelectedEmployeeId] = useState(null);

    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    // Sorting configuration
    const [sortConfig, setSortConfig] = useState({
        key: null,
        direction: "asc"
    });

    // Handle sorting
    const handleSort = (key) => {
        setSortConfig((currentSort) => {
            if (currentSort.key === key) {
                return {
                    key,
                    direction:
                        currentSort.direction === "asc" ? "desc" : "asc"
                };
            }

            return {
                key,
                direction: "asc"
            };
        });
    };


    // Sort employees based on the current sort configuration
    const sortedEmployees = [...employees].sort((a, b) => {

        if (!sortConfig.key) {
            return 0;
        }

        let aValue = a[sortConfig.key];
        let bValue = b[sortConfig.key];

        if (sortConfig.key === "department") {
            aValue = a.department?.name;
            bValue = b.department?.name;
        }

        if (aValue === null || aValue === undefined) {
            return 1;
        }

        if (bValue === null || bValue === undefined) {
            return -1;
        }

        const comparison = String(aValue)
            .toLowerCase()
            .localeCompare(
                String(bValue).toLowerCase(),
                undefined,
                {
                    numeric: true
                }
            );

        return sortConfig.direction === "asc"
            ? comparison
            : -comparison;
    });


    // Get sort indicator for column headers
    const getSortIndicator = (key) => {
        if (sortConfig.key !== key) {
            return "↕";
        }

        return sortConfig.direction === "asc"
            ? "↑"
            : "↓";
    };

    const [departments, setDepartments] = useState([]);

    // const departments = [
    //     "IT",
    //     "Human Resources",
    //     "Finance",
    //     "Accounting",
    //     "Marketing",
    //     "Sales",
    //     "Operations"
    // ];

    // Load all employees
    const loadEmployees = async () => {
        try {
            const response = await getEmployees();

            setEmployees(response.data);
            setError("");

        } catch (err) {
            console.error(err);
            setError("Failed to load employees.");
        }
    };

    // Load all departments
    const loadDepartments = async () => {
        try {
            const response = await getDepartments();
            setDepartments(response.data);
        } catch (error) {
            console.error("Error loading departments:", error);
            setError("Unable to load departments.");
        }
    };

    // Load employees and departments when page starts
    useEffect(() => {
        loadEmployees();
        loadDepartments();
    }, []);

    // Handle textbox/dropdown changes
    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData({
            ...formData,
            [name]: value
        });
    };

    // Clear form
    const clearForm = () => {

        setFormData({
            firstName: "",
            lastName: "",
            email: "",
            departmentId: ""
        });

        setSelectedEmployeeId(null);
        setMessage("");
        setError("");
    };

    // Create employee
    const handleCreate = async (event) => {

        event.preventDefault();

        try {

            await createEmployee(formData);

            setMessage("Employee created successfully.");
            setError("");

            clearForm();

            await loadEmployees();

        } catch (err) {

            console.error(err);

            setError("Failed to create employee.");
            setMessage("");
        }
    };

    // Select employee for editing
    const handleEdit = (employee) => {

        setSelectedEmployeeId(employee.id);

        setFormData({
            firstName: employee.firstName,
            lastName: employee.lastName,
            email: employee.email,
            departmentId: employee.department.id
        });

        setMessage("");
        setError("");
    };

    // Update employee
    const handleUpdate = async (event) => {

        event.preventDefault();

        if (!selectedEmployeeId) {

            setError("Please select an employee to update.");

            return;
        }

        try {

            await updateEmployee(
                selectedEmployeeId,
                formData
            );

            setMessage("Employee updated successfully.");
            setError("");

            clearForm();

            await loadEmployees();

        } catch (err) {

            console.error(err);

            setError("Failed to update employee.");
            setMessage("");
        }
    };

    // Delete employee
    const handleDelete = async (id) => {

        const confirmed = window.confirm(
            "Are you sure you want to delete this employee?"
        );

        if (!confirmed) {
            return;
        }

        try {

            await deleteEmployee(id);

            setMessage("Employee deleted successfully.");
            setError("");

            if (selectedEmployeeId === id) {
                clearForm();
            }

            await loadEmployees();

        } catch (err) {

            console.error(err);

            setError("Failed to delete employee.");
            setMessage("");
        }
    };

    return (
        <div className="container">

            <h1>Employee Management System</h1>

            {/* Success message */}
            {message && (
                <div className="success-message">
                    {message}
                </div>
            )}

            {/* Error message */}
            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {/* Employee Form */}
            <div className="form-container">

                <h2>
                    {selectedEmployeeId
                        ? "Update Employee"
                        : "Add Employee"}
                </h2>

                <form
                    onSubmit={
                        selectedEmployeeId
                            ? handleUpdate
                            : handleCreate
                    }
                >

                    <div className="form-row">

                        {/* First Name */}
                        <div className="form-group">

                            <label htmlFor="firstName">
                                First Name
                            </label>

                            <input
                                id="firstName"
                                type="text"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleChange}
                                placeholder="Enter first name"
                                required
                            />

                        </div>

                        {/* Last Name */}
                        <div className="form-group">

                            <label htmlFor="lastName">
                                Last Name
                            </label>

                            <input
                                id="lastName"
                                type="text"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleChange}
                                placeholder="Enter last name"
                                required
                            />

                        </div>

                        {/* Email */}
                        <div className="form-group">

                            <label htmlFor="email">
                                Email Address
                            </label>

                            <input
                                id="email"
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="Enter email address"
                                required
                            />

                        </div>

                        {/* Department */}
                        <div className="form-group">

                            <label htmlFor="department">
                                Department
                            </label>

                            <select
                                name="departmentId"
                                value={formData.departmentId}
                                onChange={handleChange}
                            >
                                <option value="">Select Department</option>

                                {departments.map((department) => (
                                    <option
                                        key={department.id}
                                        value={department.id}
                                    >
                                        {department.name}
                                    </option>
                                ))}
                            </select>

                        </div>

                    </div>

                    {/* Form Buttons */}
                    <div className="button-row">

                        {!selectedEmployeeId && (
                            <button
                                type="submit"
                                className="add-button"
                            >
                                Add Employee
                            </button>
                        )}

                        {selectedEmployeeId && (
                            <button
                                type="submit"
                                className="update-button"
                            >
                                Update Employee
                            </button>
                        )}

                        <button
                            type="button"
                            className="clear-button"
                            onClick={clearForm}
                        >
                            Clear
                        </button>

                    </div>

                </form>

            </div>

            {/* Employee Grid */}
            <div className="grid-container">

                <h2>Employees</h2>

                <table className="employee-table">

                    <thead>

                        <tr>
                            <th className="sortable" onClick={() => handleSort("id")}>
                                ID {getSortIndicator("id")}
                            </th>

                            <th className="sortable" onClick={() => handleSort("firstName")}>
                                First Name {getSortIndicator("firstName")}
                            </th>

                            <th className="sortable" onClick={() => handleSort("lastName")}>
                                Last Name {getSortIndicator("lastName")}
                            </th>

                            <th className="sortable" onClick={() => handleSort("email")}>
                                Email {getSortIndicator("email")}
                            </th>

                            <th className="sortable" onClick={() => handleSort("departmentId")}>
                                Department {getSortIndicator("departmentId")}
                            </th>

                            <th>Actions</th>
                        </tr>

                    </thead>

                    <tbody>

                        {employees.length === 0 ? (

                            <tr>

                                <td
                                    colSpan="6"
                                    className="no-data"
                                >
                                    No employees found.
                                </td>

                            </tr>

                        ) : (

                            sortedEmployees.map((employee) => (

                                <tr key={employee.id}>

                                    <td>
                                        {employee.id}
                                    </td>

                                    <td>
                                        {employee.firstName}
                                    </td>

                                    <td>
                                        {employee.lastName}
                                    </td>

                                    <td>
                                        {employee.email}
                                    </td>

                                    <td>
                                        {employee.department?.name}
                                    </td>

                                    <td>

                                        <button
                                            type="button"
                                            className="edit-button"
                                            onClick={() =>
                                                handleEdit(employee)
                                            }
                                        >
                                            Edit
                                        </button>

                                        <button
                                            type="button"
                                            className="delete-button"
                                            onClick={() =>
                                                handleDelete(employee.id)
                                            }
                                        >
                                            Delete
                                        </button>

                                    </td>

                                </tr>

                            ))

                        )}

                    </tbody>

                </table>

            </div>

        </div>
    );
}

export default App;