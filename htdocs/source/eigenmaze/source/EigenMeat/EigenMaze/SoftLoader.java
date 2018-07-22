package EigenMeat.EigenMaze;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

// A factory class that will load XML and possibly Lua files to create new objects.
// This isn't how it should be done, and I won't do anything further to this file
// until I learn some more patterns and figure out the best way to interlace everything.
// - Okay, I did more stuff to it even though I promised not to. I'll figure it out later.
class SoftLoader {

	private class XMLElement {
		private String name;
		private String[] attrNames;
		private String[] attrValues;
		private String value;
		
		public XMLElement(String n, String[] an, String[] av, String v) {
			name = n;
			attrNames = an;
			attrValues = av;
			value = v;
		}
		
		public String getName() {
			return name;
		}
		
		public String getValue() {
			return value;
		}
		
		public String[] getAttributeNames() {
			return attrNames;
		}
		
		public String[] getAttributeValues() {
			return attrValues;
		}
	}
	
	private class XMLParentElement extends XMLElement {
		private XMLElement[] children;
		
		public XMLParentElement(String n, String[] an, String[] av, String v, XMLElement[] c) {
			super(n, an, av, v);
			children = c;
		}
		
		public XMLElement[] getChildren() {
			return children;
		}
		
		public void setChildren(XMLElement[] c) {
			children = c;
		}
	}
	
	private class XMLParser {
		private DocumentBuilder builder;
		
		public XMLParser() {
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		public XMLElement loadXMLFile(String filename) {
			Document dom = null;
			try {
				dom = builder.parse(new File(filename));
			} catch (Exception e) {
				System.err.println("Could not open file "+filename);
				return null;
			}
			XMLElement tree = recursiveBuildNodes(dom, 0);
			return tree;
		}
		
		private XMLElement recursiveBuildNodes(Node node, int indent) {
			if (node == null)
				return null;
			if (node.getNodeType() == Node.DOCUMENT_NODE) {
				NodeList children = node.getChildNodes();
				if (children.getLength() > 0)
					return recursiveBuildNodes(children.item(0), indent+1);
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap attribs = node.getAttributes();
				Vector attrNames = new Vector(0,1);
				Vector attrValues = new Vector(0,1);
				for (int i=0; i<attribs.getLength(); i++) {
					attrNames.add(attribs.item(i).getNodeName());
					attrValues.add(attribs.item(i).getNodeValue());
				}
				String[] anArray = null;
				String[] avArray = null;
				if (attrNames.size() > 0)
					anArray = (String[])attrNames.toArray(new String[1]);
				if (attrValues.size() > 0)
					avArray = (String[])attrValues.toArray(new String[1]);
				
				NodeList children = node.getChildNodes();
				Vector xmlchildren = new Vector(0,1);
				for (int i=0; i<children.getLength(); i++) {
					XMLElement elem = recursiveBuildNodes(children.item(i), indent+1);
					if (elem != null) {
						if (elem.getName().equals("#text")) {
							if (!elem.getValue().equals(""))
								xmlchildren.add(elem);
						}
						else
							xmlchildren.add(elem);
					}
				}
				String value = "";
				if (xmlchildren.size() > 0 && ((XMLElement)xmlchildren.get(0)).getName().equals("#text")) {
					value = ((XMLElement)xmlchildren.get(0)).getValue();
					xmlchildren.remove(0);
				}
				if (xmlchildren.size() > 0)
					return new XMLParentElement(node.getNodeName(), anArray, avArray, value, (XMLElement[])xmlchildren.toArray(new XMLElement[1]));
				else
					return new XMLElement(node.getNodeName(), anArray, avArray, value);
			}
			else if (node.getNodeType() == Node.TEXT_NODE && !node.getNodeValue().matches("\\s+"))
				return new XMLElement(node.getNodeName(), null, null, node.getNodeValue());
			
			return null;
		}
		
		public void printTree(XMLElement node, int indent) {
			for (int i=0; i<indent; i++)
				System.out.print("   ");
			System.out.print("<"+node.getName());
			if (node.getAttributeNames() != null) {
				for (int i=0; i<node.getAttributeNames().length; i++)
					System.out.print(" "+node.getAttributeNames()[i]+"="+node.getAttributeValues()[i]);
			}
			System.out.print(">");
			if (!node.getValue().equals(""))
				System.out.println(": "+node.getValue());
			else
				System.out.println();
			if (node instanceof XMLParentElement) {
				XMLParentElement parent = (XMLParentElement)node;
				XMLElement[] children = parent.getChildren();
				for (int i=0; i<children.length; i++)
					printTree(children[i], indent+1);
			}
		}
	}
	
	private XMLParser parser;
	
	public SoftLoader() {
		parser = new XMLParser();
	}
	
