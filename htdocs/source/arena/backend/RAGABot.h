//
//  RAGABot.h
//
//  Created by Matthew Hielscher on 2008-10-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

#ifndef __RAGABOT_H
#define __RAGABOT_H

struct BotRule //48 bytes
{
	//percepts
	PerceptObject[4] visibleEntities;
	short distanceToObstacle;
	float velocity;
	float angularVelocity;
	short energy;
	float aim;
	short damage;
	//actions
	float accel;
	float brake;
	float turn;
	float newAim;
	short fire;
};

class RAGABot : public RABot
{
private:
	BotRule *rules;
	
public:
	void Reflex();
	void InitRules();
};


#endif