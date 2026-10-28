# DashRoute: Food-Delivery Rider Batching

Delivery apps batch orders for riders, promise ETAs and decide how many riders to keep on shift.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Orders: id, ready time, restaurant, distance (km), bag weight; rider shifts.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate orders | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Fill a rider’s bag | 4 | Week 4 / CO2 | `W04CatalogueReference.bestTotal` |
| Pair short trips | 6 | Week 6 / CO3 | `W06CatalogueReference.pair` |
| Fastest 50 orders | 7 | Week 6 / CO3 | `Week7ProjectReference.minLength` |
| Minimum delivery rate | 9 | Week 8 / CO4 | `Week9ProjectReference.min_rate` |
| Merge rider shifts | 10 | Week 9 / CO5 | `Week10ProjectReference.mergeIntervals` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate orders

Order ids submitted twice by app retries (count array over ids). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [41, 42, 43, 42, 44, 45, 41]
Verified expected: repeats=2, distinct=5, first repeat=42
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"41","42","43","42","44","45","41"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The repeated encounters, in arrival order, are ['42', '41']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Fill a rider’s bag

Choose orders whose weights fit a 10 kg bag as fully as possible (subset sum with memo). Each index is one use; nonnegative values; return the largest subset sum ≤capacity. Empty subset is valid.

**Contract:** `int bestTotal(int[] values,int capacity)`

**Input limits and conventions:** Recursive/memo lab:0..30 one-use loads, each0..5000; capacity0..5000. Nonnegative values and empty input are valid. Clone input; keep one memo for the fixed input/capacity; count false states as cached results. Other full-size implementations must state their own checked bounds.

### Worked example

```text
bag weights, capacity 10 kg
Input: [3, 4, 5, 7, 2], 10
Verified expected: 10
```

**Executable call:**

```java
W04CatalogueReference.bestTotal(new int[]{3,4,5,7,2},10)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** One optimal selection uses indices (0, 3), values [3, 7], totalling 10 within capacity 10. Each index is used once.

**Algorithm:** CapacityPlanner.exact(target) memoizes reachable(index,remaining) for one copied input. Skip an index, or take it once when affordable. bestTotal scans target downward from the capacity and reuses that memo until it finds a reachable total. A cached false is a completed result.

**Invariant and cost:** Every recursive branch advances the index, preventing reuse. Across all target queries there are at most n(B+1) memo states. Including the target scan and zero-size cases, time is O((n+1)(B+1)); auxiliary space is O(n(B+1)+n) for dense memo and recursion. The checked n<=30 bound limits stack depth.

**Additional cases:** [2,3,5],capacity=4 → 3; [],capacity=0 → 0.

## 3. Pair short trips

Find two orders whose distances sum exactly to a rider’s planned 9 km loop: sort and two pointers. Find any pair of distinct indices whose values sum to target; return a value pair, or [] if none. Output pair ordering is immaterial.

**Contract:** `int[] pair(int[] values,int target)`

**Input limits and conventions:** Nonnull0..1000 signed int values;target signed int. Any two DISTINCT positions;return their value pair (either order),or[] iff none. Preserve input via copied sort;long intermediate sum prevents int overflow. Equal values require two available occurrences.

### Worked example

```text
distances, target 9 km
Input: [2, 7, 4, 5, 3, 8], 9
Verified expected: 2 + 7 (one valid pair; others accepted)
```

**Executable call:**

```java
W06CatalogueReference.pair(new int[]{2,7,4,5,3,8},9)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Valid value pairs from distinct positions include [[2, 7], [4, 5]]; either order is accepted, and an absent pair is []. The test accepts any valid witness rather than requiring one particular pair.

**Algorithm:** Sort a copy, then use low/high pointers at distinct positions. A sum below target discards the low endpoint; a sum above target discards the high endpoint. Return the values of any matching pair.

**Invariant and cost:** Sorted order proves the discarded endpoint cannot work with any retained partner. O(n log n) sorting time plus O(n) validation/scan; O(n) copied storage. A long intermediate sum avoids signed-int overflow, and equal values require two positions.

**Additional cases:** [2,7,4,5],target=9 → [2,7] or [4,5]; [3],target=6 → [].

## 4. Fastest 50 orders

Shortest stretch of minutes in which 50 orders arrived (variable window). Nonnegative counts and positive target. Return shortest nonempty contiguous length with sum≥target, or 0 if none.

**Contract:** `int minLength(int[] values,int target)`

