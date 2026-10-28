param([switch]$Trace)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force -Path build | Out-Null
    & javac --release 17 -encoding UTF-8 -d build src/CampusBite.java test/CampusBiteTest.java
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
    & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build CampusBiteTest
    if ($LASTEXITCODE -ne 0) { throw 'Tests failed' }
    if ($Trace) { & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build CampusBite --trace } else { & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build CampusBite }
    if ($LASTEXITCODE -ne 0) { throw 'Demo failed' }
} finally { Pop-Location }
