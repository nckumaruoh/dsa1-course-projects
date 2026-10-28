# LifeLink: Blood Bank Inventory

Blood units expire. Banks issue the oldest compatible unit first and must find units across centres fast.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Units (group, collection day, expiry day) per centre; requests.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Most requested group | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Exact volume | 4 | Week 4 / CO2 | `W04CatalogueReference.countSubsets` |
| Units usable each day | 5 | Week 5 / CO3 | `W05CatalogueReference.apply` |
| Merge centre stocks | 6 | Week 6 / CO3 | `W06CatalogueReference.merge` |
| Units expiring soon | 8 | Week 7 / CO4 | `W08CatalogueReference.count` |
| Request queue | 12 | Week 12 / CO6 | `Week12ProjectReference.queueSim` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Most requested group

Requests per blood group id (count array). Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [0, 2, 0, 1, 0, 2, 3]
Verified expected: most frequent=0 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{0,2,0,1,0,2,3})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Counting each distinct ID gives {0: 3, 1: 1, 2: 2, 3: 1}; a frequency tie chooses the smaller ID.

**Algorithm:** Count all occurrences, then retain the highest-frequency ID, choosing the smaller numeric ID on ties. The hash implementation supports sparse IDs; frequentCountArray supplies the explicit bounded-universe comparison.

**Invariant and cost:** Processed counts are exact. The winning count/ID pair is updated whenever its ordering improves. Expected O(n) hashing time and O(d) entries; the count-array alternative takes O(n+U) time and O(U) space, including initialization.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Exact volume

Count ways to fulfil a request of exactly 800 ml from available bags (backtracking). Each index may be used at most once; equal values at distinct indices are separate choices. Count exact-target index subsets.

**Contract:** `long countSubsets(int[] values,int target)`

**Input limits and conventions:** At most60 one-use indices with nonnegative values0..5000; target0..5000. Equal-valued indices are distinct. The empty subset is one target0 choice; unprocessed zero-valued indices still create additional choices. Use checked long counts; do not stop recursion early at remain0 while zeros remain.

### Worked example

```text
bag volumes (ml), need 800
Input: [350, 450, 350, 250, 200], 800
Verified expected: 4
```

**Executable call:**

