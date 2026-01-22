import { useEffect, useState } from "react";
import type { CNNModel } from "../modelBuilder/types";
import { cnnModelApi } from "../services/api";
import ArrowLeft from "../assets/arrow-sm-left.svg";
import ArrowRight from "../assets/arrow-sm-right.svg";

export function ModelList() {
  const [models, setModels] = useState<CNNModel[]>([]);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const itemsPerPage = 6;

  useEffect(() => {
    const fetchModels = async () => {
      const data = await cnnModelApi.getAll();
      setModels(data);
    };
    fetchModels();
  }, []);

  function getRelativeTime(timeCreated: string): string {
    if (timeCreated == null) return 'N/A';
    const now = new Date();
    const created = new Date(timeCreated);
    const diffMs = now.getTime() - created.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return 'just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    return `${diffDays}d ago`;
  }

  const totalPages = Math.ceil(models.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedModels = models.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className="w-120 bg-bg-alt border-r border-border p-6 flex flex-col">
      <h3 className="text-xl font-bold mb-6 text-text-col">Models</h3>
      <div className="space-y-3 flex-1">
        {models.length === 0 ? (
          <p className="text-text-col-alt text-center py-8">No models found</p>
        ) : (
          paginatedModels.map((model) => (
            <div key={model.modelId} className="card-xs hover:bg-bg transition-colors cursor-pointer"
              onClick={() => console.log(`Model ${model.modelId} clicked`)}
            >
              <div className="flex justify-between items-start mb-2">
                <h4 className="font-semibold text-text-col truncate">{model.name}</h4>
                <div className="flex gap-2">
                  <span className="text-xs text-text-col bg-bg px-2 py-1 rounded">
                    {model.trainingData}
                  </span>
                  <span className="text-xs text-text-col bg-bg px-2 py-1 rounded">
                    {model.layers.length} layers
                  </span>
                </div>
              </div>
              <p className="text-sm text-text-col-alt">created: {getRelativeTime(model.timeCreated)}</p>
              <div className="flex gap-2 justify-end">
                <button className="px-3 py-1 text-xs bg-sec text-text-col-alt rounded hover:bg-sec-hover">Edit</button>
                <button className="px-3 py-1 text-xs bg-acc text-text-col-alt rounded hover:bg-acc-hover">Train</button>
                <button className="px-3 py-1 text-xs bg-prim text-text-col-alt rounded hover:bg-prim-hover">Delete</button>
              </div>
            </div>
          ))
        )}
      </div>
      {totalPages > 0 && (
        <div className="flex justify-center gap-2 mt-4">
          <button
            className="px-3 py-1 text-xs bg-bg border border-border rounded hover:bg-bg-alt"
            onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
            disabled={currentPage === 1}
          >
            <span className="text-text-col">
              <img src={ArrowLeft} alt="Prev" className="w-4 h-4 filter brightness-0 invert" />
            </span>
          </button>
          <span className="px-3 py-1 text-xs bg-bg border border-border rounded text-text-col-alt">
            {currentPage} of {totalPages}
          </span>
          <button
            className="px-3 py-1 text-xs bg-bg border border-border rounded hover:bg-bg-alt"
            onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
            disabled={currentPage === totalPages}
          >
            <span className="text-text-col">
              <img src={ArrowRight} alt="Next" className="w-4 h-4 filter brightness-0 invert" />

            </span>
          </button>
        </div>
      )}
    </div>
  );
}
