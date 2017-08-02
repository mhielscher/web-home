Steward
-------

Syntax:
java XPReader [server [basechannelname [nick]]]

Steward was written to run on my private IRC server, tracking X-Plane 7 
flights using the built-in UDP output. It currently supports data items 
2 (speed), 3 (G-forces), 16 (angles, may be 17 in older versions of 
X-Plane), and 18 (lat, lon, alt). It will join four channels, regardless 
of what data you are really sending to it, which are named from the base 
channel plus "alt", "speed", "G", or "angle".

It reports each data value every four seconds, times four lines, so to
avoid flood protection Steward should have an O-line (dancer-ircd and
possibly hybrid) with the name "steward" and the password "password", and
only +F allowed and given by default. Steward will request opership upon
connecting. Good luck using it on a server you don't own.

Steward will also accept commands typed in any of the channels it inhabits,
or /msg'd directly to it. As of this version, it recognizes only the 
MESS command, which will put a message, ATIS style, on the pilot's 
screen.

Syntax:
MESS <message>

------------------

Defaults:
server - nic (For use with my internal network)
base channel name - #missioncontrol
	(when setting on the command line, use a '\' before the '#')
nick - steward
