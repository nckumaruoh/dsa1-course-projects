# StepUp: Fitness Tracker Coach

Smartwatches record steps and heart rate every minute. A coaching app finds streaks, best weeks and fair goals.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Daily steps array; per-minute heart-rate array; workout durations.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Favourite activity | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Exactly 60 minutes | 4 | Week 4 / CO2 | `W04CatalogueReference.countSubsets` |
| Best streak above goal | 5 | Week 5 / CO3 | `W05CatalogueReference.bestInterval` |
| Best 7-day week | 7 | Week 6 / CO3 | `Week7ProjectReference.bestWindow` |
| A fair daily goal | 9 | Week 8 / CO4 | `Week9ProjectReference.max_goal` |
| Peak heart rate | 12 | Week 12 / CO6 | `Week12ProjectReference.slidingMax` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Favourite activity

Count sessions per activity type (count array); report the most frequent. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [2, 1, 2, 3, 2, 1, 1, 2]
Verified expected: most frequent=2 (count 4), distinct=3
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{2,1,2,3,2,1,1,2})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Counting each distinct ID gives {1: 3, 2: 4, 3: 1}; a frequency tie chooses the smaller ID.

**Algorithm:** Count all occurrences, then retain the highest-frequency ID, choosing the smaller numeric ID on ties. The hash implementation supports sparse IDs; frequentCountArray supplies the explicit bounded-universe comparison.

**Invariant and cost:** Processed counts are exact. The winning count/ID pair is updated whenever its ordering improves. Expected O(n) hashing time and O(d) entries; the count-array alternative takes O(n+U) time and O(U) space, including initialization.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Exactly 60 minutes

Count ways to pick workouts (each at most once) totalling exactly 60 minutes (backtracking). Each index may be used at most once; equal values at distinct indices are separate choices. Count exact-target index subsets.

**Contract:** `long countSubsets(int[] values,int target)`

**Input limits and conventions:** At most60 one-use indices with nonnegative values0..5000; target0..5000. Equal-valued indices are distinct. The empty subset is one target0 choice; unprocessed zero-valued indices still create additional choices. Use checked long counts; do not stop recursion early at remain0 while zeros remain.

### Worked example

```text
workout minutes, target 60
Input: [20, 30, 10, 40, 30], 60
Verified expected: 4
```

**Executable call:**

```java
W04CatalogueReference.countSubsets(new int[]{20,30,10,40,30},60)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The exact-target index subsets are [(0, 1, 2), (0, 3), (1, 4), (0, 2, 4)]; equal values at different indices are distinct choices.

**Algorithm:** Start counts[0]=1 and process each indexed value once. Visit target totals downward, adding counts[t-value] into counts[t]. Downward iteration prevents the current index being reused; a zero value correctly doubles existing counts.

**Invariant and cost:** After each value, counts[t] equals the number of processed-index subsets summing to t. O(n(T+1)) time plus table initialization, O(T+1) space. Equal values at different indices remain different choices.

**Additional cases:** [2,2],target=2 → 2; [],target=0 → 1.

## 3. Best streak above goal

Maximum-sum run of (steps − goal). Return the maximum sum of a nonempty contiguous interval; also report one pair of zero-based inclusive endpoints.

**Contract:** `Interval bestInterval(long[] values); optional long bestSum(long[] values) adapter; record Interval(long value,int left,int right). Return endpoints with the scalar result.`

**Input limits and conventions:** 1..1000 signed values, each abs<=10^9. Nonempty contiguous interval; return sum AND inclusive endpoint witness. Any optimal witness accepted; teaching reference retains first encountered strict improvement. Reject empty input; all-negative must return a negative best.

### Worked example

```text
steps − 9000
Input: [-800, 1450, -2700, 3100, 800, 6020, -4700]
Verified expected: "best=9920 over indices 3..5"
```

**Executable call:**

```java
W05CatalogueReference.bestInterval(new long[]{-800L,1450L,-2700L,3100L,800L,6020L,-4700L})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Enumerating every nonempty interval finds sum 9920 on inclusive indices 3..5, containing [3100, 800, 6020].

**Algorithm:** Track the best nonempty interval ending at the current index. Either extend the previous ending interval or start at the current value; update the best overall sum and inclusive endpoints only on strict improvement.

**Invariant and cost:** The ending state is optimal among intervals ending here, so the global best is optimal among all processed intervals. O(n) time and O(1) auxiliary space excluding the constant-size Interval result. All-negative input returns its largest value; it does not select an empty interval.

