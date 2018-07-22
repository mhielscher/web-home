# Twitter profile image updater

# http://twitter.com/account/update_profile_image.json
# image = [imagefile]

import sys
import os
import pycurl
import random
import re

def parsejson(body):
	r_string = re.compile(r'("(\\.|[^"\\])*")')
	r_json = re.compile(r'^[,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]+$')
	env = {'__builtins__': None, 'null': None, 'true': True, 'false': False}
	if r_json.match(r_string.sub('', body)): 
		text = r_string.sub(lambda m: 'u' + m.group(1), body)
		m = eval(body.strip(' \t\r\n'), env, {})
	return None
	
os.chdir("/home/restorer/Documents/devel/twitter/pics/")
files = os.listdir(os.getcwd())
images = []
for filename in files:
	if filename.endswith(".jpg") or filename.endswith(".png") or filename.endswith(".gif"):
		images.append(filename)
imagefile = random.choice(images)

username = "username"
password = "********"

url = 'https://twitter.com/account/update_profile_image.json'
head = ['Expect: ']
data = [('image', (pycurl.FORM_FILE, imagefile))]
c = pycurl.Curl()
c.setopt(pycurl.URL, url)
c.setopt(pycurl.USERPWD, "%s:%s" % (username, password))
c.setopt(pycurl.HTTPHEADER, head)
c.setopt(pycurl.HTTPPOST, data)
c.setopt(pycurl.WRITEFUNCTION, parsejson)
c.perform()
c.close()
