/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.base;

import java.util.ArrayList;
import org.jlab.geom.Line3D;
import org.jlab.geom.Path3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;
import org.jlab.vector.Vector3D;

/**
 *
 * @author gavalian
 */
public interface CLASDetectorGeometry {
    
    String getName();
    
    void initXML(String filename);
    void setLocalCoordinates(Boolean flag);
    /**
     * Returns direction vector for given component in CLAS (Lab)
     * coordinate System.
     * @param desc
     * @return 
     */
    Vector3D getDirection(DetectorDescriptor desc);
    Point3D  getMidpoint(DetectorDescriptor desc);
    Plane3D  getPlane(DetectorDescriptor desc);
    Double   getLength(DetectorDescriptor desc);
    
    Vector3D getDirection(int sector, int superlayer, int layer, int component);
    Point3D  getMidpoint(int sector, int superlayer, int layer, int component);
    Plane3D  getPlane(int sector, int superlayer, int layer);
    Double   getLength(int sector, int superlayer, int layer, int component);
    
    Line3D   getLine(int sector, int superlayer, int layer, int component);
    Shape3D  getShape(int sector, int superlayer, int layer, int componen);
    
    ArrayList<DetectorHit> getLayerHits(Path3D path);
    ArrayList<DetectorHit> getHits(Path3D path);
    
}
