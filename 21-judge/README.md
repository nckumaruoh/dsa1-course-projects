# CodeArena: Mini Online Judge

Programming contests grade submissions, rank participants and check code for copying.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Submissions (user, problem, time, verdict); source strings; problem points.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Verdict statistics | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Problem sets worth 100 | 4 | Week 4 / CO2 | `W04CatalogueReference.countSubsets` |
| Submissions per minute range | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| Plagiarism score | 7 | Week 6 / CO3 | `Week7ProjectReference.lcs` |
| Judge servers | 9 | Week 8 / CO4 | `Week9ProjectReference.min_capacity` |
| Expression tests | 12 | Week 12 / CO6 | `Week12ProjectReference.evaluate` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Verdict statistics

Counts per verdict id; the most common verdict. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. Empty input has no most-frequent ID and is outside this report contract.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [1, 0, 1, 2, 1, 0, 0, 1]
Verified expected: most frequent=1 (count 4), distinct=3
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{1,0,1,2,1,0,0,1})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Problem sets worth 100

Count sets of problems whose points total exactly 100 (backtracking). Each index may be used at most once; equal values at distinct indices are separate choices. Count exact-target index subsets.

**Contract:** `long countSubsets(int[] values,int target)`

**Input limits and conventions:** At most60 one-use indices with nonnegative values0..5000; target0..5000. Equal-valued indices are distinct. The empty subset is one target0 choice; unprocessed zero-valued indices still create additional choices. Use checked long counts; do not stop recursion early at remain0 while zeros remain.

### Worked example

```text
points, target 100
Input: [20, 30, 50, 40, 10, 60], 100
Verified expected: 5
```

**Executable call:**

```java
W04CatalogueReference.countSubsets(new int[]{20,30,50,40,10,60},100)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** State (index,remaining); include and skip counts add. A zero value doubles relevant index choices.

**Invariant and cost:** O(nT) memo time/space for bounded nonnegative targets. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** [2,2],target=2 → 2; [],target=0 → 1.

## 3. Submissions per minute range

Prefix sums over per-minute submissions. Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
submissions per minute, queries
Input: [14, 22, 35, 41, 28, 19], [[0, 2], [2, 5]]
Verified expected: [71, 123]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{14L,22L,35L,41L,28L,19L},new int[][]{new int[]{0,2},new int[]{2,5}})
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Build one frozen prefix with extra zero; validate bounds; compare each answer to a direct loop.

**Invariant and cost:** O(n+q) time; O(n) prefix plus O(q) output. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. Plagiarism score

LCS of token strings as a similarity measure. Return the length of the longest ordered common subsequence; adjacency is not required. This is a sequence similarity measure, not a misconduct finding.

**Contract:** `int lcs(String a,String b)`

**Input limits and conventions:** Nonnull strings0..1000 Java characters each. Return ordered common SUBSEQUENCE length;adjacency not required. Character sequence only,no automatic tokenisation. O(mn) table time/space. A similarity score does not establish misconduct.

### Worked example

```text
token strings
Input: "forijsum", "forjisum"
Verified expected: 7
```

**Executable call:**

```java
Week7ProjectReference.lcs("forijsum","forjisum")
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use guided LC1143 prefix-table recurrence; do not label LCS as KMP or as a sliding window. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** "abcde","ace" → 3; "abc","def" → 0.

## 5. Judge servers

Minimum capacity per server so queued batches (in order) finish within 3 rounds. Positive indivisible batches in original order, partitioned into at most rounds contiguous groups. Minimise maximum group sum; no splitting/reordering.

**Contract:** `long min_capacity(int[] batches,int rounds)`

**Input limits and conventions:** Nonnull1..100000 POSITIVE batches<=10^9,rounds>=1. Original order,indivisible items,at most rounds contiguous groups. Return long;rounds>n is allowed and returns max batch. Validate once,then run fits on validated data.

### Worked example

```text
batch sizes, 3 rounds
Input: [12, 30, 18, 25, 40, 15], 3
Expected output (independently verified): 55
```

**Executable call:**

```java
Week9ProjectReference.min_capacity(new int[]{12,30,18,25,40,15},3)
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Use greedy groups≤rounds and first feasible capacity in [max,sum], long arithmetic. Adapt the matching skill/problem starter or ProjectW09; create complete main, run catalogue and boundary cases. State monotonicity, safe bounds, shrinking measure and adjacent infeasible answer. Full tested faculty implementations:W09 Project Reference/private package. Choose your actual family;capacity grouping,ceil-hour rate,day threshold and spacing are different predicates. Common SLA model is support,not a compulsory additional project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** [7,2,5,10,8],2 →18; rounds1 →32; rounds5 →10. [1000000000,1000000000,1000000000],1 →3000000000.

## 6. Expression tests

Evaluate arithmetic test expressions (shunting-yard). Evaluate unsigned decimal literals with binary +,-,*,/, parentheses and spaces. Precedence */ before +−, left associativity; division truncates toward0. No unary operators in this explicit catalogue lab grammar. Reject bad syntax, zero divisor or long overflow; no eval. LC224 is a different grammar.

**Contract:** `long evaluate(String expression)`

**Input limits and conventions:** Nonnull expression length1..100000;unsigned decimal literals,binary+ - * /,spaces/parentheses. No unary signs. Left-associative,*/precedence,division toward0. Reject syntax/div0/long overflow. Numeric overload preserves catalogue;PulseLog variable overload separate.

### Worked example

```text
expression
Input: "3+4*(2-1)*5"
Verified expected: 23
```

**Executable call:**

```java
Week12ProjectReference.evaluate("3+4*(2-1)*5")
```

The actual result appears in [example-output.txt](example-output.txt). The test driver compares it with the declared expected result; pair-finding accepts any valid pair with two available occurrences.

**Algorithm:** Shunting-yard with value/operator stacks. Before pushing a binary operator apply higher/equal-precedence operators; on ) reduce until its (. Use checked long arithmetic.

**Invariant and cost:** O(L) time/O(L) stacks under fixed-width long arithmetic.

**Additional cases:** "8/3*3" →6; "2*(3+(4*5))" →46; "1/0", "1++2", "(1+2" →reject.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

toy expression evaluators; programming-practice dashboards; test-batch schedulers. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
