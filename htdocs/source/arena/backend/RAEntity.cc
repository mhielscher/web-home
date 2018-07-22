//
//  RAEntity.cc
//
//  Created by Matthew Hielscher on 2008-10-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>

#include "RAEntity.h"
#include "RAUtility.h"

RAEntity::RAEntity()
	: dead(false), world(0), position(0,0), angle(0), velocity(0), angularVelocity(0), radius(0)
{}

RAEntity::RAEntity(RAEnvironment* w)
	: dead(false), world(w), angle(0), velocity(0), angularVelocity(0), radius(0)
{
	position.x = (random() % (w->GetArenaSize().x-radius*2) + radius);
	position.y = (random() % (w->GetArenaSize().y-radius*2) + radius);
}

void RAEntity::Collide(RAEntity* other)
{
	velocity = 0;
}

bool RAEntity::IsDead()
{
	return dead;
}

Point RAEntity::getPos()
{
	return position;
}

int RAEntity::GetRadius()
{
	return radius;
}

void RAEntity::Update()
{
	position.x += cos(angle)*velocity;
	position.y += sin(angle)*velocity;
	angle += angularVelocity;
}

void RAEntity::Send(int fd)
{
	send(fd, "RAEntity", 9, 0);
	nbosend(fd, &(position.x), sizeof(position.x), 0);
	nbosend(fd, &(position.y), sizeof(position.y), 0);
	//printf("Sizeof position: %d", sizeof(position));
	nbosend(fd, &angle, sizeof(angle), 0);
	//printf("Sizeof angle: %d", sizeof(angle));
	nbosend(fd, &velocity, sizeof(velocity), 0);
	nbosend(fd, &angularVelocity, sizeof(angularVelocity), 0);
	nbosend(fd, &radius, sizeof(radius), 0);
}
