/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom;

/**
 *
 * @author gavalian
 */
public class ShapeGeometry {
    /**
     * Create a BOX object with triangular faces.
     * @param length
     * @param width
     * @param height
     * @return 
     */
    public static Shape3D getBox(double length, double width, double height){
        Shape3D box = new Shape3D();
        
        Point3D p1 = new Point3D( length*0.5,  width*0.5,  height*0.5);
        Point3D p2 = new Point3D( length*0.5, -width*0.5,  height*0.5);
        Point3D p3 = new Point3D( length*0.5, -width*0.5, -height*0.5);
        Point3D p4 = new Point3D( length*0.5,  width*0.5, -height*0.5);
        Point3D p5 = new Point3D(-length*0.5,  width*0.5,  height*0.5);
        Point3D p6 = new Point3D(-length*0.5, -width*0.5,  height*0.5);
        Point3D p7 = new Point3D(-length*0.5, -width*0.5, -height*0.5);
        Point3D p8 = new Point3D(-length*0.5,  width*0.5, -height*0.5);
        // perpendicular to x-axis faces
        box.addFace(new Face3D(p1,p2,p3));
        box.addFace(new Face3D(p1,p4,p3));
        box.addFace(new Face3D(p5,p6,p7));
        box.addFace(new Face3D(p5,p8,p7));
        // perpendicular to y-axis faces
        box.addFace(new Face3D(p1,p4,p5));
        box.addFace(new Face3D(p8,p4,p5));
        box.addFace(new Face3D(p2,p3,p6));
        box.addFace(new Face3D(p3,p6,p7));
        // perpendicular to z-axis faces        
        box.addFace(new Face3D(p1,p2,p5));
        box.addFace(new Face3D(p6,p2,p5));
        box.addFace(new Face3D(p3,p4,p7));
        box.addFace(new Face3D(p7,p4,p8));
        return box;
    }
    
    public static String toJVXString(Shape3D shape){
        StringBuilder str = new StringBuilder();
        str.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n");
        str.append("<!DOCTYPE jvx-model SYSTEM \"http://www.javaview.de/rsrc/jvx.dtd\">\n");
        str.append("<jvx-model>\n<geometries>\n");
        str.append(ShapeGeometry.clasLabFrameToJVX());
        str.append(shape.toJVXString("TOFScintilator"));
        str.append("</geometries>\n</jvx-model>\n");
        return str.toString();
    }
    
    public static String clasLabFrameToJVX(){
        StringBuilder str = new StringBuilder();
        str.append("<geometry name=\"CLASCenter\">\n");
        str.append("\t<pointSet dim=\"3\">\n\t\t<points>\n");
        str.append("\t\t\t<p>   0.5   0.0   0.0  </p>\n");
        str.append("\t\t\t<p>  -0.5   0.0   0.0  </p>\n");
        str.append("\t\t\t<p>   0.0   0.5   0.0  </p>\n");
        str.append("\t\t\t<p>   0.0  -0.5   0.0  </p>\n");
        str.append("\t\t\t<p>   0.0   0.0  10.0  </p>\n");
        str.append("\t\t\t<p>   0.0   0.0 -10.0  </p>\n");
        str.append("\t\t</points>\n\t</pointSet>\n");
        str.append("\t<lineSet line=\"show\">\n");
        str.append("\t\t<lines>\n");
        str.append("\t\t\t<l> 0 1 </l>\n");
        str.append("\t\t\t<l> 2 3 </l>\n");
        str.append("\t\t\t<l> 4 5 </l>\n");
        str.append("\t\t</lines>\n\t</lineSet>\n");
        str.append("</geometry>\n");
        return str.toString();
    }
}
