import axios from "axios";

const api = axios.create({
    baseURL: "http://172.31.253.154:8080/",
    headers: { "Content-Type": "application/json" },
});

export default api;
