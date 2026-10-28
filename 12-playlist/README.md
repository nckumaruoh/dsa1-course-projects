# TuneLoop: Music Playlist Engine

Streaming apps manage queues, 'recently played' and offline caches. Each of these maps to an array or linked structure.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Songs (id, duration, plays, artist); listening history as an array of ids.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Most played song | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Party elimination game | 3 | Week 3 / CO2 | `CoreBasics.winner` |
| Queue duration | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| At most two artists | 7 | Week 6 / CO3 | `Week7ProjectReference.longest` |
| Song at a given time | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Offline cache | 11 | Week 11 / CO6 | `Week11ProjectReference.simulate` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Most played song

Plays per song with a count array. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [4, 2, 4, 7, 9, 2, 4, 7]
Verified expected: most frequent=4 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{4,2,4,7,9,2,4,7})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Party elimination game

Josephus-style elimination round over a circle of songs (recurrence). Players/songs numbered 1..n form a circle. Count the current item as 1, remove the kth, then resume at the next. Return the last label.

**Contract:** `int winner(int n,int k)`

**Input limits and conventions:** Lab limits: 1≤n≤1000; 1≤k≤1000; iterative production method, bounded small recursive trace.

### Worked example

```text
n songs, skip k
Input: 7, 3
Verified expected: 4
```

**Executable call:**

```java
CoreBasics.winner(7,3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Zero-based recurrence J(1)=0, J(n)=(J(n−1)+k)%n; return J+1.

**Invariant and cost:** O(n) time/O(1) iterative space; recursive trace O(n) frames. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** n=5,k=2 → 3; n=1,k=3 → 1.

## 3. Queue duration

Total duration of any range of the queue with prefix sums. Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
durations (s), queries
Input: [210, 185, 240, 200, 260, 195], [[1, 3], [0, 5]]
Verified expected: [625, 1290]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{210L,185L,240L,200L,260L,195L},new int[][]{new int[]{1,3},new int[]{0,5}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Build one frozen prefix with extra zero; validate bounds; compare each answer to a direct loop.

**Invariant and cost:** O(n+q) time; O(n) prefix plus O(q) output. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. At most two artists

Longest stretch of history with at most 2 distinct artists. Return longest contiguous length using at most k distinct IDs; k≥0. Empty input or k=0 returns 0.

**Contract:** `int longest(int[] ids,int k)`

**Input limits and conventions:** Nonnull0..100000 signed int IDs;k>=0. Longest CONTIGUOUS length with at most k distinct IDs. Empty/k0 ->0. Preserve source;expected O(n) hash time,O(min(n,k)+1) map space.

### Worked example

```text
artist ids
Input: [3, 3, 1, 2, 1, 1, 2, 3, 3, 4], 2
Verified expected: 5
```

**Executable call:**

```java
Week7ProjectReference.longest(new int[]{3,3,1,2,1,1,2,3,3,4},2)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Track frequencies, shrink while too many distinct keys remain, and delete zero-frequency keys. LC904 is the k=2 version. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [1,2,1,3],k=2 → 3; [1,2],k=0 → 0.

## 5. Song at a given time

Which song plays at second t: lower bound on cumulative durations. Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target. For this catalogue lab, cumulative ends are inclusive and the requested second is 1-based: song i occupies (previousEnd,end[i]]. Return n for a second beyond the final end; report no song then.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
cumulative ends (s), t = 700
Input: [210, 395, 635, 835, 1095, 1290], 700
Verified expected output: 3
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{210,395,635,835,1095,1290},700)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use [lo,hi), hi=n; sorted[mid]<target advances lo to mid+1, otherwise hi=mid. Preserve sortedness precondition. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** cumulativeEnds=[210,395]: second 210 → index0; second211 → index1; second396 → boundary2, no song.

## 6. Offline cache

Keep the last C played songs offline (LRU). Use a positive-capacity LRU resident set. Each request first looks up the ID, counts a hit only if present, promotes on hit, and loads on miss; evict the least-recent ID when needed. Return hit count/total, ratio and final MRU→LRU IDs.

**Contract:** `String simulate(int[] requests,int capacity); Cache.get(key),put(key,value),order(),integrity(). Values use the nonnegative ID itself for this fixture.`

**Input limits and conventions:** See the checked Java implementation and regression suite.

### Worked example

```text
plays, capacity 3
Input: [4, 2, 4, 7, 9, 2, 4, 7], 3
Verified expected output: hits=1 of 8, final order (most recent first) [7, 4, 2]
Hit ratio: 1/8
```

**Executable call:**

```java
Week11ProjectReference.simulate(new int[]{4,2,4,7,9,2,4,7},3)
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

music queue managers; party games; recently played caches. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
