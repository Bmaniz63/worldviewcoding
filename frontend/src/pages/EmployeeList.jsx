import React, { useEffect, useState } from 'react';
import { getEmployees } from '../services/employeeService.js';
import EmployeeCard from '../components/EmployeeCard.jsx';

function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    getEmployees()
      .then((response) => setEmployees(response.data))
      .catch((err) => setError(err.message));
  }, []);

  if (error) {
    return <p>Error loading employees: {error}</p>;
  }

  return (
    <div className="employee-list">
      {employees.length === 0 && <p>No employees found.</p>}
      {employees.map((employee) => (
        <EmployeeCard key={employee.id} employee={employee} />
      ))}
    </div>
  );
}

export default EmployeeList;
