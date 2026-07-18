const colorMap = {
  Easy: 'text-lc-easy',
  Medium: 'text-lc-medium',
  Hard: 'text-lc-hard',
};

export default function DifficultyBadge({ difficulty }) {
  const colorClass = colorMap[difficulty] || 'text-lc-text-secondary';

  return (
    <span className={`text-xs font-medium ${colorClass}`}>
      {difficulty}
    </span>
  );
}
