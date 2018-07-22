//
//  RAEnvironment.cc
//
//  Created by Matthew Hielscher on 2008-10-18.
//  Written for CS 165A, Fall 2008, UCSB.
//

class RAEntity;

#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>

#include "RAEnvironment.h"
#include "RAEntity.h"
#include "RABot.h"
#include "RASmartBot.h"
#include "RAProjectile.h"

RAEnvironment::RAEnvironment()
	: arenaSize(0,0), entities(100), toBeAdded(10), toBeDeleted(10), tickCount(0)
{}

void RAEnvironment::InitArena(int x, int y)
{
	arenaSize.x = x;
	arenaSize.y = y;
	entities.clear();
	toBeAdded.clear();
	toBeDeleted.clear();
	tickCount = 0;
}

void RAEnvironment::InitBots(int n)
{
	for (int i=0; i<n; i++)
		entities.push_back(new RASmartBot(this));
}

void RAEnvironment::UpdateAll(int fd)
{
	printf("toBeAdded size is %d\n", toBeAdded.size());
	for (vector<RAEntity*>::iterator tba=toBeAdded.begin(); tba!=toBeAdded.end(); tba++)
	{
		entities.push_back(*tba);
	}
	toBeAdded.clear();

	for (vector<RAEntity*>::iterator tbd=toBeDeleted.begin(), ent=entities.begin(); tbd!=toBeDeleted.end() && ent!=entities.end(); ent++)
	{
		//entities.remove(*tbd);
		if ((*ent) == (*tbd))
		{
			ent = entities.erase(ent);
			tbd++;
			printf("Deleted entity %p\n", *ent);
		}
	}
	toBeDeleted.clear();

	if (fd != -1)
	{
		send(fd, "Count", 6, 0);
		int count = entities.size();
		printf("Current count is %d\n", count);
		nbosend(fd, &count, sizeof(count), 0);
	}
	for (vector<RAEntity*>::iterator ent=entities.begin(); ent!=entities.end(); ent++)
	{
		(*ent)->Update();
		printf("Updating %p\n", *ent);
		if (fd != -1)
			(*ent)->Send(fd);
		if ((*ent)->IsDead())
			toBeDeleted.push_back(*ent);
	}
	tickCount++;
}

void RAEnvironment::CreateProjectile(Point pos, double aim, int power)
{
	Point newPos = pos;
	newPos.x += 36*cos(aim);
	newPos.y += 36*sin(aim);
	RAEntity* e = new RAProjectile(this, power, newPos, 10., aim);
	toBeAdded.push_back(e);
	printf("  Added projectile %p to vector\n", e);
}

PerceptObject RAEnvironment::CreatePerceptObject(RAEntity* source, RAEntity* target)
{
	PerceptObject po = {0, 0}; //placeholder MARK
	return po;
}

RAEntity* RAEnvironment::CheckCollision(RAEntity* source, Point pos)
{
	for (vector<RAEntity*>::iterator ent=entities.begin(); ent!=entities.end(); ent++)
	{
		Point ePos = (*ent)->getPos();
		if (*ent != source && getDist(pos, ePos) < source->GetRadius()+(*ent)->GetRadius())
			return (*ent);
	}
	return NULL;
}

vector<PerceptObject> RAEnvironment::getPerceptObjects(Point pos, double aim){
	vector<PerceptObject> polist;
	for(vector<RAEntity*>::iterator ent=entities.begin(); ent!=entities.end();ent++){
		Point ePos = (*ent)->getPos();
		if (ePos.x == pos.x && ePos.y == pos.y)
			continue;
		double angle;
		if ((ePos.x-pos.x) == 0)
			angle = ((ePos.y-pos.y)>0) ? 3*M_PI/2 : M_PI/2;
		else
			angle = atan((double)(ePos.y - pos.y)/(ePos.x - pos.x));
		double diff = atan((double)(*ent)->GetRadius()/getDist(ePos,pos));
		if ((ePos.x-pos.x)<0)
		{
			angle += M_PI;
			diff += M_PI;
		}
		double leftAim = angle + diff;
		double rightAim = angle - diff;
		if (fabs(leftAim-aim) > M_PI)
		{
			if (aim > leftAim)
				leftAim += 2*M_PI;
			else
				leftAim -= 2*M_PI;
		}
		if (fabs(rightAim-aim) > M_PI)
		{
			if (aim > rightAim)
				rightAim += 2*M_PI;
			else
				rightAim -= 2*M_PI;
		}
		printf("leftAim: %lf, aim: %lf, rightAim: %lf, (%d,%d)-(%d,%d)\n", leftAim, aim, rightAim, pos.x, pos.x, ePos.x, ePos.y);
		if( (aim < leftAim) && (aim > rightAim)){
			printf("Adding %p to perceptobjects for (%d,%d)\n", (*ent), pos.x, pos.y);
			polist.push_back((*ent)->CreatePerceptObject(pos));
		}
	}
	return polist;	
}

void RAEnvironment::Send(int fd)
{
	send(fd, &arenaSize, sizeof(arenaSize), 0);
	int numEntities = entities.size();
	send(fd, &numEntities, sizeof(numEntities), 0);
	send(fd, &tickCount, sizeof(tickCount), 0);
}
