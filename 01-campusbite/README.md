# CampusBite: Canteen Order Analytics

A Java 17 console application implementing all six required project increments. It detects repeated synthetic payments, chooses a budgeted combo, indexes slot revenue, locates the lunch rush, counts orders in a time interval and compares counter staffing.

## Run

Install a JDK 17 or later and ensure `java` and `javac` are on PATH. There are no libraries, accounts or network services to configure.

Windows PowerShell: `./run.ps1 -Trace`

Linux/macOS: `sh run.sh --trace`

Manual commands from this folder:

```text
mkdir build
javac --release 17 -encoding UTF-8 -d build src/CampusBite.java test/CampusBiteTest.java
java -cp build CampusBiteTest
java -cp build CampusBite --trace
```

The scripts compile, run the regression suite and execute the integrated demonstration. Compilation or test failure stops execution. The optional trace prints each token's arrival, selected counter, start, finish and wait.

## Six increments and the realigned course

Source weeks identify the original catalogue contracts; teaching weeks follow the revised two-weeks-per-CO sequence.

| Feature | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Catch double charges | 1 | Week 1 / CO1 | `repeats`, `repeatsPairwise`, `repeatsCountArray` |
| Combo builder | 4 | Week 4 / CO2 | `bestTotal` |
| Revenue between two times | 5 | Week 5 / CO3 | `RevenueIndex`, `rangeSums` |
| Find the lunch rush | 7 | Week 6 / CO3 | `bestWindow` |
| Orders in a time slot | 8 | Week 7 / CO4 | `TimeIndex`, `count` |
| Counter queue simulation | 12 | Week 12 / CO6 | `simulate`, `queueSim` |

All six features execute in one main program. The milestone methods are also callable independently with different inputs. The course's original project guide remains the source of the required contracts; this folder supplies the newly authorized worked solution.

## Worked shift

The demonstration uses synthetic IDs `[501,502,503,502,504,501,505,502]`. The repeated encounters are the second `502`, second `501` and third `502`: **3 repeats**, **5 distinct IDs**, first repeat **502**. This counts repeated encounters, rather than merely counting two IDs that repeat. It reports a possible retry; detecting a repeat does not itself prove fraud or issue a refund.

For prices `[60,45,30,80,25]` and a budget of 150, select `45+80+25=150`. Each array index can be used once. Including an item advances the index, so it cannot accidentally become an unlimited-use coin problem.

Revenue slots `[450,1200,2300,3100,2800,900,600]` produce the prefix array `[0,450,1650,3950,7050,9850,10750,11350]`. The inclusive query `[2,4]` is `prefix[5]-prefix[2]=9850-1650=8200`. The full shift totals 11350. A frozen `RevenueIndex` safely supports many queries and must be rebuilt after source data changes.

Three-slot totals are `3950,6600,8200,6800,4300`. The best window starts at index 2 and totals 8200. If slots begin at 08:00 and last ten minutes, that example covers 08:20–08:50. Change the origin to match the actual shift; an index is not a wall-clock time by itself. Ties keep the earliest start.

Sorted order times `[30,95,180,241,250,266,299,300,345]` contain four events in `[240,300)`: `241,250,266,299`. A time exactly 300 is excluded. Two lower bounds return `lower(300)-lower(240)=7-3=4`.

Arrival buckets `[1,2,1,0,2,1,0,0]` expand to FIFO arrivals `[0,1,1,2,4,4,5]`. At three minutes per service:

| Counters | Start times | Wait times | Total wait | Longest wait |
|---:|---|---|---:|---:|
| 1 | 0,3,6,9,12,15,18 | 0,2,5,7,8,11,13 | 46 | 13 |
| 2 | 0,1,3,4,6,7,9 | 0,0,2,2,2,3,4 | 13 | 4 |

Wait excludes service. Counters finishing at minute t can immediately serve an arrival at t. Counter indexes start at zero; equal availability chooses the smaller index. The simulation drains requests after the final arrival bucket.

## Why the algorithms work

| Component | Invariant / recurrence | Time | Auxiliary space |
|---|---|---|---|
| Hash-based retries | Seen contains exactly the IDs already encountered | Expected O(n) map operations, plus string character work | O(d) entries |
| Pairwise comparison | Search all earlier positions before classifying an encounter | O(n²) comparisons | O(1), excluding report |
| Count array | `counts[id]` counts encounters so far | O(n+U), including initialization | O(U) |
| Memoized combo | `best(i,b)=max(skip,price[i]+best(i+1,b-price[i]))` when affordable | O(nB) | O(nB) memo + O(n) stack |
| Prefix revenue | `prefix[i]` sums the first i slots | O(n) build, O(1) query; batch O(n+q) | O(n), plus returned q values |
| Fixed window | Running sum equals the current exactly-k-slot total | O(n) | O(1), excluding two-value result |
| Lower-bound index | Search interval retains the first value at least the target | O(n) validation/copy once, O(log n) per query | O(n) frozen copy |
| FIFO scheduling | Queue remains arrival-ordered; each counter availability is its next free time | O(T+Nc), c≤2 | O(N+c), including queue and trace |

`count(int[],lo,hi)` constructs and validates an index each call, so that convenience wrapper is O(n+log n). Reuse `TimeIndex` to obtain O(log n) per query. The same distinction applies to revenue construction versus queries. Signed slot totals work for the fixed window; its initial best is the first real window, not zero.

## Contracts, validation and limitations

- IDs: at most 100,000, nonnull strings of at most 100 characters. Numeric count arrays require declared `1≤U≤100001` and IDs in `[0,U)`.
- Combo: at most 30 one-use nonnegative values, each at most 5000; capacity 0–5000. Empty input and zero-valued items are valid. A `-1` memo sentinel distinguishes an uncomputed state from a computed result of zero.
- Revenue: at most 1000 values in ±10⁹ and 1000 inclusive queries. Empty values require no queries. Invalid shapes and indexes are rejected.
- Windows/times: at most 100,000 entries; valid k is 1–n. Times must be sorted. Empty time indexes and equal query endpoints are valid; lo>hi is rejected.
- Queue: at most 100,000 buckets and total requests; nonnegative counts, service 1–100,000, one or two counters. Long arithmetic avoids int overflow in wait and revenue totals.
- Inputs are preserved. Frozen indexes copy or derive their data. The queue report's trace is immutable.
- This is a deterministic educational console application. It does not connect to payment systems, store real personal data, implement persistent storage, or model varying service durations. All sample data is synthetic.

## Test evidence

See [test-results.txt](test-results.txt) and [example-output.txt](example-output.txt), captured from the actual Java run. The suite performs **17,654 checks** with a fixed seed, covering all published samples, empty data, duplicate endpoints, signed totals, earliest ties, source mutation, invalid inputs, 64-bit totals and a 100,000-request drain.

Independent small-input oracles enumerate subsets, directly sum ranges/windows and simulate the queue minute by minute. Randomized tests compare these against the optimized methods, rather than only repeating the implementation's logic.

## Similar applications and extensions

The same components support cafeteria token systems, retail checkout staffing, appointment waiting rooms, event admissions and repeated telemetry ingestion. Related course projects include **CareQueue** (OPD tokens), **ParkSmart** (parking), **ShelfSense** (library circulation) and **LifeLink** (blood bank inventory).

Possible extensions are a CSV importer, item-level revenue aggregation, a timestamp-to-slot adapter and variable service times. Each would need new contracts and tests; they are not claimed as implemented here.
