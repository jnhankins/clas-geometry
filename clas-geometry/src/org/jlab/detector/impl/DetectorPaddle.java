/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.impl;

import java.util.ArrayList;
import org.jlab.geom.Face3D;
import org.jlab.geom.Line3D;
import org.jlab.geom.Path3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;
import org.jlab.geom.Transformable;
import org.jlab.vector.Vector3D;

/**
 *
 * @author J. Hankins
 */
public class DetectorPaddle implements Transformable {
    private final int component;
    private final Shape3D shape;
    private final Vector3D direction;
    private final Point3D midpoint;
    private final Plane3D plane;
    private final Line3D line;

    /**
     * Constructs a rectangular paddle centered on the origin with the direction
     * vector parallel with the y-axis, the midpoint at the origin, the plane on
     * the upstream face with the normal anti-parallel to the z-axis, and the 
     * line passing through the midpoints of the top and bottom faces.
     * @param component the paddle's component number
     * @param width the length along the x-axis
     * @param length the length along the y-axis
     * @param thickness the length along the z-axis
     */
    public DetectorPaddle(int component, double width, double length, double thickness)  {
        // Creat the "shape" as a as a verticle rectangle centerd arround
        // the origin
        this(component,
            new Point3D( width*0.5,  length*0.5,  thickness*0.5),
            new Point3D( width*0.5, -length*0.5,  thickness*0.5),
            new Point3D( width*0.5, -length*0.5, -thickness*0.5),
            new Point3D( width*0.5,  length*0.5, -thickness*0.5),
            new Point3D(-width*0.5,  length*0.5,  thickness*0.5),
            new Point3D(-width*0.5, -length*0.5,  thickness*0.5),
            new Point3D(-width*0.5, -length*0.5, -thickness*0.5),
            new Point3D(-width*0.5,  length*0.5, -thickness*0.5)
        );
    }
    
    /**
     * Constructs a paddle using the given points with the direction vector 
     * pointing from p2 to p1; the midpoint half way between point p1 and p7;
     * the plane such that its pont is half way between p3 and p8 and its
     * normal is parallel to the face defined by p3, p4, and p8; and the line 
     * such that it passes through the point half way between p1 and p8 and
     * through the point half way between p2 and p7.
     * @param component the paddle's component number
     * @param p0 left top downstream
     * @param p1 left bottom downstream
     * @param p2 left bottom upstream
     * @param p3 left top upstream
     * @param p4 right top downstream
     * @param p5 right bottom downstream
     * @param p6 right bottom upstream
     * @param p7 right top upstream
     */
    public DetectorPaddle(int component,
                Point3D p0,
                Point3D p1,
                Point3D p2,
                Point3D p3,
                Point3D p4,
                Point3D p5,
                Point3D p6,
                Point3D p7) {
        this.component = component;
        
        // Create the shape
        shape = new Shape3D();
        // perpendicular to z-axis faces
        shape.addFace(new Face3D(p2,p6,p3)); //  0 Front - upsteram face
        shape.addFace(new Face3D(p7,p3,p6)); //  1 
        shape.addFace(new Face3D(p0,p4,p1)); //  2 Back
        shape.addFace(new Face3D(p5,p1,p4)); //  3 
        // perpendicular to x-axis faces
        shape.addFace(new Face3D(p1,p2,p0)); //  4 Left - relative to the front face and direction vector
        shape.addFace(new Face3D(p3,p0,p2)); //  5 
        shape.addFace(new Face3D(p5,p4,p6)); //  6 Right
        shape.addFace(new Face3D(p7,p6,p4)); //  7 
        // perpendicular to y-axis faces (Ends)
        shape.addFace(new Face3D(p0,p3,p4)); //  8 Up - in the direction of the direction vector
        shape.addFace(new Face3D(p7,p4,p3)); //  9 
        shape.addFace(new Face3D(p1,p5,p2)); // 10 Down
        shape.addFace(new Face3D(p6,p2,p5)); // 11 
        // direction
        direction = p0.getVector(p1);
        direction.unit();
        // midpoint
        midpoint = new Point3D();
        midpoint.copy(p0);
        midpoint.combine(p6);
        // plane
        Point3D planePt = new Point3D();
        planePt.copy(p2);
        planePt.combine(p7);
        Vector3D planeVec = p7.getVector(p2).cross(p3.getVector(p2));
        plane = new Plane3D(planePt, planeVec);
        // line
        Point3D linePt1 = new Point3D();
        linePt1.copy(p0);
        linePt1.combine(p7);
        Point3D linePt2 = new Point3D();
        linePt2.copy(p1);
        linePt2.combine(p6);
        line = new Line3D(linePt1, linePt2);
    }
    
