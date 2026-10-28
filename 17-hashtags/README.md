# TrendTag: Social Hashtag Trends

Social platforms surface trending hashtags and group spelling variants. All of it starts with counting and windows.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Posts as strings; tag ids with timestamps; reply-thread trees.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Most used tag | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Thread engagement | 3 | Week 3 / CO2 | `CoreBasics.total` |
| Posts in any hours | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| Variant grouping | 7 | Week 6 / CO3 | `Week7ProjectReference.groups` |
| Posts in a time window | 8 | Week 7 / CO4 | `W08CatalogueReference.count` |
| Top trends | 10 | Week 9 / CO5 | `Week10ProjectReference.topKLinear` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Most used tag

Counts per tag id; report the top tag. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [3, 1, 3, 5, 1, 3, 7]
Verified expected: most frequent=3 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{3,1,3,5,1,3,7})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Counting each distinct ID gives {1: 2, 3: 3, 5: 1, 7: 1}; a frequency tie chooses the smaller ID.

**Algorithm:** Count all occurrences, then retain the highest-frequency ID, choosing the smaller numeric ID on ties. The hash implementation supports sparse IDs; frequentCountArray supplies the explicit bounded-universe comparison.

**Invariant and cost:** Processed counts are exact. The winning count/ID pair is updated whenever its ordering improves. Expected O(n) hashing time and O(d) entries; the count-array alternative takes O(n+U) time and O(U) space, including initialization.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Thread engagement

Total likes in a nested reply thread (post-order recursion). Each node has a nonnegative own value and children; sum every node in the rooted tree once. The input is a tree, not a cyclic graph; own value is not already a subtree total.

**Contract:** `long total(Node root)`

**Input limits and conventions:** Trusted rooted tree, not a cyclic graph or shared-child DAG; at most1000 nodes; root depth0, maximum depth16; nonnegative long own values. Sum each own value exactly once; Math.addExact raises ArithmeticException on overflow. Null root returns0. For untrusted graphs, perform separate structure validation.

### Worked example

```text
thread tree (likes, replies)
Input: post(120)->r1(30)->r1a(12); post->r2(45)
Verified expected: 207
```

**Executable call:**

