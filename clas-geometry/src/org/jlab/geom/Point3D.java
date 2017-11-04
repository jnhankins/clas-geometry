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
public class Point3D implements Transformable {
    double fX;
    double fY;
    double fZ;
    
    /**
     * Constructor to create a point at the origin (0.,0.,0.)
     */
    public Point3D()
    {
        set(0.,0.,0.);
    }
    /**
     * Constructor to create a point with given coordinates. 
     * @param x - x cartesian coordinate 
     * @param y - y cartesian coordinate
     * @param z - z cartesian coordinate
     */
    public Point3D(double x, double y, double z)
    {
        set(x,y,z);
    }
    
    /**
     * Set the point to given coordinates.
     * @param x - x cartesian coordinate 
     * @param y - y cartesian coordinate 
     * @param z - z cartesian coordinate 
     */
    public final void set(double x, double y, double z)
    {
        fX = x;
        fY = y;
        fZ = z;
    }
    
    /**
     * Copy content of given point into this class.
     * @param point - reference point
     */
    public void copy(Point3D point)
    {
        fX = point.x();
        fY = point.y();
        fZ = point.z();
    }
    
    /**
     * Return the X-component of the point.
     * @return X - x cartesian coordinate
     */
    public double x(){ return fX;}
    /**
     * Return the Y-component of the point.
     * @return Y - y cartesian coordinate
     */
    public double y(){ return fY;}
    /**
     * Return the Z-component of the point.
     * @return Z - z cartesian coordinate
     */
    public double z(){ return fZ;}
    
    /**
     * Return the distance between the points
     * @param b - the reference point.
     * @return distance between the reference point and current point.
     */
    public double distance(Point3D b)
    {
        return Math.sqrt( (b.x()-fX)*(b.x()-fX) +
                (b.y()-fY)*(b.y()-fY) +
                (b.z()-fZ)*(b.z()-fZ));
    }
    /**
     * Returns the distance from this point to the point
     * given by coordinates in the argument.
     * @param x x component
     * @param y y component
     * @param z z component
     * @return distance from this point to point(x,y,z)
     */
    public double distance(double x, double y, double z){
        return Math.sqrt( (x-fX)*(x-fX) +
                (y-fY)*(y-fY) +
                (z-fZ)*(z-fZ));
    }
    
    @Override
    public void translateXYZ(double x, double y, double z){
        this.fX += x;
        this.fY += y;
        this.fZ += z;
    }
    /**
     * Rotate the point around X axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateX(double angle){
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double yy = fY;
        fY = c*yy - s*fZ;
        fZ = s*yy + c*fZ;
    }
    /**
     * Rotate the point around Y axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateY(double angle){
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double zz = fZ;
        fZ = c*zz - s*fX;
        fX = s*zz + c*fX;
    }
    /**
     * Rotate the point around Z axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateZ(double angle){
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double xx = fX;
        fX = c*xx - s*fY;
        fY = s*xx + c*fY;
    }
    
    /**
     * Returns a vector with direction from given point to the this point.
     * equivalent of P = this - point(x,y,z).
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return Vector3D object, direction vector.
     */
    public Vector3D getVector(double x, double y, double z){
        return new Vector3D(this.fX - x, this.fY - y, this.fZ - z);
    }
    /**
     * Returns a vector with direction from given point to the this point.
     * equivalent of P = this - point.
     * @param point Point3D object
     * @return Vector3D object, direction vector.
     */
    public Vector3D getVector(Point3D point){
        return this.getVector(point.x(), point.y(), point.z());
    }
    /**
     * returns direction vector from point p1 to point p2.
     * @param p1 origin point
     * @param p2 end point
     * @return Vector3D object, direction vector.
     */
    public static Vector3D getDirVector(Point3D p1, Point3D p2){
        return new Vector3D(p2.x()-p1.x(), p2.y()-p1.y(),p2.z()-p1.z());
    }
    
    /**
     * Combines current point with a given point. The current position of the 
     * point is changed to the geometric mean of two points. The point in the
     * argument is unchanged.
     * @param point Point3D point to calculate geometric mean.
     */
    public void combine(Point3D point){
        this.fX = (this.fX + point.x())*0.5;
        this.fY = (this.fY + point.y())*0.5;
        this.fZ = (this.fZ + point.z())*0.5;
    }
    
    public String toJVXString(){
        StringBuilder str = new StringBuilder();
        str.append("<p>");
        str.append(String.format("%12.5f", fX));
        str.append(String.format("%12.5f", fY));
        str.append(String.format("%12.5f", fZ));
        str.append("</p>");
        return str.toString();
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Point3D :\t");
        str.append(String.format("%12.5f %12.5f %12.5f\n", 
                fX,fY,fZ));
        return str.toString();
    }
}