**Additional cases:** [-2,-1] → best=-1 at 1..1; [4] → best=4 at 0..0.

## 4. Best 7-day week

Highest total over any 7 consecutive days (fixed window). Exactly k consecutive positions; return [maximum sum, earliest starting index] when tied. Require 1≤k≤n.

**Contract:** `long[] bestWindow(int[] values,int k)`

**Input limits and conventions:** Nonnull signed int array1..100000;1<=k<=n. Return long[2]={maximum sum,EARLIEST zero-based start};all-negative windows valid. Preserve source;checked long addition/subtraction;fixed width only.

### Worked example

```text
steps over 10 days, k=7
Input: [8200, 10450, 6300, 12100, 9800, 15020, 4300, 7600, 11200, 9900], 7
Verified expected: "sum=69920 starting at index 3"
```

**Executable call:**

```java
Week7ProjectReference.bestWindow(new int[]{8200,10450,6300,12100,9800,15020,4300,7600,11200,9900},7)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Complete width-7 window totals are [66170, 65570, 66320, 69920]; the first maximum gives sum/start [69920, 3].

**Algorithm:** Maintain the sum of exactly k consecutive values: add each entering value and subtract the departing value. Compare only complete windows. Update the best start only on strict improvement to keep the earliest tie.

**Invariant and cost:** The running sum equals the current complete window. O(n) time and O(1) workspace, excluding the two-element result. Signed values are valid; initializing from a real window rather than zero handles all-negative input.

**Additional cases:** [1,2,3],k=2 → [5,1]; [5],k=1 → [5,0].

## 5. A fair daily goal

Largest daily goal met on at least 5 of 7 days (binary search on the goal). Choose largest daily step threshold achieved on at least requiredDays of the recorded days. Count days with steps≥goal; do not alter measurements. Return −1 if more days are required than exist.

**Contract:** `long max_goal(int[] steps,int requiredDays)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE step values<=10^9,requiredDays>=1. Largest threshold reached on at least requiredDays;days>n ->-1. Repeated values count separate days;preserve measurements.

### Worked example

```text
steps, at least 5 days
Input: [8200, 10450, 6300, 12100, 9800, 15020, 4300], 5
Expected output (independently verified): 8200
```

**Executable call:**

```java
Week9ProjectReference.max_goal(new int[]{8200,10450,6300,12100,9800,15020,4300},5)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Daily counts sorted descending are [15020, 12100, 10450, 9800, 8200, 6300, 4300]; the 5th-largest count is the highest threshold met on enough days.

**Algorithm:** Binary-search the largest daily threshold achieved on at least requiredDays entries. Count qualifying entries for each tested threshold; keep the upper feasible boundary.

**Invariant and cost:** Meeting a threshold implies meeting every smaller one. O(n log(M+1)) time and O(1) extra space, including validation. The requested days are not required to be consecutive. Return -1 when requiredDays exceeds the number of entries.

**Additional cases:** [8,10,6],2 →8; [8,8,6],2 →8; requiredDays4 →−1.

## 6. Peak heart rate

Rolling maximum heart rate over 3-minute windows (monotonic deque). Return maximum of every complete contiguous window of k observations, in order. Require1≤k≤n. Keep windows within one account/series and in original observation order; the catalogue sample represents one series.

**Contract:** `int[] slidingMax(int[] values,int k)`

**Input limits and conventions:** Nonnull n1..100000 int readings;1<=k<=n;complete windows only. Retain newer equal maxima,expire indices. Per-account streams must be grouped first;sample is one stream.

### Worked example

```text
bpm, k=3
Input: [72, 80, 95, 110, 104, 98, 120, 90], 3
Verified expected: [95, 110, 110, 110, 120, 120]
```

**Executable call:**

```java
Week12ProjectReference.slidingMax(new int[]{72,80,95,110,104,98,120,90},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The maxima of every complete width-3 slice are [95, 110, 110, 110, 120, 120]; no partial window is emitted.

**Algorithm:** Maintain a deque of candidate indices. Remove expired indices from its front and values no larger than the incoming value from its back. The front is the maximum for a complete window.

**Invariant and cost:** Deque indices increase and their values strictly decrease. Each index enters/leaves once: O(n) time and O(k) deque workspace, plus O(n-k+1) output. Store indices, not just values, so expiration is correct.

**Additional cases:** [5,5,5],k2 →[5,5]; [1,2,3],k1 →[1,2,3]; k3 →[3]; k0 or k>n →reject.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

step tracking; training streak summaries; activity-window coaching. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