```java
W04CatalogueReference.countSubsets(new int[]{350,450,350,250,200},800)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The exact-target index subsets are [(0, 1), (1, 2), (0, 3, 4), (2, 3, 4)]; equal values at different indices are distinct choices.

**Algorithm:** Start counts[0]=1 and process each indexed value once. Visit target totals downward, adding counts[t-value] into counts[t]. Downward iteration prevents the current index being reused; a zero value correctly doubles existing counts.

**Invariant and cost:** After each value, counts[t] equals the number of processed-index subsets summing to t. O(n(T+1)) time plus table initialization, O(T+1) space. Equal values at different indices remain different choices.

**Additional cases:** [2,2],target=2 → 2; [],target=0 → 1.

## 3. Units usable each day

Each unit is usable [collection, expiry): difference array. Start n zero cells. Each [l,r,delta] changes zero-based inclusive l..r. Return final values; signed deltas allowed.

**Contract:** `long[] apply(int n,int[][] updates)`

**Input limits and conventions:** 0<=n<=1000;0..1000 updates [l,r,delta], zero-based inclusive0<=l<=r<n; delta is signed int. n0 allows no updates. Use long difference/reconstruction and checked arithmetic; preserve updates.

### Worked example

```text
days 0..6, units [from, to−1, 1]
Input: 7, [[0, 4, 1], [1, 2, 1], [2, 6, 1], [3, 5, 1]]
Verified expected: [1, 2, 3, 3, 3, 2, 1]
```

**Executable call:**

```java
W05CatalogueReference.apply(7,new int[][]{new int[]{0,4,1},new int[]{1,2,1},new int[]{2,6,1},new int[]{3,5,1}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** For each position, add every covering inclusive update; the resulting position values are [1, 2, 3, 3, 3, 2, 1].

**Algorithm:** For each inclusive update [l,r,delta], add delta at difference[l] and subtract it at difference[r+1]. A single prefix pass recovers the final value at every position.

**Invariant and cost:** The running difference prefix equals the sum of all updates covering the current position. O(n+u) time and O(n) additional/output storage for u updates. The extra difference slot makes an update ending at n-1 safe.

**Additional cases:** n=3,updates=[[0,1,2],[1,2,-1]] → [2,1,-1]; n=0,updates=[] → [].

## 4. Merge centre stocks

Merge two centres’ expiry-sorted lists. Inputs are nondecreasing; return every value in sorted order including duplicates. Preserve within-input order and choose left first on equal keys.

**Contract:** `int[] merge(int[] left,int[] right)`

**Input limits and conventions:** Nonnull sorted integer arrays;combined n<=1000. Preserve all duplicates and source arrays;fresh output. Within-input order retained,left first on equality. Numeric equal-key stability is unobservable;use tagged-event faculty model to demonstrate it.

### Worked example

```text
expiry days (centre A), (centre B)
Input: [3, 8, 11, 21], [10, 11, 14, 15]
Verified expected: [3, 8, 10, 11, 11, 14, 15, 21]
```

**Executable call:**

```java
W06CatalogueReference.merge(new int[]{3,8,11,21},new int[]{10,11,14,15})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Sorting the concatenated multiset independently gives [3, 8, 10, 11, 11, 14, 15, 21]; every duplicate occurrence must remain.

**Algorithm:** Validate both sorted arrays and merge them with two read pointers. Choose from the left on equality; once one array ends, copy the remaining suffix of the other.

**Invariant and cost:** The written prefix contains the smallest consumed multiset entries in sorted order. O(n+m) time, O(n+m) returned storage and O(1) additional workspace. Inputs are preserved and duplicate occurrences remain.

**Additional cases:** [1,3],[2,3] → [1,2,3,3]; [],[] → [].

## 5. Units expiring soon

Count units expiring in [10, 15) with two lower bounds. Count all entries in the half-open value interval [lo,hi). Include every duplicate at lo; exclude every duplicate at hi. Equal endpoints return zero; reject lo>hi.

**Contract:** `int count(int[] sorted,int lo,int hi)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted expiry days, window [10, 15)
Input: [3, 8, 10, 11, 11, 14, 15, 21], 10, 15
Verified expected output: 4
```

**Executable call:**

```java
W08CatalogueReference.count(new int[]{3,8,10,11,11,14,15,21},10,15)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The half-open interval [10,15) includes [10, 11, 11, 14]; duplicates are retained and the upper endpoint is excluded.

**Algorithm:** Find the first position at least lo and first at least hi; their difference counts the half-open interval [lo,hi). Equal endpoints return zero. The adapter copies/validates the source before querying.

**Invariant and cost:** Each lower-bound search preserves its partition invariant. Numeric one-call cost is O(n+log(n+1)) time and O(n) space; retained-index queries are O(log(n+1)). For words, add validation of total characters and string-comparison character costs. Duplicates count as separate entries.

**Additional cases:** sorted=[10,10,20,30,30],range [10,30) → 3; [20,20) → 0; empty input → 0.

## 6. Request queue

Serve requests FIFO with 2 technicians. Bucket t records arrivals at minute t, starting t=0. Enqueue in bucket order; simultaneous arrivals retain token order. Counters are identical and initially idle. Service is non-preemptive and takes the stated whole minutes. A counter finishing at t may take an arrival at t immediately. Use earliest available counter, lowest index on ties, and FIFO. Wait=start−arrival, excluding service duration. Drain all requests beyond the final bucket; report served count, total wait and longest wait.

**Contract:** `String queueSim(int[] arrivals,int serviceMinutes,int counters)`

**Input limits and conventions:** Nonnull T<=100000 buckets,nonnegative counts,total requests<=100000;service1..100000,counters1or2. FIFO,earliest available counter/lowest index ties;wait excludes service;drain past last bucket;long totals.

### Worked example

```text
requests per minute, 3-min handling, 2 technicians
Input: [1, 2, 0, 1, 2, 0], 3, 2
Verified expected: served=6, total wait=8 min, longest wait=3 min
```

**Executable call:**

```java
Week12ProjectReference.queueSim(new int[]{1,2,0,1,2,0},3,2)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** A minute-by-minute FIFO simulation produces waits [0, 0, 2, 1, 2, 3]; sum 8, maximum 3. Service time is excluded from wait.

**Algorithm:** Enqueue arrival times in a circular-array FIFO. For each token, choose the counter with earliest availability, breaking ties by lower counter index; start at max(arrival,availability) and advance that counter by the service duration.

**Invariant and cost:** FIFO order and each counter’s next-free time are preserved. O(T+Nc) time and O(N+c) storage for T arrival buckets, N requests and c<=2 counters. Wait excludes service, and requests are drained after the final arrival bucket.

**Additional cases:** One counter: served=6,totalWait=32,maxWait=11; two counters: served=6,totalWait=8,maxWait=3. Empty arrivals →served0,totalWait0,maxWait0. Invalid service/counter count rejected.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

synthetic stock summaries; request queues; inventory range updates. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
