import Image from "next/image";
import Link from "next/link";
import type { Champion } from "@/types/champion";

export default function ChampionCard({ champion }: { champion: Champion }) {
  return (
    <Link href={`/champions/${champion.championId}`} className="group block">
      <div className="relative bg-surface-container rounded-xl overflow-hidden border border-white/5 transition-all duration-300 group-hover:-translate-y-1 pulse-shadow">
        {/* Splash Image */}
        <div className="relative aspect-[16/11] overflow-hidden">
          <Image
            src={champion.splashUrl}
            alt={champion.championNameKo}
            fill
            className="object-cover transition-transform duration-500 group-hover:scale-105"
            sizes="(max-width: 640px) 50vw, (max-width: 1024px) 33vw, 20vw"
          />
          {/* Hover overlay */}
          <div className="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-all duration-300 flex items-center justify-center">
            <span className="material-symbols-outlined text-primary text-5xl opacity-0 group-hover:opacity-100 transition-opacity duration-300">
              play_circle
            </span>
          </div>
          {/* Bottom gradient */}
          <div className="absolute bottom-0 inset-x-0 h-20 bg-gradient-to-t from-surface-container to-transparent" />
        </div>

        {/* Info */}
        <div className="p-3 pt-1 space-y-1">
          <h3 className="font-headline font-bold text-on-surface text-sm truncate">
            {champion.championNameKo}
          </h3>
          <div className="flex items-center justify-between text-xs text-on-surface-variant">
            <span>
              승률{" "}
              <span className="text-on-surface font-medium">
                {champion.winRate}%
              </span>
            </span>
            {champion.bestClearTime && (
              <span>
                Best
                <span className="font-mono text-primary font-bold ml-1">
                  {champion.bestClearTime}
                </span>
              </span>
            )}
          </div>
        </div>
      </div>
    </Link>
  );
}
