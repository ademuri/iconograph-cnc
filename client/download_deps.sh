#!/bin/bash

set -euo pipefail

mkdir -p deps
rm -r deps/* || true
pushd deps

wget https://phoenixnap.dl.sourceforge.net/project/ini4j/ini4j-bin/0.5.4/ini4j-0.5.4-bin.zip
unzip ini4j-0.5.4-bin.zip

wget https://ftp.wayne.edu/apache//commons/math/binaries/commons-math3-3.6.1-bin.zip
unzip commons-math3-3.6.1-bin.zip

mkdir -p auto-value
pushd auto-value
wget https://repo1.maven.org/maven2/com/google/auto/value/auto-value/1.7.4/auto-value-1.7.4-javadoc.jar
wget https://repo1.maven.org/maven2/com/google/auto/value/auto-value/1.7.4/auto-value-1.7.4-sources.jar
wget https://repo1.maven.org/maven2/com/google/auto/value/auto-value/1.7.4/auto-value-1.7.4.jar
popd

mkdir -p auto-value-annotations
pushd auto-value-annotations
wget https://repo1.maven.org/maven2/com/google/auto/value/auto-value-annotations/1.7.4/auto-value-annotations-1.7.4-javadoc.jar
wget https://repo1.maven.org/maven2/com/google/auto/value/auto-value-annotations/1.7.4/auto-value-annotations-1.7.4-sources.jar
wget https://repo1.maven.org/maven2/com/google/auto/value/auto-value-annotations/1.7.4/auto-value-annotations-1.7.4.jar
popd

wget https://fazecast.github.io/jSerialComm/binaries/jSerialComm-2.6.2.jar

mkdir -p guava
pushd guava
wget https://repo1.maven.org/maven2/com/google/guava/guava/30.1-jre/guava-30.1-jre.jar
wget https://repo1.maven.org/maven2/com/google/guava/guava/30.1-jre/guava-30.1-jre-sources.jar
wget https://repo1.maven.org/maven2/com/google/guava/guava/30.1-jre/guava-30.1-jre-javadoc.jar
popd

popd
