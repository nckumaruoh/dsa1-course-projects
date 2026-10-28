# ShelfSense: Library Circulation

Libraries track issues and returns, compute fines, and must answer 'is this book on the shelf?' instantly at the counter.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Books sorted by accession number; issue records with day numbers; a category tree.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Double scans at the gate | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Category tree totals | 3 | Week 3 / CO2 | `CoreBasics.total` |
| Books out each day | 5 | Week 5 / CO3 | `W05CatalogueReference.apply` |
| Keyword search | 7 | Week 6 / CO3 | `Week7ProjectReference.count` |
| Find a book | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Return-counter queue | 12 | Week 12 / CO6 | `Week12ProjectReference.queueSim` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Double scans at the gate

Accession numbers scanned twice in one session (count array or set). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [1450, 2210, 1450, 3999, 2210, 1450]
Verified expected: repeats=3, distinct=3, first repeat=1450
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"1450","2210","1450","3999","2210","1450"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Category tree totals

Books per category in a nested category tree (post-order recursion). Each node has a nonnegative own value and children; sum every node in the rooted tree once. The input is a tree, not a cyclic graph; own value is not already a subtree total.

**Contract:** `long total(Node root)`

**Input limits and conventions:** Trusted rooted tree, not a cyclic graph or shared-child DAG; at most1000 nodes; root depth0, maximum depth16; nonnegative long own values. Sum each own value exactly once; Math.addExact raises ArithmeticException on overflow. Null root returns0. For untrusted graphs, perform separate structure validation.

### Worked example

```text
tree (size of each node, children)
Input: CSE(120)->AI(45)->ML(30); CSE->Systems(60)
Verified expected: 255
```

**Executable call:**

```java
CoreBasics.total(new CoreBasics.Node(120,new CoreBasics.Node(45,new CoreBasics.Node(30)),new CoreBasics.Node(60)))
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Base null/leaf, recursively add child totals; validate cycles separately if accepting an untrusted graph. Define Node{long own;List<Node>children;}.

**Invariant and cost:** O(nodes) time/O(height) frames for bounded-depth trees. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** root(5) with children a(2),b(3) → 10; null root → 0.

## 3. Books out each day

Each issue [day, returnDay] adds 1 to a range: difference array gives books-out per day. Start n zero cells. Each [l,r,delta] changes zero-based inclusive l..r. Return final values; signed deltas allowed. Library source example treats returnDay as INCLUDED. Do not convert it to an exclusive end; bus/bloodbank/ticket half-open source intervals have already been adapted to inclusive endpoint triples in their samples.

**Contract:** `long[] apply(int n,int[][] updates)`

**Input limits and conventions:** 0<=n<=1000;0..1000 updates [l,r,delta], zero-based inclusive0<=l<=r<n; delta is signed int. n0 allows no updates. Use long difference/reconstruction and checked arithmetic; preserve updates.

### Worked example

```text
days 0..7, issues [from, to, 1]
Input: 8, [[0, 3, 1], [2, 6, 1], [5, 7, 1], [1, 1, 1]]
Verified expected: [1, 2, 2, 2, 1, 2, 2, 1]
```

**Executable call:**

```java
W05CatalogueReference.apply(8,new int[][]{new int[]{0,3,1},new int[]{2,6,1},new int[]{5,7,1},new int[]{1,1,1}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Allocate n+1 changes; add at l and subtract at r+1, then reconstruct n positions.

**Invariant and cost:** O(n+u) time and O(n) storage including initialisation. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** n=3,updates=[[0,1,2],[1,2,-1]] → [2,1,-1]; n=0,updates=[] → [].

## 4. Keyword search

Count occurrences of a keyword in the catalogue text (KMP). Count every contiguous exact occurrence, including overlaps. Pattern must be nonempty; comparison is case-sensitive; no implicit whole-word restriction.

**Contract:** `int count(String text,String pattern)`

**Input limits and conventions:** Nonnull text0..100000 Java characters;nonnull NONEMPTY pattern1..100000. Case-sensitive exact contiguous occurrences INCLUDING overlaps;not whole-word or token-boundary matching. KMP O(text+pattern),O(pattern) LPS;empty pattern rejected.

### Worked example

```text
catalogue text, keyword
Input: "data structures; data science; big data analytics", "data"
Verified expected: 3
```

**Executable call:**

```java
Week7ProjectReference.count("data structures; data science; big data analytics","data")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Build LPS; after a complete match, fall back to lps[m−1] to keep overlaps. Adapt LC28 first-match starter to continue counting. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** text="aaaa",pattern="aa" → 3; text="abc",pattern="z" → 0.

## 5. Find a book

Binary search the sorted accession numbers; report the insertion point when absent. Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted accession numbers, find 4417
Input: [1002, 1450, 2210, 3999, 4417, 5120, 7004], 4417
Verified expected output: 4
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{1002,1450,2210,3999,4417,5120,7004},4417)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use [lo,hi), hi=n; sorted[mid]<target advances lo to mid+1, otherwise hi=mid. Preserve sortedness precondition. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** sorted=[10,10,20],target=10 → 0; target=21 → 3 (end sentinel); sorted=[],target=10 → 0.

## 6. Return-counter queue

Students returning books queue at the counter; compare 1 and 2 staff. Bucket t records arrivals at minute t, starting t=0. Enqueue in bucket order; simultaneous arrivals retain token order. Counters are identical and initially idle. Service is non-preemptive and takes the stated whole minutes. A counter finishing at t may take an arrival at t immediately. Use earliest available counter, lowest index on ties, and FIFO. Wait=start−arrival, excluding service duration. Drain all requests beyond the final bucket; report served count, total wait and longest wait.

**Contract:** `String queueSim(int[] arrivals,int serviceMinutes,int counters)`

**Input limits and conventions:** Nonnull T<=100000 buckets,nonnegative counts,total requests<=100000;service1..100000,counters1or2. FIFO,earliest available counter/lowest index ties;wait excludes service;drain past last bucket;long totals.

### Worked example

```text
arrivals per minute, 2-min service, 1 staff
Input: [2, 1, 0, 2, 1, 1, 0, 0], 2, 1
Verified expected: served=7, total wait=26 min, longest wait=7 min
```

**Executable call:**

```java
Week12ProjectReference.queueSim(new int[]{2,1,0,2,1,1,0,0},2,1)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Implement circular-array FIFO of arrival times, then assign each dequeued request to earliest available counter at max(arrival,available). Compare one and two counters using the same arrivals/service. Use long totals and available times.

**Invariant and cost:** O(T+Nc) event-driven scheduling; T=number of arrival buckets,N=requests,c≤2, so O(T+N); O(N+c) FIFO/counter space. A minute-by-minute implementation must count the full drained horizon, not just input buckets.

**Additional cases:** One counter: served=7,totalWait=26,maxWait=7; two counters: served=7,totalWait=4,maxWait=1. Empty arrivals →served0,totalWait0,maxWait0. Invalid service/counter count rejected.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

library circulation; document catalogues; book-return service desks. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
