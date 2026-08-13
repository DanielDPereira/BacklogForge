import React, { useState } from 'react';
import { Epic, Priority, UserStory } from '../types/backlog';
import { ChevronDown, ChevronRight, CheckSquare, ListChecks, Target, AlertCircle } from 'lucide-react';

interface EpicCardProps {
  epic: Epic;
  isExpandedInitial?: boolean;
}

const getPriorityBadgeStyle = (priority: Priority) => {
  switch (priority) {
    case 'CRITICAL':
      return { background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.4)', color: '#fca5a5' };
    case 'HIGH':
      return { background: 'rgba(249, 115, 22, 0.15)', border: '1px solid rgba(249, 115, 22, 0.4)', color: '#fdba74' };
    case 'MEDIUM':
      return { background: 'rgba(59, 130, 246, 0.15)', border: '1px solid rgba(59, 130, 246, 0.4)', color: '#93c5fd' };
    case 'LOW':
      return { background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.4)', color: '#6ee7b7' };
  }
};

export const EpicCard: React.FC<EpicCardProps> = ({ epic, isExpandedInitial = true }) => {
  const [isExpanded, setIsExpanded] = useState(isExpandedInitial);
  const [expandedStories, setExpandedStories] = useState<Record<string, boolean>>({});

  const toggleStory = (storyId: string) => {
    setExpandedStories((prev) => ({ ...prev, [storyId]: !prev[storyId] }));
  };

  return (
    <div className="glass-card" style={{ marginBottom: '20px', overflow: 'hidden' }}>
      {/* Cabeçalho do Épico */}
      <div
        onClick={() => setIsExpanded(!isExpanded)}
        style={{
          padding: '20px 24px',
          background: 'rgba(30, 41, 59, 0.8)',
          cursor: 'pointer',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          borderBottom: isExpanded ? '1px solid var(--border-color)' : 'none',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <span style={{ color: 'var(--primary)', fontWeight: 700, fontSize: '1.1rem' }}>
            {epic.id}
          </span>
          <h3 style={{ fontSize: '1.15rem', color: '#ffffff' }}>{epic.title}</h3>
          <span
            style={{
              padding: '2px 10px',
              borderRadius: '12px',
              background: 'rgba(99, 102, 241, 0.15)',
              color: '#a5b4fc',
              fontSize: '0.75rem',
              fontWeight: 600,
            }}
          >
            {epic.userStories.length} User Stories
          </span>
        </div>

        <div style={{ color: '#94a3b8' }}>
          {isExpanded ? <ChevronDown size={22} /> : <ChevronRight size={22} />}
        </div>
      </div>

      {/* Conteúdo do Épico */}
      {isExpanded && (
        <div style={{ padding: '24px' }}>
          {epic.description && (
            <p style={{ color: '#94a3b8', fontSize: '0.925rem', marginBottom: '20px' }}>
              {epic.description}
            </p>
          )}

          {/* User Stories */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {epic.userStories.map((us: UserStory) => {
              const isStoryOpen = expandedStories[us.id] !== false; // Padrão aberto
              const priorityStyle = getPriorityBadgeStyle(us.priority);

              return (
                <div
                  key={us.id}
                  style={{
                    background: '#0f172a',
                    border: '1px solid var(--border-color)',
                    borderRadius: '10px',
                    padding: '16px 20px',
                  }}
                >
                  {/* Cabeçalho da User Story */}
                  <div
                    onClick={() => toggleStory(us.id)}
                    style={{
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'flex-start',
                      justifyContent: 'space-between',
                      gap: '12px',
                    }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap', marginBottom: '6px' }}>
                        <span style={{ color: '#818cf8', fontWeight: 700, fontSize: '0.95rem' }}>{us.id}</span>
                        <h4 style={{ fontSize: '1rem', color: '#f8fafc' }}>{us.title}</h4>
                      </div>
                      <p style={{ color: '#cbd5e1', fontSize: '0.9rem', marginBottom: '10px' }}>{us.description}</p>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <span
                        style={{
                          padding: '3px 10px',
                          borderRadius: '12px',
                          fontSize: '0.75rem',
                          fontWeight: 700,
                          ...priorityStyle,
                        }}
                      >
                        {us.priority}
                      </span>
                      <span
                        style={{
                          padding: '3px 10px',
                          borderRadius: '12px',
                          background: 'rgba(139, 92, 246, 0.2)',
                          border: '1px solid rgba(139, 92, 246, 0.4)',
                          color: '#c084fc',
                          fontSize: '0.75rem',
                          fontWeight: 700,
                        }}
                      >
                        {us.storyPoints} SP
                      </span>
                    </div>
                  </div>

                  {/* Detalhes da User Story (Critérios + Tasks) */}
                  {isStoryOpen && (
                    <div style={{ marginTop: '16px', paddingTop: '16px', borderTop: '1px solid #1e293b' }}>
                      {/* Critérios de Aceitação */}
                      {us.acceptanceCriteria && us.acceptanceCriteria.length > 0 && (
                        <div style={{ marginBottom: '16px' }}>
                          <h5 style={{ fontSize: '0.85rem', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                            <ListChecks size={14} color="#60a5fa" /> Critérios de Aceitação
                          </h5>
                          <ul style={{ listStyleType: 'none', paddingLeft: 0, display: 'flex', flexDirection: 'column', gap: '6px' }}>
                            {us.acceptanceCriteria.map((criteria, i) => (
                              <li key={i} style={{ fontSize: '0.875rem', color: '#cbd5e1', display: 'flex', alignItems: 'flex-start', gap: '8px' }}>
                                <span style={{ color: '#38bdf8' }}>✓</span> {criteria}
                              </li>
                            ))}
                          </ul>
                        </div>
                      )}

                      {/* Tasks */}
                      {us.tasks && us.tasks.length > 0 && (
                        <div>
                          <h5 style={{ fontSize: '0.85rem', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                            <CheckSquare size={14} color="#34d399" /> Tasks Operacionais
                          </h5>
                          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '10px' }}>
                            {us.tasks.map((task) => (
                              <div
                                key={task.id}
                                style={{
                                  background: '#161e2e',
                                  border: '1px solid #2a364f',
                                  borderRadius: '8px',
                                  padding: '10px 12px',
                                }}
                              >
                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '4px' }}>
                                  <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#a7f3d0' }}>{task.id}</span>
                                  <span style={{ fontSize: '0.7rem', color: '#94a3b8' }}>{task.priority}</span>
                                </div>
                                <div style={{ fontSize: '0.85rem', fontWeight: 600, color: '#f1f5f9' }}>{task.title}</div>
                                {task.description && (
                                  <div style={{ fontSize: '0.775rem', color: '#94a3b8', marginTop: '4px' }}>{task.description}</div>
                                )}
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};
