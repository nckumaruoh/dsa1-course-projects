# TypeRight: Spell Checker and Autocomplete

Keyboards and search boxes autocomplete as you type and suggest corrections, mixing sorted search with string algorithms.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Sorted dictionary of words; typed words; letter frequencies.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Letter frequencies | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Edit distance | 4 | Week 4 / CO2 | `W04CatalogueReference.distance` |
| Merge two dictionaries | 6 | Week 6 / CO3 | `W06CatalogueReference.merge` |
| Similarity score | 7 | Week 6 / CO3 | `Week7ProjectReference.lcs` |
| Autocomplete range | 8 | Week 7 / CO4 | `W08CatalogueReference.count` |
| Most common typos | 10 | Week 9 / CO5 | `Week10ProjectReference.topKLinear` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Letter frequencies

Count letters (int[26]); most frequent letter in a text. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. Empty input has no most-frequent ID and is outside this report contract.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [4, 0, 4, 19, 4, 0, 18]
Verified expected: most frequent=4 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{4,0,4,19,4,0,18})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Edit distance

Minimum insertions, deletions and replacements turning a typo into a word (memo table). Minimum single-character insertions, deletions and substitutions to transform from into to. Each costs one; matching characters cost zero; no transposition operation.

**Contract:** `int distance(String from,String to)`

**Input limits and conventions:** Each string length0..1000 Java characters. Insertion, deletion and substitution each cost1; matching costs0. No transposition. Use iterative row/table evaluation at full size rather than1000+ recursive frames.

### Worked example

```text
typo, word
Input: "recieve", "receive"
Verified expected: 2
```

**Executable call:**

```java
W04CatalogueReference.distance("recieve","receive")
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** State (i,j) for suffixes; base empty suffix length; memoise all three edit choices. Tabulate for large strings.

**Invariant and cost:** O(ab) time/storage or O(min(a,b)) row storage with tabulation. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** "ab","ac" → 1; "","abc" → 3.

## 3. Merge two dictionaries

Merge the sorted base dictionary with a user’s sorted custom words. Inputs are nondecreasing; return every value in sorted order including duplicates. Preserve within-input order and choose left first on equal keys.

**Contract:** `int[] merge(int[] left,int[] right)`

**Input limits and conventions:** Nonnull sorted integer arrays;combined n<=1000. Preserve all duplicates and source arrays;fresh output. Within-input order retained,left first on equality. Numeric equal-key stability is unobservable;use tagged-event faculty model to demonstrate it.

### Worked example

```text
word ids (base), (custom)
Input: [3, 8, 15, 21], [5, 8, 30]
Verified expected: [3, 5, 8, 8, 15, 21, 30]
```

**Executable call:**

```java
W06CatalogueReference.merge(new int[]{3,8,15,21},new int[]{5,8,30})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Two read indices plus output index; append the remaining suffix after either input ends.

**Invariant and cost:** O(a+b) time; O(a+b) returned storage. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

**Additional cases:** [1,3],[2,3] → [1,2,3,3]; [],[] → [].

## 4. Similarity score

LCS length between two words as a similarity score. Return the length of the longest ordered common subsequence; adjacency is not required. This is a sequence similarity measure, not a misconduct finding.

**Contract:** `int lcs(String a,String b)`

**Input limits and conventions:** Nonnull strings0..1000 Java characters each. Return ordered common SUBSEQUENCE length;adjacency not required. Character sequence only,no automatic tokenisation. O(mn) table time/space. A similarity score does not establish misconduct.

### Worked example

```text
a, b
Input: "klconnect", "kluniversity"
Verified expected: 5
```

**Executable call:**

```java
Week7ProjectReference.lcs("klconnect","kluniversity")
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use guided LC1143 prefix-table recurrence; do not label LCS as KMP or as a sliding window. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** "abcde","ace" → 3; "abc","def" → 0.

## 5. Autocomplete range

Words with a prefix form one block: two lower bounds. Count all entries in the half-open value interval [lo,hi). Include every duplicate at lo; exclude every duplicate at hi. Equal endpoints return zero; reject lo>hi. Words and prefix use lowercase English letters in Java lexicographic order. For prefix cha, the successor boundary is chb. For an all-z prefix no finite successor exists, so the upper boundary is n; an empty prefix selects every word.

**Contract:** `int count(String[] sorted,String lo,String hi)`

**Input limits and conventions:** Word lab:nonnull sorted lowercase strings,n<=100000,each length<=100000,total characters<=1000000;duplicates/empty strings valid. Keys/prefix lowercase,empty prefix selects all. All-z prefix upper bound=n. Query compares strings,not O(1) integers;prefix successor costs O(P) storage.

### Worked example

```text
sorted words, prefix 'cha' as [lb('cha'), lb('chb'))
Input: ["bread", "chai", "chakli", "chana", "cheese", "curd"], "cha", "chb"
Verified expected output: 3
```

**Executable call:**

```java
W08CatalogueReference.count(new String[]{"bread","chai","chakli","chana","cheese","curd"},"cha","chb")
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Compute lower(hi)−lower(lo), not a first/last matching-index scan. Both bounds may validly equal n. Compute a general lowercase prefix successor by incrementing the rightmost non-z letter and truncating after it. Compare strings with compareTo; validate lexicographic order once. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** sorted=[a,z,zz],prefix=z → bounds[1,3), count2; prefix="" → bounds[0,3), count3; prefix=b → count0.

## 6. Most common typos

Top-k most frequent typo ids. Count distinct integer tag/typo IDs and return k IDs with greatest frequency. Return in decreasing frequency; ties may be in any order, and any tied choice at the cutoff is valid for this lab. k=0 returns empty; reject k<0 or k>distinct count.

**Contract:** `int[] topKLinear(int[] ids,int k)`

**Input limits and conventions:** Nonnull n<=100000 integer IDs;0<=k<=distinct. Return k distinct IDs in nonincreasing frequency;ties arbitrary,including cutoff ties. Count every occurrence. Expected linear bucket method.

### Worked example

```text
typo ids, k=2
Input: [5, 2, 5, 8, 2, 5, 9, 2, 2], 2
Expected output, independently verified: [2, 5]
```

**Executable call:**

```java
Week10ProjectReference.topKLinear(new int[]{5,2,5,8,2,5,9,2,2},2)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** See the checked Java implementation and regression suite.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** ids=[1,1,2,2,3],k=2 →[1,2] or [2,1]; k=0 →[]; k=4 →IllegalArgumentException.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

dictionary search; autocomplete candidate ranking; spelling-distance tools. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
