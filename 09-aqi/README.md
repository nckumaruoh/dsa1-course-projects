# AirWatch: City AQI Monitor

Pollution boards publish hourly AQI from many stations. Citizens want alerts and trends; officials want the worst stretches and areas.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Hourly AQI per station (arrays); a city grid of pollution readings.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Hours per category | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Polluted zone size | 3 | Week 3 / CO2 | `CoreBasics.region` |
| Pollution in a city block | 5 | Week 5 / CO3 | `W05CatalogueReference.rectangle` |
| Worst 3-hour window | 7 | Week 6 / CO3 | `Week7ProjectReference.bestWindow` |
| Hours in a band | 8 | Week 7 / CO4 | `W08CatalogueReference.count` |
| Hours until it gets worse | 12 | Week 12 / CO6 | `Week12ProjectReference.waitDays` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Hours per category

Count hours in each AQI category (count array over category ids); most common category. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [2, 3, 3, 4, 3, 2, 4, 3]
Verified expected: most frequent=3 (count 4), distinct=3
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{2,3,3,4,3,2,4,3})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Polluted zone size

Size of the connected polluted region on a city grid containing a given cell (flood fill). Return the number of dot cells connected to the start by four-neighbour moves; hashes block movement. A blocked start returns zero. Each cell counted once.

**Contract:** `int region(String[] grid,int startRow,int startCol)`

**Input limits and conventions:** Rectangular grid1..30 rows by1..30 columns, cells only dot or hash; start indices in range. Dot cells are traversable and connected by four-neighbour moves; hash start returns0. Use a visited structure and explicit worklist at full size; demonstrate recursion only on a tiny grid. Preserve input.

### Worked example

```text
grid ('.' polluted), start (0,0)
Input: ["..#.", ".##.", "...#", "#..."], 0, 0
Verified expected: 9
```

**Executable call:**

```java
CoreBasics.region(new String[]{"..#.",".##.","...#","#..."},0,0)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Mark before exploring. For large grids use an explicit stack/queue to avoid Java recursion overflow.

**Invariant and cost:** O(RC) time/storage in worst case. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** ["..",".#"],start(0,0) → 3; ["#"],start(0,0) → 0.

## 3. Pollution in a city block

2-D prefix sums over a grid of sensor readings; the total for any rectangular block in O(1). Query zero-based inclusive rectangle [r1..r2]×[c1..c2]; return total of every cell.

**Contract:** `long rectangle(long[][] matrix,int r1,int c1,int r2,int c2)`

**Input limits and conventions:** Nonempty rectangular1..30 by1..30 grid, cells abs<=10^9. Inclusive ordered valid corners. Build a frozen GridIndex ONCE for repeated O(1) queries. Single rectangle(matrix,...) convenience method includes O(RC) build; rebuild after updates.

### Worked example

```text
readings grid, rows 0..1, cols 1..2
Input: [[120, 145, 180, 90], [210, 260, 230, 110], [190, 150, 140, 95]], 0, 1, 1, 2
Verified expected: 815
```

**Executable call:**

```java
W05CatalogueReference.rectangle(new long[][]{new long[]{120L,145L,180L,90L},new long[]{210L,260L,230L,110L},new long[]{190L,150L,140L,95L}},0,1,1,2)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Build 2D prefix with an extra zero border and apply four-corner inclusion-exclusion.

**Invariant and cost:** O(RC) build/O(1) query; O(RC) index. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** [[1,2],[3,4]],(0,1,1,1) → 6; [[5]],(0,0,0,0) → 5.

## 4. Worst 3-hour window

Highest 3-hour total with a fixed window. Exactly k consecutive positions; return [maximum sum, earliest starting index] when tied. Require 1≤k≤n.

**Contract:** `long[] bestWindow(int[] values,int k)`

**Input limits and conventions:** Nonnull signed int array1..100000;1<=k<=n. Return long[2]={maximum sum,EARLIEST zero-based start};all-negative windows valid. Preserve source;checked long addition/subtraction;fixed width only.

### Worked example

```text
AQI per hour, k=3
Input: [120, 145, 180, 210, 260, 230, 190, 150], 3
Verified expected: "sum=700 starting at index 3"
```

**Executable call:**

```java
Week7ProjectReference.bestWindow(new int[]{120,145,180,210,260,230,190,150},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Keep one long sum; add entering and subtract departing value; compare only complete windows. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [1,2,3],k=2 → [5,1]; [5],k=1 → [5,0].

## 5. Hours in a band

Sorted readings: hours with AQI in [200, 300) via two lower bounds. Count all entries in the half-open value interval [lo,hi). Include every duplicate at lo; exclude every duplicate at hi. Equal endpoints return zero; reject lo>hi.

**Contract:** `int count(int[] sorted,int lo,int hi)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted AQI, [200, 300)
Input: [120, 145, 150, 180, 190, 210, 230, 260, 310], 200, 300
Verified expected output: 3
```

**Executable call:**

```java
W08CatalogueReference.count(new int[]{120,145,150,180,190,210,230,260,310},200,300)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Compute lower(hi)−lower(lo), not a first/last matching-index scan. Both bounds may validly equal n. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** sorted=[10,10,20,30,30],range [10,30) → 3; [20,20) → 0; empty input → 0.

## 6. Hours until it gets worse

For each hour, hours until a higher AQI (monotonic stack). For each position i, return j−i for the earliest later j with values[j]>values[i], or0 if absent. Non-circular; equal values do not resolve a wait.

**Contract:** `int[] waitDays(int[] values)`

**Input limits and conventions:** Nonnull n0..100000 int observations;first later STRICTLY greater,non-circular;equal unresolved;0 absent. Preserve input.

### Worked example

```text
AQI
Input: [120, 145, 130, 110, 160, 150, 170]
Verified expected: [1, 3, 2, 1, 2, 1, 0]
```

**Executable call:**

```java
Week12ProjectReference.waitDays(new int[]{120,145,130,110,160,150,170})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Stack unresolved indices, popping only on a strictly larger reading and recording index difference.

**Invariant and cost:** O(n) time/O(n) stack and output; each index pushed/popped once.

**Additional cases:** [5,5,5] →[0,0,0]; [1,2,3] →[1,1,0]; [3,2,1] →[0,0,0]; [] →[].

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

environmental sensor dashboards; pollution maps; threshold trend summaries. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
