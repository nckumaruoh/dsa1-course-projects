"""Compile and verify each independent project folder. Requires JDK 17+ on PATH."""
from pathlib import Path
import json, subprocess, sys

root = Path(__file__).resolve().parent
projects = json.loads((root / 'PROJECT_CHECKLIST.json').read_text(encoding='utf8'))
failed = []
for project in projects:
    folder = root / project['folder']
    build = folder / 'build'
    build.mkdir(exist_ok=True)
    sources = sorted((folder / 'src').glob('*.java')) + sorted((folder / 'test').glob('*.java'))
    commands = [['javac', '--release', '17', '-encoding', 'UTF-8', '-d', str(build), *map(str, sources)]]
    tests = ['CampusBiteTest'] if project['number'] == 1 else ['ProjectTest'] + [p.stem for p in sorted((folder / 'test').glob('W*ProjectTests.java'))]
    commands += [['java', '-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8', '-cp', str(build), name] for name in tests]
    try:
        for command in commands:
            result = subprocess.run(command, text=True, capture_output=True, encoding='utf8', timeout=180)
            if result.returncode:
                raise RuntimeError(result.stdout + result.stderr)
        print(f"PASS {project['number']:02d}: {project['title']}", flush=True)
    except (RuntimeError, subprocess.TimeoutExpired) as error:
        failed.append(project['id'])
        print(f"FAIL {project['id']}: {error}", file=sys.stderr, flush=True)
if failed:
    raise SystemExit('Failed: ' + ', '.join(failed))
print('PASS: all 30 independent projects compiled for Java 17 and passed their suites.')
