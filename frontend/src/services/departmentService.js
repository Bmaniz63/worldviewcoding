import axios from "axios";

const API_BASE_URL = "/api/departments";

export const getDepartments = () =>
    axios.get(API_BASE_URL);