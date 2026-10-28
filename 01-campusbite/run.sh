#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
javac --release 17 -encoding UTF-8 -d build src/CampusBite.java test/CampusBiteTest.java
java -cp build CampusBiteTest
java -cp build CampusBite "$@"