    public int getComponent() {
        return component;
    }
    
    public Shape3D getShape() {
        return shape;
    }
    public Vector3D getDirection() {
        return direction;
    }
    public Point3D getMidpoint() {
        return midpoint;
    }
    public Plane3D getPlane() {
        return plane;
    }
    public Line3D getLine() {
        return line;
    }
    public double getLength() {
        return line.length();
    }
    
    /**
     * Returns a copy of the point with the given index .
     * @param p the index of the point (in the range [0, 7] )
     * @return a copy of the point, or null if the index was not valid
     * @see #DetectorPaddle(int,Point3D,Point3D,Point3D,Point3D,Point3D,Point3D,Point3D,Point3D) 
     */
    public Point3D getPoint(int p) {
        Point3D pt = new Point3D();
        switch (p) {
            case 0: pt.copy(shape.face(2).point(0)); return pt;
            case 1: pt.copy(shape.face(2).point(2)); return pt;
            case 2: pt.copy(shape.face(0).point(0)); return pt;
            case 3: pt.copy(shape.face(0).point(2)); return pt;
            case 4: pt.copy(shape.face(2).point(1)); return pt;
            case 5: pt.copy(shape.face(3).point(0)); return pt;
            case 6: pt.copy(shape.face(1).point(2)); return pt;
            case 7: pt.copy(shape.face(1).point(0)); return pt;
        }
        return null; 
    }
    
    public ArrayList<Point3D> getAllPoints() {
        ArrayList<Point3D> pts = new ArrayList();
        for (int i=0; i<8; i++)
            pts.add(getPoint(i));
        return pts;
    }
    
    public Line3D getEdge(int e) {
        switch (e) {
            case 0:  return new Line3D(getPoint(6), getPoint(7));
            case 1:  return new Line3D(getPoint(2), getPoint(3));
            case 2:  return new Line3D(getPoint(1), getPoint(0));
            case 3:  return new Line3D(getPoint(5), getPoint(4));
            case 4:  return new Line3D(getPoint(7), getPoint(3));
            case 5:  return new Line3D(getPoint(3), getPoint(0));
            case 6:  return new Line3D(getPoint(0), getPoint(4));
            case 7:  return new Line3D(getPoint(4), getPoint(7));
            case 8:  return new Line3D(getPoint(6), getPoint(2));
            case 9:  return new Line3D(getPoint(2), getPoint(1));
            case 10: return new Line3D(getPoint(1), getPoint(5));
            case 11: return new Line3D(getPoint(5), getPoint(6));
        }
        return null;
    }
    
    public ArrayList<Line3D> getAllEdges() {
        ArrayList<Line3D> edges = new ArrayList();
        for (int i=0; i<12; i++)
            edges.add(getEdge(i));
        return edges;
    }
    
    public Path3D getDrawable(double phi) {
        // Create the plane that will be used to find the crossection
        Plane3D corssPlane = new Plane3D(0, 0, 0, 0, 1, 0);
        corssPlane.rotateZ(phi);
        
        // Find the points where the edges of the paddle intesect the plane,
        // then transform the the points into the plane's coordinate sytem and
        // and the points to the path
        Path3D path = new Path3D();
        for(int e=0; e<4; e++) {
            Point3D pt = new Point3D();
            if (Plane3D.intersection(corssPlane, getEdge(e), pt) == 1) {
                pt.rotateZ(-phi);
                pt.set(pt.z(), pt.x(), 0);
                path.addPoint(pt);
            }
        }
        
        // Return the path;
        return path;
    }
    
