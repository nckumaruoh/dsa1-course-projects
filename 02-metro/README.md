# MetroPulse: Smart-Card Ridership

Metro systems log every card tap at entry and exit gates. Planners study ridership peaks, crowded stretches between stations and fare products.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Taps: card id, station index (0..24), time (minutes), entry/exit flag. Fare table as an array.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Busiest station | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Cheapest fare products | 4 | Week 4 / CO2 | `W04CatalogueReference.minCoins` |
| Load on each track segment | 5 | Week 5 / CO3 | `W05CatalogueReference.apply` |
| Peak 15-minute window | 7 | Week 6 / CO3 | `Week7ProjectReference.bestWindow` |
| Train capacity needed | 9 | Week 8 / CO4 | `Week9ProjectReference.min_capacity` |
| Next busier hour | 12 | Week 12 / CO6 | `Week12ProjectReference.waitDays` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Busiest station

Entries per station with an int array indexed by station; report the busiest. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [3, 7, 3, 12, 7, 3, 5, 7, 3]
Verified expected: most frequent=3 (count 4), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{3,7,3,12,7,3,5,7,3})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Cheapest fare products

Fewest trip cards (10, 25 and 60 rides) to cover a month’s rides exactly (coin change). Positive denominations with unlimited reuse; return fewest coins for exact amount, or −1 if impossible.

**Contract:** `int minCoins(int[] coins,int amount)`

**Input limits and conventions:** Up to1000 positive integer denominations; amount0..5000. Unlimited reuse; amount0 returns0; unreachable returns-1. Use iterative tabulation at full amount to avoid a long Java call chain. Zero/negative denominations are invalid.

### Worked example

```text
card sizes, rides 135
Input: [10, 25, 60], 135
Verified expected: 4
```

**Executable call:**

```java
W04CatalogueReference.minCoins(new int[]{10,25,60},135)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use best amount recurrence and tabulate; do not treat coins as one-use indices.

**Invariant and cost:** O(nA) time, O(A) storage. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** [2],amount=3 → -1; [1],amount=0 → 0.

## 3. Load on each track segment

Each journey occupies segments [from, to): add +k/−k to a difference array, rebuild the load per segment. Start n zero cells. Each [l,r,delta] changes zero-based inclusive l..r. Return final values; signed deltas allowed.

**Contract:** `long[] apply(int n,int[][] updates)`

**Input limits and conventions:** 0<=n<=1000;0..1000 updates [l,r,delta], zero-based inclusive0<=l<=r<n; delta is signed int. n0 allows no updates. Use long difference/reconstruction and checked arithmetic; preserve updates.

### Worked example

```text
segments 0..5, journeys [from, to−1, riders]
Input: 6, [[0, 3, 120], [2, 5, 80], [1, 2, 60]]
Verified expected: [120, 180, 260, 200, 80, 80]
```

**Executable call:**

```java
W05CatalogueReference.apply(6,new int[][]{new int[]{0,3,120},new int[]{2,5,80},new int[]{1,2,60}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Allocate n+1 changes; add at l and subtract at r+1, then reconstruct n positions.

**Invariant and cost:** O(n+u) time and O(n) storage including initialisation. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** n=3,updates=[[0,1,2],[1,2,-1]] → [2,1,-1]; n=0,updates=[] → [].

## 4. Peak 15-minute window

Busiest window of entries with a fixed-size sliding window over per-5-minute counts. Exactly k consecutive positions; return [maximum sum, earliest starting index] when tied. Require 1≤k≤n.

**Contract:** `long[] bestWindow(int[] values,int k)`

**Input limits and conventions:** Nonnull signed int array1..100000;1<=k<=n. Return long[2]={maximum sum,EARLIEST zero-based start};all-negative windows valid. Preserve source;checked long addition/subtraction;fixed width only.

### Worked example

```text
entries per 5 min, window 3
Input: [40, 65, 120, 180, 150, 90, 60], 3
Verified expected: "sum=450 starting at index 2"
```

**Executable call:**

```java
Week7ProjectReference.bestWindow(new int[]{40,65,120,180,150,90,60},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Keep one long sum; add entering and subtract departing value; compare only complete windows. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [1,2,3],k=2 → [5,1]; [5],k=1 → [5,0].

## 5. Train capacity needed

Minimum train capacity so the queued riders, in order, are carried within 4 trips (binary search on the answer, greedy check). Positive indivisible batches in original order, partitioned into at most rounds contiguous groups. Minimise maximum group sum; no splitting/reordering.

**Contract:** `long min_capacity(int[] batches,int rounds)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE batches<=10^9,rounds>=1. Original order,indivisible items,at most rounds contiguous groups. Return long;rounds>n is allowed and returns max batch. Validate once,then run fits on validated data.

### Worked example

```text
riders waiting per slot, 4 trips
Input: [300, 450, 200, 380, 520, 260], 4
Expected output (independently verified): 750
```

**Executable call:**

```java
Week9ProjectReference.min_capacity(new int[]{300,450,200,380,520,260},4)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use greedy groups≤rounds and first feasible capacity in [max,sum], long arithmetic. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [7,2,5,10,8],2 →18; rounds1 →32; rounds5 →10. [1000000000,1000000000,1000000000],1 →3000000000.

## 6. Next busier hour

For each hour, how many hours until a busier one (monotonic stack). For each position i, return j−i for the earliest later j with values[j]>values[i], or0 if absent. Non-circular; equal values do not resolve a wait.

**Contract:** `int[] waitDays(int[] values)`

**Input limits and conventions:** Nonnull n0..100000 int observations;first later STRICTLY greater,non-circular;equal unresolved;0 absent. Preserve input.

### Worked example

```text
riders per hour
Input: [820, 1100, 950, 700, 1300, 1250, 900]
Verified expected: [1, 3, 2, 1, 0, 0, 0]
```

**Executable call:**

```java
Week12ProjectReference.waitDays(new int[]{820,1100,950,700,1300,1250,900})
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

transit ridership dashboards; passenger-load planning; fare-denomination tools. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
