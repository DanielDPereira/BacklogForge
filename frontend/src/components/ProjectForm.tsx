import React, { useState } from 'react';
import { GenerateBacklogRequest } from '../types/backlog';
import { PdfUploader } from './PdfUploader';
import { Sparkles, Plus, X, FolderGit2, Users, Calendar, Clock, Code, FileText, CheckCircle2 } from 'lucide-react';

interface ProjectFormProps {
  onSubmit: (request: GenerateBacklogRequest, files: File[]) => void;
  isLoading: boolean;
}

export const ProjectForm: React.FC<ProjectFormProps> = ({ onSubmit, isLoading }) => {
  const [projectName, setProjectName] = useState('API Integradora FATEC');
  const [sprintCount, setSprintCount] = useState<number>(6);
  const [sprintDurationWeeks, setSprintDurationWeeks] = useState<number>(2);
  const [teamSize, setTeamSize] = useState<number>(4);
  const [technologies, setTechnologies] = useState<string[]>(['Java', 'Spring Boot', 'React', 'PostgreSQL']);
  const [newTech, setNewTech] = useState('');
  const [suggestTechnologies, setSuggestTechnologies] = useState(false);
  const [additionalText, setAdditionalText] = useState('');
  const [files, setFiles] = useState<File[]>([]);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const handleAddTech = () => {
    if (newTech.trim() && !technologies.includes(newTech.trim())) {
      setTechnologies([...technologies, newTech.trim()]);
      setNewTech('');
    }
  };

  const handleRemoveTech = (tech: string) => {
    setTechnologies(technologies.filter((t) => t !== tech));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg(null);

    if (!projectName.trim()) {
      setErrorMsg('Por favor, informe o nome do projeto.');
      return;
    }

    if (sprintCount < 1 || sprintCount > 20) {
      setErrorMsg('O número de Sprints deve estar entre 1 e 20.');
      return;
    }

    const request: GenerateBacklogRequest = {
      projectName: projectName.trim(),
      sprintCount,
      sprintDurationWeeks,
      teamSize,
      technologies: suggestTechnologies ? [] : technologies,
      suggestTechnologies,
      additionalText: additionalText.trim() || undefined,
    };

    onSubmit(request, files);
  };

  return (
    <form onSubmit={handleSubmit} className="glass-panel" style={{ padding: '32px' }}>
      <h2 style={{ fontSize: '1.25rem', color: '#f8fafc', marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '10px' }}>
        <FolderGit2 size={22} color="var(--primary)" /> Configuração do Projeto e Parâmetros
      </h2>

      {errorMsg && (
        <div style={{ padding: '12px 16px', background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.4)', borderRadius: '8px', color: '#fca5a5', marginBottom: '24px', fontSize: '0.9rem' }}>
          {errorMsg}
        </div>
      )}

      {/* Grid Principal */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '20px', marginBottom: '24px' }}>
        
        {/* Nome do Projeto */}
        <div style={{ gridColumn: '1 / -1' }}>
          <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#cbd5e1', marginBottom: '6px' }}>
            Nome do Projeto *
          </label>
          <input
            type="text"
            value={projectName}
            onChange={(e) => setProjectName(e.target.value)}
            placeholder="Ex: BacklogForge - Sistema de Gestão de Vendas"
            required
          />
        </div>

        {/* Número de Sprints */}
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#cbd5e1', marginBottom: '6px' }}>
            <Calendar size={14} style={{ display: 'inline', marginRight: '4px' }} /> Número de Sprints *
          </label>
          <input
            type="number"
            min={1}
            max={20}
            value={sprintCount}
            onChange={(e) => setSprintCount(parseInt(e.target.value) || 1)}
            required
          />
        </div>

        {/* Duração da Sprint */}
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#cbd5e1', marginBottom: '6px' }}>
            <Clock size={14} style={{ display: 'inline', marginRight: '4px' }} /> Duração da Sprint (semanas) *
          </label>
          <input
            type="number"
            min={1}
            max={8}
            value={sprintDurationWeeks}
            onChange={(e) => setSprintDurationWeeks(parseInt(e.target.value) || 1)}
            required
          />
        </div>

        {/* Tamanho da Equipe */}
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#cbd5e1', marginBottom: '6px' }}>
            <Users size={14} style={{ display: 'inline', marginRight: '4px' }} /> Tamanho da Equipe (integrantes) *
          </label>
          <input
            type="number"
            min={1}
            max={50}
            value={teamSize}
            onChange={(e) => setTeamSize(parseInt(e.target.value) || 1)}
            required
          />
        </div>
      </div>

      {/* Tecnologias */}
      <div style={{ marginBottom: '24px' }}>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#cbd5e1', marginBottom: '6px' }}>
          <Code size={14} style={{ display: 'inline', marginRight: '4px' }} /> Tecnologias do Projeto
        </label>
        
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '12px' }}>
          <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '0.9rem', color: '#a5b4fc' }}>
            <input
              type="checkbox"
              checked={suggestTechnologies}
              onChange={(e) => setSuggestTechnologies(e.target.checked)}
              style={{ width: '16px', height: '16px', cursor: 'pointer' }}
            />
            Permitir que a Inteligência Artificial sugira as tecnologias ideais
          </label>
        </div>

        {!suggestTechnologies && (
          <div>
            <div style={{ display: 'flex', gap: '8px', marginBottom: '12px' }}>
              <input
                type="text"
                value={newTech}
                onChange={(e) => setNewTech(e.target.value)}
                placeholder="Adicionar tecnologia (ex: Python, Docker, Flutter...)"
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                    handleAddTech();
                  }
                }}
              />
              <button
                type="button"
                onClick={handleAddTech}
                style={{
                  padding: '0 16px',
                  background: 'var(--primary)',
                  border: 'none',
                  borderRadius: '8px',
                  color: '#ffffff',
                  fontWeight: 600,
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px',
                }}
              >
                <Plus size={16} /> Adicionar
              </button>
            </div>

            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
              {technologies.map((tech) => (
                <span
                  key={tech}
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '6px',
                    padding: '4px 12px',
                    background: '#1e293b',
                    border: '1px solid var(--border-color)',
                    borderRadius: '16px',
                    fontSize: '0.85rem',
                    color: '#e2e8f0',
                  }}
                >
                  {tech}
                  <button
                    type="button"
                    onClick={() => handleRemoveTech(tech)}
                    style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', display: 'flex', padding: 0 }}
                  >
                    <X size={14} />
                  </button>
                </span>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Upload de PDFs */}
      <div style={{ marginBottom: '24px' }}>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#cbd5e1', marginBottom: '6px' }}>
          <FileText size={14} style={{ display: 'inline', marginRight: '4px' }} /> Documentação do Projeto (PDFs)
        </label>
        <PdfUploader files={files} onFilesChange={setFiles} />
      </div>

      {/* Texto Complementar */}
      <div style={{ marginBottom: '32px' }}>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#cbd5e1', marginBottom: '6px' }}>
          Texto Complementar / Observações do Projeto
        </label>
        <textarea
          rows={4}
          maxLength={10000}
          value={additionalText}
          onChange={(e) => setAdditionalText(e.target.value)}
          placeholder="Insira regras de negócio adicionais, restrições da equipe, integrações desejadas, decisões tomadas..."
        />
        <div style={{ textAlign: 'right', fontSize: '0.75rem', color: '#64748b', marginTop: '4px' }}>
          {additionalText.length} / 10.000 caracteres
        </div>
      </div>

      {/* Botão de Envio */}
      <button
        type="submit"
        disabled={isLoading}
        style={{
          width: '100%',
          padding: '16px',
          background: 'linear-gradient(135deg, var(--primary), var(--secondary))',
          border: 'none',
          borderRadius: '12px',
          color: '#ffffff',
          fontSize: '1.05rem',
          fontWeight: 700,
          cursor: isLoading ? 'not-allowed' : 'pointer',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '10px',
          boxShadow: '0 4px 20px var(--primary-glow)',
          opacity: isLoading ? 0.7 : 1,
          transition: 'all 0.2s',
        }}
      >
        {isLoading ? (
          <>
            <div className="animate-spin" style={{ width: '20px', height: '20px', border: '2px solid #ffffff', borderTopColor: 'transparent', borderRadius: '50%' }} />
            Gerando Product Backlog com IA...
          </>
        ) : (
          <>
            <Sparkles size={20} /> Gerar Product Backlog
          </>
        )}
      </button>
    </form>
  );
};
