import type { Video } from "@/types/champion";

export default function VideoPlayer({
  video,
  championName,
}: {
  video: Video;
  championName: string;
}) {
  return (
    <div className="space-y-3">
      {/* YouTube Embed */}
      <div className="relative aspect-video rounded-xl overflow-hidden bg-surface-container border border-white/5">
        <iframe
          src={`https://www.youtube.com/embed/${video.youtubeVideoId}?autoplay=0&rel=0`}
          title={`${championName} - ${video.summonerName} Full Clear`}
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowFullScreen
          className="absolute inset-0 w-full h-full"
        />
      </div>

      {/* Video Meta */}
      <div className="flex items-center gap-3 flex-wrap">
        {video.rank === 1 && (
          <span className="inline-flex items-center gap-1 bg-primary/10 text-primary text-xs font-bold px-3 py-1 rounded-full border border-primary/20">
            <span className="material-symbols-outlined text-sm">
              emoji_events
            </span>
            RECORD HOLDER
          </span>
        )}
        <span className="text-sm text-on-surface-variant">
          <span className="font-mono font-bold text-on-surface">
            {video.clearTime}
          </span>{" "}
          · Patch {video.patchVersion}
        </span>
      </div>
    </div>
  );
}
