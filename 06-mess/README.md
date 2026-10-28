# MessVoice: Hostel Feedback Analyzer

Hostel messes collect short text feedback through a form. The committee wants the common complaints, keyword alerts and clean data.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Feedback strings, word ids, ratings, dates.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Most common complaint word | 1 | Week 1 / CO1 | `CoreBasics.frequent` |
| Palindrome coupon codes | 3 | Week 3 / CO2 | `CoreBasics.palindrome` |
| Merge two feedback forms | 6 | Week 6 / CO3 | `W06CatalogueReference.merge` |
| Keyword alerts | 7 | Week 6 / CO3 | `Week7ProjectReference.count` |
| Ratings in a band | 8 | Week 7 / CO4 | `W08CatalogueReference.count` |
| Clean formatting | 12 | Week 12 / CO6 | `Week12ProjectReference.validBrackets` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Most common complaint word

Word frequencies with a count array over word ids. Count occurrences per bounded integer ID and report the ID with highest frequency, its frequency and number of distinct IDs. Lab tie rule: smaller ID wins. The source report contract assumes nonempty input. This implementation additionally returns a no-result report for empty input, as a defensive extension.

**Contract:** `String frequent(int[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: [12, 4, 12, 7, 4, 12, 9]
Verified expected: most frequent=12 (count 3), distinct=4
```

**Executable call:**

```java
CoreBasics.frequent(new int[]{12,4,12,7,4,12,9})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Counting each distinct ID gives {4: 2, 7: 1, 9: 1, 12: 3}; a frequency tie chooses the smaller ID.

**Algorithm:** Count all occurrences, then retain the highest-frequency ID, choosing the smaller numeric ID on ties. The hash implementation supports sparse IDs; frequentCountArray supplies the explicit bounded-universe comparison.

**Invariant and cost:** Processed counts are exact. The winning count/ID pair is updated whenever its ordering improves. Expected O(n) hashing time and O(d) entries; the count-array alternative takes O(n+U) time and O(U) space, including initialization.

**Additional cases:** [3,1,3,1] →mostFrequent1,count2,distinct2; [0] →ID0,count1,distinct1.

## 2. Palindrome coupon codes

Validate palindromic coupon codes recursively, ignoring punctuation and case. Ignore non-alphanumeric characters and compare case-insensitively. Return whether the remaining sequence is a palindrome.

**Contract:** `boolean palindrome(String text)`

**Input limits and conventions:** Text length0..1000 Java characters; ignore non-alphanumeric characters and compare case-insensitively. Demonstrate recursion only on a short bounded example; use the two-pointer loop for full length. Empty filtered text is a palindrome.

### Worked example

```text
code
Input: "Mess-Code: edoc ssem"
Verified expected: true
```

**Executable call:**

```java
CoreBasics.palindrome("Mess-Code: edoc ssem")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** After removing punctuation and folding case, the text is 'messcodeedocssem'; compare it with its reversal.

**Algorithm:** Use two pointers. Skip non-alphanumeric characters and compare case-folded endpoint characters, moving inward after a match. This iterative full-input path avoids a recursion-depth dependency.

**Invariant and cost:** All characters outside the current endpoints have already matched or been ignored. O(L) time and O(1) auxiliary space for L Java characters. The stated behavior follows Character.isLetterOrDigit/toLowerCase; empty filtered text is valid.

**Additional cases:** "A-b-a" → true; "ab" → false.

## 3. Merge two feedback forms

Merge date-sorted entries from the paper form and the online form in one pass. Inputs are nondecreasing; return every value in sorted order including duplicates. Preserve within-input order and choose left first on equal keys.

**Contract:** `int[] merge(int[] left,int[] right)`

**Input limits and conventions:** Nonnull sorted integer arrays;combined n<=1000. Preserve all duplicates and source arrays;fresh output. Within-input order retained,left first on equality. Numeric equal-key stability is unobservable;use tagged-event faculty model to demonstrate it.

### Worked example

```text
dates (form A), dates (form B)
Input: [1, 3, 4, 8], [2, 3, 6, 9]
Verified expected: [1, 2, 3, 3, 4, 6, 8, 9]
```

**Executable call:**

