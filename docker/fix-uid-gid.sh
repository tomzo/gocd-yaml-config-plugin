#!/bin/bash

###########################################################################
# This file ensures that ide user has the same uid and gid as /ide/work directory.
# Used as fix-uid-gid solution in docker, almost copied from:
# https://github.com/tomzo/docker-uid-gid-fix/blob/master/fix-uid-gid.sh
###########################################################################

ide_work="${ide_work:-/ide/work}"
ide_home="${ide_home:-/home/ide}"
owner_username="${owner_username:-ide}"
owner_groupname="${owner_groupname:-ide}"

if [ ! -d "$ide_work" ]; then
  echo "$ide_work does not exist, expected to be mounted as docker volume"
  exit 1;
fi

ret=false
getent passwd "$owner_username" >/dev/null 2>&1 && ret=true
if ! $ret; then
    echo "User $owner_username does not exist"
    exit 1;
fi

ret=false
getent passwd "$owner_groupname" >/dev/null 2>&1 && ret=true
if ! $ret; then
    echo "Group $owner_groupname does not exist"
    exit 1;
fi

# use -n option which is the same as --numeric-uid-gid on Debian/Ubuntu,
# but on Alpine, there is no --numeric-uid-gid option
newuid=$(ls -n -d "$ide_work" | awk '{ print $3 }')
newgid=$(ls -n -d "$ide_work" | awk '{ print $4 }')

usermod -u "$newuid" "$owner_username"
groupmod -g "$newgid" "$owner_groupname"
