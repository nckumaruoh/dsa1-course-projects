# SafePay: UPI Fraud Monitor

Payment networks screen transactions in real time for duplicates, bursts, unusual amounts and mule-account patterns.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

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

**Example reasoning:** The repeated encounters, in arrival order, are ['7002', '7002']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

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

**Example reasoning:** The exact-target index subsets are [(0, 1, 2), (0, 1, 3, 4), (2, 3, 4), (3, 5), (1, 4, 5)]; equal values at different indices are distinct choices.

**Algorithm:** Start counts[0]=1 and process each indexed value once. Visit target totals downward, adding counts[t-value] into counts[t]. Downward iteration prevents the current index being reused; a zero value correctly doubles existing counts.

**Invariant and cost:** After each value, counts[t] equals the number of processed-index subsets summing to t. O(n(T+1)) time plus table initialization, O(T+1) space. Equal values at different indices remain different choices.

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

**Example reasoning:** Enumerating all nonempty contiguous sums finds 4 sums exactly equal to 10000; this is not the other overload.

**Algorithm:** Count earlier prefix sums. A new prefix p completes one qualifying interval for every earlier prefix p-target. Insert the new prefix only after counting, and initialize frequency zero to one for intervals starting at index zero.

**Invariant and cost:** The map contains exactly earlier prefixes, so each counted interval is nonempty. Expected O(n) time and O(n) space. Use the long target overload, such as 2L; the int overload instead counts divisible sums.

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

**Example reasoning:** Exhaustively checking all intervals gives shortest length 4, witnessed by indices 1..4; zero means no qualifying interval.

**Algorithm:** Grow the right endpoint until a nonnegative-value window reaches target. While it still reaches target, record its length and remove the leftmost value. Continue growing after shrinking makes it too small.

**Invariant and cost:** Every discarded left endpoint has been checked at its shortest current right endpoint. Each index enters/leaves at most once: O(n) time, O(1) workspace. Zero counts are valid; negative counts break this argument and are rejected.

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

**Example reasoning:** The half-open interval [30,60) includes [30, 31, 44, 59]; duplicates are retained and the upper endpoint is excluded.

**Algorithm:** Find the first position at least lo and first at least hi; their difference counts the half-open interval [lo,hi). Equal endpoints return zero. The adapter copies/validates the source before querying.

**Invariant and cost:** Each lower-bound search preserves its partition invariant. Numeric one-call cost is O(n+log(n+1)) time and O(n) space; retained-index queries are O(log(n+1)). For words, add validation of total characters and string-comparison character costs. Duplicates count as separate entries.

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

**Example reasoning:** The maxima of every complete width-3 slice are [1200, 9000, 9000, 9000, 8000]; no partial window is emitted.

**Algorithm:** Maintain a deque of candidate indices. Remove expired indices from its front and values no larger than the incoming value from its back. The front is the maximum for a complete window.

**Invariant and cost:** Deque indices increase and their values strictly decrease. Each index enters/leaves once: O(n) time and O(k) deque workspace, plus O(n-k+1) output. Store indices, not just values, so expiration is correct.

**Additional cases:** [5,5,5],k2 →[5,5]; [1,2,3],k1 →[1,2,3]; k3 →[3]; k0 or k>n →reject.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

synthetic transaction pattern exploration; rolling amount summaries; duplicate-ID reporting. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
