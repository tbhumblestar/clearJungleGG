import Image from "next/image";
import Link from "next/link";
import type { Champion } from "@/types/champion";

export default function SearchItem({ champion }: { champion: Champion }) {
  if (!champion.hasVideo) {
    return (
      <div className="flex items-center gap-4 px-4 py-3 opacity-50">
        <Image
          src={champion.portraitUrl}
          alt={champion.championNameKo}
          width={40}
          height={40}
          className="rounded-lg grayscale"
        />
        <div className="flex-1 min-w-0">
          <p className="text-sm font-medium text-on-surface-variant truncate">
            {champion.championNameKo}
          </p>
          <p className="text-xs text-on-surface-variant">영상 준비중</p>
        </div>
      </div>
    );
  }

  return (
    <Link
      href={`/champions/${champion.championId}`}
      className="flex items-center gap-4 px-4 py-3 hover:bg-surface-container-high transition-colors"
    >
      <div className="relative w-10 h-10 shrink-0">
        <Image
          src={champion.portraitUrl}
          alt={champion.championNameKo}
          fill
          className="rounded-lg object-cover"
          sizes="40px"
        />
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-on-surface truncate">
          {champion.championNameKo}
          <span className="text-xs text-on-surface-variant font-normal ml-2">
            {champion.winRate}%
          </span>
        </p>
        {champion.bestClearTime && (
          <p className="text-xs text-on-surface-variant mt-0.5">
            Best
            <span className="font-mono text-primary font-bold ml-1.5">
              {champion.bestClearTime}
            </span>
          </p>
        )}
      </div>
      <span className="shrink-0 ml-auto flex items-center gap-1 text-xs font-headline font-bold text-primary uppercase tracking-wide">
        <span className="material-symbols-outlined text-sm">play_circle</span>
        Watch Video
      </span>
    </Link>
  );
}
