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

See [official GoCD XML configuration reference](https://docs.go.cd/current/configuration/configuration_reference.html)
for details about each element.

# Pipeline

A minimal [pipeline](https://docs.go.cd/current/configuration/configuration_reference.html#pipeline) configuration must contain:
 * pipeline name - as a key in hash
 * [materials](#materials) - a **hash**
 * [stages](#stage) - a **list**

```yaml
mypipe:
  materials:
    mygit:
      git: http://example.com/mygit.git
  stages:
    - build:
        jobs:
          build:
            tasks:
             - exec:
                 command: make
```

All elements available on a pipeline object are:

 * `group`
 * `label_template`
 * `locking`
 * `tracking_tool` or `mingle`
 * `timer`
 * `environment_variables`
 * `secure_variables`
 * `materials`
 * `stages`

```yaml
pipe2:
  group: group1
  label_template: "foo-1.0-${COUNT}"
  locking: on
  tracking_tool:
    link: "http://your-trackingtool/yourproject/${ID}"
    regex: "evo-(\\d+)"
  timer:
    spec: "0 15 10 * * ? *"
  environment_variables:
    DEPLOYMENT: testing
  secure_variables:
    ENV_PASSWORD: "s&Du#@$xsSa"
  materials:
    ...
  stages:
    ...
```

## Stage

### Single job stages

A common use case is that stage has only one job. This plugin provides a shorthand
to declared such stages - just **omit the `jobs:` and job name** from configuration tree.
You can then declare job and stage options on the same (stage) level:
```yaml
stages:
  - build:
      approval: manual
      resources:
        - cpp
      tasks:
       - exec:
           command: make
```
Above configuration declares `build` stage with `build` job which executes `make` task.

## Materials

### Git

Minimal configuration of a [**git** pipeline material](https://docs.go.cd/current/configuration/configuration_reference.html#git):
 * material name is `mygit`
 * git repository url is `http://example.com/mygit.git`

```yaml
mygit:
  git: http://example.com/mygit.git
```

Above can be also written more explicitly:
```yaml
mygit:
  type: git
  url: http://example.com/mygit.git
```

More customized git material is possible:
```yaml
gitMaterial1:
  git: "http://my.git.repository.com"
  branch: feature12
  blacklist:
    - externals/**/*.*
    - tools/**/*.*
  destination: dir1
  auto_update: false
  shallow_clone: true
```

Since Go `>= 16.7.0` whitelist is also supported,
you can specify `whitelist` **instead** of `blacklist`, as such
```yaml
gitMaterial1:
  git: "git@my.git.repository.com"
  branch: "feature12"
  whitelist:
    - src/**/*.*
```

### Environment variables

[Environment variables](https://docs.go.cd/current/configuration/configuration_reference.html#environmentvariables)
 can be declared in **Environments, Pipelines, Stages and Jobs**.

In YAML you have 2 keywords for *secure* (encrypted) variables and standard variables.
```yaml
environment_variables:
  DEPLOYMENT: testing
  FOO: bar
secure_variables:
  # this value is encrypted by Go's private key (Note in 16.7.0 there is no easy way to obtain such value yet)
  MY_PASSWORD: "s&Du#@$xsSa"
```

### Boolean values

Among all configuration elements there are boolean values, which can be defined
using any of the keywords below (as in [yaml specs](http://yaml.org/type/bool.html)):
 * **true** - `y|Y|yes|Yes|YES|true|True|TRUE|on|On|ON`
 * **false** - `n|N|no|No|NO|false|False|FALSE|off|Off|OFF`
