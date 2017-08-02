//
//  RASmartBot.cc
//
//  Created by Matthew Hielscher on 2008-11-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>

#include "RAEnvironment.h"
#include "RABot.h"
#include "RASmartBot.h"
#include "RAUtility.h"

//#define PI 3.14159

RASmartBot::RASmartBot()
	: goalPoint(0,0), mode(dumb), lastmode(dumb), RABot()
{}

RASmartBot::RASmartBot(RAEnvironment* w)
	: goalPoint(0,0), mode(dumb), lastmode(dumb), RABot(w)
{}

void RASmartBot::Update()
{
	RABot::Update();
	
	timeShot--;
	if (world->GetTickCount() == 0)
		Init();
	//if (mode == dumb)
	//{
		AdjustVelocity();
		if (timeShot < 90)
			IncrementAim();
		int minDist = 10000;
		vector<PerceptObject> vPO = world->getPerceptObjects(position, aim);
		printf("vPO is %d\n", vPO.size());
		if (vPO.size() > 0 && timeShot <= 0)
		{
			printf("Shooting now\n");
			world->CreateProjectile(position, aim, 10);
			timeShot = 120;
		}
	//}
	//else if (mode == collisionsearch)
	//{
	//	IncrementAim();
	//}
	
	PrintStats();
}

void RASmartBot::Init()
{
	ChooseGoalPoint();
	timeShot = 0;
}

void RASmartBot::ChooseGoalPoint()
{
	goalPoint.x = random() % world->GetArenaSize().x;
	goalPoint.y = random() % world->GetArenaSize().y;
}

void RASmartBot::AdjustVelocity()
{
	int targetX = goalPoint.x - position.x;
	int targetY = goalPoint.y - position.y;
	int distance = sqrt(targetX*targetX + targetY*targetY);
	double targetAngle = 0;
	if (targetX == 0)
		targetAngle = ((targetY > 0) ? 3*M_PI/2 : M_PI/2);
	else
	{
		targetAngle = atan((double)targetY/targetX);
		if (targetX < 0)
			targetAngle += M_PI;
	}
	if (fabs(targetAngle-angle) > M_PI)
	{
		if (angle > targetAngle)
			targetAngle += 2*M_PI;
		else
			targetAngle -= 2*M_PI;
	}
	printf("   Debug: targetAngle=%lf, targetX=%d, targetY=%d\n", targetAngle, targetX, targetY);
	if (fabs(targetAngle-angle) > .05)
	{
			Turn((targetAngle-angle)/25);
	}
	else
		Turn(0);
	if (distance < 75 && velocity > 2)
		Brake(1);
	else if (velocity < 150/30)
		Accelerate(.4);
	//velocity = 5;
	//angle = targetAngle;
	
	if (distance < 40)
		ChooseGoalPoint();
}

void RASmartBot::IncrementAim()
{
	Aim(aim + 1.4486);
	//Aim(aim + .1);
}

void RASmartBot::HandleCollision(PerceptObject other)
{
	lastmode = mode;
	mode = collisionsearch;
}

void RASmartBot::Collide(RAEntity* other)
{
	RABot::Collide(other);
	HandleCollision(world->CreatePerceptObject(this, other));
}

PerceptObject RASmartBot::CreatePerceptObject(Point pos)
{
	PerceptObject po;
	po.type = 's';
	po.distance = sqrt((pos.x-position.x)*(pos.x-position.x) * (pos.y-position.y)*(pos.y-position.y));
	return po;
}

void RASmartBot::PrintStats()
{
	printf("-------\n");
	printf("Stats for Bot %p\n", this);
	printf("  position: (%d,%d)\n", position.x, position.y);
	printf("  angle: %lf\n", angle*180/3.14159);
	printf("  velocity: %lf\n", velocity);
	printf("  angularVelocity: %lf\n", angularVelocity);
	printf("  goalPoint: (%d,%d)\n", goalPoint.x, goalPoint.y);
	printf("  energy: %d\n", energy);
	printf("  damage: %d\n", damage);
	printf("  aim: %lf\n", aim);
}

void RASmartBot::Send(int fd)
{
	send(fd, "RASmartBot", 11, 0);
	RABot::Send(fd);
	nbosend(fd, &(goalPoint.x), sizeof(goalPoint.x), 0);
	nbosend(fd, &(goalPoint.y), sizeof(goalPoint.y), 0);
}
