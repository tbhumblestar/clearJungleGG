import type { Champion } from "@/types/champion";
import ChampionCard from "./ChampionCard";

export default function ChampionGrid({
  champions,
}: {
  champions: Champion[];
}) {
  const visible = champions
    .filter((c) => c.hasVideo)
    .sort((a, b) => a.popularityRank - b.popularityRank);

  return (
    <section className="max-w-[1440px] mx-auto px-8">
      <div className="flex items-center gap-4 mb-8">
        <span className="w-12 h-[2px] bg-primary" />
        <h2 className="text-2xl font-headline font-bold text-on-surface uppercase tracking-tight">
          Popular Champions
        </h2>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 md:gap-6">
        {visible.map((champion) => (
          <ChampionCard key={champion.championId} champion={champion} />
        ))}
      </div>
    </section>
  );
}
