# TickerTrail: Stock Watchlist Analytics

Retail investors track watchlists on trading apps. Simple analytics (best trade, spans, budgets) are classic array problems.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Daily closing prices per stock; two exchanges’ tick lists; a budget.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate trade confirmations | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Spend a budget | 4 | Week 4 / CO2 | `W04CatalogueReference.bestTotal` |
| Best single trade | 5 | Week 5 / CO3 | `W05CatalogueReference.bestTrade` |
| Merge two exchanges | 6 | Week 6 / CO3 | `W06CatalogueReference.merge` |
| Price on a date | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Stock span | 12 | Week 12 / CO6 | `Week12ProjectReference.span` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate trade confirmations

Order ids confirmed twice (count array). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [9001, 9002, 9003, 9001, 9004, 9001]
Verified expected: repeats=2, distinct=4, first repeat=9001
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"9001","9002","9003","9001","9004","9001"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Spend a budget

Choose stocks (one share each) whose prices use a Rs 5 000 budget as fully as possible (subset sum). Each index is one use; nonnegative values; return the largest subset sum ≤capacity. Empty subset is valid.

**Contract:** `int bestTotal(int[] values,int capacity)`

**Input limits and conventions:** Recursive/memo lab:0..30 one-use loads, each0..5000; capacity0..5000. Nonnegative values and empty input are valid. Clone input; keep one memo for the fixed input/capacity; count false states as cached results. Other full-size implementations must state their own checked bounds.

### Worked example

```text
share prices, budget 5000
Input: [1850, 2420, 980, 1460, 3100], 5000
Verified expected: 4950
```

**Executable call:**

```java
W04CatalogueReference.bestTotal(new int[]{1850,2420,980,1460,3100},5000)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** State (index,remaining); memoise include/skip and reuse cache across target queries.

**Invariant and cost:** O(nB) dense memo time/space plus O(n) frames. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** [2,3,5],capacity=4 → 3; [],capacity=0 → 0.

## 3. Best single trade

Maximum profit buying before selling (running minimum). At most one buy and later sell; return best nonnegative profit plus one witness pair when positive. Dates are zero-based; no sorting by value.

**Contract:** `Interval bestTrade(long[] prices); optional long profit(long[] prices) adapter; record Interval(long value,int left,int right). Return endpoints with the scalar result.`

**Input limits and conventions:** 0..1000 signed lab prices/corrections, each abs<=10^9. Actual source price samples retain their meaning. Buy index<sell index; optional no trade returns0 and(-1,-1). Return profit AND witness; any optimal positive witness accepted. Do not sort.

### Worked example

```text
closes
Input: [2841, 2856, 2830, 2812, 2847, 2869, 2858, 2875]
Verified expected: "profit=63 (buy day 3, sell day 7)"
```

**Executable call:**

```java
W05CatalogueReference.bestTrade(new long[]{2841L,2856L,2830L,2812L,2847L,2869L,2858L,2875L})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Maintain minimum earlier price and best later difference.

**Invariant and cost:** O(n) time/O(1) workspace. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** [3,1,4] → profit=3, buy1 sell2; [3,2,1] → profit=0 (no trade).

## 4. Merge two exchanges

Merge two time-sorted tick lists into one timeline. Inputs are nondecreasing; return every value in sorted order including duplicates. Preserve within-input order and choose left first on equal keys.

**Contract:** `int[] merge(int[] left,int[] right)`

**Input limits and conventions:** Nonnull sorted integer arrays;combined n<=1000. Preserve all duplicates and source arrays;fresh output. Within-input order retained,left first on equality. Numeric equal-key stability is unobservable;use tagged-event faculty model to demonstrate it.

### Worked example

```text
tick times (NSE), (BSE)
Input: [1, 4, 6, 9], [2, 3, 7, 10]
Verified expected: [1, 2, 3, 4, 6, 7, 9, 10]
```

**Executable call:**

```java
W06CatalogueReference.merge(new int[]{1,4,6,9},new int[]{2,3,7,10})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Two read indices plus output index; append the remaining suffix after either input ends.

**Invariant and cost:** O(a+b) time; O(a+b) returned storage. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

**Additional cases:** [1,3],[2,3] → [1,2,3,3]; [],[] → [].

## 5. Price on a date

Find a trading day in the sorted date list (lower bound). Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted trading days, day 15
Input: [2, 3, 6, 8, 13, 15, 16], 15
Verified expected output: 5
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{2,3,6,8,13,15,16},15)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use [lo,hi), hi=n; sorted[mid]<target advances lo to mid+1, otherwise hi=mid. Preserve sortedness precondition. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** sorted=[10,10,20],target=10 → 0; target=21 → 3 (end sentinel); sorted=[],target=10 → 0.

## 6. Stock span

Consecutive days ending today with price ≤ today. For each day, return consecutive days backwards including today with prices≤today; stop at first strictly higher day.

**Contract:** `int[] span(int[] prices)`

**Input limits and conventions:** Nonnull n0..100000 int prices;consecutive earlier <=current including equals;span includes current. Preserve input.

### Worked example

```text
closes
Input: [100, 80, 60, 70, 60, 75, 85]
Verified expected: [1, 1, 1, 2, 1, 4, 6]
```

**Executable call:**

```java
Week12ProjectReference.span(new int[]{100,80,60,70,60,75,85})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use LC901 price/span stack; combine equal-price blocks.

**Invariant and cost:** O(n) total time/O(n) stack/output; each next amortizedO(1).

**Additional cases:** [5,5,5] →[1,2,3]; [3,2,1] →[1,1,1]; [] →[].

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

historical price exercise tools; watchlist merging; trend-span visualizations. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
