param([switch]$Trace)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force -Path build | Out-Null
    & javac --release 17 -encoding UTF-8 -d build src/CampusBite.java test/CampusBiteTest.java
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
    & java -cp build CampusBiteTest
    if ($LASTEXITCODE -ne 0) { throw 'Tests failed' }
    if ($Trace) { & java -cp build CampusBite --trace } else { & java -cp build CampusBite }
    if ($LASTEXITCODE -ne 0) { throw 'Demo failed' }
} finally { Pop-Location }
