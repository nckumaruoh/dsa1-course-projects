# SafePay: UPI Fraud Monitor

Payment networks screen transactions in real time for duplicates, bursts, unusual amounts and mule-account patterns.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Transactions (account, time, amount, merchant).

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate transactions | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Matching a flagged amount | 4 | Week 4 / CO2 | `W04CatalogueReference.countSubsets` |
| Exact-amount runs | 5 | Week 5 / CO3 | `W05CatalogueReference.count` |
| Fastest Rs 50 000 | 7 | Week 6 / CO3 | `Week7ProjectReference.minLength` |
| Transactions in a window | 8 | Week 7 / CO4 | `W08CatalogueReference.count` |
| Rolling maximum amount | 12 | Week 12 / CO6 | `Week12ProjectReference.slidingMax` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate transactions

Transaction ids seen twice (count array). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [7001, 7002, 7003, 7002, 7004, 7002]
Verified expected: repeats=2, distinct=4, first repeat=7002
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"7001","7002","7003","7002","7004","7002"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Matching a flagged amount

Count sets of transfers totalling exactly a flagged amount (backtracking). Each index may be used at most once; equal values at distinct indices are separate choices. Count exact-target index subsets.

**Contract:** `long countSubsets(int[] values,int target)`

**Input limits and conventions:** At most60 one-use indices with nonnegative values0..5000; target0..5000. Equal-valued indices are distinct. The empty subset is one target0 choice; unprocessed zero-valued indices still create additional choices. Use checked long counts; do not stop recursion early at remain0 while zeros remain.

### Worked example

```text
transfers (Rs 100s), flagged 100
Input: [20, 30, 50, 40, 10, 60], 100
Verified expected: 5
```

**Executable call:**

```java
W04CatalogueReference.countSubsets(new int[]{20,30,50,40,10,60},100)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** State (index,remaining); include and skip counts add. A zero value doubles relevant index choices.

**Invariant and cost:** O(nT) memo time/space for bounded nonnegative targets. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** [2,2],target=2 → 2; [],target=0 → 1.

## 3. Exact-amount runs

Count consecutive runs summing exactly to a target (prefix + map). Count nonempty contiguous intervals with sum exactly target, including signed values.

**Contract:** `long countExact(long[] values,long target); reference adapter count(values,target). Use a long target (e.g.2L) to select exact-count overload, not divisibility.`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9. Count nonempty index intervals, including zero/negative values; empty input returns0. Exact-target lab abs(target)<=10^12. Divisibility lab requires1<=k<=10000; normalise remainders. Use long counts; query before recording prefix.

### Worked example

```text
amounts, target 10000
Input: [2000, 3000, 5000, 4000, 1000, 5000, 5000], 10000
Verified expected: 4
```

**Executable call:**

```java
W05CatalogueReference.count(new long[]{2000L,3000L,5000L,4000L,1000L,5000L,5000L},10000L)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Prefix-frequency map starts {0:1}; add frequency[currentâˆ’target] before recording current prefix.

**Invariant and cost:** Expected O(n) time/O(n) space with hashing. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** [1,1,1],target=2 → 2; [0,0],target=0 → 3.

## 4. Fastest Rs 50 000

Shortest burst of consecutive transactions reaching Rs 50 000. Nonnegative counts and positive target. Return shortest nonempty contiguous length with sum≥target, or 0 if none.

**Contract:** `int minLength(int[] values,int target)`

**Input limits and conventions:** Nonnull0..100000 NONNEGATIVE int counts;positive int target. Shortest NONEMPTY contiguous interval with sum>=target,or0 if none. Zeros permitted;negative counts rejected. Preserve source;long running sum.

### Worked example

```text
amounts (Rs 1000s), target 50
Input: [5, 12, 8, 20, 15, 3, 25], 50
Verified expected: 4
```

**Executable call:**

```java
Week7ProjectReference.minLength(new int[]{5,12,8,20,15,3,25},50)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Grow right; while sum reaches target, update length and shrink left. Negative values break this monotonic shrinking rule. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [2,3,1],target=4 → 2; [1,1],target=5 → 0.

## 5. Transactions in a window

Sorted times: count in [t1, t2). Count all entries in the half-open value interval [lo,hi). Include every duplicate at lo; exclude every duplicate at hi. Equal endpoints return zero; reject lo>hi.

**Contract:** `int count(int[] sorted,int lo,int hi)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted times, [30, 60)
Input: [5, 18, 30, 31, 44, 59, 60, 75], 30, 60
Verified expected output: 4
```

**Executable call:**

```java
W08CatalogueReference.count(new int[]{5,18,30,31,44,59,60,75},30,60)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Compute lower(hi)−lower(lo), not a first/last matching-index scan. Both bounds may validly equal n. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** sorted=[10,10,20,30,30],range [10,30) → 3; [20,20) → 0; empty input → 0.

## 6. Rolling maximum amount

Sliding window maximum per account. Return maximum of every complete contiguous window of k observations, in order. Require1≤k≤n. Keep windows within one account/series and in original observation order; the catalogue sample represents one series.

**Contract:** `int[] slidingMax(int[] values,int k)`

**Input limits and conventions:** Nonnull n1..100000 int readings;1<=k<=n;complete windows only. Retain newer equal maxima,expire indices. Per-account streams must be grouped first;sample is one stream.

### Worked example

```text
amounts, k=3
Input: [500, 1200, 300, 9000, 400, 350, 8000], 3
Verified expected: [1200, 9000, 9000, 9000, 8000]
```

**Executable call:**

```java
Week12ProjectReference.slidingMax(new int[]{500,1200,300,9000,400,350,8000},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Deque of indices: expire indices≤i−k from front; remove back values≤current, keeping newer equal peaks; emit front value after k readings.

**Invariant and cost:** O(n) time/O(k) deque workspace plus O(n−k+1) output.

**Additional cases:** [5,5,5],k2 →[5,5]; [1,2,3],k1 →[1,2,3]; k3 →[3]; k0 or k>n →reject.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

synthetic transaction pattern exploration; rolling amount summaries; duplicate-ID reporting. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
