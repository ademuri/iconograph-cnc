#!/bin/bash

set -euo pipefail

mkdir -p deps
rm -r deps/* || true
pushd deps

wget https://phoenixnap.dl.sourceforge.net/project/ini4j/ini4j-bin/0.5.4/ini4j-0.5.4-bin.zip
unzip ini4j-0.5.4-bin.zip

wget https://ftp.wayne.edu/apache//commons/math/binaries/commons-math3-3.6.1-bin.zip
unzip commons-math3-3.6.1-bin.zip

popd
