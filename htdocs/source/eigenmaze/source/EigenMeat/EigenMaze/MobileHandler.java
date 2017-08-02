package EigenMeat.EigenMaze;

import java.util.*;

class MobileHandler  {
	
  	private List mobileList;
	private List addQue;
	//private volatile boolean canUpdate;
	
  	public MobileHandler() {	
		mobileList = Collections.synchronizedList(new LinkedList());
		addQue =  Collections.synchronizedList(new LinkedList());
	}
	
	public void add(MobileEntity entity) {
			addQue.add(entity);
	}

	public void update() {
		synchronized(mobileList) {
			ListIterator iter = mobileList.listIterator(0);
			MobileEntity current,colCurrent;
			while(iter.hasNext()) {
				current = (MobileEntity)iter.next();
			
				ListIterator collisionIter = mobileList.listIterator(iter.nextIndex());
				while(collisionIter.hasNext()) {
					colCurrent = (MobileEntity)collisionIter.next();
					if(Physics.checkEntityCollision(current,colCurrent)) {
						
						if(current.collide(colCurrent) &&  colCurrent.collide(current))
							Physics.handleEntityCollision(current,colCurrent);

					}
				}
				
				current.update();

				if(current.isDead()) {
					current.die();
					iter.remove();
				}
			}
		}
		while(addQue.size() > 0)
			mobileList.add(addQue.remove(0));
	}

	public Vector getEntities(Vect3d point, float radius) {
		Vector v = new Vector();

		for(int i=0; i < mobileList.size(); i++) {
			MobileEntity current = (MobileEntity)mobileList.get(i);

			if(Vect3d.getDistanceBetweenPoints(point,current.getPosition()) <= radius) {
				v.add(current);
			}
		}

		return v;
	}
}
