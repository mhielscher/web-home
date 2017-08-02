//
//  RABot.h
//
//  Created by Matthew Hielscher on 2008-10-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#ifndef __RABOT_H
#define __RABOT_H

#include "RAEntity.h"
#include "RAEnvironment.h"
/*
struct PerceptObject
{
	char type; //or an enum?
	short distance;
};
*/
class RABot : public RAEntity
{
private:
	
	void Reflex();
	void InitRules();
	
protected:
	double aim;
	bool aimSet;
	int energy;
	int damage;
	
public:
	RABot();
	RABot(RAEnvironment* w);
	
	virtual void Update();
	
	bool Accelerate(double accel);
	bool Brake(double decel);
	bool Turn(double angVel);
	void Aim(double a);
	bool Fire(int f);

	void DoDamage(int d);
	virtual void Collide(RAEntity* other);
	virtual PerceptObject CreatePerceptObject(Point pos);
	
	virtual void Send(int fd);
};


#endif
