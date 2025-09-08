export function Header() {
  return (
    <header className="bg-bg-alt border-b border-border p-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-text-col">FFN ML Platform</h1>
        <nav className="flex space-x-6">
          <a href="#">Dashboard</a>
          <a href="#">Models</a>
          <a href="#">Training</a>
          <a href="#">Results</a>
          <a href="#">Settings</a>
        </nav>
      </div>
    </header>
  );
}