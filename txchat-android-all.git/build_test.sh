#!/bin/bash

#1. create folder

if [ ! -d ~/genPackage ];then
  mkdir -p ~/genPackage
fi

if [ ! -d ~/genPackage/Android ];then
  mkdir -p ~/genPackage/Android
fi
pkgPath=~/genPackage/Android

version_name=1.4.8
build_no=28

# version_name=$0
# build_no=$1

#2. build 

buildDayTime=$(date +'%Y-%m-%d_%H%M%S')
apk_parent="${pkgPath}/lxt-parent-v${version_name}-${build_no}_${buildDayTime}.apk"
apk_teacher="${pkgPath}/lxt-teacher-v${version_name}-${build_no}_${buildDayTime}.apk"
./gradlew clean
./gradlew build

\cp home/build/outputs/apk/home-release.apk ${apk_parent}
\cp teacher/build/outputs/apk/teacher-release.apk ${apk_teacher}



if [ ! -f ${apk_parent} ];then
  echo "apk_parent not exist!!!"
  exit 1
fi


if [ ! -f ${apk_teacher} ];then
  echo "apk_teacher not exist!!!"
  exit 1
fi

#exit 1

#3.上传apk-parent
#API_TOKEN和APP_ID，在fir.im网站上注册一个账号，新建一个项目就，有这两个变量了
API_TOKEN="24b45603fad80e915bc7bc7702623f6f"
APP_ID="57356dcee75e2d7f2a000039"
APP_TYPE="android"
BINARY_NAME="乐学堂"
BINARY_VERSION=${version_name}
BINARY_BUILD=${build_no}
APK=${apk_parent}

####获取当前时间
date_Y_M_D_W_T()
{
    WEEKDAYS=(星期日 星期一 星期二 星期三 星期四 星期五 星期六)
    WEEKDAY=$(date +%w)
    DT="$(date +%Y年%m月%d日) ${WEEKDAYS[$WEEKDAY]} $(date "+%H:%M:%S")"
    echo "$DT 更新测试包"
}
BINARY_CHANGELOG="$(date_Y_M_D_W_T)"


#POST请求获取上传凭证
APP_INFO=`curl -d "type=${APP_TYPE}&bundle_id=${APP_ID}&api_token=${API_TOKEN}" http://api.fir.im/apps/${APP_ID}/releases 2>/dev/null`
# echo ${APP_INFO}

echo "uploading parent_apk"
BINARY_KEY=$(echo ${APP_INFO} | grep "binary.*token" -o | awk -F '"' '{print $5;}')
BINARY_TOKEN=$(echo ${APP_INFO} | grep "binary.*upload_url" -o | awk -F '"' '{print $9;}')

BINARY_INFO=`curl -# -F file=@${APK} -F "key=${BINARY_KEY}" -F "token=${BINARY_TOKEN}" -F "x:name=${BINARY_NAME}" -F "x:version=${BINARY_VERSION}" -F "x:build=${BINARY_BUILD}" -F "x:changelog=${BINARY_CHANGELOG}" http://upload.qiniu.com`

echo ${BINARY_INFO}
if [ $? != 0 ];then
  echo "上传IPA失败"
  exit 1
fi

echo "成功上传到fir地址：http://fir.im/lxtParentV1"


#4.上传apk-teacher
API_TOKEN="24b45603fad80e915bc7bc7702623f6f"
APP_ID="57356d05f2fc427a8a000018"
APP_TYPE="android"
BINARY_NAME="乐学堂"
BINARY_VERSION=${version_name}
BINARY_BUILD=${build_no}
APK=${apk_teacher}

####获取当前时间
date_Y_M_D_W_T()
{
    WEEKDAYS=(星期日 星期一 星期二 星期三 星期四 星期五 星期六)
    WEEKDAY=$(date +%w)
    DT="$(date +%Y年%m月%d日) ${WEEKDAYS[$WEEKDAY]} $(date "+%H:%M:%S")"
    echo "$DT 更新测试包"
}
BINARY_CHANGELOG="$(date_Y_M_D_W_T)"


#POST请求获取上传凭证
APP_INFO=`curl -d "type=${APP_TYPE}&bundle_id=${APP_ID}&api_token=${API_TOKEN}" http://api.fir.im/apps/${APP_ID}/releases 2>/dev/null`
# echo ${APP_INFO}

echo "uploading teacher_apk"
BINARY_KEY=$(echo ${APP_INFO} | grep "binary.*token" -o | awk -F '"' '{print $5;}')
BINARY_TOKEN=$(echo ${APP_INFO} | grep "binary.*upload_url" -o | awk -F '"' '{print $9;}')

BINARY_INFO=`curl -# -F file=@${APK} -F "key=${BINARY_KEY}" -F "token=${BINARY_TOKEN}" -F "x:name=${BINARY_NAME}" -F "x:version=${BINARY_VERSION}" -F "x:build=${BINARY_BUILD}" -F "x:changelog=${BINARY_CHANGELOG}" http://upload.qiniu.com`

echo ${BINARY_INFO}
if [ $? != 0 ];then
  echo "上传IPA失败"
  exit 1
fi

echo "成功上传到fir地址：http://fir.im/lxtTeacherV1"
