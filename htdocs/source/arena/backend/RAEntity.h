//
//  RAEntity.h
//
//  Created by Matthew Hielscher on 2008-10-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#ifndef __RAENTITY_H
#define __RAENTITY_H

//#include "RAEntity.h"
#include "RAEnvironment.h"
#include "RAUtility.h"

class RAEnvironment;

struct PerceptObject;

class RAEntity
{
protected:
	RAEnvironment *world;
	Point position;
	double angle;
	double velocity;
	double angularVelocity;
	int radius;
	bool dead;
	
public:
	RAEntity();
	RAEntity(RAEnvironment* w);
	
	virtual void Collide(RAEntity* other);
	bool IsDead();
	Point getPos();
	int GetRadius();
	virtual void Update();

	virtual PerceptObject CreatePerceptObject(Point pos)=0;
	
	virtual void Send(int fd);
};


#endif
