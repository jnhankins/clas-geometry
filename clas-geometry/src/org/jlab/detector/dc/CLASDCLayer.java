/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.dc;

import org.jlab.detector.base.ConstantProvider;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.base.DetectorHit;
import org.jlab.geom.Face3D;
import org.jlab.geom.Line3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;
import org.jlab.vector.Vector3D;

/**
 *
 * @author gavalian
 */
public class CLASDCLayer {
    
    private DetectorDescriptor data; // descriptor of the detector
    private Line3D[]           layerWires; // physics wires end points
    private Point3D[]          midPoints; // midpoint is the intersection of 
    private Vector3D[]         wireDirections;    
    private Plane3D            midPlane;
    private Shape3D            dcLayerSurface;
    private static final int   dcLayerWireCount = 112;
    
    
    public CLASDCLayer(int sector, int superlayer, int layer){
        this.data = new DetectorDescriptor("DC",sector,superlayer, layer);
    }
    /**
     * Initialize the DC layer with given wires. 
     * @param x1 - wire start point x coordinate
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2 
     */
    public final void initialize(String[] x1,String[] y1,String[] z1,String[] x2,String[] y2,String[] z2){
        
        layerWires = new Line3D[dcLayerWireCount];
        wireDirections = new Vector3D[dcLayerWireCount];
        for(int loop = 0; loop < dcLayerWireCount;loop++){
            layerWires[loop] = new Line3D(
                    Double.parseDouble(x1[loop]),
                    Double.parseDouble(y1[loop]),
                    Double.parseDouble(z1[loop]),
                    Double.parseDouble(x2[loop]),
                    Double.parseDouble(y2[loop]),
                    Double.parseDouble(z2[loop])
            );
        }        
        initLayerSurface();
        initMidPoints();
    }
    
    public DetectorDescriptor detectorDescriptor(){
        return data;
    }
    
    final void initLayerSurface(){
        dcLayerSurface = new Shape3D();
        int size = layerWires.length;
        if(size>0){
            // adding face composed of two end points of first wire
            // and the origin of last wire.
            dcLayerSurface.addFace(
                    new Face3D(layerWires[0].origin(),
                            layerWires[0].end(),layerWires[size-1].origin()
            ));
            
            // adding face composed of two end points of last wire
            // and the origin of first wire.
            dcLayerSurface.addFace(
                    new Face3D(layerWires[size-1].origin(),
                            layerWires[size-1].end(),layerWires[0].end()
            ));            
        }
    }
    
    final void initMidPoints(){
        midPlane = new Plane3D(0.0,0.0,0.0,0.0,1.0,0.0);
        midPoints = new Point3D[dcLayerWireCount];
        for(int loop = 0; loop < layerWires.length; loop++){
            Point3D point = new Point3D();
            int status = Plane3D.intersection(midPlane, layerWires[loop], point);
            midPoints[loop] = point;
        }
    }
    
    public DetectorHit getLayerHit(Line3D line){
        Point3D point = new Point3D();
        
        for(int loop = 0; loop < dcLayerSurface.size(); loop++){
            if(Face3D.intersection(dcLayerSurface.face(loop),line,point)==true){
                return new DetectorHit(data,point,new Point3D(0.,0.,0.));
            }
        }
        return null;        
    }
    
    public Point3D getMidPoint(int index){
        return midPoints[index];
    }
    
    public Line3D getWire(int index){
        return layerWires[index];
    }
    
    public int getWireCount(){
        return layerWires.length;
    }
}
