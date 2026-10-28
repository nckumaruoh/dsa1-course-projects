# DashRoute: Food-Delivery Rider Batching

Delivery apps batch orders for riders, promise ETAs and decide how many riders to keep on shift.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

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

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

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

**Algorithm:** State (index,remaining); memoise include/skip and reuse cache across target queries.

**Invariant and cost:** O(nB) dense memo time/space plus O(n) frames. Explain your counted operation, state, base/undo rule and boundary convention.

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

**Algorithm:** Sort a copy; move low or high according to the sum. Use long for intermediate sums.

**Invariant and cost:** O(n log n) sort + O(n) scan; O(n) copy. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

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

**Algorithm:** Grow right; while sum reaches target, update length and shrink left. Negative values break this monotonic shrinking rule. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

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

**Algorithm:** Search first rate with sum ceil(work/rate)≤hours, in [1,max(work)]. Use long totals. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

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

**Algorithm:** See the checked Java implementation and regression suite.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [[1,2],[2,3]] →[[1,3]]; [[1,4],[2,3]] →[[1,4]]; [] →[]; [[3,1]] →IllegalArgumentException.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

courier load planning; last-mile dispatch simulations; work-shift batching. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
