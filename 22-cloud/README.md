# DriveDiet: Cloud Storage Cleaner

Cloud drives fill up with duplicates and forgotten folders. A cleaner finds what to delete and how to sync efficiently.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

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

**Example reasoning:** The repeated encounters, in arrival order, are ['a9', 'c4', 'a9']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

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

**Example reasoning:** The node-own values are [5, 1200, 800, 950, 300]; adding each once gives 3255. These are not already subtree totals.

**Algorithm:** Post-order recursion adds a node’s own value to its children’s totals. Identity tracking rejects repeated nodes; depth, node-count and nonnegative-value checks enforce the declared tree bounds. Math.addExact rejects overflow.

**Invariant and cost:** Each accepted node is visited once. For n nodes and height h, time is O(n), and actual auxiliary storage is O(n+h): an identity set plus recursion frames. Null root returns zero. A child value is its own contribution, not a precomputed subtree total.

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

**Example reasoning:** Sorting the concatenated multiset independently gives [2, 3, 5, 5, 9, 11, 14]; every duplicate occurrence must remain.

**Algorithm:** Validate both sorted arrays and merge them with two read pointers. Choose from the left on equality; once one array ends, copy the remaining suffix of the other.

**Invariant and cost:** The written prefix contains the smallest consumed multiset entries in sorted order. O(n+m) time, O(n+m) returned storage and O(1) additional workspace. Inputs are preserved and duplicate occurrences remain.

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

**Example reasoning:** Direct substring comparisons match at start indices [1, 10, 22, 31]; overlaps count separately.

**Algorithm:** Build the pattern’s prefix/failure array, then scan the text using KMP. On a mismatch, reuse the longest remaining prefix; after a match, fall back to its proper prefix rather than resetting to zero, so overlaps count.

**Invariant and cost:** The matched-length state is the longest pattern prefix matching the current text suffix. O(textLength+patternLength) time and O(patternLength) storage. The pattern must be nonempty.

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

**Example reasoning:** Exhaustive contiguous partitions using at most 4 groups yield minimum capacity 560; one partition is [[450], [200], [380, 120], [260, 300]], with loads [450, 200, 500, 560].

**Algorithm:** Search the smallest capacity between the largest batch and the total load. A greedy predicate fills each contiguous group until the next batch would exceed capacity, then starts another group. Keep capacities requiring at most rounds groups.

**Invariant and cost:** For a fixed capacity, greedily taking the longest prefix minimizes the number of groups; larger capacity cannot hurt feasibility. O(n log(S-M+1)+n) time and O(1) extra space for total S and largest batch M. Preserve original order; this is not arbitrary bin packing.

**Additional cases:** [7,2,5,10,8],2 →18; rounds1 →32; rounds5 →10. [1000000000,1000000000,1000000000],1 →3000000000.

## 6. Thumbnail cache

LRU cache of thumbnails; hit ratio. Use a positive-capacity LRU resident set. Each request first looks up the ID, counts a hit only if present, promotes on hit, and loads on miss; evict the least-recent ID when needed. Return hit count/total, ratio and final MRU→LRU IDs.

**Contract:** `String simulate(int[] requests,int capacity); Week11ProjectReference.LRUCache.get(key), put(key,value), order(), integrity(). Values use the nonnegative ID itself for this fixture.`

**Input limits and conventions:** Nonnull 0..200000 requests, capacity 1..3000, keys 0..10000. Cache values are 0..100000; -1 denotes a miss. Empty replay has ratio N/A. order() returns a copied MRU-to-LRU key array. integrity() validates reciprocal links and map/list agreement.

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

**Example reasoning:** A plain recency list produces 1 hits and final most-to-least-recent keys [3, 1]; a hit moves its key to the front.

**Algorithm:** Use a hash map from key to a doubly linked cache node. get moves a hit to the most-recent front; a missing key is inserted with value equal to its ID, evicting the least-recent tail if full. Normal simulate uses replay with debug disabled.

**Invariant and cost:** The map and linked list contain exactly the same resident keys in recency order. Normal replay takes expected O(n+C) time including the final order snapshot and O(C) storage. Explicit debug replay calls integrity after each request and can take O(nC) time; order/integrity scans are O(C), while individual get/put are expected O(1).

**Additional cases:** requests=[4,4,5,4],C=1 →hits1/4,order[4]; empty,C=2 →hits0/0,ratioN/A,order[]; C=0 →IllegalArgumentException. Add repeated-key update and missing-get-no-change tests.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

folder-size summaries; duplicate-file reporting; recently used file caches. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
