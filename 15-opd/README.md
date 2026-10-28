# CareQueue: Hospital OPD Tokens

Hospital out-patient departments issue tokens and display waiting times. Administrators decide how many doctors to schedule.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Arrivals per minute, consultation times, department tree.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate tokens | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Department totals | 3 | Week 3 / CO2 | `CoreBasics.total` |
| Patients in any hours | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| Busiest two hours | 7 | Week 6 / CO3 | `Week7ProjectReference.bestWindow` |
| Consultation capacity | 9 | Week 8 / CO4 | `Week9ProjectReference.min_capacity` |
| Token queue | 12 | Week 12 / CO6 | `Week12ProjectReference.queueSim` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate tokens

Token numbers issued twice by a faulty kiosk (count array). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [17, 18, 19, 18, 20, 21, 17]
Verified expected: repeats=2, distinct=5, first repeat=18
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"17","18","19","18","20","21","17"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The repeated encounters, in arrival order, are ['18', '17']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Department totals

Patients per department in a nested department tree (post-order recursion). Each node has a nonnegative own value and children; sum every node in the rooted tree once. The input is a tree, not a cyclic graph; own value is not already a subtree total.

**Contract:** `long total(Node root)`

**Input limits and conventions:** Trusted rooted tree, not a cyclic graph or shared-child DAG; at most1000 nodes; root depth0, maximum depth16; nonnegative long own values. Sum each own value exactly once; Math.addExact raises ArithmeticException on overflow. Null root returns0. For untrusted graphs, perform separate structure validation.

### Worked example

```text
department tree
Input: OPD(0)->Medicine(40)->Cardio(25); OPD->Surgery(35)
Verified expected: 100
```

**Executable call:**

```java
CoreBasics.total(new CoreBasics.Node(0,new CoreBasics.Node(40,new CoreBasics.Node(25)),new CoreBasics.Node(35)))
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The node-own values are [0, 40, 25, 35]; adding each once gives 100. These are not already subtree totals.

**Algorithm:** Post-order recursion adds a node’s own value to its children’s totals. Identity tracking rejects repeated nodes; depth, node-count and nonnegative-value checks enforce the declared tree bounds. Math.addExact rejects overflow.

**Invariant and cost:** Each accepted node is visited once. For n nodes and height h, time is O(n), and actual auxiliary storage is O(n+h): an identity set plus recursion frames. Null root returns zero. A child value is its own contribution, not a precomputed subtree total.

**Additional cases:** root(5) with children a(2),b(3) → 10; null root → 0.

## 3. Patients in any hours

Prefix sums over hourly arrivals. Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
patients per hour, queries
Input: [30, 45, 20, 38, 52, 26], [[0, 2], [3, 5]]
Verified expected: [95, 116]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{30L,45L,20L,38L,52L,26L},new int[][]{new int[]{0,2},new int[]{3,5}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Direct inclusive slices [[30, 45, 20], [38, 52, 26]] sum to [95, 116]; both query endpoints count.

**Algorithm:** Build a frozen prefix array with an extra leading zero. For an inclusive query [l,r], subtract prefix[l] from prefix[r+1]. The index must be rebuilt after source updates.

**Invariant and cost:** prefix[i] is the sum of the first i values. The convenience batch method takes O(n+q) time and O(n+q) space including its output; construction is O(n), each retained-index query O(1). Signed values and long totals are supported.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. Busiest two hours

Highest 2-hour total (fixed window). Exactly k consecutive positions; return [maximum sum, earliest starting index] when tied. Require 1≤k≤n.

**Contract:** `long[] bestWindow(int[] values,int k)`

**Input limits and conventions:** Nonnull signed int array1..100000;1<=k<=n. Return long[2]={maximum sum,EARLIEST zero-based start};all-negative windows valid. Preserve source;checked long addition/subtraction;fixed width only.

### Worked example

```text
patients per hour, k=2
Input: [30, 45, 20, 38, 52, 26], 2
Verified expected: "sum=90 starting at index 3"
```

**Executable call:**

```java
Week7ProjectReference.bestWindow(new int[]{30,45,20,38,52,26},2)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Complete width-2 window totals are [75, 65, 58, 90, 78]; the first maximum gives sum/start [90, 3].

