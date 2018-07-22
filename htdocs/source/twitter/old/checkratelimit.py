#!/usr/bin/python

import urllib2
import json
import time
import sys

if len(sys.argv) < 2 or sys.argv[1] != '-ip':
	username = "username"
	password = "********"
	passwordmgr = urllib2.HTTPPasswordMgrWithDefaultRealm()
	toplevelurl = "http://twitter.com/"
	passwordmgr.add_password(None, toplevelurl, username, password)
	handler = urllib2.HTTPBasicAuthHandler(passwordmgr)
	opener = urllib2.build_opener(handler)
	urllib2.install_opener(opener)

url = "http://twitter.com/account/rate_limit_status.json"
f = urllib2.urlopen(url)
r = f.read()
data = json.loads(r)

print "Remaining requests: %s" % data['remaining_hits']
print "Hourly limit: %s" % data['hourly_limit']
print "Reset time: %s" % (time.strftime("%H:%M:%S", time.localtime(int(data['reset_time_in_seconds']))))
