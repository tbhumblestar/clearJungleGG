const variantColors = {
  primary: "text-primary",
  white: "text-on-surface",
  error: "text-error",
} as const;

export default function StatBox({
  label,
  value,
  variant = "white",
}: {
  label: string;
  value: string;
  variant?: keyof typeof variantColors;
}) {
  return (
    <div className="bg-surface-container-high rounded-xl p-4 text-center border border-white/5">
      <p className="text-[10px] uppercase tracking-widest text-on-surface-variant font-bold mb-1">
        {label}
      </p>
      <p className={`text-xl font-headline font-bold ${variantColors[variant]}`}>
        {value}
      </p>
    </div>
  );
}
