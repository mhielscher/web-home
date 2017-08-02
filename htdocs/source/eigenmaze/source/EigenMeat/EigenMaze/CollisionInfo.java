package EigenMeat.EigenMaze;

class CollisionInfo {
	private Vect3d triNormal;
	private float distance;

	private Vect3d currentPosition;
	private Vect3d rawVelocity;
	private Vect3d realVelocity;
	private Vect3d proposedPosition;
	
	private Vect3d collisionPoint;

	private float boundingSphereRadius;

	private boolean collision;
	
	public int trianglesChecked;

	public boolean edgeCollision;
	
	public CollisionInfo(Vect3d pos, Vect3d vel, float rad) {
		currentPosition = pos;
		rawVelocity = vel;

		realVelocity = new Vect3d(rawVelocity);
		realVelocity.mult(Game.tof);
		
		proposedPosition = new Vect3d(currentPosition);
		proposedPosition.add(realVelocity);
	
		trianglesChecked = 0;

		boundingSphereRadius = rad;

		distance  = 100;
		
		collision = false;
		collisionPoint = null;
	}

	public void setCollision(boolean b) {
		collision = b;
	}

	public void setEdgeCollision(boolean b) {
		edgeCollision = b;
	}
	
	public boolean getCollision() {
		return collision;
	}
	
	public boolean getEdgeCollision() {
		return edgeCollision;
	}
	
	public void setDistance(float d) {
		distance = d;
	}

	public float getDistance() {
		return distance;
	}

	public void setTriNormal(Vect3d v) {
		triNormal = new Vect3d(v);
	}

	public Vect3d getTriNormal() {
		return triNormal;
	}
	
	public Vect3d getCurrentPosition() {
		return currentPosition;
	}

	public Vect3d getVelocity() {
		return realVelocity;
	}
	
	public Vect3d getProposedPosition() {
		return proposedPosition;
	}
	public Vect3d getRealVelocity() {
		return realVelocity;
	}

	public float getBoundingSphere() {
		return boundingSphereRadius;
	}
	
	public Vect3d getCollisionPoint() {
		return collisionPoint;
	}
	
	public void setCollisionPoint(Vect3d p) {
		collisionPoint = p;
	}
}
