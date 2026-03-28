import Link from "next/link";

export default function GNB() {
  return (
    <nav className="fixed top-0 w-full z-50 bg-[#0A0E17]/80 backdrop-blur-xl shadow-[0_0_20px_rgba(16,185,129,0.06)] border-b border-white/5">
      <div className="flex justify-between items-center h-16 px-8 max-w-[1440px] mx-auto">
        {/* Logo */}
        <Link
          href="/"
          className="text-2xl font-bold tracking-tighter text-primary uppercase italic font-headline"
        >
          JungleClear.gg
        </Link>

        {/* Nav Links */}
        <div className="hidden md:flex items-center gap-8 font-headline tracking-tight">
          <Link
            href="/"
            className="text-white/70 hover:text-white transition-colors"
          >
            Champions
          </Link>
          <Link
            href="/"
            className="text-white/70 hover:text-white transition-colors"
          >
            Leaderboards
          </Link>
        </div>

        {/* Auth Buttons */}
        <div className="flex items-center gap-4">
          <button className="text-white/70 hover:text-white px-4 py-2 transition-all active:scale-95">
            Login
          </button>
          <button className="jungle-gradient text-on-primary-container font-bold px-6 py-2 rounded-lg transition-all active:scale-95 shadow-lg shadow-primary/10">
            Sign Up
          </button>
        </div>
      </div>
    </nav>
  );
}
