# ParkSmart: Mall Parking Occupancy

Malls show free-space counts at the entrance and charge by the hour. Sensors log every entry and exit.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Entries/exits with times and plates; slot grid (levels × slots) as a 2-D array.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Re-entry without exit | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Routes to a free slot | 4 | Week 4 / CO2 | `W04CatalogueReference.paths` |
| Free slots in a zone | 5 | Week 5 / CO3 | `W05CatalogueReference.rectangle` |
| EV charger bookings | 6 | Week 6 / CO3 | `W06CatalogueReference.maxSlots` |
| Fee slab lookup | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Entry gate queue | 12 | Week 12 / CO6 | `Week12ProjectReference.queueSim` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Re-entry without exit

Plates seen entering twice without an exit (count array over plate ids). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [3301, 4410, 3301, 5120, 4410, 6008]
Verified expected: repeats=2, distinct=4, first repeat=3301
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"3301","4410","3301","5120","4410","6008"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The repeated encounters, in arrival order, are ['3301', '4410']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Routes to a free slot

Count right/down routes through the lot grid avoiding blocked cells (memo DP). Start top-left, finish bottom-right, move right/down only. Hash cells are blocked and dot cells are free. Count routes; blocked start/end means zero.

**Contract:** `long paths(String[] grid)`

**Input limits and conventions:** Rectangular grid1..30 by1..30, only dot/free or hash/blocked. Right/down moves only; blocked start/end returns0. Use checked long counts and row tabulation (or a memo with stated frame depth); preserve input.

### Worked example

```text
lot grid (# = blocked)
Input: ["...", ".#.", "..."]
Verified expected: 2
```

**Executable call:**

```java
W04CatalogueReference.paths(new String[]{"...",".#.","..."})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Enumerating right/down paths while rejecting blocked cells leaves 2 valid paths to the bottom-right cell.

**Algorithm:** Scan grid rows with a one-dimensional DP array. A blocked cell becomes zero; an open cell receives paths from above plus the current row’s left neighbour. The initial one at column zero represents the empty start path.

**Invariant and cost:** After a cell is processed, its DP slot counts exactly the right/down paths to that cell. O(RC) time and O(C) extra space. A blocked start or destination yields zero; checked addition protects the bounded long result.

**Additional cases:** ["...",".#.","..."] → 2; ["#"] → 0.

## 3. Free slots in a zone

2-D prefix sums over the slot grid answer 'free slots in this block' in O(1). Query zero-based inclusive rectangle [r1..r2]×[c1..c2]; return total of every cell.

**Contract:** `long rectangle(long[][] matrix,int r1,int c1,int r2,int c2)`

**Input limits and conventions:** Nonempty rectangular1..30 by1..30 grid, cells abs<=10^9. Inclusive ordered valid corners. Build a frozen GridIndex ONCE for repeated O(1) queries. Single rectangle(matrix,...) convenience method includes O(RC) build; rebuild after updates.

### Worked example

```text
grid (1 = free), rows 0..1, cols 1..2
Input: [[1, 0, 1, 1], [0, 1, 1, 0], [1, 1, 0, 1]], 0, 1, 1, 2
Verified expected: 3
```

**Executable call:**

```java
W05CatalogueReference.rectangle(new long[][]{new long[]{1L,0L,1L,1L},new long[]{0L,1L,1L,0L},new long[]{1L,1L,0L,1L}},0,1,1,2)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The selected inclusive row contributions are [1, 2], whose sum is 3.

**Algorithm:** Construct a frozen two-dimensional prefix grid. An inclusive rectangle combines its bottom-right prefix with two excluded strips and their added-back overlap.

**Invariant and cost:** Each prefix cell totals rows/columns before its coordinates. The one-call wrapper takes O(RC) build time/space; a retained GridIndex answers each query in O(1). Validate rectangle bounds and use long arithmetic.

**Additional cases:** [[1,2],[3,4]],(0,1,1,1) → 6; [[5]],(0,0,0,0) → 5.

## 4. EV charger bookings

Most non-overlapping bookings one charger can serve (activity selection). Intervals are half-open [start,end) with start<end. Choose as many nonoverlapping intervals as possible; touching endpoints are compatible.

**Contract:** `int maxSlots(int[][] intervals)`

