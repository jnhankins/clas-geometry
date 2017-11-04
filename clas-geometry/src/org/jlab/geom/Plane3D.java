/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom;

import org.jlab.vector.Vector3D;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix3d;

/**
 *
 * @author gavalian
 */
public class Plane3D implements Transformable {
    private static double SMALL_NUM = 0.00000001;
    private final Vector3D planePoint  = new Vector3D();
    private final Vector3D planeNormal = new Vector3D();
    
    public Plane3D(Point3D p, Vector3D n){
        this(p.x(), p.y(), p.z(), n.x(), n.y(), n.z());
    }
    public Plane3D(double xp, double yp, double zp, 
            double xn, double yn, double zn){
        planePoint.setXYZ(xp, yp, zp);
        planeNormal.setXYZ(xn, yn, zn);
        planeNormal.unit();
    }
    
    /**
     * Returns the normal to the plane.
     * @return Vector3D 
     */
    public Vector3D normal(){
        return planeNormal;
    }
    /**
     * Returns the reference point to the plane.
     * @return Vector3D object that is the middle point of the plane
     */
    public Vector3D point(){
        return planePoint;
    }    
    
    /**
     * Returns the intersection point between three planes.
     * @param pa first plane
     * @param pb second plane
     * @param pc third plane
     * @return Point3D object with coordinates of intersection
     */
    public static Point3D intersection(Plane3D pa, Plane3D pb, Plane3D pc){
        if (pa == null || pb == null || pc == null)
        	return null;

        Vector3D na = pa.normal();
        Vector3D nb = pb.normal();
        Vector3D nc = pc.normal();

        Matrix4d m = new Matrix4d(
            na.x(), na.y(), na.z(), 0,
            nb.x(), nb.y(), nb.z(), 0,
            nc.x(), nc.y(), nc.z(), 0,
            0, 0, 0, 1
        );

        Matrix4d mInverse = m;
        mInverse.invert();
        
        double x = pa.normal().dot(pa.point());
        double y = pb.normal().dot(pb.point());
        double z = pc.normal().dot(pc.point());
       
        return new Point3D(
                (mInverse.m00 * x) + (mInverse.m01 * y) + (mInverse.m02 * z),
                ( (mInverse.m10 * x) + (mInverse.m11 * y) + (mInverse.m12 * z) ),
                (mInverse.m20 * x) + (mInverse.m21 * y) + (mInverse.m22 * z));  
    }
    /**
     * returns a line that is the intersection of two planes.
     * @param pa first plane
     * @param pb second plane
     * @return Line3D object 
     */
    public static Line3D intersection(Plane3D pa, Plane3D pb){
         if (pa == null || pb == null)
        	return null;

        Vector3D na = pa.normal();
        Vector3D nb = pb.normal();

        Matrix3d m = new Matrix3d(
                na.x(), na.y(),0,
                nb.x(), nb.y(), 0,
                0, 0, 1
        );
        Matrix3d mInverse = m;
        mInverse.invert();
        
        double x = pa.normal().dot(pa.point());
        double y = pb.normal().dot(pb.point());
        
        Vector3D ref = new Vector3D(
                (mInverse.m00 * x) + (mInverse.m01 * y) ,
                (mInverse.m10 * x) + (mInverse.m11 * y) ,
                (mInverse.m20 * x) + (mInverse.m21 * y) );
        
        Vector3D dir = na.cross(nb);
        //dir.multi(1./dir.len());
        dir.unit();
        Line3D line = new Line3D();
        line.setOrigin(ref.x(), ref.y(), ref.z());
        line.setEnd(ref.x() + dir.x(), ref.y() + dir.y(), ref.z() + dir.z());
        return line;
    }
    /**
     * Returns the intersection line between this plane and the reference plane
     * @param plane reference plane
     * @return Line3D object (the intersection)
     */
    public Line3D intersection(Plane3D plane){        
        Line3D line = new Line3D();
        if(plane==null) return null;
        Matrix3d m = new Matrix3d(
                this.normal().x(),   this.normal().y(),   0,
                plane.normal().x(),  plane.normal().y(),  0,
                0, 0, 1);
        
        Matrix3d mInverse = m;
        mInverse.invert();
        
        double x = this.normal().dot(this.point());
        double y = plane.normal().dot(plane.point());
        
        Vector3D ref = new Vector3D( 
                (mInverse.m00 * x) + (mInverse.m01 * y) ,
                (mInverse.m10 * x) + (mInverse.m11 * y) ,
                (mInverse.m20 * x) + (mInverse.m21 * y));
        
        Vector3D dir = this.normal().cross(plane.normal());
        //dir.multi(1./dir.len());
        dir.unit();
        //return new Line(ref,dir);
        line.setOrigin(ref.x(), ref.y(), ref.z());
        line.setEnd(ref.x() + dir.x(), ref.y() + dir.y(), ref.z() + dir.z());
        return line;
    }
    /**
     * calculates the intersection point of a line with a plane. 
     * @param plane Plane3D object
     * @param line Line3D object
     * @param inter_p calculated intersection point.
     * @return 0 = disjoint (no intersection)
     * 1 =  intersection in the unique point inter_p
     * 2 = the  segment lies in the plane
     */
    public static int intersection(Plane3D plane, Line3D line, Point3D inter_p){
        Point3D pnV0 = new Point3D(plane.point().x(),
                plane.point().y(),plane.point().z());
        
        Vector3D u = Point3D.getDirVector(line.origin(),line.end());
        Vector3D w = Point3D.getDirVector(pnV0, line.origin());
        
        double D =  plane.normal().dot(u);
        double N = -plane.normal().dot(w);
        
        //System.out.println("u-v : \n " + u.toString() + w.toString());
        //System.out.println(" D = " + D + " N = " + N);
        
        if(Math.abs(D)<SMALL_NUM){
            if(N==0){
                return 2;
            } else {
                return 0;
            }
        }
        //System.out.println("-------> made it here");
        double sI = N/D;
        if (sI < 0 || sI > 1)
            return 0;
        inter_p.set(  
                line.origin().x() + sI*u.x(), 
                line.origin().y() + sI*u.y(), 
                line.origin().z() + sI*u.z()
        );        
        return 1;
    }
    
