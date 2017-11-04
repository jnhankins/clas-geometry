/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.dch;

import java.util.ArrayList;
import org.jlab.detector.base.ConstantProvider;
import org.jlab.detector.impl.DetectorPaddle;
import org.jlab.geom.Face3D;
import org.jlab.geom.Line3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;
import org.jlab.vector.Vector3D;

/**
 *
 * @author J. Hankins
 */
public class DCLayerFactory {
    private static final double small = 0.1; // 1mm
    
    public static DCLayer createLayer(ConstantProvider cp, int sector, int superlayer, int layer) {
        if(!(0<=sector || sector<6)) {
            System.err.println("Error: sector should be 0...5");
            return null;
        }
        if(!(0<=superlayer || superlayer<6)) {
            System.err.println("Error: superlayer should be 0...6");
            return null;
        }
        if(!(0<=layer || layer<6)) {
            System.err.println("Error: layer should be 0...5");
            return null;
        }
        int region = superlayer/2;
        
        // Load constants
        double dist2tgt =                cp.getDouble("/geometry/dc/region/dist2tgt", region);
        double midgap   =                cp.getDouble("/geometry/dc/region/midgap", region);
        double thtilt   = Math.toRadians(cp.getDouble("/geometry/dc/region/thtilt", region));
        double thopen   = Math.toRadians(cp.getDouble("/geometry/dc/region/thopen", region));
        double xdist    =                cp.getDouble("/geometry/dc/region/xdist", region);
        double d_layer  =                cp.getDouble("/geometry/dc/superlayer/wpdist", superlayer);
        double thmin    = Math.toRadians(cp.getDouble("/geometry/dc/superlayer/thmin", superlayer));
        double thster   = Math.toRadians(cp.getDouble("/geometry/dc/superlayer/thster", superlayer));
        int numWires    =                cp.getInteger("/geometry/dc/layer/nsensewires", 0);
        
        // Calculate the midpoint (gx, 0, gz) of the guard wire nearest to the 
        // beam in the first guard wire layer of the current superlayer
        double gz = dist2tgt;
        if (superlayer%2 == 1) {
            gz += midgap + 21*cp.getDouble("/geometry/dc/superlayer/wpdist", superlayer-1);
        }
        double gx = -gz*Math.tan(thtilt-thmin);
        
        // Calculate the distance between the line of intersection of the two 
        // end-plate planes and the z-axis
        double xoff = dist2tgt*Math.tan(thtilt) - xdist/Math.sin(Math.PI/2-thtilt);
        
        // Construct the "left" end-plate plane
        Point3D  p1 = new Point3D(-xoff, 0, 0);
        Vector3D n1 = new Vector3D(0, 1, 0);
        n1.rotateZ(-thopen*0.5);
        Plane3D lPlane = new Plane3D(p1, n1);
        
        // Construct the "right" end-plate plane
        Point3D  p2 = new Point3D(-xoff, 0, 0);
        Vector3D n2 = new Vector3D(0, -1, 0);
        n2.rotateZ(thopen*0.5);
        Plane3D rPlane = new Plane3D(p2, n2);
        
        // Calculate the distance between wire midpoints in the current layer
        double w_layer = Math.sqrt(3)*d_layer/Math.cos(thster);
        
        // Calculate the point the midpoint (mx, 0, mz) of the first sense wire 
        // of the current layer
        double mx = gx + midpointXOffset(layer, w_layer);
        double mz = gz + (layer + 1)*(3*d_layer);
        
        // Iterate through all of the sense wires and store them as detector
        // paddles in a list
        ArrayList<DetectorPaddle> paddles = new ArrayList();
        for(int wire=0; wire<numWires; wire++) {
            
            // The point given by (wx, 0, wz) is the midpoint of the current
            // wire.
            double wx = mx + wire*2*w_layer;
            double wz = mz;
            Point3D wMid = new Point3D(wx, 0, wz);
            
//            System.out.println((layer+1)+" "+(wire+1)+" "+wx+"\t"+wz);
            
            // Find the interesection of the current wire with the end-plate 
            // planes by construciting a long line that passes through the
            // the midpoint and which incorporates the wire's angle (thster)
            Line3D line = new Line3D(0, 1000, 0, 0, -1000, 0);
            line.rotateZ(thster);
            line.translateXYZ(wx, 0, wz);
            Point3D lPoint = new Point3D();
            Point3D rPoint = new Point3D();
            Plane3D.intersection(lPlane, line, lPoint);
            Plane3D.intersection(rPlane, line, rPoint);
            
            // Construct a line from one end point to the other to aid in the
            // construction and positioning of the current wire's detector 
            // paddle object
            Line3D wireLine = new Line3D(lPoint, rPoint);
            
            // Construct the current wire's detector paddle object
            DetectorPaddle paddle = new DetectorPaddle(wire, small, wireLine.length(), small);
            
            // Rotate the paddle to account for the wire's angle
            paddle.rotateZ(thster);
            
            // Translate the paddle into position
            Point3D geometricMid = wireLine.middle();
            paddle.translateXYZ(geometricMid.x(), geometricMid.y(), geometricMid.z());
            
            // Overwrite the paddle's default midpoint, which is the geometric
            // midpoint, with the wire's point of intersection with the midplane
            paddle.getMidpoint().copy(wMid);
            
            // Add wire's paddle object to the list
            paddles.add(paddle);
        }
        
        Plane3D plane = new Plane3D(0, 0, mz, 0, 0, -1);
        
        Shape3D boundary = new Shape3D();
        Point3D pLL = new Point3D(
                paddles.get(0).getLine().origin().x(),
                paddles.get(0).getLine().origin().y(),
                mz);
        Point3D pLR = new Point3D(
                paddles.get(0).getLine().end().x(),
                paddles.get(0).getLine().end().y(),
                mz);
        Point3D pUL = new Point3D(
                paddles.get(numWires-1).getLine().origin().x(),
                paddles.get(numWires-1).getLine().origin().y(),
                mz);
        Point3D pUR = new Point3D(
                paddles.get(numWires-1).getLine().end().x(),
                paddles.get(numWires-1).getLine().end().y(),
                mz);
        boundary.addFace(new Face3D(pLL, pLR, pUL));
        boundary.addFace(new Face3D(pUR, pUL, pLR));
        
//        System.out.println(boundary.face(0));
//        System.out.println(boundary.face(1));
        Plane3D midplane = new Plane3D(0, 0, mz, 0, 1, 0);
        
        return new DCLayer(sector, superlayer, layer, paddles, plane, boundary, midplane);
    }
    
