[![Join the chat at https://gitter.im/gocd/gocd](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/gocd/configrepo-plugins?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# gocd-yaml-config-plugin

[GoCD](https://www.gocd.org) server plugin to keep **pipelines** and **environments**
configuration in source-control in [YAML](http://www.yaml.org/).
See [this document](https://docs.gocd.org/current/advanced_usage/pipelines_as_code.html)
to find out what are GoCD configuration repositories.

This is second GoCD configuration plugin, which enhances some of shortcomings of
[JSON configuration plugin](https://github.com/tomzo/gocd-json-config-plugin)

* Format is **concise**. Unlike JSON, there are no unnecessary quotations `"`, brackets `{}` and commas `,`
* Tries to **enforce correctness** where possible. By using maps instead of lists and shorter object graphs.
* Allows to have multiple pipelines and environments in single file. But doesn't force it.
* **Comments in configuration files** - YAML supports comments,
so you can explain why pipeline/environment it is configured like this.

## Some highlights

 * Shorter syntax for declaring [single-job stages](#single-job-stage)
 * Very short syntax for [declaring tasks with script executor plugin](#script)

## Setup

If you're using GoCD version *older than 17.8.0*, you need to install the plugin in the GoCD server. Download it from
[the releases page](https://github.com/tomzo/gocd-yaml-config-plugin/releases) and place it on the GoCD server in
`plugins/external` [directory](https://docs.gocd.org/current/extension_points/plugin_user_guide.html).

Add `config-repos` element right above first `<pipelines />`. Then you can
add any number of YAML configuration repositories as such:

```xml
<config-repos>
  <config-repo pluginId="yaml.config.plugin" id="repo1">
    <git url="https://github.com/tomzo/gocd-yaml-config-example.git" />
  </config-repo>
</config-repos>
```

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
    group: mygroup # note that the group name can contain only of alphanumeric & underscore characters
    label_template: "${mygit[:8]}"
    locking: off
    materials:
      mygit: # this is the name of material, the name can contain only of alphanumeric & underscore characters
        # keyword git says about type of material and url at once
        git: http://my.example.org/mygit.git
        branch: ci
      myupstream:
        # type is optional here, material type is implied based on presence of pipeline and stage fields
        # type: dependency
        pipeline: pipe2
        stage: test
    stages: # list of stages in order
      - build: # name of stage
          clean_workspace: true
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
                   arguments:
                    - "VERBOSE=true"
               # shorthand for script-executor plugin
               - script: ./build.sh ci
```

## File pattern

By default GoCD configuration files should end with `.gocd.yaml`.

# Specification

See [official GoCD XML configuration reference](https://docs.gocd.org/current/configuration/configuration_reference.html)
for details about each element. Below is a reference of format supported by this plugin.
Feel free to improve it!

1. [Environment](#environment)
1. [Environment variables](#environment-variables)
1. [Pipeline](#pipeline)
    * [Tabs](#tabs)
    * [Tracking tool](#tracking-tool)
    * [Timer](#timer)
1. [Stage](#stage)
    * [Approval](#approval)
1. [Job](#job)
    * [Property](#property)
    * [Tab](#tab)
    * [Many instances](#run-many-instances)
1. [Tasks](#tasks)
    * [rake](#rake)
    * [ant](#ant)
    * [nant](#nant)
    * [exec](#exec)
    * [fetch](#fetch)
    * [pluggabletask](#plugin)
    * [script](#script)
1. [Materials](#materials)
    * [dependency](#dependency)
    * [package](#package)
    * [git](#git)
    * [svn](#svn)
    * [perforce](#perforce)
    * [tfs](#tfs)
    * [hg](#hg)
    * [pluggable scm](#pluggable)
    * [config repo](#configrepo)
1. [Secure variables](#to-generate-an-encrypted-value)
1. [YAML Aliases](#yaml-aliases)

# Pipeline

A minimal [pipeline](https://docs.gocd.org/current/configuration/configuration_reference.html#pipeline) configuration must contain:
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
 * [tracking_tool](#tracking-tool) or `mingle`
 * [timer](#timer)
 * [environment_variables](#environment-variables)
 * `secure_variables`
 * [materials](#materials)
 * [stages](#stage)

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

Please note:
 * [templates](https://docs.gocd.org/current/configuration/configuration_reference.html#templates) are not supported
 * [parameters](https://docs.gocd.org/current/configuration/configuration_reference.html#params) are not supported
 * pipeline declares a group to which it belongs

### Tracking tool

```yaml
tracking_tool:
  link: "http://your-trackingtool/yourproject/${ID}"
  regex: "evo-(\\d+)"
```

### Timer

```yaml
timer:
  spec: "0 15 10 * * ? *"
  only_on_changes: yes
```

See [XML reference](https://docs.gocd.org/current/configuration/configuration_reference.html#timer) for more information.

## Stage

A minimal stage must contain `jobs:` element or `tasks:` in [single-job stage case](single-job-stage).
```yaml
build:
  jobs:
    firstJob:
      ...
    secondJob:
      ...
```

A custom stage:
```yaml
test:
  fetch_materials: yes
  keep_artifacts: yes
  clean_workspace: yes
  approval:
    type: manual
    roles:
      - manager
    users:
      - john
  environment_variables:
    TEST_NUM: 1
  secure_variables:
    PASSWORD: "!@ESsdD323#sdu"
  jobs:
    one:
      ...
    two:
      ...
```

### Approval

Stage can have [approval](https://docs.gocd.org/current/configuration/configuration_reference.html#approval),
 which is `success` by default. There are 2 ways to declare approval:
```yaml
approval: manual
```
If you need to set associated users or roles:
```yaml
approval:
  type: manual
  roles:
    - manager
  users:
    - john
```

## Job

[Job](https://docs.gocd.org/current/configuration/configuration_reference.html#job) is a hash starting with jobs name:

```yaml
test:
  timeout: 5
  run_instances: 7
  environment_variables:
    LD_LIBRARY_PATH: .
  tabs:
    test: results.xml
  resources:
    - linux
  artifacts:
    - test:
        source: src
        destination: dest
    - build:
        source: bin
  properties:
    perf:
      source: test.xml
      xpath: "substring-before(//report/data/all/coverage[starts-with(@type,\u0027class\u0027)]/@value, \u0027%\u0027)"
  tasks:
    ...
```

*Note: timeout is added since 0.2.0 version of yaml plugin*

### Elastic agent profile id

Job configuration may define elastic agents profile id, as such:
```yaml
elastic_profile_id: "docker.unit-test"
```

It MUST NOT be specified along with `resources`.
Available in GoCD server since v16.12.0, yaml plugin 0.4.0.

### Run many instances

Part of job object can be [number of job to runs](https://docs.gocd.org/current/advanced_usage/admin_spawn_multiple_jobs.html):
```yaml
run_instances: 7
```
Or to run on all agents:
```yaml
run_instances: all
```

### Tabs

Tabs are a hash with `<tab-name>: <path>` pairs.
Path should exist in GoCD servers artifacts.
```yaml
tabs:
  tests: test-reports/index.html
  gauge: functional-reports/index.html
```

### Property

Job can have properties, declared as a hash:
```yaml
properties:
  cov1: # this is the name of property
    source: test.xml
    xpath: "substring-before(//report/data/all/coverage[starts-with(@type,\u0027class\u0027)]/@value, \u0027%\u0027)"
  performance.ind1.mbps:
    source: PerfTestReport.xml
    xpath: "//PerformanceSuiteReport/WriteOnly/MBps"
```

### Single job stage

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
Above configuration declares `build` **stage** with `build` **job** which executes `make` task.

## Materials

### Git

Minimal configuration of a [**git** pipeline material](https://docs.gocd.org/current/configuration/configuration_reference.html#git):
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

Since GoCD `>= 16.7.0` whitelist is also supported,
you can specify `whitelist` **instead** of `blacklist`, as such
```yaml
gitMaterial1:
  git: "git@my.git.repository.com"
  branch: "feature12"
  whitelist:
    - src/**/*.*
```

### Svn

For details about each option, see [GoCD XML reference](https://docs.gocd.org/current/configuration/configuration_reference.html#svn)
```yaml
svnMaterial1:
  svn: "http://svn"
  username: "user1"
  password: "pass1"
  check_externals: true
  blacklist:
    - tools
    - lib
  destination: destDir1
  auto_update: false
```

### Hg

```yaml
hgMaterial1:
  hg: repos/myhg
  blacklist:
    - externals
    - tools
  destination: dir1
  auto_update: false
```

### Perforce

```yaml
p4Material1:
  p4: "host.domain.com:12345"
  username: johndoe
  password: pass
  use_tickets: false
  view: |
    //depot/external... //ws/external...
    //depot/tools... //ws/external...
  blacklist:
    - externals
    - tools
  auto_update: false
```

Instead of `password` you can use `encrypted_password`.

### Tfs

*TODO: - not supported by yaml plugin yet*

### Pluggable

```yaml
myPluggableGit:
  scm: someScmGitRepositoryId
  destination: destinationDir
  blacklist:
    - dir1
    - dir2
```

### Configrepo

This is a convenience for shorter and more consistent material declaration.
When configuration repository is the same as one of pipeline materials,
then you usually need to repeat definitions in XML and in JSON, for example:

```yaml
materials:
  foo:
    git: "https://github.com/tomzo/gocd-json-config-example.git",
    branch: ci
```

And in server XML:
```xml
<config-repos>
   <config-repo pluginId="yaml.config.plugin" id="repo1">
     <git url="https://github.com/tomzo/gocd-json-config-example.git" branch="ci" />
   </config-repo>
</config-repos>
```

Notice that url and branch is repeated. This is inconvenient in case when you move repository,
because it requires 2 updates, in code and in server XML.

Using  **`configrepo` material type**, above repetition can be avoided,
last example can be refactored into:

```yaml
materials:
  foo:
    type: configrepo
```

Server interprets `configrepo` material in this way:

> Clone the material configuration of the repository we are parsing **as is in XML** and replace **name, destination and filters (whitelist/blacklist)**,
then use the modified clone in place of `configrepo` material.

### Dependency

To add a dependency on another pipeline stage:
```yaml
mydependency:
  pipeline: upstream-pipeline-1
  stage: test
```

**Note: `mydependency` is the name of material - it must be unique**

### Package

```yaml
myapt:
  package: apt-repo-id
```

## Tasks

Every task is a hash starting with its type.
Type can be `exec`, `ant`, `nant`, `rake`, `fetch`, `plugin` or `script`.

```yaml
<type>:
  <task-prop1>: <prop1-value>
```

Optionally any task can have `run_if` and `on_cancel`.

 * `run_if` is a string. Valid values are `passed`, `failed`, `any`
 * `on_cancel` is a task object. Same rules apply as to tasks described on this page.
 See [rake example](#rake).

### Exec

```yaml
exec:
  run_if: any
  working_directory: dir
  command: make
  arguments:
   - -j3
   - docs
   - install
```

### Ant

```yaml
ant:
  build_file: mybuild.xml
  target: compile
  run_if: passed
```

### Nant

```yaml
nant:
  run_if: passed
  working_directory: "script/build/123"
  build_file: FilePath
  target: Build
  nant_path: NantExe
```

### Rake

A minimal rake task with default values is very short
```yaml
rake:
```

A complete rake example:
```yaml
rake:
  run_if: passed
  working_directory: sample-project
  build_file: SomeRakefile
  target: build
  on_cancel:
    rake:
      working_directory: sample-project
      build_file: CancelRakefile
      target: cancel
```

### Fetch

```yaml
fetch:
  run_if: any
  pipeline: myupstream
  stage: upstream_stage
  job: upstream_job
  is_file: yes
  source: result
  destination: test
```

### Plugin

```yaml
plugin:
  options:
    ConverterType: jsunit
  secure_options:
    password: "ssd#%fFS*!Esx"
  run_if: failed
  configuration:
    id: xunit.converter.task.plugin
    version: 1
```

### Script

Because [script-executor plugin](https://github.com/gocd-contrib/script-executor-task)
requires quite a lot of boiler-plate configuration
there is a shorthand for defining tasks with it:
```yaml
script: ./build.sh compile
```
Above is equivalent of
```yaml
plugin:
  options:
    script: ./build.sh compile
  configuration:
    id: script-executor
    version: 1
```

You can declare **multi-line scripts** too:
```yaml
script: |
  ./build.sh compile
  make test
```
Above executes a 2-line script:
```bash
./build.sh compile
make test
```

If you want to **execute** a single *long* line script, but break it into multiple lines in YAML,
you can do this with `>` as such:
```yaml
script: >
  ./build.sh compile &&
  make test
```
Above executes a **single line** script:
```bash
./build.sh compile && make test
```

## Environment

*NOTE: The agents should be a guid, which is currently impossible to get for user*

```yaml
testing:
  environment_variables:
    DEPLOYMENT: testing
  secure_variables:
    ENV_PASSWORD: "s&Du#@$xsSa"
  pipelines:
    - example-deploy-testing
    - build-testing
  agents:
    - 123
```

### Environment variables

[Environment variables](https://docs.gocd.org/current/configuration/configuration_reference.html#environmentvariables)
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

#### To generate an encrypted value

**For versions of GoCD >= 17.1:**

See the [encryption API](https://api.gocd.org/current/#encrypt-a-plain-text-value).

**For versions of GoCD <= 16.12:**

> There is no easy way to generate obtain [encrypted value](https://github.com/tomzo/gocd-yaml-config-plugin/issues/5) from GoServer, alternatively you can login into go-server and execute the following command to generate encrypted value

```sh
sudo -u go bash -c 'echo -n 'YOUR-INPUT' | openssl enc -des-cbc -a -iv 0 -K $(cat /etc/go/cipher)'
```

### Boolean values

Among all configuration elements there are boolean values, which can be defined
using any of the keywords below (as in [yaml specs](http://yaml.org/type/bool.html)):
 * **true** - `y|Y|yes|Yes|YES|true|True|TRUE|on|On|ON`
 * **false** - `n|N|no|No|NO|false|False|FALSE|off|Off|OFF`

 ## YAML Aliases

YAML Aliases ([specification](http://www.yaml.org/spec/1.2/spec.html#id2786196)) are supported and provide a way to avoid duplication.

Aliases can be defined anywhere in the configuration as long as they are valid configuration elements.

```yaml
- exec:
  command: make
  arguments:
   - clean
   - &verbose_arg "VERBOSE=true" # define the alias
- exec:
  command: make
  arguments:
   - world
   - *verbose_arg # use the alias
```

There is also a dedicated top-level `common` section which allows you to have all aliases in one place and where you don't need to worry about correct placement within the configuration.

```yaml
common:
  verbose_arg: &verbose_arg "VERBOSE=true"
  build_tasks: &build_tasks
    - exec:
        command: make
        arguments:
         - clean
    - exec:
        command: make
        arguments:
         - world
pipelines:
  pipe1:
    stages:
      - build:
          jobs:
            build:
              tasks: *build_tasks
            test:
              tasks:
               - *build_tasks # task list aliases can also be mixed with additional tasks in the same job
               - exec:
                   command: make
                   arguments:
                    - test
```

# Development

Run all tests and create a ready to use jar
```bash
./gradlew test fatJar
```

## Tests structure

There are [examples of yaml partials](src/test/resources/parts) and
 their resulting json to be sent to GoCD server. If something is not working right
 we can always add a new case covering exact yaml that user has and json that we
 expect on server side.
