/** API 1: 챔피언 목록 아이템 */
export interface Champion {
  championId: string;
  championNameKo: string;
  portraitUrl: string;
  splashUrl: string;
  bestClearTime: string | null;
  bestClearTimeMs: number | null;
  winRate: number;
  pickRate: number;
  tierRank: number;
  popularityRank: number;
  hasVideo: boolean;
}

/** API 2: 챔피언 상세 메타데이터 */
export interface ChampionMeta {
  championId: string;
  championNameKo: string;
  title: string;
  portraitUrl: string;
  splashUrl: string;
  tierRank: number;
  winRate: number;
  pickRate: number;
  banRate: number;
  bestClearTime: string;
  bestClearTimeMs: number;
  bestClearPatch: string;
}

/** API 2: 영상 리더보드 아이템 */
export interface Video {
  rank: number;
  clearTime: string;
  clearTimeMs: number;
  patchVersion: string;
  summonerName: string;
  summonerTier: "CHALLENGER" | "GRANDMASTER" | "MASTER";
  youtubeVideoId: string;
}

/** API 2: 챔피언 상세 응답 */
export interface ChampionDetail {
  championMeta: ChampionMeta;
  videos: Video[];
}
