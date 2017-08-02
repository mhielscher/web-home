//
//  InstrumentData.java
//  XPReader
//
//  Created by Matthew Hielscher on Fri Jul 2 2004
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

class InstrumentData {
	
	// index 18
	private float latitude;
	private float longitude;
	private float altitudeMSL;
	private float altitudeAGL;
	private float altitudeIndicated;
	private float latitudeWest;
	private float longitudeSouth;
	
	//index 02
	private float trueSpeedK;
	private float indicSpeedK;
	private float trueSpeedM;
	private float indicSpeedM;
	private float vertSpeed;
	
	//index 03
	private float machRatio;
	private float Gnormal;
	private float Gaxial;
	private float Gside;
	
	//index 17
	private float pitch;
	private float roll;
	private float trueHeading;
	private float magHeading;
	private float magVar; // don't know what this is
	private float headingBug; //nor this
	
	public InstrumentData(byte[] buffer, int length, int of) {
		latitude = 0;
		longitude = 0;
		altitudeMSL = 0;
		altitudeAGL = 0;
		altitudeIndicated = 0;
		latitudeWest = 0;
		longitudeSouth = 0;
		
		trueSpeedK = 0;
		indicSpeedK = 0;
		trueSpeedM = 0;
		indicSpeedM = 0;
		vertSpeed = 0;
		
		machRatio = 0;
		Gnormal = 0;
		Gaxial = 0;
		Gside = 0;
		
		pitch = 0;
		roll = 0;
		trueHeading = 0;
		magHeading = 0;
		magVar = 0;
		headingBug = 0;
		
		int pos = 5;
		for (int i=0; i<(length-5)/36; i++) {
			int index = (((buffer[pos] & 0xff) << 24) | ((buffer[pos+1] & 0xff) << 16) | ((buffer[pos+2] & 0xff) << 8) | (buffer[pos+3] & 0xff));
			if (index == 18) { // position, absolute
				latitude = Float.intBitsToFloat(((buffer[pos+4] & 0xff) << 24) | ((buffer[pos+5] & 0xff) << 16) | ((buffer[pos+6] & 0xff) << 8) | (buffer[pos+7] & 0xff));
				longitude = Float.intBitsToFloat(((buffer[pos+8] & 0xff) << 24) | ((buffer[pos+9] & 0xff) << 16) | ((buffer[pos+10] & 0xff) << 8) | (buffer[pos+11] & 0xff));
				altitudeMSL = Float.intBitsToFloat(((buffer[pos+12] & 0xff) << 24) | ((buffer[pos+13] & 0xff) << 16) | ((buffer[pos+14] & 0xff) << 8) | (buffer[pos+15] & 0xff));
				altitudeAGL = Float.intBitsToFloat(((buffer[pos+16] & 0xff) << 24) | ((buffer[pos+17] & 0xff) << 16) | ((buffer[pos+18] & 0xff) << 8) | (buffer[pos+19] & 0xff));
				altitudeIndicated = Float.intBitsToFloat(((buffer[pos+24] & 0xff) << 24) | ((buffer[pos+25] & 0xff) << 16) | ((buffer[pos+26] & 0xff) << 8) | (buffer[pos+27] & 0xff));
				latitudeWest = Float.intBitsToFloat(((buffer[pos+28] & 0xff) << 24) | ((buffer[pos+29] & 0xff) << 16) | ((buffer[pos+30] & 0xff) << 8) | (buffer[pos+31] & 0xff));
				longitudeSouth = Float.intBitsToFloat(((buffer[pos+32] & 0xff) << 24) | ((buffer[pos+33] & 0xff) << 16) | ((buffer[pos+34] & 0xff) << 8) | (buffer[pos+35] & 0xff));
			}
			if (index == 2) { // speed
				trueSpeedK = Float.intBitsToFloat(((buffer[pos+4] & 0xff) << 24) | ((buffer[pos+5] & 0xff) << 16) | ((buffer[pos+6] & 0xff) << 8) | (buffer[pos+7] & 0xff));
				indicSpeedK = Float.intBitsToFloat(((buffer[pos+8] & 0xff) << 24) | ((buffer[pos+9] & 0xff) << 16) | ((buffer[pos+10] & 0xff) << 8) | (buffer[pos+11] & 0xff));
				trueSpeedM = Float.intBitsToFloat(((buffer[pos+12] & 0xff) << 24) | ((buffer[pos+13] & 0xff) << 16) | ((buffer[pos+14] & 0xff) << 8) | (buffer[pos+15] & 0xff));
				indicSpeedM = Float.intBitsToFloat(((buffer[pos+16] & 0xff) << 24) | ((buffer[pos+17] & 0xff) << 16) | ((buffer[pos+18] & 0xff) << 8) | (buffer[pos+19] & 0xff));
				vertSpeed = Float.intBitsToFloat(((buffer[pos+20] & 0xff) << 24) | ((buffer[pos+21] & 0xff) << 16) | ((buffer[pos+22] & 0xff) << 8) | (buffer[pos+23] & 0xff));
			}
			if (index == 3) { // Mach, G's
				machRatio = Float.intBitsToFloat(((buffer[pos+4] & 0xff) << 24) | ((buffer[pos+5] & 0xff) << 16) | ((buffer[pos+6] & 0xff) << 8) | (buffer[pos+7] & 0xff));
				Gnormal = Float.intBitsToFloat(((buffer[pos+8] & 0xff) << 24) | ((buffer[pos+9] & 0xff) << 16) | ((buffer[pos+10] & 0xff) << 8) | (buffer[pos+11] & 0xff));
				Gaxial = Float.intBitsToFloat(((buffer[pos+12] & 0xff) << 24) | ((buffer[pos+13] & 0xff) << 16) | ((buffer[pos+14] & 0xff) << 8) | (buffer[pos+15] & 0xff));
				Gside = Float.intBitsToFloat(((buffer[pos+16] & 0xff) << 24) | ((buffer[pos+17] & 0xff) << 16) | ((buffer[pos+18] & 0xff) << 8) | (buffer[pos+19] & 0xff));
			}
			if (index == 16) { // angles
				pitch = Float.intBitsToFloat(((buffer[pos+4] & 0xff) << 24) | ((buffer[pos+5] & 0xff) << 16) | ((buffer[pos+6] & 0xff) << 8) | (buffer[pos+7] & 0xff));
				roll = Float.intBitsToFloat(((buffer[pos+8] & 0xff) << 24) | ((buffer[pos+9] & 0xff) << 16) | ((buffer[pos+10] & 0xff) << 8) | (buffer[pos+11] & 0xff));
				trueHeading = Float.intBitsToFloat(((buffer[pos+12] & 0xff) << 24) | ((buffer[pos+13] & 0xff) << 16) | ((buffer[pos+14] & 0xff) << 8) | (buffer[pos+15] & 0xff));
				magHeading = Float.intBitsToFloat(((buffer[pos+16] & 0xff) << 24) | ((buffer[pos+17] & 0xff) << 16) | ((buffer[pos+18] & 0xff) << 8) | (buffer[pos+19] & 0xff));
				magVar = Float.intBitsToFloat(((buffer[pos+20] & 0xff) << 24) | ((buffer[pos+21] & 0xff) << 16) | ((buffer[pos+22] & 0xff) << 8) | (buffer[pos+23] & 0xff));
				headingBug = Float.intBitsToFloat(((buffer[pos+24] & 0xff) << 24) | ((buffer[pos+25] & 0xff) << 16) | ((buffer[pos+26] & 0xff) << 8) | (buffer[pos+27] & 0xff));
			}
			pos += 36;
		}
	}
	
