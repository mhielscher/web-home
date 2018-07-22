//
//  RAProjectile.cc
//
//  Created by Matthew Hielscher on 2008-10-18.
//  Written for CS 165A, Fall 2008, UCSB.
//

#include "RAEntity.h"
#include "RAProjectile.h"
#include "RAUtility.h"

#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>

#include <math.h>

RAProjectile::RAProjectile()
	: power(0), RAEntity()
{}

RAProjectile::RAProjectile(RAEnvironment* w)
	: power(0), RAEntity(w)
{
	radius = 5;
}

RAProjectile::RAProjectile(RAEnvironment* w, int p, Point pos, double v, double a)
	: power(p), RAEntity(w)
{
	position = pos;
	velocity = v;
	angle = a;
	radius = 5;
}

int RAProjectile::GetPower()
{
	return power;
}

void RAProjectile::Update()
{
	RAEntity::Update();
	RAEntity* ent;
	if (ent = (world->CheckCollision(this, position)))
	{
		ent->Collide(this);
		Collide(ent);
	}
}

void RAProjectile::Collide(RAEntity* other)
{
	RAEntity::Collide(other);
	dead = true;
	power = 0;
	//other->DoDamage(power);
}

PerceptObject RAProjectile::CreatePerceptObject(Point pos)
{
	PerceptObject po;
	po.type = 'p';
	po.distance = sqrt((pos.x-position.x)*(pos.x-position.x) * (pos.y-position.y)*(pos.y-position.y));
	return po;
}

void RAProjectile::Send(int fd)
{
	send(fd, "RAProjectile", 13, 0);
	RAEntity::Send(fd);
	nbosend(fd, &power, sizeof(power), 0);
}
