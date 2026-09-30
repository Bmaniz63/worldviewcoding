import { useEffect, useState } from "react";
import {
    getEmployees,
    createEmployee,
    updateEmployee,
    deleteEmployee
} from "./services/employeeService";

import "./App.css";

function App() {

    const [employees, setEmployees] = useState([]);

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        department: ""
    });

    const [selectedEmployeeId, setSelectedEmployeeId] = useState(null);

    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const departments = [
        "IT",
        "Human Resources",
        "Finance",
        "Accounting",
        "Marketing",
        "Sales",
        "Operations"
    ];

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

    // Load employees when page starts
    useEffect(() => {
        loadEmployees();
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
            department: ""
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
            department: employee.department
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
                                id="department"
                                name="department"
                                value={formData.department}
                                onChange={handleChange}
                                required
                            >

                                <option value="">
                                    -- Select Department --
                                </option>

                                {departments.map((department) => (
                                    <option
                                        key={department}
                                        value={department}
                                    >
                                        {department}
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
                            <th>ID</th>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Email</th>
                            <th>Department</th>
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

                            employees.map((employee) => (

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
                                        {employee.department}
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