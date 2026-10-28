# DriveDiet: Cloud Storage Cleaner

Cloud drives fill up with duplicates and forgotten folders. A cleaner finds what to delete and how to sync efficiently.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Folder tree; file sizes and hashes; two devices’ listings; paths.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate files | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Folder sizes | 3 | Week 3 / CO2 | `CoreBasics.total` |
| Merge device listings | 6 | Week 6 / CO3 | `W06CatalogueReference.merge` |
| Find a name pattern | 7 | Week 6 / CO3 | `Week7ProjectReference.count` |
| Sync bandwidth | 9 | Week 8 / CO4 | `Week9ProjectReference.min_capacity` |
| Thumbnail cache | 11 | Week 11 / CO6 | `Week11ProjectReference.simulate` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate files

Files with the same content hash. Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: ["a9", "c4", "a9", "f1", "c4", "a9"]
Verified expected: repeats=3, distinct=3, first repeat=a9
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"a9","c4","a9","f1","c4","a9"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Folder sizes

Post-order recursion for every folder’s total size. Each node has a nonnegative own value and children; sum every node in the rooted tree once. The input is a tree, not a cyclic graph; own value is not already a subtree total.

**Contract:** `long total(Node root)`

**Input limits and conventions:** Trusted rooted tree, not a cyclic graph or shared-child DAG; at most1000 nodes; root depth0, maximum depth16; nonnegative long own values. Sum each own value exactly once; Math.addExact raises ArithmeticException on overflow. Null root returns0. For untrusted graphs, perform separate structure validation.

### Worked example

```text
tree
Input: root(5)->photos(1200)->{2025(800),2026(950)}, root->docs(300)
Verified expected: 3255
```

**Executable call:**

```java
CoreBasics.total(new CoreBasics.Node(5,new CoreBasics.Node(1200,new CoreBasics.Node(800),new CoreBasics.Node(950)),new CoreBasics.Node(300)))
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Base null/leaf, recursively add child totals; validate cycles separately if accepting an untrusted graph. Define Node{long own;List<Node>children;}.

**Invariant and cost:** O(nodes) time/O(height) frames for bounded-depth trees. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** root(5) with children a(2),b(3) → 10; null root → 0.

## 3. Merge device listings

Merge two name-sorted file listings in one pass. Inputs are nondecreasing; return every value in sorted order including duplicates. Preserve within-input order and choose left first on equal keys.

**Contract:** `int[] merge(int[] left,int[] right)`

**Input limits and conventions:** Nonnull sorted integer arrays;combined n<=1000. Preserve all duplicates and source arrays;fresh output. Within-input order retained,left first on equality. Numeric equal-key stability is unobservable;use tagged-event faculty model to demonstrate it.

### Worked example

```text
file ids (phone), (laptop)
Input: [2, 5, 9, 14], [3, 5, 11]
Verified expected: [2, 3, 5, 5, 9, 11, 14]
```

**Executable call:**

```java
W06CatalogueReference.merge(new int[]{2,5,9,14},new int[]{3,5,11})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Two read indices plus output index; append the remaining suffix after either input ends.

**Invariant and cost:** O(a+b) time; O(a+b) returned storage. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

**Additional cases:** [1,3],[2,3] → [1,2,3,3]; [],[] → [].

## 4. Find a name pattern

Count occurrences of a pattern in the concatenated path list (KMP). Count every contiguous exact occurrence, including overlaps. Pattern must be nonempty; comparison is case-sensitive; no implicit whole-word restriction.

**Contract:** `int count(String text,String pattern)`

**Input limits and conventions:** Nonnull text0..100000 Java characters;nonnull NONEMPTY pattern1..100000. Case-sensitive exact contiguous occurrences INCLUDING overlaps;not whole-word or token-boundary matching. KMP O(text+pattern),O(pattern) LPS;empty pattern rejected.

### Worked example

```text
paths, pattern
Input: "/img/2025/img_01.jpg /img/2026/img_02.jpg", "img"
Verified expected: 4
```

**Executable call:**

```java
Week7ProjectReference.count("/img/2025/img_01.jpg /img/2026/img_02.jpg","img")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Build LPS; after a complete match, fall back to lps[m−1] to keep overlaps. Adapt LC28 first-match starter to continue counting. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** text="aaaa",pattern="aa" → 3; text="abc",pattern="z" → 0.

## 5. Sync bandwidth

Minimum upload capacity per hour so files, in order, sync within 4 hours. Positive indivisible batches in original order, partitioned into at most rounds contiguous groups. Minimise maximum group sum; no splitting/reordering.

**Contract:** `long min_capacity(int[] batches,int rounds)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE batches<=10^9,rounds>=1. Original order,indivisible items,at most rounds contiguous groups. Return long;rounds>n is allowed and returns max batch. Validate once,then run fits on validated data.

### Worked example

```text
file sizes (MB), 4 hours
Input: [450, 200, 380, 120, 260, 300], 4
Expected output (independently verified): 560
```

**Executable call:**

```java
Week9ProjectReference.min_capacity(new int[]{450,200,380,120,260,300},4)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use greedy groups≤rounds and first feasible capacity in [max,sum], long arithmetic. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [7,2,5,10,8],2 →18; rounds1 →32; rounds5 →10. [1000000000,1000000000,1000000000],1 →3000000000.

## 6. Thumbnail cache

LRU cache of thumbnails; hit ratio. Use a positive-capacity LRU resident set. Each request first looks up the ID, counts a hit only if present, promotes on hit, and loads on miss; evict the least-recent ID when needed. Return hit count/total, ratio and final MRU→LRU IDs.

**Contract:** `String simulate(int[] requests,int capacity); Cache.get(key),put(key,value),order(),integrity(). Values use the nonnegative ID itself for this fixture.`

**Input limits and conventions:** See the checked Java implementation and regression suite.

### Worked example

```text
thumbnail ids, capacity 2
Input: [1, 2, 1, 3, 2, 1, 3], 2
Verified expected output: hits=1 of 7, final order (most recent first) [3, 1]
Hit ratio: 1/7
```

**Executable call:**

```java
Week11ProjectReference.simulate(new int[]{1,2,1,3,2,1,3},2)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** See the checked Java implementation and regression suite.

**Invariant and cost:** Map keys and live list nodes are bijective; next/prev reciprocal; size≤C; front MRU/back LRU. get/put average O(1), storageO(C). Debug traversal costsO(C); full-trace debugO(qC).

**Additional cases:** requests=[4,4,5,4],C=1 →hits1/4,order[4]; empty,C=2 →hits0/0,ratioN/A,order[]; C=0 →IllegalArgumentException. Add repeated-key update and missing-get-no-change tests.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

folder-size summaries; duplicate-file reporting; recently used file caches. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
