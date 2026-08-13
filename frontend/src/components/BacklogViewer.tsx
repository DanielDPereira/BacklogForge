import React, { useState } from 'react';
import { ProductBacklog } from '../types/backlog';
import { EpicCard } from './EpicCard';
import { SprintBoard } from './SprintBoard';
import { exportMarkdown, exportPdf } from '../services/api';
import { Download, Copy, RefreshCw, Layers, Calendar, Check, Code, FileText } from 'lucide-react';

interface BacklogViewerProps {
  backlog: ProductBacklog;
  onReset: () => void;
}

export const BacklogViewer: React.FC<BacklogViewerProps> = ({ backlog, onReset }) => {
  const [activeTab, setActiveTab] = useState<'epics' | 'sprints'>('epics');
  const [isExportingMd, setIsExportingMd] = useState(false);
  const [isExportingPdf, setIsExportingPdf] = useState(false);
  const [copied, setCopied] = useState(false);

  const handleDownloadMarkdown = async () => {
    try {
      setIsExportingMd(true);
      const blob = await exportMarkdown(backlog);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${backlog.projectName.replaceAll(/\s+/g, '_')}_Backlog.md`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      alert('Erro ao exportar arquivo Markdown.');
    } finally {
      setIsExportingMd(false);
    }
  };

  const handleDownloadPdf = async () => {
    try {
      setIsExportingPdf(true);
      const blob = await exportPdf(backlog);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${backlog.projectName.replaceAll(/\s+/g, '_')}_Backlog.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      alert('Erro ao exportar arquivo PDF.');
    } finally {
      setIsExportingPdf(false);
    }
  };

  const handleCopyJson = () => {
    navigator.clipboard.writeText(JSON.stringify(backlog, null, 2));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div>
      {/* Banner Principal do Backlog */}
      <div className="glass-panel" style={{ padding: '32px', marginBottom: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px', marginBottom: '16px' }}>
          <div>
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--primary)', textTransform: 'uppercase', letterSpacing: '0.08em' }}>
              Product Backlog Gerado
            </span>
            <h2 style={{ fontSize: '1.75rem', color: '#ffffff', marginTop: '4px' }}>
              {backlog.projectName}
            </h2>
          </div>

          {/* Botões de Ação */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
            <button
              onClick={handleDownloadPdf}
              disabled={isExportingPdf}
              style={{
                padding: '10px 18px',
                background: 'linear-gradient(135deg, #e11d48, #be123c)',
                border: 'none',
                borderRadius: '8px',
                color: '#ffffff',
                fontWeight: 600,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                fontSize: '0.9rem',
                boxShadow: '0 4px 15px rgba(225, 29, 72, 0.4)',
              }}
            >
              <FileText size={16} /> {isExportingPdf ? 'Gerando PDF...' : 'Baixar PDF (.pdf)'}
            </button>

            <button
              onClick={handleDownloadMarkdown}
              disabled={isExportingMd}
              style={{
                padding: '10px 18px',
                background: 'linear-gradient(135deg, var(--primary), var(--secondary))',
                border: 'none',
                borderRadius: '8px',
                color: '#ffffff',
                fontWeight: 600,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                fontSize: '0.9rem',
                boxShadow: '0 4px 15px var(--primary-glow)',
              }}
            >
              <Download size={16} /> {isExportingMd ? 'Baixando...' : 'Baixar Markdown (.md)'}
            </button>

            <button
              onClick={handleCopyJson}
              style={{
                padding: '10px 16px',
                background: '#1e293b',
                border: '1px solid var(--border-color)',
                borderRadius: '8px',
                color: '#e2e8f0',
                fontWeight: 600,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '0.9rem',
              }}
            >
              {copied ? <Check size={16} color="#34d399" /> : <Copy size={16} />}
              {copied ? 'Copiado!' : 'Copiar JSON'}
            </button>

            <button
              onClick={onReset}
              style={{
                padding: '10px 16px',
                background: 'rgba(239, 68, 68, 0.1)',
                border: '1px solid rgba(239, 68, 68, 0.3)',
                borderRadius: '8px',
                color: '#fca5a5',
                fontWeight: 600,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '0.9rem',
              }}
            >
              <RefreshCw size={16} /> Novo Projeto
            </button>
          </div>
        </div>

        {/* Resumo */}
        {backlog.summary && (
          <p style={{ color: '#cbd5e1', fontSize: '0.95rem', marginBottom: '20px', lineHeight: 1.6 }}>
            {backlog.summary}
          </p>
        )}

        {/* Stack Tecnológica */}
        {backlog.suggestedTechnologies && backlog.suggestedTechnologies.length > 0 && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#94a3b8', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Code size={14} /> Tecnologias:
            </span>
            {backlog.suggestedTechnologies.map((tech) => (
              <span
                key={tech}
                style={{
                  padding: '3px 10px',
                  borderRadius: '12px',
                  background: 'rgba(99, 102, 241, 0.15)',
                  border: '1px solid rgba(99, 102, 241, 0.3)',
                  color: '#a5b4fc',
                  fontSize: '0.8rem',
                  fontWeight: 600,
                }}
              >
                {tech}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* Tabs de Navegação */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '24px' }}>
        <button
          onClick={() => setActiveTab('epics')}
          style={{
            padding: '12px 24px',
            background: activeTab === 'epics' ? 'var(--primary)' : '#151c2c',
            border: '1px solid ' + (activeTab === 'epics' ? 'var(--primary)' : 'var(--border-color)'),
            borderRadius: '10px',
            color: '#ffffff',
            fontWeight: 600,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            transition: 'all 0.2s',
          }}
        >
          <Layers size={18} /> Épicos & User Stories ({backlog.epics.length})
        </button>

        <button
          onClick={() => setActiveTab('sprints')}
          style={{
            padding: '12px 24px',
            background: activeTab === 'sprints' ? 'var(--primary)' : '#151c2c',
            border: '1px solid ' + (activeTab === 'sprints' ? 'var(--primary)' : 'var(--border-color)'),
            borderRadius: '10px',
            color: '#ffffff',
            fontWeight: 600,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            transition: 'all 0.2s',
          }}
        >
          <Calendar size={18} /> Planejamento das Sprints ({backlog.sprints.length})
        </button>
      </div>

      {/* Conteúdo da Tab Selecionada */}
      {activeTab === 'epics' ? (
        <div>
          {backlog.epics.map((epic) => (
            <EpicCard key={epic.id} epic={epic} />
          ))}
        </div>
      ) : (
        <SprintBoard backlog={backlog} />
      )}
    </div>
  );
};
