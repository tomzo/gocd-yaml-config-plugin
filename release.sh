#!/bin/bash

source token.sh

if [ ! -f linux-amd64-github-release.tar.bz2 ]; then
    wget https://github.com/aktau/github-release/releases/download/v0.6.2/linux-amd64-github-release.tar.bz2 -O linux-amd64-github-release.tar.bz2
fi
tar xf linux-amd64-github-release.tar.bz2

VERSION="0.2.0"
GHRELEASE_BIN="./bin/linux/amd64/github-release"

$GHRELEASE_BIN release \
  --user tomzo \
  --repo gocd-yaml-config-plugin \
  --tag $VERSION \
  --name $VERSION \
  --pre-release

$GHRELEASE_BIN upload \
  --user tomzo \
  --repo gocd-yaml-config-plugin \
  --tag $VERSION \
  --name "yaml-config-plugin-$VERSION.jar" \
  --file build/libs/yaml-config-plugin-bundle-$VERSION.jar
