//
//  RAEnvironment.h
//
//  Created by Matthew Hielscher on 2008-10-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#ifndef __RAENVIRONMENT_H
#define __RAENVIRONMENT_H

#include <vector>

using namespace std;

class RAEntity;

#include <vector>

#include "RAEntity.h"
#include "RAUtility.h"
//#include "RABot.h"

struct PerceptObject
{
	char type; //or an enum?
	short distance;
};

class RAEnvironment
{
private:
	Point arenaSize;
	vector<RAEntity*> entities;
	vector<RAEntity*> toBeAdded;
	vector<RAEntity*> toBeDeleted;
	int tickCount;
	
public:
	RAEnvironment();
	
	void InitArena(int x, int y);
	void InitBots(int n);
	void UpdateAll(int fd);
	
	void CreateProjectile(Point pos, double aim, int power);
	
	Point GetArenaSize() {return arenaSize;}
	int GetTickCount() {return tickCount;}
	RAEntity* CheckCollision(RAEntity* source, Point pos);

	PerceptObject CreatePerceptObject(RAEntity* source, RAEntity* target);
	vector<PerceptObject> getPerceptObjects(Point pos, double aim);
	
	void Send(int fd);
};


#endif
