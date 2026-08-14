import React, { useState } from 'react';
import { ProductBacklog, Epic, Sprint } from '../types/backlog';
import { EpicCard } from './EpicCard';
import { SprintBoard } from './SprintBoard';
import { exportMarkdown, exportPdf } from '../services/api';
import { Download, Copy, RefreshCw, Layers, Calendar, Check, Code, FileText, Edit3, Plus, Trash2 } from 'lucide-react';

interface BacklogViewerProps {
  backlog: ProductBacklog;
  onUpdateBacklog?: (updatedBacklog: ProductBacklog) => void;
  onReset: () => void;
}

export const BacklogViewer: React.FC<BacklogViewerProps> = ({ backlog, onUpdateBacklog, onReset }) => {
  const [activeTab, setActiveTab] = useState<'epics' | 'sprints'>('epics');
  const [isEditMode, setIsEditMode] = useState(false);
  const [isExportingMd, setIsExportingMd] = useState(false);
  const [isExportingPdf, setIsExportingPdf] = useState(false);
  const [copied, setCopied] = useState(false);

  const handleUpdateProjectName = (projectName: string) => {
    if (onUpdateBacklog) {
      onUpdateBacklog({ ...backlog, projectName });
    }
  };

  const handleUpdateSummary = (summary: string) => {
    if (onUpdateBacklog) {
      onUpdateBacklog({ ...backlog, summary });
    }
  };

  const handleUpdateEpic = (updatedEpic: Epic) => {
    if (!onUpdateBacklog) return;
    const newEpics = backlog.epics.map((e) => (e.id === updatedEpic.id ? updatedEpic : e));
    onUpdateBacklog({ ...backlog, epics: newEpics });
  };

  const handleDeleteEpic = (epicId: string) => {
    if (!onUpdateBacklog) return;
    const newEpics = backlog.epics.filter((e) => e.id !== epicId);
    onUpdateBacklog({ ...backlog, epics: newEpics });
  };

  const handleAddEpic = () => {
    if (!onUpdateBacklog) return;
    const newNum = backlog.epics.length + 1;
    const newEpicId = `EPIC-${String(newNum).padStart(2, '0')}`;
    const newEpic: Epic = {
      id: newEpicId,
      title: `Novo Épico ${newNum}`,
      description: 'Descrição do novo épico...',
      userStories: [],
    };
    onUpdateBacklog({ ...backlog, epics: [...backlog.epics, newEpic] });
  };

  const handleUpdateSprint = (sprintId: string, updatedSprint: Sprint) => {
    if (!onUpdateBacklog) return;
    const newSprints = backlog.sprints.map((s) => (s.id === sprintId ? updatedSprint : s));
    onUpdateBacklog({ ...backlog, sprints: newSprints });
  };

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
          <div style={{ flex: 1, minWidth: '280px' }}>
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--primary)', textTransform: 'uppercase', letterSpacing: '0.08em' }}>
              Product Backlog Gerado
            </span>
            {isEditMode ? (
              <input
                type="text"
                className="inline-input"
                value={backlog.projectName}
                onChange={(e) => handleUpdateProjectName(e.target.value)}
                style={{ fontSize: '1.5rem', fontWeight: 700, marginTop: '4px' }}
              />
            ) : (
              <h2 style={{ fontSize: '1.75rem', color: '#ffffff', marginTop: '4px' }}>
                {backlog.projectName}
              </h2>
            )}
          </div>

          {/* Botões de Ação */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
            {/* Toggle Modo Edição */}
            <button
              onClick={() => setIsEditMode(!isEditMode)}
              style={{
                padding: '10px 16px',
                background: isEditMode ? 'rgba(59, 130, 246, 0.25)' : '#1e293b',
                border: '1px solid ' + (isEditMode ? '#3b82f6' : 'var(--border-color)'),
                borderRadius: '8px',
                color: isEditMode ? '#93c5fd' : '#e2e8f0',
                fontWeight: 600,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '0.9rem',
              }}
            >
              <Edit3 size={16} color={isEditMode ? '#60a5fa' : '#94a3b8'} />
              {isEditMode ? 'Modo Edição (ON)' : 'Editar Backlog'}
            </button>

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

        {/* Notificação Modo Edição */}
        {isEditMode && (
          <div
            style={{
              padding: '10px 14px',
              background: 'rgba(59, 130, 246, 0.15)',
              border: '1px solid rgba(59, 130, 246, 0.3)',
              borderRadius: '8px',
              color: '#93c5fd',
              fontSize: '0.875rem',
              marginBottom: '16px',
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
            }}
          >
            <Edit3 size={16} color="#60a5fa" />
            <span>
              <strong>Modo Edição Habilitado:</strong> Altere títulos, descrições, prioridades, Story Points, tarefas e critérios. As mudanças são salvas em memória para download imediato em PDF e Markdown.
            </span>
          </div>
        )}

        {/* Resumo */}
        {isEditMode ? (
          <div style={{ marginBottom: '20px' }}>
            <label style={{ fontSize: '0.75rem', color: '#94a3b8', fontWeight: 600, display: 'block', marginBottom: '4px' }}>
              Resumo do Projeto:
            </label>
            <textarea
              className="inline-textarea"
              rows={3}
              value={backlog.summary || ''}
              onChange={(e) => handleUpdateSummary(e.target.value)}
            />
          </div>
        ) : (
          backlog.summary && (
            <p style={{ color: '#cbd5e1', fontSize: '0.95rem', marginBottom: '20px', lineHeight: 1.6 }}>
              {backlog.summary}
            </p>
          )
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
            <EpicCard
              key={epic.id}
              epic={epic}
              isEditMode={isEditMode}
              onUpdateEpic={handleUpdateEpic}
              onDeleteEpic={handleDeleteEpic}
            />
          ))}

          {isEditMode && (
            <button
              onClick={handleAddEpic}
              style={{
                width: '100%',
                padding: '16px',
                background: 'rgba(99, 102, 241, 0.15)',
                border: '2px dashed var(--primary)',
                borderRadius: '12px',
                color: '#a5b4fc',
                fontWeight: 700,
                fontSize: '1rem',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '10px',
                marginTop: '12px',
              }}
            >
              <Plus size={20} /> Adicionar Novo Épico ao Backlog
            </button>
          )}
        </div>
      ) : (
        <SprintBoard
          backlog={backlog}
          isEditMode={isEditMode}
          onUpdateSprint={handleUpdateSprint}
        />
      )}
    </div>
  );
};
