load '/opt/bats-support/load.bash'
load '/opt/bats-assert/load.bash'

@test "can git clone using https from github" {
  run /bin/bash -c "ide --idefile Idefile.to_be_tested \"rm -rf yaml-example &&\
    git clone https://github.com/tomzo/gocd-yaml-config-example.git yaml-example\""
  assert_output --partial "Cloning into 'yaml-example'..."
  assert_equal "$status" 0
}
