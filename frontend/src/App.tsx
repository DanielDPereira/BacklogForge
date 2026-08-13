import React, { useState } from 'react';
import { GenerateBacklogRequest, ProductBacklog } from './types/backlog';
import { generateBacklog, generateBacklogWithPdf } from './services/api';
import { Header } from './components/Header';
import { ProjectForm } from './components/ProjectForm';
import { BacklogViewer } from './components/BacklogViewer';
import { LoadingOverlay } from './components/LoadingOverlay';
import { AlertCircle } from 'lucide-react';

export const App: React.FC = () => {
  const [backlog, setBacklog] = useState<ProductBacklog | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleFormSubmit = async (request: GenerateBacklogRequest, files: File[]) => {
    setIsLoading(true);
    setErrorMessage(null);

    try {
      let result: ProductBacklog;
      if (files.length > 0) {
        result = await generateBacklogWithPdf(request, files);
      } else {
        result = await generateBacklog(request);
      }
      setBacklog(result);
    } catch (err: any) {
      console.error('Erro na geração do backlog:', err);
      setErrorMessage(
        err.message || 'Falha ao se comunicar com o backend do BacklogForge. Verifique se o servidor está rodando na porta 8080.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = () => {
    setBacklog(null);
    setErrorMessage(null);
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '32px 16px' }}>
      <Header />

      {errorMessage && (
        <div
          style={{
            padding: '16px 20px',
            background: 'rgba(239, 68, 68, 0.15)',
            border: '1px solid rgba(239, 68, 68, 0.4)',
            borderRadius: '12px',
            color: '#fca5a5',
            marginBottom: '24px',
            display: 'flex',
            alignItems: 'center',
            gap: '12px',
            fontSize: '0.95rem',
          }}
        >
          <AlertCircle size={24} color="#ef4444" style={{ flexShrink: 0 }} />
          <div>
            <strong>Ocorreu um erro:</strong> {errorMessage}
          </div>
        </div>
      )}

      {isLoading && <LoadingOverlay />}

      {!backlog ? (
        <ProjectForm onSubmit={handleFormSubmit} isLoading={isLoading} />
      ) : (
        <BacklogViewer backlog={backlog} onReset={handleReset} />
      )}
    </div>
  );
};

export default App;
