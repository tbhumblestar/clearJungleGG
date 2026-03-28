import type { Video } from "@/types/champion";

const tierBadgeClass: Record<string, string> = {
  CHALLENGER: "tier-badge-challenger",
  GRANDMASTER: "tier-badge-grandmaster",
  MASTER: "tier-badge-master",
};

export default function VideoListItem({
  video,
  isActive,
  onSelect,
}: {
  video: Video;
  isActive: boolean;
  onSelect: () => void;
}) {
  return (
    <button
      onClick={onSelect}
      className={`w-full flex items-center gap-4 px-4 py-3 rounded-xl transition-all text-left ${
        isActive
          ? "bg-surface-container-high border border-primary/30 pulse-border"
          : "bg-surface-container border border-white/5 hover:bg-surface-container-high"
      }`}
    >
      {/* Rank */}
      <span
        className={`text-lg font-headline font-bold w-8 text-center shrink-0 ${
          isActive ? "text-primary" : "text-on-surface-variant"
        }`}
      >
        {video.rank}
      </span>

      {/* Active indicator bar */}
      {isActive && (
        <span className="w-0.5 h-8 bg-primary rounded-full shrink-0" />
      )}

      {/* Info */}
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <span className="text-sm font-medium text-on-surface truncate">
            {video.summonerName}
          </span>
          <span
            className={`text-[10px] font-bold uppercase ${
              tierBadgeClass[video.summonerTier] ?? "text-on-surface-variant"
            }`}
          >
            {video.summonerTier}
          </span>
        </div>
        <div className="flex items-center gap-2 text-xs text-on-surface-variant mt-0.5">
          <span className="font-mono font-bold text-on-surface">
            {video.clearTime}
          </span>
          <span className="bg-surface-container-highest text-on-surface-variant px-1.5 py-0.5 rounded text-[10px]">
            v{video.patchVersion}
          </span>
        </div>
      </div>

      {/* Play icon */}
      <span
        className={`material-symbols-outlined text-xl shrink-0 ${
          isActive ? "text-primary" : "text-on-surface-variant"
        }`}
      >
        {isActive ? "pause_circle" : "play_circle"}
      </span>
    </button>
  );
}
