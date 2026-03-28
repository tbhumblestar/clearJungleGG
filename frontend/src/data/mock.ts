import type { Champion, ChampionDetail } from "@/types/champion";

const V = "16.6.1";
const p = (id: string) => `https://ddragon.leagueoflegends.com/cdn/${V}/img/champion/${id}.png`;
const s = (id: string) => `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${id}_0.jpg`;

/** 메인 페이지: 챔피언 목록 (popularityRank 순) */
export const champions: Champion[] = [
  { championId: "LeeSin", championNameKo: "리 신", portraitUrl: p("LeeSin"), splashUrl: s("LeeSin"), bestClearTime: "03:05", bestClearTimeMs: 185000, winRate: 51.2, pickRate: 15.4, tierRank: 1, popularityRank: 1, hasVideo: true },
  { championId: "Nidalee", championNameKo: "니달리", portraitUrl: p("Nidalee"), splashUrl: s("Nidalee"), bestClearTime: "02:58", bestClearTimeMs: 178000, winRate: 49.8, pickRate: 12.1, tierRank: 2, popularityRank: 2, hasVideo: true },
  { championId: "Graves", championNameKo: "그레이브즈", portraitUrl: p("Graves"), splashUrl: s("Graves"), bestClearTime: "03:01", bestClearTimeMs: 181000, winRate: 52.4, pickRate: 14.2, tierRank: 1, popularityRank: 3, hasVideo: true },
  { championId: "Khazix", championNameKo: "카직스", portraitUrl: p("Khazix"), splashUrl: s("Khazix"), bestClearTime: "03:08", bestClearTimeMs: 188000, winRate: 51.8, pickRate: 13.5, tierRank: 2, popularityRank: 4, hasVideo: true },
  { championId: "Kayn", championNameKo: "케인", portraitUrl: p("Kayn"), splashUrl: s("Kayn"), bestClearTime: "03:02", bestClearTimeMs: 182000, winRate: 51.5, pickRate: 13.1, tierRank: 1, popularityRank: 5, hasVideo: true },
  { championId: "Viego", championNameKo: "비에고", portraitUrl: p("Viego"), splashUrl: s("Viego"), bestClearTime: "03:04", bestClearTimeMs: 184000, winRate: 50.9, pickRate: 11.8, tierRank: 1, popularityRank: 6, hasVideo: true },
  { championId: "Hecarim", championNameKo: "헤카림", portraitUrl: p("Hecarim"), splashUrl: s("Hecarim"), bestClearTime: "03:00", bestClearTimeMs: 180000, winRate: 52.0, pickRate: 9.6, tierRank: 1, popularityRank: 7, hasVideo: true },
  { championId: "Diana", championNameKo: "다이애나", portraitUrl: p("Diana"), splashUrl: s("Diana"), bestClearTime: "03:06", bestClearTimeMs: 186000, winRate: 51.7, pickRate: 9.3, tierRank: 1, popularityRank: 8, hasVideo: true },
  { championId: "Ekko", championNameKo: "에코", portraitUrl: p("Ekko"), splashUrl: s("Ekko"), bestClearTime: "03:10", bestClearTimeMs: 190000, winRate: 51.1, pickRate: 8.9, tierRank: 2, popularityRank: 9, hasVideo: true },
  { championId: "Briar", championNameKo: "브라이어", portraitUrl: p("Briar"), splashUrl: s("Briar"), bestClearTime: "03:06", bestClearTimeMs: 186000, winRate: 51.5, pickRate: 9.2, tierRank: 2, popularityRank: 10, hasVideo: true },
  { championId: "Nocturne", championNameKo: "녹턴", portraitUrl: p("Nocturne"), splashUrl: s("Nocturne"), bestClearTime: "03:04", bestClearTimeMs: 184000, winRate: 52.3, pickRate: 6.8, tierRank: 2, popularityRank: 11, hasVideo: true },
  { championId: "Rengar", championNameKo: "렝가", portraitUrl: p("Rengar"), splashUrl: s("Rengar"), bestClearTime: "03:12", bestClearTimeMs: 192000, winRate: 50.5, pickRate: 8.7, tierRank: 3, popularityRank: 12, hasVideo: true },
  { championId: "Belveth", championNameKo: "벨베스", portraitUrl: p("Belveth"), splashUrl: s("Belveth"), bestClearTime: "03:03", bestClearTimeMs: 183000, winRate: 50.1, pickRate: 7.3, tierRank: 2, popularityRank: 13, hasVideo: true },
  { championId: "Evelynn", championNameKo: "이블린", portraitUrl: p("Evelynn"), splashUrl: s("Evelynn"), bestClearTime: "03:07", bestClearTimeMs: 187000, winRate: 51.3, pickRate: 8.4, tierRank: 2, popularityRank: 14, hasVideo: true },
  { championId: "Lillia", championNameKo: "릴리아", portraitUrl: p("Lillia"), splashUrl: s("Lillia"), bestClearTime: "02:55", bestClearTimeMs: 175000, winRate: 51.9, pickRate: 6.2, tierRank: 1, popularityRank: 15, hasVideo: true },
  { championId: "Kindred", championNameKo: "킨드레드", portraitUrl: p("Kindred"), splashUrl: s("Kindred"), bestClearTime: "03:09", bestClearTimeMs: 189000, winRate: 50.7, pickRate: 7.1, tierRank: 2, popularityRank: 16, hasVideo: true },
  { championId: "JarvanIV", championNameKo: "자르반 4세", portraitUrl: p("JarvanIV"), splashUrl: s("JarvanIV"), bestClearTime: "03:14", bestClearTimeMs: 194000, winRate: 50.8, pickRate: 6.5, tierRank: 3, popularityRank: 17, hasVideo: true },
  { championId: "Sejuani", championNameKo: "세주아니", portraitUrl: p("Sejuani"), splashUrl: s("Sejuani"), bestClearTime: "03:11", bestClearTimeMs: 191000, winRate: 52.1, pickRate: 5.8, tierRank: 2, popularityRank: 18, hasVideo: true },
  { championId: "Elise", championNameKo: "엘리스", portraitUrl: p("Elise"), splashUrl: s("Elise"), bestClearTime: "03:09", bestClearTimeMs: 189000, winRate: 49.5, pickRate: 4.9, tierRank: 3, popularityRank: 19, hasVideo: true },
  { championId: "RekSai", championNameKo: "렉사이", portraitUrl: p("RekSai"), splashUrl: s("RekSai"), bestClearTime: "03:08", bestClearTimeMs: 188000, winRate: 50.2, pickRate: 4.3, tierRank: 3, popularityRank: 20, hasVideo: true },
  // === 채널 영상에서 추가된 챔피언 ===
  { championId: "Vi", championNameKo: "바이", portraitUrl: p("Vi"), splashUrl: s("Vi"), bestClearTime: "03:07", bestClearTimeMs: 187000, winRate: 51.4, pickRate: 7.8, tierRank: 2, popularityRank: 21, hasVideo: true },
  { championId: "Olaf", championNameKo: "올라프", portraitUrl: p("Olaf"), splashUrl: s("Olaf"), bestClearTime: "03:05", bestClearTimeMs: 185000, winRate: 50.6, pickRate: 5.4, tierRank: 2, popularityRank: 22, hasVideo: true },
  { championId: "Udyr", championNameKo: "우디르", portraitUrl: p("Udyr"), splashUrl: s("Udyr"), bestClearTime: "03:03", bestClearTimeMs: 183000, winRate: 51.8, pickRate: 5.1, tierRank: 2, popularityRank: 23, hasVideo: true },
  { championId: "Shyvana", championNameKo: "쉬바나", portraitUrl: p("Shyvana"), splashUrl: s("Shyvana"), bestClearTime: "03:02", bestClearTimeMs: 182000, winRate: 52.5, pickRate: 4.8, tierRank: 1, popularityRank: 24, hasVideo: true },
  { championId: "Warwick", championNameKo: "워윅", portraitUrl: p("Warwick"), splashUrl: s("Warwick"), bestClearTime: "03:10", bestClearTimeMs: 190000, winRate: 52.2, pickRate: 6.1, tierRank: 2, popularityRank: 25, hasVideo: true },
  { championId: "Volibear", championNameKo: "볼리베어", portraitUrl: p("Volibear"), splashUrl: s("Volibear"), bestClearTime: "03:08", bestClearTimeMs: 188000, winRate: 51.6, pickRate: 5.5, tierRank: 2, popularityRank: 26, hasVideo: true },
  { championId: "XinZhao", championNameKo: "신 짜오", portraitUrl: p("XinZhao"), splashUrl: s("XinZhao"), bestClearTime: "03:09", bestClearTimeMs: 189000, winRate: 51.0, pickRate: 5.2, tierRank: 2, popularityRank: 27, hasVideo: true },
  { championId: "Taliyah", championNameKo: "탈리야", portraitUrl: p("Taliyah"), splashUrl: s("Taliyah"), bestClearTime: "03:06", bestClearTimeMs: 186000, winRate: 50.3, pickRate: 3.9, tierRank: 3, popularityRank: 28, hasVideo: true },
  { championId: "Brand", championNameKo: "브랜드", portraitUrl: p("Brand"), splashUrl: s("Brand"), bestClearTime: "03:11", bestClearTimeMs: 191000, winRate: 51.2, pickRate: 4.6, tierRank: 3, popularityRank: 29, hasVideo: true },
  { championId: "Talon", championNameKo: "탈론", portraitUrl: p("Talon"), splashUrl: s("Talon"), bestClearTime: "03:05", bestClearTimeMs: 185000, winRate: 50.8, pickRate: 4.2, tierRank: 2, popularityRank: 30, hasVideo: true },
  { championId: "Maokai", championNameKo: "마오카이", portraitUrl: p("Maokai"), splashUrl: s("Maokai"), bestClearTime: "03:13", bestClearTimeMs: 193000, winRate: 51.0, pickRate: 3.4, tierRank: 3, popularityRank: 31, hasVideo: true },
  { championId: "DrMundo", championNameKo: "문도 박사", portraitUrl: p("DrMundo"), splashUrl: s("DrMundo"), bestClearTime: "03:08", bestClearTimeMs: 188000, winRate: 52.0, pickRate: 4.0, tierRank: 2, popularityRank: 32, hasVideo: true },
  { championId: "Fizz", championNameKo: "피즈", portraitUrl: p("Fizz"), splashUrl: s("Fizz"), bestClearTime: "03:12", bestClearTimeMs: 192000, winRate: 49.8, pickRate: 3.5, tierRank: 3, popularityRank: 33, hasVideo: true },
  { championId: "Karthus", championNameKo: "카서스", portraitUrl: p("Karthus"), splashUrl: s("Karthus"), bestClearTime: "03:04", bestClearTimeMs: 184000, winRate: 51.7, pickRate: 4.7, tierRank: 2, popularityRank: 34, hasVideo: true },
  { championId: "ChoGath", championNameKo: "초가스", portraitUrl: p("Chogath"), splashUrl: s("Chogath"), bestClearTime: "03:15", bestClearTimeMs: 195000, winRate: 50.4, pickRate: 3.1, tierRank: 3, popularityRank: 35, hasVideo: true },
  // === 영상 없는 챔피언 ===
  { championId: "Teemo", championNameKo: "티모", portraitUrl: p("Teemo"), splashUrl: s("Teemo"), bestClearTime: null, bestClearTimeMs: null, winRate: 48.1, pickRate: 1.2, tierRank: 5, popularityRank: 45, hasVideo: false },
  { championId: "Amumu", championNameKo: "아무무", portraitUrl: p("Amumu"), splashUrl: s("Amumu"), bestClearTime: null, bestClearTimeMs: null, winRate: 52.3, pickRate: 4.1, tierRank: 3, popularityRank: 46, hasVideo: false },
];

