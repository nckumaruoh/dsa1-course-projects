# NoteNest: Text Editor Core

Every editor and notes app needs word counts, find, diffs, autocorrect and bracket matching. Each is a data-structure exercise.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

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

**Example reasoning:** Counting each distinct ID gives {1: 1, 2: 1, 3: 2, 7: 3}; a frequency tie chooses the smaller ID.

**Algorithm:** Count all occurrences, then retain the highest-frequency ID, choosing the smaller numeric ID on ties. The hash implementation supports sparse IDs; frequentCountArray supplies the explicit bounded-universe comparison.

**Invariant and cost:** Processed counts are exact. The winning count/ID pair is updated whenever its ordering improves. Expected O(n) hashing time and O(d) entries; the count-array alternative takes O(n+U) time and O(U) space, including initialization.

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

**Example reasoning:** The full edit-distance table has final row [8, 7, 6, 5, 4, 3, 2, 1, 1, 1]; its last cell is the minimum number of insertions, deletions and substitutions.

**Algorithm:** Use Levenshtein edit distance: equal characters take the diagonal value; otherwise add one to the minimum of delete, insert and replace. The implementation retains only the previous and current row, using the shorter string for columns.

**Invariant and cost:** Each cell is the minimum edits between the two corresponding prefixes. O(mn+m+n) time, O(min(m,n)+1) auxiliary space. Empty strings are handled by row/column initialization; these are edits, not adjacent swaps.

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

**Example reasoning:** Direct inclusive slices [[85, 240, 60], [120, 85, 240, 60, 175]] sum to [385, 680]; both query endpoints count.

**Algorithm:** Build a frozen prefix array with an extra leading zero. For an inclusive query [l,r], subtract prefix[l] from prefix[r+1]. The index must be rebuilt after source updates.

**Invariant and cost:** prefix[i] is the sum of the first i values. The convenience batch method takes O(n+q) time and O(n+q) space including its output; construction is O(n), each retained-index query O(1). Signed values and long totals are supported.

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

**Example reasoning:** Direct substring comparisons match at start indices [0, 7, 12, 19]; overlaps count separately.

**Algorithm:** Build the pattern’s prefix/failure array, then scan the text using KMP. On a mismatch, reuse the longest remaining prefix; after a match, fall back to its proper prefix rather than resetting to zero, so overlaps count.

**Invariant and cost:** The matched-length state is the longest pattern prefix matching the current text suffix. O(textLength+patternLength) time and O(patternLength) storage. The pattern must be nonempty.

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

**Example reasoning:** A direct left-to-right scan first finds a value at least 140 at index 3; n=5 is a valid end sentinel.

**Algorithm:** Copy and validate the sorted array, then search a half-open unclassified interval [l,r). If a[mid]<target, move l past mid; otherwise retain mid by setting r=mid. Return the meeting boundary.

**Invariant and cost:** Values before l are below target and those at/after r are at least target. A one-call adapter is O(n+log(n+1)) time and O(n) frozen-copy space. Reuse the constructed index for O(log(n+1)) numeric queries. The valid return n must not be dereferenced.

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

**Example reasoning:** Ignoring non-brackets leaves '([]){(]}'; repeatedly removing adjacent matched pairs leaves '{(]}', so validity is False.

**Algorithm:** Ignore ordinary text characters. Push opening brackets; each closing bracket must match the most recent unclosed opener. Reject an unmatched or wrong-type close and require an empty stack at the end.

**Invariant and cost:** The stack is exactly the sequence of unclosed openers. O(L) time and O(L) worst-case space. Balanced counts alone are insufficient: nesting order and bracket type both matter.

**Additional cases:** "[veg](spicy)" →true; "([)]" →false; "abc" →true; "((" →false.

## Reproduce and inspect

Cost notation: n is the input count, d the number of distinct IDs, U a declared ID universe, B capacity, T a target, and q the query count. R and C denote grid rows/columns; in cache costs C instead denotes cache capacity. Output storage is identified separately where relevant.

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Construction and per-query costs are stated beside each feature above. Only the support classes present in this folder are needed to run this project.

## Similar applications

text search; edit-distance tools; delimiter validation. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
