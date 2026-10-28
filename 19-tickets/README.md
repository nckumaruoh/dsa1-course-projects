# SeatSure: Event Ticket Booking

Concert and train bookings must prevent double-selling a seat across overlapping segments, under heavy demand.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Bookings [from, to) on segments; group sizes; price tiers.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Double bookings | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Fill a row exactly | 4 | Week 4 / CO2 | `W04CatalogueReference.combinations` |
| Segment occupancy | 5 | Week 5 / CO3 | `W05CatalogueReference.apply` |
| Most shows in one venue | 6 | Week 6 / CO3 | `W06CatalogueReference.maxSlots` |
| Price tier | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Merge holds | 10 | Week 9 / CO5 | `Week10ProjectReference.mergeIntervals` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Double bookings

Seat ids booked twice (count array). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [14, 15, 16, 15, 17, 14]
Verified expected: repeats=2, distinct=4, first repeat=15
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"14","15","16","15","17","14"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The repeated encounters, in arrival order, are ['15', '14']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Fill a row exactly

List distinct groups of bookings that exactly fill a 10-seat row (backtracking). Each index at most once; return distinct value combinations reaching target; different orders are the same answer.

**Contract:** `List<List<Integer>> combinations(int[] values,int target)`

**Input limits and conventions:** At most20 one-use positive values1..5000; target0..5000. Equal values may occupy separate indices, but deduplicate equal VALUE combinations. Ignore combination presentation order, preserve value multiplicity, copy each path; input must be preserved.

### Worked example

```text
group sizes, row 10
Input: [2, 3, 5, 5, 7, 8], 10
Verified expected: [[2, 3, 5], [2, 8], [3, 7], [5, 5]]
```

**Executable call:**

