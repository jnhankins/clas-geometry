/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom;

import org.jlab.vector.Vector3D;

/**
 * 
 * @author gavalian
 */
public class Face3D implements Transformable {
    private final Point3D[]  points ;//= new Point3D[3];
    
    /**
     * Default Constructor.
     */
    public Face3D(){
        points = new Point3D[3];
        points[0] = new Point3D();
        points[1] = new Point3D();
        points[2] = new Point3D();
        this.set(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    /**
     * Constructor for initializing the face.
     * @param x1 x component of the first point
     * @param y1 y component of the first point
     * @param z1 z component of the first point
     * @param x2 x component of the second point
     * @param y2 y component of the second point
     * @param z2 z component of the second point
     * @param x3 x component of the third point
     * @param y3 y component of the third point
     * @param z3 z component of the third point
     */
    public Face3D(double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3){
        points = new Point3D[3];
        points[0] = new Point3D();
        points[1] = new Point3D();
        points[2] = new Point3D();
        this.set(x1,y1,z1,x2,y2,z2,x3,y3,z3);
    }
    /**
     * Initialize the face from points. the values of the
     * point are copied to the internal array. so changing
     * original points will not affect the face.
     * @param p1 first base point
     * @param p2 second base point
     * @param p3 third base point
     */
    public Face3D(Point3D p1, Point3D p2, Point3D p3){
        points = new Point3D[3];
        points[0] = new Point3D();
        points[1] = new Point3D();
        points[2] = new Point3D();
        this.set(
                p1.x(), p1.y(), p1.z(),
                p2.x(), p2.y(), p2.z(),
                p3.x(), p3.y(), p3.z()
        );
    }
    
    /**
     * Method for setting all point for the face
     * @param x1 x component of the first point
     * @param y1 y component of the first point
     * @param z1 z component of the first point
     * @param x2 x component of the second point
     * @param y2 y component of the second point
     * @param z2 z component of the second point
     * @param x3 x component of the third point
     * @param y3 y component of the third point
     * @param z3 z component of the third point
     */
    public final void set(double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3){
        points[0].set(x1, y1, z1);
        points[1].set(x2, y2, z2);        
        points[2].set(x3, y3, z3);
    }
    
    @Override
    public void translateXYZ(double x, double y, double z){
        points[0].translateXYZ(x, y, z);
        points[1].translateXYZ(x, y, z);
        points[2].translateXYZ(x, y, z);        
    }
    /**
     * Rotate the shape around X axis. All points are rotated.
     * @param angle rotation angle in radians
     */
    @Override
    public void rotateX(double angle){
        points[0].rotateX(angle);
        points[1].rotateX(angle);
        points[2].rotateX(angle);
    }
    /**
     * Rotate the shape around Y axis. All points are rotated.
     * @param angle rotation angle in radians
     */
    @Override
    public void rotateY(double angle){
        points[0].rotateY(angle);
        points[1].rotateY(angle);
        points[2].rotateY(angle);
    }
    /**
     * Rotate the shape around Z axis. All points are rotated.
     * @param angle rotation angle in radians
     */
    @Override
    public void rotateZ(double angle){
        points[0].rotateZ(angle);
        points[1].rotateZ(angle);
        points[2].rotateZ(angle);
    }    
    
    /**
     * Returns the point on the face, index 0-2 should be used
     * @param index index of the point
     * @return Point3D object
     */
    public Point3D point(int index){
        if(index<0||index>2){
            System.err.println("ERROR: Face3D requested point = " + index);
            return null;
        }
        return points[index];
    }
    
    /**
     * Calculates the middle point of the face.
     * @return Point3D object of the center.
     */
    public Point3D center(){
        
        Point3D centerpoint = new Point3D(points[0].x(),points[1].y(),points[0].z());
        double cX = 0.0;
        double cY = 0.0;
        double cZ = 0.0;
        for(int loop = 0; loop < 3; loop++){
            cX += points[loop].x();
            cY += points[loop].y();
            cZ += points[loop].z();
            //centerpoint.combine(points[loop]);
        }
        centerpoint.set(cX/3.0, cY/3.0, cZ/3.0);
        return centerpoint;
    }
    
    /**
     * Calculates if the line intersects with a face. if intersection of the line
     * is found with the plane that represents the face, the point is checked for 
     * the bounds of the triangle that makes up the Face3D object.
     * @param face face object with three points
     * @param line line object with two points
     * @param point contains the intersection point of the plane with line
     * @return true - if line intersects with plane and is within the bounds of the face, false - otherwise.
     */
    public static boolean intersection(Face3D face, Line3D line, Point3D point){
        Vector3D vec_a = Point3D.getDirVector(face.points[0], face.points[1]);
        Vector3D vec_b = Point3D.getDirVector(face.points[0], face.points[2]);
        Vector3D normal = vec_a.cross(vec_b);
        Plane3D plane = new Plane3D(
                face.point(0).x(),face.point(0).y(),face.point(0).z(),
                normal.x(),normal.y(),normal.z()
        );
        
        int status = Plane3D.intersection(plane, line, point);
        if(status==2||status==0) return false;
        
        Vector3D v0 = Point3D.getDirVector(face.point(0), face.point(2));
        Vector3D v1 = Point3D.getDirVector(face.point(0), face.point(1));
        Vector3D v2 = Point3D.getDirVector(face.point(0), point);
        
        double dot00 = v0.dot(v0);
        double dot01 = v0.dot(v1);
        double dot02 = v0.dot(v2);
        double dot11 = v1.dot(v1);
        double dot12 = v1.dot(v2);
        
        double invDenom = 1./(dot00*dot11 - dot01*dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
        //System.out.println(" u and v = " + u + " " + v);
        return (u>=0 && v>=0 &&( (u+v)<1));
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("Face : center (%12.5f %12.5f %12.5f)\n",
                this.center().x(),this.center().y(),this.center().z()));
        for(int loop = 0; loop < 3; loop++){
            str.append(String.format("\tp:\t %12.5f %12.5f %12.5f\n", 
                    points[loop].x(),
                    points[loop].y(),
                    points[loop].z())
                    );
        }
        return str.toString();
    }
}
