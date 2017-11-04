/**
 * Wrapper class for Clas12-geometry.
 * Takes an xml feed of data and parses it using geometry classes.
 * Stores 114 unit points as Line3D objects.
 */
package org.jlab.detector.dc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import org.jlab.detector.base.*;
import org.jlab.geom.Face3D;
import org.jlab.geom.Line3D;
import org.jlab.geom.Path3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.vector.Vector3D;

/**
 * DC Layer class - Holds all layers and layer data of a specific sector. Stored in a sector array in the DC class.
 * 
 * @author juanvallejo
 */
public class DCLayerGeometry {
	private DetectorDescriptor data;
	public Line3D[] units;
	
	public DCLayerGeometry(int sector,int superlayer,int layer) {
		this.data = new DetectorDescriptor("DC",sector,superlayer,layer);
	}
	public void parseUnits(String[] x1,String[] y1,String[] z1,String[] x2,String[] y2,String[] z2) {
		//initialize units array
		this.units = new Line3D[x1.length];
		//generate and add lines
		for(int i=0;i<x1.length;i++) {
			Line3D line = new Line3D(Double.parseDouble(x1[i]),Double.parseDouble(y1[i]),Double.parseDouble(z1[i]),Double.parseDouble(x2[i]),Double.parseDouble(y2[i]),Double.parseDouble(z2[i]));
			this.units[i] = line;
		}
	}
	public int getSector() {
		return this.data.getSector();
	}
	public int getLayer() {
		return this.data.getLayer();
	}
	public int getSuperlayer() {
		return this.data.getSuperLayer();
	}
	public int getSuperLayer() {
		return this.getSuperlayer();
	}
	public Line3D[] getUnits() {
		return this.units;
	}
	public DetectorDescriptor getLayerDescriptor() {
		return this.data;
	}
	public Point3D[] getMidpoints() {
		Point3D[] points = new Point3D[this.units.length];
		Plane3D plane = new Plane3D(0.,0.,0.,0.,1.,0.);
		for(int i=0;i<points.length;i++) {
			Plane3D.intersection(plane,this.units[i],points[i]);
		}
		return points;
	}
	public Double[] getLengths() {
		Double[] vals = new Double[this.units.length];
		for(int i=0;i<vals.length;i++) {
			vals[i] = this.units[i].length();
		}
		return vals;
	}
	public Point3D[] getIntersections() {
		Plane3D plane = new Plane3D(0.,0.,0.,0.,1.,0.);
		//There are getUnits().length wires to return. Create array accordingly
		Point3D[] points = new Point3D[getUnits().length];
		for(int l=0;l<getUnits().length;l++) {
			Point3D p = new Point3D();
			int status = Plane3D.intersection(plane,getUnits()[l],p);
			//The data coordinates for the point of interection will be stored in third param p
			if(status == 0 || status == 2) {
				System.err.println("no intersection with the plane for line = " + l
					+ " status = " + status);
			}
			points[l] = p;
		}
		return points;
	}
	public Vector3D[] getDirections() {
		Vector3D[] vectors = new Vector3D[getUnits().length];
		for(int v=0;v<getUnits().length;v++) {
			Point3D pointA = getUnits()[v].origin(); 
			Point3D pointB = getUnits()[v].end();
			//create vectors
			Vector3D vectorA = Point3D.getDirVector(pointB,pointA);
			vectorA.unit();
			vectors[v] = vectorA;
		}
		return vectors;
	}
	public Plane3D getPlane() {
		Point3D pointA = getUnits()[0].origin(); 
		Point3D pointB = getUnits()[0].end();
		Point3D pointC = getUnits()[111].origin();
		//create vectors
		Vector3D vectorA = Point3D.getDirVector(pointB,pointA);
		Vector3D vectorB = Point3D.getDirVector(pointC,pointA);
		Vector3D normal = vectorA.cross(vectorB);
		normal.unit();
		//create plane
		Plane3D plane = new Plane3D(pointA,normal);
		return plane;
	}
	public DetectorHit getDetectorHit(Path3D path) {
		DetectorHit hitpoint = null;
		if(path.nodes() > 0) {
			Line3D[] lines = new Line3D[path.nodes()-1];
			Point3D[] point = new Point3D[2];
			point[0] = new Point3D();
			point[1] = new Point3D();
			Point3D pointA = getUnits()[0].origin(); 
			Point3D pointB = getUnits()[0].end();
			Point3D pointC = getUnits()[getUnits().length-1].origin();
			Point3D pointD = getUnits()[getUnits().length-1].end();
			Point3D pointE = new Point3D();
			Face3D faceA = new Face3D(pointA,pointB,pointC);
			Face3D faceB = new Face3D(pointA,pointC,pointD);
			for(int i=0;i<path.nodes()-1;i++) {
				lines[i] = new Line3D(path.getNode(i),path.getNode(i+1));
				if(Face3D.intersection(faceA,lines[i],point[0])) {
					if(hitpoint == null) {
						hitpoint = new DetectorHit(getLayerDescriptor(),point[0],pointE);
					}
				} else if(Face3D.intersection(faceB,lines[i],point[1])) {
					if(hitpoint == null) {
						hitpoint = new DetectorHit(getLayerDescriptor(),point[1],pointE);
					}
				}
			}
		}
		return hitpoint;
	}
	public static Point3D[] getClosestWires(DetectorHit hit) {
		// double distance = null;
		// DetectorHit closestWire = null;
		// for(int i=0;i<getUnits().length;i++) {
		// 	double difference = getUnits()[i].distance(hit.position());
		// 	if(distance == null || difference < distance) {
		// 		distance = difference;
		// 		closestWire = getUnits()[i];
		// 	}
		// }
		// return new Point3D[2];
		return null;
	}
	public int hashCode() {
		return this.data.hashCode();
	}
	public static int hashCode(int sector,int superlayer,int layer) {
		DetectorDescriptor desc = new DetectorDescriptor("DC",sector,superlayer,layer);
		return desc.hashCode();
	}
	public static String getMD5(String sector,String superlayer,String layer) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update((sector+superlayer+layer).getBytes());
			
			byte[] md5bytes = md5.digest();
			BigInteger bigInt = new BigInteger(1,md5bytes);
			String hash = bigInt.toString(16);

			while(hash.length() < 32) {
				hash = "0"+hash;
			}
			return hash;
		} catch(NoSuchAlgorithmException e) {
			System.out.println("There was an error generating a hash for this object.");
			return "";
		}
	}
}