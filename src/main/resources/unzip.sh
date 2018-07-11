#!/bin/bash
echo "----------begin to unzip files---------"
# upzip all *.zip
ls *.zip | xargs -n1 unzip -o -P infected
echo "----------finish unzip files---------"
cur=$(pwd)
echo "current path: "${cur}
echo "----------begin to convert hprof files----------"
# make files dir
mkdir files
# get all *.hprof files in current dir
filenames=$(ls *.hprof)
for file in ${filenames};do
    echo "current file:"${file}
    # convert the *.hprof to standard hprof file whith AndroidSdk tools
    hprof-conv ./${file} ./files/"standard_"${file}
done
echo "---------finish convert hprof file----------"

