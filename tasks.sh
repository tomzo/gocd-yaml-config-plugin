#!/bin/bash

set -e

# Fix for multi-line environment variables not working in docker envs
unset TRAVIS_COMMIT_MESSAGE

source .build/docker-ops
source .build/releaser

releaser_init

function get_version_tag {
  changelog_first_line=$(cat ${changelog_file} | head -1)
  changelog_version=$(get_last_version_from_changelog "${changelog_file}")
  short_sha=$(git rev-parse --short=8 HEAD)
  if [[ "${changelog_first_line}" == "#"*"Unreleased"* ]] || [[ "${changelog_first_line}" == "#"*"unreleased"* ]] || [[ "${changelog_first_line}" == "#"*"UNRELEASED"* ]];then
    log_info "Top of changelog has 'Unreleased' flag"
    echo "$changelog_version-$short_sha"
  else
    echo "$changelog_version"
  fi
}

command="$1"
case "${command}" in
  set_version)
    if [[ -n "$2" ]]; then
        next_version="$2"
    else
        changelog_version=$(get_last_version_from_changelog "${changelog_file}")
        next_version=$(bump_patch_version $changelog_version)
    fi
    set_version_in_changelog "${changelog_file}" "${next_version}" "true"
    set_version_in_file "version " "build.gradle" "${next_version}"
    ;;
  commit)
    git add "${changelog_file}"
    git add "build.gradle"
    git commit --author "Tomasz Setkowski <tom@ai-traders.com>" -m "Version bump"
    ;;
  prepare_release)
    next_version=$(get_last_version_from_changelog "${changelog_file}")
    set_version_in_changelog "${changelog_file}" "${next_version}" "false"
    set_version_in_file "version " "build.gradle" "${next_version}"
    ;;
  build)
    dojo "gradle test jar"
    ;;
  github_release)
    if [ -z "$GITHUB_TOKEN" ]; then
        echo "GITHUB_TOKEN is unset";
        exit 1;
    fi

    if [ ! -f linux-amd64-github-release.tar.bz2 ]; then
        wget https://github.com/aktau/github-release/releases/download/v0.6.2/linux-amd64-github-release.tar.bz2 -O linux-amd64-github-release.tar.bz2
    fi
    tar xf linux-amd64-github-release.tar.bz2

    VERSION=$(ls build/libs/yaml-config-plugin-*.jar | grep -Eo '[0-9]+\.[0-9]+\.[0-9]+')
    GHRELEASE_BIN="./bin/linux/amd64/github-release"

    changelog_version=$(get_last_version_from_changelog "${changelog_file}")
    if [ $changelog_version != $VERSION ]; then
      echo "changelog version $changelog_version does not match file version $VERSION"
      exit 2
    fi

    $GHRELEASE_BIN release \
      --user tomzo \
      --repo gocd-yaml-config-plugin \
      --tag $VERSION \
      --name $VERSION \
      --description "$changelog_version<br>docker image kudulab/gocd-cli-dojo:yaml-$VERSION" \
      --pre-release

    $GHRELEASE_BIN upload \
      --user tomzo \
      --repo gocd-yaml-config-plugin \
      --tag $VERSION \
      --name "yaml-config-plugin-$VERSION.jar" \
      --file build/libs/yaml-config-plugin-$VERSION.jar
    ;;
esac
