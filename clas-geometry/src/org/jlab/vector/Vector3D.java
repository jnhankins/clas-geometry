/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.vector;

/**
 *
 * @author gavalian
 */
public class Vector3D {
    double fX;
    double fY;
    double fZ;
    /**
     * Default Constructor for Vector3D class with zero length
     */
    public Vector3D()
    {
        fX = 0.0;
        fY = 0.0;
        fZ = 0.0;
    }
    
    public Vector3D(double x, double y, double z){
        fX = x;
        fY = y;
        fZ = z;
    }
    
    public void copy(Vector3D vec) {
        fX = vec.fX;
        fY = vec.fY;
        fZ = vec.fZ;
    }
    
    public double x() { return fX;}
    public double y() { return fY;}
    public double z() { return fZ;}
    
    public void   scale(double factor)
    {
        fX = factor*fX;
        fY = factor*fY;
        fZ = factor*fZ;
    }
    
    public void setXYZ(double x, double y, double z)
    {
        fX = x;
        fY = y;
        fZ = z;  
    }
    
    public void setMagThetaPhi(double mag, double theta , double phi)
    {
        double amag = Math.abs(mag);
        fX = amag * Math.sin(theta) * Math.cos(phi);
        fY = amag * Math.sin(theta) * Math.sin(phi);
        fZ = amag * Math.cos(theta);
    }
    
    public double mag2()
    {
        return (fX*fX+fY*fY+fZ*fZ);
    }
    
    public double mag()
    {
        return Math.sqrt(this.mag2());
    }
    
    public double rho()
    {
        return Math.sqrt(fX*fX + fY*fY);
    }
    
    public double theta()
    {
        return Math.acos(fZ/this.mag());
    }
    
    public double phi()
    {
        return Math.atan2(fY, fX);
    }
    
    public void add(Vector3D vector)
    {
        fX = fX + vector.x();
        fY = fY + vector.y();
        fZ = fZ + vector.z();
    }
    
    public void negative()
    {
        fX = -fX;
        fY = -fY;
        fZ = -fZ;
    }
    
    public void sub(Vector3D vector)
    {
        fX = fX - vector.x();
        fY = fY - vector.y();
        fZ = fZ - vector.z();
    }
    
    /**
     * Rotate the point around X axis for given angle.
     * @param angle - rotation angle in radians
     */
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
    public void rotateZ(double angle){
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double xx = fX;
        fX = c*xx - s*fY;
        fY = s*xx + c*fY;
    }
    
    public double compare(Vector3D vect)
    {
        double quality = 0.0;
        quality += Math.abs(fX-vect.x())/Math.abs(fX);
        quality += Math.abs(fY-vect.y())/Math.abs(fY);
        quality += Math.abs(fZ-vect.z())/Math.abs(fZ);
        return quality;
    }
    
    public double dot(Vector3D vect)
    {
        return fX*vect.x()+fY*vect.y()+fZ*vect.z();
    }
    
    public Vector3D cross(Vector3D vect)
    {
        Vector3D vprod = new Vector3D();
        vprod.setXYZ(fY*vect.z()-fZ*vect.y(), 
                fZ*vect.x()-fX*vect.z(), fX*vect.y()-fY*vect.x());
        return vprod;
    }
    
    public void unit()
    {        
        if(this.mag()!=0)
        {
            double factor = 1.0/this.mag();
            fX = fX*factor;
            fY = fY*factor;
            fZ = fZ*factor;
        }
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Vector3D :\t");
        str.append(String.format("%12.5f %12.5f %12.5f\n", 
                fX,fY,fZ));
        return str.toString();
    }
    
}
