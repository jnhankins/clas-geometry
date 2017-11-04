/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom;
import java.util.ArrayList;

/**
 * Geometrical Shape described by Face3D objects. The object can be tested
 * for line intersection.
 * @author gavalian
 */
public class Shape3D implements Transformable {
    private Integer colorR = 110;
    private Integer colorG = 200;
    private Integer colorB = 255;
    private String  shapeName = "genericShape";
    
    private final ArrayList<Face3D> shapeFaces = new ArrayList<>();
    
    public Shape3D(){
        
    }
    
    public void setName(String name){
        shapeName = name;
    }
    
    public void setColor(int r, int g, int b){
        colorR = r;
        colorG = g;
        colorB = b;
    }
    
    public void addFace(Face3D face){
        shapeFaces.add(face);
    }
    
    public Face3D face(int index){
        return shapeFaces.get(index);
    }
    
    /**
     * Returns the center of the shape.
     * @return point that represents the geometric center of the shape.
     */
    public Point3D center(){
        Point3D shape_c = new Point3D();
        double cX = 0.0;
        double cY = 0.0;
        double cZ = 0.0;
        //shape_c.copy(shapeFaces.get(0).center());
        for(int loop = 0; loop < shapeFaces.size();loop++){
            for(int p = 0 ; p < 3; p++){
              cX += shapeFaces.get(loop).point(p).x();
              cY += shapeFaces.get(loop).point(p).y();
              cZ += shapeFaces.get(loop).point(p).z();
            }
            //shape_c.combine(shapeFaces.get(loop).center());
        }
        double factor = 1.0/(shapeFaces.size()*3.0);
        shape_c.set(cX*factor, cY*factor, cZ*factor);
        return shape_c;
    }
    
    public int size(){
        return shapeFaces.size();
    }
    
    @Override
    public void translateXYZ(double x, double y, double z){
        for(Face3D face : shapeFaces){
            face.translateXYZ(x, y, z);
        }
    }
    /**
     * Rotate the entire shape with respect to X axis
     * @param angle rotation angle in radians
     */
    @Override
    public void rotateX(double angle){
        for(Face3D face : shapeFaces){
            face.rotateX(angle);
        }
    }
    /**
     * Rotate the entire shape with respect to Y axis
     * @param angle rotation angle in radians
     */
    @Override
    public void rotateY(double angle){
        for(Face3D face : shapeFaces){
            face.rotateY(angle);
        }
    }
    /**
     * Rotate the entire shape with respect to Z axis
     * @param angle rotation angle in radians
     */
    @Override
    public void rotateZ(double angle){
        for(Face3D face : shapeFaces){
            face.rotateZ(angle);
        }
    }
    /**
     * Move the shape to a coordinate (x,y,z). The center point of the
     * shape will be moved to given coordinate.
     * @param x x coordinate for the new center of the shape
     * @param y y coordinate for the new center of the shape
     * @param z z coordinate for the new center of the shape
     */
    public void moveTo(double x, double y, double z){
        Point3D shape_c = this.center();
        for(Face3D face : shapeFaces){
            for(int loop = 0; loop < 3; loop++){
                Point3D cp = face.point(loop);
                double xp = cp.x() - shape_c.x() + x;
                double yp = cp.y() - shape_c.y() + y;
                double zp = cp.z() - shape_c.z() + z;
                face.point(loop).set(xp, yp, zp);
            }
        }
    }
    
    /**
     * Creates a string corresponding to a geometry shape in JVX format.
     * @param name name that will appear in XML file
     * @return XML string for the object.
     */
    public String toJVXString(String name){
        
        StringBuilder str = new StringBuilder();
        str.append("<geometry name=\"");
        str.append(name);
        str.append("\">\n");
        str.append("<pointSet dim=\"3\" point=\"hide\">\n\t<points>\n");
        for(Face3D face : shapeFaces){
            for(int loop = 0; loop < 3 ; loop++){
                str.append("\t\t");
                str.append(face.point(loop).toJVXString());
                str.append("\n");
            }
        }
        str.append("\t</points>\n</pointSet>\n");
        str.append("<faceSet face=\"show\" edge=\"hide\">\n\t<faces>\n");
        for(int loop = 0 ; loop < this.size(); loop++){
            str.append("\t\t");
            Integer p1 = loop*3;
            Integer p2 = loop*3+1;
            Integer p3 = loop*3+2;
            str.append("<f> ");
            str.append(String.format("%8d", p1));
            str.append(String.format("%8d", p2));
            str.append(String.format("%8d", p3));
            str.append(String.format("%8d", p3));
            str.append("</f>\n");
        }
        str.append("\t\t<color type=\"rgb\">");
        str.append(String.format("%6d %6d %6d ",colorR,colorG,colorB));
        str.append("</color>\n");
        str.append("\t</faces>\n</faceSet>\n");
        str.append("</geometry>\n");
        return str.toString();
    }
}
