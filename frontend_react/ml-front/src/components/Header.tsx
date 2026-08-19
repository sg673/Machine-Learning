interface HeaderProps {
  currentPage: string;
  onPageChange: (page: string) => void;
}

export function Header({ currentPage, onPageChange }: HeaderProps) {
  return (
    <header className="bg-bg-alt border-b border-border p-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-text-col">FFN ML Platform</h1>
        <nav className="flex space-x-6">
          <div className={currentPage === 'dashboard' ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => onPageChange('dashboard')}>Dashboard</a>
          </div>
          <div className={currentPage === 'modelBuilder' ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => onPageChange('modelBuilder')}>Model Builder</a>
          </div>
          <div className={currentPage === 'modelView' ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => onPageChange('modelView')}>Models</a>
          </div>
          <div className={currentPage === 'modelTrainer' ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => onPageChange('modelTrainer')}>Training</a>
          </div>
          <div className={currentPage === 'results' ? "border-b-2 border-prim pb-1" : ""}>
            <a href="#" className="text-acc hover:text-acc-hover" onClick={() => onPageChange('results')}>Results</a>
          </div>
        </nav>
      </div>
    </header>
  );
}