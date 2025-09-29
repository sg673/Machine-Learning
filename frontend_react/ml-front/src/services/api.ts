import type { model_values } from "./constants";

const BASE_URL = "http://localhost:8080/api/v1/";

export const api = {
  get: async (endpoint: string) => await fetch(`${BASE_URL}${endpoint}`),
  post: async (endpoint: string, body: unknown) => await fetch(`${BASE_URL}${endpoint}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  }),
  put: async (endpoint: string, body: unknown) => await fetch(`${BASE_URL}${endpoint}`, {
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

  getDashboardStats: async () => (await api.get("dashboard/stats")).json(),

  getModels: async () => (await api.get("models")).json(),
  deleteModelById: async (id: string) => await api.delete(`models/${id}`),
  getModelById: async (id: string) => (await api.get(`models/${id}`)).json(),
  createModel: async (values: model_values) => (await api.post("models", values)).json(),

  startTraining: async (values: model_values) => (await api.post("training/start", values)).json(),
  getTrainingStatus: async (id: string) => (await api.get(`training/${id}/status`)).json(),
  stopTraining: async (id: string) => await api.post(`training/${id}/stop`, {}),


  getResults: async () => (await api.get("results")).json(),
  getResultById: async (id: string) => (await api.get(`results/${id}`)).json(),
}

const request = async (endpoint: string, options: RequestInit = {}) => {
  try {
    const res = await fetch(`${BASE_URL}${endpoint}`, options);
    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(`API Error ${res.status}: ${errorText}`);
    }
    return res;
  } catch (err) {
    console.error("API Request Failed", err);
    throw err;
  }
}

const apiClient = {
  get: (endpoint: string) => request(endpoint),

  post: (endpoint: string, body?: unknown) => request(endpoint, {
    method: "POST",
    headers: {"Content-Type":"application/json"},
    body: JSON.stringify(body)
  }),

  put: (endpoint: string, body?: unknown) => request(endpoint, {
    method: "PUT",
    headers: {"Content-Type":"application/json"},
    body: JSON.stringify(body)
  }),
  
  delete: (endpoint: string) => request(endpoint, {method: "DELETE"})
}