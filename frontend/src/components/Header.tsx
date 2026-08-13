import React from 'react';
import { Sparkles, Layers, Cpu } from 'lucide-react';

export const Header: React.FC = () => {
  return (
    <header className="glass-panel" style={{ padding: '20px 32px', marginBottom: '32px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <div style={{ 
          background: 'linear-gradient(135deg, var(--primary), var(--secondary))', 
          padding: '12px', 
          borderRadius: '12px', 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          boxShadow: '0 4px 15px var(--primary-glow)' 
        }}>
          <Layers size={28} color="#ffffff" />
        </div>
        <div>
          <h1 style={{ fontSize: '1.75rem', color: '#ffffff', letterSpacing: '-0.02em', display: 'flex', alignItems: 'center', gap: '8px' }}>
            BacklogForge
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Geração assistida por IA de Product Backlogs estruturados
          </p>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <span style={{ 
          display: 'inline-flex', 
          alignItems: 'center', 
          gap: '6px', 
          padding: '6px 14px', 
          borderRadius: '20px', 
          background: 'rgba(99, 102, 241, 0.15)', 
          border: '1px solid rgba(99, 102, 241, 0.3)', 
          color: '#a5b4fc', 
          fontSize: '0.825rem',
          fontWeight: 600
        }}>
          <Sparkles size={14} /> Gemini 1.5 Flash
        </span>
        <span style={{ 
          display: 'inline-flex', 
          alignItems: 'center', 
          gap: '6px', 
          padding: '6px 14px', 
          borderRadius: '20px', 
          background: 'rgba(16, 185, 129, 0.15)', 
          border: '1px solid rgba(16, 185, 129, 0.3)', 
          color: '#6ee7b7', 
          fontSize: '0.825rem',
          fontWeight: 600
        }}>
          <Cpu size={14} /> Spring Boot 3
        </span>
      </div>
    </header>
  );
};
