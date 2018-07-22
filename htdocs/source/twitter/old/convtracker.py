#!/usr/bin/python

import sys
import time
import codecs
import urllib2
import urllib
import json
import getpass

# Configuration options
username = "username"
password = getpass.getpass()
starttime = time.strptime("Nov 10 2009", "%b %d %Y")
usecached = False

if not usecached:
	passwordmgr = urllib2.HTTPPasswordMgrWithDefaultRealm()
	toplevelurl = "http://twitter.com/"
	passwordmgr.add_password(None, toplevelurl, username, password)
	handler = urllib2.HTTPBasicAuthHandler(passwordmgr)
	opener = urllib2.build_opener(handler)
	urllib2.install_opener(opener)

	baseurl = 'http://twitter.com/statuses/friends.json'
	args = {'screen_name' : username} # cursor will be needed if friends > 100

	url = baseurl+'?'+urllib.urlencode(args)
	f = urllib2.urlopen(url)
	r = f.read()
	data = json.loads(r)
	friends = [username]
	for d in data:
		friends.append(d['screen_name'])

	baseurl = 'http://twitter.com/statuses/user_timeline.json'
	args = {'screen_name' : username, 'count' : '100', 'page' : '1'}
	nicks = [username]
	depth = 0

	tweets = {'WasabiFlux' : []}

	for n in nicks:
		args['screen_name'] = n
		while True:
			url = baseurl+'?'+urllib.urlencode(args)
			f = None
			while not f:
				try:
					f = urllib2.urlopen(url)
				except urllib2.HTTPError, e:
					if e.code == 401:
						print "Authorization incorrect. Cannot retrieve some feeds because they are protected."
						sys.exit(1)
					if e.code == 403:
						print "API limit reached. Waiting 2 minutes before trying again."
						time.sleep(120)
					elif e.code == 500:
						print "Internal server error, something may be wrong. If this error continues, there is a problem."
						time.sleep(10)
					elif e.code == 502 or e.code == 503:
						time.sleep(5)
			r = f.read()
			data = json.loads(r)
			if len(data) == 0 or time.strptime(data[-1]['created_at'], "%a %b %d %H:%M:%S +0000 %Y") < starttime:
				break
			tweets[n] += data
			args['page'] = str(int(args['page'])+1)
		print len(tweets[n])
		for t in tweets[n]:
			rep = t['in_reply_to_screen_name']
			if rep != None and rep in friends and rep not in nicks:
				nicks.append(rep)
				tweets[rep] = []
		args['page'] = '1'


	save = open("convos.json", 'w')
	json.dump(tweets, save)
	save.close()
	
else:
	save = open("convos.json", 'r')
	tweets = json.load(save)
	save.close()

def byid(a,b):
	return int(b['id']-a['id'])

from itertools import chain
alltweets = sorted(chain(*tweets.itervalues()), cmp=byid)
tweetmap ={}

for t in alltweets:
	t['children'] = []
	t['counted'] = False
	tweetmap[t['id']] = t

for t in alltweets:
	if t['in_reply_to_status_id'] in tweetmap.keys():
		tweetmap[t['in_reply_to_status_id']]['children'].append(t)

ctweets = []
for t in alltweets:
	if len(t['children']) > 0:
		t['children'] = sorted(t['children'], cmp=byid)
		ctweets.append(t)
	elif t['in_reply_to_status_id'] in tweetmap.keys():
		ctweets.append(t)

convos = []
def recursivegrab(tweet, index):
	if len(convos) <= index:
		convos.append([tweet])
	else:
		convos[index].append(tweet)
	for t in tweet['children']:
		recursivegrab(t, index)
	tweet['counted'] = True
	
i=0
ctweets = ctweets[::-1]
tqueue = []
for t in ctweets:
	if t['counted']:
		continue
	recursivegrab(t, i)
	i += 1

f = codecs.getwriter('utf8')(open("convos.log", 'w'))
for c in convos:
	c = sorted(c, cmp=byid)[::-1]
	for t in c:
		link = "http://twitter.com/"+t['user']['screen_name']+"/status/"+str(t['id'])
		print >>f, "<%s> %s [%s] <%s>" % (t['user']['screen_name'], t['text'], t['created_at'], link)
	print >>f, "\n---------\n"
f.close()
