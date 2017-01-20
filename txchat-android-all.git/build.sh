#!/bin/bash

#1. create folder

current=$(cd `dirname $0`; pwd)

cd "$current"

rm -rf packages

mkdir packages
mkdir packages/release
mkdir packages/test

version_name=$1
build_no=$2

#2. build
pkg_category=("test" "release")
test=("true" "false")
h5_host_list=("http:\/123.57.43.111:8080\/" "http:\/\/h5.tx2010.com\/")

for i in {0..1}
do
    pkg=${pkg_category[$i]}
    is_test=${test[$i]}
    h5_host=${h5_host_list[$i]}

    sed -i "s/isTest =.*/isTest = ${is_test};/" common/src/main/java/com/tuxing/app/util/SysConstants.java
    sed -i "s/H5_HOST_URL =.*/H5_HOST_URL = \"${h5_host}\";/" common/src/main/java/com/tuxing/app/util/SysConstants.java

    ./gradlew clean
    ./gradlew build

    \cp home/build/outputs/apk/home-release.apk packages/${pkg}/wjy-home-v${version_name}-${build_no}.apk
    \cp teacher/build/outputs/apk/teacher-release.apk packages/${pkg}/wjy-teacher-v${version_name}-${build_no}.apk
done















