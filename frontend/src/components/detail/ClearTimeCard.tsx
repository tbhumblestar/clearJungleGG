export default function ClearTimeCard({
  time,
  patch,
}: {
  time: string;
  patch: string;
}) {
  return (
    <div className="bg-surface-container-high rounded-xl p-4 border-l-4 border-primary border-y border-r border-y-white/5 border-r-white/5">
      <p className="text-[10px] uppercase tracking-widest text-on-surface-variant font-bold mb-1">
        Fastest Clear
      </p>
      <p className="text-2xl font-mono font-bold text-primary">{time}</p>
      <p className="text-xs text-on-surface-variant mt-1">Patch {patch}</p>
    </div>
  );
}
