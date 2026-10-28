#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
javac --release 17 -encoding UTF-8 -d build src/*.java test/*.java
java -cp build ProjectTest
java -cp build W04ProjectTests
java -cp build W05ProjectTests
java -cp build W06ProjectTests
java -cp build W09ProjectTests
java -cp build W11ProjectTests
java -cp build ProjectApp