**Input limits and conventions:** Nonnull0..100000 NONNEGATIVE int counts;positive int target. Shortest NONEMPTY contiguous interval with sum>=target,or0 if none. Zeros permitted;negative counts rejected. Preserve source;long running sum.

### Worked example

```text
orders per minute, target 50
Input: [8, 12, 5, 20, 9, 14, 3], 50
Verified expected: 5
```

**Executable call:**

```java
Week7ProjectReference.minLength(new int[]{8,12,5,20,9,14,3},50)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Exhaustively checking all intervals gives shortest length 5, witnessed by indices 0..4; zero means no qualifying interval.

**Algorithm:** Grow the right endpoint until a nonnegative-value window reaches target. While it still reaches target, record its length and remove the leftmost value. Continue growing after shrinking makes it too small.

**Invariant and cost:** Every discarded left endpoint has been checked at its shortest current right endpoint. Each index enters/leaves at most once: O(n) time, O(1) workspace. Zero counts are valid; negative counts break this argument and are rejected.

**Additional cases:** [2,3,1],target=4 → 2; [1,1],target=5 → 0.

## 5. Minimum delivery rate

Minimum deliveries per hour so every zone’s orders finish within 6 hours (binary search on the rate). Sequentially process zones/warehouses. A zone of w takes ceil(w/rate) whole hours; spare fractional time cannot be shared between zones. Sum these times≤hours. Return minimum positive rate or −1 if hours<zone count. This explicit lab scheduling policy disambiguates the catalogue example; it is not parallel independent completion.

**Contract:** `long min_rate(int[] work,int hours)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE workloads<=10^9,hours>=1. SEQUENTIAL zones charged ceil(work/rate) each;no fractional-hour transfer or parallel-zone assumption. Return-1 if hours<n;otherwise positive minimum rate. Long time sums.

### Worked example

```text
orders per zone, 6 hours
Input: [30, 11, 23, 4, 20], 6
Expected output (independently verified): 23
```

**Executable call:**

```java
Week9ProjectReference.min_rate(new int[]{30,11,23,4,20},6)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** At rate 23, per-zone whole-hour charges are [2, 1, 1, 1, 1], total 6 <= 6. Every lower rate is rejected by exhaustive search.

**Algorithm:** Reject hours below the number of zones. Otherwise binary-search rates 1..max(work), summing ceil(work/rate) whole hours for every sequential zone. Keep the first rate whose sum fits the budget.

**Invariant and cost:** The total required hours is nonincreasing in rate. O(n log(M+1)) time and O(1) workspace, with long sums and overflow-safe ceiling division. Fractional time does not transfer between zones.

**Additional cases:** [30,11,23,4,20],6 →23; [5,7],1 →−1; [5,7],2 →7.

## 6. Merge rider shifts

Merge overlapping shift windows to see when coverage exists. Each pair is a closed interval [start,end], start≤end. Merge when the next start≤current end, including shared endpoints. Return intervals by start, preserving exactly the covered union. Empty input returns empty; reversed intervals rejected. Treat the source sample as one room/resource. For multiple rooms, group by room ID first and never merge different rooms.

**Contract:** `int[][] mergeIntervals(int[][] intervals)`

**Input limits and conventions:** Nonnull n<=100000 valid CLOSED pairs;start<=end. Touching merges. Preserve rows;group multiple rooms first with mergeByRoom;never merge different rooms.

### Worked example

```text
shifts
Input: [[9, 13], [12, 15], [18, 22], [21, 23], [14, 16]]
Expected output, independently verified: [[9, 16], [18, 23]]
```

**Executable call:**

```java
Week10ProjectReference.mergeIntervals(new int[][]{new int[]{9,13},new int[]{12,15},new int[]{18,22},new int[]{21,23},new int[]{14,16}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Repeatedly uniting intersecting closed intervals, including touching endpoints, produces [[9, 16], [18, 23]]; gaps remain separate.

**Algorithm:** Copy and sort closed intervals by start. Keep a current covered interval; extend its end when the next start is at most that end, otherwise emit it and start a separate interval.

**Invariant and cost:** The output prefix is exactly the union of processed intervals in sorted disjoint form. O(n log n) time and O(n) copy/sort/result storage. Touching endpoints merge. Different rooms/resources must be grouped first, using mergeByRoom.

**Additional cases:** [[1,2],[2,3]] →[[1,3]]; [[1,4],[2,3]] →[[1,4]]; [] →[]; [[3,1]] →IllegalArgumentException.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

courier load planning; last-mile dispatch simulations; work-shift batching. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