```java
W06CatalogueReference.merge(new int[]{1,3,4,8},new int[]{2,3,6,9})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Sorting the concatenated multiset independently gives [1, 2, 3, 3, 4, 6, 8, 9]; every duplicate occurrence must remain.

**Algorithm:** Validate both sorted arrays and merge them with two read pointers. Choose from the left on equality; once one array ends, copy the remaining suffix of the other.

**Invariant and cost:** The written prefix contains the smallest consumed multiset entries in sorted order. O(n+m) time, O(n+m) returned storage and O(1) additional workspace. Inputs are preserved and duplicate occurrences remain.

**Additional cases:** [1,3],[2,3] → [1,2,3,3]; [],[] → [].

## 4. Keyword alerts

Count occurrences of a phrase such as 'cold food' in the day’s feedback (KMP). Count every contiguous exact occurrence, including overlaps. Pattern must be nonempty; comparison is case-sensitive; no implicit whole-word restriction.

**Contract:** `int count(String text,String pattern)`

**Input limits and conventions:** Nonnull text0..100000 Java characters;nonnull NONEMPTY pattern1..100000. Case-sensitive exact contiguous occurrences INCLUDING overlaps;not whole-word or token-boundary matching. KMP O(text+pattern),O(pattern) LPS;empty pattern rejected.

### Worked example

```text
text, phrase
Input: "rice was cold food cold food again", "cold food"
Verified expected: 2
```

**Executable call:**

```java
Week7ProjectReference.count("rice was cold food cold food again","cold food")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Direct substring comparisons match at start indices [9, 19]; overlaps count separately.

**Algorithm:** Build the pattern’s prefix/failure array, then scan the text using KMP. On a mismatch, reuse the longest remaining prefix; after a match, fall back to its proper prefix rather than resetting to zero, so overlaps count.

**Invariant and cost:** The matched-length state is the longest pattern prefix matching the current text suffix. O(textLength+patternLength) time and O(patternLength) storage. The pattern must be nonempty.

**Additional cases:** text="aaaa",pattern="aa" → 3; text="abc",pattern="z" → 0.

## 5. Ratings in a band

Sorted ratings: count entries with rating in [2, 4) using two lower bounds. Count all entries in the half-open value interval [lo,hi). Include every duplicate at lo; exclude every duplicate at hi. Equal endpoints return zero; reject lo>hi.

**Contract:** `int count(int[] sorted,int lo,int hi)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted ratings, [2, 4)
Input: [1, 1, 2, 2, 3, 3, 3, 4, 5, 5], 2, 4
Verified expected output: 5
```

**Executable call:**

```java
W08CatalogueReference.count(new int[]{1,1,2,2,3,3,3,4,5,5},2,4)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The half-open interval [2,4) includes [2, 2, 3, 3, 3]; duplicates are retained and the upper endpoint is excluded.

**Algorithm:** Find the first position at least lo and first at least hi; their difference counts the half-open interval [lo,hi). Equal endpoints return zero. The adapter copies/validates the source before querying.

**Invariant and cost:** Each lower-bound search preserves its partition invariant. Numeric one-call cost is O(n+log(n+1)) time and O(n) space; retained-index queries are O(log(n+1)). For words, add validation of total characters and string-comparison character costs. Duplicates count as separate entries.

**Additional cases:** sorted=[10,10,20,30,30],range [10,30) → 3; [20,20) → 0; empty input → 0.

## 6. Clean formatting

Validate bracketed tags like [veg](spicy) in entries with a stack. Validate correct nesting of (),[] and{} while ignoring all other characters, including letters and asterisks. This is a bracket validator, not a Markdown parser. Return false on a mismatched closer or leftover opener; an empty/no-bracket text is valid.

**Contract:** `boolean validBrackets(String text)`

**Input limits and conventions:** Nonnull text length<=100000. Only bracket nesting checked;other chars ignored. No quote/comment/HTML parser semantics assumed.

### Worked example

```text
entry
Input: "[veg](spicy){menu}[x(y])"
Verified expected: false
```

**Executable call:**

```java
Week12ProjectReference.validBrackets("[veg](spicy){menu}[x(y])")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Ignoring non-brackets leaves '[](){}[(])'; repeatedly removing adjacent matched pairs leaves '[(])', so validity is False.

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

meal feedback summaries; coupon validation; text-search tools. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
