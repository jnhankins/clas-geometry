/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom;

import java.util.ArrayList;
import org.jlab.vector.Vector3D;

/**
 * Class to present a path in 3D. Path consists of collection of Point3D objects.
 * 
 * @author gavalian
 */
public class Path3D implements Transformable {
    private final ArrayList<Point3D> pathPoints = new ArrayList<>();
    
    /**
     * default constructor to create an empty path.
     */
    public Path3D(){
        
    }
    
    /**
     * Add a point to the path with given coordinates (x,y,z)
     * @param x component of the point
     * @param y y component of the point
     * @param z z component of the point
     */
    public void addPoint(double x, double y, double z){
        pathPoints.add(new Point3D(x,y,z));
    }
    /**
     * Add given point to the path. The content of the point is copied, so changing
     * the point after this call will no affect the path.
     * @param p point to be added to the path.
     */
    public void addPoint(Point3D p){
        this.addPoint(p.x(), p.y(), p.z());
    }
    
    /**
     * Returns the number of nodes in the path (number of points)
     * @return 
     */
    public int nodes(){
        return pathPoints.size();
    }
    /**
     * Returns a point corresponding to the node=index
     * @param index number of node to read
     * @return 
     */
    public Point3D getNode(int index){
        return pathPoints.get(index);
    }
    
    /**
     * Clears the path. The array of points will be emptied.
     */
    public void clear(){
        pathPoints.clear();
    }
    
    /**
     * Calculates the distance for the path up to the point given in the argument.
     * First the closest segment in the path to the given point. then the path is
     * calculated until the node that contains the segment of closest approach. and
     * the resulting path length is returned.
     * @param p Point3D object of a point in space.
     * @return  distance of the path up to the given point.
     */
    public double distance(Point3D p){
        double doca  = 1000.0;
        double index = 0;
        
        return 0.0;
    }
    /**
     * Calculates the length of the path up to the node = last node.
     * @param lastnode number of nodes to run over in calculation
     * @return length of the path from the origin to the - last node.
     */
    public double length(int lastnode){
        double len  = 0.0;
        int    alen = pathPoints.size();
        if(lastnode<alen){
            alen = lastnode;
        }        
        for(int loop = 0; loop < alen - 1; loop++){
            len += pathPoints.get(loop).distance(pathPoints.get(loop+1));
        }
        return len;
    }
    
    /**
     * Generates a path for at given origin (vx,vy,vz) for a vector (px,py,pz)
     * for given length with given number of points.
     * @param vx x coordinate of the origin
     * @param vy y coordinate of the origin
     * @param vz z coordinate of the origin
     * @param px direction vector x component
     * @param py direction vector y component
     * @param pz direction vector z component
     * @param length length of the path to generate
     * @param npoints number of nodes in the path
     */
    public void generate(double vx, double vy, double vz,
            double px, double py, double pz, double length, int npoints){
        this.clear();
        this.addPoint(vx,vy,vz);
        double increment = length/(npoints);
        Vector3D direction = new Vector3D(px,py,pz);
        direction.unit();
        for(int loop = 1; loop < npoints; loop++){
            this.addPoint(vx + loop * increment * direction.x(),
                    vy + loop * increment * direction.y(),
                    vz + loop * increment * direction.z());
        }
    }
    /**
     * Generates path in a straight line starting with given origin and with the
     * given direction.
     * @param origin vector of the origin point
     * @param direction direction vector
     * @param length path length
     * @param npoints number of nodes in the path
     */
    public void generate(Vector3D origin, Vector3D direction, double length,
            int npoints){
        this.generate(origin.x(),origin.y(),origin.z(),
                direction.x(),direction.y(),direction.z(), length, npoints);
    }
    /**
     * Generated a random path from given origin, and given ranges for
     * theta and phi angle, Angles are given in degrees.
     * @param vx - origin x coordinate
     * @param vy - origin y coordinate
     * @param vz - origin z coordinate
     * @param theta_min_deg - minimum theta angle in degrees
     * @param theta_max_deg - maximum theta angle in degrees
     * @param phi_min_deg   - minimum phi angles in degrees
     * @param phi_max_deg - maximum phi angle in degrees 
     * @param len - length of the path in cm
     * @param npoints  - number of points along the path
     */
    public void generateRandom(double vx, double vy, double vz, 
            double theta_min_deg, double theta_max_deg, double phi_min_deg, 
            double phi_max_deg,
            double len,
            int npoints){
        double mag   = 1.0;
        double theta = Math.random()*(theta_max_deg-theta_min_deg) 
                + theta_min_deg;
        double phi   = Math.random()*(phi_max_deg-phi_min_deg)+phi_min_deg;
        double theta_r = theta*Math.PI/180.0;
        double phi_r   = phi*Math.PI/180.0;
        double px    = mag*Math.sin(theta_r)*Math.cos(phi_r);
        double py    = mag*Math.sin(theta_r)*Math.sin(phi_r);
        double pz    = mag*Math.cos(theta_r);
        this.generate(vx, vy, vz, px, py, pz, len, npoints);
        //this.generate(origin.x(),origin.y(),origin.z(),
        //        direction.x(),direction.y(),direction.z(), length, npoints);
    }
    
    /**
     * Finds the closest node to the given point.
     * @param p point in space
     * @return index of the closest node.
     */
    public int closestNode(Point3D p){
        return 0;
    }
    
    
    @Override
    public void translateXYZ(double dx, double dy, double dz) {
        for(Point3D p : pathPoints)
            p.translateXYZ(dx, dy, dz);
    }
    @Override
    public void rotateX(double angle) {
        for(Point3D p : pathPoints)
            p.rotateX(angle);
    }
    @Override
    public void rotateY(double angle) {
        for(Point3D p : pathPoints)
            p.rotateY(angle);
    }
    @Override
    public void rotateZ(double angle) {
        for(Point3D p : pathPoints)
            p.rotateZ(angle);
    }
    
    public String toJVXString(String name){
        StringBuilder str = new StringBuilder();
        str.append("<geometry name=\"");
        str.append(name);
        str.append("\">\n");
        str.append("\t<pointSet dim=\"3\">\n\t\t<points>\n");
        for(Point3D p : pathPoints){
            str.append(p.toJVXString());
        }
        str.append("\t\t</points>\n\t</pointSet>\n");
        
        str.append("\t<lineSet line=\"show\">\n");
        str.append("\t\t<lines>\n");
        for(int loop = 0; loop < this.nodes()-1; loop++){
            str.append(String.format("\t\t\t<l> %4d %4d </l>", loop, loop+1));
        }
        str.append("\t\t</lines>\n\t</lineSet>\n");
        str.append("</geometry>\n");
        return str.toString();
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Path3D:\n");
        for(Point3D point : pathPoints){
            str.append("\t");
            str.append(point.toString());
            str.append("\n");
        }
        return str.toString();
    }

}
