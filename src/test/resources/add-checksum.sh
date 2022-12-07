#!/bin/bash

for f in $(find ./repository -type f -not -name "*.sha1");
do 
	outfile="${f}.sha1"
	sha1sum $f | awk '{print $1}' > $outfile 
	echo "Written ${outfile}";
done;
