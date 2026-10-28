# Audit of all 30 DSA-1 projects

Audited on 28 October 2026. Each project was checked in catalogue order. All 30 documented PowerShell commands, all 30 shell commands and both platforms’ manual demo commands passed from isolated copies. All 180 public examples agreed with independent calculations. All 30 live website destinations opened the matching GitHub folder and README title.

## Issues found and fixed

- **Output encoding:** CampusBite and DashRoute produced corrupted punctuation under the original Windows commands. All 60 run scripts now explicitly select UTF-8. Manual JVM flags are quoted so they parse correctly in PowerShell as well as sh.
- **Incomplete explanations:** Replaced 174 placeholder or task-prompt explanation blocks across Projects 2–30 with the actual algorithm, invariant, time/space costs and an independently calculated example witness. Removed references to inaccessible private teaching packages. Checked CampusBite’s six existing worked explanations and corrected the zero-capacity cost notation.
- **Cache execution cost:** FlashCart, TuneLoop and DriveDiet previously enabled an O(C) structural scan after every request. Their normal simulate path now uses the expected-linear replay; explicit debug replay retains integrity checks. Added a public-API regression at the 200000-request limit with capacity 3000.
- **Audit coverage:** The previous root verifier only compiled and ran tests. It now runs the documented script, checks the actual demo output, runs the manual command, and independently verifies all six examples. Each copy starts without build output and has spaces in its path.

## One-by-one results

| # | Project | PowerShell | sh | Examples | Explanations | Website folder |
|---:|---|---|---|---|---|---|
| 1 | [CampusBite: Canteen Order Analytics](01-campusbite/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/01-campusbite) |
| 2 | [MetroPulse: Smart-Card Ridership](02-metro/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/02-metro) |
| 3 | [ScoreLens: Ball-by-Ball Cricket Analyzer](03-cricket/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/03-cricket) |
| 4 | [DashRoute: Food-Delivery Rider Batching](04-delivery/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/04-delivery) |
| 5 | [ShelfSense: Library Circulation](05-library/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/05-library) |
| 6 | [MessVoice: Hostel Feedback Analyzer](06-mess/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/06-mess) |
| 7 | [StepUp: Fitness Tracker Coach](07-fitness/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/07-fitness) |
| 8 | [ParkSmart: Mall Parking Occupancy](08-parking/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/08-parking) |
| 9 | [AirWatch: City AQI Monitor](09-aqi/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/09-aqi) |
| 10 | [FlashCart: Flash-Sale Inventory](10-flashsale/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/10-flashsale) |
| 11 | [ExamDesk: Results and Evaluation](11-exams/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/11-exams) |
| 12 | [TuneLoop: Music Playlist Engine](12-playlist/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/12-playlist) |
| 13 | [FairFare: Ride-Share Surge Analyzer](13-surge/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/13-surge) |
| 14 | [BusBuddy: Route and Timetable Planner](14-bus/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/14-bus) |
| 15 | [CareQueue: Hospital OPD Tokens](15-opd/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/15-opd) |
| 16 | [TickerTrail: Stock Watchlist Analytics](16-stocks/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/16-stocks) |
| 17 | [TrendTag: Social Hashtag Trends](17-hashtags/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/17-hashtags) |
| 18 | [TypeRight: Spell Checker and Autocomplete](18-spell/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/18-spell) |
| 19 | [SeatSure: Event Ticket Booking](19-tickets/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/19-tickets) |
| 20 | [WattWise: Smart Electricity Billing](20-meter/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/20-meter) |
| 21 | [CodeArena: Mini Online Judge](21-judge/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/21-judge) |
| 22 | [DriveDiet: Cloud Storage Cleaner](22-cloud/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/22-cloud) |
| 23 | [KisanRate: Mandi Crop-Price Tracker](23-mandi/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/23-mandi) |
| 24 | [LifeLink: Blood Bank Inventory](24-bloodbank/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/24-bloodbank) |
| 25 | [SafeChat: Message Moderation](25-moderation/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/25-moderation) |
| 26 | [PackRight: Warehouse Order Packing](26-warehouse/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/26-warehouse) |
| 27 | [PlaceMe: Campus Placement Scheduler](27-placement/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/27-placement) |
| 28 | [NoteNest: Text Editor Core](28-editor/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/28-editor) |
| 29 | [SafePay: UPI Fraud Monitor](29-fraud/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/29-fraud) |
| 30 | [RainCatch: Rooftop Water Harvesting](30-harvest/README.md) | PASS | PASS | 6/6 | 6 reviewed | [Correct](https://github.com/nckumaruoh/dsa1-course-projects/tree/codex/course-projects/30-harvest) |

## Reproduce the audit

With Python 3.10+ and JDK 17+ on PATH, run:

```text
python verify_all.py
python verify_all.py --shell
python verify_all.py --project canteen
python audit_examples.py
```

The project applications themselves still require only a JDK. Python is needed only for the course-wide independent audit. The verifier chooses PowerShell on Windows and sh elsewhere. On Windows, --shell can use Git for Windows sh. Native Java 17 Linux and Windows checks are defined in the [CI workflow](.github/workflows/java.yml); see its [live results](https://github.com/nckumaruoh/dsa1-course-projects/actions/workflows/java.yml).

## Evidence and limits

[audit/results.json](audit/results.json) contains the per-project results, actual example values, independent witnesses and hashes of the reviewed code/scripts/documentation. [audit/website-links.json](audit/website-links.json) records every live destination. The local audit used JDK 21 with --release 17 on Windows and exercised both PowerShell and Git for Windows sh; CI additionally exercises Java 17 on native Linux and Windows. No native macOS execution is claimed.

Independent fixture checks use exhaustive subsets/partitions, direct interval scans, a full edit table, enumerated subsequences, list-based LRU simulation and minute-by-minute queue simulation. They do not call the Java methods being checked. The algorithm-family regression suites also run for every relevant project. Their shared assertion counts are not counts of unique projects or unique coverage.

This audit covers the six specified educational increments, the supplied examples, regression/boundary tests, execution instructions, explanations and website-to-folder mapping. The projects remain console demonstrations, with the documented synthetic input limits. The weekly student labs were not changed and remain solution-free. Website URLs required no correction.