/** 상세 페이지: 챔피언별 상세 데이터 */
export const championDetails: Record<string, ChampionDetail> = {
  LeeSin: {
    championMeta: { championId: "LeeSin", championNameKo: "리 신", title: "눈먼 수도승", portraitUrl: p("LeeSin"), splashUrl: s("LeeSin"), tierRank: 1, winRate: 51.2, pickRate: 15.4, banRate: 8.2, bestClearTime: "03:05", bestClearTimeMs: 185000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:05", clearTimeMs: 185000, patchVersion: "16.6", summonerName: "Hide on bush", summonerTier: "CHALLENGER", youtubeVideoId: "DPn--PzoYWM" },
      { rank: 2, clearTime: "03:08", clearTimeMs: 188000, patchVersion: "16.5", summonerName: "Canyon", summonerTier: "CHALLENGER", youtubeVideoId: "ZnCB_-IAe-0" },
    ],
  },
  Nidalee: {
    championMeta: { championId: "Nidalee", championNameKo: "니달리", title: "짐승 사냥꾼", portraitUrl: p("Nidalee"), splashUrl: s("Nidalee"), tierRank: 2, winRate: 49.8, pickRate: 12.1, banRate: 5.4, bestClearTime: "02:58", bestClearTimeMs: 178000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "02:58", clearTimeMs: 178000, patchVersion: "16.6", summonerName: "Tarzan", summonerTier: "CHALLENGER", youtubeVideoId: "DPn--PzoYWM" },
      { rank: 2, clearTime: "03:02", clearTimeMs: 182000, patchVersion: "16.5", summonerName: "Oner", summonerTier: "CHALLENGER", youtubeVideoId: "ZnCB_-IAe-0" },
      { rank: 3, clearTime: "03:05", clearTimeMs: 185000, patchVersion: "16.6", summonerName: "Canyon", summonerTier: "GRANDMASTER", youtubeVideoId: "WMkkguCRfmQ" },
    ],
  },
  Graves: {
    championMeta: { championId: "Graves", championNameKo: "그레이브즈", title: "무법자", portraitUrl: p("Graves"), splashUrl: s("Graves"), tierRank: 1, winRate: 52.4, pickRate: 14.2, banRate: 10.1, bestClearTime: "03:01", bestClearTimeMs: 181000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:01", clearTimeMs: 181000, patchVersion: "16.6", summonerName: "Inspired", summonerTier: "CHALLENGER", youtubeVideoId: "WMkkguCRfmQ" },
      { rank: 2, clearTime: "03:04", clearTimeMs: 184000, patchVersion: "16.5", summonerName: "Blaber", summonerTier: "CHALLENGER", youtubeVideoId: "DPn--PzoYWM" },
      { rank: 3, clearTime: "03:07", clearTimeMs: 187000, patchVersion: "16.6", summonerName: "Jankos", summonerTier: "GRANDMASTER", youtubeVideoId: "ZnCB_-IAe-0" },
    ],
  },
  Khazix: {
    championMeta: { championId: "Khazix", championNameKo: "카직스", title: "공허의 포식자", portraitUrl: p("Khazix"), splashUrl: s("Khazix"), tierRank: 2, winRate: 51.8, pickRate: 13.5, banRate: 12.3, bestClearTime: "03:08", bestClearTimeMs: 188000, bestClearPatch: "16.5" },
    videos: [
      { rank: 1, clearTime: "03:08", clearTimeMs: 188000, patchVersion: "16.5", summonerName: "Kanavi", summonerTier: "CHALLENGER", youtubeVideoId: "lbDiUKF5nr0" },
      { rank: 2, clearTime: "03:11", clearTimeMs: 191000, patchVersion: "16.6", summonerName: "Clid", summonerTier: "CHALLENGER", youtubeVideoId: "6OhIcjDxol0" },
    ],
  },
  Kayn: {
    championMeta: { championId: "Kayn", championNameKo: "케인", title: "그림자 사신", portraitUrl: p("Kayn"), splashUrl: s("Kayn"), tierRank: 1, winRate: 51.5, pickRate: 13.1, banRate: 11.2, bestClearTime: "03:02", bestClearTimeMs: 182000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:02", clearTimeMs: 182000, patchVersion: "16.6", summonerName: "Clid", summonerTier: "CHALLENGER", youtubeVideoId: "Pr8hvO-T5iI" },
      { rank: 2, clearTime: "03:05", clearTimeMs: 185000, patchVersion: "16.5", summonerName: "Jankos", summonerTier: "CHALLENGER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  Viego: {
    championMeta: { championId: "Viego", championNameKo: "비에고", title: "몰락한 왕", portraitUrl: p("Viego"), splashUrl: s("Viego"), tierRank: 1, winRate: 50.9, pickRate: 11.8, banRate: 7.6, bestClearTime: "03:04", bestClearTimeMs: 184000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:04", clearTimeMs: 184000, patchVersion: "16.6", summonerName: "Canyon", summonerTier: "CHALLENGER", youtubeVideoId: "4yEgeFS17ks" },
      { rank: 2, clearTime: "03:07", clearTimeMs: 187000, patchVersion: "16.5", summonerName: "Lucid", summonerTier: "CHALLENGER", youtubeVideoId: "xMiW-C5fYSk" },
      { rank: 3, clearTime: "03:09", clearTimeMs: 189000, patchVersion: "16.6", summonerName: "Kanavi", summonerTier: "GRANDMASTER", youtubeVideoId: "4yEgeFS17ks" },
    ],
  },
  Hecarim: {
    championMeta: { championId: "Hecarim", championNameKo: "헤카림", title: "전쟁의 전조", portraitUrl: p("Hecarim"), splashUrl: s("Hecarim"), tierRank: 1, winRate: 52.0, pickRate: 9.6, banRate: 6.5, bestClearTime: "03:00", bestClearTimeMs: 180000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:00", clearTimeMs: 180000, patchVersion: "16.6", summonerName: "Peanut", summonerTier: "CHALLENGER", youtubeVideoId: "3zYYJM0GdPk" },
      { rank: 2, clearTime: "03:03", clearTimeMs: 183000, patchVersion: "16.5", summonerName: "Oner", summonerTier: "CHALLENGER", youtubeVideoId: "aK-7CpGbjac" },
      { rank: 3, clearTime: "03:06", clearTimeMs: 186000, patchVersion: "16.6", summonerName: "Elyoya", summonerTier: "GRANDMASTER", youtubeVideoId: "3zYYJM0GdPk" },
    ],
  },
  Diana: {
    championMeta: { championId: "Diana", championNameKo: "다이애나", title: "달빛의 복수", portraitUrl: p("Diana"), splashUrl: s("Diana"), tierRank: 1, winRate: 51.7, pickRate: 9.3, banRate: 6.8, bestClearTime: "03:06", bestClearTimeMs: 186000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:06", clearTimeMs: 186000, patchVersion: "16.6", summonerName: "Kanavi", summonerTier: "CHALLENGER", youtubeVideoId: "k-PEw3rD_QQ" },
      { rank: 2, clearTime: "03:09", clearTimeMs: 189000, patchVersion: "16.5", summonerName: "Tarzan", summonerTier: "CHALLENGER", youtubeVideoId: "KPpdfcatWng" },
    ],
  },
  Ekko: {
    championMeta: { championId: "Ekko", championNameKo: "에코", title: "시간을 달리는 소년", portraitUrl: p("Ekko"), splashUrl: s("Ekko"), tierRank: 2, winRate: 51.1, pickRate: 8.9, banRate: 5.2, bestClearTime: "03:10", bestClearTimeMs: 190000, bestClearPatch: "16.5" },
    videos: [
      { rank: 1, clearTime: "03:10", clearTimeMs: 190000, patchVersion: "16.5", summonerName: "Wei", summonerTier: "CHALLENGER", youtubeVideoId: "625skLkSy88" },
      { rank: 2, clearTime: "03:13", clearTimeMs: 193000, patchVersion: "16.6", summonerName: "Lucid", summonerTier: "CHALLENGER", youtubeVideoId: "xuXv7r5akNI" },
    ],
  },
  Briar: {
    championMeta: { championId: "Briar", championNameKo: "브라이어", title: "억제된 굶주림", portraitUrl: p("Briar"), splashUrl: s("Briar"), tierRank: 2, winRate: 51.5, pickRate: 9.2, banRate: 8.7, bestClearTime: "03:06", bestClearTimeMs: 186000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:06", clearTimeMs: 186000, patchVersion: "16.6", summonerName: "Oner", summonerTier: "CHALLENGER", youtubeVideoId: "HYmfoY682VY" },
      { rank: 2, clearTime: "03:09", clearTimeMs: 189000, patchVersion: "16.5", summonerName: "Closer", summonerTier: "CHALLENGER", youtubeVideoId: "CsVb6CEgXdU" },
    ],
  },
  Nocturne: {
    championMeta: { championId: "Nocturne", championNameKo: "녹턴", title: "영원한 악몽", portraitUrl: p("Nocturne"), splashUrl: s("Nocturne"), tierRank: 2, winRate: 52.3, pickRate: 6.8, banRate: 4.5, bestClearTime: "03:04", bestClearTimeMs: 184000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:04", clearTimeMs: 184000, patchVersion: "16.6", summonerName: "Clid", summonerTier: "CHALLENGER", youtubeVideoId: "Hk4JEIDRXXc" },
      { rank: 2, clearTime: "03:07", clearTimeMs: 187000, patchVersion: "16.5", summonerName: "Inspired", summonerTier: "CHALLENGER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  Rengar: {
    championMeta: { championId: "Rengar", championNameKo: "렝가", title: "사냥의 군주", portraitUrl: p("Rengar"), splashUrl: s("Rengar"), tierRank: 3, winRate: 50.5, pickRate: 8.7, banRate: 6.1, bestClearTime: "03:12", bestClearTimeMs: 192000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:12", clearTimeMs: 192000, patchVersion: "16.6", summonerName: "Peanut", summonerTier: "CHALLENGER", youtubeVideoId: "DPn--PzoYWM" },
      { rank: 2, clearTime: "03:14", clearTimeMs: 194000, patchVersion: "16.5", summonerName: "Closer", summonerTier: "GRANDMASTER", youtubeVideoId: "ZnCB_-IAe-0" },
    ],
  },
  Belveth: {
    championMeta: { championId: "Belveth", championNameKo: "벨베스", title: "공허의 여제", portraitUrl: p("Belveth"), splashUrl: s("Belveth"), tierRank: 2, winRate: 50.1, pickRate: 7.3, banRate: 9.8, bestClearTime: "03:03", bestClearTimeMs: 183000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:03", clearTimeMs: 183000, patchVersion: "16.6", summonerName: "Oner", summonerTier: "CHALLENGER", youtubeVideoId: "iv9fVuVrU2g" },
      { rank: 2, clearTime: "03:06", clearTimeMs: 186000, patchVersion: "16.5", summonerName: "Elyoya", summonerTier: "CHALLENGER", youtubeVideoId: "y_xV72SHFt8" },
      { rank: 3, clearTime: "03:09", clearTimeMs: 189000, patchVersion: "16.6", summonerName: "Willer", summonerTier: "GRANDMASTER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  Evelynn: {
    championMeta: { championId: "Evelynn", championNameKo: "이블린", title: "고통의 포옹", portraitUrl: p("Evelynn"), splashUrl: s("Evelynn"), tierRank: 2, winRate: 51.3, pickRate: 8.4, banRate: 7.9, bestClearTime: "03:07", bestClearTimeMs: 187000, bestClearPatch: "16.5" },
    videos: [
      { rank: 1, clearTime: "03:07", clearTimeMs: 187000, patchVersion: "16.5", summonerName: "Inspired", summonerTier: "CHALLENGER", youtubeVideoId: "eaA_dhsKFOc" },
      { rank: 2, clearTime: "03:10", clearTimeMs: 190000, patchVersion: "16.6", summonerName: "Tarzan", summonerTier: "CHALLENGER", youtubeVideoId: "LmiDTfVq0-U" },
      { rank: 3, clearTime: "03:12", clearTimeMs: 192000, patchVersion: "16.5", summonerName: "Closer", summonerTier: "GRANDMASTER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  Lillia: {
    championMeta: { championId: "Lillia", championNameKo: "릴리아", title: "수줍은 꽃", portraitUrl: p("Lillia"), splashUrl: s("Lillia"), tierRank: 1, winRate: 51.9, pickRate: 6.2, banRate: 4.7, bestClearTime: "02:55", bestClearTimeMs: 175000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "02:55", clearTimeMs: 175000, patchVersion: "16.6", summonerName: "Oner", summonerTier: "CHALLENGER", youtubeVideoId: "DpAnaPSKy_k" },
      { rank: 2, clearTime: "02:58", clearTimeMs: 178000, patchVersion: "16.5", summonerName: "Canyon", summonerTier: "CHALLENGER", youtubeVideoId: "yiXjIwdwOo0" },
      { rank: 3, clearTime: "03:01", clearTimeMs: 181000, patchVersion: "16.6", summonerName: "Peanut", summonerTier: "GRANDMASTER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  Kindred: {
    championMeta: { championId: "Kindred", championNameKo: "킨드레드", title: "영겁의 사냥꾼", portraitUrl: p("Kindred"), splashUrl: s("Kindred"), tierRank: 2, winRate: 50.7, pickRate: 7.1, banRate: 3.8, bestClearTime: "03:09", bestClearTimeMs: 189000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:09", clearTimeMs: 189000, patchVersion: "16.6", summonerName: "Blaber", summonerTier: "CHALLENGER", youtubeVideoId: "YTVqaTw94dM" },
      { rank: 2, clearTime: "03:12", clearTimeMs: 192000, patchVersion: "16.5", summonerName: "Willer", summonerTier: "GRANDMASTER", youtubeVideoId: "J8m67gKBT60" },
    ],
  },
  JarvanIV: {
    championMeta: { championId: "JarvanIV", championNameKo: "자르반 4세", title: "데마시아의 모범", portraitUrl: p("JarvanIV"), splashUrl: s("JarvanIV"), tierRank: 3, winRate: 50.8, pickRate: 6.5, banRate: 3.2, bestClearTime: "03:14", bestClearTimeMs: 194000, bestClearPatch: "16.5" },
    videos: [
      { rank: 1, clearTime: "03:14", clearTimeMs: 194000, patchVersion: "16.5", summonerName: "Hide on bush", summonerTier: "CHALLENGER", youtubeVideoId: "ELk5PftfzwI" },
      { rank: 2, clearTime: "03:16", clearTimeMs: 196000, patchVersion: "16.6", summonerName: "Willer", summonerTier: "GRANDMASTER", youtubeVideoId: "hlae0E8nT_w" },
    ],
  },
  Sejuani: {
    championMeta: { championId: "Sejuani", championNameKo: "세주아니", title: "북방의 분노", portraitUrl: p("Sejuani"), splashUrl: s("Sejuani"), tierRank: 2, winRate: 52.1, pickRate: 5.8, banRate: 2.9, bestClearTime: "03:11", bestClearTimeMs: 191000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:11", clearTimeMs: 191000, patchVersion: "16.6", summonerName: "Jankos", summonerTier: "CHALLENGER", youtubeVideoId: "2E8XiB3tPtc" },
      { rank: 2, clearTime: "03:14", clearTimeMs: 194000, patchVersion: "16.5", summonerName: "Blaber", summonerTier: "GRANDMASTER", youtubeVideoId: "JJ4xz8RMKOs" },
    ],
  },
  Elise: {
    championMeta: { championId: "Elise", championNameKo: "엘리스", title: "거미 여왕", portraitUrl: p("Elise"), splashUrl: s("Elise"), tierRank: 3, winRate: 49.5, pickRate: 4.9, banRate: 3.5, bestClearTime: "03:09", bestClearTimeMs: 189000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:09", clearTimeMs: 189000, patchVersion: "16.6", summonerName: "Canyon", summonerTier: "CHALLENGER", youtubeVideoId: "ByARspyLWYg" },
      { rank: 2, clearTime: "03:12", clearTimeMs: 192000, patchVersion: "16.5", summonerName: "Wei", summonerTier: "CHALLENGER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  RekSai: {
    championMeta: { championId: "RekSai", championNameKo: "렉사이", title: "공허의 굴착아수", portraitUrl: p("RekSai"), splashUrl: s("RekSai"), tierRank: 3, winRate: 50.2, pickRate: 4.3, banRate: 2.1, bestClearTime: "03:08", bestClearTimeMs: 188000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:08", clearTimeMs: 188000, patchVersion: "16.6", summonerName: "Tarzan", summonerTier: "CHALLENGER", youtubeVideoId: "HRVxugCXCCQ" },
      { rank: 2, clearTime: "03:11", clearTimeMs: 191000, patchVersion: "16.5", summonerName: "Clid", summonerTier: "CHALLENGER", youtubeVideoId: "acnagTwdbOI" },
    ],
  },
  Vi: {
    championMeta: { championId: "Vi", championNameKo: "바이", title: "필트오버의 집행자", portraitUrl: p("Vi"), splashUrl: s("Vi"), tierRank: 2, winRate: 51.4, pickRate: 7.8, banRate: 4.3, bestClearTime: "03:07", bestClearTimeMs: 187000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:07", clearTimeMs: 187000, patchVersion: "16.6", summonerName: "Peanut", summonerTier: "CHALLENGER", youtubeVideoId: "y_xV72SHFt8" },
      { rank: 2, clearTime: "03:10", clearTimeMs: 190000, patchVersion: "16.5", summonerName: "Elyoya", summonerTier: "CHALLENGER", youtubeVideoId: "Zj7SGIH4Cgw" },
    ],
  },
  Olaf: {
    championMeta: { championId: "Olaf", championNameKo: "올라프", title: "광전사", portraitUrl: p("Olaf"), splashUrl: s("Olaf"), tierRank: 2, winRate: 50.6, pickRate: 5.4, banRate: 3.1, bestClearTime: "03:05", bestClearTimeMs: 185000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:05", clearTimeMs: 185000, patchVersion: "16.6", summonerName: "Inspired", summonerTier: "CHALLENGER", youtubeVideoId: "DpAnaPSKy_k" },
      { rank: 2, clearTime: "03:08", clearTimeMs: 188000, patchVersion: "16.5", summonerName: "Blaber", summonerTier: "GRANDMASTER", youtubeVideoId: "yiXjIwdwOo0" },
    ],
  },
  Udyr: {
    championMeta: { championId: "Udyr", championNameKo: "우디르", title: "영혼 수호자", portraitUrl: p("Udyr"), splashUrl: s("Udyr"), tierRank: 2, winRate: 51.8, pickRate: 5.1, banRate: 3.6, bestClearTime: "03:03", bestClearTimeMs: 183000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:03", clearTimeMs: 183000, patchVersion: "16.6", summonerName: "Kanavi", summonerTier: "CHALLENGER", youtubeVideoId: "iv9fVuVrU2g" },
      { rank: 2, clearTime: "03:06", clearTimeMs: 186000, patchVersion: "16.5", summonerName: "Wei", summonerTier: "CHALLENGER", youtubeVideoId: "dcXXdMA1q1M" },
    ],
  },
  Shyvana: {
    championMeta: { championId: "Shyvana", championNameKo: "쉬바나", title: "하프 드래곤", portraitUrl: p("Shyvana"), splashUrl: s("Shyvana"), tierRank: 1, winRate: 52.5, pickRate: 4.8, banRate: 2.8, bestClearTime: "03:02", bestClearTimeMs: 182000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:02", clearTimeMs: 182000, patchVersion: "16.6", summonerName: "Hide on bush", summonerTier: "CHALLENGER", youtubeVideoId: "eaA_dhsKFOc" },
      { rank: 2, clearTime: "03:05", clearTimeMs: 185000, patchVersion: "16.5", summonerName: "Lucid", summonerTier: "CHALLENGER", youtubeVideoId: "LmiDTfVq0-U" },
    ],
  },
  Warwick: {
    championMeta: { championId: "Warwick", championNameKo: "워윅", title: "자운의 무자비한 사냥감", portraitUrl: p("Warwick"), splashUrl: s("Warwick"), tierRank: 2, winRate: 52.2, pickRate: 6.1, banRate: 2.4, bestClearTime: "03:10", bestClearTimeMs: 190000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:10", clearTimeMs: 190000, patchVersion: "16.6", summonerName: "Jankos", summonerTier: "CHALLENGER", youtubeVideoId: "YTVqaTw94dM" },
      { rank: 2, clearTime: "03:13", clearTimeMs: 193000, patchVersion: "16.5", summonerName: "Closer", summonerTier: "GRANDMASTER", youtubeVideoId: "J8m67gKBT60" },
    ],
  },
  Volibear: {
    championMeta: { championId: "Volibear", championNameKo: "볼리베어", title: "폭풍의 분노", portraitUrl: p("Volibear"), splashUrl: s("Volibear"), tierRank: 2, winRate: 51.6, pickRate: 5.5, banRate: 3.0, bestClearTime: "03:08", bestClearTimeMs: 188000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:08", clearTimeMs: 188000, patchVersion: "16.6", summonerName: "Oner", summonerTier: "CHALLENGER", youtubeVideoId: "3zYYJM0GdPk" },
      { rank: 2, clearTime: "03:11", clearTimeMs: 191000, patchVersion: "16.5", summonerName: "Canyon", summonerTier: "CHALLENGER", youtubeVideoId: "aK-7CpGbjac" },
    ],
  },
  XinZhao: {
    championMeta: { championId: "XinZhao", championNameKo: "신 짜오", title: "데마시아의 원수", portraitUrl: p("XinZhao"), splashUrl: s("XinZhao"), tierRank: 2, winRate: 51.0, pickRate: 5.2, banRate: 2.6, bestClearTime: "03:09", bestClearTimeMs: 189000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:09", clearTimeMs: 189000, patchVersion: "16.6", summonerName: "Tarzan", summonerTier: "CHALLENGER", youtubeVideoId: "HRVxugCXCCQ" },
      { rank: 2, clearTime: "03:12", clearTimeMs: 192000, patchVersion: "16.5", summonerName: "Clid", summonerTier: "GRANDMASTER", youtubeVideoId: "acnagTwdbOI" },
    ],
  },
  Taliyah: {
    championMeta: { championId: "Taliyah", championNameKo: "탈리야", title: "돌의 직조자", portraitUrl: p("Taliyah"), splashUrl: s("Taliyah"), tierRank: 3, winRate: 50.3, pickRate: 3.9, banRate: 2.2, bestClearTime: "03:06", bestClearTimeMs: 186000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:06", clearTimeMs: 186000, patchVersion: "16.6", summonerName: "Wei", summonerTier: "CHALLENGER", youtubeVideoId: "1rDcH_WAaxM" },
      { rank: 2, clearTime: "03:09", clearTimeMs: 189000, patchVersion: "16.5", summonerName: "Elyoya", summonerTier: "CHALLENGER", youtubeVideoId: "Wz6QGpab3jI" },
    ],
  },
  Brand: {
    championMeta: { championId: "Brand", championNameKo: "브랜드", title: "불타는 복수", portraitUrl: p("Brand"), splashUrl: s("Brand"), tierRank: 3, winRate: 51.2, pickRate: 4.6, banRate: 5.8, bestClearTime: "03:11", bestClearTimeMs: 191000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:11", clearTimeMs: 191000, patchVersion: "16.6", summonerName: "Lucid", summonerTier: "CHALLENGER", youtubeVideoId: "ByARspyLWYg" },
      { rank: 2, clearTime: "03:14", clearTimeMs: 194000, patchVersion: "16.5", summonerName: "Willer", summonerTier: "GRANDMASTER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  Talon: {
    championMeta: { championId: "Talon", championNameKo: "탈론", title: "칼날의 그림자", portraitUrl: p("Talon"), splashUrl: s("Talon"), tierRank: 2, winRate: 50.8, pickRate: 4.2, banRate: 3.4, bestClearTime: "03:05", bestClearTimeMs: 185000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:05", clearTimeMs: 185000, patchVersion: "16.6", summonerName: "Hide on bush", summonerTier: "CHALLENGER", youtubeVideoId: "TiBPmueQTLI" },
      { rank: 2, clearTime: "03:08", clearTimeMs: 188000, patchVersion: "16.5", summonerName: "Kanavi", summonerTier: "GRANDMASTER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  Maokai: {
    championMeta: { championId: "Maokai", championNameKo: "마오카이", title: "뒤틀린 나무정령", portraitUrl: p("Maokai"), splashUrl: s("Maokai"), tierRank: 3, winRate: 51.0, pickRate: 3.4, banRate: 1.8, bestClearTime: "03:13", bestClearTimeMs: 193000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:13", clearTimeMs: 193000, patchVersion: "16.6", summonerName: "Peanut", summonerTier: "CHALLENGER", youtubeVideoId: "afSvPoTonZQ" },
      { rank: 2, clearTime: "03:16", clearTimeMs: 196000, patchVersion: "16.5", summonerName: "Jankos", summonerTier: "GRANDMASTER", youtubeVideoId: "DPn--PzoYWM" },
    ],
  },
  DrMundo: {
    championMeta: { championId: "DrMundo", championNameKo: "문도 박사", title: "자운의 광인", portraitUrl: p("DrMundo"), splashUrl: s("DrMundo"), tierRank: 2, winRate: 52.0, pickRate: 4.0, banRate: 2.5, bestClearTime: "03:08", bestClearTimeMs: 188000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:08", clearTimeMs: 188000, patchVersion: "16.6", summonerName: "Blaber", summonerTier: "CHALLENGER", youtubeVideoId: "oV_21CnFYQA" },
      { rank: 2, clearTime: "03:11", clearTimeMs: 191000, patchVersion: "16.5", summonerName: "Inspired", summonerTier: "CHALLENGER", youtubeVideoId: "QMpU61VotFo" },
      { rank: 3, clearTime: "03:14", clearTimeMs: 194000, patchVersion: "16.6", summonerName: "Closer", summonerTier: "GRANDMASTER", youtubeVideoId: "Mc4I_TueYE8" },
    ],
  },
  Fizz: {
    championMeta: { championId: "Fizz", championNameKo: "피즈", title: "대양의 말썽꾸러기", portraitUrl: p("Fizz"), splashUrl: s("Fizz"), tierRank: 3, winRate: 49.8, pickRate: 3.5, banRate: 4.1, bestClearTime: "03:12", bestClearTimeMs: 192000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:12", clearTimeMs: 192000, patchVersion: "16.6", summonerName: "Canyon", summonerTier: "CHALLENGER", youtubeVideoId: "gTG-9740OyE" },
      { rank: 2, clearTime: "03:15", clearTimeMs: 195000, patchVersion: "16.5", summonerName: "Oner", summonerTier: "GRANDMASTER", youtubeVideoId: "UlkskslMpVU" },
    ],
  },
  Karthus: {
    championMeta: { championId: "Karthus", championNameKo: "카서스", title: "죽음의 노래꾼", portraitUrl: p("Karthus"), splashUrl: s("Karthus"), tierRank: 2, winRate: 51.7, pickRate: 4.7, banRate: 6.3, bestClearTime: "03:04", bestClearTimeMs: 184000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:04", clearTimeMs: 184000, patchVersion: "16.6", summonerName: "Tarzan", summonerTier: "CHALLENGER", youtubeVideoId: "SXd-uzFe1fU" },
      { rank: 2, clearTime: "03:07", clearTimeMs: 187000, patchVersion: "16.5", summonerName: "Lucid", summonerTier: "CHALLENGER", youtubeVideoId: "KTqhE0Vai4w" },
    ],
  },
  ChoGath: {
    championMeta: { championId: "ChoGath", championNameKo: "초가스", title: "공포의 식충", portraitUrl: p("Chogath"), splashUrl: s("Chogath"), tierRank: 3, winRate: 50.4, pickRate: 3.1, banRate: 1.5, bestClearTime: "03:15", bestClearTimeMs: 195000, bestClearPatch: "16.6" },
    videos: [
      { rank: 1, clearTime: "03:15", clearTimeMs: 195000, patchVersion: "16.6", summonerName: "Clid", summonerTier: "CHALLENGER", youtubeVideoId: "hOBsCddHC9E" },
      { rank: 2, clearTime: "03:18", clearTimeMs: 198000, patchVersion: "16.5", summonerName: "Wei", summonerTier: "GRANDMASTER", youtubeVideoId: "09sT_8HDqj0" },
    ],
  },
};