```java
W04CatalogueReference.combinations(new int[]{2,3,5,5,7,8},10)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Enumerating one-use index subsets and deduplicating equal value combinations gives [(2, 3, 5), (2, 8), (3, 7), (5, 5)].

**Algorithm:** Sort a copy and recursively choose later indices only. Skip equal values at the same search depth to avoid duplicate value combinations; append a copied path whenever the remaining target reaches zero, then undo each choice.

**Invariant and cost:** The path uses increasing indices and sums to originalTarget-remaining. With n<=20, worst-case search/output-sensitive time is O(n·2^n), plus sorting; workspace is O(n) excluding the returned combinations, which may contain O(n·2^n) values. Empty target returns the empty combination.

**Additional cases:** [2,2],target=4 → [[2,2]]; [2],target=1 → [].

## 3. Segment occupancy

Rebuild occupancy from bookings with a difference array. Start n zero cells. Each [l,r,delta] changes zero-based inclusive l..r. Return final values; signed deltas allowed.

**Contract:** `long[] apply(int n,int[][] updates)`

**Input limits and conventions:** 0<=n<=1000;0..1000 updates [l,r,delta], zero-based inclusive0<=l<=r<n; delta is signed int. n0 allows no updates. Use long difference/reconstruction and checked arithmetic; preserve updates.

### Worked example

```text
segments 0..6, bookings [from, to−1, seats]
Input: 7, [[0, 6, 3], [1, 3, 2], [2, 5, 1], [5, 6, 1]]
Verified expected: [3, 5, 6, 6, 4, 5, 4]
```

**Executable call:**

```java
W05CatalogueReference.apply(7,new int[][]{new int[]{0,6,3},new int[]{1,3,2},new int[]{2,5,1},new int[]{5,6,1}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** For each position, add every covering inclusive update; the resulting position values are [3, 5, 6, 6, 4, 5, 4].

**Algorithm:** For each inclusive update [l,r,delta], add delta at difference[l] and subtract it at difference[r+1]. A single prefix pass recovers the final value at every position.

**Invariant and cost:** The running difference prefix equals the sum of all updates covering the current position. O(n+u) time and O(n) additional/output storage for u updates. The extra difference slot makes an update ending at n-1 safe.

**Additional cases:** n=3,updates=[[0,1,2],[1,2,-1]] → [2,1,-1]; n=0,updates=[] → [].

## 4. Most shows in one venue

Activity selection over show slots. Intervals are half-open [start,end) with start<end. Choose as many nonoverlapping intervals as possible; touching endpoints are compatible.

**Contract:** `int maxSlots(int[][] intervals)`

**Input limits and conventions:** Nonnull0..1000 valid int[2] intervals,start<end. Half-open[start,end);touching endpoints compatible. Maximum cardinality,not weighted reward/minimum rooms. Sort a copied structure with Integer.compare/comparingInt (no subtract comparator). Empty returns0;preserve source.

### Worked example

```text
slots
Input: [[10, 12], [11, 13], [12, 14], [14, 16], [13, 17]]
Verified expected: 3
```

**Executable call:**

```java
W06CatalogueReference.maxSlots(new int[][]{new int[]{10,12},new int[]{11,13},new int[]{12,14},new int[]{14,16},new int[]{13,17}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** An exhaustive compatible subset is [[10, 12], [12, 14], [14, 16]]; touching end/start times are compatible. No larger subset is compatible.

**Algorithm:** Copy and sort valid nonempty intervals by finishing time. Accept an interval whenever its start is at least the last accepted end. Choosing an earliest-finishing compatible interval leaves at least as much room for later choices.

**Invariant and cost:** An exchange argument replaces the first interval of any optimum with this greedy choice without reducing the remaining choices. O(n log n) time and O(n) copied/sort storage. Touching end/start times are compatible here; this is the scheduling contract, not the closed-interval union contract.

**Additional cases:** [[1,3],[3,5]] → 2; [] → 0.

## 5. Price tier

Tier for a seat row with a lower bound on tier boundaries. Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
tier boundaries (rows), row 17
Input: [5, 10, 20, 30], 17
Verified expected output: 2
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{5,10,20,30},17)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** A direct left-to-right scan first finds a value at least 17 at index 2; n=4 is a valid end sentinel.

**Algorithm:** Copy and validate the sorted array, then search a half-open unclassified interval [l,r). If a[mid]<target, move l past mid; otherwise retain mid by setting r=mid. Return the meeting boundary.

**Invariant and cost:** Values before l are below target and those at/after r are at least target. A one-call adapter is O(n+log(n+1)) time and O(n) frozen-copy space. Reuse the constructed index for O(log(n+1)) numeric queries. The valid return n must not be dereferenced.

**Additional cases:** sorted=[10,10,20],target=10 → 0; target=21 → 3 (end sentinel); sorted=[],target=10 → 0.

## 6. Merge holds

Merge overlapping seat-hold periods. Each pair is a closed interval [start,end], start≤end. Merge when the next start≤current end, including shared endpoints. Return intervals by start, preserving exactly the covered union. Empty input returns empty; reversed intervals rejected. Treat the source sample as one room/resource. For multiple rooms, group by room ID first and never merge different rooms.

**Contract:** `int[][] mergeIntervals(int[][] intervals)`

**Input limits and conventions:** Nonnull n<=100000 valid CLOSED pairs;start<=end. Touching merges. Preserve rows;group multiple rooms first with mergeByRoom;never merge different rooms.

### Worked example

```text
holds
Input: [[1, 4], [2, 6], [8, 10], [9, 12], [15, 18]]
Expected output, independently verified: [[1, 6], [8, 12], [15, 18]]
```

**Executable call:**

```java
Week10ProjectReference.mergeIntervals(new int[][]{new int[]{1,4},new int[]{2,6},new int[]{8,10},new int[]{9,12},new int[]{15,18}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Repeatedly uniting intersecting closed intervals, including touching endpoints, produces [[1, 6], [8, 12], [15, 18]]; gaps remain separate.

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

seat-block reservations; event schedules; booking-window consolidation. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
