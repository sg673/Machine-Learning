import { useState } from 'react';
import { Header } from './components/Header';
import { Dashboard } from './dashboard/dashboard';
import { ModelBuilder } from './modelBuilder';

type Page = 'dashboard' | 'modelBuilder';

function App() {
  const [currentPage, setCurrentPage] = useState<Page>('dashboard');

  return (
    <div className="flex h-screen flex-col bg-bg">
      <Header currentPage={currentPage} onPageChange={(page) => setCurrentPage(page as Page)} />
      {currentPage === 'dashboard' && <Dashboard />}
      {currentPage === 'modelBuilder' && <ModelBuilder />}
    </div>
  )
}

export default App
