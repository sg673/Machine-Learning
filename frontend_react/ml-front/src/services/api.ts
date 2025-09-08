const BASE_URL = "http://localhost:8080/api/v1/";

export const api = {
    get: async (endpoint: string) => await fetch(`${BASE_URL}${endpoint}`),
    post: async (endpoint: string, body: never) => await fetch(`${BASE_URL}${endpoint}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    }),
    put: async (endpoint: string, body: never) => await fetch(`${BASE_URL}${endpoint}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    }),
    delete: async (endpoint: string) => await fetch(`${BASE_URL}${endpoint}`, {
        method: "DELETE"
    }),
    
    test: async () => (await api.get("test")).text(),
}
