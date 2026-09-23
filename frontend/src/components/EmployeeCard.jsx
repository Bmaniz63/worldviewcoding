import React from 'react';

function EmployeeCard({ employee }) {
  return (
    <div className="employee-card">
      <h3>{employee.firstName} {employee.lastName}</h3>
      <p>{employee.email}</p>
      <p>{employee.department}</p>
    </div>
  );
}

export default EmployeeCard;
