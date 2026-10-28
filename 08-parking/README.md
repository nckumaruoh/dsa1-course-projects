# ParkSmart: Mall Parking Occupancy

Malls show free-space counts at the entrance and charge by the hour. Sensors log every entry and exit.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Entries/exits with times and plates; slot grid (levels × slots) as a 2-D array.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Re-entry without exit | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Routes to a free slot | 4 | Week 4 / CO2 | `W04CatalogueReference.paths` |
| Free slots in a zone | 5 | Week 5 / CO3 | `W05CatalogueReference.rectangle` |
| EV charger bookings | 6 | Week 6 / CO3 | `W06CatalogueReference.maxSlots` |
| Fee slab lookup | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Entry gate queue | 12 | Week 12 / CO6 | `Week12ProjectReference.queueSim` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Re-entry without exit

Plates seen entering twice without an exit (count array over plate ids). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [3301, 4410, 3301, 5120, 4410, 6008]
Verified expected: repeats=2, distinct=4, first repeat=3301
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"3301","4410","3301","5120","4410","6008"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Routes to a free slot

Count right/down routes through the lot grid avoiding blocked cells (memo DP). Start top-left, finish bottom-right, move right/down only. Hash cells are blocked and dot cells are free. Count routes; blocked start/end means zero.

**Contract:** `long paths(String[] grid)`

**Input limits and conventions:** Rectangular grid1..30 by1..30, only dot/free or hash/blocked. Right/down moves only; blocked start/end returns0. Use checked long counts and row tabulation (or a memo with stated frame depth); preserve input.

### Worked example

```text
lot grid (# = blocked)
Input: ["...", ".#.", "..."]
Verified expected: 2
```

**Executable call:**

```java
W04CatalogueReference.paths(new String[]{"...",".#.","..."})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Base start=1 only when free; each open cell sums top and left. Memo state is (row,col).

**Invariant and cost:** O(RC) time and O(RC) memo or O(C) iterative row storage. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** ["...",".#.","..."] → 2; ["#"] → 0.

## 3. Free slots in a zone

2-D prefix sums over the slot grid answer 'free slots in this block' in O(1). Query zero-based inclusive rectangle [r1..r2]×[c1..c2]; return total of every cell.

**Contract:** `long rectangle(long[][] matrix,int r1,int c1,int r2,int c2)`

**Input limits and conventions:** Nonempty rectangular1..30 by1..30 grid, cells abs<=10^9. Inclusive ordered valid corners. Build a frozen GridIndex ONCE for repeated O(1) queries. Single rectangle(matrix,...) convenience method includes O(RC) build; rebuild after updates.

### Worked example

```text
grid (1 = free), rows 0..1, cols 1..2
Input: [[1, 0, 1, 1], [0, 1, 1, 0], [1, 1, 0, 1]], 0, 1, 1, 2
Verified expected: 3
```

**Executable call:**

```java
W05CatalogueReference.rectangle(new long[][]{new long[]{1L,0L,1L,1L},new long[]{0L,1L,1L,0L},new long[]{1L,1L,0L,1L}},0,1,1,2)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Build 2D prefix with an extra zero border and apply four-corner inclusion-exclusion.

**Invariant and cost:** O(RC) build/O(1) query; O(RC) index. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** [[1,2],[3,4]],(0,1,1,1) → 6; [[5]],(0,0,0,0) → 5.

## 4. EV charger bookings

Most non-overlapping bookings one charger can serve (activity selection). Intervals are half-open [start,end) with start<end. Choose as many nonoverlapping intervals as possible; touching endpoints are compatible.

**Contract:** `int maxSlots(int[][] intervals)`

**Input limits and conventions:** Nonnull0..1000 valid int[2] intervals,start<end. Half-open[start,end);touching endpoints compatible. Maximum cardinality,not weighted reward/minimum rooms. Sort a copied structure with Integer.compare/comparingInt (no subtract comparator). Empty returns0;preserve source.

### Worked example

```text
booking requests [start, end)
Input: [[1, 3], [2, 5], [4, 6], [6, 8], [5, 9], [8, 10]]
Verified expected: 4
```

**Executable call:**

```java
W06CatalogueReference.maxSlots(new int[][]{new int[]{1,3},new int[]{2,5},new int[]{4,6},new int[]{6,8},new int[]{5,9},new int[]{8,10}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Sort a copy by finish time, then accept when startâ‰¥lastFinish. Provide an exchange argument; this catalogue extension is guided.

**Invariant and cost:** O(n log n) time/O(n) copy; O(1) scan state. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

**Additional cases:** [[1,3],[3,5]] → 2; [] → 0.

## 5. Fee slab lookup

Fee slab for a stay length with a lower bound on slab boundaries. Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
slab boundaries (minutes), stay 135
Input: [60, 120, 180, 240, 360], 135
Verified expected output: 2
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{60,120,180,240,360},135)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use [lo,hi), hi=n; sorted[mid]<target advances lo to mid+1, otherwise hi=mid. Preserve sortedness precondition. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** sorted=[10,10,20],target=10 → 0; target=21 → 3 (end sentinel); sorted=[],target=10 → 0.

## 6. Entry gate queue

Cars queue at the boom barrier; compare 1 and 2 gates. Bucket t records arrivals at minute t, starting t=0. Enqueue in bucket order; simultaneous arrivals retain token order. Counters are identical and initially idle. Service is non-preemptive and takes the stated whole minutes. A counter finishing at t may take an arrival at t immediately. Use earliest available counter, lowest index on ties, and FIFO. Wait=start−arrival, excluding service duration. Drain all requests beyond the final bucket; report served count, total wait and longest wait.

**Contract:** `String queueSim(int[] arrivals,int serviceMinutes,int counters)`

**Input limits and conventions:** Nonnull T<=100000 buckets,nonnegative counts,total requests<=100000;service1..100000,counters1or2. FIFO,earliest available counter/lowest index ties;wait excludes service;drain past last bucket;long totals.

### Worked example

```text
arrivals per minute, 2-min service, 1 gate
Input: [1, 2, 2, 1, 0, 1, 0], 2, 1
Verified expected: served=7, total wait=28 min, longest wait=7 min
```

**Executable call:**

```java
Week12ProjectReference.queueSim(new int[]{1,2,2,1,0,1,0},2,1)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Implement circular-array FIFO of arrival times, then assign each dequeued request to earliest available counter at max(arrival,available). Compare one and two counters using the same arrivals/service. Use long totals and available times.

**Invariant and cost:** O(T+Nc) event-driven scheduling; T=number of arrival buckets,N=requests,c≤2, so O(T+N); O(N+c) FIFO/counter space. A minute-by-minute implementation must count the full drained horizon, not just input buckets.

**Additional cases:** One counter: served=7,totalWait=28,maxWait=7; two counters: served=7,totalWait=7,maxWait=2. Empty arrivals →served0,totalWait0,maxWait0. Invalid service/counter count rejected.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

parking occupancy dashboards; booking time windows; entrance queue planning. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
