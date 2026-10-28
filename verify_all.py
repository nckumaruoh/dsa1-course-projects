"""Audit documented commands, fresh builds and 180 independent examples.

Requires Python 3.10+ and JDK 17+ on PATH. Windows uses PowerShell; other
platforms use sh. --shell explicitly exercises sh (also Git for Windows).
"""
from pathlib import Path
import argparse
import datetime
import json
import os
import shutil
import subprocess
import sys
import tempfile
from audit_examples import verify_project


def normal(text):
    return text.lstrip('\ufeff').replace('\r\n', '\n').strip()


def audit(root, project, shell):
    original = root / project['folder']
    with tempfile.TemporaryDirectory(prefix='DSA project audit ') as temporary:
        parent = Path(temporary).resolve()
        assert parent.is_relative_to(Path(tempfile.gettempdir()).resolve())
        assert parent.name.startswith('DSA project audit ')
        folder = parent / project['folder']
        shutil.copytree(original, folder, ignore=shutil.ignore_patterns('build', '.git'))
        if shell:
            executable = shutil.which('sh')
            if not executable and os.name == 'nt':
                candidate = Path('C:/Program Files/Git/bin/sh.exe')
                if candidate.exists():
                    executable = str(candidate)
            if not executable:
                raise RuntimeError('sh is required for --shell; install Git for Windows or use a POSIX shell')
            command = [executable, str(folder/'run.sh')]
            if project['number'] == 1:
                command.append('--trace')
            platform = 'sh'
        else:
            executable = shutil.which('pwsh') or shutil.which('powershell')
            if not executable:
                raise RuntimeError('PowerShell is required on Windows; alternatively use --shell')
            command = [executable, '-NoProfile', '-File', str(folder/'run.ps1')]
            if project['number'] == 1:
                command.append('-Trace')
            platform = 'PowerShell'
        result = subprocess.run(command, cwd=parent, text=True, capture_output=True,
                                encoding='utf-8', timeout=180)
        if result.returncode:
            raise RuntimeError(result.stdout + result.stderr)
        if '\ufffd' in result.stdout:
            raise AssertionError('Replacement character in the documented command output')
        expected = (original/'example-output.txt').read_text(encoding='utf-8-sig')
        if not normal(result.stdout).endswith(normal(expected)):
            raise AssertionError('Fresh demo output differs from example-output.txt\n' + result.stdout)
        example_results = verify_project(project, result.stdout)
        main_class = 'CampusBite' if project['number'] == 1 else 'ProjectApp'
        manual = 'java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build ' + main_class
        if project['number'] == 1:
            manual += ' --trace'
        if shell:
            manual_command = [executable, '-c', manual]
        else:
            manual_file = folder/'manual-audit.ps1'
            manual_file.write_text(manual+'\nif ($LASTEXITCODE -ne 0) { throw "Manual command failed" }\n', encoding='utf-8')
            manual_command = [executable, '-NoProfile', '-File', str(manual_file)]
        manual_result = subprocess.run(manual_command, cwd=folder, text=True, capture_output=True, encoding='utf-8', timeout=60)
        if manual_result.returncode or normal(manual_result.stdout) != normal(expected):
            raise AssertionError('Documented manual demo command failed or output changed\n' + manual_result.stdout + manual_result.stderr)
        return {'number':project['number'], 'id':project['id'], 'folder':project['folder'],
                'platform':platform, 'exit_code':0, 'isolated_build':True,
                'path_with_spaces':True, 'example_output_matches':True, 'manual_command_verified':True,
                'test_summary':[line for line in result.stdout.splitlines() if line.startswith('PASS')],
                'independent_examples':example_results, 'passed':True}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--shell', action='store_true', help='run the documented sh commands')
    parser.add_argument('--project', help='audit one project by its catalogue ID')
    parser.add_argument('--report', type=Path, help='write a JSON audit report')
    options = parser.parse_args()
    root = Path(__file__).resolve().parent
    projects = json.loads((root/'PROJECT_CHECKLIST.json').read_text(encoding='utf-8'))
    if options.project:
        projects = [x for x in projects if x['id'] == options.project]
        if not projects:
            parser.error('Unknown project ID')
    results = []
    for project in projects:
        try:
            result = audit(root, project, options.shell or os.name != 'nt')
            print(f'PASS {project["number"]:02d}: {project["title"]} - documented command, fresh build, six independent examples', flush=True)
        except (RuntimeError, AssertionError, subprocess.TimeoutExpired, UnicodeError) as error:
            result = {'number':project['number'], 'id':project['id'], 'passed':False, 'error':str(error)}
            print(f'FAIL {project["id"]}: {error}', file=sys.stderr, flush=True)
        results.append(result)
        if options.report:
            options.report.parent.mkdir(parents=True, exist_ok=True)
            options.report.write_text(json.dumps({'verified_at':datetime.datetime.now(datetime.timezone.utc).isoformat(), 'results':results}, indent=2), encoding='utf-8')
    failed = [x['id'] for x in results if not x['passed']]
    if failed:
        raise SystemExit('Failed: ' + ', '.join(failed))
    print(f'PASS: {len(results)} independent projects, {len(results)*6} examples, documented commands and isolated builds.')


if __name__ == '__main__':
    main()
