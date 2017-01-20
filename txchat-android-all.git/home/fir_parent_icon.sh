#上传到fir


####获取上传基本信息
USER_TOKEN="ee71ba0f10dae25b74659e976c470e73"
APP_ID="56fccf6e00fc745885000040"
APP_TYPE="android"
ICON_FILE="$(pwd)/fir_icon.png"

#POST请求获取上传凭证
APP_INFO=`curl -d "type=${APP_TYPE}&bundle_id=${APP_ID}&api_token=${USER_TOKEN}" http://api.fir.im/apps/${APP_ID}/releases 2>/dev/null`
# echo ${APP_INFO}

#上传Icon
echo "uploading icon"
ICON_KEY=$(echo ${APP_INFO} | grep "key.*binary" -o | awk -F '"' '{print $3;}')
ICON_TOKEN=$(echo ${APP_INFO} | grep "token.*token" -o | awk -F '"' '{print $3;}')

ICON_INFO=`curl -# -F file=@${ICON_FILE} -F "key=${ICON_KEY}" -F "token=${ICON_TOKEN}" http://upload.qiniu.com`
echo ${ICON_INFO}

if [ $? != 0 ]
then
  echo "上传Icon失败"
  exit 1
fi

echo "成功上传到fir地址：http://fir.im/jyjparentAndroid"