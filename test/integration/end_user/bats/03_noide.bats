load '/opt/bats-support/load.bash'
load '/opt/bats-assert/load.bash'

@test "Without IDE: gocd-yaml returns OK when file is valid" {
  run /bin/bash -c 'source Idefile.to_be_tested && docker run --rm --volume $IDE_WORK:/ide/work $IDE_DOCKER_IMAGE "cd yaml-example && gocd-yaml syntax ci.gocd.yaml"'
  assert_output --partial "OK"
  assert_equal "$status" 0
}
