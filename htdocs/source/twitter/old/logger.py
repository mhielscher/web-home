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
import smtplib

username = "username"
password = "********"
txtmentions = False

def getit(body):
	global received
	received += body
	return len(body)
	
def sendtxt(user, msg):
	conn = smtplib.SMTP("smtp.gmail.com", 587)
	conn.starttls()
	conn.login("mhielscher", "********")
	conn.sendmail("mhielscher@gmail.com", "8185552525@vtext.com", "Subject: "+user+"\r\n\r\n"+msg)
	conn.quit()
	
def dotxtmentions(tweets):
	global username
	for tweet in tweets[::-1]:
		if tweet['text'].find("@"+username) != -1:
			sendtxt(tweet['user']['screen_name'], tweet['text'])

def checkdms(body):
	global username
	global txtmentions
	txtfile = open('txtmentions.stat', 'r')
	opt = txtfile.read().strip()
	if opt == "on":
		txtmentions = True
	data = json.loads(body)
	if len(data) == 0:
		return
	#print data
	for dm in data:
		#print dm
		if dm['sender_screen_name'] == username and dm['text'].startswith("tm"):
			if dm['text'].endswith("on"):
				txtmentions = True
			elif dm['text'].endswith("off"):
				txtmentions = False
			else:
				continue
			txtfile = open('txtmentions.stat', 'w')
			if txtmentions:
				print >>txtfile, "on"
			else:
				print >>txtfile, "off"
			txtfile.close()
			return
	
def writelog(body):
	tj = codecs.getwriter('utf8')(open('timeline.json', 'a'))
	print >>tj, body
	tj.close()
	data = json.loads(body)
	if len(data) == 0:
		exit(0)
	log = codecs.getwriter('utf8')(open('timeline.log', 'a'))
	if len(data) == 200:
		print >>log, "-- May have lost some tweets here [%s]" % (time.strftime("%a %b %d %H:%M:%S +0000 %Y"))
	for i in xrange(len(data)-1, -1, -1):
		tweet = data[i]
		link = "http://twitter.com/"+tweet['user']['screen_name']+"/status/"+str(tweet['id'])
		print >>log, "<%s> %s [%s] <%s>" % (tweet['user']['screen_name'], tweet['text'], tweet['created_at'], link)
	log.close()
	idfile = open('timeline.id', 'w')
	print >>idfile, data[0]['id']
	idfile.close()
	if txtmentions:
		dotxtmentions(data)
	
os.chdir("/home/restorer/Documents/devel/twitter/logs/")

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

url = 'http://twitter.com/direct_messages.json'
head = ['Expect: ']
data = [('count', 10)]
postdata = urllib.urlencode(data)
url = url+'?'+postdata
c = pycurl.Curl()
c.setopt(pycurl.URL, url)
c.setopt(pycurl.USERPWD, "%s:%s" % (username, password))
c.setopt(pycurl.HTTPHEADER, head)
#c.setopt(pycurl.POSTFIELDS, postdata)
c.setopt(pycurl.WRITEFUNCTION, getit)
c.perform()
c.close()

checkdms(received)

received = ""

url = 'https://api.twitter.com/1/statuses/home_timeline.json'
head = ['Expect: ']
data = [('since_id', lastid), ('count', 200)]
postdata = urllib.urlencode(data)
c = pycurl.Curl()
c.setopt(pycurl.URL, url)
c.setopt(pycurl.USERPWD, "%s:%s" % (username, password))
c.setopt(pycurl.HTTPHEADER, head)
c.setopt(pycurl.POSTFIELDS, postdata)
c.setopt(pycurl.WRITEFUNCTION, getit)
c.perform()
c.close()

writelog(received)