    /**
     * Returns the distance from the given point to the reference point
     * of the plane.
     * @param p point in space
     * @return distance from p to reference point.
     */
    public double distance(Point3D p){
        return p.distance(planePoint.x(), planePoint.y(),
                planePoint.z());
    }    
    /**
     * Calculates is given point is in a circle with radius "radius"
     * with the point of reference of the plane.
     * @param x x component of the point
     * @param y y component of the point
     * @param z z component of the point
     * @param radius of the circle to check
     * @return true - if the point is in the circle, false - otherwise
     */
    public boolean inCircle(double x, double y, double z, double radius){
        double distance = Math.sqrt(
                (x-planePoint.x())*(x-planePoint.x())
                +(y-planePoint.y())*(y-planePoint.y())
                +(z-planePoint.z())*(z-planePoint.z())
        );
        return (distance<=radius);
    }
    /**
     * Calculates is given point is in a circle with radius "radius"
     * with the point of reference of the plane.
     * @param p point to be checked 
     * @param radius of the circle to check
     * @return true - if the point is in the circle, false - otherwise
     */
    public boolean inCircle(Point3D p, double radius){
        return this.inCircle(p.x(), p.y(), p.z(), radius);
    }
    
    @Override
    public void translateXYZ(double x, double y, double z){
        planePoint.setXYZ(planePoint.x()+x,
                planePoint.y()+y, 
                planePoint.z()+z);
    }
    /**
     * Rotate the point around X axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateX(double angle){
        planePoint.rotateX(angle);
        planeNormal.rotateX(angle);
    }
    /**
     * Rotate the point around Y axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateY(double angle){
        planePoint.rotateY(angle);
        planeNormal.rotateY(angle);
    }
    /**
     * Rotate the point around Z axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateZ(double angle){
        planePoint.rotateZ(angle);
        planeNormal.rotateZ(angle);
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Plane3D:\n");
        str.append(String.format("\t point  : %12.5f %12.5f %12.5f\n", 
                this.planePoint.x(),planePoint.y(),planePoint.z()));
        str.append(String.format("\t normal : %12.5f %12.5f %12.5f\n",
                planeNormal.x(),planeNormal.y(),planeNormal.z()));
        return str.toString();                
    }
}
