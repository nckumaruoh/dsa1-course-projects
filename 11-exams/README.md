# ExamDesk: Results and Evaluation

Exam cells take attendance, distribute scripts to evaluators, catch copying and publish ranked results.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Students (roll, marks per subject); answer strings; script bundles.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Duplicate attendance | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Grace marks | 4 | Week 4 / CO2 | `W04CatalogueReference.minCoins` |
| Marks in any roll range | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| Copying check | 7 | Week 6 / CO3 | `Week7ProjectReference.lcs` |
| Evaluator load | 9 | Week 8 / CO4 | `Week9ProjectReference.min_capacity` |
| Rank list | 10 | Week 9 / CO5 | `Week10ProjectReference.rank` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Duplicate attendance

Roll numbers marked present twice (count array). Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [101, 102, 103, 101, 104, 102]
Verified expected: repeats=2, distinct=4, first repeat=101
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"101","102","103","101","104","102"})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Grace marks

Fewest grace 'units' (1, 2, 5 marks) to reach the pass mark exactly (coin change). Positive denominations with unlimited reuse; return fewest coins for exact amount, or −1 if impossible.

**Contract:** `int minCoins(int[] coins,int amount)`

**Input limits and conventions:** Up to1000 positive integer denominations; amount0..5000. Unlimited reuse; amount0 returns0; unreachable returns-1. Use iterative tabulation at full amount to avoid a long Java call chain. Zero/negative denominations are invalid.

### Worked example

```text
units, need 13
Input: [1, 2, 5], 13
Verified expected: 4
```

**Executable call:**

```java
W04CatalogueReference.minCoins(new int[]{1,2,5},13)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use best amount recurrence and tabulate; do not treat coins as one-use indices.

**Invariant and cost:** O(nA) time, O(A) storage. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** [2],amount=3 → -1; [1],amount=0 → 0.

## 3. Marks in any roll range

Prefix sums of marks; totals for roll ranges in O(1). Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
marks by roll order, queries
Input: [72, 85, 64, 91, 58, 77], [[0, 2], [3, 5]]
Verified expected: [221, 226]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{72L,85L,64L,91L,58L,77L},new int[][]{new int[]{0,2},new int[]{3,5}})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Build one frozen prefix with extra zero; validate bounds; compare each answer to a direct loop.

**Invariant and cost:** O(n+q) time; O(n) prefix plus O(q) output. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. Copying check

Longest common subsequence of two answer strings as a similarity score. Return the length of the longest ordered common subsequence; adjacency is not required. This is a sequence similarity measure, not a misconduct finding.

**Contract:** `int lcs(String a,String b)`

**Input limits and conventions:** Nonnull strings0..1000 Java characters each. Return ordered common SUBSEQUENCE length;adjacency not required. Character sequence only,no automatic tokenisation. O(mn) table time/space. A similarity score does not establish misconduct.

### Worked example

```text
answer A, answer B
Input: "abcbdacb", "abdcabcb"
Verified expected: 6
```

**Executable call:**

```java
Week7ProjectReference.lcs("abcbdacb","abdcabcb")
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use guided LC1143 prefix-table recurrence; do not label LCS as KMP or as a sliding window. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** "abcde","ace" → 3; "abc","def" → 0.

## 5. Evaluator load

Split script bundles (in order) among 3 evaluators minimising the maximum load. Positive indivisible batches in original order, partitioned into at most rounds contiguous groups. Minimise maximum group sum; no splitting/reordering.

**Contract:** `long min_capacity(int[] batches,int rounds)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE batches<=10^9,rounds>=1. Original order,indivisible items,at most rounds contiguous groups. Return long;rounds>n is allowed and returns max batch. Validate once,then run fits on validated data.

### Worked example

```text
bundles, 3 evaluators
Input: [42, 38, 51, 29, 47, 33, 60, 25], 3
Expected output (independently verified): 127
```

**Executable call:**

```java
Week9ProjectReference.min_capacity(new int[]{42,38,51,29,47,33,60,25},3)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use greedy groups≤rounds and first feasible capacity in [max,sum], long arithmetic. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [7,2,5,10,8],2 →18; rounds1 →32; rounds5 →10. [1000000000,1000000000,1000000000],1 →3000000000.

## 6. Rank list

Total desc, then maths desc (multi-key comparator). Sort students by total descending, then maths descending. Preserve original input order on complete ties. Return names, keeping all records, not numeric rank labels. Use aligned equal-length arrays and stable comparator with original index as final key.

**Contract:** `String[] rank(String[] names,int[] primary,int[] secondary,boolean secondaryDesc)`

**Input limits and conventions:** Aligned nonnull arrays,n<=100000;nonnull names. Primary DESC;secondary ASC cricket/DESC exams;full ties original index. Integer.compare avoids overflow.

### Worked example

```text
names; total desc, maths desc
Input: names=[Asha,Bala,Chitra,Dev], total=[410,425,410,398], maths=[88,90,95,99]. Source omits the names vector. Lab fixture explicitly uses [Asha,Bala,Chitra,Dev] in input order, consistent with the source expected names. Ranking keys and numeric inputs are unchanged.
Expected output, independently verified: ["Bala", "Chitra", "Asha", "Dev"]
```

**Executable call:**

```java
Week10ProjectReference.rank(new String[]{"Asha","Bala","Chitra","Dev"},new int[]{410,425,410,398},new int[]{88,90,95,99},true)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** See the checked Java implementation and regression suite.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** names=[A,B,C],primary=[10,10,10],secondary=[2,2,1]: cricket →[C,A,B]; exams →[A,B,C]. A and B retain source order on full ties.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

synthetic exam-result dashboards; stable rankings; evaluation batch scheduling. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
