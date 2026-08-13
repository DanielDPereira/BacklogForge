import React, { useEffect, useState } from 'react';
import { Sparkles, Brain, Cpu, FileCheck } from 'lucide-react';

const LOADING_TIPS = [
  'Analisando documentação em PDF e identificando requisitos funcionais...',
  'Decompondo requisitos em Épicos e User Stories orientadas a valor...',
  'Calculando estimativas em Story Points baseadas no tamanho da equipe...',
  'Criando critérios de aceitação e quebrando histórias em Tasks técnicas...',
  'Distribuindo User Stories proporcionalmente nas Sprints especificadas...',
  'Validando consistência dos IDs e conformidade do contrato JSON...',
];

export const LoadingOverlay: React.FC = () => {
  const [tipIndex, setTipIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setTipIndex((prev) => (prev + 1) % LOADING_TIPS.length);
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(11, 15, 25, 0.85)',
        backdropFilter: 'blur(10px)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 9999,
        padding: '24px',
      }}
    >
      <div
        className="glass-panel"
        style={{
          padding: '48px 40px',
          maxWidth: '480px',
          width: '100%',
          textAlign: 'center',
          border: '1px solid var(--primary)',
          boxShadow: '0 0 50px var(--primary-glow)',
        }}
      >
        <div
          style={{
            margin: '0 auto 24px auto',
            width: '80px',
            height: '80px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, var(--primary), var(--secondary))',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 0 30px var(--primary-glow)',
          }}
        >
          <Brain size={40} color="#ffffff" className="animate-pulse-slow" />
        </div>

        <h3 style={{ fontSize: '1.4rem', color: '#ffffff', marginBottom: '8px' }}>
          Engenharia de Backlog em Progresso
        </h3>
        
        <p style={{ color: 'var(--primary)', fontWeight: 600, fontSize: '0.9rem', marginBottom: '24px' }}>
          Processando com Google Gemini 1.5 & Spring AI
        </p>

        <div
          style={{
            padding: '16px',
            background: '#0f172a',
            borderRadius: '12px',
            border: '1px solid var(--border-color)',
            minHeight: '80px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <p style={{ fontSize: '0.9rem', color: '#cbd5e1', fontStyle: 'italic', transition: 'all 0.3s' }}>
            "{LOADING_TIPS[tipIndex]}"
          </p>
        </div>
      </div>
    </div>
  );
};
