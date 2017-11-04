/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom.detector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.jlab.geom.Face3D;
import org.jlab.geom.Line3D;
import org.jlab.geom.Path3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;

/**
 *
 * @author gavalian
 */
public abstract class CLASDetectorGeometry {
    
    private HashMap<String,double[]>  doubleArray    = new HashMap<>();
    private ArrayList<Shape3D>        detectorShapes = new ArrayList<>();
    private String detectorName = "undefined";
    
    public CLASDetectorGeometry(){
        
    }
    
    public CLASDetectorGeometry(String name){
        detectorName = name;
    }
    
    public String getName(){
        return detectorName;
    }
    
    public void setName(String name){
        detectorName = name;
    }
    
    public void addStringDouble(String name, Vector<String> data){
        double[] array = new double[data.size()];
        for(int loop = 0; loop < data.size(); loop++){
            array[loop] = Double.parseDouble(data.elementAt(loop));
        }
        this.addDouble(name, array);
    }
    
    public void addDouble(String name, double[] data){
        doubleArray.put(name, data);
    }
    
    public double[] getDouble(String name){
        if(doubleArray.containsKey(name)==false) return null;
        return doubleArray.get(name);
    }
    
    public void addShape(Shape3D shape){
        detectorShapes.add(shape);
    }
    
    public Shape3D getShape(int index){
        return detectorShapes.get(index);
    }
    
    public int getShapeCount(){
        return detectorShapes.size();
    }
    
    public void show(){
        for(Map.Entry<String,double[]> entry : doubleArray.entrySet()){
            System.err.println(entry.getKey() + " : length = " + entry.getValue().length);
        }
    }
    
    public void clearHits(){
        for(int loop = 0 ; loop < this.getShapeCount(); loop++){
            this.getShape(loop).setColor(110, 200, 255);
        }
    }
    
    public void calculateHits(Path3D path){
        Line3D  testline = new Line3D();
        Point3D inter_point = new Point3D();
        for(int loop = 0 ; loop < this.getShapeCount(); loop++){
            for(int f = 0 ; f < this.getShape(loop).size(); f++){
                for(int p = 0 ; p < path.nodes() - 1; p++){
                    testline.set(path.getNode(p), path.getNode(p+1));
                    if(Face3D.intersection(this.getShape(loop).face(f), 
                            testline, inter_point)){
                        this.getShape(loop).setColor(255, 0, 0);
                        System.out.println("---> intersection found : " + 
                                " s = " + loop + " f = " + f + " path point " + p);
                    }
                }
            }
        }
    }
    
    abstract void initGeometry();
    
}
