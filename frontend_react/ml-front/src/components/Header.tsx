import { useState } from "react";
import { PAGES } from "../services/constants";

export function Header() {
  const [selectedPage, setSelectedPage] = useState(PAGES.DASHBOARD);

  return (
    <header className="bg-bg-alt border-b border-border p-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-text-col">FFN ML Platform</h1>
        <nav className="flex space-x-6">
          <div className={selectedPage == PAGES.DASHBOARD ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => setSelectedPage(PAGES.DASHBOARD)}>Dashboard</a>
          </div>
          <div className={selectedPage == PAGES.MODELS ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => setSelectedPage(PAGES.MODELS)}>Models</a>
          </div>
          <div className={selectedPage == PAGES.TRAINING ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => setSelectedPage(PAGES.TRAINING)}>Training</a>
          </div>
          <div className={selectedPage == PAGES.RESULTS ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => setSelectedPage(PAGES.RESULTS)}>Results</a>
          </div>
          <div className={selectedPage == PAGES.SETTINGS ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => setSelectedPage(PAGES.SETTINGS)}>Settings</a>
          </div>
        </nav>
      </div>
    </header>
  );
}