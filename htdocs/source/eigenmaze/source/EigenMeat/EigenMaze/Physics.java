package EigenMeat.EigenMaze;

//buffers: physics handling class. perhaps add more physics relation functions
//later. having a class with static methods for something like this seems dirty.
//this stuff could actually just go into entity...
/**
 * A utility class with static methods for mobile collision handling.
 */
public class Physics {
	//buffers: won't work with objects at high velocities.
	//i will fix this tomorrow. need to treat velocities as functions
	//of time and than checking time at intervals between 0 and 1
	public static boolean checkSphereCollision(Vect3d p1, float r1, Vect3d p2, float r2) {
		float distance = Vect3d.getDistanceBetweenPoints(p1,p2);

		if(distance < (r1+r2))
			return true;

		return false;
	}
	
	/**
	 * Checks whether two MobileEntitys are colliding. The result is based on
	 * the MobileEntitys' bounding spheres.
	 * @param e1 the first entity.
	 * @param e2 the second entity.
	 * @return true if the entities are colliding; false if they are not colliding.
	 */
	public static boolean checkEntityCollision(MobileEntity e1, MobileEntity e2) {
		Vect3d p1 = new Vect3d(e1.getPosition());
		p1.add(e1.getRawVelocity());

		Vect3d p2 = new Vect3d(e2.getPosition());
		p2.add(e2.getRawVelocity());
		
                return checkSphereCollision(p1,e1.getBoundingSphere(),p2,e2.getBoundingSphere());
     }


	
/* buffers: WIP
	public static boolean checkEntityCollision(MobileEntity e1, MobileEntity e2) {
		//start and end points for entity1
		Vect3d start1 = new Vect3d(e1.getPosition());
		Vect3d end1 = new Vect3d(start1);
		end1.add(e1.getRawVelocity());
		
		//directional vector
		Vect3d dir1 = new Vect3d(end1);
		dir1.sub(start1);
			
		//start and end points for entity2	
		Vect3d start2 = new Vect3d(e2.getPosition());
		Vect3d end2 = new Vect3d(start2);
		end2.add(e2.getRawVelocity());

		//directional vector
		Vect3d dir2 = new Vect3d(end2);
		dir2.sub(start2);
	
		float time = 1;
		
		
		while(time >= 0) {

		}
		
		
		return checkSphereCollision(p1,e1.getBoundingSphere(),p2,e2.getBoundingSphere());
	}
*/
	/**
	 * Bounces two entities off each other based on sphere collision.
	 * @param e1 the first entity.
	 * @param e2 the second entity.
	 */
	public static void handleEntityCollision(MobileEntity e1, MobileEntity e2) {
		float m1,m2;
		
		//buffers: not sure I like instanceof statements...
		if(e1 instanceof MassEntity && e2 instanceof MassEntity) {
			m1 = ((MassEntity)e1).getMass();
			m2 = ((MassEntity)e2).getMass();
		} else {
			m1 = 1;
			m2 = 1;
		}
		
		Vect3d p1 = new Vect3d(e1.getPosition());
		p1.add(e1.getRawVelocity());

		Vect3d p2 = new Vect3d(e2.getPosition());
		p2.add(e2.getRawVelocity());
		
		//make sure the entities are moving toward each other
		Vect3d pc1 = new Vect3d(p1);
		pc1.add(e1.getRawVelocity());
		Vect3d pc2 = new Vect3d(p2);
		pc2.add(e2.getRawVelocity());
		Vect3d d = new Vect3d(p1);
		d.sub(p2);
		float dis1 = d.length();
		d.set(pc1);
		d.sub(pc2);
		float dis2 = d.length();
		if (dis1 <= dis2)
			return;
	
		//get the vector located at sphere1 pointing towards sphere2 
		//(called sphere x axis)
		Vect3d axis = new Vect3d(p2);
		axis.sub(p1);
		axis.normalize();
		
		//take velocity of sphere1 and split it into XY components
		Vect3d u1x = new Vect3d(axis);
		float dot = axis.dot(e1.getVelocity());
		u1x.mult(dot);
		Vect3d u1y = new Vect3d(e1.getVelocity());
		u1y.sub(u1x);

		//get vector located at sphere2 pointing towards sphere1
		axis.set(p1);
		axis.sub(p2);
		axis.normalize();
		//split velocity of sphere2 into XY components
		Vect3d u2x = new Vect3d(axis);
		dot = axis.dot(e2.getVelocity());
		u2x.mult(dot);
		Vect3d u2y = new Vect3d(e2.getVelocity());
		u2y.sub(u2x);
	
		//conservation of momentum
		Vect3d vel1 = new Vect3d(u1x);
		vel1.mult(m1);
		vel1.addm(u2x,m2);
		vel1.subm(u1x,m2);
		vel1.addm(u2x,m2);
		vel1.div(m1+m2);
		vel1.add(u1y);
		e1.setVelocity(vel1);

		Vect3d vel2 = new Vect3d(u1x);
		vel2.mult(m1);
		vel2.addm(u2x,m2);
		vel2.subm(u2x,m1);
		vel2.addm(u1x,m1);
		vel2.div(m1+m2);
		vel2.add(u2y);
		e2.setVelocity(vel2);
	}
	
	/**
	 * Bounces an entity off an unmoving point of collision associated with a wall edge.
	 * @param e1 the entity.
	 * @param impact the point of collision.
	 */
	public static void handleEdgeCollision(MobileEntity e1, Vect3d impact) {
		float m1,m2;
		
		Vect3d p1 = new Vect3d(e1.getPosition());
		Vect3d p2 = new Vect3d(impact);
		
		//make sure the entity is moving towards the point
		Vect3d predCen = new Vect3d(p1);
		predCen.add(e1.getRawVelocity());
		Vect3d d = new Vect3d(p1);
		d.sub(impact);
		float dis1 = d.length();
		d.set(predCen);
		d.sub(impact);
		float dis2 = d.length();
		if (dis1 <= dis2)
			return;
	
		//get the vector located at sphere1 pointing towards impact point
		//(called sphere x axis)
		Vect3d axis = new Vect3d(p2);
		axis.sub(p1);
		axis.normalize();
		
		//take velocity of sphere1 and split it into XY components WRT the axis
		Vect3d u1x = new Vect3d(axis);
		float dot = axis.dot(e1.getVelocity());
		u1x.mult(dot);
		Vect3d u1y = new Vect3d(e1.getVelocity());
		u1y.sub(u1x);
/*
		//get vector located at sphere2 pointing towards sphere1
		axis.set(p1);
		axis.sub(p2);
		axis.normalize();
		//split velocity of sphere2 into XY components
		Vect3d u2x = new Vect3d(axis);
		dot = axis.dot(e2.getVelocity());
		u2x.mult(dot);
		Vect3d u2y = new Vect3d(e2.getVelocity());
		u2y.sub(u2x);
*/
		//conservation of momentum
		Vect3d vel1 = new Vect3d(u1x);
		/*
		vel1.mult(m1);
		vel1.addm(u2x,m2);
		vel1.subm(u1x,m2);
		vel1.addm(u2x,m2);
		vel1.div(m1+m2);
		*/
		vel1.invert();
		vel1.add(u1y);
		e1.setVelocity(vel1);
	}
}