```java
CoreBasics.total(new CoreBasics.Node(120,new CoreBasics.Node(30,new CoreBasics.Node(12)),new CoreBasics.Node(45)))
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The node-own values are [120, 30, 12, 45]; adding each once gives 207. These are not already subtree totals.

**Algorithm:** Post-order recursion adds a node’s own value to its children’s totals. Identity tracking rejects repeated nodes; depth, node-count and nonnegative-value checks enforce the declared tree bounds. Math.addExact rejects overflow.

**Invariant and cost:** Each accepted node is visited once. For n nodes and height h, time is O(n), and actual auxiliary storage is O(n+h): an identity set plus recursion frames. Null root returns zero. A child value is its own contribution, not a precomputed subtree total.

**Additional cases:** root(5) with children a(2),b(3) → 10; null root → 0.

## 3. Posts in any hours

Prefix sums over posts per hour. Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
posts per hour, queries
Input: [540, 820, 1310, 2200, 1900, 760], [[2, 4], [0, 5]]
Verified expected: [5410, 7530]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{540L,820L,1310L,2200L,1900L,760L},new int[][]{new int[]{2,4},new int[]{0,5}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Direct inclusive slices [[1310, 2200, 1900], [540, 820, 1310, 2200, 1900, 760]] sum to [5410, 7530]; both query endpoints count.

**Algorithm:** Build a frozen prefix array with an extra leading zero. For an inclusive query [l,r], subtract prefix[l] from prefix[r+1]. The index must be rebuilt after source updates.

**Invariant and cost:** prefix[i] is the sum of the first i values. The convenience batch method takes O(n+q) time and O(n+q) space including its output; construction is O(n), each retained-index query O(1). Signed values and long totals are supported.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. Variant grouping

Group anagram variants of tags. Group by identical lowercase-letter multiplicities; preserve every input occurrence; group and within-group orders do not matter.

**Contract:** `List<List<String>> groups(String[] words)`

**Input limits and conventions:** Nonnull0..100000 strings,lowercase a..z;empty strings valid,total characters<=100000. Preserve EVERY input occurrence,including duplicate identical words. Group/member order arbitrary. Unambiguous count keys;do not deduplicate members.

### Worked example

```text
tags
Input: ["iplfinal", "finalipl", "goat", "toga", "rcb", "bcr", "csk"]
Verified expected: [["bcr", "rcb"], ["csk"], ["finalipl", "iplfinal"], ["goat", "toga"]]
```

**Executable call:**

```java
Week7ProjectReference.groups(new String[]{"iplfinal","finalipl","goat","toga","rcb","bcr","csk"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Sorting characters gives an anagram signature; equal signatures form groups [['bcr', 'rcb'], ['csk'], ['finalipl', 'iplfinal'], ['goat', 'toga']]. Group order is immaterial.

**Algorithm:** Count the 26 lowercase character frequencies of each word and use that vector as its anagram signature. Append the original word to the signature’s group, retaining first-signature and within-group arrival order.

**Invariant and cost:** Words share a signature exactly when their character multiplicities agree. For C total characters, n words and bounded 26-letter signatures, expected O(C+26n) time and O(C+26n) signature/result storage under the checked length bounds. The result contains original words, not sorted replacements.

**Additional cases:** ["ab","ba","a"] → [["ab","ba"],["a"]]; [""] → [[""]].

## 5. Posts in a time window

Sorted timestamps: count posts in [t1, t2) with two lower bounds. Count all entries in the half-open value interval [lo,hi). Include every duplicate at lo; exclude every duplicate at hi. Equal endpoints return zero; reject lo>hi.

**Contract:** `int count(int[] sorted,int lo,int hi)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted times, [100, 200)
Input: [20, 75, 100, 130, 155, 199, 200, 260], 100, 200
Verified expected output: 4
```

**Executable call:**

```java
W08CatalogueReference.count(new int[]{20,75,100,130,155,199,200,260},100,200)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The half-open interval [100,200) includes [100, 130, 155, 199]; duplicates are retained and the upper endpoint is excluded.

**Algorithm:** Find the first position at least lo and first at least hi; their difference counts the half-open interval [lo,hi). Equal endpoints return zero. The adapter copies/validates the source before querying.

**Invariant and cost:** Each lower-bound search preserves its partition invariant. Numeric one-call cost is O(n+log(n+1)) time and O(n) space; retained-index queries are O(log(n+1)). For words, add validation of total characters and string-comparison character costs. Duplicates count as separate entries.

**Additional cases:** sorted=[10,10,20,30,30],range [10,30) → 3; [20,20) → 0; empty input → 0.

## 6. Top trends

k most frequent tags (bucket sort by frequency, or a heap). Count distinct integer tag/typo IDs and return k IDs with greatest frequency. Return in decreasing frequency; ties may be in any order, and any tied choice at the cutoff is valid for this lab. k=0 returns empty; reject k<0 or k>distinct count.

**Contract:** `int[] topKLinear(int[] ids,int k)`

**Input limits and conventions:** Nonnull n<=100000 integer IDs;0<=k<=distinct. Return k distinct IDs in nonincreasing frequency;ties arbitrary,including cutoff ties. Count every occurrence. Expected linear bucket method.

### Worked example

```text
tag ids, k=3
Input: [3, 1, 3, 5, 1, 3, 7, 5, 5, 3], 3
Expected output, independently verified: [3, 5, 1]
```

**Executable call:**

```java
Week10ProjectReference.topKLinear(new int[]{3,1,3,5,1,3,7,5,5,3},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** ID frequencies are {1: 2, 3: 4, 5: 3, 7: 1}; select 3 IDs from the largest frequencies. Tied order and tied cutoff choices may differ.

**Algorithm:** Count ID frequencies, place each distinct ID into its frequency bucket, then scan buckets from high to low until k IDs have been emitted. No sort of the ID set is needed.

**Invariant and cost:** Every emitted ID has frequency at least that of any later bucket. Expected O(n+d) time and O(n+d+k) storage including buckets and output. Frequencies are nonincreasing; within a tied bucket and at a tied cutoff, any choice/order is valid.

**Additional cases:** ids=[1,1,2,2,3],k=2 →[1,2] or [2,1]; k=0 →[]; k=4 →IllegalArgumentException.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

hashtag trend summaries; threaded discussion analytics; grouped text indexes. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
