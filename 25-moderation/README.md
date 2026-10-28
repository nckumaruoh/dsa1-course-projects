# SafeChat: Message Moderation

Group-chat apps filter abusive phrases, throttle spammers and catch disguised words without slowing delivery.

## Execute

This independent folder requires a JDK 17 or later on PATH. It has no external libraries or service credentials. All fixtures are synthetic.

Windows PowerShell: `./run.ps1`

Linux/macOS: `sh run.sh`

The scripts compile all `src/*.java` and `test/*.java`, run project checks and the applicable algorithm-family regression suites, then execute `ProjectApp`. A failed compile or test stops the script. To run only the demo after compilation: `java -cp build ProjectApp`.

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

**Algorithm:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

**Invariant and cost:** Use a frequency/seen map, or coordinate-compress IDs once and count with an array. Mark firstRepeat only on the first repeated encounter. Preserve input. Compare against a small nested-loop/reference oracle. Array counting O(n+U) time/O(U) auxiliary space; hashing expectedO(n), O(distinct) entries plus string character work. Pair total can exceed int.

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

**Algorithm:** State (i,j) for suffixes; base empty suffix length; memoise all three edit choices. Tabulate for large strings.

**Invariant and cost:** O(ab) time/storage or O(min(a,b)) row storage with tabulation. Explain your counted operation, state, base/undo rule and boundary convention.

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

**Algorithm:** Two read indices plus output index; append the remaining suffix after either input ends.

**Invariant and cost:** O(a+b) time; O(a+b) returned storage. Explain pointer/greedy progress,invariant,copy/output storage and exact boundary convention;recursion/undo prompts are N/A.

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

**Algorithm:** Build LPS; after a complete match, fall back to lps[m−1] to keep overlaps. Adapt LC28 first-match starter to continue counting. Create a complete Java class with main and run all three cases. See W07 Java and the matching skill method for a starting pattern. Complete tested faculty source for this family:W07 Project Reference/private package. Implement the full ONE chosen increment;common rolling model is not a compulsory extra project.

**Invariant and cost:** See the checked Java implementation and regression suite.

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

**Algorithm:** Compute lower(hi)−lower(lo), not a first/last matching-index scan. Both bounds may validly equal n. Adapt ProjectW08 or the matching bound-based skill starter, add a complete main and run the source example plus boundary cases. Faculty complete code:W08 Project Reference/private package. W08CatalogueReference one-call adapters INCLUDE validation/copy build per call;query O(log n) applies only after retaining a built index. Raw static bound helpers assume sorted validated input. Implement ONE chosen source increment;common model is support.

**Invariant and cost:** See the checked Java implementation and regression suite.

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

**Algorithm:** Push opener type (and character index for editor highlighting). On closer require the matching top; retain the first mismatch position for diagnosis. For the editor expose unmatched bracket positions as a UI extension; source acceptance sample is boolean.

**Invariant and cost:** O(L) time/O(L) stack; diagnostic positions need O(L) output in worst case.

**Additional cases:** "[veg](spicy)" →true; "([)]" →false; "abc" →true; "((" →false.

## Reproduce and inspect

Read [test-results.txt](test-results.txt) for the completed run and [example-output.txt](example-output.txt) for the integrated demonstration. `ProjectTest.java` checks this project's six fixtures and available listed additional cases. `CoreBasicsTest.java` supplies independent ID/counting, bounded-tree, flood-fill and recurrence oracles. The `WxxProjectTests.java` files exercise the relevant algorithm families with their owned course regression cases.

Numeric ID analysis also exposes `frequentCountArray(ids,U)` and `repeatsCountArray(ids,U)` for the declared universe `[0,U)`. These take O(n+U) time and O(U) memory including zero initialization. The quadratic small-input oracle in `CoreBasicsTest` supplies the comparison baseline. Hashing handles arbitrary bounded strings without pretending that strings are array indexes.

The tree implementation additionally validates repeated node identities and depth using an identity set. Its actual auxiliary cost is O(n+h), rather than the O(h) trusted-tree recursion alone stated in the original contract. This rejects shared-child DAGs and protects the bounded recursion. Flood fill uses an explicit queue; full-size palindrome and Josephus operations use loops.

Convenience methods that construct a frozen prefix or sorted index include construction/validation cost. Reuse `PulseLogWeek5`, `W05CatalogueReference.GridIndex`, `Week8ProjectReference` or `W08WordIndex` directly for repeated O(1) range queries or O(log n) bound queries after their O(n) build. String comparisons also include character work. The source contract and actual checked implementation determine the supported bounds.

## Similar applications

toy message-rule tools; repeated-message reports; bracketed markup validation. These are application categories, not claims of integration with a commercial product.

## Scope and limitations

This is a complete implementation of the six specified course increments and a console demonstration. It is not a deployed production service. It has no database, UI, external API integration or real personal data. Inputs beyond the documented synthetic bounds are rejected; integer overflow checks are retained where specified. Safety, financial, medical and placement scenarios remain synthetic algorithm exercises, with no real eligibility or operational decisions.

The implementations reuse this course's owned, reviewed Java primitives. Their original class names retain traceability to the source catalogue; they are now included under the explicit authorization to publish completed project solutions. Private assessment sheets, student records and weekly lab answer keys are excluded.
