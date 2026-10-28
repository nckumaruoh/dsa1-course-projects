# SafeChat: Message Moderation

Group-chat apps filter abusive phrases, throttle spammers and catch disguised words without slowing delivery.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts explicitly use UTF-8 for Java output. The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ProjectApp`.

## Project data and integration

Messages (user, time, text); banned phrases; flag lists.

`ProjectApp.main` runs all six required increments in course order and prints their results in one execution. Its named calls provide editable fixtures; change the inputs there or call the corresponding static methods from another Java driver. The source files under `src` implement the algorithms; they do not invoke a remote service or a hidden implementation.

Related algorithm-family regression suites deliberately test more than the six sample cases. They check the reusable primitives shared by course projects, including boundary cases and independent oracles. `test-results.txt` records the actual output; it is not a substitute for rerunning tests.

## Six required increments

| Increment | Source week | Teaching week / CO | Entry point |
|---|---:|---|---|
| Repeated messages | 1 | Week 1 / CO1 | `CoreBasics.repeats` |
| Disguised words | 4 | Week 4 / CO2 | `W04CatalogueReference.distance` |
| Merge moderator flags | 6 | Week 6 / CO3 | `W06CatalogueReference.merge` |
| Phrase filter | 7 | Week 6 / CO3 | `Week7ProjectReference.count` |
| Messages in a window | 8 | Week 7 / CO4 | `W08CatalogueReference.count` |
| Markup validation | 12 | Week 12 / CO6 | `Week12ProjectReference.validBrackets` |

The source week is the original catalogue identifier. Teaching weeks use the revised two-weeks-per-CO delivery plan; a source identifier is not a second timetable.

## 1. Repeated messages

Identical message hashes repeated by a user. Process identifiers in arrival order. repeats counts every occurrence after the first of an ID, not just the number of distinct repeated IDs. Report distinct ID count and the first ID encountered for a second time; use none if no repeats. Equal strings/IDs identify the same item in this synthetic dataset.

**Contract:** `String repeats(String[] ids)`

**Input limits and conventions:** n≤100000; nonnegative integer IDs≤100000 or non-null hash strings≤100 characters; weights0..100000,target0..200000. Match full input/source domain; numeric array indexing needs a declared universe U.

### Worked example

```text
Input: ["h1", "h2", "h1", "h1", "h3", "h2"]
Verified expected: repeats=3, distinct=3, first repeat=h1
```

**Executable call:**

```java
CoreBasics.repeats(new String[]{"h1","h2","h1","h1","h3","h2"})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The repeated encounters, in arrival order, are ['h1', 'h1', 'h2']; each encounter after the first contributes one repeat.

**Algorithm:** Keep the IDs already seen. An encounter whose ID is already present adds one repeat; remember the first such encounter only once. The distinct count is the size of the seen set. Numeric count-array and quadratic comparison variants are also available.

**Invariant and cost:** Before each encounter, the set contains exactly earlier IDs. The normal hash path uses expected O(n) set operations and O(d) entries, plus string hashing/equality character work. The declared-universe count array uses O(n+U) time and O(U) space. The small quadratic oracle uses O(n²) comparisons.

**Additional cases:** [a,a,a,b,b] →repeats3,distinct2,firstRepeat a; [] →repeats0,distinct0,firstRepeat none.

## 2. Disguised words

Edit distance between a message word and a banned word (memo table). Minimum single-character insertions, deletions and substitutions to transform from into to. Each costs one; matching characters cost zero; no transposition operation.

**Contract:** `int distance(String from,String to)`

**Input limits and conventions:** Each string length0..1000 Java characters. Insertion, deletion and substitution each cost1; matching costs0. No transposition. Use iterative row/table evaluation at full size rather than1000+ recursive frames.

### Worked example

```text
message word, banned word
Input: "sp4mmer", "spammer"
Verified expected: 1
```

**Executable call:**

```java
W04CatalogueReference.distance("sp4mmer","spammer")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The full edit-distance table has final row [7, 6, 5, 5, 4, 3, 2, 1]; its last cell is the minimum number of insertions, deletions and substitutions.

**Algorithm:** Use Levenshtein edit distance: equal characters take the diagonal value; otherwise add one to the minimum of delete, insert and replace. The implementation retains only the previous and current row, using the shorter string for columns.

**Invariant and cost:** Each cell is the minimum edits between the two corresponding prefixes. O(mn+m+n) time, O(min(m,n)+1) auxiliary space. Empty strings are handled by row/column initialization; these are edits, not adjacent swaps.

**Additional cases:** "ab","ac" → 1; "","abc" → 3.

## 3. Merge moderator flags

Merge two moderators’ time-sorted flag lists. Inputs are nondecreasing; return every value in sorted order including duplicates. Preserve within-input order and choose left first on equal keys.

**Contract:** `int[] merge(int[] left,int[] right)`

**Input limits and conventions:** Nonnull sorted integer arrays;combined n<=1000. Preserve all duplicates and source arrays;fresh output. Within-input order retained,left first on equality. Numeric equal-key stability is unobservable;use tagged-event faculty model to demonstrate it.

### Worked example

```text
flag times (mod A), (mod B)
Input: [5, 12, 30], [7, 12, 18, 40]
Verified expected: [5, 7, 12, 12, 18, 30, 40]
```

**Executable call:**

```java
W06CatalogueReference.merge(new int[]{5,12,30},new int[]{7,12,18,40})
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Sorting the concatenated multiset independently gives [5, 7, 12, 12, 18, 30, 40]; every duplicate occurrence must remain.

