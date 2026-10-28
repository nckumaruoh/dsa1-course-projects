# DSA-1: 30 independent Java projects

Completed implementations of the six required catalogue increments for each project, with worked examples, explanations, execution instructions, similar applications and regression tests. Each folder runs independently using JDK 17 or later; no external libraries, credentials or services are required. All data is synthetic.

## Start here

Choose a project below, open its folder and follow its README. On Windows run `./run.ps1`; on Linux/macOS run `sh run.sh`. Each script compiles, checks the project and runs its integrated demonstration. The README explains the callable methods so that you can supply different data.

To verify the entire repository with Python 3.10+ and a JDK on PATH: `python verify_all.py`. This runs each documented script from a fresh temporary copy, compares its actual demo output and independently checks all six examples. Use `python verify_all.py --shell` to explicitly exercise `sh`, or `python verify_all.py --project canteen` for one project. The CI workflow runs the audit on Java 17 on Linux and Windows. Each project itself still needs only a JDK.

See [AUDIT_REPORT.md](AUDIT_REPORT.md) for the one-by-one results, explanation review, execution fixes and verified website destinations. Each folder contains actual `test-results.txt` and `example-output.txt` from its completed local run.

## Project catalogue

| # | Project | Local verification | GitHub | Website |
|---:|---|---|---|---|
| 1 | [CampusBite: Canteen Order Analytics](01-campusbite/README.md) | Six increments verified | Published | Linked |
| 2 | [MetroPulse: Smart-Card Ridership](02-metro/README.md) | Six increments verified | Published | Linked |
| 3 | [ScoreLens: Ball-by-Ball Cricket Analyzer](03-cricket/README.md) | Six increments verified | Published | Linked |
| 4 | [DashRoute: Food-Delivery Rider Batching](04-delivery/README.md) | Six increments verified | Published | Linked |
| 5 | [ShelfSense: Library Circulation](05-library/README.md) | Six increments verified | Published | Linked |
| 6 | [MessVoice: Hostel Feedback Analyzer](06-mess/README.md) | Six increments verified | Published | Linked |
| 7 | [StepUp: Fitness Tracker Coach](07-fitness/README.md) | Six increments verified | Published | Linked |
| 8 | [ParkSmart: Mall Parking Occupancy](08-parking/README.md) | Six increments verified | Published | Linked |
| 9 | [AirWatch: City AQI Monitor](09-aqi/README.md) | Six increments verified | Published | Linked |
| 10 | [FlashCart: Flash-Sale Inventory](10-flashsale/README.md) | Six increments verified | Published | Linked |
| 11 | [ExamDesk: Results and Evaluation](11-exams/README.md) | Six increments verified | Published | Linked |
| 12 | [TuneLoop: Music Playlist Engine](12-playlist/README.md) | Six increments verified | Published | Linked |
| 13 | [FairFare: Ride-Share Surge Analyzer](13-surge/README.md) | Six increments verified | Published | Linked |
| 14 | [BusBuddy: Route and Timetable Planner](14-bus/README.md) | Six increments verified | Published | Linked |
| 15 | [CareQueue: Hospital OPD Tokens](15-opd/README.md) | Six increments verified | Published | Linked |
| 16 | [TickerTrail: Stock Watchlist Analytics](16-stocks/README.md) | Six increments verified | Published | Linked |
| 17 | [TrendTag: Social Hashtag Trends](17-hashtags/README.md) | Six increments verified | Published | Linked |
| 18 | [TypeRight: Spell Checker and Autocomplete](18-spell/README.md) | Six increments verified | Published | Linked |
| 19 | [SeatSure: Event Ticket Booking](19-tickets/README.md) | Six increments verified | Published | Linked |
| 20 | [WattWise: Smart Electricity Billing](20-meter/README.md) | Six increments verified | Published | Linked |
| 21 | [CodeArena: Mini Online Judge](21-judge/README.md) | Six increments verified | Published | Linked |
| 22 | [DriveDiet: Cloud Storage Cleaner](22-cloud/README.md) | Six increments verified | Published | Linked |
| 23 | [KisanRate: Mandi Crop-Price Tracker](23-mandi/README.md) | Six increments verified | Published | Linked |
| 24 | [LifeLink: Blood Bank Inventory](24-bloodbank/README.md) | Six increments verified | Published | Linked |
| 25 | [SafeChat: Message Moderation](25-moderation/README.md) | Six increments verified | Published | Linked |
| 26 | [PackRight: Warehouse Order Packing](26-warehouse/README.md) | Six increments verified | Published | Linked |
| 27 | [PlaceMe: Campus Placement Scheduler](27-placement/README.md) | Six increments verified | Published | Linked |
| 28 | [NoteNest: Text Editor Core](28-editor/README.md) | Six increments verified | Published | Linked |
| 29 | [SafePay: UPI Fraud Monitor](29-fraud/README.md) | Six increments verified | Published | Linked |
| 30 | [RainCatch: Rooftop Water Harvesting](30-harvest/README.md) | Six increments verified | Published | Linked |

## Course alignment

| CO | Teaching weeks |
|---|---|
| CO1 | Weeks 1–2 |
| CO2 | Weeks 3–4 |
| CO3 | Weeks 5–6 |
| CO4 | Weeks 7–8 |
| CO5 | Weeks 9–10 |
| CO6 | Weeks 11–12 |

The original source-week identifiers are retained for traceability. Each project maps its increments to the revised teaching weeks; it does not invent additional milestones.

## Repository checklist

Each project has nine separately tracked checks in [PROJECT_CHECKLIST.json](PROJECT_CHECKLIST.json): specification review, implementation, automated tests, worked examples, explanation and complexity, related applications, execution documentation, GitHub publication and website link. Local verification and publication are separate states.

## Materials boundary

This repository contains the project solutions explicitly requested by the course owner. Weekly student skill-lab pages remain task-only and do not import these solutions. Private assessment workbooks, student records, credentials and faculty evaluation sheets are excluded. The course website links each project separately.

## Implementation provenance and scope

The projects reuse this course's owned, reviewed Java algorithm primitives, alongside new project drivers, validation, tests and documentation. Original Java class names retain source-catalogue traceability. Shared primitive tests include additional synthetic algorithm-family fixtures; their assertion counts are not counts of unique projects or student submissions.

These are runnable educational console applications implementing the specified six increments. They are not production deployments or integrations with named commercial services. Domain descriptions and related application categories explain how the algorithms transfer to other settings.
