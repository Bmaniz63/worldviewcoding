import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api/employees';

export const getEmployees = () => axios.get(API_BASE_URL);

export const getEmployeeById = (id) => axios.get(`${API_BASE_URL}/${id}`);

export const createEmployee = (employee) => axios.post(API_BASE_URL, employee);

export const updateEmployee = (id, employee) => axios.put(`${API_BASE_URL}/${id}`, employee);

export const deleteEmployee = (id) => axios.delete(`${API_BASE_URL}/${id}`);
