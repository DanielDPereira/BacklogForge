import React, { useState } from 'react';
import { Epic, Priority, UserStory, Task } from '../types/backlog';
import { ChevronDown, ChevronRight, CheckSquare, ListChecks, Trash2, Plus, Edit2 } from 'lucide-react';

interface EpicCardProps {
  epic: Epic;
  isEditMode?: boolean;
  onUpdateEpic?: (updatedEpic: Epic) => void;
  onDeleteEpic?: (epicId: string) => void;
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
    default:
      return { background: 'rgba(148, 163, 184, 0.15)', border: '1px solid rgba(148, 163, 184, 0.4)', color: '#cbd5e1' };
  }
};

export const EpicCard: React.FC<EpicCardProps> = ({
  epic,
  isEditMode = false,
  onUpdateEpic,
  onDeleteEpic,
  isExpandedInitial = true,
}) => {
  const [isExpanded, setIsExpanded] = useState(isExpandedInitial);
  const [expandedStories, setExpandedStories] = useState<Record<string, boolean>>({});

  const toggleStory = (storyId: string) => {
    setExpandedStories((prev) => ({ ...prev, [storyId]: !prev[storyId] }));
  };

  // Funções de alteração do Épico
  const handleUpdateEpicField = (field: keyof Epic, value: any) => {
    if (onUpdateEpic) {
      onUpdateEpic({ ...epic, [field]: value });
    }
  };

  // Funções de alteração de User Stories
  const handleUpdateStory = (storyId: string, updatedStory: UserStory) => {
    if (!onUpdateEpic) return;
    const newStories = epic.userStories.map((us) => (us.id === storyId ? updatedStory : us));
    onUpdateEpic({ ...epic, userStories: newStories });
  };

  const handleDeleteStory = (storyId: string) => {
    if (!onUpdateEpic) return;
    const newStories = epic.userStories.filter((us) => us.id !== storyId);
    onUpdateEpic({ ...epic, userStories: newStories });
  };

  const handleAddStory = () => {
    if (!onUpdateEpic) return;
    const newId = `US-${String(epic.userStories.length + 1).padStart(3, '0')}`;
    const newStory: UserStory = {
      id: `${epic.id}-${newId}`,
      title: 'Nova User Story',
      description: 'Descrição da nova User Story...',
      priority: 'MEDIUM',
      storyPoints: 3,
      acceptanceCriteria: ['Critério de Aceitação 1'],
      tasks: [],
    };
    onUpdateEpic({ ...epic, userStories: [...epic.userStories, newStory] });
  };

  // Critérios de Aceitação
  const handleUpdateCriteria = (story: UserStory, index: number, value: string) => {
    const newCriteria = [...story.acceptanceCriteria];
    newCriteria[index] = value;
    handleUpdateStory(story.id, { ...story, acceptanceCriteria: newCriteria });
  };

  const handleAddCriteria = (story: UserStory) => {
    const newCriteria = [...(story.acceptanceCriteria || []), 'Novo Critério de Aceitação'];
    handleUpdateStory(story.id, { ...story, acceptanceCriteria: newCriteria });
  };

  const handleDeleteCriteria = (story: UserStory, index: number) => {
    const newCriteria = story.acceptanceCriteria.filter((_, i) => i !== index);
    handleUpdateStory(story.id, { ...story, acceptanceCriteria: newCriteria });
  };

  // Tasks
  const handleUpdateTask = (story: UserStory, taskId: string, updatedTask: Task) => {
    const newTasks = story.tasks.map((t) => (t.id === taskId ? updatedTask : t));
    handleUpdateStory(story.id, { ...story, tasks: newTasks });
  };

  const handleAddTask = (story: UserStory) => {
    const newId = `TASK-${String((story.tasks?.length || 0) + 1).padStart(3, '0')}`;
    const newTask: Task = {
      id: `${story.id}-${newId}`,
      title: 'Nova Task Operacional',
      description: 'Detalhes da nova tarefa...',
      priority: 'MEDIUM',
    };
    handleUpdateStory(story.id, { ...story, tasks: [...(story.tasks || []), newTask] });
  };

  const handleDeleteTask = (story: UserStory, taskId: string) => {
    const newTasks = story.tasks.filter((t) => t.id !== taskId);
    handleUpdateStory(story.id, { ...story, tasks: newTasks });
  };

  return (
    <div className="glass-card" style={{ marginBottom: '20px', overflow: 'hidden' }}>
      {/* Cabeçalho do Épico */}
      <div
        style={{
          padding: '20px 24px',
          background: 'rgba(30, 41, 59, 0.8)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          borderBottom: isExpanded ? '1px solid var(--border-color)' : 'none',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px', flex: 1 }}>
          <span style={{ color: 'var(--primary)', fontWeight: 700, fontSize: '1.1rem' }}>
            {epic.id}
          </span>

          {isEditMode ? (
            <input
              type="text"
              className="inline-input"
              value={epic.title}
              onChange={(e) => handleUpdateEpicField('title', e.target.value)}
              style={{ fontSize: '1.1rem', fontWeight: 600 }}
              onClick={(e) => e.stopPropagation()}
            />
          ) : (
            <h3
              onClick={() => setIsExpanded(!isExpanded)}
              style={{ fontSize: '1.15rem', color: '#ffffff', cursor: 'pointer', flex: 1 }}
            >
              {epic.title}
            </h3>
          )}

          <span
            style={{
              padding: '2px 10px',
              borderRadius: '12px',
              background: 'rgba(99, 102, 241, 0.15)',
              color: '#a5b4fc',
              fontSize: '0.75rem',
              fontWeight: 600,
              flexShrink: 0,
            }}
          >
            {epic.userStories.length} User Stories
          </span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginLeft: '16px' }}>
          {isEditMode && onDeleteEpic && (
            <button
              onClick={() => onDeleteEpic(epic.id)}
              style={{
                background: 'rgba(239, 68, 68, 0.2)',
                border: '1px solid rgba(239, 68, 68, 0.4)',
                borderRadius: '6px',
                color: '#fca5a5',
                padding: '4px 10px',
                fontSize: '0.8rem',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
              }}
            >
              <Trash2 size={14} /> Excluir Épico
            </button>
          )}

          <div
            onClick={() => setIsExpanded(!isExpanded)}
            style={{ color: '#94a3b8', cursor: 'pointer' }}
          >
            {isExpanded ? <ChevronDown size={22} /> : <ChevronRight size={22} />}
          </div>
        </div>
      </div>

      {/* Conteúdo do Épico */}
      {isExpanded && (
        <div style={{ padding: '24px' }}>
          {isEditMode ? (
            <div style={{ marginBottom: '20px' }}>
              <label style={{ fontSize: '0.75rem', color: '#94a3b8', fontWeight: 600, display: 'block', marginBottom: '4px' }}>
                Descrição do Épico:
              </label>
              <textarea
                className="inline-textarea"
                rows={2}
                value={epic.description || ''}
                onChange={(e) => handleUpdateEpicField('description', e.target.value)}
              />
            </div>
          ) : (
            epic.description && (
              <p style={{ color: '#94a3b8', fontSize: '0.925rem', marginBottom: '20px' }}>
                {epic.description}
              </p>
            )
          )}

          {/* User Stories */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {epic.userStories.map((us: UserStory) => {
              const isStoryOpen = expandedStories[us.id] !== false;
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
                    style={{
                      display: 'flex',
                      alignItems: 'flex-start',
                      justifyContent: 'space-between',
                      gap: '12px',
                    }}
                  >
                    <div style={{ flex: 1 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap', marginBottom: '6px' }}>
                        <span style={{ color: '#818cf8', fontWeight: 700, fontSize: '0.95rem' }}>{us.id}</span>
                        {isEditMode ? (
                          <input
                            type="text"
                            className="inline-input"
                            value={us.title}
                            onChange={(e) => handleUpdateStory(us.id, { ...us, title: e.target.value })}
                            style={{ flex: 1, fontSize: '0.95rem' }}
                          />
                        ) : (
                          <h4
                            onClick={() => toggleStory(us.id)}
                            style={{ fontSize: '1rem', color: '#f8fafc', cursor: 'pointer' }}
                          >
                            {us.title}
                          </h4>
                        )}
                      </div>

                      {isEditMode ? (
                        <textarea
                          className="inline-textarea"
                          rows={2}
                          value={us.description || ''}
                          onChange={(e) => handleUpdateStory(us.id, { ...us, description: e.target.value })}
                          style={{ marginBottom: '10px' }}
                        />
                      ) : (
                        <p style={{ color: '#cbd5e1', fontSize: '0.9rem', marginBottom: '10px' }}>{us.description}</p>
                      )}
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexShrink: 0 }}>
                      {isEditMode ? (
                        <>
                          <select
                            className="inline-select"
                            value={us.priority}
                            onChange={(e) => handleUpdateStory(us.id, { ...us, priority: e.target.value as Priority })}
                          >
                            <option value="CRITICAL">CRITICAL</option>
                            <option value="HIGH">HIGH</option>
                            <option value="MEDIUM">MEDIUM</option>
                            <option value="LOW">LOW</option>
                          </select>

                          <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                            <input
                              type="number"
                              className="inline-input"
                              style={{ width: '60px', padding: '4px 6px', textAlign: 'center' }}
                              value={us.storyPoints || 0}
                              onChange={(e) => handleUpdateStory(us.id, { ...us, storyPoints: parseInt(e.target.value) || 0 })}
                            />
                            <span style={{ fontSize: '0.75rem', color: '#c084fc', fontWeight: 700 }}>SP</span>
                          </div>

                          <button
                            onClick={() => handleDeleteStory(us.id)}
                            style={{
                              background: 'rgba(239, 68, 68, 0.15)',
                              border: 'none',
                              color: '#fca5a5',
                              padding: '6px',
                              borderRadius: '6px',
                              cursor: 'pointer',
                            }}
                            title="Excluir User Story"
                          >
                            <Trash2 size={16} />
                          </button>
                        </>
                      ) : (
                        <>
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
                        </>
                      )}
                    </div>
                  </div>

                  {/* Detalhes da User Story (Critérios + Tasks) */}
                  {isStoryOpen && (
                    <div style={{ marginTop: '16px', paddingTop: '16px', borderTop: '1px solid #1e293b' }}>
                      {/* Critérios de Aceitação */}
                      <div style={{ marginBottom: '16px' }}>
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
                          <h5 style={{ fontSize: '0.85rem', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.05em', display: 'flex', alignItems: 'center', gap: '6px' }}>
                            <ListChecks size={14} color="#60a5fa" /> Critérios de Aceitação
                          </h5>
                          {isEditMode && (
                            <button
                              onClick={() => handleAddCriteria(us)}
                              style={{
                                background: 'rgba(96, 165, 250, 0.15)',
                                border: '1px solid rgba(96, 165, 250, 0.3)',
                                color: '#93c5fd',
                                padding: '2px 8px',
                                borderRadius: '4px',
                                fontSize: '0.75rem',
                                cursor: 'pointer',
                                display: 'flex',
                                alignItems: 'center',
                                gap: '4px',
                              }}
                            >
                              <Plus size={12} /> Critério
                            </button>
                          )}
                        </div>

                        <ul style={{ listStyleType: 'none', paddingLeft: 0, display: 'flex', flexDirection: 'column', gap: '8px' }}>
                          {(us.acceptanceCriteria || []).map((criteria, i) => (
                            <li key={i} style={{ fontSize: '0.875rem', color: '#cbd5e1', display: 'flex', alignItems: 'center', gap: '8px' }}>
                              <span style={{ color: '#38bdf8' }}>✓</span>
                              {isEditMode ? (
                                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', width: '100%' }}>
                                  <input
                                    type="text"
                                    className="inline-input"
                                    value={criteria}
                                    onChange={(e) => handleUpdateCriteria(us, i, e.target.value)}
                                  />
                                  <button
                                    onClick={() => handleDeleteCriteria(us, i)}
                                    style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer' }}
                                  >
                                    <Trash2 size={14} />
                                  </button>
                                </div>
                              ) : (
                                <span>{criteria}</span>
                              )}
                            </li>
                          ))}
                        </ul>
                      </div>

                      {/* Tasks Operacionais */}
                      <div>
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
                          <h5 style={{ fontSize: '0.85rem', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.05em', display: 'flex', alignItems: 'center', gap: '6px' }}>
                            <CheckSquare size={14} color="#34d399" /> Tasks Operacionais ({us.tasks?.length || 0})
                          </h5>
                          {isEditMode && (
                            <button
                              onClick={() => handleAddTask(us)}
                              style={{
                                background: 'rgba(52, 211, 153, 0.15)',
                                border: '1px solid rgba(52, 211, 153, 0.3)',
                                color: '#a7f3d0',
                                padding: '2px 8px',
                                borderRadius: '4px',
                                fontSize: '0.75rem',
                                cursor: 'pointer',
                                display: 'flex',
                                alignItems: 'center',
                                gap: '4px',
                              }}
                            >
                              <Plus size={12} /> Task
                            </button>
                          )}
                        </div>

                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '10px' }}>
                          {(us.tasks || []).map((task) => (
                            <div
                              key={task.id}
                              style={{
                                background: '#161e2e',
                                border: '1px solid #2a364f',
                                borderRadius: '8px',
                                padding: '10px 12px',
                              }}
                            >
                              {isEditMode ? (
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '6px' }}>
                                    <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#a7f3d0' }}>{task.id}</span>
                                    <select
                                      className="inline-select"
                                      value={task.priority}
                                      onChange={(e) => handleUpdateTask(us, task.id, { ...task, priority: e.target.value as Priority })}
                                    >
                                      <option value="CRITICAL">CRITICAL</option>
                                      <option value="HIGH">HIGH</option>
                                      <option value="MEDIUM">MEDIUM</option>
                                      <option value="LOW">LOW</option>
                                    </select>
                                    <button
                                      onClick={() => handleDeleteTask(us, task.id)}
                                      style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer' }}
                                    >
                                      <Trash2 size={14} />
                                    </button>
                                  </div>
                                  <input
                                    type="text"
                                    className="inline-input"
                                    value={task.title}
                                    onChange={(e) => handleUpdateTask(us, task.id, { ...task, title: e.target.value })}
                                    placeholder="Título da Task"
                                  />
                                  <textarea
                                    className="inline-textarea"
                                    rows={2}
                                    value={task.description || ''}
                                    onChange={(e) => handleUpdateTask(us, task.id, { ...task, description: e.target.value })}
                                    placeholder="Descrição da Task"
                                  />
                                </div>
                              ) : (
                                <>
                                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '4px' }}>
                                    <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#a7f3d0' }}>{task.id}</span>
                                    <span style={{ fontSize: '0.7rem', color: '#94a3b8' }}>{task.priority}</span>
                                  </div>
                                  <div style={{ fontSize: '0.85rem', fontWeight: 600, color: '#f1f5f9' }}>{task.title}</div>
                                  {task.description && (
                                    <div style={{ fontSize: '0.775rem', color: '#94a3b8', marginTop: '4px' }}>{task.description}</div>
                                  )}
                                </>
                              )}
                            </div>
                          ))}
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}

            {isEditMode && (
              <button
                onClick={handleAddStory}
                style={{
                  padding: '12px',
                  background: 'rgba(99, 102, 241, 0.1)',
                  border: '1px dashed var(--primary)',
                  borderRadius: '10px',
                  color: '#a5b4fc',
                  fontWeight: 600,
                  fontSize: '0.9rem',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '8px',
                  marginTop: '10px',
                }}
              >
                <Plus size={16} /> Adicionar User Story a este Épico
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
