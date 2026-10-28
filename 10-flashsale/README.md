# FlashCart: Flash-Sale Inventory

During a flash sale thousands of add-to-cart events hit in minutes. The store must avoid overselling and spot bots.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Events: user id, product id, quantity, timestamp; stock per product; coupon values.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Bot detection | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Fewest coupons | 4 | Week 4 / CO2 | `W04CatalogueReference.minCoins` |
| Units sold in any minute range | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| Bundle offers | 6 | Week 6 / CO3 | `W06CatalogueReference.pair` |
| Packing rate | 9 | Week 8 / CO4 | `Week9ProjectReference.min_rate` |
| Product page cache | 11 | Week 11 / CO6 | `Week11ProjectReference.simulate` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Bot detection

Users placing repeated orders (count array over user ids). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [7, 3, 7, 9, 7, 3, 11, 7]
Verified expected: repeats=4, distinct=4, first repeat=7
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"7","3","7","9","7","3","11","7"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The repeated encounters, in arrival order, are ['7', '7', '3', '7']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Fewest coupons

Fewest coupons of values {50, 100, 200} that make an exact discount (coin change). Positive denominations with unlimited reuse; return fewest coins for exact amount, or −1 if impossible.

**Contract:** `int minCoins(int[] coins,int amount)`

**Input limits and conventions:** Up to1000 positive integer denominations; amount0..5000. Unlimited reuse; amount0 returns0; unreachable returns-1. Use iterative tabulation at full amount to avoid a long Java call chain. Zero/negative denominations are invalid.

### Worked example

```text
coupon values, discount 450
Input: [50, 100, 200], 450
Verified expected: 3
```

**Executable call:**

```java
W04CatalogueReference.minCoins(new int[]{50,100,200},450)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Breadth-first search of reachable totals first reaches 450 after 3 coins; denomination reuse is allowed.

**Algorithm:** Tabulate the fewest coins for every amount. For total t, try each affordable denomination and extend the best answer for t-coin. Positive denominations allow unlimited reuse; unreachable totals keep an impossible sentinel.

**Invariant and cost:** Before processing t, all smaller amounts are final. For m denominations and amount A, time is O((m+1)(A+1)), including validation/table initialization; space is O(A+1). Amount zero returns zero; an unreachable positive amount returns -1.

**Additional cases:** [2],amount=3 → -1; [1],amount=0 → 0.

## 3. Units sold in any minute range

Prefix sums over units per minute. Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
units per minute, queries
Input: [120, 340, 560, 410, 220, 90], [[1, 3], [0, 5]]
Verified expected: [1310, 1740]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{120L,340L,560L,410L,220L,90L},new int[][]{new int[]{1,3},new int[]{0,5}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Direct inclusive slices [[340, 560, 410], [120, 340, 560, 410, 220, 90]] sum to [1310, 1740]; both query endpoints count.

**Algorithm:** Build a frozen prefix array with an extra leading zero. For an inclusive query [l,r], subtract prefix[l] from prefix[r+1]. The index must be rebuilt after source updates.

**Invariant and cost:** prefix[i] is the sum of the first i values. The convenience batch method takes O(n+q) time and O(n+q) space including its output; construction is O(n), each retained-index query O(1). Signed values and long totals are supported.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. Bundle offers

Two products whose prices sum exactly to the bundle price: sort and two pointers. Find any pair of distinct indices whose values sum to target; return a value pair, or [] if none. Output pair ordering is immaterial.

**Contract:** `int[] pair(int[] values,int target)`

**Input limits and conventions:** Nonnull0..1000 signed int values;target signed int. Any two DISTINCT positions;return their value pair (either order),or[] iff none. Preserve input via copied sort;long intermediate sum prevents int overflow. Equal values require two available occurrences.

### Worked example

```text
prices, bundle 1000
Input: [499, 650, 250, 750, 300, 820], 1000
Verified expected: 250 + 750 (one valid pair; others accepted)
```

**Executable call:**

```java
W06CatalogueReference.pair(new int[]{499,650,250,750,300,820},1000)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Valid value pairs from distinct positions include [[250, 750]]; either order is accepted, and an absent pair is []. The test accepts any valid witness rather than requiring one particular pair.

**Algorithm:** Sort a copy, then use low/high pointers at distinct positions. A sum below target discards the low endpoint; a sum above target discards the high endpoint. Return the values of any matching pair.

**Invariant and cost:** Sorted order proves the discarded endpoint cannot work with any retained partner. O(n log n) sorting time plus O(n) validation/scan; O(n) copied storage. A long intermediate sum avoids signed-int overflow, and equal values require two positions.

**Additional cases:** [2,7,4,5],target=9 → [2,7] or [4,5]; [3],target=6 → [].

## 5. Packing rate

Minimum orders packed per hour so every warehouse finishes within 6 hours. Sequentially process zones/warehouses. A zone of w takes ceil(w/rate) whole hours; spare fractional time cannot be shared between zones. Sum these times≤hours. Return minimum positive rate or −1 if hours<zone count. This explicit lab scheduling policy disambiguates the catalogue example; it is not parallel independent completion.

**Contract:** `long min_rate(int[] work,int hours)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE workloads<=10^9,hours>=1. SEQUENTIAL zones charged ceil(work/rate) each;no fractional-hour transfer or parallel-zone assumption. Return-1 if hours<n;otherwise positive minimum rate. Long time sums.

### Worked example

```text
orders per warehouse, 6 hours
Input: [120, 45, 300, 80], 6
Expected output (independently verified): 120
```

**Executable call:**

```java
Week9ProjectReference.min_rate(new int[]{120,45,300,80},6)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** At rate 120, per-zone whole-hour charges are [1, 1, 3, 1], total 6 <= 6. Every lower rate is rejected by exhaustive search.

**Algorithm:** Reject hours below the number of zones. Otherwise binary-search rates 1..max(work), summing ceil(work/rate) whole hours for every sequential zone. Keep the first rate whose sum fits the budget.

**Invariant and cost:** The total required hours is nonincreasing in rate. O(n log(M+1)) time and O(1) workspace, with long sums and overflow-safe ceiling division. Fractional time does not transfer between zones.

**Additional cases:** [30,11,23,4,20],6 →23; [5,7],1 →−1; [5,7],2 →7.

## 6. Product page cache

Cache product pages with LRU eviction; report the hit ratio. Use a positive-capacity LRU resident set. Each request first looks up the ID, counts a hit only if present, promotes on hit, and loads on miss; evict the least-recent ID when needed. Return hit count/total, ratio and final MRU→LRU IDs.

**Contract:** `String simulate(int[] requests,int capacity); Week11ProjectReference.LRUCache.get(key), put(key,value), order(), integrity(). Values use the nonnegative ID itself for this fixture.`

**Input limits and conventions:** Nonnull 0..200000 requests, capacity 1..3000, keys 0..10000. Cache values are 0..100000; -1 denotes a miss. Empty replay has ratio N/A. order() returns a copied MRU-to-LRU key array. integrity() validates reciprocal links and map/list agreement.

### Worked example

```text
page ids, capacity 3
Input: [1, 2, 3, 1, 4, 2, 1, 5, 1], 3
Verified expected output: hits=3 of 9, final order (most recent first) [1, 5, 2]
Hit ratio: 3/9
```

**Executable call:**

```java
Week11ProjectReference.simulate(new int[]{1,2,3,1,4,2,1,5,1},3)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** A plain recency list produces 3 hits and final most-to-least-recent keys [1, 5, 2]; a hit moves its key to the front.

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

inventory reservation simulations; checkout caches; dispatch capacity planning. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
