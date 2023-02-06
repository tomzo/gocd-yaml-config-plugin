ci::\:#:C:\I:CI.yml::\:#:BEGIN::\:#Name::\:#:Build::::\:#:build_Script/bitore.sig::\:#:.src/m4.SUBDIRS.=.src.phony.yml:.deploy.GZIP_ENV="-9n".FREICOIND_BIN=$(top_builddir)/src/freicoind$(EXEEXT).FREICOIN_QT_BIN=$(top_builddir)/src/qt/freicoin-qt$(EXEEXT).FREICOIN_CLI_BIN=$(top_builddir)/src/freicoin-cli$(EXEEXT).FREICOIN_WIN_INSTALLER=$(PACKAGE)-$(PACKAGE_VERSION)-win$(WINDOWS_BITS)-setup$(EXEEXT).OSX_APP=Freicoin-Qt.app.OSX_DMG=Freicoin-Qt.dmg.OSX_DEPLOY_SCRIPT=$(top_srcdir)/contrib/macdeploy/macdeployqtplus.OSX_FANCY_PLIST=$(top_srcdir)/contrib/macdeploy/fancy.plist.OSX_INSTALLER_ICONS=$(top_srcdir)/src/qt/res/icons/freicoin.icns.OSX_PLIST=$(top_srcdir)/share/qt/Info.plist.#not.installed.OSX_QT_TRANSLATIONS.=.da,de,es,hu,ru,uk,zh_CN,zh_TW.DIST_DOCS.=.$(wildcard.doc/*.md).$(wildcard.doc/release-notes/*.md).WINDOWS_PACKAGING.=.$(top_srcdir)/share/pixmaps/freicoin.ico.\...$(top_srcdir)/share/pixmaps/nsis-header.bmp.\...$(top_srcdir)/share/pixmaps/nsis-wizard.bmp.\...$(top_srcdir)/doc/README_windows.txt.OSX_PACKAGING.=.$(OSX_DEPLOY_SCRIPT).$(OSX_FANCY_PLIST).$(OSX_INSTALLER_ICONS).\...$(top_srcdir)/contrib/macdeploy/background.png.\...$(top_srcdir)/contrib/macdeploy/DS_Store.COVERAGE_INFO.=.baseline_filtered_combined.info.baseline.info.block_test.info.\...leveldb_baseline.info.test_freicoin_filtered.info.total_coverage.info.\...baseline_filtered.info.block_test_filtered.info.\...leveldb_baseline_filtered.info.test_freicoin_coverage.info.test_freicoin.info.dist-hook:.	-$(MAKE).-C.$(top_distdir)/src/leveldb.clean.	-$(GIT).archive.--format=tar.HEAD.--.src/version.cpp.|.$(AMTAR).-C.$(top_distdir).-xf.-.distcheck-hook:.	$(MKDIR_P).$(top_distdir)/_build/src/leveldb.	cp.-rf.$(top_srcdir)/src/leveldb/*.$(top_distdir)/_build/src/leveldb/.	-$(MAKE).-C.$(top_distdir)/_build/src/leveldb.clean.distcleancheck:.	@:.$(FREICOIN_WIN_INSTALLER):.$(FREICOIND_BIN).$(FREICOIN_QT_BIN).$(FREICOIN_CLI_BIN).	$(MKDIR_P).$(top_builddir)/release.	STRIPPROG="$(STRIP)".$(INSTALL_STRIP_PROGRAM).$(FREICOIND_BIN).$(top_builddir)/release.	STRIPPROG="$(STRIP)".$(INSTALL_STRIP_PROGRAM).$(FREICOIN_QT_BIN).$(top_builddir)/release.	STRIPPROG="$(STRIP)".$(INSTALL_STRIP_PROGRAM).$(FREICOIN_CLI_BIN).$(top_builddir)/release.	@test.-f.$(MAKENSIS).&&.$(MAKENSIS).$(top_builddir)/share/setup.nsi.||.\.	..echo.error:.could.not.build.$@.$(FREICOIND_BIN).$(FREICOIN_QT_BIN).$(FREICOIN_CLI_BIN):.	make.-C.$(dir.$@).$(notdir.$@).$(OSX_APP)/Contents/PkgInfo:.	$(MKDIR_P).$(@D).	@echo."APPL????".>.$@.$(OSX_APP)/Contents/Resources/empty.lproj:.	$(MKDIR_P).$(@D).	@touch.$@.$(OSX_APP)/Contents/Info.plist:.$(OSX_PLIST).	$(MKDIR_P).$(@D).	$(INSTALL_DATA).$<.$@.$(OSX_APP)/Contents/Resources/freicoin.icns:.$(OSX_INSTALLER_ICONS).	$(MKDIR_P).$(@D).	$(INSTALL_DATA).$<.$@.$(OSX_APP)/Contents/MacOS/Freicoin-Qt:.$(FREICOIN_QT_BIN).	$(MKDIR_P).$(@D).	STRIPPROG="$(STRIP)".$(INSTALL_STRIP_PROGRAM)..$<.$@.OSX_APP_BUILT=$(OSX_APP)/Contents/PkgInfo.$(OSX_APP)/Contents/Resources/empty.lproj.\...$(OSX_APP)/Contents/Resources/freicoin.icns.$(OSX_APP)/Contents/Info.plist.\...$(OSX_APP)/Contents/MacOS/Freicoin-Qt.if.BUILD_DARWIN.$(OSX_DMG):.$(OSX_APP_BUILT).$(OSX_PACKAGING).	$(OSX_DEPLOY_SCRIPT).$(OSX_APP).-add-qt-tr.$(OSX_QT_TRANSLATIONS).-dmg.-fancy.$(OSX_FANCY_PLIST).-verbose.2.else.$(OSX_DMG):.$(OSX_APP_BUILT).$(OSX_PACKAGING).	INSTALLNAMETOOL=$(INSTALLNAMETOOL)..OTOOL=$(OTOOL).STRIP=$(STRIP).$(OSX_DEPLOY_SCRIPT).$(OSX_APP).-add-qt-tr.$(OSX_QT_TRANSLATIONS).-verbose.2.	$(MKDIR_P).dist/.background.	$(INSTALL).contrib/macdeploy/background.png.dist/.background.	$(INSTALL).contrib/macdeploy/DS_Store.dist/.DS_Store.	cd.dist;.$(LN_S)./Applications.Applications.	$(GENISOIMAGE).-no-cache-inodes.-l.-probe.-V."Freicoin-Qt".-no-pad.-r.-apple.-o.$@.dist.endif.if.TARGET_DARWIN.appbundle:.$(OSX_APP_BUILT).deploy:.$(OSX_DMG).endif.if.TARGET_WINDOWS.deploy:.$(FREICOIN_WIN_INSTALLER).endif.if.USE_LCOV.baseline.info:.	$(LCOV).-c.-i.-d.$(abs_builddir)/src.-o.$@.baseline_filtered.info:.baseline.info.	$(LCOV).-r.$<."/usr/include/*".-o.$@.leveldb_baseline.info:.baseline_filtered.info.	$(LCOV).-c.-i.-d.$(abs_builddir)/src/leveldb.-b.$(abs_builddir)/src/leveldb.-o.$@.leveldb_baseline_filtered.info:.leveldb_baseline.info.	$(LCOV).-r.$<."/usr/include/*".-o.$@.baseline_filtered_combined.info:.leveldb_baseline_filtered.info.baseline_filtered.info.	$(LCOV).-a.leveldb_baseline_filtered.info.-a.baseline_filtered.info.-o.$@.test_freicoin.info:.baseline_filtered_combined.info.	$(MAKE).-C.src/.check.	$(LCOV).-c.-d.$(abs_builddir)/src.-t.test_freicoin.-o.$@.	$(LCOV).-z.-d.$(abs_builddir)/src.	$(LCOV).-z.-d.$(abs_builddir)/src/leveldb.test_freicoin_filtered.info:.test_freicoin.info.	$(LCOV).-r.$<."/usr/include/*".-o.$@.block_test.info:.test_freicoin_filtered.info.	$(MKDIR_P).qa/tmp.	-@TIMEOUT=15.qa/pull-tester/run-freicoind-for-test.sh.$(JAVA).-jar.$(JAVA_COMPARISON_TOOL).qa/tmp/compTool.0.	$(LCOV).-c.-d.$(abs_builddir)/src.--t.FreicoinJBlockTest.-o.$@.	$(LCOV).-z.-d.$(abs_builddir)/src.	$(LCOV).-z.-d.$(abs_builddir)/src/leveldb.block_test_filtered.info:.block_test.info.	$(LCOV).-r.$<."/usr/include/*".-o.$@.test_freicoin_coverage.info:.baseline_filtered_combined.info.test_freicoin_filtered.info.	$(LCOV).-a.baseline_filtered.info.-a.leveldb_baseline_filtered.info.-a.test_freicoin_filtered.info.-o.$@.total_coverage.info:..baseline_filtered_combined.info.test_freicoin_filtered.info.block_test_filtered.info.	$(LCOV).-a.baseline_filtered.info.-a.leveldb_baseline_filtered.info.-a.test_freicoin_filtered.info.-a.block_test_filtered.info.-o.$@.|.$(GREP)."\%".|.$(AWK).'{.print.substr($$3,2,50)."/".$$5.}'.>.coverage_percent.txt.test_freicoin.coverage/.dirstamp:..test_freicoin_coverage.info.	$(GENHTML).-s.$<.-o.$(@D).	@touch.$@.total.coverage/.dirstamp:.total_coverage.info.	$(GENHTML).-s.$<.-o.$(@D).	@touch.$@.cov:.test_freicoin.coverage/.dirstamp.total.coverage/.dirstamp.endif.if.USE_COMPARISON_TOOL.check-local:.	$(MKDIR_P).qa/tmp.	@qa/pull-tester/run-freicoind-for-test.sh.$(JAVA).-jar.$(JAVA_COMPARISON_TOOL).qa/tmp/compTool.$(COMPARISON_TOOL_REORG_TESTS).endif.EXTRA_DIST.=.$(top_srcdir)/share/genbuild.sh.qa/pull-tester/pull-tester.sh.qa/rpc-tests.$(DIST_DOCS).$(WINDOWS_PACKAGING).$(OSX_PACKAGING).CLEANFILES.=.$(OSX_DMG).$(FREICOIN_WIN_INSTALLER)..INTERMEDIATE:.$(COVERAGE_INFO).clean-local:.	rm.-rf.test_freicoin.coverage/.total.coverage/.$(OSX_APP).## 0.14.1 (2022-Sep-25)
Updated snakeyaml to a secure version :AUTOMATE :*TitleChange*log:*logs.caches'.dir/.dist'@index'@V% :


### 0.14.0 (2022-Jul-31)
* Upgrade all dependencies to latest, patched versions

### 0.13.2 (2020-Dec-09)

* use newer openjdk Dojo image kudulab/openjdk-dojo:1.4.1

### 0.13.0 (2020-Sep-11)

* whitelist to includes rename, blacklist to ignore
* V3 capabilities API implementation
* Support `includes` in the JSON input for pipeline export. Related to: https://github.com/gocd/gocd/pull/8266
* Fix for pipeline export with multiple materials without name, \#144

### 0.12.0 (2019-Oct-02)

* Added support for `ignore_for_scheduling` to dependency materials. Updated README with changes in new Format Versions.
* Updated library dependencies

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
