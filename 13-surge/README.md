# FairFare: Ride-Share Surge Analyzer

Ride-hailing apps raise prices when demand outstrips drivers. Analysts check when surges happen and whether they are fair.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Requests and available drivers per minute per zone; shift lengths.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate ride requests | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Shift plans | 4 | Week 4 / CO2 | `W04CatalogueReference.ways` |
| Worst shortage stretch | 5 | Week 5 / CO3 | `W05CatalogueReference.bestInterval` |
| Shortest stretch reaching 50 rides | 7 | Week 6 / CO3 | `Week7ProjectReference.minLength` |
| Driver rate needed | 9 | Week 8 / CO4 | `Week9ProjectReference.min_rate` |
| Surge cap | 12 | Week 12 / CO6 | `Week12ProjectReference.slidingMax` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate ride requests

Rider ids that requested twice within a minute (count array). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [88, 91, 88, 93, 95, 91, 88]
Verified expected: repeats=3, distinct=4, first repeat=88
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"88","91","88","93","95","91","88"})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Shift plans

Ordered ways to cover a 10-hour day with 2-, 3- and 4-hour shifts (memoised recursion). Positive choices may repeat; different orders count separately. Count sequences with exact sum.

**Contract:** `long ways(int[] choices,int target)`

**Input limits and conventions:** Distinct positive integer choices, at most60; target0..60 for the checked-long lab. Orders count separately and choices may repeat; target0 has one empty sequence. Amount-outer iteration counts ordered sequences. Reject zero/negative/duplicate choices; use Math.addExact.

### Worked example

```text
shift lengths, 10 hours
Input: [2, 3, 4], 10
Verified expected: 17
```

**Executable call:**

```java
W04CatalogueReference.ways(new int[]{2,3,4},10)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** ways[0]=1; for each total, sum ways[total−choice] over choices. Use checked long addition.

**Invariant and cost:** O(nT) time, O(T) storage. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** choices=[1,2],target=3 → 3 ([1,1,1],[1,2],[2,1]); target=0 → 1.

## 3. Worst shortage stretch

Maximum run of (requests − drivers): the most painful surge. Return the maximum sum of a nonempty contiguous interval; also report one pair of zero-based inclusive endpoints.

**Contract:** `Interval bestInterval(long[] values); optional long bestSum(long[] values) adapter; record Interval(long value,int left,int right). Return endpoints with the scalar result.`

**Input limits and conventions:** 1..1000 signed values, each abs<=10^9. Nonempty contiguous interval; return sum AND inclusive endpoint witness. Any optimal witness accepted; teaching reference retains first encountered strict improvement. Reject empty input; all-negative must return a negative best.

### Worked example

```text
requests − drivers per 5 min
Input: [-3, 4, 6, -2, 5, -8, 3, 2]
Verified expected: "best=13 over indices 1..4"
```

**Executable call:**

```java
W05CatalogueReference.bestInterval(new long[]{-3L,4L,6L,-2L,5L,-8L,3L,2L})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Track best interval ending here, then best seen. All-negative inputs must retain their largest element.

**Invariant and cost:** O(n) time/O(1) workspace excluding returned endpoints. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** [-2,-1] → best=-1 at 1..1; [4] → best=4 at 0..0.

## 4. Shortest stretch reaching 50 rides

Minimum window with requests ≥ target (positive counts). Nonnegative counts and positive target. Return shortest nonempty contiguous length with sum≥target, or 0 if none.

**Contract:** `int minLength(int[] values,int target)`

**Input limits and conventions:** Nonnull0..100000 NONNEGATIVE int counts;positive int target. Shortest NONEMPTY contiguous interval with sum>=target,or0 if none. Zeros permitted;negative counts rejected. Preserve source;long running sum.

### Worked example

```text
requests per minute, target 50
Input: [8, 12, 5, 20, 9, 14, 3], 50
Verified expected: 5
```

**Executable call:**

```java
Week7ProjectReference.minLength(new int[]{8,12,5,20,9,14,3},50)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Grow right; while sum reaches target, update length and shrink left. Negative values break this monotonic shrinking rule. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [2,3,1],target=4 → 2; [1,1],target=5 → 0.

## 5. Driver rate needed

Minimum rides per hour per driver so every zone clears within 8 hours. Sequentially process zones/warehouses. A zone of w takes ceil(w/rate) whole hours; spare fractional time cannot be shared between zones. Sum these times≤hours. Return minimum positive rate or −1 if hours<zone count. This explicit lab scheduling policy disambiguates the catalogue example; it is not parallel independent completion.

**Contract:** `long min_rate(int[] work,int hours)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE workloads<=10^9,hours>=1. SEQUENTIAL zones charged ceil(work/rate) each;no fractional-hour transfer or parallel-zone assumption. Return-1 if hours<n;otherwise positive minimum rate. Long time sums.

### Worked example

```text
riders per zone, 8 hours
Input: [120, 45, 300, 80], 8
Expected output (independently verified): 80
```

**Executable call:**

```java
Week9ProjectReference.min_rate(new int[]{120,45,300,80},8)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Search first rate with sum ceil(work/rate)≤hours, in [1,max(work)]. Use long totals. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [30,11,23,4,20],6 →23; [5,7],1 →−1; [5,7],2 →7.

## 6. Surge cap

Rolling maximum surge multiplier over 3 intervals (deque). Return maximum of every complete contiguous window of k observations, in order. Require1≤k≤n. Keep windows within one account/series and in original observation order; the catalogue sample represents one series.

**Contract:** `int[] slidingMax(int[] values,int k)`

**Input limits and conventions:** Nonnull n1..100000 int readings;1<=k<=n;complete windows only. Retain newer equal maxima,expire indices. Per-account streams must be grouped first;sample is one stream.

### Worked example

```text
multiplier ×10, k=3
Input: [10, 12, 15, 13, 18, 11, 10], 3
Verified expected: [15, 15, 18, 18, 18]
```

**Executable call:**

```java
Week12ProjectReference.slidingMax(new int[]{10,12,15,13,18,11,10},3)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Deque of indices: expire indices≤i−k from front; remove back values≤current, keeping newer equal peaks; emit front value after k readings.

**Invariant and cost:** O(n) time/O(k) deque workspace plus O(n−k+1) output.

**Additional cases:** [5,5,5],k2 →[5,5]; [1,2,3],k1 →[1,2,3]; k3 →[3]; k0 or k>n →reject.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

ride-demand summaries; capacity simulations; demand-spike dashboards. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
