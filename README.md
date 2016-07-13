[![Build Status](https://snap-ci.com/tomzo/gocd-yaml-config-plugin/branch/master/build_image)](https://snap-ci.com/tomzo/gocd-yaml-config-plugin/branch/master)

# gocd-yaml-config-plugin

Plugin to declare Go pipeline and environments configuration in YAML.

With this plugin and GoCD `>= 16.7.0` you can keep your pipeline and environments
 configuration in source control.

### Example

More examples are in [test resources](src/testResources/examples/).

```yaml
#ci.gocd.yaml
mygroup: # pipelines of mygroup by name
  mypipe1: # definition of mypipe1 pipeline
    label_template: "${mygit[:8]}"
    materials:
      mygit: # this is the name of material
        # keyword git says about type of material and url at once
        git: http://my.example.org/mygit.git
        branch: ci
      upstream:
        # type is optional here, material type is implied based on presence of pipeline and stage fields
        # type: dependency
        pipeline: pipe2
        stage: test
    stages: # list of stages in order
      - build: # name of stage
        clean: true
        jobs:
          csharp: # name of the job
            resources:
             - net45
            artifacts:
             - build:
                 source: bin/
                 destination: build
             - test:
                 source: tests/
                 destination: test-reports/
            tabs:
              report: test-reports/index.html
            tasks: # list of tasks to execute in job csharp
             - fetch:
                 pipeline: pipe2
                 stage: build
                 job: test
                 source: test-bin/
                 destination: bin/
             - exec: # indicates type of task
                 command: make
                 args:
                  - "VERBOSE=true"
             # shorthand for script-executor plugin
             - script: ./build.sh ci
```
