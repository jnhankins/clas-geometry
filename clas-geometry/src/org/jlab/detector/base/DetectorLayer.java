/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.base;

import java.util.ArrayList;
import java.util.List;
import org.jlab.geom.Line3D;
import org.jlab.geom.Path3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;
import org.jlab.geom.Transformation3D;
import org.jlab.vector.Vector3D;

/**
 *
 * @author gavalian
 */
public interface DetectorLayer {
    int numberOfComponents();
    
    String getDetectorName();
    int getDetectorId();
    int getSectorId();
    int getSuperLayerId();
    int getLayerId();
    
    Transformation3D getTransformation();
    void             setTransformation(Transformation3D trans);
    
    Point3D  getMidpoint(int component);
    Vector3D getDirection(int component);
    Line3D   getLine(int component);
    Double   getLength(int component);
    Shape3D  getShape(int component);
    Path3D   getDrawable(int component, double phi);
    List<Path3D>  getAllDrawables(int component, double phi);
    
    Shape3D  getBoundary();
    Plane3D  getPlane();
    
    DetectorHit            getLayerHit(Path3D path);
    ArrayList<DetectorHit> getHits(Path3D path);
    
    void show();
}