**Algorithm:** Validate both sorted arrays and merge them with two read pointers. Choose from the left on equality; once one array ends, copy the remaining suffix of the other.

**Invariant and cost:** The written prefix contains the smallest consumed multiset entries in sorted order. O(n+m) time, O(n+m) returned storage and O(1) additional workspace. Inputs are preserved and duplicate occurrences remain.

**Additional cases:** [1,3],[2,3] → [1,2,3,3]; [],[] → [].

## 4. Phrase filter

Count banned phrase occurrences in a message (KMP). Count every contiguous exact occurrence, including overlaps. Pattern must be nonempty; comparison is case-sensitive; no implicit whole-word restriction.

**Contract:** `int count(String text,String pattern)`

**Input limits and conventions:** Nonnull text0..100000 Java characters;nonnull NONEMPTY pattern1..100000. Case-sensitive exact contiguous occurrences INCLUDING overlaps;not whole-word or token-boundary matching. KMP O(text+pattern),O(pattern) LPS;empty pattern rejected.

### Worked example

```text
message, phrase
Input: "buy now buy now limited buy now", "buy now"
Verified expected: 3
```

**Executable call:**

```java
Week7ProjectReference.count("buy now buy now limited buy now","buy now")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Direct substring comparisons match at start indices [0, 8, 24]; overlaps count separately.

**Algorithm:** Build the pattern’s prefix/failure array, then scan the text using KMP. On a mismatch, reuse the longest remaining prefix; after a match, fall back to its proper prefix rather than resetting to zero, so overlaps count.

**Invariant and cost:** The matched-length state is the longest pattern prefix matching the current text suffix. O(textLength+patternLength) time and O(patternLength) storage. The pattern must be nonempty.

**Additional cases:** text="aaaa",pattern="aa" → 3; text="abc",pattern="z" → 0.

## 5. Messages in a window

Sorted timestamps: count messages in [t1, t2). Count all entries in the half-open value interval [lo,hi). Include every duplicate at lo; exclude every duplicate at hi. Equal endpoints return zero; reject lo>hi.

**Contract:** `int count(int[] sorted,int lo,int hi)`

**Input limits and conventions:** Numeric lab:nonnull sorted nondecreasing int values,n<=100000;duplicates and empty arrays valid. Use persistent validated frozen index for repeated queries;boundary n is valid but never dereferenced. Range lo<=hi. Song/line positions positive,1-based inclusive cumulative ends;boundary n means no song/line.

### Worked example

```text
sorted times, [10, 20)
Input: [2, 5, 10, 11, 12, 19, 20, 27], 10, 20
Verified expected output: 4
```

**Executable call:**

```java
W08CatalogueReference.count(new int[]{2,5,10,11,12,19,20,27},10,20)
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** The half-open interval [10,20) includes [10, 11, 12, 19]; duplicates are retained and the upper endpoint is excluded.

**Algorithm:** Find the first position at least lo and first at least hi; their difference counts the half-open interval [lo,hi). Equal endpoints return zero. The adapter copies/validates the source before querying.

**Invariant and cost:** Each lower-bound search preserves its partition invariant. Numeric one-call cost is O(n+log(n+1)) time and O(n) space; retained-index queries are O(log(n+1)). For words, add validation of total characters and string-comparison character costs. Duplicates count as separate entries.

**Additional cases:** sorted=[10,10,20,30,30],range [10,30) → 3; [20,20) → 0; empty input → 0.

## 6. Markup validation

Validate nested formatting markers with a stack. Validate correct nesting of (),[] and{} while ignoring all other characters, including letters and asterisks. This is a bracket validator, not a Markdown parser. Return false on a mismatched closer or leftover opener; an empty/no-bracket text is valid.

**Contract:** `boolean validBrackets(String text)`

**Input limits and conventions:** Nonnull text length<=100000. Only bracket nesting checked;other chars ignored. No quote/comment/HTML parser semantics assumed.

### Worked example

```text
message
Input: "*(bold [link])* {quote}"
Verified expected: true
```

**Executable call:**

```java
Week12ProjectReference.validBrackets("*(bold [link])* {quote}")
```

The actual result appears in [example-output.txt](example-output.txt). ProjectTest checks this fixture against its declared expected result.

**Example reasoning:** Ignoring non-brackets leaves '([]){}'; repeatedly removing adjacent matched pairs leaves '', so validity is True.

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

toy message-rule tools; repeated-message reports; bracketed markup validation. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.

## Audit verification

This project’s documented PowerShell and shell commands, six examples and source-matched explanations are checked in the [course audit report](../AUDIT_REPORT.md). The shell command is also exercised in Linux CI; local shell checks on Windows use Git for Windows `sh`.
