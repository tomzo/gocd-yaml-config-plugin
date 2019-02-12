
If you are submitting a new feature then please do a **minor version bump** by
```
./tasks.sh set_version 0.X.0
```

If you are submitting a fix, then do not change any versions as patch bump is made right after each release.

PR should contain:
 - tests of new/changed behavior
 - documentation if adding new feature
 - added change summary in CHANGELOG.md
