#!/usr/bin/python

import glob, os, re

data = {}

for file in glob.glob("**/*.md"):
	#print(re.sub(r'(.*)/.*\.md', r'\1', file))
	m = re.search('(.*)/(.*)\.md', file)
	dir = m.group(1)
	file = m.group(2)
	if not dir in data:
		data[dir] = []
	data[dir].append(file)
	
for key in data:
	#print "- " + key + ":"
	#for value in sorted(data[key]):
		#print "    - '" + key + "/" + value + ".md'"
	print "[" + key + "]" + "(" + key + ")"
	
	