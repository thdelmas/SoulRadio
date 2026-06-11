# SoulRadio static site

The public landing page and **privacy policy** for SoulRadio. Plain static HTML —
no build step, no dependencies. Styled to the ecosystem design system (Broadcast
gold `#D4AF37` on black, dark mode).

- `index.html` — landing page (mirrors the store full description).
- `privacy.html` — privacy policy (the canonical hosted copy of [`../docs/PRIVACY.md`](../docs/PRIVACY.md)).
- `style.css` — shared styles.

## Why this exists

Google Play's Data Safety form requires a **publicly reachable** privacy-policy URL.
A markdown file in the repo isn't enough; this site is that public copy.

## Deploy (GitHub Pages — free)

A workflow at [`../.github/workflows/pages.yml`](../.github/workflows/pages.yml)
deploys `site/` on every push to `main` that touches it. To turn it on once:

1. Repo **Settings → Pages → Build and deployment → Source: GitHub Actions**.
2. Push to `main` (or run the workflow manually). The site publishes at:
   - Landing: `https://thdelmas.github.io/SoulRadio/`
   - **Privacy (use this in the Play listing):** `https://thdelmas.github.io/SoulRadio/privacy.html`

A custom domain (e.g. `soulradio.theophile.world`) can be set later in
Settings → Pages → Custom domain; the privacy URL in the listing should then be
updated to match.

## Any other free host

The files are provider-agnostic — drop the `site/` folder on Netlify, Cloudflare
Pages, or Vercel as a static site (no build command, publish directory `site`).

## Keeping privacy in sync

`privacy.html` and [`../docs/PRIVACY.md`](../docs/PRIVACY.md) must carry the same
text and **effective date**. If one changes, change both.
