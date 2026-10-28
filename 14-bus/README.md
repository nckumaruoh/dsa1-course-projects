# BusBuddy: Route and Timetable Planner

City bus apps answer 'when is the next bus?' and help operators plan trips and depots along a corridor.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Sorted departures per stop; passenger counts; trip windows; depot candidates.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Busiest boarding stop | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Routes through the grid | 4 | Week 4 / CO2 | `W04CatalogueReference.paths` |
| Passengers on board | 5 | Week 5 / CO3 | `W05CatalogueReference.apply` |
| Most trips for one bus | 6 | Week 6 / CO3 | `W06CatalogueReference.maxSlots` |
| Depot spacing | 9 | Week 8 / CO4 | `Week9ProjectReference.max_min_gap` |
| Operating hours | 10 | Week 9 / CO5 | `Week10ProjectReference.mergeIntervals` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Busiest boarding stop

Boardings per stop with a count array. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [5, 2, 5, 9, 2, 5, 1]
Verified expected: most frequent=5 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{5,2,5,9,2,5,1})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Counting each distinct ID gives {1: 1, 2: 2, 5: 3, 9: 1}; a frequency tie chooses the smaller ID.

**Algorithm:** Count all occurrences, then retain the highest-frequency ID, choosing the smaller numeric ID on ties. The hash implementation supports sparse IDs; frequentCountArray supplies the explicit bounded-universe comparison.

**Invariant and cost:** Processed counts are exact. The winning count/ID pair is updated whenever its ordering improves. Expected O(n) hashing time and O(d) entries; the count-array alternative takes O(n+U) time and O(U) space, including initialization.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Routes through the grid

Count monotone routes avoiding closed roads (memo DP). Start top-left, finish bottom-right, move right/down only. Hash cells are blocked and dot cells are free. Count routes; blocked start/end means zero.

**Contract:** `long paths(String[] grid)`

**Input limits and conventions:** Rectangular grid1..30 by1..30, only dot/free or hash/blocked. Right/down moves only; blocked start/end returns0. Use checked long counts and row tabulation (or a memo with stated frame depth); preserve input.

### Worked example

```text
grid (# = closed)
Input: ["....", ".#..", "...#", "#..."]
Verified expected: 3
```

**Executable call:**

