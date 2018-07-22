# Twitter profile image updater

# http://twitter.com/account/update_profile_image.json
# image = [imagefile]

import sys
import os
import pycurl
import random
import re
import urllib
import json
import codecs
import time

stream = ""

def getit(body):
	global received
	received += body
	return len(body)
	
def parse(body):
	tj = codecs.getwriter('utf8')(open('timeline.json', 'a'))
	print >>tj, body
	tj.close()
	data = json.loads(body)
	if len(data) == 0:
		exit(0)
	return data

def streamit(body):
	global stream
	if body == "\n":
		return len(body)
	stream += body
	stream = stream.strip()
	#print stream[:2]
	#print stream[-2:]
	#print stream
	if stream.startswith('{') and stream.endswith('}'):
		tweet = json.loads(stream)
		timestamp = time.strptime(tweet['created_at'], "%a %b %d %H:%M:%S +0000 %Y")
		timestamp = time.strftime("%H:%M:%S", timestamp)
		print "%s <%s> %s  [http://twitter.com/%s/status/%s]" % (timestamp, tweet['user']['screen_name'], tweet['text'], tweet['user']['screen_name'], tweet['id'])
		stream = ""
	return len(body)
	
os.chdir("/home/restorer/Documents/devel/twitter/logs/")

username = "username"
password = "********"

lastid = open('timeline.id', 'r').readline().strip()
if not lastid or lastid == '\n':
	log = open('timeline.log', 'r')
	lasttweet = ""
	for line in log:
		lasttweet = line
	m = re.search(r"/status/(\d+)>", lasttweet)
	if not m:
		print "Problem with lastid"
		exit(1)
	lastid = int(m.group(1))

received = ""

url = 'https://twitter.com/friends/ids.json'
head = ['Expect: ']
data = [('screen_name', username)]
postdata = urllib.urlencode(data)
url = url+"?"+postdata
c = pycurl.Curl()
c.setopt(pycurl.URL, url)
c.setopt(pycurl.USERPWD, "%s:%s" % (username, password))
c.setopt(pycurl.HTTPHEADER, head)
#c.setopt(pycurl.POSTFIELDS, postdata)
c.setopt(pycurl.WRITEFUNCTION, getit)
c.perform()
c.close()

following = parse(received)

received = ""

url = 'https://twitter.com/users/show.json'
head = ['Expect: ']
data = [('screen_name', username)]
postdata = urllib.urlencode(data)
url = url+"?"+postdata
c = pycurl.Curl()
c.setopt(pycurl.URL, url)
c.setopt(pycurl.USERPWD, "%s:%s" % (username, password))
c.setopt(pycurl.HTTPHEADER, head)
#c.setopt(pycurl.POSTFIELDS, postdata)
c.setopt(pycurl.WRITEFUNCTION, getit)
c.perform()
c.close()

userdata = parse(received)
following.append(userdata['id'])
print ','.join([str(f) for f in following])

url = "http://stream.twitter.com/1/statuses/filter.json"
#head = ['Expect: ']
data = [('follow', (','.join([str(f) for f in following])))]
postdata = urllib.urlencode(data)
print postdata
c = pycurl.Curl()
c.setopt(pycurl.URL, url)
c.setopt(pycurl.USERPWD, "%s:%s" % (username, password))
c.setopt(pycurl.HTTPHEADER, head)
c.setopt(pycurl.POSTFIELDS, postdata)
c.setopt(pycurl.WRITEFUNCTION, streamit)
c.perform()
c.close()
"""
while True:
	if len(stream) > 100:
		print stream
		stream = ""
	time.sleep(2)
"""
