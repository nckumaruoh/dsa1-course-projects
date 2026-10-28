# NoteNest: Text Editor Core

Every editor and notes app needs word counts, find, diffs, autocorrect and bracket matching. Each is a data-structure exercise.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

## Project data and integration

Document as a string; paragraph word counts; line lengths.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Most used word | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Autocorrect distance | 4 | Week 4 / CO2 | `W04CatalogueReference.distance` |
| Words in any paragraphs | 5 | Week 5 / CO3 | `W05CatalogueReference.rangeSums` |
| Find all | 7 | Week 6 / CO3 | `Week7ProjectReference.count` |
| Jump to an offset | 8 | Week 7 / CO4 | `W08CatalogueReference.lower` |
| Bracket matching | 12 | Week 12 / CO6 | `Week12ProjectReference.validBrackets` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Most used word

Word frequencies over word ids. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [7, 3, 7, 1, 3, 7, 2]
Verified expected: most frequent=7 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{7,3,7,1,3,7,2})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Count using array for fixed small universe or a hash map for sparse IDs. On ties choose the smaller ID; frequency is not the numerical ID. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Autocorrect distance

Edit distance between a word and a suggestion. Minimum single-character insertions, deletions and substitutions to transform from into to. Each costs one; matching characters cost zero; no transposition operation.

**Contract:** `int distance(String from,String to)`

**Input limits and conventions:** Each string length0..1000 Java characters. Insertion, deletion and substitution each cost1; matching costs0. No transposition. Use iterative row/table evaluation at full size rather than1000+ recursive frames.

### Worked example

```text
word, suggestion
Input: "algoritm", "algorithm"
Verified expected: 1
```

**Executable call:**

```java
W04CatalogueReference.distance("algoritm","algorithm")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** State (i,j) for suffixes; base empty suffix length; memoise all three edit choices. Tabulate for large strings.

**Invariant and cost:** O(ab) time/storage or O(min(a,b)) row storage with tabulation. Explain your counted operation, state, base/undo rule and boundary convention.

**Additional cases:** "ab","ac" → 1; "","abc" → 3.

## 3. Words in any paragraphs

Prefix sums of per-paragraph word counts. Each query is zero-based inclusive [l,r]; return the range sums in query order. Use prefix[r+1]−prefix[l].

**Contract:** `long[] rangeSums(long[] values,int[][] queries)`

**Input limits and conventions:** 0..1000 signed values, each abs<=10^9;0..1000 queries. Inclusive0<=l<=r<n. Empty input valid only with no queries. Freeze/build once; rebuild after updates. long totals; reject invalid shapes/bounds.

### Worked example

```text
words per paragraph, queries
Input: [120, 85, 240, 60, 175], [[1, 3], [0, 4]]
Verified expected: [385, 680]
```

**Executable call:**

```java
W05CatalogueReference.rangeSums(new long[]{120L,85L,240L,60L,175L},new int[][]{new int[]{1,3},new int[]{0,4}})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Build one frozen prefix with extra zero; validate bounds; compare each answer to a direct loop.

**Invariant and cost:** O(n+q) time; O(n) prefix plus O(q) output. Explain your counted operation, prefix/ending-state invariant and exact boundary conversion; recursion/base/undo is N/A. Separate preprocessing, per-query and output costs.

**Additional cases:** values=[2,-1,3],queries=[[0,0],[1,2]] → [2,2]; no queries → [].

## 4. Find all

Count occurrences of a search term (KMP). Count every contiguous exact occurrence, including overlaps. Pattern must be nonempty; comparison is case-sensitive; no implicit whole-word restriction.

**Contract:** `int count(String text,String pattern)`

**Input limits and conventions:** Nonnull text0..100000 Java characters;nonnull NONEMPTY pattern1..100000. Case-sensitive exact contiguous occurrences INCLUDING overlaps;not whole-word or token-boundary matching. KMP O(text+pattern),O(pattern) LPS;empty pattern rejected.

### Worked example

```text
text, term
Input: "abracadabra abracadabra", "abra"
Verified expected: 4
```

**Executable call:**

```java
Week7ProjectReference.count("abracadabra abracadabra","abra")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Build LPS; after a complete match, fall back to lps[m−1] to keep overlaps. Adapt LC28 first-match starter to continue counting. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** text="aaaa",pattern="aa" → 3; text="abc",pattern="z" → 0.

## 5. Jump to an offset

Line containing character offset k: lower bound on cumulative line ends. Return the first zero-based index with value≥target, or n when none. This is an insertion boundary; exact presence additionally requires index<n and sorted[index]==target. Character positions are 1-based and cumulative line ends are inclusive. At an exact line end, that character belongs to the current line. Return n beyond the final end and report no line.

**Contract:** `int lower(int[] sorted,int target)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
cumulative line ends, offset 140
Input: [40, 95, 130, 180, 260], 140
Verified expected output: 3
```

**Executable call:**

```java
W08CatalogueReference.lower(new int[]{40,95,130,180,260},140)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Use [lo,hi), hi=n; sorted[mid]<target advances lo to mid+1, otherwise hi=mid. Preserve sortedness precondition. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

**Additional cases:** cumulativeEnds=[40,95]: position40 → line0; position41 → line1; position96 → boundary2, no line.

## 6. Bracket matching

Highlight unmatched brackets with a stack. Validate correct nesting of (),[] and{} while ignoring all other characters, including letters and asterisks. This is a bracket validator, not a Markdown parser. Return false on a mismatched closer or leftover opener; an empty/no-bracket text is valid.

**Contract:** `boolean validBrackets(String text)`

**Input limits and conventions:** Nonnull text length<=100000. Only bracket nesting checked;other chars ignored. No quote/comment/HTML parser semantics assumed.

### Worked example

```text
snippet
Input: "if (a[i] > b) { x = (y]; }"
Verified expected: false
```

**Executable call:**

```java
Week12ProjectReference.validBrackets("if (a[i] > b) { x = (y]; }")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Algorithm:** Push opener type (and character index for editor highlighting). On closer require the matching top; retain the first mismatch position for diagnosis. For the editor expose unmatched bracket positions as a UI extension; source acceptance sample is boolean.

**Invariant and cost:** O(L) time/O(L) stack; diagnostic positions need O(L) output in worst case.

**Additional cases:** "[veg](spicy)" →true; "([)]" →false; "abc" →true; "((" →false.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

text search; edit-distance tools; delimiter validation. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
