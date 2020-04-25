#!/usr/bin/env bash

source ./common.sh

ADBPATH=/Users/kcn3hi/Library/Android/sdk/platform-tools/adb
PACKAGE_NAME=io.github.nuhkoca.vivy

IS_PACKAGE_EXISTS=$($ADBPATH shell pm list packages $PACKAGE_NAME)
echo "pm list package returned: $IS_PACKAGE_EXISTS "

rm -f ./apk/vivy-release.apks
bundletool build-apks --bundle=./apk/release/app-release.aab --output=./apk/vivy-release.apks --mode=universal --ks=./keystore/release.jks --ks-pass pass:vivyrelease --ks-key-alias=Vivy --key-pass pass:vivyrelease

if test -n "$IS_PACKAGE_EXISTS"; then
  adb uninstall io.github.nuhkoca.vivy
fi

bundletool install-apks --apks=./apk/vivy-release.apks
