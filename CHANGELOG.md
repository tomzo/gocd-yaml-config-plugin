### 0.12.0 (2019-Oct-01)

* Added support for `ignore_for_scheduling` to dependency materials. Updated README with changes in new Format Versions.

### 0.11.2 (2019-Aug-22)

* updated README documenting `allow_only_on_success` attribute for approval in stages

### 0.11.1 (2019-Aug-21)

Release deleted.

### 0.11.0 (2019-Aug-02)

* Adding endpoint that lists config files for the given directory

### 0.10.2 (2019-May-22)

 * updated README and tests to support credentials attributes in materials

### 0.10.1 (2019-May-01)

 * switch build system to use open source openjdk-dojo image \#17574
 * remove docker image from this repo, use [new image](https://github.com/gocd-contrib/docker-gocd-cli-dojo) with gocd-cli

### 0.10.0 (2019-Apr-05)

 * Add support for `display_order` at pipeline level \#114

### 0.9.1 (2019-Apr-05)

 * fixes export of external artifacts

### 0.9.0 (2019-Feb-12)

 * automate releases and version bump
 * support nested lists of stages \#95
 * added syntax to configure new scms \#109

### 0.8.6 (2019-Jan-21)

 * Changed JSON keys returned by `get-capabilities` call
 * Changed JSON structure returned by `parse-content` call
 * Implemented a new `get-icon` call that will return the icon for this plugin

### 0.8.5 (2019-Jan-15)

 * return json from CLI command

### 0.8.4 (2019-Jan-09)

 * Add export content metadata
 * Fix plugin settings request and implement handler for plugin config change notification

### 0.8.3 (2019-Jan-03)

 * Added support for `parse-content`.

### 0.8.2 (2018-Dec-12)

 * accept stdin input in cli tool

### 0.8.1 (2018-Nov-30)

 * adds CLI for syntax checking
 * adds docker image with the CLI to releases

### 0.8.0 (2018-Nov-09)

 * added inverse transforms and config-repo 2.0 API support
 * updated to gradle 4.10.2

### 0.7.0 (2018-Jul-09)

 * introduces `format_version: 3` and external fetch task

### 0.6.2 (2018-Mar-13)

 * fix ability to set custom file pattern per configuration repository

### 0.6.1 (2018-Feb-23)

 * switch to EsotericSoftware's yamlbeans

### 0.6.0 (2017-Oct-26)

 * adds support for referencing templates and specifying parameters
 * adds better support for YAML aliases
 * introduces format_version field, with future work for v2 format

### 0.5.0 (2017-Sep-15)

 * new release build tasks
 * added configrepo material type
 * added p4 material type support

### 0.4.0 (2016-Dec-16)

 * fixed IO error reporting
 * added elastic_profile_id in job

### 0.3.0 (2016-Nov-07)

 * added validation of duplicate keys

### 0.2.0 (2016-Oct-21)

* added optional `timeout` field to job

### 0.1.0 (2016-Jul-16)

Initial release.
