export default function TierRankCard({ rank }: { rank: number }) {
  return (
    <div className="bg-surface-container-high rounded-xl p-4 border border-white/5">
      <p className="text-[10px] uppercase tracking-widest text-on-surface-variant font-bold mb-1">
        Tier Rank
      </p>
      <p className="text-2xl font-headline font-bold text-on-surface">
        #{rank}{" "}
        <span className="text-sm text-on-surface-variant font-medium">
          Jungle
        </span>
      </p>
    </div>
  );
}
