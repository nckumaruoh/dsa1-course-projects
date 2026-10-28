# RainCatch: Rooftop Water Harvesting

Housing societies estimate how much rain their terraces trap and plan pumping and tank capacity for the monsoon.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Terrace heights along a row; daily rainfall (mm); a terrace grid.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Rainfall bands | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Flooded region | 3 | Week 3 / CO2 | `CoreBasics.region` |
| Rainfall over a period | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| Water trapped | 6 | Week 6 / CO3 | `W06CatalogueReference.water` |
| Pump capacity | 9 | Week 8 / CO4 | `Week9ProjectReference.min_capacity` |
| Largest flat roof area | 12 | Week 12 / CO6 | `Week12ProjectReference.maxRectangle` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Rainfall bands

Days per rainfall band (count array); the most common band. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [0, 1, 2, 3, 1, 0, 2, 3, 0]
Verified expected: most frequent=0 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{0,1,2,3,1,0,2,3,0})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Flooded region

Flood fill on a terrace grid to size a connected puddle. Return the number of dot cells connected to the start by four-neighbour moves; hashes block movement. A blocked start returns zero. Each cell counted once.

**Contract:** `int region(String[] grid,int startRow,int startCol)`

**Input limits and conventions:** Rectangular grid1..30 rows by1..30 columns, cells only dot or hash; start indices in range. Dot cells are traversable and connected by four-neighbour moves; hash start returns0. Use a visited structure and explicit worklist at full size; demonstrate recursion only on a tiny grid. Preserve input.

### Worked example

```text
grid, start (0,0)
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

## 3. Rainfall over a period

Prefix sums of daily rainfall. Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
mm per day, queries
Input: [0, 12, 35, 60, 8, 0, 22, 41], [[1, 4], [0, 7]]
Verified expected: [115, 178]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{0L,12L,35L,60L,8L,0L,22L,41L},new int[][]{new int[]{1,4},new int[]{0,7}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Build one frozen prefix with extra zero; validate bounds; compare each answer to a direct loop.

**Invariant and cost:** O(n+q) time; O(n) prefix plus O(q) output. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. Water trapped

Trapping rain water with two pointers. Unit-width nonnegative bars trap rain above them; return total trapped units, with no water beyond the outside edges.

**Contract:** `long water(int[] heights)`

**Input limits and conventions:** Nonnull0..1000 nonnegative unit-width heights0..10^9. Empty/singleton returns0;outside edges do not hold water. Return long (up to998000000000 in this lab),preserve source. Compare direct left/right maxima on small cases.

### Worked example

```text
terrace heights
Input: [12, 4, 7, 2, 9, 3, 3, 15, 6, 8, 1, 10]
Verified expected: 59
```

**Executable call:**

```java
W06CatalogueReference.water(new int[]{12,4,7,2,9,3,3,15,6,8,1,10})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Maintain left/right maxima and advance the side with the smaller boundary. Compare a direct per-position bound on small inputs.

**Invariant and cost:** O(n) time/O(1) workspace. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

**Additional cases:** [2,0,2] → 2; [] → 0.

## 5. Pump capacity

Minimum daily pumping capacity to move collected water, in order, within 4 days. Positive indivisible batches in original order, partitioned into at most rounds contiguous groups. Minimise maximum group sum; no splitting/reordering.

**Contract:** `long min_capacity(int[] batches,int rounds)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE batches<=10^9,rounds>=1. Original order,indivisible items,at most rounds contiguous groups. Return long;rounds>n is allowed and returns max batch. Validate once,then run fits on validated data.

### Worked example

```text
litres per collection (×100), 4 days
Input: [12, 35, 60, 8, 22, 41, 18], 4
Expected output (independently verified): 60
```

**Executable call:**

```java
Week9ProjectReference.min_capacity(new int[]{12,35,60,8,22,41,18},4)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use greedy groups≤rounds and first feasible capacity in [max,sum], long arithmetic. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [7,2,5,10,8],2 →18; rounds1 →32; rounds5 →10. [1000000000,1000000000,1000000000],1 →3000000000.

## 6. Largest flat roof area

Largest rectangle in the height histogram. Each histogram bar has unit width and nonnegative height. Find maximum rectangle area over consecutive bars using their minimum height times count. Return long; empty histogram has area0.

**Contract:** `long maxRectangle(int[] heights)`

**Input limits and conventions:** Nonnull n0..100000,heights0..1e9,unit widths. Empty0;long area up to1e14. Preserve input.

### Worked example

```text
heights
Input: [2, 1, 5, 6, 2, 3]
Verified expected: 10
```

**Executable call:**

```java
Week12ProjectReference.maxRectangle(new int[]{2,1,5,6,2,3})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Increasing index stack; pop heights≥current, compute width between previous smaller and current boundary; flush at n. Do not accidentally access heights[n].

**Invariant and cost:** O(n) time/O(n) stack; area may exceed int.

**Additional cases:** [2,2] →4; [0,0] →0; [1,2,3] →4; [] →0.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

rooftop grid simulations; water-storage planning; rainfall window summaries. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
