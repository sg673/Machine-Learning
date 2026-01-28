import { useState } from 'react';
import { Header } from './components/Header';
import { Dashboard } from './dashboard/dashboard';
import { ModelBuilder } from './modelBuilder';
import { ModelView } from './modelView/modelView';

type Page = 'dashboard' | 'modelBuilder' | 'modelView';

function App() {
  const [currentPage, setCurrentPage] = useState<Page>('dashboard');

  const handlePageChange = (page: string) => {
    console.log(page)
    setCurrentPage(page as Page);
  }
  return (
    <div className="flex h-screen flex-col bg-bg">
      <Header currentPage={currentPage} onPageChange={handlePageChange} />
      {currentPage === 'dashboard' && <Dashboard />}
      {currentPage === 'modelBuilder' && <ModelBuilder />}
      {currentPage === 'modelView' && <ModelView onPageChange={handlePageChange}/>}
    </div>
  )
}

export default App
