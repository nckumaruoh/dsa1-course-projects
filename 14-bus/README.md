# BusBuddy: Route and Timetable Planner

City bus apps answer 'when is the next bus?' and help operators plan trips and depots along a corridor.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

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

Boardings per stop with a count array. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. Empty input has no most-frequent ID and is outside this report contract.

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

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

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

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Base start=1 only when free; each open cell sums top and left. Memo state is (row,col).

**Invariant and cost:** O(RC) time and O(RC) memo or O(C) iterative row storage. Explain your counted operation, state, base/undo rule and boundary convention.

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

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Allocate n+1 changes; add at l and subtract at r+1, then reconstruct n positions.

**Invariant and cost:** O(n+u) time and O(n) storage including initialisation. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

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

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Sort a copy by finish time, then accept when startâ‰¥lastFinish. Provide an exchange argument; this catalogue extension is guided.

**Invariant and cost:** O(n log n) time/O(n) copy; O(1) scan state. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

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

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Sort a copy, greedily place at earliest coordinate respecting gap; search last feasible gap. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

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

timetable planning; depot placement; route-window consolidation. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
