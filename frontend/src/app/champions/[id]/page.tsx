"use client";

import { useState } from "react";
import { notFound } from "next/navigation";
import { use } from "react";
import GNB from "@/components/layout/GNB";
import Footer from "@/components/layout/Footer";
import ChampionMetaPanel from "@/components/detail/ChampionMetaPanel";
import VideoPlayer from "@/components/detail/VideoPlayer";
import VideoList from "@/components/detail/VideoList";
import { championDetails } from "@/data/mock";

export default function ChampionDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = use(params);
  const detail = championDetails[id];

  if (!detail) notFound();

  const [selectedIndex, setSelectedIndex] = useState(0);
  const currentVideo = detail.videos[selectedIndex];

  return (
    <>
      <GNB />
      <main className="pt-20 pb-12 px-6 md:px-8 flex-1 max-w-[1440px] mx-auto w-full">
        <div className="grid grid-cols-12 gap-6 lg:gap-8">
          {/* Left: Meta Panel */}
          <div className="col-span-12 lg:col-span-4">
            <ChampionMetaPanel meta={detail.championMeta} />
          </div>

          {/* Right: Video + Leaderboard */}
          <div className="col-span-12 lg:col-span-8 space-y-6">
            <VideoPlayer
              video={currentVideo}
              championName={detail.championMeta.championNameKo}
            />
            <VideoList
              videos={detail.videos}
              selectedIndex={selectedIndex}
              onSelect={setSelectedIndex}
            />
          </div>
        </div>
      </main>
      <Footer />
    </>
  );
}
