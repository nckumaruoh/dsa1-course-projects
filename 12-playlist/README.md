# TuneLoop: Music Playlist Engine

Streaming apps manage queues, 'recently played' and offline caches. Each of these maps to an array or linked structure.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

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

**Example reasoning:** Counting each distinct ID gives {2: 2, 4: 3, 7: 2, 9: 1}; a frequency tie chooses the smaller ID.

**Algorithm:** Count all occurrences, then retain the highest-frequency ID, choosing the smaller numeric ID on ties. The hash implementation supports sparse IDs; frequentCountArray supplies the explicit bounded-universe comparison.

**Invariant and cost:** Processed counts are exact. The winning count/ID pair is updated whenever its ordering improves. Expected O(n) hashing time and O(d) entries; the count-array alternative takes O(n+U) time and O(U) space, including initialization.

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

**Example reasoning:** Counting the current item as one removes [3, 6, 2, 7, 5, 1], leaving label 4.

**Algorithm:** Maintain the survivor in zero-based coordinates: J(1)=0 and J(size)=(J(size-1)+k)%size. Iterate sizes 2..n, then return J+1 to restore the required labels 1..n.

**Invariant and cost:** After each iteration, result is the survivor for that circle size. O(n) time and O(1) extra space. This loop implements the recurrence without n stack frames.

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

**Example reasoning:** Direct inclusive slices [[185, 240, 200], [210, 185, 240, 200, 260, 195]] sum to [625, 1290]; both query endpoints count.

**Algorithm:** Build a frozen prefix array with an extra leading zero. For an inclusive query [l,r], subtract prefix[l] from prefix[r+1]. The index must be rebuilt after source updates.

**Invariant and cost:** prefix[i] is the sum of the first i values. The convenience batch method takes O(n+q) time and O(n+q) space including its output; construction is O(n), each retained-index query O(1). Signed values and long totals are supported.

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

**Example reasoning:** Enumerating all intervals with at most 2 distinct IDs finds length 5, for example indices 2..6.

**Algorithm:** Maintain a frequency map for a contiguous window. Add the entering ID; while the map has more than k distinct IDs, remove values from the left and erase zero counts. Record the largest valid length.

**Invariant and cost:** After shrinking, the map exactly describes a window with at most k distinct IDs. Expected O(n) time; up to O(min(n,k+1)) map entries while adding/shrinking. k=0 yields zero.

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

**Example reasoning:** A direct left-to-right scan first finds a value at least 700 at index 3; n=6 is a valid end sentinel.

**Algorithm:** Copy and validate the sorted array, then search a half-open unclassified interval [l,r). If a[mid]<target, move l past mid; otherwise retain mid by setting r=mid. Return the meeting boundary.

**Invariant and cost:** Values before l are below target and those at/after r are at least target. A one-call adapter is O(n+log(n+1)) time and O(n) frozen-copy space. Reuse the constructed index for O(log(n+1)) numeric queries. The valid return n must not be dereferenced.

**Additional cases:** cumulativeEnds=[210,395]: second 210 → index0; second211 → index1; second396 → boundary2, no song.

## 6. Offline cache

Keep the last C played songs offline (LRU). Use a positive-capacity LRU resident set. Each request first looks up the ID, counts a hit only if present, promotes on hit, and loads on miss; evict the least-recent ID when needed. Return hit count/total, ratio and final MRU→LRU IDs.

**Contract:** `String simulate(int[] requests,int capacity); Week11ProjectReference.LRUCache.get(key), put(key,value), order(), integrity(). Values use the nonnegative ID itself for this fixture.`

**Input limits and conventions:** Nonnull 0..200000 requests, capacity 1..3000, keys 0..10000. Cache values are 0..100000; -1 denotes a miss. Empty replay has ratio N/A. order() returns a copied MRU-to-LRU key array. integrity() validates reciprocal links and map/list agreement.

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

**Example reasoning:** A plain recency list produces 1 hits and final most-to-least-recent keys [7, 4, 2]; a hit moves its key to the front.

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

music queue managers; party games; recently played caches. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
