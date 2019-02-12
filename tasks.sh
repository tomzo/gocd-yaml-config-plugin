#!/bin/bash

set -e

source .build/docker-ops
source .build/releaser

releaser_init

image_name_no_registry="gocd-yaml-ide"
private_image_name="docker-registry.ai-traders.com/${image_name_no_registry}"
public_image_name="tomzo/${image_name_no_registry}"
image_dir="./docker"
imagerc_filename="imagerc"

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
  prepare_release)
    next_version=$(get_last_version_from_changelog "${changelog_file}")
    set_version_in_changelog "${changelog_file}" "${next_version}" "false"
    set_version_in_file "version " "build.gradle" "${next_version}"
    ;;
  build_docker)
    changelog_version=$(get_last_version_from_changelog "${changelog_file}")
    docker_build_options="--build-arg this_image_tag_arg=${changelog_version}"
    image_tag=$2
    docker_build "${image_dir}" "${imagerc_filename}" "${private_image_name}" "$image_tag"
    exit $?
    ;;
  test_docker)
    source_imagerc "${image_dir}"  "${imagerc_filename}"
    echo "Testing image: ${AIT_DOCKER_IMAGE_NAME}:${AIT_DOCKER_IMAGE_TAG}"
    echo "IDE_DOCKER_IMAGE=\"${AIT_DOCKER_IMAGE_NAME}:${AIT_DOCKER_IMAGE_TAG}\"" > Idefile.to_be_tested
    echo "IDE_WORK=$(pwd)/test/integration/test_ide_work" >> Idefile.to_be_tested
    time bats "$(pwd)/test/integration/end_user/bats"
    exit $?
    ;;
  publish_docker_private)
    source_imagerc "${image_dir}"  "${imagerc_filename}"
    production_image_tag=$(get_version_tag)
    docker_push "${AIT_DOCKER_IMAGE_NAME}" "${AIT_DOCKER_IMAGE_TAG}" "${production_image_tag}"
    exit $?
    ;;
  publish_docker_public)
    source_imagerc "${image_dir}"  "${imagerc_filename}"
    production_image_tag=$(get_version_tag)
    docker login --username tomzo --password ${DOCKERHUB_PASSWORD}
    testing_image_tag="${AIT_DOCKER_IMAGE_TAG}"

    log_info "testing_image_tag set to: ${testing_image_tag}"
    log_info "production_image_tag set to: ${production_image_tag}"
    if ! docker images ${AIT_DOCKER_IMAGE_NAME} | awk '{print $2}' | grep ${testing_image_tag} 1>/dev/null ; then
      # if docker image does not exist locally, then "docker tag" will fail,
      # so pull it. However, do not always pull it, the image may be not pushed
      # and only available locally.
      set -x -e
      docker pull "${AIT_DOCKER_IMAGE_NAME}:${testing_image_tag}"
    fi
    set -x -e
    # When tagging a docker image using docker 1.8.3, we can use `docker tag -f`.
    # When using docker 1.12, there is no `-f` option, but `docker tag`
    # always works as if force was used.
    docker tag -f "${AIT_DOCKER_IMAGE_NAME}:${testing_image_tag}" "${public_image_name}:${production_image_tag}" || docker tag "${AIT_DOCKER_IMAGE_NAME}:${testing_image_tag}" "${public_image_name}:${production_image_tag}"
    docker tag -f "${AIT_DOCKER_IMAGE_NAME}:${testing_image_tag}" "${public_image_name}:latest" || docker tag "${AIT_DOCKER_IMAGE_NAME}:${testing_image_tag}" "${public_image_name}:latest"
    if [[ "${dryrun}" != "true" ]];then
      docker push "${public_image_name}:${production_image_tag}"
      docker push "${public_image_name}:latest"
    fi
    set +x +e
    exit $?
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
      --description "$changelog_version<br>docker image tomzo/gocd-yaml-ide:$VERSION" \
      --pre-release

    $GHRELEASE_BIN upload \
      --user tomzo \
      --repo gocd-yaml-config-plugin \
      --tag $VERSION \
      --name "yaml-config-plugin-$VERSION.jar" \
      --file build/libs/yaml-config-plugin-$VERSION.jar
    ;;
esac
