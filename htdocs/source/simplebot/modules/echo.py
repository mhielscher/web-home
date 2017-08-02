#!/usr/bin/python

def do_echo(c, s, t, ch, a):
	msg = " ".join(a)
	if ch:
		c.privmsg(ch, msg)
	else:
		c.privmsg(t, msg)

botmodulename = "echo"
commands = {
	"echo" : (do_echo, 0)
	}
help = {
	"echo" : ("Echoes whatever you tell it.", "echo [channel] <message>", "Echoes <message>, optionally to another [channel].")
	}
bot = None #this will be set by the bot that loads the module
