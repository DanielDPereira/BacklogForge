import type { GenerateBacklogRequest, ProductBacklog } from '../types/backlog';


const API_BASE_URL = 'http://localhost:8080/api/v1/backlog';

export async function generateBacklog(request: GenerateBacklogRequest): Promise<ProductBacklog> {
  const response = await fetch(`${API_BASE_URL}/generate`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || errorData.error || `Erro HTTP ${response.status} ao gerar backlog.`);
  }

  return response.json();
}

export async function generateBacklogWithPdf(
  request: GenerateBacklogRequest,
  files: File[]
): Promise<ProductBacklog> {
  const formData = new FormData();
  
  const requestBlob = new Blob([JSON.stringify(request)], { type: 'application/json' });
  formData.append('request', requestBlob);

  files.forEach((file) => {
    formData.append('files', file);
  });

  const response = await fetch(`${API_BASE_URL}/generate-with-pdf`, {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || errorData.error || `Erro HTTP ${response.status} ao gerar backlog com PDF.`);
  }

  return response.json();
}

export async function exportMarkdown(backlog: ProductBacklog): Promise<Blob> {
  const response = await fetch(`${API_BASE_URL}/export-markdown`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(backlog),
  });

  if (!response.ok) {
    throw new Error('Falha ao gerar arquivo Markdown para download.');
  }

  return response.blob();
}

export async function exportPdf(backlog: ProductBacklog): Promise<Blob> {
  const response = await fetch(`${API_BASE_URL}/export-pdf`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(backlog),
  });

  if (!response.ok) {
    throw new Error('Falha ao gerar arquivo PDF para download.');
  }

  return response.blob();
}
