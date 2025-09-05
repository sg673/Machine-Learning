const BASE_URL = "http://localhost:8080/api/v1/";

export const api = {
    get: async (endpoint: string) => await fetch(`${BASE_URL}${endpoint}`),
    
    test: async () => (await api.get("test")).text(),
}
