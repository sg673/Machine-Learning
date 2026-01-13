import type { CNNModel } from "../modelBuilder/types";
import type { Model, Training, Result } from "./constants";

const BASE_URL = "http://localhost:8080/api/v1/";

const request = async (endpoint: string, options: RequestInit = {}, timeout = 10000) => {
  const controller = new AbortController();
  const id = setTimeout(() => controller.abort(), timeout);
  try {
    const res = await fetch(`${BASE_URL}${endpoint}`, { ...options, signal: controller.signal });
    clearTimeout(id);
    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(`API Error ${res.status}: ${errorText}`);
    }
    return res;
  } catch (err) {
    clearTimeout(id);
    console.error("API Request Failed", err);
    throw err;
  }
}

const apiClient = {
  get: (endpoint: string) => request(endpoint),

  post: (endpoint: string, body?: unknown) => request(endpoint, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  }),

  put: (endpoint: string, body?: unknown) => request(endpoint, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  }),

  delete: (endpoint: string) => request(endpoint, { method: "DELETE" })
}

const getJson = async <T>(endpoint: string): Promise<T> =>
  (await apiClient.get(endpoint)).json();

export const testApi = {
  ping: async () => (await apiClient.get("test")).text(),
};

export const dashboardApi = {
  getStats: () => getJson<Record<string, string>>("dashboard/stats"),
};

export const modelApi = {
  getAll: () => getJson<Model[]>("models"),
  getById: (id: string) => getJson<Model>(`models/${id}`),
  create: (values: Model) =>
    apiClient.post("models", values).then(res => res.json()),
  delete: (id: string) =>
    apiClient.delete(`models/${id}`).then(res => res.json()),
};

export const trainingApi = {
  start: (values: Model) =>
    apiClient.post("training/start", values).then(res => res.json()),
  status: (id: string) =>
    getJson<Training>(`training/${id}/status`),
  stop: (id: string) =>
    apiClient.post(`training/${id}/stop`, {}).then(res => res.json()),
};

export const resultsApi = {
  getAll: () => getJson<Result>("results"),
  getById: (id: string) => getJson<Result>(`results/${id}`),
}

export const cnnModelApi = {
  create: (model: CNNModel) => apiClient.post(`models/cnn`, model).then(res => res.json()),
  getAll: () => getJson<CNNModel[]>(`models/cnn`),
  getById: (id: string) => getJson<CNNModel>(`models/cnn/${id}`),
  delete: (id: string) => apiClient.delete(`models/cnn/${id}`).then(res => res.json()),

}