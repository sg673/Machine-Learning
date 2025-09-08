interface StatsCardProps {
  title: string;
  value: string | number;
  icon?: string;
}

export function StatsCard({ title, value, icon }: StatsCardProps) {
  return (
    <div className="card">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-text-col-alt text-sm">{title}</p>
          <p className="text-2xl font-bold text-text-col">{value}</p>
        </div>
        {icon && <img src={icon} alt="" className="w-8 h-8" style={{filter: 'brightness(0) saturate(100%) invert(47%) sepia(79%) saturate(2476%) hue-rotate(315deg) brightness(98%) contrast(93%)'}} />}
      </div>
    </div>
  );
}