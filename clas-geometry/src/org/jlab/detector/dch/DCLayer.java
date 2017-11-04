/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.dch;

import java.util.ArrayList;
import java.util.List;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.base.DetectorHit;
import org.jlab.detector.impl.AbstractDetectorLayer;
import org.jlab.detector.impl.DetectorPaddle;
import org.jlab.geom.Face3D;
import org.jlab.geom.Line3D;
import org.jlab.geom.Path3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;
import org.jlab.geom.Transformation3D;

/**
 *
 * @author J. Hankins
 */
public class DCLayer extends AbstractDetectorLayer {
    private final Plane3D midplane;
    
    protected DCLayer(
            int sector, int superlayer, int layer,
            List<DetectorPaddle> paddles, Plane3D plane, Shape3D boundary,
            Plane3D midplane) {
        super("DC", sector, superlayer, layer, paddles, plane, boundary, true);
        this.midplane = midplane;
    }
    
    @Override
    public void setTransformation(Transformation3D trans) {
        Transformation3D oldTransormation = transformation;
        Transformation3D newTransormation = trans;
        oldTransormation.reverseTransform(midplane);
        newTransormation.forwardTransform(midplane);
        super.setTransformation(trans);
    }
    
    public Plane3D getMidplane() {
        return midplane;
    }
    
    @Override
    public DetectorHit getLayerHit(Path3D path) {
        if (path == null) {
            return null;
        }

        // Create an intersect point object that will contain the line-face
        // insercetion coordinates, if there is an intersection, set by the
        // intersect method
        Point3D intersectPt = new Point3D();

        // Create an error point object which will remain (0,0,0) because we do
        // not have error data
        Point3D errorPt = new Point3D();

        // Use the path's points to create lines...
        // For each line:
        int nPathNodes = path.nodes();
        Line3D line = new Line3D();
        for (int node = 1; node < nPathNodes; node++) {
            line.set(path.getNode(node - 1), path.getNode(node));
            
            Shape3D boundary = getBoundary();
            for(int f=0; f<boundary.size(); f++) {
                if (Face3D.intersection(boundary.face(f), line, intersectPt)) {
                    DetectorDescriptor desc = new DetectorDescriptor(
                            getDetectorId(), getSectorId(), getSuperLayerId(), getLayerId(), 0);
                    return new DetectorHit(desc, intersectPt, errorPt);
                }
            }
        }

        return null;
    }
    @Override
    public ArrayList<DetectorHit> getHits(Path3D path) {
        ArrayList<DetectorHit> list = new ArrayList();
        if (path == null) {
            return list;
        }

        
        // Create an intersect point object that will contain the line-face
        // insercetion coordinates, if there is an intersection, set by the
        // intersect method
        Point3D intersectPt = new Point3D();

        // Create an error point object which will remain (0,0,0) because we do
        // not have error data
        Point3D errorPt = new Point3D();

        // Use the path's points to create lines...
        // For each line:
        int nPathNodes = path.nodes();
        Line3D line = new Line3D();
        for (int node = 1; node < nPathNodes; node++) {
            line.set(path.getNode(node - 1), path.getNode(node));
            
            Shape3D boundary = getBoundary();
            for(int f=0; f<boundary.size(); f++) {
                if (Face3D.intersection(boundary.face(f), line, intersectPt)) {
                    
                    int closestComponentId = -1;
                    double closestDist = Double.POSITIVE_INFINITY;
                    for (int componentId=0; componentId<numberOfComponents(); componentId++) {
                        Line3D wire = getLine(componentId);
                        double dist = wire.distance(line).length();
                        if(closestDist < dist) {
                            closestDist = dist;
                            closestComponentId = componentId;
                        }
                    }
                        
                    DetectorDescriptor desc = new DetectorDescriptor(
                            getDetectorId(), getSectorId(), getSuperLayerId(), getLayerId(), closestComponentId);
                    list.add(new DetectorHit(desc, intersectPt, errorPt));
                    return list;
                }
            }
        }

            return list;
    }
    
    @Override
    public void show() {
        System.out.println(toString());
    }
    
    @Override
    public String toString() {
        return String.format("Layer: %8s %2d %2d %2d (%7.1f %7.1f %7.1f) (%7.1f %7.1f %7.1f) (%7.1f %7.1f %7.1f) (%7.1f %7.1f %7.1f)",
            getDetectorName(),
            getSectorId(),
            getSuperLayerId(),
            getLayerId(),
            getBoundary().face(0).point(0).x(),
            getBoundary().face(0).point(0).y(),
            getBoundary().face(0).point(0).z(),
            getBoundary().face(0).point(1).x(),
            getBoundary().face(0).point(1).y(),
            getBoundary().face(0).point(1).z(),
            getBoundary().face(0).point(2).x(),
            getBoundary().face(0).point(2).y(),
            getBoundary().face(0).point(2).z(),
            getBoundary().face(1).point(0).x(),
            getBoundary().face(1).point(0).y(),
            getBoundary().face(1).point(0).z());
    }
}