	public Entity loadEntityFile(String file) {
		// load the element tree
		XMLElement r = parser.loadXMLFile(file);
		if (!(r instanceof XMLParentElement)) //no children to look at
			return null;
		XMLParentElement root = (XMLParentElement)r;
		
		// do all the checking to make sure we're really defining a Projectile class
		if (!root.getName().equals("Entity"))
			return null;
		String[] attrs = root.getAttributeNames();
		int typeIndex = -1;
		for (int i=0; i<attrs.length; i++)
			if (attrs[i].equals("type"))
				typeIndex = i;
		if (typeIndex == -1)
			return null;
		String[] attrValues = root.getAttributeValues();
		if (attrValues[typeIndex].equals("Projectile"))
			return loadProjectileFile(file);
		else if (attrValues[typeIndex].equals("Ship"))
			return loadShipFile(file);
		
		return null;
	}
	
	public Projectile loadProjectileFile(String file) {
		// load the element tree
		XMLElement r = parser.loadXMLFile(file);
		if (!(r instanceof XMLParentElement)) //no children to look at
			return null;
		XMLParentElement root = (XMLParentElement)r;
		
		// do all the checking to make sure we're really defining a Projectile class
		if (!root.getName().equals("Entity"))
			return null;
		String[] attrs = root.getAttributeNames();
		int typeIndex = -1;
		for (int i=0; i<attrs.length; i++)
			if (attrs[i].equals("type"))
				typeIndex = i;
		if (typeIndex == -1)
			return null;
		String[] attrValues = root.getAttributeValues();
		if (!attrValues[typeIndex].equals("Projectile"))
			return null;
		
		// this is definitely a Projectile, now let's tear it apart
		XMLElement[] data = root.getChildren();
		// define a couple things
		String modelPath = null;
		float modelScale = 0;
		PrototypeProjectile proto = new PrototypeProjectile();
		for (int i=0; i<data.length; i++) {
			XMLElement child = data[i];
			String name = child.getName();
			if (name.equals("Name"))
				proto.setName(child.getValue());
			else if (name.equals("Model")) {
				if (modelScale > 0)
					proto.setMesh(MeshLoader.loadMesh(child.getValue(), modelScale));
				modelPath = child.getValue();
			}
			else if (name.equals("ModelScale")) {
				if (modelPath != null)
					proto.setMesh(MeshLoader.loadMesh(modelPath, Float.parseFloat(child.getValue())));
				modelScale = Float.parseFloat(child.getValue());
			}
			else if (name.equals("SphereRadius"))
				proto.setBoundingSphere(Float.parseFloat(child.getValue()));
			else if (name.equals("Mass"))
				proto.setMass(Float.parseFloat(child.getValue()));
			else if (name.equals("Damage"))
				proto.setDamage(Float.parseFloat(child.getValue()));
			else if (name.equals("BlastRadius"))
				proto.setBlastRadius(Float.parseFloat(child.getValue()));
			else if (name.equals("Bounces"))
				proto.setBounces(Boolean.valueOf(child.getValue()).booleanValue());
			else if (name.equals("Weathervane"))
				proto.setWeathervane(Boolean.valueOf(child.getValue()).booleanValue());
			else if (name.equals("Particles")) {
				attrs = child.getAttributeNames(); //attr already defined above and now unused
				typeIndex = -1; //typeIndex already defined above and now unused
				for (int j=0; j<attrs.length; j++)
					if (attrs[j].equals("type"))
						typeIndex = j;
				if (typeIndex != -1) {
					attrValues = child.getAttributeValues(); //attrValues already defined above and now unused
					String partType = attrValues[typeIndex];
					ParticleEffect pe = new ParticleEffect();
					float r1=0, g1=0, b1=0;
					float r2=0, g2=0, b2=0;
					float speedMin=0, speedMax=0;
					long lifeMin=0, lifeMax=0;
					float scaleMin=0, scaleMax=0;
					XMLElement[] childdata = ((XMLParentElement)child).getChildren();
					for (int j=0; j<childdata.length; j++) {
						XMLElement grandchild = childdata[j];
						String grandname = grandchild.getName();
						if (grandname.equals("Size"))
							pe.setSize(Integer.parseInt(grandchild.getValue()));
						else if (grandname.equals("ColorMin")) {
							String[] rgb = grandchild.getValue().split(",");
							r1 = Float.parseFloat(rgb[0]);
							g1 = Float.parseFloat(rgb[1]);
							b1 = Float.parseFloat(rgb[2]);
						}
						else if (grandname.equals("ColorMax")) {
							String[] rgb = grandchild.getValue().split(",");
							r2 = Float.parseFloat(rgb[0]);
							g2 = Float.parseFloat(rgb[1]);
							b2 = Float.parseFloat(rgb[2]);
						}
						else if (grandname.equals("SpeedMin"))
							speedMin = Float.parseFloat(grandchild.getValue());
						else if (grandname.equals("SpeedMax"))
							speedMax = Float.parseFloat(grandchild.getValue());
						else if (grandname.equals("LifeMin"))
							lifeMin = Long.parseLong(grandchild.getValue());
						else if (grandname.equals("LifeMax"))
							lifeMax = Long.parseLong(grandchild.getValue());
						else if (grandname.equals("ScaleMin"))
							scaleMin = Float.parseFloat(grandchild.getValue());
						else if (grandname.equals("ScaleMax"))
							scaleMax = Float.parseFloat(grandchild.getValue());
					}
					pe.setColorRange(r1,g1,b1, r2,g2,b2);
					pe.setSpeedRange(speedMin, speedMax);
					pe.setLifeRange(lifeMin, lifeMax);
					pe.setScaleRange(scaleMin, scaleMax);
					if (partType.equals("death"))
						proto.setDeathExplosion(pe);
				}
			}
		}
		return proto;
	}
	