    /**
     * Returns the xoffset between the first guard wire of the first guard wire
     * layer of the current superlayer and the the first sense wire of the 
     * current layer.
     * 
     * The old geometry implementation produces this value incorrectly.  It
     * performs the layer staggering so that every other layer is shifted down
     * towards the x-axis.  However, it should shift every other layer up away
     * from the x-axis.  The correct and incorrect methods are both included.
     * 
     * @param layer the layer index within superlayer where the first sense wire
     * layer has index 0
     * @param w_layer the distance between midpoints of wires in the same layer
     * in the x-axis
     * @return the x-offset
     */
    private static double midpointXOffset(int layer, double w_layer) {
        
        // old, incorrect method:
        return (1 + (layer%2))*w_layer;
        
        // new, correct method:
        //return (3 - (layer%2))*w_layer;   // KEEP
    }
    
//    public static void main(String[] args) {
//        ConstantProvider cp = new ConstantProvider() {
//            @Override public boolean hasConstant(String name) { return get(name) != null; }
//            @Override public int length(String name) { return get(name).size(); }
//            @Override public double getDouble(String name, int row) { return (double)get(name).get(row); }
//            @Override public int getInteger(String name, int row) { return (int)get(name).get(row); }
//            private List get(String name) {
//                switch (name) {
//                    case "/geometry/dc/region/region":            return Arrays.asList(1, 2, 3);
//                    case "/geometry/dc/region/nsuperlayers":      return Arrays.asList(2, 2, 2);
//                    case "/geometry/dc/region/dist2tgt":          return Arrays.asList(228.08, 348.09, 450);
//                    case "/geometry/dc/region/frontgap":          return Arrays.asList(2.5, 2.5, 2.5);
//                    case "/geometry/dc/region/midgap":            return Arrays.asList(2.5, 7.0, 2.5);
//                    case "/geometry/dc/region/backgap":           return Arrays.asList(2.5, 2.5, 2.5);
//                    case "/geometry/dc/region/thopen":            return Arrays.asList(59.0, 60.0, 59.0);
//                    case "/geometry/dc/region/thtilt":            return Arrays.asList(25.0, 25.0, 25.0);
//                    case "/geometry/dc/region/xdist":             return Arrays.asList(7.2664, 16.2106, 7.2664);
//                    case "/geometry/dc/superlayer/superlayer":    return Arrays.asList(1, 2, 3, 4, 5, 6);
//                    case "/geometry/dc/superlayer/nsenselayers":  return Arrays.asList(6, 6, 6, 6, 6, 6);
//                    case "/geometry/dc/superlayer/nguardlayers":  return Arrays.asList(2, 2, 2, 2, 2, 2);
//                    case "/geometry/dc/superlayer/nfieldlayers":  return Arrays.asList(2, 2, 2, 2, 2, 2);
//                    case "/geometry/dc/superlayer/thster":        return Arrays.asList(6.0,-6.0, 6.0,-6.0, 6.0,-6.0);
//                    case "/geometry/dc/superlayer/thmin":         return Arrays.asList(4.55, 4.55, 4.55, 4.55, 4.50, 4.899);
//                    case "/geometry/dc/superlayer/wpdist":        return Arrays.asList(0.3862, 0.4042, 0.6219, 0.6586, 0.8600, 0.9000);
//                    case "/geometry/dc/superlayer/cellthickness": return Arrays.asList(3, 3, 3, 3, 3, 3);
//                    case "/geometry/dc/layer/nsensewires":        return Arrays.asList(112);
//                    case "/geometry/dc/layer/nguardwires":        return Arrays.asList(2);
//                }
//                return null;
//            }
//        };
//        
//        createLayer(cp, 0, 0, 0);
//        createLayer(cp, 0, 0, 1);
//        createLayer(cp, 0, 0, 2);
//        createLayer(cp, 0, 0, 3);
//        createLayer(cp, 0, 0, 4);
//        createLayer(cp, 0, 0, 5);
//        
//        System.out.println();
//        Geometry.Load();
//    }
}