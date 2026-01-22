import { ModelList } from "./modelList";
import { ModelPreview } from "./modelPreview";
import { ModelProperties } from "./modelProperties";


export function ModelView() {
  return (
    <div className="h-screen flex bg-bg">
      <ModelList />
      <ModelPreview />
      <ModelProperties />
    </div>
  );
}