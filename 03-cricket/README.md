# ScoreLens: Ball-by-Ball Cricket Analyzer

Fantasy and broadcast apps compute strike rates, partnerships and momentum swings from ball-by-ball data seconds after each delivery.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Runs per ball and per over (arrays), wicket flags, batter ids.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Scoring-shot profile | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Ways to make a target | 4 | Week 4 / CO2 | `W04CatalogueReference.ways` |
| Best momentum phase | 5 | Week 5 / CO3 | `W05CatalogueReference.bestInterval` |
| Hottest 3-over spell | 7 | Week 6 / CO3 | `Week7ProjectReference.bestWindow` |
| When did they reach 50? | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Leaderboard | 10 | Week 9 / CO5 | `Week10ProjectReference.rank` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Scoring-shot profile

Count how often each run value (0, 1, 2, 4, 6) occurs with a count array; report the most frequent. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [1, 4, 1, 0, 6, 1, 4, 1, 2, 0]
Verified expected: most frequent=1 (count 4), distinct=5
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{1,4,1,0,6,1,4,1,2,0})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Counting each distinct ID gives {0: 2, 1: 4, 2: 1, 4: 2, 6: 1}; a frequency tie chooses the smaller ID.

**Algorithm:** Count all occurrences, then retain the highest-frequency ID, choosing the smaller numeric ID on ties. The hash implementation supports sparse IDs; frequentCountArray supplies the explicit bounded-universe comparison.

**Invariant and cost:** Processed counts are exact. The winning count/ID pair is updated whenever its ordering improves. Expected O(n) hashing time and O(d) entries; the count-array alternative takes O(n+U) time and O(U) space, including initialization.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Ways to make a target

Number of ordered ball sequences scoring exactly R from shots {1, 2, 4, 6} (memoised recursion). Positive choices may repeat; different orders count separately. Count sequences with exact sum.

**Contract:** `long ways(int[] choices,int target)`

**Input limits and conventions:** Distinct positive integer choices, at most60; target0..60 for the checked-long lab. Orders count separately and choices may repeat; target0 has one empty sequence. Amount-outer iteration counts ordered sequences. Reject zero/negative/duplicate choices; use Math.addExact.

### Worked example

```text
shots, target 8
Input: [1, 2, 4, 6], 8
Verified expected: 60
```

**Executable call:**

```java
W04CatalogueReference.ways(new int[]{1,2,4,6},8)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Classify sequences by their final shot/choice. Counts for remaining totals 0..8 are [1, 1, 2, 3, 6, 10, 19, 33, 60]; different orders remain distinct.

**Algorithm:** Set ways[0]=1. For each total t in increasing order, add ways[t-choice] for every affordable choice. Classifying by the last choice counts different orders separately and permits repeated choices.

**Invariant and cost:** All dependencies use smaller totals because choices are positive. O((m+1)(T+1)) time and O(T+m+1) storage including duplicate-choice validation. Checked long addition detects overflow; zero target has one empty sequence.

**Additional cases:** choices=[1,2],target=3 → 3 ([1,1,1],[1,2],[2,1]); target=0 → 1.

## 3. Best momentum phase

Overs where the batting side did best against a par of 8: maximum subarray of (runs − 8). Return the maximum sum of a nonempty contiguous interval; also report one pair of zero-based inclusive endpoints.

**Contract:** `Interval bestInterval(long[] values); optional long bestSum(long[] values) adapter; record Interval(long value,int left,int right). Return endpoints with the scalar result.`

**Input limits and conventions:** 1..1000 signed values, each abs<=10^9. Nonempty contiguous interval; return sum AND inclusive endpoint witness. Any optimal witness accepted; teaching reference retains first encountered strict improvement. Reject empty input; all-negative must return a negative best.

### Worked example

```text
runs − 8 per over
Input: [-2, 1, -4, 4, -1, 7, -5, 2]
Verified expected: "best=10 over indices 3..5"
```

**Executable call:**

```java
W05CatalogueReference.bestInterval(new long[]{-2L,1L,-4L,4L,-1L,7L,-5L,2L})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Enumerating every nonempty interval finds sum 10 on inclusive indices 3..5, containing [4, -1, 7].

**Algorithm:** Track the best nonempty interval ending at the current index. Either extend the previous ending interval or start at the current value; update the best overall sum and inclusive endpoints only on strict improvement.

