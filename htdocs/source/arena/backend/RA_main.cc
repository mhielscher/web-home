//
//  RA_main.cc
//
//  Created by Matthew Hielscher on 2008-11-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#include <vector>
#include <time.h>
#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>

#include <boost/thread/thread.hpp>
#include <boost/bind.hpp>

using namespace std;

#include "RAEnvironment.h"

#define ARENA_X_DIM 800
#define ARENA_Y_DIM 600
#define NUM_BOTS 10
#define REALTIME_SPEED 30

void GetConnection(int* connfd)
{
	// Set up the connections, taken from Beej's Networking Guide:
	// http://beej.us/guide/bgnet/output/html/multipage/syscalls.html
	int status;
	struct addrinfo hints;
	struct addrinfo *servinfo;  // will point to the results

	memset(&hints, 0, sizeof hints); // make sure the struct is empty
	hints.ai_family = AF_INET;     // IPv4 (changed from Beej's tutorial)
	hints.ai_socktype = SOCK_STREAM; // TCP stream sockets
	hints.ai_flags = AI_PASSIVE;     // fill in my IP for me

	if ((status = getaddrinfo(NULL, "3232", &hints, &servinfo)) == -1) {
	    fprintf(stderr, "getaddrinfo error: %s\n", gai_strerror(status));
	    exit(1);
	}

	// servinfo now points to a linked list of 1 or more struct addrinfos

	// ... do everything until you don't need servinfo anymore ....
	int incoming = socket(servinfo->ai_family, servinfo->ai_socktype, servinfo->ai_protocol);
	//printf("%d\n", incoming);
	int server = 0;
	
	bind(incoming, servinfo->ai_addr, servinfo->ai_addrlen);
	listen(incoming, 10);

	struct sockaddr the_client;
	socklen_t addr_size = sizeof the_client;
	server = accept(incoming, &the_client, &addr_size);
	//printf("%d\n", server);
	
	*connfd = server;
}

int main(int argc, char** argv)
{
	bool realtime = false;
	int backend = -1;
	
	//do the socket attachment here
	boost::thread connThread(boost::bind(&GetConnection, &backend));
	
	RAEnvironment env;
	env.InitArena(ARENA_X_DIM, ARENA_Y_DIM);
	env.InitBots(NUM_BOTS);
	
	sleep(3);
	
	bool done = false;
	bool connected = false;
	int count = 0;
	while (!done)
	{
		if (backend != -1)
		{
			if (!connected)
			{
				//env.Send(backend);
				connected = true;
			}
			timespec delay = {0, (1./REALTIME_SPEED)*1.e9};
			nanosleep(&delay, NULL);
			//printf("Iter %d\n", count);
		}
		env.UpdateAll(backend);
		printf("Iter %d\n", count);
		count++;
	}
}
