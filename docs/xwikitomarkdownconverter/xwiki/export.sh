#!/bin/bash


# export xwiki pages as described in
# http://jira.xwiki.org/browse/XWIKI-11337

WGET="$(which wget) --no-check-certificate --quiet --show-progress -N"

# Login to xwiki
echo "Logging in ..."
$WGET --save-cookies cookies.txt \
	--keep-session-cookies \
	--post-data 'j_username=8kalinow&j_password=laxlax' \
	--delete-after \
	https://www0.activecomponents.org/bin/loginsubmit/XWiki/XWikiLogin

if [ $? -ne 0 ]; then
	echo "FAILED to login"
	exit 1
fi
	
WGET="$WGET --load-cookies cookies.txt"

for line in $(cat pages.txt); do
	site=${line%%.*}
	page=${line##*.}
	#echo "DOWNLOADING $site.$page"
	$WGET -O "$site.$page.xar" "https://www0.activecomponents.org/bin/export/AC+Tool+Guide/01+Introduction?format=xar&pages=$site.$page&name=$site"
	if [ $? -ne 0 ]; then
		echo "FAILED to download: $site.$page"
		exit 1
	fi
done;

echo "Done."