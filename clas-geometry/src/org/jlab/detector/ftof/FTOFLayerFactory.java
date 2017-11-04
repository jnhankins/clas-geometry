/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ftof;

import java.util.ArrayList;
import java.util.List;
import org.jlab.detector.base.ConstantProvider;
import org.jlab.detector.impl.DetectorPaddle;
import org.jlab.geom.Face3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;

/**
 *
 * @author J. Hankins
 */
public class FTOFLayerFactory {
    
    public static FTOFLayer createLayer(ConstantProvider cp, int sector, int superlayer, int layer) {
        if(!(0<=sector || sector<6)) {
            System.err.println("Error: sector should be 0...5");
            return null;
        }
        if(!(0<=superlayer || superlayer<3)) {
            System.err.println("Error: superlayer should be 0, 1, or 2");
            return null;
        }
        if(layer!=0) {
            System.err.println("Error: layer should be 0");
            return null;
        }
            
        
        String layerStr = null;
        if      (superlayer == 0) layerStr = "panel1a";
        else if (superlayer == 1) layerStr = "panel1b";
        else if (superlayer == 2) layerStr = "panel2";
        int    numPaddles       =    cp.length("/geometry/ftof/"+layerStr+"/paddles/paddle");
        double paddlewidth      = cp.getDouble("/geometry/ftof/"+layerStr+"/panel/paddlewidth", 0); 
        double paddlethickness  = cp.getDouble("/geometry/ftof/"+layerStr+"/panel/paddlethickness", 0); 
        double gap              = cp.getDouble("/geometry/ftof/"+layerStr+"/panel/gap", 0);
        double wrapperthickness = cp.getDouble("/geometry/ftof/"+layerStr+"/panel/wrapperthickness", 0);
        String lengthstr = "/geometry/ftof/"+layerStr+"/paddles/Length";
        
        List<DetectorPaddle> paddles = new ArrayList();
        
        for (int paddleId=0; paddleId<numPaddles; paddleId++) {
            double paddlelength = cp.getDouble(lengthstr, paddleId);
            DetectorPaddle paddle = new DetectorPaddle(paddleId, paddlewidth, paddlelength, paddlethickness);
            double xoffset = paddleId * (paddlewidth + gap + 2*wrapperthickness);
            paddle.translateXYZ(paddlewidth*0.5 + xoffset, 0, paddlethickness*0.5);
            paddles.add(paddle);
        }
        
        Plane3D plane = new Plane3D(0, 0, 0, 0, 0, -1);
        
        Shape3D boundary = new Shape3D();
        Point3D pLL = paddles.get(0).getShape().face(1).point(2);
        Point3D pLR = paddles.get(0).getShape().face(1).point(0);
        Point3D pUL = paddles.get(numPaddles-1).getShape().face(0).point(0);
        Point3D pUR = paddles.get(numPaddles-1).getShape().face(0).point(2);
        Point3D pBL = paddles.get(numPaddles-1).getShape().face(1).point(2);
        Point3D pBR = paddles.get(numPaddles-1).getShape().face(1).point(0);
        pUL = new Point3D(pUL.x(), pUL.y() + paddlewidth*(pBL.y()-pLL.y())/(pBL.x()-pLL.x()) , pUL.z());
        pUR = new Point3D(pUR.x(), pUR.y() + paddlewidth*(pBR.y()-pLR.y())/(pBR.x()-pLR.x()) , pUR.z());
        boundary.addFace(new Face3D(pLL, pLR, pUL));
        boundary.addFace(new Face3D(pUR, pUL, pLR));
        
        return new FTOFLayer(sector, superlayer, 0, paddles, plane, boundary);
    }
}