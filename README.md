[![Build Status](https://snap-ci.com/tomzo/gocd-yaml-config-plugin/branch/master/build_image)](https://snap-ci.com/tomzo/gocd-yaml-config-plugin/branch/master)

# gocd-yaml-config-plugin

Plugin to declare [Go's](go.cd) pipelines and environments configuration in YAML.

With this plugin and GoCD `>= 16.7.0` you can keep your pipeline and environments
 configuration in source control.

This is second GoCD configuration plugin, which enhances some of shortcomings of
[JSON configuration plugin](https://github.com/tomzo/gocd-json-config-plugin)

* Format is **concise**. Unlike JSON, there are no unnecessary quotations `"`, brackets `{}` and commas `,`
* Tries to **enforce correctness** where possible. By using maps instead of lists and shorter object graphs.
* Allows to have multiple pipelines and environments in single file. But doesn't force it.
* **Comments in configuration files** - YAML supports comments,
so you can explain why pipeline/environment it is configured like this.

### Example

More examples are in [test resources](src/test/resources/examples/).

```yaml
#ci.gocd.yaml
environments:
  testing:
    environment_variables:
      DEPLOYMENT: testing
    secure_variables:
      ENV_PASSWORD: "s&Du#@$xsSa"
    pipelines:
      - example-deploy-testing
      - build-testing
pipelines:
  mypipe1: # definition of mypipe1 pipeline
    group: mygroup
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
            tasks: # ordered list of tasks to execute in job csharp
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

# Specification
