//
//  RAProjectile.h
//
//  Created by Matthew Hielscher on 2008-10-18.
//  Written for CS 165A, Fall 2008, UCSB.
//

#ifndef __RAPROJECTILE_H
#define __RAPROJECTILE_H

#include "RAEntity.h"
#include "RAEnvironment.h"

class RAProjectile : public RAEntity
{
private:
	int power;

public:
	RAProjectile();
	RAProjectile(RAEnvironment* w);
	RAProjectile(RAEnvironment* w, int p, Point pos, double v, double a);

	int GetPower();
	void Update();
	void Collide(RAEntity* other);
	PerceptObject CreatePerceptObject(Point pos);

	void Send(int fd);
};


#endif
