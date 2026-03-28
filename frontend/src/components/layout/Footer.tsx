export default function Footer() {
  return (
    <footer className="border-t border-white/5 mt-auto">
      <div className="max-w-[1440px] mx-auto px-8 py-28 flex flex-col items-center gap-6">
        <span className="text-2xl font-bold tracking-tighter text-primary uppercase italic font-headline">
          JungleClear.gg
        </span>
        <p className="text-xs text-on-surface-variant/50 uppercase tracking-[0.25em] font-headline">
          Built for the fastest clear in the rift
        </p>
        <span className="text-xs text-on-surface-variant/30 tracking-widest">
          &copy; {new Date().getFullYear()} JungleClear.gg
        </span>
      </div>
    </footer>
  );
}
