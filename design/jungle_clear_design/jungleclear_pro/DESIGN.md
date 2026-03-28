# Tactical Pulse Design System

### 1. Overview & Creative North Star
**Creative North Star: The Precision Tactician**
Tactical Pulse is a design system engineered for high-performance competitive environments. It eschews the softness of consumer-grade web design for a high-contrast, technical aesthetic inspired by military HUDs and professional gaming interfaces. The system utilizes aggressive typography, "Abyss" depth levels, and high-energy neon accents to create an atmosphere of urgency and precision.

It breaks the "template" look through:
*   **Intentional Asymmetry:** Utilizing slanted (italic) headlines and off-grid element placement.
*   **Aggressive Typography:** Massive display scales contrasted with tiny, high-density metadata.
*   **Chromostereopsis:** Using high-vibrancy greens against deep blacks to create a sense of vibrating depth.

### 2. Colors
Tactical Pulse relies on a "Vibrant Dark" palette where the primary green (#10B981) acts as a high-visibility signal against a void of deep navy and charcoal.

*   **Primary (Pulse Green):** Reserved for active state, success metrics, and primary calls to action. It represents movement and life.
*   **Surface Hierarchy:**
    *   **Surface (Abyss):** #0A0E17 — The foundation.
    *   **Surface Container (Core):** #141A26 — The standard card background.
    *   **Surface Container High:** #1A1F2C — The hover state or active selection.
*   **The "No-Line" Rule:** Explicitly prohibit 1px solid borders for sectioning. Structural definition must be achieved through the transition from `Surface` to `Surface Container`.
*   **Glassmorphism:** Navigation and floating overlays should use `rgba(20, 26, 38, 0.8)` with a 20px backdrop blur to maintain context of the underlying "battlefield."

### 3. Typography
The system uses a dual-font approach to balance technical precision with readability.
*   **Headline Font (Space Grotesk):** A geometric sans-serif with wide apertures. Used in Bold and Italic weights to create a sense of forward momentum.
*   **Body Font (Manrope):** A modern sans-serif optimized for legibility in dense data sets.
*   **Typography Scale (Extracted Ground Truth):**
    *   **Display / Hero:** 3.75rem (60px) to 5.5rem (88px) — Bold, Italic, Uppercase.
    *   **Section Headers:** 2.25rem (36px) or 1.875rem (30px).
    *   **Subheaders / Titles:** 1.25rem (20px) to 1.5rem (24px).
    *   **Body Text:** 0.875rem (14px) to 1.125rem (18px).
    *   **Technical Label:** 10px — Uppercase, tracking-widest (0.2em), used for metadata and eyebrow tags.

### 4. Elevation & Depth
Depth is not communicated through physical distance (shadows) alone, but through "Luminous Layering."

*   **The Layering Principle:** Use the surface tiering (Abyss -> Core -> High) to stack information. No more than three levels of nesting are permitted.
*   **Ambient Shadows:** Use `0 0 25px rgba(16, 185, 129, 0.12)` for hovered interactive elements. This creates a "glow" effect rather than a traditional drop shadow.
*   **Tonal Borders:** If a separator is required, use `white/5` (5% white) to create a subtle "ghost border" that suggests structure without adding visual noise.

### 5. Components
*   **Buttons:** Primary actions use the `Jungle Gradient` (#10B981 to #0DA573). They are sharp-edged (4px radius) and scale down on click (95%).
*   **Tactical Search:** A full-width autocomplete layer. The input should be oversized (text-lg) with a 20px blur background.
*   **Data Cards:** Cards must feature a high-density layout. Use Monospace font for numerical data (Win Rates, Timers) to ensure alignment and technical feel.
*   **Status Badges:** Small (10px), uppercase, italic. They should look like stamped military designations.
*   **Pathing Timelines:** Use a 2px horizontal rail with "Pulse Green" indicating progress and `white/10` for pending steps.

### 6. Do's and Don'ts
*   **Do:** Use 0.2em letter spacing for any text under 12px.
*   **Do:** Use the 5-column grid for champion or unit cards to maximize density.
*   **Don't:** Use rounded corners larger than 12px (except for icons). The system must feel "engineered," not "organic."
*   **Don't:** Use pure white for secondary text. Always use `on-surface-variant` (#A7ABB7) to maintain the dark-room UI ergonomics.
*   **Don't:** Use standard blue for links. Links should be subtle white with a primary-green hover state.