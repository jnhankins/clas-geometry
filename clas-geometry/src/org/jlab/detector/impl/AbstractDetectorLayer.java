/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.base.DetectorHit;
import org.jlab.detector.base.DetectorLayer;
import org.jlab.detector.base.DetectorTypes;
import org.jlab.geom.Face3D;
import org.jlab.geom.Line3D;
import org.jlab.geom.Path3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;
import org.jlab.geom.Transformation3D;
import org.jlab.vector.Vector3D;

/**
 *
 * @author J. Hankins
 */
public abstract class AbstractDetectorLayer implements DetectorLayer {
    private final String detectorName;
    private final int detectorId;
    private final int sectorId;
    private final int superlayerId;
    private final int layerId;
    private final DetectorPaddle defaultPaddle;
    private final HashMap<Integer,DetectorPaddle> paddles;
    private final Plane3D plane;
    private final Shape3D boundary;
    private final boolean useBoundaryFilter;
    protected Transformation3D transformation;

    protected AbstractDetectorLayer(
            String detectorName,
            int sector,
            int superlayer,
            int layer,
            List<DetectorPaddle> paddles,
            Plane3D plane,
            Shape3D boundary,
            boolean useBoundaryFilter) {
        if (detectorName == null)
            throw new IllegalArgumentException("Error: detectorName cannot be null");
        if (sector < 0 || sector>=6)
            throw new IllegalArgumentException("Error: sector should be in range [0,5], but sector="+sector);
        if (superlayer < 0)
            throw new IllegalArgumentException("Error: superlayer cannot be negative");
        if (layer < 0)
            throw new IllegalArgumentException("Error: layer cannot be negative");
        if (paddles == null)
            throw new IllegalArgumentException("Error: paddles cannot be null");
        if (plane == null)
            throw new IllegalArgumentException("Error: plane cannot be null");
        if (boundary == null)
            throw new IllegalArgumentException("Error: boundary cannot be null");
        this.detectorName = detectorName;
        detectorId = DetectorTypes.getType(detectorName).id();
        sectorId = sector;
        superlayerId = superlayer;
        layerId = layer;
        defaultPaddle = new DetectorPaddle(-1, 1, 1, 1);
        defaultPaddle.translateXYZ(0, 0, -10000);
        this.paddles = new HashMap();
        for (DetectorPaddle paddle : paddles)
            this.paddles.put(paddle.getComponent(), paddle);
        this.plane = plane;
        this.boundary = boundary;
        this.useBoundaryFilter = useBoundaryFilter;
        transformation = new Transformation3D();
    }
    
    @Override
    public int numberOfComponents() {
        return paddles.size();
    }
    
    @Override 
    public String getDetectorName() {
        return detectorName;
    }
    
    @Override
    public int getDetectorId() {
        return detectorId;
    }
    
    @Override
    public int getSectorId() {
        return sectorId;
    }
    
    @Override
    public int getSuperLayerId() {
        return superlayerId;
    }
    
    @Override
    public int getLayerId() {
        return layerId;
    }

    @Override
    public Transformation3D getTransformation() {
        return transformation.copy();
    }

    @Override
    public void setTransformation(Transformation3D trans) {
        if (trans == null)
            throw new IllegalArgumentException("Error: trans cannot be null");
        
        Transformation3D oldTransormation = transformation;
        Transformation3D newTransormation = trans.copy();
        
        for (DetectorPaddle paddle : paddles.values()) {
            oldTransormation.reverseTransform(paddle);
            newTransormation.forwardTransform(paddle);
        }
        
        oldTransormation.reverseTransform(boundary);
        newTransormation.forwardTransform(boundary);
        
        oldTransormation.reverseTransform(plane);
        newTransormation.forwardTransform(plane);
        
        transformation = newTransormation;
    }

    @Override
    public Point3D getMidpoint(int component) {
        DetectorPaddle paddle = paddles.get(component);
        if (paddle == null) {
            System.out.println("Warning: No such component: component="+component);
            paddle = defaultPaddle;
        }
        return paddle.getMidpoint();
    }

