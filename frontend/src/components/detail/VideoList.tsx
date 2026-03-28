import type { Video } from "@/types/champion";
import VideoListItem from "./VideoListItem";

export default function VideoList({
  videos,
  selectedIndex,
  onSelect,
}: {
  videos: Video[];
  selectedIndex: number;
  onSelect: (index: number) => void;
}) {
  // PRD: 영상이 1개일 경우 리스트 숨김
  if (videos.length <= 1) return null;

  return (
    <div className="space-y-3">
      <div className="flex items-center gap-4">
        <span className="w-8 h-[2px] bg-primary" />
        <h2 className="text-lg font-headline font-bold text-on-surface uppercase tracking-tight">
          Jungle Speedrun Leaderboard
        </h2>
      </div>
      <div className="space-y-2">
        {videos.map((video, i) => (
          <VideoListItem
            key={video.rank}
            video={video}
            isActive={i === selectedIndex}
            onSelect={() => onSelect(i)}
          />
        ))}
      </div>
    </div>
  );
}
