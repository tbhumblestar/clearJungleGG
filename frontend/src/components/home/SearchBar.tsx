"use client";

import { useState, useRef, useEffect } from "react";
import type { Champion } from "@/types/champion";
import SearchDropdown from "./SearchDropdown";

export default function SearchBar({
  champions,
}: {
  champions: Champion[];
}) {
  const [query, setQuery] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const results =
    query.trim().length > 0
      ? champions.filter((c) =>
          c.championNameKo.toLowerCase().includes(query.trim().toLowerCase())
        )
      : [];

  // 외부 클릭 시 닫기
  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (
        containerRef.current &&
        !containerRef.current.contains(e.target as Node)
      ) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div ref={containerRef} className="relative w-full max-w-2xl mx-auto">
      <div className="relative">
        <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant text-xl">
          search
        </span>
        <input
          type="text"
          placeholder="영상을 보고 싶은 챔피언을 입력해 주세요."
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            setIsOpen(true);
          }}
          onFocus={() => {
            if (query.trim().length > 0) setIsOpen(true);
          }}
          className="w-full bg-surface-container border border-white/10 rounded-xl py-3.5 pl-12 pr-4 text-on-surface placeholder:text-on-surface-variant/50 outline-none focus:border-primary/50 focus:shadow-[0_0_0_2px_rgba(16,185,129,0.1)] transition-all font-body"
        />
      </div>

      {isOpen && results.length > 0 && <SearchDropdown results={results} />}
    </div>
  );
}
