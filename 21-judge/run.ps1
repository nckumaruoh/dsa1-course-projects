param()
$ErrorActionPreference='Stop'
Push-Location $PSScriptRoot
try {
 New-Item -ItemType Directory -Force build | Out-Null
 $sources=@(Get-ChildItem src,test -Filter '*.java' | ForEach-Object FullName)
 & javac --release 17 -encoding UTF-8 -d build @sources
 if($LASTEXITCODE -ne 0){throw 'Compilation failed'}
 & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build ProjectTest
 if($LASTEXITCODE -ne 0){throw 'ProjectTest failed'}
 & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build W04ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W04ProjectTests failed'}
 & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build W05ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W05ProjectTests failed'}
 & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build W07ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W07ProjectTests failed'}
 & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build W09ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W09ProjectTests failed'}
 & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build W12ProjectTests
 if($LASTEXITCODE -ne 0){throw 'W12ProjectTests failed'}
 & java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp build ProjectApp
 if($LASTEXITCODE -ne 0){throw 'Demo failed'}
} finally {Pop-Location}