**Algorithm:** Maintain the sum of exactly k consecutive values: add each entering value and subtract the departing value. Compare only complete windows. Update the best start only on strict improvement to keep the earliest tie.

**Invariant and cost:** The running sum equals the current complete window. O(n) time and O(1) workspace, excluding the two-element result. Signed values are valid; initializing from a real window rather than zero handles all-negative input.

**Additional cases:** [1,2,3],k=2 → [5,1]; [5],k=1 → [5,0].

## 5. Consultation capacity

Minimum patients per slot so the day’s patients, in arrival order, are seen within 5 slots. Positive indivisible batches in original order, partitioned into at most rounds contiguous groups. Minimise maximum group sum; no splitting/reordering.

**Contract:** `long min_capacity(int[] batches,int rounds)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE batches<=10^9,rounds>=1. Original order,indivisible items,at most rounds contiguous groups. Return long;rounds>n is allowed and returns max batch. Validate once,then run fits on validated data.

### Worked example

```text
patients per hour, 5 slots
Input: [30, 45, 20, 38, 52, 26, 18, 12, 9], 5
Expected output (independently verified): 65
```

**Executable call:**

```java
Week9ProjectReference.min_capacity(new int[]{30,45,20,38,52,26,18,12,9},5)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Exhaustive contiguous partitions using at most 5 groups yield minimum capacity 65; one partition is [[30], [45], [20, 38], [52], [26, 18, 12, 9]], with loads [30, 45, 58, 52, 65].

**Algorithm:** Search the smallest capacity between the largest batch and the total load. A greedy predicate fills each contiguous group until the next batch would exceed capacity, then starts another group. Keep capacities requiring at most rounds groups.

**Invariant and cost:** For a fixed capacity, greedily taking the longest prefix minimizes the number of groups; larger capacity cannot hurt feasibility. O(n log(S-M+1)+n) time and O(1) extra space for total S and largest batch M. Preserve original order; this is not arbitrary bin packing.

**Additional cases:** [7,2,5,10,8],2 →18; rounds1 →32; rounds5 →10. [1000000000,1000000000,1000000000],1 →3000000000.

## 6. Token queue

Circular-array FIFO; simulate 2 doctors. Bucket t records arrivals at minute t, starting t=0. Enqueue in bucket order; simultaneous arrivals retain token order. Counters are identical and initially idle. Service is non-preemptive and takes the stated whole minutes. A counter finishing at t may take an arrival at t immediately. Use earliest available counter, lowest index on ties, and FIFO. Wait=start−arrival, excluding service duration. Drain all requests beyond the final bucket; report served count, total wait and longest wait.

**Contract:** `String queueSim(int[] arrivals,int serviceMinutes,int counters)`

**Input limits and conventions:** Nonnull T<=100000 buckets,nonnegative counts,total requests<=100000;service1..100000,counters1or2. FIFO,earliest available counter/lowest index ties;wait excludes service;drain past last bucket;long totals.

### Worked example

```text
arrivals/min, 4-min consults, 2 doctors
Input: [2, 1, 1, 0, 2, 1, 0, 0], 4, 2
Verified expected: served=7, total wait=20 min, longest wait=7 min
```

**Executable call:**

```java
Week12ProjectReference.queueSim(new int[]{2,1,1,0,2,1,0,0},4,2)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** A minute-by-minute FIFO simulation produces waits [0, 0, 3, 2, 4, 4, 7]; sum 20, maximum 7. Service time is excluded from wait.

**Algorithm:** Enqueue arrival times in a circular-array FIFO. For each token, choose the counter with earliest availability, breaking ties by lower counter index; start at max(arrival,availability) and advance that counter by the service duration.

**Invariant and cost:** FIFO order and each counter’s next-free time are preserved. O(T+Nc) time and O(N+c) storage for T arrival buckets, N requests and c<=2 counters. Wait excludes service, and requests are drained after the final arrival bucket.

**Additional cases:** One counter: served=7,totalWait=68,maxWait=19; two counters: served=7,totalWait=20,maxWait=7. Empty arrivals →served0,totalWait0,maxWait0. Invalid service/counter count rejected.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

synthetic hospital token queues; department workload summaries; service-desk staffing. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
