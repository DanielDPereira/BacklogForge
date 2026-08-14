import React from 'react';
import { ProductBacklog, Sprint, UserStory } from '../types/backlog';
import { Calendar, Target } from 'lucide-react';

interface SprintBoardProps {
  backlog: ProductBacklog;
  isEditMode?: boolean;
  onUpdateSprint?: (sprintId: string, updatedSprint: Sprint) => void;
}

export const SprintBoard: React.FC<SprintBoardProps> = ({
  backlog,
  isEditMode = false,
  onUpdateSprint,
}) => {
  // Mapeamento rápido de User Stories por ID
  const storyMap = new Map<string, UserStory>();
  backlog.epics.forEach((epic) => {
    epic.userStories.forEach((us) => {
      storyMap.set(us.id, us);
    });
  });

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '20px' }}>
      {backlog.sprints.map((sprint: Sprint) => {
        const totalPoints = sprint.userStoryIds.reduce((sum, usId) => {
          const us = storyMap.get(usId);
          return sum + (us?.storyPoints || 0);
        }, 0);

        return (
          <div
            key={sprint.id}
            className="glass-card"
            style={{
              padding: '24px',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
            }}
          >
            <div>
              {/* Header da Sprint */}
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px', gap: '10px' }}>
                {isEditMode ? (
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flex: 1 }}>
                    <Calendar size={18} color="var(--primary)" style={{ flexShrink: 0 }} />
                    <input
                      type="text"
                      className="inline-input"
                      value={sprint.name}
                      onChange={(e) => onUpdateSprint && onUpdateSprint(sprint.id, { ...sprint, name: e.target.value })}
                      style={{ fontWeight: 700 }}
                    />
                  </div>
                ) : (
                  <h3 style={{ fontSize: '1.15rem', color: '#ffffff', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Calendar size={18} color="var(--primary)" /> {sprint.name}
                  </h3>
                )}

                <span
                  style={{
                    padding: '4px 10px',
                    borderRadius: '12px',
                    background: 'rgba(99, 102, 241, 0.15)',
                    color: '#a5b4fc',
                    fontSize: '0.8rem',
                    fontWeight: 700,
                    flexShrink: 0,
                  }}
                >
                  {totalPoints} Story Points
                </span>
              </div>

              {/* Objetivo da Sprint */}
              <div
                style={{
                  padding: '12px',
                  background: 'rgba(15, 23, 42, 0.7)',
                  borderRadius: '8px',
                  border: '1px solid var(--border-color)',
                  marginBottom: '16px',
                }}
              >
                <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#94a3b8', textTransform: 'uppercase', marginBottom: '4px', display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <Target size={12} color="#38bdf8" /> Objetivo da Sprint
                </div>
                {isEditMode ? (
                  <textarea
                    className="inline-textarea"
                    rows={2}
                    value={sprint.goal}
                    onChange={(e) => onUpdateSprint && onUpdateSprint(sprint.id, { ...sprint, goal: e.target.value })}
                  />
                ) : (
                  <p style={{ fontSize: '0.875rem', color: '#e2e8f0' }}>{sprint.goal}</p>
                )}
              </div>

              {/* User Stories Alocadas */}
              <div>
                <h4 style={{ fontSize: '0.85rem', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '10px' }}>
                  User Stories ({sprint.userStoryIds.length})
                </h4>

                {sprint.userStoryIds.length === 0 ? (
                  <p style={{ fontSize: '0.85rem', color: '#64748b', fontStyle: 'italic' }}>
                    Nenhuma User Story alocada.
                  </p>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {sprint.userStoryIds.map((usId) => {
                      const us = storyMap.get(usId);
                      return (
                        <div
                          key={usId}
                          style={{
                            padding: '10px 12px',
                            background: '#0f172a',
                            border: '1px solid var(--border-color)',
                            borderRadius: '8px',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'space-between',
                          }}
                        >
                          <div>
                            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#818cf8', marginRight: '6px' }}>
                              {usId}
                            </span>
                            <span style={{ fontSize: '0.85rem', color: '#f1f5f9' }}>
                              {us ? us.title : 'User Story'}
                            </span>
                          </div>

                          {us && (
                            <span style={{ fontSize: '0.75rem', fontWeight: 700, color: '#c084fc', background: 'rgba(192, 132, 252, 0.15)', padding: '2px 6px', borderRadius: '4px' }}>
                              {us.storyPoints} SP
                            </span>
                          )}
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};
