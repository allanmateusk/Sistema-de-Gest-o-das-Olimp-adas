/** Referência visual aos 5 anéis olímpicos (estilização; não é logotipo oficial) */
export function OlympicRings() {
  return (
    <svg
      className="olympic-rings"
      viewBox="0 0 88 44"
      width="100"
      height="44"
      aria-hidden
    >
      <g fill="none" strokeWidth="3.2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="16" cy="16" r="9" stroke="#0c8bd9" />
        <circle cx="40" cy="16" r="9" stroke="#1c242c" />
        <circle cx="64" cy="16" r="9" stroke="#e63946" />
        <circle cx="28" cy="30" r="9" stroke="#e8c000" />
        <circle cx="52" cy="30" r="9" stroke="#06b87a" />
      </g>
    </svg>
  );
}