    public boolean intersectIgnoreEnds(Line3D line, Point3D intersectPt1, Point3D intersectPt2) {
        // Note: face#:
        // 0 & 1 - front
        // 2 & 3 - back
        // 4 & 5 - left
        // 6 & 7 - right
        // 8, 9, 10 & 11 - end caps
        
        // Check ony faces 0-7 because we are not interesetd in intersections
        // with the end caps:
        int i = 0;
        for( ; i<8; ++i) {
            if(Face3D.intersection(shape.face(i), line, intersectPt1)) {
                i++;
                // Each side has two faces, but its not possible to hit the
                // same side twice so skip the face on the same side if it
                // hasn't been checked yet.
                if(i%2==0) i++;
                // Found the first hit
                break;
            }
        }
        for( ; i<8; ++i) {
            // Assert: to enter this loop, i must be less than 8, which means
            // that we found atleast one intersection in the first loop
            if(Face3D.intersection(shape.face(i), line, intersectPt2)) {
                // We've found both intersections, so return true.
                return true;
            }
        }
        // Assert: to reach this line of code we have not found both
        // intersections
        return false;
    }
    public boolean intersectFront(Line3D line, Point3D intersectPt) {
        return Face3D.intersection(shape.face(0), line, intersectPt) ||
               Face3D.intersection(shape.face(1), line, intersectPt);
    }
    public boolean intersectAll(Line3D line, Point3D intersectPt1, Point3D intersectPt2) {
        // Note: face#:
        // 0 & 1 - front
        // 2 & 3 - back
        // 4 & 5 - left
        // 6 & 7 - right
        // 8, 9, 10 & 11 - end caps
        
        int i = 0;
        for( ; i<12; ++i) {
            if(Face3D.intersection(shape.face(i), line, intersectPt1)) {
                i++;
                // Each side has two faces, but its not possible to hit the
                // same side twice so skip the face on the same side if it
                // hasn't been checked yet.
                if(i%2==0) i++;
                // Found the first hit
                break;
            }
        }
        for( ; i<12; ++i) {
            // Assert: to enter this loop, i must be less than 8, which means
            // that we found atleast one intersection in the first loop
            if(Face3D.intersection(shape.face(i), line, intersectPt2)) {
                // We've found both intersections, so return true.
                return true;
            }
        }
        // Assert: to reach this line of code we have not found both
        // intersections
        return false;
    }
    
    @Override
    public void translateXYZ(double dx, double dy, double dz) {
        shape.translateXYZ(dx, dy, dz);
        midpoint.translateXYZ(dx, dy, dz);
        plane.translateXYZ(dx, dy, dz);
        line.translateXYZ(dx, dy, dz);
    }
    @Override
    public void rotateX(double angle) {
        shape.rotateX(angle);
        direction.rotateX(angle);
        midpoint.rotateX(angle);
        plane.rotateX(angle);
        line.rotateX(angle);
    }
    @Override
    public void rotateY(double angle) {
        shape.rotateY(angle);
        direction.rotateY(angle);
        midpoint.rotateY(angle);
        plane.rotateY(angle);
        line.rotateY(angle);
    }
    @Override
    public void rotateZ(double angle) {
        shape.rotateZ(angle);
        direction.rotateZ(angle);
        midpoint.rotateZ(angle);
        plane.rotateZ(angle);
        line.rotateZ(angle);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DetectorPaddle::\n");
        builder.append("   ").append(direction.toString());
        builder.append("   ").append(midpoint.toString());
        builder.append("   ").append(plane.toString());
        builder.append("   ").append(line.toString());
        for(int i=0; i<12; ++i) {
            builder.append(String.format("   Face %2d: ",i));
            Point3D p1 = shape.face(i).point(0);
            Point3D p2 = shape.face(i).point(1);
            Point3D p3 = shape.face(i).point(2);
            builder.append(String.format("(%7.3f,%7.3f,%7.3f) ",p1.x(),p1.y(),p1.z()));
            builder.append(String.format("(%7.3f,%7.3f,%7.3f) ",p2.x(),p2.y(),p2.z()));
            builder.append(String.format("(%7.3f,%7.3f,%7.3f)\n",p3.x(),p3.y(),p3.z()));
        }
        return builder.toString();
    }
}
