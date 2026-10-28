# PlaceMe: Campus Placement Scheduler

Placement cells schedule company slots, rooms and interviews across a busy week, while students juggle overlapping drives.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

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

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The repeated encounters, in arrival order, are ['2201', '2202']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

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

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Enumerating one-use index subsets and deduplicating equal value combinations gives [(15, 30, 30, 45), (15, 45, 60), (30, 30, 60), (30, 90)].

**Algorithm:** Sort a copy and recursively choose later indices only. Skip equal values at the same search depth to avoid duplicate value combinations; append a copied path whenever the remaining target reaches zero, then undo each choice.

**Invariant and cost:** The path uses increasing indices and sums to originalTarget-remaining. With n<=20, worst-case search/output-sensitive time is O(n·2^n), plus sorting; workspace is O(n) excluding the returned combinations, which may contain O(n·2^n) values. Empty target returns the empty combination.

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

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** For each position, add every covering inclusive update; the resulting position values are [1, 2, 3, 2, 2, 2, 1, 1].

**Algorithm:** For each inclusive update [l,r,delta], add delta at difference[l] and subtract it at difference[r+1]. A single prefix pass recovers the final value at every position.

**Invariant and cost:** The running difference prefix equals the sum of all updates covering the current position. O(n+u) time and O(n) additional/output storage for u updates. The extra difference slot makes an update ending at n-1 safe.

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

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** An exhaustive compatible subset is [[9, 10], [10, 11], [11, 13], [13, 15]]; touching end/start times are compatible. No larger subset is compatible.

**Algorithm:** Copy and sort valid nonempty intervals by finishing time. Accept an interval whenever its start is at least the last accepted end. Choosing an earliest-finishing compatible interval leaves at least as much room for later choices.

**Invariant and cost:** An exchange argument replaces the first interval of any optimum with this greedy choice without reducing the remaining choices. O(n log n) time and O(n) copied/sort storage. Touching end/start times are compatible here; this is the scheduling contract, not the closed-interval union contract.

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

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The half-open interval [75,90) includes [75, 78, 81, 84]; duplicates are retained and the upper endpoint is excluded.

**Algorithm:** Find the first position at least lo and first at least hi; their difference counts the half-open interval [lo,hi). Equal endpoints return zero. The adapter copies/validates the source before querying.

**Invariant and cost:** Each lower-bound search preserves its partition invariant. Numeric one-call cost is O(n+log(n+1)) time and O(n) space; retained-index queries are O(log(n+1)). For words, add validation of total characters and string-comparison character costs. Duplicates count as separate entries.

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

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Repeatedly uniting intersecting closed intervals, including touching endpoints, produces [[9, 12], [13, 17], [18, 19]]; gaps remain separate.

**Algorithm:** Copy and sort closed intervals by start. Keep a current covered interval; extend its end when the next start is at most that end, otherwise emit it and start a separate interval.

**Invariant and cost:** The output prefix is exactly the union of processed intervals in sorted disjoint form. O(n log n) time and O(n) copy/sort/result storage. Touching endpoints merge. Different rooms/resources must be grouped first, using mergeByRoom.

**Additional cases:** [[1,2],[2,3]] →[[1,3]]; [[1,4],[2,3]] →[[1,4]]; [] →[]; [[3,1]] →IllegalArgumentException.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

synthetic interview scheduling; slot allocation; candidate-ID deduplication. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
