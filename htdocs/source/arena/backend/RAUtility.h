//
//  RAUtility.h
//
//  Created by Matthew Hielscher on 2008-10-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#ifndef __RAUTILITY_H
#define __RAUTILITY_H

#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <math.h>

struct Point
{
	int x, y;
	
	Point() : x(0), y(0) {}
	Point(int a, int b) : x(a), y(b) {}
};

inline void nbosend(int fd, const void* data, size_t size, int flags)
{
	//char* byteArray = new char[size];
	//*byteArray = data;
	for (int i=size-1; i>=0; i--)
		send(fd, ((char*)data)+i, 1, flags);
	//delete byteArray;
}

inline double getDist(Point p1, Point p2)
{
	return sqrt((double)(p1.x-p2.x)*(p1.x-p2.x) + (double)(p1.y-p2.y)*(p1.y-p2.y));
};




#endif
