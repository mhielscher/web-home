//
//  RABot.cc
//
//  Created by Matthew Hielscher on 2008-10-18.
//  Written for CS 165A, Fall 2008, UCSB.
//

#include <math.h>
#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>

#include "RAUtility.h"
#include "RAEntity.h"
#include "RABot.h"

#define PI 3.14159

RABot::RABot()
	: aim(0), aimSet(false), energy(1000), damage(100), RAEntity()
{
	radius = 30;
}

RABot::RABot(RAEnvironment* w)
	: aim(0), aimSet(false), energy(1000), damage(100), RAEntity(w)
{
	radius = 30;
}

void RABot::Update()
{
	aimSet = false;
	energy+=5;
	int energyToTurn = angularVelocity/.314159;
	//int energyToTurn = 0;
	if (energy > energyToTurn) //this has precedence so we can rotate away from walls
	{
		energy -= energyToTurn;
		angle += angularVelocity;
		if (angle > 2*PI)
			angle -= 2*PI;
	}
	int energyToMove = pow(velocity*1.25, 1.5);
	//int energyToMove = 0;
	if (energy > energyToMove)
	/*	&& !(position.x < 0 && angle > PI/2 && angle < 3*PI/2)
				&& !(position.y < 0 && angle > 0 && angle < PI)
				&& !(position.x > world->GetArenaSize().x && (angle < PI/2 || angle > 3*PI/2))
				&& !(position.y > world->GetArenaSize().y && angle > PI)*/
	{
		energy -= energyToMove;
		int lastX = position.x, lastY = position.y;
		position.x += velocity*cos(angle);
		position.y += velocity*sin(angle);
		if ((position.x < radius && position.x < lastX) || (position.x > world->GetArenaSize().x-radius && position.x > lastX))
			position.x = lastX;
		if ((position.y < radius && position.y < lastY) || (position.y > world->GetArenaSize().y-radius && position.y > lastY))
			position.y = lastY;
	}
}

bool RABot::Accelerate(double accel)
{
	int engPerTick = accel*66.6667;
	if (energy >= engPerTick)
	{
		velocity += accel;
		energy -= engPerTick;
	}
}

bool RABot::Brake(double decel)
{
	int engPerTick = 4 + decel*6;
	if (energy >= engPerTick)
	{
		velocity -= decel;
		energy -= engPerTick;
		if (velocity < 0)
			velocity = 0;
	}
}

bool RABot::Turn(double angVel)
{
	int engPerTick = angVel/.314159;
	if (energy >= engPerTick)
	{
		angularVelocity = angVel;
		energy -= engPerTick; //yes, we charge an extra tick for changing the rate
	}
}

void RABot::Aim(double a)
{
	if (!aimSet)
		aim = a;
	if (aim > 2*M_PI)
		aim -= 2*M_PI;
	aimSet = true;
}

bool RABot::Fire(int f)
{
	int engUsed = (f/2)*(f/2);
	if (energy >= engUsed)
	{
		energy -= engUsed;
		world->CreateProjectile(position, aim, f);
	}
}

void RABot::Send(int fd)
{
	send(fd, "RABot", 6, 0);
	RAEntity::Send(fd);
	nbosend(fd, &aim, sizeof(aim), 0);
	nbosend(fd, &energy, sizeof(energy), 0);
	nbosend(fd, &damage, sizeof(damage), 0);
}

void RABot::DoDamage(int d)
{
	damage -= d;
	if (damage <= 0)
		dead = true;
}

void RABot::Collide(RAEntity* other)
{
	RAEntity::Collide(other);
	//nothing extra to do here
}

PerceptObject RABot::CreatePerceptObject(Point pos)
{
	PerceptObject po;
	po.type = 'b';
	po.distance = sqrt((pos.x-position.x)*(pos.x-position.x) * (pos.y-position.y)*(pos.y-position.y));
	return po;
}
