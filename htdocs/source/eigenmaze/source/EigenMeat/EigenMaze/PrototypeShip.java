package EigenMeat.EigenMaze;

import java.util.*;

class PrototypeShip extends Ship implements Cloneable {
	private String name;
	
	public PrototypeShip() {
		super();
		launchers = new Vector(0,1);
	}
	
	public PrototypeShip(String meshName, float meshScale, float r, float ts, float accel, float ms, float sh,
							float m, ProjectileLauncher[] pl, ParticleEffect death, ParticleEffect thrust) {
		setMesh(MeshLoader.loadMesh(meshName, meshScale));
		setBoundingSphere(r);
		setTurnSpeed(ts);
		setAcceleration(accel);
		setMaxSpeed(ms);
		setShields(sh);
		setMass(m);
		launchers = new Vector(0,1);
		for (int i=0; i<pl.length; i++) {
			launchers.add(pl[i]);
			pl[i].setEntity(this);
		}
		dieExplosion = death;
		thrustParticles = thrust;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setDeathExplosion(ParticleEffect p) {
		dieExplosion = p;
	}
	
	public void setThrustParticles(ParticleEffect p) {
		thrustParticles = p;
	}
	
	public void addProjectileLauncher(ProjectileLauncher pl) {
		if (launchers == null)
			launchers = new Vector(1,1);
		launchers.add(pl);
		pl.setEntity(this);
	}
	
	public Object clone() {
		try {
			PrototypeShip myClone = (PrototypeShip)super.clone();
			myClone.setPosition(new Vect3d(myClone.getPosition()));
			for (int i=0; i<myClone.launchers.size(); i++) {
				ProjectileLauncher pl = (ProjectileLauncher)((ProjectileLauncher)myClone.launchers.get(i)).clone();
				pl.setEntity(myClone);
				myClone.launchers.set(i, pl);
			}
			myClone.dieExplosion = (ParticleEffect)myClone.dieExplosion.clone();
			myClone.thrustParticles = (ParticleEffect)myClone.thrustParticles.clone();
			return myClone;
		} catch (Exception e) {return null;}
	}
}