```java
W04CatalogueReference.paths(new String[]{"....",".#..","...#","#..."})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Enumerating right/down paths while rejecting blocked cells leaves 3 valid paths to the bottom-right cell.

**Algorithm:** Scan grid rows with a one-dimensional DP array. A blocked cell becomes zero; an open cell receives paths from above plus the current row’s left neighbour. The initial one at column zero represents the empty start path.

**Invariant and cost:** After a cell is processed, its DP slot counts exactly the right/down paths to that cell. O(RC) time and O(C) extra space. A blocked start or destination yields zero; checked addition protects the bounded long result.

**Additional cases:** ["...",".#.","..."] → 2; ["#"] → 0.

## 3. Passengers on board

Boardings/alightings along the route via a difference array. Start n zero cells. Each [l,r,delta] changes zero-based inclusive l..r. Return final values; signed deltas allowed.

**Contract:** `long[] apply(int n,int[][] updates)`

**Input limits and conventions:** 0<=n<=1000;0..1000 updates [l,r,delta], zero-based inclusive0<=l<=r<n; delta is signed int. n0 allows no updates. Use long difference/reconstruction and checked arithmetic; preserve updates.

### Worked example

```text
stops 0..5, groups [board, alight−1, count]
Input: 6, [[0, 2, 12], [1, 4, 8], [3, 5, 15]]
Verified expected: [12, 20, 20, 23, 23, 15]
```

**Executable call:**

```java
W05CatalogueReference.apply(6,new int[][]{new int[]{0,2,12},new int[]{1,4,8},new int[]{3,5,15}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** For each position, add every covering inclusive update; the resulting position values are [12, 20, 20, 23, 23, 15].

**Algorithm:** For each inclusive update [l,r,delta], add delta at difference[l] and subtract it at difference[r+1]. A single prefix pass recovers the final value at every position.

**Invariant and cost:** The running difference prefix equals the sum of all updates covering the current position. O(n+u) time and O(n) additional/output storage for u updates. The extra difference slot makes an update ending at n-1 safe.

**Additional cases:** n=3,updates=[[0,1,2],[1,2,-1]] → [2,1,-1]; n=0,updates=[] → [].

## 4. Most trips for one bus

Activity selection over trip windows. Intervals are half-open [start,end) with start<end. Choose as many nonoverlapping intervals as possible; touching endpoints are compatible.

**Contract:** `int maxSlots(int[][] intervals)`

**Input limits and conventions:** Nonnull0..1000 valid int[2] intervals,start<end. Half-open[start,end);touching endpoints compatible. Maximum cardinality,not weighted reward/minimum rooms. Sort a copied structure with Integer.compare/comparingInt (no subtract comparator). Empty returns0;preserve source.

### Worked example

```text
trip windows
Input: [[0, 40], [30, 70], [45, 90], [95, 130], [70, 100]]
Verified expected: 3
```

**Executable call:**

```java
W06CatalogueReference.maxSlots(new int[][]{new int[]{0,40},new int[]{30,70},new int[]{45,90},new int[]{95,130},new int[]{70,100}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** An exhaustive compatible subset is [[0, 40], [45, 90], [95, 130]]; touching end/start times are compatible. No larger subset is compatible.

**Algorithm:** Copy and sort valid nonempty intervals by finishing time. Accept an interval whenever its start is at least the last accepted end. Choosing an earliest-finishing compatible interval leaves at least as much room for later choices.

**Invariant and cost:** An exchange argument replaces the first interval of any optimum with this greedy choice without reducing the remaining choices. O(n log n) time and O(n) copied/sort storage. Touching end/start times are compatible here; this is the scheduling contract, not the closed-interval union contract.

**Additional cases:** [[1,3],[3,5]] → 2; [] → 0.

## 5. Depot spacing

Place 3 depots on candidate markers maximising the minimum gap. Place the requested number of depots at distinct candidate coordinates, maximising the minimum pairwise gap. Each candidate hosts at most one depot; at least two depots and no more than the candidate count. Coordinates are nonnegative and distinct.

**Contract:** `long max_min_gap(int[] positions,int depots)`

**Input limits and conventions:** Nonnull2..100000 DISTINCT coordinates0..10^9;2<=requested<=n. Sort copy;one depot per candidate;return maximum minimum gap as long. Invalid count,duplicate/out-of-range coordinates rejected;preserve input.

### Worked example

```text
candidate km markers, 3 depots
Input: [0, 4, 9, 15, 22, 30], 3
Expected output (independently verified): 15
```

**Executable call:**

```java
Week9ProjectReference.max_min_gap(new int[]{0,4,9,15,22,30},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Enumerating all placements gives a minimum-gap optimum of 15, witnessed by positions (0, 15, 30).

**Algorithm:** Sort a copy of distinct positions. For a candidate minimum gap, greedily place the first depot at the first position and each later depot at the earliest position far enough away. Binary-search the largest feasible gap.

**Invariant and cost:** Earlier placements leave at least as much room for later ones. Feasibility is nonincreasing with gap. O(n log n+n log(D+1)) time and O(n) copied storage for span D. Reject duplicate positions and invalid depot counts.

**Additional cases:** [0,4,9,15,22,30],3 →15; [0,30],2 →30; invalid depot count → IllegalArgumentException.

## 6. Operating hours

Merge overlapping service windows into operating hours. Each pair is a closed interval [start,end], start≤end. Merge when the next start≤current end, including shared endpoints. Return intervals by start, preserving exactly the covered union. Empty input returns empty; reversed intervals rejected. Treat the source sample as one room/resource. For multiple rooms, group by room ID first and never merge different rooms.

**Contract:** `int[][] mergeIntervals(int[][] intervals)`

**Input limits and conventions:** Nonnull n<=100000 valid CLOSED pairs;start<=end. Touching merges. Preserve rows;group multiple rooms first with mergeByRoom;never merge different rooms.

### Worked example

```text
service windows
Input: [[6, 10], [9, 12], [14, 18], [17, 21], [12, 13]]
Expected output, independently verified: [[6, 13], [14, 21]]
```

**Executable call:**

```java
Week10ProjectReference.mergeIntervals(new int[][]{new int[]{6,10},new int[]{9,12},new int[]{14,18},new int[]{17,21},new int[]{12,13}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Repeatedly uniting intersecting closed intervals, including touching endpoints, produces [[6, 13], [14, 21]]; gaps remain separate.

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

timetable planning; depot placement; route-window consolidation. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
