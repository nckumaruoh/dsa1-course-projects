param()
$ErrorActionPreference='Stop'
Push-Location $PSScriptRoot
try {
 New-Item -ItemType Directory -Force build | Out-Null
 $sources=@(Get-ChildItem src,test -Filter '*.java' | ForEach-Object FullName)
 & javac --release 17 -encoding UTF-8 -d build @sources
 if($LASTEXITCODE -ne 0){throw 'Compilation failed'}
 & java -cp build ProjectTest
 if($LASTEXITCODE -ne 0){throw 'ProjectTest failed'}
 & java -cp build W04ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W04ProjectTests failed'}
 & java -cp build W05ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W05ProjectTests failed'}
 & java -cp build W06ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W06ProjectTests failed'}
 & java -cp build W09ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W09ProjectTests failed'}
 & java -cp build W10ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W10ProjectTests failed'}
 & java -cp build ProjectApp
 if($LASTEXITCODE -ne 0){throw 'Demo failed'}
} finally {Pop-Location}