    @Override
    public Vector3D getDirection(int component) {
        DetectorPaddle paddle = paddles.get(component);
        if (paddle == null) {
            System.out.println("Warning: No such component: component="+component);
            paddle = defaultPaddle;
        }
        return paddle.getDirection();
    }
    
    @Override
    public Line3D getLine(int component) {
        DetectorPaddle paddle = paddles.get(component);
        if (paddle == null) {
            System.out.println("Warning: No such component: component="+component);
            paddle = defaultPaddle;
        }
        return paddle.getLine();
    }

    @Override
    public Double getLength(int component) {
        DetectorPaddle paddle = paddles.get(component);
        if (paddle == null) {
            System.out.println("Warning: No such component: component="+component);
            paddle = defaultPaddle;
        }
        return paddle.getLength();
    }

    @Override
    public Shape3D getShape(int component) {
        DetectorPaddle paddle = paddles.get(component);
        if (paddle == null) {
            System.out.println("Warning: No such component: component="+component);
            paddle = defaultPaddle;
        }
        return paddle.getShape();
    }

    @Override
    public Path3D getDrawable(int component, double phi) {
        DetectorPaddle paddle = paddles.get(component);
        if (paddle != null) {
            return paddle.getDrawable(phi);
        } else {
            System.out.println("Warning: No such component: component="+component);
            return new Path3D();
        }
    }
    
    @Override
    public List<Path3D> getAllDrawables(int component, double phi) {
        List<Path3D> drawables = new ArrayList();
        for (DetectorPaddle paddle: paddles.values())
            drawables.add(paddle.getDrawable(phi));
        return drawables;
    }
    
    @Override
    public Shape3D getBoundary() {
        return boundary;
    }

    @Override
    public Plane3D getPlane() {
        return plane;
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

            if (useBoundaryFilter) {
                boolean noHit = true;
                for(int f=0; f<boundary.size() && noHit; f++)
                    if (Face3D.intersection(boundary.face(f), line, errorPt))
                        noHit = false;
                if (noHit)
                    continue;
            }
            
            // For each paddle in the in the sector:
            for (DetectorPaddle paddle: paddles.values()) {

                // If the line intersects the front face of the paddle
                if (paddle.intersectFront(line, intersectPt)) {
                    DetectorDescriptor desc = new DetectorDescriptor(
                            detectorId, sectorId, superlayerId, layerId, paddle.getComponent());
                    DetectorHit hit = new DetectorHit(desc, intersectPt, errorPt);
                    return hit;
                }
            }
        }

        return null;
    }

    @Override
    public ArrayList<DetectorHit> getHits(Path3D path) {        // Initialize the hit list array
        ArrayList<DetectorHit> hitList = new ArrayList<>();

        if (path == null) {
            return hitList;
        }

        // Create an intersect point object that will contain the line-face
        // insercetion coordinates, if there is an intersection, set by the
        // intersect method
        Point3D intersectPt1 = new Point3D();
        Point3D intersectPt2 = new Point3D();

        // Create an error point object which will remain (0,0,0) because we do
        // not have error data
        Point3D errorPt = new Point3D();

        // Use the path's points to create lines...
        // For each line:
        int nPathNodes = path.nodes();
        Line3D line = new Line3D();
        for (int node = 1; node < nPathNodes; node++) {
            line.set(path.getNode(node - 1), path.getNode(node));

            if (useBoundaryFilter) {
                boolean noHit = true;
                for(int f=0; f<boundary.size() && noHit; f++)
                    if (Face3D.intersection(boundary.face(f), line, errorPt))
                        noHit = false;
                if (noHit)
                    continue;
            }

            // For each paddle in the in the sector:
            for (DetectorPaddle paddle : paddles.values()) {

                // If the line intersects the front, back, or sides of the paddle
                if (paddle.intersectIgnoreEnds(line, intersectPt1, intersectPt2)) {
                    
                    // Create a descriptor for this paddle
                    DetectorDescriptor desc = new DetectorDescriptor(
                            detectorId, sectorId, superlayerId, layerId, paddle.getComponent());
                    
                    // Add the hits to the list
                    hitList.add(new DetectorHit(desc, intersectPt1, errorPt));
                    hitList.add(new DetectorHit(desc, intersectPt2, errorPt));
                }
            }
        }

        return hitList;
    }
}
