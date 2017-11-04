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
public class Line3D implements Transformable {
    private static double SMALL_NUM = 0.00000001;
    Point3D startPoint;
    Point3D endPoint;
    
    /**
     * Default constructor create a line with two points at the origin
     * with length = 0
     */
    public Line3D()
    {
        startPoint = new Point3D();
        endPoint   = new Point3D();
    }
    /**
     * Create a line with given end points.
     * @param point_start start point
     * @param point_end end point
     */
    public Line3D(Point3D point_start, Point3D point_end)
    {
        startPoint = new Point3D();
        endPoint   = new Point3D();
        startPoint.copy(point_start);
        endPoint.copy(point_end);
    }
    /**
     * Create a line with given points by coordinates.
     * @param x1 x component of the start point
     * @param y1 y component of the start point
     * @param z1 z component of the start point
     * @param x2 x component of the end point
     * @param y2 y component of the end point
     * @param z2 z component of the end point
     */
    public Line3D(double x1, double y1, double z1,
            double x2, double y2, double z2)
            
    {
        startPoint = new Point3D();
        endPoint   = new Point3D();
        startPoint.set(x1, y1, z1);
        endPoint.set(x2, y2, z2);
    }
    /**
     * Set the origin point coordinates
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public void setOrigin(double x, double y, double z)
    {
        startPoint.set(x, y, z);
    }
    /**
     * Set the end point of the line
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public void setEnd(double x, double y, double z)
    {
        endPoint.set(x, y, z);
    }
    /**
     * Sets the line origin and end point to given points.
     * @param p1 origin point
     * @param p2 end point
     */
    public void set(Point3D p1, Point3D p2){
        startPoint.set(p1.x(), p1.y(), p1.z());
        endPoint.set(p2.x(), p2.y(), p2.z());        
    }
    
    /**
     * Get the origin point of the line
     * @return Point3D object
     */
    public Point3D origin() {
        return startPoint;
    }
    /**
     * Get the end point of the line
     * @return Point3D object
     */
    public Point3D end() {
        return endPoint;
    }
    /**
     * Calculates the distance between the end point and
     * start point.
     * @return length
     */
    public double length()
    {
        return startPoint.distance(endPoint);
    }
    
    /**
     * Returns a Vector3D object from the line
     * @param p1 origin point
     * @param p2 end point
     * @return Vector3D object describing line
     */
    Vector3D getVector(Point3D p1, Point3D p2)
    {
        Vector3D vec = new Vector3D(
                p2.x()-p1.x(),
                p2.y()-p1.y(),
                p2.z()-p1.z());
        return vec;
    }
    Vector3D getVectorSub(double f1, Vector3D v1, double f2,Vector3D v2)
    {
        Vector3D vec = new Vector3D();
        vec.setXYZ( 
                f1*v1.x()-f2*v2.x(),
                f1*v1.y()-f2*v2.y(),
                f1*v1.z()-f2*v2.z()
                );
        return vec;
    }
    
    /**
     * Returns the middle point of the line 
     * @param wgt1
     * @param wgt2
     * @return 
     */
    public Point3D middleWighted(double wgt1, double wgt2)
    {
        double relative = wgt1/(wgt1+wgt2);
        return new Point3D(
                (startPoint.x()+endPoint.x())*relative,
                (startPoint.y()+endPoint.y())*relative,
                (startPoint.z()+endPoint.z())*relative);
        //double factor = 0.0;
        //return factor;
    }
    public Point3D middle()
    {
        return new Point3D(
                (startPoint.x()+endPoint.x())*0.5,
                (startPoint.y()+endPoint.y())*0.5,
                (startPoint.z()+endPoint.z())*0.5);
    }
    
    /**
     * Calculates the closest approach between this line and the reference line.
     * @param line reference line
     * @return Line3D object connecting points of closest approach of two lines.
     */
    public Line3D distance(Line3D line)
    {
        Vector3D Diff = getVector(line.origin(),this.origin());
        Vector3D p1   = getVector(this.end(),this.origin());
        Vector3D p2   = getVector(line.end(),line.origin());
        Vector3D v1   = new Vector3D(this.origin().x(),this.origin().y(),this.origin().z());
        Vector3D v2   = new Vector3D(line.origin().x(),line.origin().y(),line.origin().z());
        
        p1.unit();
        p2.unit();
        
        double R = p1.dot(p2);
        
        //if(R-1.0<1e-6) R = 1.0;
        if(R>1.0)
        {
            System.out.println("----> Line3D. error in distance(). R dot is larger that 1.0 R="
                    + R + " normals = " + p1.mag() + " " + p2.mag());
        }
        
        double R1 = 1 / ( 1- R*R );
        Vector3D rp21 = getVectorSub(R,p2,1.0,p1);
        Vector3D rp12 = getVectorSub(1.0,p2,R,p1);
        
        double      dot21 = Diff.dot(rp21);
        double      dot12 = Diff.dot(rp12);
        
        p1.scale(R1*dot21);
        p2.scale(R1*dot12);
        
        Vector3D  M1  = new Vector3D();
        Vector3D  M2  = new Vector3D();
        
        M1.add(v1);
        M1.add(p1);
        M2.add(v2);
        M2.add(p2);
        
        return (new Line3D(M1.x(),M1.y(),M1.z(),M2.x(),M2.y(),M2.z()));
    }
    /**
     * Calculates the closest approach between two lines. Returns a Line3D
     * object connecting the points of closest approach on two reference lines.
     * To get the distance between two lines use Line3D.length(). To get the
     * midpoint of the closest approach use Line3D.middle().
     * @param l1 first reference line
     * @param l2 second reference line
     * @return Line3D object connecting the points of closest approach on lines.
     */
    public static Line3D distance(Line3D l1, Line3D l2){
        Vector3D u = Point3D.getDirVector(l1.origin(), l1.end());
        Vector3D v = Point3D.getDirVector(l2.origin(), l2.end());
        Vector3D w = Point3D.getDirVector(l2.origin(), l1.origin());
        
        double a = u.dot(u);
        double b = u.dot(v);
        double c = v.dot(v);
        double d = u.dot(w);
        double e = v.dot(w);
        double D = a*c - b*b;
        double sc, tc;
        if(D<SMALL_NUM){
            sc = 0.0;
            tc = (b>c ? d/b : e/c);
        } else {
            sc = (b*e - c*d) / D;
            tc = (a*e - b*d) / D;
        }
        
        return new Line3D( 
                tc*v.x(), tc*v.y(), tc*v.z(),
                w.x() + (sc*u.x()),
                w.x() + (sc*u.x()),
                w.x() + (sc*u.x())
        );
    }
    
    @Override
    public void translateXYZ(double x, double y, double z){
        this.startPoint.translateXYZ(x, y, z);
        this.endPoint.translateXYZ(x, y, z);
    }
    /**
     * Rotate the point around X axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateX(double angle){
        this.startPoint.rotateX(angle);
        this.endPoint.rotateX(angle);
    }
    /**
     * Rotate the point around Y axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateY(double angle){
       this.startPoint.rotateY(angle);
        this.endPoint.rotateY(angle);
    }
    /**
     * Rotate the point around Z axis for given angle.
     * @param angle - rotation angle in radians
     */
    @Override
    public void rotateZ(double angle){
        this.startPoint.rotateZ(angle);
        this.endPoint.rotateZ(angle);
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Line3D:\n");
        str.append("\t");
        str.append(this.origin().toString());
        str.append("\t");
        str.append(this.end().toString());
        return str.toString();
    }
}
