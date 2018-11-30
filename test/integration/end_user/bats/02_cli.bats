load '/opt/bats-support/load.bash'
load '/opt/bats-assert/load.bash'

@test "With IDE: gocd-yaml returns OK when file is valid" {
  run /bin/bash -c "ide --idefile Idefile.to_be_tested \"cd yaml-example && gocd-yaml syntax ci.gocd.yaml\""
  assert_output --partial "OK"
  assert_equal "$status" 0
}
