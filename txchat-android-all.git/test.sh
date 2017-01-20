mainVersion=1.4
file="version.txt"
versionCode=`awk -F= '/versionCode/{print $2}' $file`
versionCode=$(($versionCode+1))
echo $versionCode

versionNameNo=`awk -F="${mainVersion}." '/versionName/{print $2}' $file`
versionNameNo=$(($versionNameNo+1))
versionName="${mainVersion}.${versionNameNo}"
echo $versionName

echo "versionCode=$versionCode\nversionName=$versionName" > $file


