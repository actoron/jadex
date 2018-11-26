#!/bin/bash
echo "Please make sure 'broken-link-checker' is installed: https://www.npmjs.com/package/broken-link-checker"

blc -e -r --exclude URLJavaDoc --exclude URLACDownloadPage --exclude URLJadexExamples --exclude URLJadexForum --exclude URLPlatformConfigDoc --exclude URLRelay --exclude URLLegacyDoc --exclude www http://localhost:8000 > links.txt
