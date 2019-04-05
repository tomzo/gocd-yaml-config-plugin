### 0.9.1 (2019-Apr-05)

 * fixes export of external artifacts

### 0.9.0 (2019-Feb-12)

 * automate releases and version bump
 * support nested lists of stages \#95
 * added syntax to configure new scms \#109

# 0.8.6 (21 Jan 2019)

* Changed JSON keys returned by `get-capabilities` call
* Changed JSON structure returned by `parse-content` call
* Implemented a new `get-icon` call that will return the icon for this plugin

# 0.8.5 (15 Jan 2019)

 * return json from CLI command

# 0.8.4 (09 Jan 2019)

 * Add export content metadata
 * Fix plugin settings request and implement handler for plugin config change notification

# 0.8.3 (03 Jan 2019)

 * Added support for `parse-content`.

# 0.8.2 (12 Dec 2018)

 * accept stdin input in cli tool

# 0.8.1 (30 Nov 2018)

 * adds CLI for syntax checking
 * adds docker image with the CLI to releases

# 0.8.0 (9 Nov 2018)

 * added inverse transforms and config-repo 2.0 API support
 * updated to gradle 4.10.2

# 0.7.0 (9 Jul 2018)

 * introduces `format_version: 3` and external fetch task

# 0.6.2 (13 Mar 2018)

 * fix ability to set custom file pattern per configuration repository

# 0.6.1 (23 Feb 2018)

 * switch to EsotericSoftware's yamlbeans

# 0.6.0 (26 Oct 2017)

 * adds support for referencing templates and specifying parameters
 * adds better support for YAML aliases
 * introduces format_version field, with future work for v2 format

# 0.5.0 (15 Sep 2017)

 * new release build tasks
 * added configrepo material type
 * added p4 material type support

# 0.4.0 (16 Dec 2016)

 * fixed IO error reporting
 * added elastic_profile_id in job

# 0.3.0 (07 Nov 2016)

 * added validation of duplicate keys

# 0.2.0 (21 Oct 2016)

* added optional `timeout` field to job

# 0.1.0 (16 Jul 2016)

Initial release.
