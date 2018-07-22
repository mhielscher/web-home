//
//  RASmartBot.h
//
//  Created by Matthew Hielscher on 2008-11-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#ifndef __RASMARTBOT_H
#define __RASMARTBOT_H

#include "RABot.h"
#include "RAUtility.h"

enum AIMode
{
	dumb,
	search,
	collisionsearch
};

class RASmartBot : public RABot
{
private:
	Point goalPoint;
	AIMode mode;
	AIMode lastmode;
	int timeShot;
	
	//parameters to optimize
	
	
	//action functions
	void Init();
	void ChooseGoalPoint();
	void AdjustVelocity();
	void IncrementAim();
	void HandleCollision(PerceptObject other);
	void Collide(RAEntity* other);
	PerceptObject CreatePerceptObject(Point pos);
	
	void PrintStats();
	
public:
	RASmartBot();
	RASmartBot(RAEnvironment* w);
	
	void Update();
	
	void Send(int fd);
};


#endif
