#!/bin/bash

#蒲公英:http://pgyer.com/
#账号：ouyangshima@163.com
#密码：xcsd123456

#1. create folder

if [ ! -d ~/genPackage ];then
  mkdir -p ~/genPackage
fi

if [ ! -d ~/genPackage/Android ];then
  mkdir -p ~/genPackage/Android
fi
pkgPath=~/genPackage/Android

#1. 版本号增加1

mainVersion=1.4
file="version.properties"
versionCode=`awk -F= '/versionCode/{print $2}' $file`
versionCode=$(($versionCode+1))
echo "versionCode=$versionCode"

versionNameNo=`awk -F="${mainVersion}." '/versionName/{print $2}' $file`
versionNameNo=$(($versionNameNo+1))
versionName="${mainVersion}.${versionNameNo}"
echo "versionName=$versionName"

echo "versionCode=$versionCode\nversionName=$versionName" > $file


#2. build 

buildDayTime=$(date +'%Y-%m-%d_%H%M%S')
apk_parent="${pkgPath}/lxt-parent-v${versionName}-${versionNameNo}_${buildDayTime}.apk"
apk_teacher="${pkgPath}/lxt-teacher-v${versionName}-${versionNameNo}_${buildDayTime}.apk"
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

# echo '打包成功'
# exit 1

####获取当前时间
date_Y_M_D_W_T()
{
    WEEKDAYS=(星期日 星期一 星期二 星期三 星期四 星期五 星期六)
    WEEKDAY=$(date +%w)
    DT="$(date +%Y年%m月%d日) ${WEEKDAYS[$WEEKDAY]} $(date "+%H:%M:%S")"
    echo "$DT 更新测试包"
}
BINARY_CHANGELOG="$(date_Y_M_D_W_T)"

#3.上传apk-parent
APK_FILE=${apk_parent}
UKEY="2c8574ba1270e6b7f1923a7d01df1fa5"
API_KEY="c3aa98cecb17fea432f54176752e653f"

BINARY_INFO=`curl -# -F "file=@${APK_FILE}" \
-F "uKey=${UKEY}" \
-F "_api_key=${API_KEY}" \
-F "updateDescription=${BINARY_CHANGELOG}" \
https://www.pgyer.com/apiv1/app/upload`

echo ${BINARY_INFO}
if [ $? != 0 ];then
  echo "上传apk-parent失败"
  exit 1
fi
echo "成功上传到pgyer地址：https://www.pgyer.com/srup"


#4.上传apk_teacher
APK_FILE=${apk_teacher}
UKEY="2c8574ba1270e6b7f1923a7d01df1fa5"
API_KEY="c3aa98cecb17fea432f54176752e653f"

BINARY_INFO=`curl -# -F "file=@${APK_FILE}" \
-F "uKey=${UKEY}" \
-F "_api_key=${API_KEY}" \
-F "updateDescription=${BINARY_CHANGELOG}" \
https://www.pgyer.com/apiv1/app/upload`

echo ${BINARY_INFO}
if [ $? != 0 ];then
  echo "上传apk_teacher失败"
  exit 1
fi

echo "成功上传到pgyer地址：https://www.pgyer.com/lZBc"

