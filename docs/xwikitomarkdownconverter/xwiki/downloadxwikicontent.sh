#!/bin/bash


# export xwiki pages as described in
# http://jira.xwiki.org/browse/XWIKI-11337

WGET="$(which wget) --no-check-certificate --quiet"

HOST="www0"

# Login to xwiki
echo "Logging in ..."
$WGET --save-cookies cookies.txt \
	--keep-session-cookies \
	--post-data 'j_username=8kalinow&j_password=laxlax' \
	--delete-after \
	https://$HOST.activecomponents.org/bin/loginsubmit/XWiki/XWikiLogin

if [ $? -ne 0 ]; then
	echo "FAILED to login"
	exit 1
fi
	
#WGET="$WGET --load-cookies cookies.txt --show-progress -N"
WGET="$WGET --load-cookies cookies.txt -N"

for line in $(cat pages.txt); do
	site=${line%%.*}
	page=${line##*.}
	echo "DOWNLOADING $site.$page"
	filename=$site.$page.xar
	if [ ! -s "$filename" ]; then
		url="https://$HOST.activecomponents.org/bin/export/AC+Tool+Guide/01+Introduction?format=xar&pages=$site.$page&name=$site"
		$WGET -O "$filename" "$url"
		if [ $? -ne 0 ]; then
			echo "FAILED to download: $site.$page from: $url"
		exit 1
	fi
	else 
		echo "File already exists, not downloading: $filename"
	fi
	
done;

rm cookies.txt
echo "Done."
