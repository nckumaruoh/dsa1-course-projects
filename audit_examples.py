"""Independent, deliberately simple oracles for the 180 small project examples.

These checks use enumeration, direct scans, exhaustive partitions and list-based
simulation rather than calling the Java algorithms under review.
"""
from collections import Counter, deque
from functools import lru_cache
from itertools import combinations
from pathlib import Path
import ast
import json
import re


def jformat(value):
    if isinstance(value, bool):
        return str(value).lower()
    if isinstance(value, list):
        return '[' + ', '.join(jformat(x) for x in value) + ']'
    return str(value)


def expected(case):
    """Return the result and an explanatory witness for one public fixture."""
    m, a, w = case['method'], case['args'], case['source_week']
    if m == 'repeats':
        ids = a[0]
        repeated = [x for i, x in enumerate(ids) if x in ids[:i]]
        answer = f'repeats={len(repeated)}, distinct={len(set(ids))}, first repeat={repeated[0] if repeated else "none"}'
        return answer, f'The repeated encounters, in arrival order, are {repeated}; each encounter after the first contributes one repeat.'
    if m == 'frequent':
        ids = a[0]
        winner = min(set(ids), key=lambda x: (-ids.count(x), x))
        return f'most frequent={winner} (count {ids.count(winner)}), distinct={len(set(ids))}', f'Counting each distinct ID gives {dict(sorted(Counter(ids).items()))}; a frequency tie chooses the smaller ID.'
    if m == 'pairs':
        pairs = [(i, j) for i in range(len(a[0])) for j in range(i+1, len(a[0])) if a[0][i]+a[0][j] == a[1]]
        return len(pairs), f'The matching distinct index pairs are {pairs}; repeated equal weights still represent different positions.'
    if m == 'total':
        values = case['own_values']
        return sum(values), f'The node-own values are {values}; adding each once gives {sum(values)}. These are not already subtree totals.'
    if m == 'palindrome':
        text = ''.join(x.lower() for x in a[0] if x.isalnum())
        return text == text[::-1], f'After removing punctuation and folding case, the text is {text!r}; compare it with its reversal.'
    if m == 'winner':
        circle = list(range(1, a[0]+1)); pos = 0; removed = []
        while len(circle) > 1:
            pos = (pos+a[1]-1) % len(circle)
            removed.append(circle.pop(pos))
        return circle[0], f'Counting the current item as one removes {removed}, leaving label {circle[0]}.'
    if m == 'region':
        grid, r, c = a
        seen = set() if grid[r][c] == '#' else {(r, c)}
        changed = True
        while changed:
            changed = False
            for i, row in enumerate(grid):
                for j, value in enumerate(row):
                    if value == '.' and (i, j) not in seen and any((i+di,j+dj) in seen for di,dj in [(-1,0),(1,0),(0,-1),(0,1)]):
                        seen.add((i,j)); changed = True
        return len(seen), f'The four-neighbour reachable cells are {sorted(seen)}; blocked cells and diagonal-only connections do not count.'
    if m in ('bestTotal', 'countSubsets', 'combinations'):
        values, target = a
        choices = [(tuple(i for i in range(len(values)) if mask & (1 << i)), sum(x for i,x in enumerate(values) if mask & (1 << i))) for mask in range(1 << len(values))]
        if m == 'bestTotal':
            indices, total = max((x for x in choices if x[1] <= target), key=lambda x:x[1])
            return total, f'One optimal selection uses indices {indices}, values {[values[i] for i in indices]}, totalling {total} within capacity {target}. Each index is used once.'
        matches = [indices for indices, total in choices if total == target]
        if m == 'countSubsets':
            return len(matches), f'The exact-target index subsets are {matches}; equal values at different indices are distinct choices.'
        result = sorted({tuple(sorted(values[i] for i in indices)) for indices in matches})
        return [list(x) for x in result], f'Enumerating one-use index subsets and deduplicating equal value combinations gives {result}.'
    if m == 'minCoins':
        coins, target = a; q = deque([(0, 0)]); seen = {0}
        while q:
            total, count = q.popleft()
            if total == target:
                return count, f'Breadth-first search of reachable totals first reaches {target} after {count} coins; denomination reuse is allowed.'
            for coin in coins:
                nxt = total+coin
                if nxt <= target and nxt not in seen:
                    seen.add(nxt); q.append((nxt,count+1))
        return -1, f'No total reachable using denominations {coins} equals {target}.'
    if m == 'ways':
        @lru_cache(None)
        def count(total):
            return 1 if total == 0 else sum(count(total-x) for x in a[0] if x <= total)
        result = count(a[1])
        return result, f'Classify sequences by their final shot/choice. Counts for remaining totals 0..{a[1]} are {[count(t) for t in range(a[1]+1)]}; different orders remain distinct.'
    if m == 'paths':
        grid = a[0]
        def visit(r,c):
            if r >= len(grid) or c >= len(grid[0]) or grid[r][c] == '#': return 0
            if (r,c) == (len(grid)-1,len(grid[0])-1): return 1
            return visit(r+1,c)+visit(r,c+1)
        result = visit(0,0)
        return result, f'Enumerating right/down paths while rejecting blocked cells leaves {result} valid paths to the bottom-right cell.'
    if m == 'distance':
        x,y = a; table = [[0]*(len(y)+1) for _ in range(len(x)+1)]
        for i in range(len(x)+1): table[i][0] = i
        for j in range(len(y)+1): table[0][j] = j
        for i in range(1,len(x)+1):
            for j in range(1,len(y)+1): table[i][j] = min(table[i-1][j]+1,table[i][j-1]+1,table[i-1][j-1]+(x[i-1]!=y[j-1]))
        return table[-1][-1], f'The full edit-distance table has final row {table[-1]}; its last cell is the minimum number of insertions, deletions and substitutions.'
    if m == 'rangeSums':
        result = [sum(a[0][left:right+1]) for left,right in a[1]]
        return result, f'Direct inclusive slices {[a[0][l:r+1] for l,r in a[1]]} sum to {result}; both query endpoints count.'
    if m == 'apply':
        result = [sum(delta for left,right,delta in a[1] if left <= i <= right) for i in range(a[0])]
        return result, f'For each position, add every covering inclusive update; the resulting position values are {result}.'
    if m in ('bestSum','bestInterval'):
        values = a[0]; candidates = [(sum(values[l:r+1]),l,r) for r in range(len(values)) for l in range(r+1)]
        total,left,right = max(candidates,key=lambda x:x[0])
        return f'Interval[value={total}, left={left}, right={right}]', f'Enumerating every nonempty interval finds sum {total} on inclusive indices {left}..{right}, containing {values[left:right+1]}.'
    if m in ('profit','bestTrade'):
        prices = a[0]; candidates = [(prices[j]-prices[i],i,j) for j in range(1,len(prices)) for i in range(j)]
        gain,buy,sell = max([(0,-1,-1)]+candidates,key=lambda x:x[0])
        return f'Interval[value={gain}, left={buy}, right={sell}]', f'Compare every buy-before-sell pair. The best positive difference is {gain}, with buy index {buy} and sell index {sell}; -1,-1 denotes no profitable trade.'
    if m == 'rectangle':
        matrix,r1,c1,r2,c2 = a
        rows = [sum(matrix[r][c1:c2+1]) for r in range(r1,r2+1)]
        return sum(rows), f'The selected inclusive row contributions are {rows}, whose sum is {sum(rows)}.'
    if m == 'count' and w == 5:
        values,target = a
        totals = [sum(values[l:r+1]) for l in range(len(values)) for r in range(l,len(values))]
        matched = [x for x in totals if (x == target if case['project'] == 'fraud' else x % target == 0)]
        return len(matched), f'Enumerating all nonempty contiguous sums finds {len(matched)} '+('sums exactly equal to' if case['project']=='fraud' else 'sums divisible by')+f' {target}; this is not the other overload.'
    if m == 'pair':
        valid = [[x,y] for i,x in enumerate(a[0]) for y in a[0][i+1:] if x+y == a[1]]
        return valid, f'Valid value pairs from distinct positions include {valid}; either order is accepted, and an absent pair is [].'
    if m == 'merge':
        result = sorted(a[0]+a[1])
        return result, f'Sorting the concatenated multiset independently gives {result}; every duplicate occurrence must remain.'
    if m == 'maxSlots':
        valid = []
        for size in range(len(a[0])+1):
            for chosen in combinations(a[0],size):
                ordered = sorted(chosen)
                if all(ordered[i][1] <= ordered[i+1][0] for i in range(len(ordered)-1)):valid.append(ordered)
        witness = max(valid,key=len)
        return len(witness), f'An exhaustive compatible subset is {witness}; touching end/start times are compatible. No larger subset is compatible.'
    if m == 'water':
        heights = a[0]
        levels = [max(0,min(max(heights[:i+1]),max(heights[i:]))-x) for i,x in enumerate(heights)]
        return sum(levels), f'Trapped units above each column are {levels}; total retained water is {sum(levels)}.'
    if m == 'bestWindow':
        values,k = a; sums = [sum(values[i:i+k]) for i in range(len(values)-k+1)]
        result = [max(sums),sums.index(max(sums))]
        return result, f'Complete width-{k} window totals are {sums}; the first maximum gives sum/start {result}.'
    if m == 'minLength':
        values,target = a; intervals = [(r-l+1,l,r) for l in range(len(values)) for r in range(l,len(values)) if sum(values[l:r+1]) >= target]
        best = min(intervals) if intervals else (0,-1,-1)
        return best[0], f'Exhaustively checking all intervals gives shortest length {best[0]}, witnessed by indices {best[1]}..{best[2]}; zero means no qualifying interval.'
    if m == 'count' and w == 7:
        text,pattern = a; matches = [i for i in range(len(text)-len(pattern)+1) if text[i:i+len(pattern)] == pattern]
        return len(matches), f'Direct substring comparisons match at start indices {matches}; overlaps count separately.'
    if m == 'lcs':
        x,y = a
        for length in range(min(len(x),len(y)),-1,-1):
            for indices in combinations(range(len(x)),length):
                candidate = ''.join(x[i] for i in indices); it = iter(y)
                if all(any(c == wanted for c in it) for wanted in candidate):return length, f'A longest common subsequence is {candidate!r}, length {length}; skipped characters need not be contiguous.'
    if m == 'longest':
        values,k = a; valid = [(r-l+1,l,r) for l in range(len(values)) for r in range(l,len(values)) if len(set(values[l:r+1])) <= k]
        best = max(valid,default=(0,-1,-1))
        return best[0], f'Enumerating all intervals with at most {k} distinct IDs finds length {best[0]}, for example indices {best[1]}..{best[2]}.'
    if m == 'groups':
        groups = {}
        for word in a[0]:groups.setdefault(''.join(sorted(word)),[]).append(word)
        result = sorted([sorted(x) for x in groups.values()])
        return result, f'Sorting characters gives an anagram signature; equal signatures form groups {result}. Group order is immaterial.'
    if m == 'lower':
        values,target = a; answer = next((i for i,x in enumerate(values) if x >= target),len(values))
        return answer, f'A direct left-to-right scan first finds a value at least {target} at index {answer}; n={len(values)} is a valid end sentinel.'
    if m == 'count' and w == 8:
        selected = [x for x in a[0] if a[1] <= x < a[2]]
        return len(selected), f'The half-open interval [{a[1]},{a[2]}) includes {selected}; duplicates are retained and the upper endpoint is excluded.'
    if m == 'min_capacity':
        values,rounds = a; best = sum(values); witness = [values]
        for groups in range(1,min(rounds,len(values))+1):
            for cuts in combinations(range(1,len(values)),groups-1):
                ends = (0,)+cuts+(len(values),); chunks = [values[ends[i]:ends[i+1]] for i in range(groups)]; capacity = max(map(sum,chunks))
                if capacity < best:best,witness = capacity,chunks
        return best, f'Exhaustive contiguous partitions using at most {rounds} groups yield minimum capacity {best}; one partition is {witness}, with loads {list(map(sum,witness))}.'
    if m == 'min_rate':
        work,hours = a
        if hours < len(work):return -1,'Even one whole hour per zone exceeds the available hours, so no positive rate is feasible.'
        rate = next(r for r in range(1,max(work)+1) if sum((x+r-1)//r for x in work) <= hours)
        times = [(x+rate-1)//rate for x in work]
        return rate, f'At rate {rate}, per-zone whole-hour charges are {times}, total {sum(times)} <= {hours}. Every lower rate is rejected by exhaustive search.'
    if m == 'max_goal':
        steps,days = a; ordered = sorted(steps,reverse=True); result = ordered[days-1] if days <= len(steps) else -1
        return result, f'Daily counts sorted descending are {ordered}; the {days}th-largest count is the highest threshold met on enough days.'
    if m == 'max_min_gap':
        positions,k = a; choices = [(min(y-x for x,y in zip(c,c[1:])),c) for c in combinations(sorted(positions),k)]; result,witness = max(choices)
        return result, f'Enumerating all placements gives a minimum-gap optimum of {result}, witnessed by positions {witness}.'
    if m == 'mergeIntervals':
        parts = [list(x) for x in a[0]]; changed = True
        while changed:
            changed = False
            for i in range(len(parts)):
                for j in range(i+1,len(parts)):
                    if max(parts[i][0],parts[j][0]) <= min(parts[i][1],parts[j][1]):
                        parts[i] = [min(parts[i][0],parts[j][0]),max(parts[i][1],parts[j][1])];parts.pop(j);changed = True;break
                if changed:break
        result = sorted(parts)
        return result, f'Repeatedly uniting intersecting closed intervals, including touching endpoints, produces {result}; gaps remain separate.'
    if m == 'rank':
        names,primary,secondary,descending = a
        order = sorted(range(len(names)),key=lambda i:(-primary[i],-secondary[i] if descending else secondary[i],i))
        return [names[i] for i in order], f'Ordering (primary descending, secondary '+('descending' if descending else 'ascending')+f', original index) gives source indices {order}; complete ties preserve input order.'
    if m == 'topKLinear':
        values,k = a; counts = Counter(values); result = sorted(counts,key=lambda x:(-counts[x],x))[:k]
        return result, f'ID frequencies are {dict(sorted(counts.items()))}; select {k} IDs from the largest frequencies. Tied order and tied cutoff choices may differ.'
    if m == 'simulate':
        requests,capacity = a; recent = [];hits = 0
        for x in requests:
            if x in recent:hits += 1;recent.remove(x)
            elif len(recent) == capacity:recent.pop()
            recent.insert(0,x)
        return f'hits={hits} of {len(requests)}, final order (most recent first) {recent}', f'A plain recency list produces {hits} hits and final most-to-least-recent keys {recent}; a hit moves its key to the front.'
    if m == 'queueSim':
        buckets,service,counters = a; arrivals = [t for t,n in enumerate(buckets) for _ in range(n)]
        waiting = deque();free = [0]*counters;served = 0;time = 0;waits = []
        while served < len(arrivals):
            if time < len(buckets):waiting.extend([time]*buckets[time])
            for c in range(counters):
                if free[c] <= time and waiting:
                    waits.append(time-waiting.popleft());free[c] = time+service;served += 1
            time += 1
        answer = f'served={served}, total wait={sum(waits)} min, longest wait={max(waits,default=0)} min'
        return answer, f'A minute-by-minute FIFO simulation produces waits {waits}; sum {sum(waits)}, maximum {max(waits,default=0)}. Service time is excluded from wait.'
    if m == 'waitDays':
        values = a[0]; result = [next((j-i for j in range(i+1,len(values)) if values[j] > x),0) for i,x in enumerate(values)]
        return result, f'Directly scan ahead from each day for the first strictly larger value; resulting delays are {result}, with zero when none exists.'
    if m == 'slidingMax':
        values,k = a; result = [max(values[i:i+k]) for i in range(len(values)-k+1)]
        return result, f'The maxima of every complete width-{k} slice are {result}; no partial window is emitted.'
    if m == 'span':
        values = a[0]; result = []
        for i,x in enumerate(values):
            j = i-1
            while j >= 0 and values[j] <= x:j -= 1
            result.append(i-j)
        return result, f'Count each day and its consecutive preceding prices <= today; the resulting spans are {result}.'
    if m == 'validBrackets':
        filtered = ''.join(c for c in a[0] if c in '()[]{}'); reduced = filtered
        while True:
            nxt = reduced.replace('()','').replace('[]','').replace('{}','')
            if nxt == reduced:break
            reduced = nxt
        return not reduced, f'Ignoring non-brackets leaves {filtered!r}; repeatedly removing adjacent matched pairs leaves {reduced!r}, so validity is {not reduced}.'
    if m == 'maxRectangle':
        values = a[0]; result = max([0]+[min(values[l:r+1])*(r-l+1) for l in range(len(values)) for r in range(l,len(values))])
        return result, f'Enumerate every contiguous histogram interval and multiply its width by its minimum height; the maximum area is {result}.'
    if m == 'evaluate':
        def visit(node):
            if isinstance(node,ast.Constant) and isinstance(node.value,int):return node.value
            if isinstance(node,ast.BinOp):
                x,y = visit(node.left),visit(node.right)
                if isinstance(node.op,ast.Add):return x+y
                if isinstance(node.op,ast.Sub):return x-y
                if isinstance(node.op,ast.Mult):return x*y
                if isinstance(node.op,ast.Div):return (1 if x*y >= 0 else -1)*(abs(x)//abs(y))
            raise ValueError('Unsupported oracle expression')
        answer = visit(ast.parse(a[0],mode='eval').body)
        return answer, f'An independent expression syntax tree evaluates parentheses and multiplication/division before addition/subtraction, giving {answer}; integer division truncates toward zero.'
    raise ValueError(f'Unhandled oracle: {w} {m}')


def check_result(case, actual):
    want, trace = expected(case)
    method = case['method']
    if method in ('pair','topKLinear','groups'):
        if method == 'groups':
            # Java List<String> output has unquoted lowercase words.
            groups = [sorted(re.findall('[a-z]+',part)) for part in re.findall(r'\[([^\[\]]*)\]',actual)]
            ok = sorted(groups) == want
        else:
            got = json.loads(actual)
            if method == 'pair':ok = (not want and got == []) or any(sorted(got) == sorted(pair) for pair in want)
            else:
                counts = Counter(case['args'][0]);k = case['args'][1]
                ok = len(got) == k and len(set(got)) == k and all(x in counts for x in got) and all(counts[got[i]] >= counts[got[i+1]] for i in range(len(got)-1))
                if ok and got:ok = all(counts[x] <= min(counts[y] for y in got) for x in counts if x not in got)
        if not ok:raise AssertionError(f'{case["project"]}: {method}: {actual!r} disagrees with independent oracle {want!r}')
    elif actual != jformat(want):
        raise AssertionError(f'{case["project"]}: {method}: {actual!r} != oracle {jformat(want)!r}')
    return {'method':method,'actual':actual,'oracle':jformat(want),'trace':trace,'passed':True}


def verify_project(project, stdout):
    root = Path(__file__).resolve().parent
    fixtures = json.loads((root/'audit/example-fixtures.json').read_text(encoding='utf8'))[project['id']]
    if project['number'] == 1:
        lines = stdout.splitlines()
        prefixes = ['', 'Best combo within Rs 150: ', 'Revenue queries: ', 'Rush [sum,start]: ', 'Orders in [240,300): ', '1 counter(s): ']
        actual = [next(line for line in lines if line.startswith('repeats='))]
        actual += [next(line[len(prefix):] for line in lines if line.startswith(prefix)) for prefix in prefixes[1:]]
    else:
        actual = [next(line.split(': ',1)[1] for line in stdout.splitlines() if line.startswith(f'{i}. ')) for i in range(1,7)]
    return [check_result(case,result) for case,result in zip(fixtures,actual)]


if __name__ == '__main__':
    root = Path(__file__).resolve().parent
    projects = json.loads((root/'PROJECT_CHECKLIST.json').read_text(encoding='utf8'))
    total = 0
    for project in projects:
        result = verify_project(project,(root/project['folder']/'example-output.txt').read_text(encoding='utf-8-sig'))
        total += len(result)
        print(f'PASS {project["number"]:02d}: six independently verified examples')
    print(f'PASS: {total} examples across {len(projects)} projects')