**Input limits and conventions:** Nonnull0..1000 valid int[2] intervals,start<end. Half-open[start,end);touching endpoints compatible. Maximum cardinality,not weighted reward/minimum rooms. Sort a copied structure with Integer.compare/comparingInt (no subtract comparator). Empty returns0;preserve source.

### Worked example

```text
booking requests [start, end)
Input: [[1, 3], [2, 5], [4, 6], [6, 8], [5, 9], [8, 10]]
Verified expected: 4
```

**Executable call:**

```java
W06CatalogueReference.maxSlots(new int[][]{new int[]{1,3},new int[]{2,5},new int[]{4,6},new int[]{6,8},new int[]{5,9},new int[]{8,10}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** An exhaustive compatible subset is [[1, 3], [4, 6], [6, 8], [8, 10]]; touching end/start times are compatible. No larger subset is compatible.

**Algorithm:** Copy and sort valid nonempty intervals by finishing time. Accept an interval whenever its start is at least the last accepted end. Choosing an earliest-finishing compatible interval leaves at least as much room for later choices.

**Invariant and cost:** An exchange argument replaces the first interval of any optimum with this greedy choice without reducing the remaining choices. O(n log n) time and O(n) copied/sort storage. Touching end/start times are compatible here; this is the scheduling contract, not the closed-interval union contract.

**Additional cases:** [[1,3],[3,5]] → 2; [] → 0.

## 5. Fee slab lookup

Fee slab for a stay length with a lower bound on slab boundaries. Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
slab boundaries (minutes), stay 135
Input: [60, 120, 180, 240, 360], 135
Verified expected output: 2
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{60,120,180,240,360},135)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** A direct left-to-right scan first finds a value at least 135 at index 2; n=5 is a valid end sentinel.

**Algorithm:** Copy and validate the sorted array, then search a half-open unclassified interval [l,r). If a[mid]<target, move l past mid; otherwise retain mid by setting r=mid. Return the meeting boundary.

**Invariant and cost:** Values before l are below target and those at/after r are at least target. A one-call adapter is O(n+log(n+1)) time and O(n) frozen-copy space. Reuse the constructed index for O(log(n+1)) numeric queries. The valid return n must not be dereferenced.

**Additional cases:** sorted=[10,10,20],target=10 → 0; target=21 → 3 (end sentinel); sorted=[],target=10 → 0.

## 6. Entry gate queue

Cars queue at the boom barrier; compare 1 and 2 gates. Bucket t records arrivals at minute t, starting t=0. Enqueue in bucket order; simultaneous arrivals retain token order. Counters are identical and initially idle. Service is non-preemptive and takes the stated whole minutes. A counter finishing at t may take an arrival at t immediately. Use earliest available counter, lowest index on ties, and FIFO. Wait=start−arrival, excluding service duration. Drain all requests beyond the final bucket; report served count, total wait and longest wait.

**Contract:** `String queueSim(int[] arrivals,int serviceMinutes,int counters)`

**Input limits and conventions:** Nonnull T<=100000 buckets,nonnegative counts,total requests<=100000;service1..100000,counters1or2. FIFO,earliest available counter/lowest index ties;wait excludes service;drain past last bucket;long totals.

### Worked example

```text
arrivals per minute, 2-min service, 1 gate
Input: [1, 2, 2, 1, 0, 1, 0], 2, 1
Verified expected: served=7, total wait=28 min, longest wait=7 min
```

**Executable call:**

```java
Week12ProjectReference.queueSim(new int[]{1,2,2,1,0,1,0},2,1)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** A minute-by-minute FIFO simulation produces waits [0, 1, 3, 4, 6, 7, 7]; sum 28, maximum 7. Service time is excluded from wait.

**Algorithm:** Enqueue arrival times in a circular-array FIFO. For each token, choose the counter with earliest availability, breaking ties by lower counter index; start at max(arrival,availability) and advance that counter by the service duration.

**Invariant and cost:** FIFO order and each counter’s next-free time are preserved. O(T+Nc) time and O(N+c) storage for T arrival buckets, N requests and c<=2 counters. Wait excludes service, and requests are drained after the final arrival bucket.

**Additional cases:** One counter: served=7,totalWait=28,maxWait=7; two counters: served=7,totalWait=7,maxWait=2. Empty arrivals →served0,totalWait0,maxWait0. Invalid service/counter count rejected.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

parking occupancy dashboards; booking time windows; entrance queue planning. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
