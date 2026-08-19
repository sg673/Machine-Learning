

interface DropdownProps {
  expanded: boolean;
  color: string;
}

export function DropDown({ expanded, color }: DropdownProps) {
  return (
    <svg
      className={`${color} w-4 h-4 transition-transform ${expanded ? '' : 'rotate-90'}`}
      fill="currentColor"
      viewBox="-6.5 0 32 32" version="1.1" xmlns="http://www.w3.org/2000/svg">
      <title>dropdown</title>
      <path stroke="currentColor" d="M18.813 11.406l-7.906 9.906c-0.75 0.906-1.906 0.906-2.625 0l-7.906-9.906c-0.75-0.938-0.375-1.656 0.781-1.656h16.875c1.188 0 1.531 0.719 0.781 1.656z"></path>
    </svg>
  );
}