	public Ship loadShipFile(String file) {
		// load the element tree
		XMLElement r = parser.loadXMLFile(file);
		if (!(r instanceof XMLParentElement)) //no children to look at
			return null;
		XMLParentElement root = (XMLParentElement)r;
		
		// do all the checking to make sure we're really defining a Ship class
		if (!root.getName().equals("Entity"))
			return null;
		String[] attrs = root.getAttributeNames();
		int typeIndex = -1;
		for (int i=0; i<attrs.length; i++)
			if (attrs[i].equals("type"))
				typeIndex = i;
		if (typeIndex == -1)
			return null;
		String[] attrValues = root.getAttributeValues();
		if (!attrValues[typeIndex].equals("Ship"))
			return null;
		
		// this is definitely a Ship, now let's tear it apart
		XMLElement[] data = root.getChildren();
		// define a couple things
		String modelPath = null;
		float modelScale = 0;
		PrototypeShip proto = new PrototypeShip();
		for (int i=0; i<data.length; i++) {
			XMLElement child = data[i];
			String name = child.getName();
			if (name.equals("Name"))
				proto.setName(child.getValue());
			else if (name.equals("Model")) {
				if (modelScale > 0)
					proto.setMesh(MeshLoader.loadMesh(child.getValue(), modelScale));
				modelPath = child.getValue();
			}
			else if (name.equals("ModelScale")) {
				if (modelPath != null)
					proto.setMesh(MeshLoader.loadMesh(modelPath, Float.parseFloat(child.getValue())));
				modelScale = Float.parseFloat(child.getValue());
			}
			else if (name.equals("SphereRadius"))
				proto.setBoundingSphere(Float.parseFloat(child.getValue()));
			else if (name.equals("TurnSpeed"))
				proto.setTurnSpeed(Float.parseFloat(child.getValue()));
			else if (name.equals("Acceleration"))
				proto.setAcceleration(Float.parseFloat(child.getValue()));
			else if (name.equals("MaxSpeed"))
				proto.setMaxSpeed(Float.parseFloat(child.getValue()));
			else if (name.equals("Shields"))
				proto.setShields(Float.parseFloat(child.getValue()));
			else if (name.equals("Mass"))
				proto.setMass(Float.parseFloat(child.getValue()));
			else if (name.equals("ProjectileLauncher")) {
				String pr = null;
				float speed = 0;
				int delay = 0;
				int life = 0;
				XMLElement[] childdata = ((XMLParentElement)child).getChildren();
				for (int j=0; j<childdata.length; j++) {
					XMLElement grandchild = childdata[j];
					String grandname = grandchild.getName();
					if (grandname.equals("Projectile"))
						pr = grandchild.getValue();
					else if (grandname.equals("Speed"))
						speed = Float.parseFloat(grandchild.getValue());
					else if (grandname.equals("FireDelay"))
						delay = Integer.parseInt(grandchild.getValue());
					else if (grandname.equals("Life"))
						life = Integer.parseInt(grandchild.getValue());
				}
				ProjectileLauncher pl = new ProjectileLauncher(null, speed, delay, life);
				pl.setProjectile(pr);
				proto.addProjectileLauncher(pl);
			}
			else if (name.equals("Particles")) {
				attrs = child.getAttributeNames(); //attr already defined above and now unused
				typeIndex = -1; //typeIndex already defined above and now unused
				for (int j=0; j<attrs.length; j++)
					if (attrs[j].equals("type"))
						typeIndex = j;
				if (typeIndex != -1) {
					attrValues = child.getAttributeValues(); //attrValues already defined above and now unused
					String partType = attrValues[typeIndex];
					ParticleEffect pe = new ParticleEffect();
					float r1=0, g1=0, b1=0;
					float r2=0, g2=0, b2=0;
					float speedMin=0, speedMax=0;
					long lifeMin=0, lifeMax=0;
					float scaleMin=0, scaleMax=0;
					XMLElement[] childdata = ((XMLParentElement)child).getChildren();
					for (int j=0; j<childdata.length; j++) {
						XMLElement grandchild = childdata[j];
						String grandname = grandchild.getName();
						if (grandname.equals("Size"))
							pe.setSize(Integer.parseInt(grandchild.getValue()));
						else if (grandname.equals("ColorMin")) {
							String[] rgb = grandchild.getValue().split(",");
							r1 = Float.parseFloat(rgb[0]);
							g1 = Float.parseFloat(rgb[1]);
							b1 = Float.parseFloat(rgb[2]);
						}
						else if (grandname.equals("ColorMax")) {
							String[] rgb = grandchild.getValue().split(",");
							r2 = Float.parseFloat(rgb[0]);
							g2 = Float.parseFloat(rgb[1]);
							b2 = Float.parseFloat(rgb[2]);
						}
						else if (grandname.equals("SpeedMin"))
							speedMin = Float.parseFloat(grandchild.getValue());
						else if (grandname.equals("SpeedMax"))
							speedMax = Float.parseFloat(grandchild.getValue());
						else if (grandname.equals("LifeMin"))
							lifeMin = Long.parseLong(grandchild.getValue());
						else if (grandname.equals("LifeMax"))
							lifeMax = Long.parseLong(grandchild.getValue());
						else if (grandname.equals("ScaleMin"))
							scaleMin = Float.parseFloat(grandchild.getValue());
						else if (grandname.equals("ScaleMax"))
							scaleMax = Float.parseFloat(grandchild.getValue());
					}
					pe.setColorRange(r1,g1,b1, r2,g2,b2);
					pe.setSpeedRange(speedMin, speedMax);
					pe.setLifeRange(lifeMin, lifeMax);
					pe.setScaleRange(scaleMin, scaleMax);
					if (partType.equals("death"))
						proto.setDeathExplosion(pe);
					else if (partType.equals("thrust"))
						proto.setThrustParticles(pe);
				}
			}
		}
		return proto;
	}
	
