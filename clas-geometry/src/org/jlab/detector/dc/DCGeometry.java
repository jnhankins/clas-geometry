/**
 * Wrapper class for Clas12-geometry: Drift Chamber.
 * Takes an xml feed of data and parses it using geometry classes
 * To fetch sectors, feed in order: sector, superlayer, layer.
 *
 * TODO: change DetectorHit to DetectorDescriptor, change DetectorDescriptor to DetectorDescriptor, implement interface methods for CLASDetectorGeometry, make hashmaps for each calculated (at init)
 */
package org.jlab.detector.dc;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.jlab.detector.base.*;
import org.jlab.geom.Line3D;
import org.jlab.geom.Path3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;
import org.jlab.vector.Vector3D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Standard class for parsing and storing xml data
 * @author juanvallejo
 */
public class DCGeometry implements CLASDetectorGeometry {
    
    private HashMap<Integer,CLASDCSector>  dcSectors;
	private Map<Integer,DCLayerGeometry> sectors;
	private Map<Integer,Vector3D[]> sectors_by_direction;
	private Map<Integer,Point3D[]> sectors_by_midpoint;
	private Map<Integer,Plane3D> sectors_by_plane;
	private Map<Integer,Double[]> sectors_by_length;
	private Map<Integer,Shape3D> sectors_by_shape;
	private String length_units;
	private String coord_system;
	private boolean isLoaded;
	public static DCSettings settings;

