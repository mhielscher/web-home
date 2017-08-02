package EigenMeat.EigenMaze;

import java.util.*;

class PrototypeProjectile extends Projectile implements Cloneable {
	String name;
	
	public PrototypeProjectile() {
		super();
	}
	
	public PrototypeProjectile(String name, float damage, int ownerid, boolean bounces,
								boolean weathervane, float blastRadius, ParticleEffect pe) {
		this.name = name;
		setDamage(damage);
	//	setOwnerID(ownerid); //are we ever going to use this?
		this.bounces = bounces;
		this.weathervane = weathervane;
		this.blastRadius = blastRadius;
		peffect = pe;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setBounces(boolean b) {
		bounces = b;
	}
	
	public void setWeathervane(boolean w) {
		weathervane = w;
	}
	
	public void setBlastRadius(float r) {
		blastRadius = r;
	}
	
	public void setDeathExplosion(ParticleEffect pe) {
		peffect = pe;
	}
	
	public Object clone() {
		try {
			PrototypeProjectile myClone = (PrototypeProjectile)super.clone();
			myClone.peffect = (ParticleEffect)myClone.peffect.clone();
			return myClone;
		} catch (Exception e) {
			return null;
		}
	}
}
