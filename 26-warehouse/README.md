# PackRight: Warehouse Order Packing

Fulfilment centres pack orders into boxes and trucks, deciding box contents and truck capacity under deadlines.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Items (weight, value); orders; truck capacities; dock bookings.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Pairs for a box | 1 | Week 1 / CO1 | `CoreBasics.pairs` |
| Best box fill | 4 | Week 4 / CO2 | `W04CatalogueReference.bestTotal` |
| Units in any hour range | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| Fill a truck fastest | 7 | Week 6 / CO3 | `Week7ProjectReference.minLength` |
| Truck capacity | 9 | Week 8 / CO4 | `Week9ProjectReference.min_capacity` |
| Dock slots | 10 | Week 9 / CO5 | `Week10ProjectReference.mergeIntervals` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Pairs for a box

Count pairs of items whose weights sum exactly to a box limit (hash counting). Count unordered pairs of distinct original indices i<j whose weights sum to target. Equal-valued items at different indices remain distinct; count each pair once. Return long.

**Contract:** `long pairs(int[] weights,int target)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [3, 7, 5, 5, 2, 8, 6], 10
Verified expected: 3
```

**Executable call:**

```java
CoreBasics.pairs(new int[]{3,7,5,5,2,8,6},10)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** As each weight x arrives, add the number of earlier target−x values, then update frequency[x]. Query before insert to avoid pairing an item with itself. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** As each weight x arrives, add the number of earlier target−x values, then update frequency[x]. Query before insert to avoid pairing an item with itself. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [5,5,5],target10 →3; [3,7],target10 →1; [],target10 →0.

## 2. Best box fill

Best weight within a 15 kg box (subset DP). Each index is one use; nonnegative values; return the largest subset sum ≤capacity. Empty subset is valid.

**Contract:** `int bestTotal(int[] values,int capacity)`

**Input limits and conventions:** Recursive/memo lab:0..30 one-use loads, each0..5000; capacity0..5000. Nonnegative values and empty input are valid. Clone input; keep one memo for the fixed input/capacity; count false states as cached results. Other full-size implementations must state their own checked bounds.

### Worked example

```text
weights, box 15
Input: [6, 4, 9, 3, 7], 15
Verified expected: 15
```

**Executable call:**

```java
W04CatalogueReference.bestTotal(new int[]{6,4,9,3,7},15)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** State (index,remaining); memoise include/skip and reuse cache across target queries.

**Invariant and cost:** O(nB) dense memo time/space plus O(n) frames. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** [2,3,5],capacity=4 → 3; [],capacity=0 → 0.

## 3. Units in any hour range

Prefix sums over units packed per hour. Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
units per hour, queries
Input: [140, 220, 310, 280, 190, 120], [[1, 3], [0, 5]]
Verified expected: [810, 1260]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{140L,220L,310L,280L,190L,120L},new int[][]{new int[]{1,3},new int[]{0,5}})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Build one frozen prefix with extra zero; validate bounds; compare each answer to a direct loop.

**Invariant and cost:** O(n+q) time; O(n) prefix plus O(q) output. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. Fill a truck fastest

Fewest consecutive orders whose weight reaches a truck load (variable window). Nonnegative counts and positive target. Return shortest nonempty contiguous length with sum≥target, or 0 if none.

**Contract:** `int minLength(int[] values,int target)`

**Input limits and conventions:** Nonnull0..100000 NONNEGATIVE int counts;positive int target. Shortest NONEMPTY contiguous interval with sum>=target,or0 if none. Zeros permitted;negative counts rejected. Preserve source;long running sum.

### Worked example

```text
order weights, load 30
Input: [7, 2, 5, 10, 8, 3, 6], 30
Verified expected: 5
```

**Executable call:**

```java
Week7ProjectReference.minLength(new int[]{7,2,5,10,8,3,6},30)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Grow right; while sum reaches target, update length and shrink left. Negative values break this monotonic shrinking rule. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [2,3,1],target=4 → 2; [1,1],target=5 → 0.

## 5. Truck capacity

Least truck capacity to ship in-order orders within 3 days. Positive indivisible batches in original order, partitioned into at most rounds contiguous groups. Minimise maximum group sum; no splitting/reordering.

**Contract:** `long min_capacity(int[] batches,int rounds)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE batches<=10^9,rounds>=1. Original order,indivisible items,at most rounds contiguous groups. Return long;rounds>n is allowed and returns max batch. Validate once,then run fits on validated data.

### Worked example

```text
orders, 3 days
Input: [7, 2, 5, 10, 8, 3, 6], 3
Expected output (independently verified): 17
```

**Executable call:**

```java
Week9ProjectReference.min_capacity(new int[]{7,2,5,10,8,3,6},3)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use greedy groups≤rounds and first feasible capacity in [max,sum], long arithmetic. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [7,2,5,10,8],2 →18; rounds1 →32; rounds5 →10. [1000000000,1000000000,1000000000],1 →3000000000.

## 6. Dock slots

Merge overlapping dock bookings. Each pair is a closed interval [start,end], start≤end. Merge when the next start≤current end, including shared endpoints. Return intervals by start, preserving exactly the covered union. Empty input returns empty; reversed intervals rejected. Treat the source sample as one room/resource. For multiple rooms, group by room ID first and never merge different rooms.

**Contract:** `int[][] mergeIntervals(int[][] intervals)`

**Input limits and conventions:** Nonnull n<=100000 valid CLOSED pairs;start<=end. Touching merges. Preserve rows;group multiple rooms first with mergeByRoom;never merge different rooms.

### Worked example

```text
dock bookings
Input: [[8, 10], [9, 11], [13, 14], [11, 12], [15, 17]]
Expected output, independently verified: [[8, 12], [13, 14], [15, 17]]
```

**Executable call:**

```java
Week10ProjectReference.mergeIntervals(new int[][]{new int[]{8,10},new int[]{9,11},new int[]{13,14},new int[]{11,12},new int[]{15,17}})
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

parcel pairing; packing-capacity planning; dispatch-window consolidation. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