	public Preferences loadPreferencesFile(String file) {
		// load the element tree
		XMLElement r = parser.loadXMLFile(file);
		if (!(r instanceof XMLParentElement)) //no children to look at
			return null;
		XMLParentElement root = (XMLParentElement)r;
		
		// do all the checking to make sure we're really defining the preferences
		if (!root.getName().equals("Preferences"))
			return null;
		
		// now let's tear it apart
		XMLElement[] data = root.getChildren();
		PrototypePreferences proto = new PrototypePreferences();
		for (int i=0; i<data.length; i++) {
			XMLElement child = data[i];
			String name = child.getName();
			if (name.equals("PlayerName"))
				proto.setPlayerName(child.getValue());
			else if (name.equals("WindowResolution")) {
				String[] xy = child.getValue().split("x");
				if (xy.length > 1) {
					proto.setWindowedResX(Integer.parseInt(xy[0]));
					proto.setWindowedResY(Integer.parseInt(xy[1]));
				}
			}
			else if (name.equals("DefaultMazeSize")) {
				String[] xy = child.getValue().split("x");
				if (xy.length > 1) {
					proto.setDefaultMazeSizeX(Integer.parseInt(xy[0]));
					proto.setDefaultMazeSizeY(Integer.parseInt(xy[1]));
				}
			}
			else if (name.equals("Fullscreen"))
				proto.setStartsFullscreen(Boolean.valueOf(child.getValue()).booleanValue());
			else if (name.equals("EntityShadows"))
				proto.setEntityShadows(Boolean.valueOf(child.getValue()).booleanValue());
		}
		return proto;
	}
	
	public void writePreferencesFile(Preferences prefs, String filepath) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(filepath), true);
			writer.println("<Preferences>");
			writer.println("	<PlayerName>"+prefs.getPlayerName()+"</PlayerName>");
			writer.println("	<WindowResolution>"+prefs.getWindowedResX()+"x"+prefs.getWindowedResY()+"</WindowResolution>");
			writer.println("	<DefaultMazeSize>"+prefs.getDefaultMazeSizeX()+"x"+prefs.getDefaultMazeSizeY()+"</DefaultMazeSize>");
			writer.println("	<Fullscreen>"+prefs.getStartsFullscreen()+"</Fullscreen>");
			writer.println("	<EntityShadows>"+prefs.getEntityShadows()+"</EntityShadows>");
			writer.println("</Preferences>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SoftLoader loader = new SoftLoader();
	}
}