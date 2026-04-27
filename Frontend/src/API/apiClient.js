import axios from "axios";

const api = axios.create({
    baseURL: "http://172.31.250.140:8080",
    headers: {
        "Content-Type": "application/json",
    },
});

export default api;