	public DCGeometry() {

	}
	public DCGeometry(String filepath) {
		load(filepath);
	}
	public void initXML(String filename) {
		load(filename);
	}
    public void setLocalCoordinates(Boolean flag) {

    }
	public void setLengthUnits(String a) {
		length_units = a;
	}
	public void setCoordinateSystem(String a) {
		coord_system = a;
	}
	public String getLengthUnits(String a) {
		return length_units;
	}
	public String getCoordinateSystem(String a) {
		return coord_system;
	}
	public String getName() {
		return "DC";
	}
	public Vector3D getDirection(int sector, int superlayer, int layer, int component) {
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		layerFromDescription.set("DC",sector,superlayer,layer,component);

		return getDirection(layerFromDescription);
	}
	public Point3D getMidpoint(int sector, int superlayer, int layer, int component) {
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		layerFromDescription.set("DC",sector,superlayer,layer,component);

		return getMidpoint(layerFromDescription);
	}
	public Plane3D getPlane(int sector, int superlayer, int layer) {
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		layerFromDescription.set("DC",sector,superlayer,layer,0);

		return getPlane(layerFromDescription);
	}
	public Double getLength(int sector, int superlayer, int layer, int component) {
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		layerFromDescription.set("DC",sector,superlayer,layer,component);

		return getLength(layerFromDescription);
	}
	public Line3D getLine(int sector,int superlayer,int layer,int component) {
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		layerFromDescription.set("DC",sector,superlayer,layer,component);

		return getLine(layerFromDescription);
	}
	public Shape3D getShape(int sector,int superlayer,int layer,int component) {
		return null;
	}
	public DCLayerGeometry getLayer(DetectorDescriptor descriptor) {
		return sectors.get(descriptor.hashCode());
	}
	public Double getLength(DetectorDescriptor desc) {
		return sectors_by_length.get(desc.hashCode())[desc.getComponent()];
	}
	public Plane3D getPlane(DetectorDescriptor desc) {
		return sectors_by_plane.get(desc.hashCode());
	}
	public Point3D getMidpoint(DetectorDescriptor desc) {
		return sectors_by_midpoint.get(desc.hashCode())[desc.getComponent()];
	}
	public Line3D getLine(DetectorDescriptor desc) {
		return sectors.get(desc.hashCode()).getUnits()[desc.getComponent()];
	}
	public Vector3D getDirection(DetectorDescriptor desc) {
		return sectors_by_direction.get(desc.hashCode())[desc.getComponent()];
	}
	public DCLayerGeometry getLayer(String sector,String superlayer,String layer) {
		return getLayer(Integer.parseInt(sector),Integer.parseInt(superlayer),Integer.parseInt(layer));
	}
	public DCLayerGeometry getLayer(int sector,int superlayer,int layer) {
		return sectors.get(DCLayerGeometry.hashCode(sector,superlayer,layer));
	}
	public DCLayerGeometry getLayerFromKey(String key) {
		return sectors.get(key);
	}
	public DCLayerGeometry getLayerFromKey(int key) {
		return sectors.get(key);
	}
	/**
	 * Returns points where each wire intersects a plane
	 * @return pointArray comprised of 112 point3D points
	 */
	public Point3D[][][] getIntersections() {
		//normal. Only focusing on first sector
		Point3D[][][] pointArray = new Point3D[6][6][112];
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		DCLayerGeometry layer;
		for(int i=0;i<6;i++){
			for(int x=0;x<6;x++){
				layerFromDescription.set("DC",0,i,x,0);
				layer =	getLayerFromKey(layerFromDescription.hashCode());
				pointArray[i][x] = layer.getIntersections();
			}
		}
		//return three-dimensional point array
		return pointArray;
	}
	/**
	 * Returns vectors indicating the direction of each wire
	 * @return vectors comprised of 112 point3D points
	 */
	public Vector3D[][][] getDirections() {
		Vector3D[][][] vectors = new Vector3D[6][6][112];
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		DCLayerGeometry layer;
		for(int i=0;i<6;i++){
			for(int x=0;x<6;x++){
				layerFromDescription.set("DC",0,i,x,0);
				layer =	getLayerFromKey(layerFromDescription.hashCode());
				vectors[i][x] = layer.getDirections();
			}
		}
		//return three-dimensional vector array
		return vectors;
	}
	/**
	 * Returns planes for each wire
	 * @return planes comprised of 112 point3D points
	 */
	public Plane3D[][] getPlanes() {
		Plane3D[][] planes = new Plane3D[6][6];
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		DCLayerGeometry layer;
		for(int superlayer=0;superlayer<6;superlayer++){
			for(int currentLayer=0;currentLayer<6;currentLayer++){
				layerFromDescription.set("DC",0,superlayer,currentLayer,0);
				layer =	getLayerFromKey(layerFromDescription.hashCode());
				planes[superlayer][currentLayer] = layer.getPlane();
			}
		}
		//return three-dimensional plane array
		return planes;
	}
	/**
	 * Returns array list of "hit-points" where a path intersects each sector's plane
	 * This method uses Face3D to check whether or not a point is contained within the sector
	 * @return hits arraylist object of DetectorHit objects containing point intersection data
	 */
	public ArrayList<DetectorHit> getLayerHits(Path3D path) {
		ArrayList<DetectorHit> hits = new ArrayList<DetectorHit>();
		//iterate through all sectors and call their getHits method. Determine whether or not the returned value is null or a DetectorHit object
		//and store the non-null value into an ArrayList object of DetectorHit
		DetectorDescriptor layerFromDescription = new DetectorDescriptor();
		DCLayerGeometry layer;
		DetectorHit layerHit;
		for(int i=0;i<6;i++){
			for(int x=0;x<6;x++){
				layerFromDescription.set("DC",0,i,x,0);
				layer =	getLayerFromKey(layerFromDescription.hashCode());
				layerHit = layer.getDetectorHit(path);
				if(layerHit != null) hits.add(layerHit);
			}
		}
		//return three-dimensional point array
		return hits;
	}
	@Override
	public ArrayList<DetectorHit> getHits(Path3D path) { ////--
		ArrayList<DetectorHit> layerHits = getLayerHits(path);
		for(int i=0;i<layerHits.size();i++) {
			// w[2] = DCLayerGeometry.getClosestWires(layerHits.get(i));
		}
		return null;
	}
	/**
	 * Load and parses an xml document at the specified path
	 * @return document normalized Document object from parsed XML feed
	 */
	public static Document loadXML(String path) {
		try {
			File file = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			//normalize dom parsing
			document.getDocumentElement().normalize();
			return document;
		} catch(FileNotFoundException e) {
			settings.log("The file '"+path+"' could not be found.",settings.ERROR);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void load(String filepath) {
		//load and parse the XML document
		try {
			Document xml = loadXML(filepath);
			NodeList data = xml.getElementsByTagName("wire_endpoints");
			Node node = data.item(0);
			//indicate that init function has been called and executed successfully
			isLoaded = true;
			//initialize objects which will hold the Sector objects
			sectors = new HashMap<Integer,DCLayerGeometry>();
			sectors_by_plane = new HashMap<Integer,Plane3D>();
			sectors_by_direction = new HashMap<Integer,Vector3D[]>();
			sectors_by_midpoint = new HashMap<Integer,Point3D[]>();
			sectors_by_length = new HashMap<Integer,Double[]>();
			//if node is of type element, let's cast it into its proper type
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element tag = (Element)node;
				//Set main DC class information
				setLengthUnits(tag.getAttribute("length_units"));
				setCoordinateSystem(tag.getAttribute("coordinate_system"));
				//get sector nodes
				NodeList nodes = xml.getElementsByTagName("layer");
				//iterate through each loaded sector and add it to the list and hashmap
				for(int i=0;i<nodes.getLength();i++) {
					//check node types to see if they can be casted into Elements
					if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
						//cast node to element to allow us to read its attributes
						Element element = (Element)nodes.item(i);
						//if element has a senselayer attribute, parse it; else ignore it.
						if(element.hasAttribute("senselayer")) {
							//superlayer modifier. Alters its value so that each sector contains 6 superlayers
							if(element.getAttribute("senselayer").equals("0")) {
								settings.cachedSuperlayerData++;
							}
							if(!element.getAttribute("sector").equals(""+settings.cachedSectorData)) {
								settings.cachedSectorData++;
								settings.cachedSuperlayerData = 0;
							}
							//Create sector object and perform a key repetition check. Hopefully none will repeat.
							DCLayerGeometry sector = new DCLayerGeometry(Integer.parseInt(element.getAttribute("sector")),settings.cachedSuperlayerData,Integer.parseInt(element.getAttribute("senselayer")));
							if(sectors.get(sector.hashCode()) == null) {
								//calculate sector line points. Start by getting left and right point nodes
								Element leftPointsNode = (Element)element.getElementsByTagName("left").item(0);
								Element rightPointsNode = (Element)element.getElementsByTagName("right").item(0);
								//get collections of left x, y, and z points
								String leftXpoints = leftPointsNode.getElementsByTagName("x").item(0).getTextContent().trim();
								String leftYpoints = leftPointsNode.getElementsByTagName("y").item(0).getTextContent().trim();
								String leftZpoints = leftPointsNode.getElementsByTagName("z").item(0).getTextContent().trim();
								// //get collections of right x, y, and z points
								String rightXpoints = rightPointsNode.getElementsByTagName("x").item(0).getTextContent().trim();
								String rightYpoints = rightPointsNode.getElementsByTagName("y").item(0).getTextContent().trim();
								String rightZpoints = rightPointsNode.getElementsByTagName("z").item(0).getTextContent().trim();
								//get individual points from string of data
								String[] x1Units = leftXpoints.split(" ");
								String[] x2Units = rightXpoints.split(" ");
								String[] y1Units = leftYpoints.split(" ");
								String[] y2Units = rightYpoints.split(" ");
								String[] z1Units = leftZpoints.split(" ");
								String[] z2Units = rightZpoints.split(" ");
								//check for x coordinate consistency
								if(x1Units.length != x2Units.length || y1Units.length != y2Units.length || z1Units.length != z2Units.length) {
									settings.log("Invalid sector information. Point mismatch detected in x, y, or z coordinates.",settings.WARNING);
								} else {									
									//parse all points into lines and store them as units
									sector.parseUnits(x1Units,y1Units,z1Units,x2Units,y2Units,z2Units);
									sectors.put(sector.hashCode(),sector);
									//pre-calculate plane for this layer
									sectors_by_plane.put(sector.hashCode(),sector.getPlane());
									//pre-calculate directions vector for current layer
									sectors_by_direction.put(sector.hashCode(),sector.getDirections());
									//pre-calculate midpoints for current layer
									sectors_by_midpoint.put(sector.hashCode(),sector.getMidpoints());
									//pre-calculate lengths for current layer
									sectors_by_length.put(sector.hashCode(),sector.getLengths());
									//announce addition of layer
									settings.log("Added layer. "+sectors.size()+" total.",settings.NOTICE);								
								}
							} else {
								//the hash key generated already exists. This means the attributes for this sector are the same as another one. Not good.
								settings.log("Duplicate sector data detected on layer tag "+(sectors.size()+1)+" of the XML document.\nDuplicate keys are being generated due to replicated sector attribute data.",settings.WARNING);
							}
						} else {
							//XML feed contains items with guardlayers and senselayers. Ignore guardlayer items
							settings.log("Ignoring tag without senselayer attribute.",settings.WARNING);
						}
					} else {
						//In case a node cannot be treated as an ELEMENT_NODE
						settings.log("Node "+nodes.item(i).getNodeName()+" is not of type ELEMENT_NODE",settings.ERROR);
					}
				}
			} else {
				settings.log("Invalid data format found in the XML feed; no nodes are of type ELEMENT_NODE",settings.ERROR);
			}
		} catch(NullPointerException e) {
			settings.log("XML document parsing could not be initialized. No data has been loaded.",settings.ERROR);
		} catch(Exception e) {
			settings.log("An unknown error occurred parsing the data. It may be formatted incorrectly, or have misspelled tags.",settings.ERROR);
			e.printStackTrace();
		}
	}
	/**
	 * Main method - used for debugging and testing other methods
	 * @param args[] - default string array argument
	 */
	public static void main(String args[]) {
		//initializing settings
		settings = new DCSettings();
		settings.displayLogs = true;			//display all logs, warnings, notices, and errors
		settings.displayWarnings = false;		//display or hide warning logs
		settings.displayNotices = false;		//display or hide notice logs
		settings.showDebugData = false;			//display or hide debug data. No correlation to above.
		//initialize and load xml data and objects
		DCGeometry geom = new DCGeometry();
		geom.load("dc_wire_endpoints.xml");
		geom.getHits(new Path3D());
	}
}


