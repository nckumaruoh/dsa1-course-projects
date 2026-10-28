# PlaceMe: Campus Placement Scheduler

Placement cells schedule company slots, rooms and interviews across a busy week, while students juggle overlapping drives.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Company slots [start, end), registrations, CGPA (×10 as integers), round durations.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate registrations | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Round plans | 4 | Week 4 / CO2 | `W04CatalogueReference.combinations` |
| Rooms needed per hour | 5 | Week 5 / CO3 | `W05CatalogueReference.apply` |
| Most interviews for one student | 6 | Week 6 / CO3 | `W06CatalogueReference.maxSlots` |
| Eligibility cut-off | 8 | Week 7 / CO4 | `W08CatalogueReference.count` |
| Merge room bookings | 10 | Week 9 / CO5 | `Week10ProjectReference.mergeIntervals` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate registrations

Students registered twice for one drive (count array). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [2201, 2202, 2203, 2201, 2204, 2202]
Verified expected: repeats=2, distinct=4, first repeat=2201
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"2201","2202","2203","2201","2204","2202"})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Round plans

List distinct sets of interview rounds whose durations total exactly 120 minutes. Each index at most once; return distinct value combinations reaching target; different orders are the same answer.

**Contract:** `List<List<Integer>> combinations(int[] values,int target)`

**Input limits and conventions:** At most20 one-use positive values1..5000; target0..5000. Equal values may occupy separate indices, but deduplicate equal VALUE combinations. Ignore combination presentation order, preserve value multiplicity, copy each path; input must be preserved.

### Worked example

```text
round minutes, total 120
Input: [30, 45, 60, 15, 30, 90], 120
Verified expected: [[15, 30, 30, 45], [15, 45, 60], [30, 30, 60], [30, 90]]
```

**Executable call:**

```java
W04CatalogueReference.combinations(new int[]{30,45,60,15,30,90},120)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Sort a copy, skip equal siblings, recurse with i+1 and copy successful paths.

**Invariant and cost:** Safe O(n·2^n) generation/copy bound; O(n) working state excluding output. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** [2,2],target=4 → [[2,2]]; [2],target=1 → [].

## 3. Rooms needed per hour

Simultaneous slots per hour via a difference array. Start n zero cells. Each [l,r,delta] changes zero-based inclusive l..r. Return final values; signed deltas allowed.

**Contract:** `long[] apply(int n,int[][] updates)`

**Input limits and conventions:** 0<=n<=1000;0..1000 updates [l,r,delta], zero-based inclusive0<=l<=r<n; delta is signed int. n0 allows no updates. Use long difference/reconstruction and checked arithmetic; preserve updates.

### Worked example

```text
hours 9..16 as 0..7, slots [from, to−1, 1]
Input: 8, [[0, 2, 1], [1, 3, 1], [2, 5, 1], [4, 7, 1]]
Verified expected: [1, 2, 3, 2, 2, 2, 1, 1]
```

**Executable call:**

```java
W05CatalogueReference.apply(8,new int[][]{new int[]{0,2,1},new int[]{1,3,1},new int[]{2,5,1},new int[]{4,7,1}})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Allocate n+1 changes; add at l and subtract at r+1, then reconstruct n positions.

**Invariant and cost:** O(n+u) time and O(n) storage including initialisation. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** n=3,updates=[[0,1,2],[1,2,-1]] → [2,1,-1]; n=0,updates=[] → [].

## 4. Most interviews for one student

Activity selection: maximum non-overlapping interviews. Intervals are half-open [start,end) with start<end. Choose as many nonoverlapping intervals as possible; touching endpoints are compatible.

**Contract:** `int maxSlots(int[][] intervals)`

**Input limits and conventions:** Nonnull0..1000 valid int[2] intervals,start<end. Half-open[start,end);touching endpoints compatible. Maximum cardinality,not weighted reward/minimum rooms. Sort a copied structure with Integer.compare/comparingInt (no subtract comparator). Empty returns0;preserve source.

### Worked example

```text
interviews
Input: [[9, 10], [9, 12], [10, 11], [11, 13], [12, 14], [13, 15]]
Verified expected: 4
```

**Executable call:**

```java
W06CatalogueReference.maxSlots(new int[][]{new int[]{9,10},new int[]{9,12},new int[]{10,11},new int[]{11,13},new int[]{12,14},new int[]{13,15}})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Sort a copy by finish time, then accept when startâ‰¥lastFinish. Provide an exchange argument; this catalogue extension is guided.

**Invariant and cost:** O(n log n) time/O(n) copy; O(1) scan state. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

**Additional cases:** [[1,3],[3,5]] → 2; [] → 0.

## 5. Eligibility cut-off

Sorted CGPAs (×10): count students in [75, 90). Count all entries in the half-open value interval [lo,hi). Include every duplicate at lo; exclude every duplicate at hi. Equal endpoints return zero; reject lo>hi.

**Contract:** `int count(int[] sorted,int lo,int hi)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted CGPA×10, [75, 90)
Input: [62, 68, 71, 75, 78, 81, 84, 90, 93], 75, 90
Verified expected output: 4
```

**Executable call:**

```java
W08CatalogueReference.count(new int[]{62,68,71,75,78,81,84,90,93},75,90)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Compute lower(hi)−lower(lo), not a first/last matching-index scan. Both bounds may validly equal n. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** sorted=[10,10,20,30,30],range [10,30) → 3; [20,20) → 0; empty input → 0.

## 6. Merge room bookings

Merge overlapping bookings per room. Each pair is a closed interval [start,end], start≤end. Merge when the next start≤current end, including shared endpoints. Return intervals by start, preserving exactly the covered union. Empty input returns empty; reversed intervals rejected. Treat the source sample as one room/resource. For multiple rooms, group by room ID first and never merge different rooms.

**Contract:** `int[][] mergeIntervals(int[][] intervals)`

**Input limits and conventions:** Nonnull n<=100000 valid CLOSED pairs;start<=end. Touching merges. Preserve rows;group multiple rooms first with mergeByRoom;never merge different rooms.

### Worked example

```text
bookings
Input: [[9, 11], [10, 12], [13, 15], [14, 17], [18, 19]]
Expected output, independently verified: [[9, 12], [13, 17], [18, 19]]
```

**Executable call:**

```java
Week10ProjectReference.mergeIntervals(new int[][]{new int[]{9,11},new int[]{10,12},new int[]{13,15},new int[]{14,17},new int[]{18,19}})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** See the checked Java implementation and regression suite.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [[1,2],[2,3]] →[[1,3]]; [[1,4],[2,3]] →[[1,4]]; [] →[]; [[3,1]] →IllegalArgumentException.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

synthetic interview scheduling; slot allocation; candidate-ID deduplication. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
