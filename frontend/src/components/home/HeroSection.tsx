import type { Champion } from "@/types/champion";
import SearchBar from "./SearchBar";

export default function HeroSection({
  champions,
}: {
  champions: Champion[];
}) {
  return (
    <section className="text-center py-20 px-8">
      <h1 className="text-5xl md:text-7xl font-headline font-bold tracking-tighter italic uppercase text-primary mb-4">
        Master the Jungle
      </h1>
      <p className="text-on-surface-variant text-lg max-w-xl mx-auto mb-10">
        천상계 유저들의 정글링 영상을 확인하세요.
      </p>
      <SearchBar champions={champions} />
    </section>
  );
}
