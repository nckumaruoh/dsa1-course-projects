# KisanRate: Mandi Crop-Price Tracker

Farmers check mandi prices on phones before deciding when and where to sell. Price trends and the best selling day matter.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Daily modal prices per crop per mandi; truck loads.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Most traded crop | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Load a truck | 4 | Week 4 / CO2 | `W04CatalogueReference.bestTotal` |
| Best store-and-sell | 5 | Week 5 / CO3 | `W05CatalogueReference.bestTrade` |
| Merge two mandis | 6 | Week 6 / CO3 | `W06CatalogueReference.merge` |
| Price on a date | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Days until a better price | 12 | Week 12 / CO6 | `Week12ProjectReference.waitDays` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Most traded crop

Arrivals per crop id with a count array. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. Empty input has no most-frequent ID and is outside this report contract.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [2, 5, 2, 7, 5, 2, 1]
Verified expected: most frequent=2 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{2,5,2,7,5,2,1})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Load a truck

Bags whose weights fill a 1 000 kg truck as fully as possible (subset sum). Each index is one use; nonnegative values; return the largest subset sum ≤capacity. Empty subset is valid.

**Contract:** `int bestTotal(int[] values,int capacity)`

**Input limits and conventions:** Recursive/memo lab:0..30 one-use loads, each0..5000; capacity0..5000. Nonnegative values and empty input are valid. Clone input; keep one memo for the fixed input/capacity; count false states as cached results. Other full-size implementations must state their own checked bounds.

### Worked example

```text
bag weights, capacity 1000
Input: [350, 420, 280, 160, 510], 1000
Verified expected: 950
```

**Executable call:**

```java
W04CatalogueReference.bestTotal(new int[]{350,420,280,160,510},1000)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** State (index,remaining); memoise include/skip and reuse cache across target queries.

**Invariant and cost:** O(nB) dense memo time/space plus O(n) frames. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** [2,3,5],capacity=4 → 3; [],capacity=0 → 0.

## 3. Best store-and-sell

Best buy-low, sell-high pair (running minimum). At most one buy and later sell; return best nonnegative profit plus one witness pair when positive. Dates are zero-based; no sorting by value.

**Contract:** `Interval bestTrade(long[] prices); optional long profit(long[] prices) adapter; record Interval(long value,int left,int right). Return endpoints with the scalar result.`

**Input limits and conventions:** 0..1000 signed lab prices/corrections, each abs<=10^9. Actual source price samples retain their meaning. Buy index<sell index; optional no trade returns0 and(-1,-1). Return profit AND witness; any optimal positive witness accepted. Do not sort.

### Worked example

```text
tomato Rs/quintal
Input: [1800, 1650, 1700, 2100, 1900, 2400, 2250]
Verified expected: "profit=750 (buy day 1, sell day 5)"
```

**Executable call:**

```java
W05CatalogueReference.bestTrade(new long[]{1800L,1650L,1700L,2100L,1900L,2400L,2250L})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Maintain minimum earlier price and best later difference.

**Invariant and cost:** O(n) time/O(1) workspace. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** [3,1,4] → profit=3, buy1 sell2; [3,2,1] → profit=0 (no trade).

## 4. Merge two mandis

Merge two date-sorted price lists into one timeline. Inputs are nondecreasing; return every value in sorted order including duplicates. Preserve within-input order and choose left first on equal keys.

**Contract:** `int[] merge(int[] left,int[] right)`

**Input limits and conventions:** Nonnull sorted integer arrays;combined n<=1000. Preserve all duplicates and source arrays;fresh output. Within-input order retained,left first on equality. Numeric equal-key stability is unobservable;use tagged-event faculty model to demonstrate it.

### Worked example

```text
dates at mandi A, mandi B
Input: [1, 4, 6, 9], [2, 3, 7, 10]
Verified expected: [1, 2, 3, 4, 6, 7, 9, 10]
```

**Executable call:**

```java
W06CatalogueReference.merge(new int[]{1,4,6,9},new int[]{2,3,7,10})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Two read indices plus output index; append the remaining suffix after either input ends.

**Invariant and cost:** O(a+b) time; O(a+b) returned storage. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

**Additional cases:** [1,3],[2,3] → [1,2,3,3]; [],[] → [].

## 5. Price on a date

Find a date in the sorted list (lower bound). Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted dates, day 7
Input: [1, 2, 3, 4, 6, 7, 9, 10], 7
Verified expected output: 5
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{1,2,3,4,6,7,9,10},7)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use [lo,hi), hi=n; sorted[mid]<target advances lo to mid+1, otherwise hi=mid. Preserve sortedness precondition. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** sorted=[10,10,20],target=10 → 0; target=21 → 3 (end sentinel); sorted=[],target=10 → 0.

## 6. Days until a better price

Monotonic stack over prices. For each position i, return j−i for the earliest later j with values[j]>values[i], or0 if absent. Non-circular; equal values do not resolve a wait.

**Contract:** `int[] waitDays(int[] values)`

**Input limits and conventions:** Nonnull n0..100000 int observations;first later STRICTLY greater,non-circular;equal unresolved;0 absent. Preserve input.

### Worked example

```text
prices
Input: [1800, 1650, 1700, 2100, 1900, 2400, 2250]
Verified expected: [3, 1, 1, 2, 1, 0, 0]
```

**Executable call:**

```java
Week12ProjectReference.waitDays(new int[]{1800,1650,1700,2100,1900,2400,2250})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Stack unresolved indices, popping only on a strictly larger reading and recording index difference.

**Invariant and cost:** O(n) time/O(n) stack and output; each index pushed/popped once.

**Additional cases:** [5,5,5] →[0,0,0]; [1,2,3] →[1,1,0]; [3,2,1] →[0,0,0]; [] →[].

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

crop-price exercise dashboards; market quote merging; seasonal price trend summaries. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
