import GNB from "@/components/layout/GNB";
import Footer from "@/components/layout/Footer";
import HeroSection from "@/components/home/HeroSection";
import ChampionGrid from "@/components/home/ChampionGrid";
import { champions } from "@/data/mock";

export default function Home() {
  return (
    <>
      <GNB />
      <main className="pt-16 flex-1">
        <HeroSection champions={champions} />
        <ChampionGrid champions={champions} />
      </main>
      <Footer />
    </>
  );
}
