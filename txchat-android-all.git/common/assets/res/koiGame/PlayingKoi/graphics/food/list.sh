#! /bin/sh
for eachfile in `ls -B`
do
  if [ ! -d $eachfile ]
  then
	  filename=`echo $eachfile | awk -F .png '{print $1 }'`
	  echo "$filename : \"res/PlayingKoi/graphics/food/$eachfile\","
  fi
done
