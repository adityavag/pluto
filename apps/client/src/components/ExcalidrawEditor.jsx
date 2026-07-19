import { useImperativeHandle, forwardRef, useEffect, useState } from 'react';
import '@excalidraw/excalidraw/index.css';

const ExcalidrawEditor = forwardRef(function ExcalidrawEditor(
  { initialData = null },
  ref
) {
  const [excalidrawAPI, setExcalidrawAPI] = useState(null);
  const [Comp, setComp] = useState(null);

  // Dynamically import Excalidraw (it doesn't support SSR and is large)
  useEffect(() => {
    import('@excalidraw/excalidraw').then((module) => {
      setComp(() => module.Excalidraw);
    });
  }, []);

  // Expose getSceneData to parent via ref
  useImperativeHandle(ref, () => ({
    getSceneData: () => {
      if (excalidrawAPI) {
        const elements = excalidrawAPI.getSceneElements();
        const appState = excalidrawAPI.getAppState();
        const files = excalidrawAPI.getFiles();
        return {
          type: 'excalidraw',
          version: 2,
          elements,
          appState: {
            viewBackgroundColor: appState.viewBackgroundColor,
          },
          files,
        };
      }
      return null;
    },
  }));

  if (!Comp) {
    return (
      <div className="flex items-center justify-center h-full bg-lc-bg-primary text-lc-text-secondary text-sm">
        Loading editor...
      </div>
    );
  }

  return (
    <div className="excalidraw-wrapper h-full w-full">
      <Comp
        excalidrawAPI={setExcalidrawAPI}
        theme="dark"
        initialData={initialData}
        UIOptions={{
          canvasActions: {
            loadScene: false,
            export: false,
            saveToActiveFile: false,
          },
        }}
        gridModeEnabled={true}
      />
    </div>
  );
});

export default ExcalidrawEditor;