**Invariant and cost:** The ending state is optimal among intervals ending here, so the global best is optimal among all processed intervals. O(n) time and O(1) auxiliary space excluding the constant-size Interval result. All-negative input returns its largest value; it does not select an empty interval.

**Additional cases:** [-2,-1] → best=-1 at 1..1; [4] → best=4 at 0..0.

## 4. Hottest 3-over spell

Most runs in any 3 consecutive overs. Exactly k consecutive positions; return [maximum sum, earliest starting index] when tied. Require 1≤k≤n.

**Contract:** `long[] bestWindow(int[] values,int k)`

**Input limits and conventions:** Nonnull signed int array1..100000;1<=k<=n. Return long[2]={maximum sum,EARLIEST zero-based start};all-negative windows valid. Preserve source;checked long addition/subtraction;fixed width only.

### Worked example

```text
runs per over, k=3
Input: [6, 9, 4, 12, 7, 15, 3, 10], 3
Verified expected: "sum=34 starting at index 3"
```

**Executable call:**

```java
Week7ProjectReference.bestWindow(new int[]{6,9,4,12,7,15,3,10},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Complete width-3 window totals are [19, 25, 23, 34, 25, 28]; the first maximum gives sum/start [34, 3].

**Algorithm:** Maintain the sum of exactly k consecutive values: add each entering value and subtract the departing value. Compare only complete windows. Update the best start only on strict improvement to keep the earliest tie.

**Invariant and cost:** The running sum equals the current complete window. O(n) time and O(1) workspace, excluding the two-element result. Signed values are valid; initializing from a real window rather than zero handles all-negative input.

**Additional cases:** [1,2,3],k=2 → [5,1]; [5],k=1 → [5,0].

## 5. When did they reach 50?

Over in which the cumulative score first reached 50: lower bound on the running total. Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
cumulative runs per over, target 50
Input: [6, 15, 19, 31, 38, 53, 56, 66], 50
Verified expected output: 5
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{6,15,19,31,38,53,56,66},50)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** A direct left-to-right scan first finds a value at least 50 at index 5; n=8 is a valid end sentinel.

**Algorithm:** Copy and validate the sorted array, then search a half-open unclassified interval [l,r). If a[mid]<target, move l past mid; otherwise retain mid by setting r=mid. Return the meeting boundary.

**Invariant and cost:** Values before l are below target and those at/after r are at least target. A one-call adapter is O(n+log(n+1)) time and O(n) frozen-copy space. Reuse the constructed index for O(log(n+1)) numeric queries. The valid return n must not be dereferenced.

**Additional cases:** sorted=[10,10,20],target=10 → 0; target=21 → 3 (end sentinel); sorted=[],target=10 → 0.

## 6. Leaderboard

Rank batters by runs (desc), then fewer balls (multi-key comparator). Sort batters by runs descending, then balls ascending. Preserve original input order on complete ties. Return names, keeping all records, not numeric rank labels. Use aligned equal-length arrays and stable comparator with original index as final key.

**Contract:** `String[] rank(String[] names,int[] primary,int[] secondary,boolean secondaryDesc)`

**Input limits and conventions:** Aligned nonnull arrays,n<=100000;nonnull names. Primary DESC;secondary ASC cricket/DESC exams;full ties original index. Integer.compare avoids overflow.

### Worked example

```text
names; runs desc, balls asc
Input: ["Rohit","Gill","Kohli","Pant"], runs [45,52,45,38], balls [30,41,28,22]
Expected output, independently verified: ["Gill", "Kohli", "Rohit", "Pant"]
```

**Executable call:**

```java
Week10ProjectReference.rank(new String[]{"Rohit","Gill","Kohli","Pant"},new int[]{45,52,45,38},new int[]{30,41,28,22},false)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Ordering (primary descending, secondary ascending, original index) gives source indices [1, 2, 0, 3]; complete ties preserve input order.

**Algorithm:** Sort original record indices by primary score descending, then the configured secondary direction, then original index. Read names through that ordering so the aligned source arrays remain unchanged.

**Invariant and cost:** The comparator defines a total order while original index preserves complete ties. O(n log n) comparisons and O(n) index/sort/result storage. Integer.compare avoids overflow; this returns names in order rather than numeric rank labels.

**Additional cases:** names=[A,B,C],primary=[10,10,10],secondary=[2,2,1]: cricket →[C,A,B]; exams →[A,B,C]. A and B retain source order on full ties.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

sports score analysis; tournament leaderboards; live match summaries. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
