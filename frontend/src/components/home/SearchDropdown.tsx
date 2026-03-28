import type { Champion } from "@/types/champion";
import SearchItem from "./SearchItem";

export default function SearchDropdown({
  results,
}: {
  results: Champion[];
}) {
  const withVideo = results.filter((c) => c.hasVideo);
  const withoutVideo = results.filter((c) => !c.hasVideo);

  if (results.length === 0) {
    return (
      <div className="absolute top-full left-0 right-0 mt-2 bg-surface-container border border-white/10 rounded-xl overflow-hidden shadow-2xl z-50">
        <div className="px-4 py-6 text-center text-sm text-on-surface-variant">
          검색 결과가 없습니다.
        </div>
      </div>
    );
  }

  return (
    <div className="absolute top-full left-0 right-0 mt-2 bg-surface-container border border-white/10 rounded-xl overflow-hidden shadow-2xl z-50 max-h-[400px] overflow-y-auto text-left">
      {withVideo.map((c) => (
        <SearchItem key={c.championId} champion={c} />
      ))}
      {withoutVideo.length > 0 && withVideo.length > 0 && (
        <div className="border-t border-white/5" />
      )}
      {withoutVideo.map((c) => (
        <SearchItem key={c.championId} champion={c} />
      ))}
    </div>
  );
}
