/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.geom.Path3D;
import org.jlab.geom.Shape3D;
import org.jlab.geom.detector.CLASDetectorGeometry;

/**
 *
 * @author gavalian
 */
public class JVXFile {
    private StringBuilder jvx_XML = new StringBuilder();
    public void JVXFile(){
        
    }
    
    /**
     * Adds CLAS coordinate system for reference.
     */
    public void addCLAS(){
        
        StringBuilder str = new StringBuilder();
        str.append("<geometry name=\"CLASCenter\">\n");
        str.append("\t<pointSet dim=\"3\">\n\t\t<points>\n");
        str.append("\t\t\t<p>   20.   0.0   0.0  </p>\n");
        str.append("\t\t\t<p>  -20.   0.0   0.0  </p>\n");
        str.append("\t\t\t<p>   0.0   20.   0.0  </p>\n");
        str.append("\t\t\t<p>   0.0  -20.   0.0  </p>\n");
        str.append("\t\t\t<p>   0.0   0.0  600.0  </p>\n");
        str.append("\t\t\t<p>   0.0   0.0 -100.0  </p>\n");
        str.append("\t\t</points>\n\t</pointSet>\n");
        str.append("\t<lineSet line=\"show\">\n");
        str.append("\t\t<lines>\n");
        str.append("\t\t\t<l> 0 1 </l>\n");
        str.append("\t\t\t<l> 2 3 </l>\n");
        str.append("\t\t\t<l> 4 5 </l>\n");
        str.append("\t\t</lines>\n\t</lineSet>\n");
        str.append("</geometry>\n");
        jvx_XML.append(str.toString());
    }
    /**
     * Adds a geometry object Path3D, which is connected lines
     * @param path path object
     * @param name name that will appear in the XML file
     */
    public void addPath(Path3D path, String name){
        jvx_XML.append(path.toJVXString(name));
    }
    /**
     * Adds shape to the JVX file.
     * @param shape Shape3D geometry object
     * @param name name that will appear in the XML file
     */
    public void addShape(Shape3D shape, String name){
        jvx_XML.append(shape.toJVXString(name));
    }
    /**
     * Write output to an XML file in JVX format.
     * @param filename output file name.
     */
    public void write(String filename){
        BufferedWriter writer = null;
        try {
            StringBuilder str = new StringBuilder();
            str.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n");
            str.append("<!DOCTYPE jvx-model SYSTEM \"http://www.javaview.de/rsrc/jvx.dtd\">\n");
            str.append("<jvx-model>\n<geometries>\n");
            str.append(jvx_XML.toString());
            str.append("</geometries>\n</jvx-model>\n");
            File logFile=new File(filename);
            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write (str.toString());
            //Close writer
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(JVXFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(JVXFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void addDetector(CLASDetectorGeometry detector){
        String dname = detector.getName();
        for ( int loop = 0; loop < detector.getShapeCount(); loop++){
            Integer component = loop;
            String  shape_name = dname + "_" + component.toString();
            jvx_XML.append(detector.getShape(loop).toJVXString(dname));
        }
    }
}
