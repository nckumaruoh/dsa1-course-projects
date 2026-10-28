# WattWise: Smart Electricity Billing

Smart meters report consumption every interval. Utilities bill with slab tariffs, sell recharge packs and flag faults.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Readings per interval; slab boundaries; recharge pack sizes.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate readings | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Recharge packs | 4 | Week 4 / CO2 | `W04CatalogueReference.minCoins` |
| Bill divisibility | 5 | Week 5 / CO3 | `W05CatalogueReference.count` |
| Peak demand | 7 | Week 6 / CO3 | `Week7ProjectReference.bestWindow` |
| Slab lookup | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Rolling maximum load | 12 | Week 12 / CO6 | `Week12ProjectReference.slidingMax` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate readings

Reading ids uploaded twice (count array). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [301, 302, 303, 302, 304, 302]
Verified expected: repeats=2, distinct=4, first repeat=302
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"301","302","303","302","304","302"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The repeated encounters, in arrival order, are ['302', '302']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Recharge packs

Fewest prepaid packs of 50, 120 and 200 units for an exact top-up (coin change). Positive denominations with unlimited reuse; return fewest coins for exact amount, or −1 if impossible.

**Contract:** `int minCoins(int[] coins,int amount)`

**Input limits and conventions:** Up to1000 positive integer denominations; amount0..5000. Unlimited reuse; amount0 returns0; unreachable returns-1. Use iterative tabulation at full amount to avoid a long Java call chain. Zero/negative denominations are invalid.

### Worked example

```text
pack sizes, need 370
Input: [50, 120, 200], 370
Verified expected: 3
```

**Executable call:**

```java
W04CatalogueReference.minCoins(new int[]{50,120,200},370)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Breadth-first search of reachable totals first reaches 370 after 3 coins; denomination reuse is allowed.

**Algorithm:** Tabulate the fewest coins for every amount. For total t, try each affordable denomination and extend the best answer for t-coin. Positive denominations allow unlimited reuse; unreachable totals keep an impossible sentinel.

**Invariant and cost:** Before processing t, all smaller amounts are final. For m denominations and amount A, time is O((m+1)(A+1)), including validation/table initialization; space is O(A+1). Amount zero returns zero; an unreachable positive amount returns -1.

**Additional cases:** [2],amount=3 → -1; [1],amount=0 → 0.

## 3. Bill divisibility

Count periods whose usage is a multiple of k (remainder counting). Count nonempty contiguous intervals whose sum is divisible by k; k is positive.

**Contract:** `long countDivisible(long[] values,int k); reference adapter count(values,k). Keep distinct from exact-target counting.`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9. Count nonempty index intervals, including zero/negative values; empty input returns0. Exact-target lab abs(target)<=10^12. Divisibility lab requires1<=k<=10000; normalise remainders. Use long counts; query before recording prefix.

### Worked example

```text
units, k=5
Input: [4, 5, 0, 3, 2, 1], 5
Verified expected: 7
```

**Executable call:**

```java
W05CatalogueReference.count(new long[]{4L,5L,0L,3L,2L,1L},5)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Enumerating all nonempty contiguous sums finds 7 sums divisible by 5; this is not the other overload.

**Algorithm:** Maintain frequencies of earlier prefix remainders modulo k. Two prefixes with the same remainder delimit a sum divisible by k. Normalize signed remainders and seed remainder zero once.

**Invariant and cost:** Before inserting each remainder, its frequency counts exactly matching earlier prefixes. O(n+k) time and O(k) space including frequency-array initialization; long counts retain all matching intervals. This differs from exact-target counting.

**Additional cases:** [1,2,3],k=3 → 3; [1],k=2 → 0.

## 4. Peak demand

Highest consumption over any 3 consecutive intervals. Exactly k consecutive positions; return [maximum sum, earliest starting index] when tied. Require 1≤k≤n.

**Contract:** `long[] bestWindow(int[] values,int k)`

**Input limits and conventions:** Nonnull signed int array1..100000;1<=k<=n. Return long[2]={maximum sum,EARLIEST zero-based start};all-negative windows valid. Preserve source;checked long addition/subtraction;fixed width only.

### Worked example

```text
units per interval, k=3
Input: [12, 15, 9, 22, 18, 30, 14], 3
Verified expected: "sum=70 starting at index 3"
```

**Executable call:**

```java
Week7ProjectReference.bestWindow(new int[]{12,15,9,22,18,30,14},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Complete width-3 window totals are [36, 46, 49, 70, 62]; the first maximum gives sum/start [70, 3].

**Algorithm:** Maintain the sum of exactly k consecutive values: add each entering value and subtract the departing value. Compare only complete windows. Update the best start only on strict improvement to keep the earliest tie.

**Invariant and cost:** The running sum equals the current complete window. O(n) time and O(1) workspace, excluding the two-element result. Signed values are valid; initializing from a real window rather than zero handles all-negative input.

**Additional cases:** [1,2,3],k=2 → [5,1]; [5],k=1 → [5,0].

## 5. Slab lookup

Slab for a cumulative usage (lower bound). Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
slab ends, usage 260
Input: [100, 200, 300, 500], 260
Verified expected output: 2
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{100,200,300,500},260)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** A direct left-to-right scan first finds a value at least 260 at index 2; n=4 is a valid end sentinel.

**Algorithm:** Copy and validate the sorted array, then search a half-open unclassified interval [l,r). If a[mid]<target, move l past mid; otherwise retain mid by setting r=mid. Return the meeting boundary.

**Invariant and cost:** Values before l are below target and those at/after r are at least target. A one-call adapter is O(n+log(n+1)) time and O(n) frozen-copy space. Reuse the constructed index for O(log(n+1)) numeric queries. The valid return n must not be dereferenced.

**Additional cases:** sorted=[10,10,20],target=10 → 0; target=21 → 3 (end sentinel); sorted=[],target=10 → 0.

## 6. Rolling maximum load

Sliding window maximum for transformer sizing. Return maximum of every complete contiguous window of k observations, in order. Require1≤k≤n. Keep windows within one account/series and in original observation order; the catalogue sample represents one series.

**Contract:** `int[] slidingMax(int[] values,int k)`

**Input limits and conventions:** Nonnull n1..100000 int readings;1<=k<=n;complete windows only. Retain newer equal maxima,expire indices. Per-account streams must be grouped first;sample is one stream.

### Worked example

```text
load, k=3
Input: [12, 15, 9, 22, 18, 30, 14], 3
Verified expected: [15, 22, 22, 30, 30]
```

**Executable call:**

```java
Week12ProjectReference.slidingMax(new int[]{12,15,9,22,18,30,14},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The maxima of every complete width-3 slice are [15, 22, 22, 30, 30]; no partial window is emitted.

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

synthetic electricity usage summaries; rolling-peak dashboards; billing-denomination tools. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