/**
 * Stores the settings for the main DC class and provides
 * methods for efficiently logging and classifying output text.
 * @author juanvallejo
 */
class DCSettings {
	public static final int NONE = 0;
	public static final int NOTICE = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	public static boolean showDebugData = false;
	public static boolean displayNotices = true;
	public static boolean displayWarnings = true;
	public static boolean displayErrors = true;
	public static boolean displayLogs = true;
	public static int cachedLayerData = 0;
	public static int cachedRegionData = 0;
	public static int cachedSectorData = -1;
	public static int cachedSuperlayerData = -1;

	public static void write(String a) {
		System.out.println(a);
	}
	public static void write(int a) {
		System.out.println(a);
	}
	public static void log(String text,int flag) {
		if(displayLogs) {
			if(flag == NOTICE) {
				if(displayNotices) {
					write("[Notice] "+text);
				}
			} else if(flag == WARNING) {
				if(displayWarnings) {
					write("[Warning] "+text);
				}
			} else if(flag == ERROR) {
				if(displayErrors) {
					write("[Error] "+text);
				}
			} else if(flag == NONE) {
				write(text);
			} else {
				write("Invalid flag.");
			}
		}
	}
	public static void log(int text,int flag) {
		log(text+"",flag);
	}
	public static void log(String text) {
		log(text,NONE);
	}
	public static void log(int text) {
		log(text+"",NONE);
	}
}