	public float getLatitude() {
		return ((int)(latitude*100))/100;
	}
	
	public float getLongitude() {
		return ((int)(longitude*100))/100;
	}
	
	public float getAltitude() {
		return ((int)(altitudeIndicated*100))/100;
	}
	
	public float getAltitudeAGL() {
		return ((int)(altitudeAGL*100))/100;
	}
	
	public float getTrueSpeedK() {
		return ((int)(trueSpeedK*100))/100;
	}
	
	public float getIndicSpeedK() {
		return ((int)(indicSpeedK*100))/100;
	}
	
	public float getTrueSpeedM() {
		return ((int)(trueSpeedM*100))/100;
	}
	
	public float getIndicSpeedM() {
		return ((int)(indicSpeedM*100))/100;
	}
	
	public float getVertSpeed() {
		return ((int)(vertSpeed*100))/100;
	}
	
	public float getMachRatio() {
		return ((int)(machRatio*1000))/1000;
	}
	
	public float getGnormal() {
		return ((int)(Gnormal*1000))/1000;
	}
	
	public float getGaxial() {
		return ((int)(Gaxial*1000))/1000;
	}
	
	public float getGside() {
		return ((int)(Gside*1000))/1000;
	}
	
	public float getPitch() {
		return ((int)(pitch*100))/100;
	}
	
	public float getRoll() {
		return ((int)(roll*100))/100;
	}
	
	public float getTrueHeading() {
		return ((int)(trueHeading*100))/100;
	}
	
	public float getMagHeading() {
		return ((int)(magHeading*100))/100;
	}
	
	public float getMagVar() {
		return ((int)(magVar*100))/100;
	}
	
	public float getHeadingBug() {
		return ((int)(headingBug*100))/100;
	}
}
