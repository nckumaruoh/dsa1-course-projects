# CareQueue: Hospital OPD Tokens

Hospital out-patient departments issue tokens and display waiting times. Administrators decide how many doctors to schedule.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

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

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

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

**Algorithm:** Base null/leaf, recursively add child totals; validate cycles separately if accepting an untrusted graph. Define Node{long own;List<Node>children;}.

**Invariant and cost:** O(nodes) time/O(height) frames for bounded-depth trees. Explain your counted operation, state, base/undo rule and boundary convention.

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

**Algorithm:** Build one frozen prefix with extra zero; validate bounds; compare each answer to a direct loop.

**Invariant and cost:** O(n+q) time; O(n) prefix plus O(q) output. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

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

**Algorithm:** Keep one long sum; add entering and subtract departing value; compare only complete windows. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

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

**Algorithm:** Use greedy groups≤rounds and first feasible capacity in [max,sum], long arithmetic. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

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

**Algorithm:** Implement circular-array FIFO of arrival times, then assign each dequeued request to earliest available counter at max(arrival,available). Compare one and two counters using the same arrivals/service. Use long totals and available times.

**Invariant and cost:** O(T+Nc) event-driven scheduling; T=number of arrival buckets,N=requests,c≤2, so O(T+N); O(N+c) FIFO/counter space. A minute-by-minute implementation must count the full drained horizon, not just input buckets.

**Additional cases:** One counter: served=7,totalWait=68,maxWait=19; two counters: served=7,totalWait=20,maxWait=7. Empty arrivals →served0,totalWait0,maxWait0. Invalid service/counter count rejected.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

synthetic hospital token queues; department workload summaries; service-desk staffing. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
