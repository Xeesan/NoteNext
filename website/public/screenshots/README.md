# Screenshots

Drop the 5 attached app screenshots here as PNG files with these exact filenames so the home page picks them up automatically:

| Filename | What it shows |
|---|---|
| `notes-list.png` | The notes grid view (vhgy / 305 / list / hi cards) |
| `fab-menu.png` | The "+" FAB expanded showing Note / Checklist / Drawing / Todo / Projects |
| `drawer.png` | The side drawer (Notes, Projects, Archive, Reminders, Todos, Bin, Settings, Labels) |
| `ai-settings.png` | The AI settings page with the master switch |
| `privacy-security.png` | The Privacy & Security settings page (App Lock, Decoy Vault, Disallow Screenshots) |

**Recommended size:** 1080×2400 (or whatever your phone's native resolution is). The home-page mockup renders them at ~300×650, so anything ≥ 600 wide works perfectly.

**Optimization (optional):** run them through [squoosh.app](https://squoosh.app) → MozJPEG quality 80 or WebP, then re-save with `.png` extension if you want to keep the filenames. Cloudflare Pages will gzip/brotli them automatically either way.

To change the captions or order, edit `src/consts.ts` → `SCREENSHOTS`.
