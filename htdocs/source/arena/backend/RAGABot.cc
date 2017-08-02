//
//  RAGABot.cc
//
//  Created by Matthew Hielscher on 2008-10-19.
//  Written for CS 165A, Fall 2008, UCSB.
//

void RAGABot::Reflex()
{
	vector<RAPerceptObject> objectsInView = world->getPerceptObjects(position, aim);
	int distanceToWall = world->getDistanceToWall(position, aim);
	
	long long actions = matchRules(objectsInView, distanceToWall);
	int accelEncode = (int)(actions & 0xff);
	int brakeEncode = (int)((actions >> 8) & 0x7ff);
	int turnEncode = (int)((actions >> 16) & 0xff);
	int aimEncode = (int)((actions >> 24) & 0xff);
	int fireEncode = (int)((actions >> 32) & 0xff);
	
	double newAccel = (double)accelEncode/300.;
	double newBrake = (double)brakeEncode/500.;
	double newTurn = (double)turnEncode/40.;
	double newAim = (double)aimEncode/40.74;
	int firePower = fireEncode;
	
	accelerate(accel);
	brake(brake);
	turn(turn);
	if (newAim != aim)
		aim(aim);
	if (firePower)
		fire(firePower);
}

long long RABot::matchRules(vector<RAPerceptObject>& objectsInView, int distanceToWall)
{
	//go through all the rules, find best match with current percepts
	//note: might not want to encode anything for internal use; use struct for actions
	// only encode/decode when reproducing
}

#define DiscreteProb(p,a,b) random(0,1) < p ? a : b

void RAGABot::InitRules()
{
	rules = new BotRule[1000];
	for (int i=0; i<1000; i++)
	{
		rules[i].visibleEntities[0].type = DiscreteProb(.33, 0, (DiscreteProb(.5, 1, 2)));
		rules[i].visibleEntities[0].distance = rules[i].visibleEntities[0].type == 0 ? 0 : gaussian(300, 200);
		for (int j=1; j<4; j++)
		{
			rules[i].visibleEntities[j].type = DiscreteProb(.67, 0, (DiscreteProb(.5, 1, 2)));
			rules[i].visibleEntities[j].distance = rules[i].visibleEntities[j].type == 0 ? 0 : gaussian(400, 300);
		}
		rules[i].distanceToObstacle = gaussian(500, 250);
		rules[i].velocity = DiscreteProb(.1, 0, gaussian(5, 3));
		rules[i].angularVelocity = DiscreteProb(.2, 0, gaussian(.1,.05));
		rules[i].energy = gaussian(500, 250);
		rules[i].aim = uniform(0, 6.283);
		rules[i].damage = gaussian(500, 250);
		rules[i].accel = DiscreteProb(.2, 0, gaussian(.3, .2));
		rules[i].brake = DiscreteProb(.4, 0, gaussian(.2, .4));
		rules[i].turn = DiscreteProb(.2, 0, gaussian(.1,.05));
		rules[i].newAim = DiscreteProb(.25, rules[i].aim, uniform(0, 6.283));
		rules[i].fire = DiscreteProb(.5, 0, gaussian(20, 5));
	}
}