import Image from "next/image";
import type { ChampionMeta } from "@/types/champion";
import StatBox from "./StatBox";
import ClearTimeCard from "./ClearTimeCard";
import TierRankCard from "./TierRankCard";

export default function ChampionMetaPanel({ meta }: { meta: ChampionMeta }) {
  return (
    <div className="space-y-4">
      {/* Splash Image + Name Overlay */}
      <div className="relative aspect-[4/3] rounded-xl overflow-hidden">
        <Image
          src={meta.splashUrl}
          alt={meta.championNameKo}
          fill
          className="object-cover"
          sizes="(max-width: 1024px) 100vw, 33vw"
          priority
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent" />
        <div className="absolute bottom-4 left-4">
          <h1 className="text-3xl font-headline font-bold text-white">
            {meta.championNameKo}
          </h1>
          <p className="text-sm text-white/60">{meta.title}</p>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-3">
        <StatBox label="Win Rate" value={`${meta.winRate}%`} variant="primary" />
        <StatBox label="Pick Rate" value={`${meta.pickRate}%`} />
        <StatBox label="Ban Rate" value={`${meta.banRate}%`} variant="error" />
      </div>

      {/* Clear Time & Tier */}
      <ClearTimeCard time={meta.bestClearTime} patch={meta.bestClearPatch} />
      <TierRankCard rank={meta.tierRank} />
    </div>
  );
}
