/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ec;

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
public class ECLayerFactory {
    
    public static ECLayer createLayer(ConstantProvider cp, int sector, int superlayer, int layer)  {
        switch (superlayer) {
            case 0: switch(layer) {
                case 0: return createPCALU(cp, sector);
                case 1: return createPCALV(cp, sector);
                case 2: return createPCALW(cp, sector);
            }
            case 1: switch(layer) {
                case 0: return null; //createECU(cp, sector, 1, 0);
                case 1: return null; //createECV(cp, sector, 1, 1);
                case 2: return null; //createECW(cp, sector, 1, 2);
            }
            case 2: switch(layer) {
                case 0: return null; //createECU(cp, sector, 2, 15);
                case 1: return null; //createECV(cp, sector, 2, 16);
                case 2: return null; //createECV(cp, sector, 2, 17);
            }
        }
        throw new IllegalArgumentException("Illegal combination: superlayer="+superlayer+" & layer="+layer);
    }
    
    private static ECLayer createPCALU(ConstantProvider cp, int sector) {
        final int numPaddlesPCalU = 68;
        final int numDoublePaddlesPCalU = 16;
        final double strip_thick = cp.getDouble("/geometry/pcal/pcal/strip_thick", 0)*0.1;
        final double strip_width = cp.getDouble("/geometry/pcal/pcal/strip_width", 0)*0.1;
        final double max_length = cp.getDouble("/geometry/pcal/UView/max_length", 0)*0.1;
        final double yhigh = cp.getDouble("/geometry/pcal/pcal/yhigh", 0)*0.1;
        
        List<DetectorPaddle> paddles = new ArrayList();
        
        // Create the u-view paddles such that paddleId 0 corresponds to the 
        // shortest paddle and the first readout channel
        for (int paddleId = 0; paddleId < numPaddlesPCalU; ++paddleId) {

            double lengthShorter;
            double lengthLonger;
            double width;
            double yOff;
            int paddleIdInv = numPaddlesPCalU-paddleId-1;
            if (paddleIdInv < numDoublePaddlesPCalU) {
                lengthLonger = calcLengthPCALU(max_length, strip_width, 2*paddleIdInv);
                lengthShorter = calcLengthPCALU(max_length, strip_width, 2*paddleIdInv+2);
                width = strip_width*2;
                yOff = paddleIdInv*strip_width*2;
            } else {
                lengthLonger = calcLengthPCALU(max_length, strip_width, numDoublePaddlesPCalU+paddleIdInv);
                lengthShorter = calcLengthPCALU(max_length, strip_width, numDoublePaddlesPCalU+paddleIdInv+1);
                width = strip_width;
                yOff = (numDoublePaddlesPCalU+paddleIdInv)*strip_width;
            }

            // Create the points that define the paddle's trapazoidal shape
            // l - left, r - right, u - up, d - down, f - far, n - near
            Point3D p1 = new Point3D( 0,      lengthLonger*0.5,  strip_thick); // l u f
            Point3D p2 = new Point3D( 0,     -lengthLonger*0.5,  strip_thick); // l d f
            Point3D p3 = new Point3D( 0,     -lengthLonger*0.5,  0);           // l d n
            Point3D p4 = new Point3D( 0,      lengthLonger*0.5,  0);           // l u n
            Point3D p5 = new Point3D(-width,  lengthShorter*0.5, strip_thick); // r u f
            Point3D p6 = new Point3D(-width, -lengthShorter*0.5, strip_thick); // r d f
            Point3D p7 = new Point3D(-width, -lengthShorter*0.5, 0);           // r d n
            Point3D p8 = new Point3D(-width,  lengthShorter*0.5, 0);           // r u n
            
            // Create the paddle
            DetectorPaddle paddle = new DetectorPaddle(paddleId, p1, p2, p3, p4, p5, p6, p7, p8);

            // Rotate it so that its direction vector is anti-parralel to the
            // x-axis in PCAL coordinates
            paddle.rotateZ(Math.toRadians(90));

            // Translate the paddle upwards into position with its upstream face
            // flush with the xy-plane.
            paddle.translateXYZ(0, yhigh - yOff, 0);

            // Add the paddle to the list
            paddles.add(paddle);
        }
        
        Plane3D plane = new Plane3D(0, 0, 0, 0, 0, -1);
        
        Shape3D boundary = new Shape3D();
        Point3D pLL = paddles.get(0).getShape().face(1).point(2);
        Point3D pLR = paddles.get(0).getShape().face(1).point(0);
        Point3D pUL = paddles.get(numPaddlesPCalU-1).getShape().face(0).point(0);
        Point3D pUR = paddles.get(numPaddlesPCalU-1).getShape().face(0).point(2);
        boundary.addFace(new Face3D(pLL, pLR, pUL));
        boundary.addFace(new Face3D(pUR, pUL, pLR));
        
        return new ECLayer(sector, 0, 0, paddles, plane, boundary);
    }
    private static ECLayer createPCALV(ConstantProvider cp, int sector) {
        final int numPaddlesPCalVW = 62;
        final int numSinglePaddlesPCalVW = 47;
        final double strip_thick = cp.getDouble("/geometry/pcal/pcal/strip_thick", 0)*0.1;
        final double strip_width = cp.getDouble("/geometry/pcal/pcal/strip_width", 0)*0.1;
        final double max_length = cp.getDouble("/geometry/pcal/VView/max_length", 0)*0.1;
        final double yhigh = cp.getDouble("/geometry/pcal/pcal/yhigh", 0)*0.1;
        final double view_angle = Math.toRadians(cp.getDouble("/geometry/pcal/pcal/view_angle", 0));
        
        final double l0u = cp.getDouble("/geometry/pcal/UView/max_length", 0)*0.1;
        final double height = l0u*0.5*Math.tan(view_angle);
        final double ylo = height - yhigh;
        
        List<DetectorPaddle> paddles = new ArrayList();
        
        // Create the v-view paddles such that paddleId 0 corresponds to the 
        // shortest paddle and the first readout channel
        for (int paddleId = 0; paddleId < numPaddlesPCalVW; ++paddleId) {

            double lengthLonger;
            double width;
            double yOff;
            int paddleIdInv = numPaddlesPCalVW-paddleId-1;
            if (paddleIdInv < numSinglePaddlesPCalVW) {
                lengthLonger = calcLengthPCALVW(max_length, strip_width, paddleIdInv);
                width = strip_width;
                yOff = paddleIdInv*strip_width;
            } else {
                lengthLonger = calcLengthPCALVW(max_length, strip_width, 2*paddleIdInv-numSinglePaddlesPCalVW);
                width = strip_width*2;
                yOff = (2*paddleIdInv-numSinglePaddlesPCalVW)*strip_width;
            }

            // Create the points that define the paddle's trapazoidal shape
            // l - left, r - right, u - up, d - down, f - far, n - near
            double topAngle = Math.toRadians(90)-view_angle; 
            double botAngle = 2*view_angle-Math.toRadians(90);
            double top = lengthLonger - width*Math.tan(topAngle);
            double bot = width*Math.tan(botAngle);
            Point3D p1 = new Point3D( 0,     lengthLonger, strip_thick); // l u f
            Point3D p2 = new Point3D( 0,     0,            strip_thick); // l d f
            Point3D p3 = new Point3D( 0,     0,            0);           // l d n
            Point3D p4 = new Point3D( 0,     lengthLonger, 0);           // l u n
            Point3D p5 = new Point3D(-width, top,          strip_thick); // r u f
            Point3D p6 = new Point3D(-width, bot,          strip_thick); // r d f
            Point3D p7 = new Point3D(-width, bot,          0);           // r d n
            Point3D p8 = new Point3D(-width, top,          0);           // r u n

            // Create the paddle
            DetectorPaddle paddle = new DetectorPaddle(paddleId, p1, p2, p3, p4, p5, p6, p7, p8);

            // Rotate it so that its direction vector is parallel with the
            // x-axis in an intermediate coordinate system defined as:
            //    the z-axis is the same as the PCAL z-axis
            //    the x-axis is parallel with the v-axis
            //    the y-axis is points towards the rest of the triangle
            //    the origin is at the apex of the isosceles triangle
            paddle.rotateZ(Math.toRadians(-90));

            // Translate the paddle into position in the triangle in the
            // intermediate coordinate system.
            double gamma = 1/Math.tan(Math.toRadians(180)-2*view_angle);
            paddle.translateXYZ(yOff*gamma, yOff, strip_thick);

            // Transform the coordinate system from from the intermediate 
            // coordinates to PCAL coordinates
            paddle.rotateZ(view_angle);
            paddle.translateXYZ(0, -ylo, 0);

            // Add the paddle to the list
            paddles.add(paddle);
        }
        
        Plane3D plane = new Plane3D(0, 0, 0, 0, 0, -1);
        
        Shape3D boundary = new Shape3D();
        Point3D pLL = paddles.get(0).getShape().face(0).point(0);
        Point3D pLR = paddles.get(0).getShape().face(0).point(2);
        Point3D pUL = paddles.get(numPaddlesPCalVW-1).getShape().face(1).point(2);
        Point3D pUR = paddles.get(numPaddlesPCalVW-1).getShape().face(1).point(0);
        boundary.addFace(new Face3D(pLL, pLR, pUL));
        boundary.addFace(new Face3D(pUR, pUL, pLR));
        
        return new ECLayer(sector, 0, 1, paddles, plane, boundary);
    }
    private static ECLayer createPCALW(ConstantProvider cp, int sector) {
        final int numPaddlesPCalVW = 62;
        final int numSinglePaddlesPCalVW = 47;
        final double strip_thick = cp.getDouble("/geometry/pcal/pcal/strip_thick", 0)*0.1;
        final double strip_width = cp.getDouble("/geometry/pcal/pcal/strip_width", 0)*0.1;
        final double max_length = cp.getDouble("/geometry/pcal/WView/max_length", 0)*0.1;
        final double yhigh = cp.getDouble("/geometry/pcal/pcal/yhigh", 0)*0.1;
        final double view_angle = Math.toRadians(cp.getDouble("/geometry/pcal/pcal/view_angle", 0));
        
        final double l0u = cp.getDouble("/geometry/pcal/UView/max_length", 0)*0.1;
        final double height = l0u*0.5*Math.tan(view_angle);
        final double ylo = height - yhigh;
        
        List<DetectorPaddle> paddles = new ArrayList();
        
        // Create the w-view paddles such that paddleId 0 corresponds to the 
        // shortest paddle and the first readout channel
        for (int paddleId = 0; paddleId < numPaddlesPCalVW; ++paddleId) {

            double lengthLonger;
            double width;
            double yOff;
            int paddleIdInv = numPaddlesPCalVW-paddleId-1;
            if (paddleIdInv < numSinglePaddlesPCalVW) {
                lengthLonger = calcLengthPCALVW(max_length, strip_width, paddleIdInv);
                width = strip_width;
                yOff = paddleIdInv*strip_width;
            } else {
                lengthLonger = calcLengthPCALVW(max_length, strip_width, 2*paddleIdInv-numSinglePaddlesPCalVW);
                width = strip_width*2;
                yOff = (2*paddleIdInv-numSinglePaddlesPCalVW)*strip_width;
            }

            // Create the points that define the paddle's trapazoidal shape
            // l - left, r - right, u - up, d - down, f - far, n - near
            double topAngle = Math.toRadians(90)-view_angle; 
            double botAngle = 2*view_angle-Math.toRadians(90);
            double top = lengthLonger - width*Math.tan(topAngle);
            double bot = width*Math.tan(botAngle);
            Point3D p1 = new Point3D(width, top,          strip_thick); // l u f
            Point3D p2 = new Point3D(width, bot,          strip_thick); // l d f
            Point3D p3 = new Point3D(width, bot,          0);           // l d n
            Point3D p4 = new Point3D(width, top,          0);           // l u n
            Point3D p5 = new Point3D( 0,    lengthLonger, strip_thick); // r u f
            Point3D p6 = new Point3D( 0,    0,            strip_thick); // r d f
            Point3D p7 = new Point3D( 0,    0,            0);           // r d n
            Point3D p8 = new Point3D( 0,    lengthLonger, 0);           // r u n

            // Create the paddle
            DetectorPaddle paddle = new DetectorPaddle(paddleId, p1, p2, p3, p4, p5, p6, p7, p8);

            // Rotate it so that its direction vector is antiparallel with 
            // the x-axis in an intermediate coordinate system defined as:
            //    the z-axis is the same as the PCAL z-axis
            //    the x-axis is parallel with the v-axis
            //    the y-axis is points towards the rest of the triangle
            //    the origin is at the apex of the isosceles triangle
            paddle.rotateZ(Math.toRadians(90));

            // Translate the paddle into position in the triangle in the
            // intermediate coordinate system.
            double gamma = -1/Math.tan(Math.toRadians(180)-2*view_angle);
            paddle.translateXYZ(yOff*gamma, yOff, 2*strip_thick);

            // Transform the coordinate system from from the intermediate 
            // coordinates to PCAL coordinates
            paddle.rotateZ(-view_angle);
            paddle.translateXYZ(0, -ylo, 0);

            // Add the paddle to the list
            paddles.add(paddle);
        }
        
        Plane3D plane = new Plane3D(0, 0, 0, 0, 0, -1);
        
        Shape3D boundary = new Shape3D();
        Point3D pLL = paddles.get(0).getShape().face(1).point(2);
        Point3D pLR = paddles.get(0).getShape().face(1).point(0);
        Point3D pUL = paddles.get(numPaddlesPCalVW-1).getShape().face(0).point(0);
        Point3D pUR = paddles.get(numPaddlesPCalVW-1).getShape().face(0).point(2);
        boundary.addFace(new Face3D(pLL, pLR, pUL));
        boundary.addFace(new Face3D(pUR, pUL, pLR));
        
        return new ECLayer(sector, 0, 2, paddles, plane, boundary);
    }
    
    private static double calcLengthPCALU(double L1, double width, int paddleId) {
        // Equation given in "The Geometry of the CLAS12 Pre-shower Calorimeter"
        // https://clasweb.jlab.org/wiki/images/d/d0/Pcal_geometry_note.pdf
        return L1 - 2*paddleId*width*Math.tan(Math.toRadians(27.1));
    }
    private static double calcLengthPCALVW(double L1, double width, int paddleId) {
        // Equation given in "The Geometry of the CLAS12 Pre-shower Calorimeter"
        // https://clasweb.jlab.org/wiki/images/d/d0/Pcal_geometry_note.pdf
        return L1 - paddleId*width*(Math.tan(Math.toRadians(27.1))+Math.tan(Math.toRadians(35.8)));
    }

    /*
    private static ECLayer createECU(ConstantProvider cp, int sector, int superlayer, int layer) {
        final int numPaddlesPCalVW = 62;
        final int numSinglePaddlesPCalVW = 47;
        final double strip_thick = cp.getDouble("/geometry/pcal/pcal/strip_thick", 0)*0.1;
        final double strip_width = cp.getDouble("/geometry/pcal/pcal/strip_width", 0)*0.1;
        final double max_length = cp.getDouble("/geometry/pcal/WView/max_length", 0)*0.1;
        final double yhigh = cp.getDouble("/geometry/pcal/pcal/yhigh", 0)*0.1;
        final double view_angle = cp.getDouble("/geometry/pcal/pcal/view_angle", 0)*0.1;
        
        List<DetectorPaddle> paddles = new ArrayList();
        Point3D pUL = new Point3D();
        Point3D pUR = new Point3D();
        Point3D pLL = new Point3D();
        Point3D pLR = new Point3D();
        
        // Create the u-view paddles such that paddleId 0 corresponds to the 
        // shortest paddle and the first readout channel
        for (int paddleId = 0; paddleId < numPaddlesEC; ++paddleId) {

            int paddleIdInv = numPaddlesEC-paddleId-1;
            double L1 = calcL1ECU(layer);
            double width = calcWidthECU(layer);
            double lengthLonger = calcLengthU(L1, width, paddleIdInv);
            double lengthShorter = calcLengthU(L1, width, paddleIdInv+1);
            double yOff = paddleIdInv*width;
            double d2 = layer <= 14? d2innerEC : d2outerEC;

            // Create the points that define the paddle's trapazoidal shape
            // l - left, r - right, u - up, d - down, f - far, n - near
            Point3D p1 = new Point3D( 0,      lengthLonger*0.5+d2, thicknessEC); // l u f
            Point3D p2 = new Point3D( 0,     -lengthLonger*0.5,    thicknessEC); // l d f
            Point3D p3 = new Point3D( 0,     -lengthLonger*0.5,    0);             // l d n
            Point3D p4 = new Point3D( 0,      lengthLonger*0.5+d2, 0);             // l u n
            Point3D p5 = new Point3D(-width,  lengthLonger*0.5+d2, thicknessEC); // r u f
            Point3D p6 = new Point3D(-width, -lengthShorter*0.5,   thicknessEC); // r d f
            Point3D p7 = new Point3D(-width, -lengthShorter*0.5,   0);             // r d n
            Point3D p8 = new Point3D(-width,  lengthLonger*0.5+d2, 0);             // r u n
            
            // Create the paddle
            DetectorPaddle paddle = new DetectorPaddle(paddleId, p1, p2, p3, p4, p5, p6, p7, p8);

            // Rotate it so that its direction vector is anti-parralel to the
            // x-axis in EC coordinates
            paddle.rotateZ(Math.toRadians(90));

            // Translate the paddle upwards into position with its upstream face
            // flush with the xy-plane.
            paddle.translateXYZ(0, calcYhEC(layer) - yOff, calcZEC(layer));
            
            // Add the paddle to the list
            paddles.add(paddle);
            
            if(paddleId == 0) {
                pLL.copy(p7);
                pLR.copy(p8);
                pLL.rotateZ(Math.toRadians(90));
                pLR.rotateZ(Math.toRadians(90));
                pLL.translateXYZ(0, calcYhEC(layer) - yOff, calcZEC(layer));
                pLR.translateXYZ(0, calcYhEC(layer) - yOff, calcZEC(layer));
            }
            if(paddleId == numPaddlesPCalVW-1 ) {
                pUL.copy(p3);
                pUR.copy(p4);
                pUL.rotateZ(Math.toRadians(90));
                pUR.rotateZ(Math.toRadians(90));
                pUL.translateXYZ(0, calcYhEC(layer) - yOff, calcZEC(layer));
                pUR.translateXYZ(0, calcYhEC(layer) - yOff, calcZEC(layer));
            }
        }
        
        Shape3D boundary = new Shape3D();
        boundary.addFace(new Face3D(pLL, pLR, pUL));
        boundary.addFace(new Face3D(pUR, pUL, pLR));
        
        Plane3D plane = new Plane3D(0, 0, 0, 0, 0, -1);
        
        return new ECLayer(sector, 0, 2, paddles, plane, boundary);
    }
    private static ECLayer createECV(ConstantProvider cp, int sector, int superlayer, int layer) {
        final int numPaddlesPCalVW = 62;
        final int numSinglePaddlesPCalVW = 47;
        final double strip_thick = cp.getDouble("/geometry/pcal/pcal/strip_thick", 0)*0.1;
        final double strip_width = cp.getDouble("/geometry/pcal/pcal/strip_width", 0)*0.1;
        final double max_length = cp.getDouble("/geometry/pcal/WView/max_length", 0)*0.1;
        final double yhigh = cp.getDouble("/geometry/pcal/pcal/yhigh", 0)*0.1;
        final double view_angle = cp.getDouble("/geometry/pcal/pcal/view_angle", 0)*0.1;
        
        List<DetectorPaddle> paddles = new ArrayList();
        Point3D pUL = new Point3D();
        Point3D pUR = new Point3D();
        Point3D pLL = new Point3D();
        Point3D pLR = new Point3D();
        
        // Create the v-view paddles such that paddleId 0 corresponds to the 
        // shortest paddle and the first readout channel
        for (int paddleId = 0; paddleId < numPaddlesEC; ++paddleId) {

            int paddleIdInv = numPaddlesEC-paddleId-1;
            double L1 = calcL1ECVW(layer);
            double width = calcWidthECWV(layer);
            double lengthLonger = calcLengthVW(L1, width, paddleIdInv);
            double yOff = paddleIdInv*width;
            double d2 = layer <= 14? d2innerEC : d2outerEC;
            
            // Create the points that define the paddle's trapazoidal shape
            // l - left, r - right, u - up, d - down, f - far, n - near
            double botAngle = 2*alphaPCal-Math.toRadians(90);
            double bot = width*Math.tan(botAngle);
            Point3D p1 = new Point3D( 0,     lengthLonger+d2, thicknessEC); // l u f
            Point3D p2 = new Point3D( 0,     0,               thicknessEC); // l d f
            Point3D p3 = new Point3D( 0,     0,               0);           // l d n
            Point3D p4 = new Point3D( 0,     lengthLonger+d2, 0);           // l u n
            Point3D p5 = new Point3D(-width, lengthLonger+d2, thicknessEC); // r u f
            Point3D p6 = new Point3D(-width, bot,             thicknessEC); // r d f
            Point3D p7 = new Point3D(-width, bot,             0);           // r d n
            Point3D p8 = new Point3D(-width, lengthLonger+d2, 0);           // r u n
            
            // Create the paddle
            DetectorPaddle paddle = new DetectorPaddle(paddleId, p1, p2, p3, p4, p5, p6, p7, p8);

            // Rotate it so that its direction vector is parallel with the
            // x-axis in an intermediate coordinate system defined as:
            //    the z-axis is the same as the EC z-axis
            //    the x-axis is parallel with the v-axis
            //    the y-axis is points towards the rest of the triangle
            //    the origin is at the apex of the isosceles triangle
            paddle.rotateZ(Math.toRadians(-90));

            // Translate the paddle into position in the triangle in the
            // intermediate coordinate system.
            double gamma = 1/Math.tan(Math.toRadians(180)-2*alphaEC);
            paddle.translateXYZ(yOff*gamma, yOff, calcZEC(layer));

            // Transform the coordinate system from from the intermediate 
            // coordinates to EC coordinates
            paddle.rotateZ(alphaEC);
            paddle.translateXYZ(0, calcYlEC(layer), 0);
            
            // Add the paddle to the list
            paddles.add(paddle);
            
            if(paddleId == 0) {
                pLL.copy(p3);
                pLR.copy(p4);
                pLL.rotateZ(Math.toRadians(-90));
                pLR.rotateZ(Math.toRadians(-90));
                pLL.translateXYZ(yOff*gamma, yOff, 2*strip_thick);
                pLR.translateXYZ(yOff*gamma, yOff, 2*strip_thick);
                pLL.rotateZ(-view_angle);
                pLR.rotateZ(-view_angle);
                pLL.translateXYZ(0, yhigh, 0);
                pLR.translateXYZ(0, yhigh, 0);
            }
            if(paddleId == numPaddlesPCalVW-1 ) {
                pUL.copy(p7);
                pUR.copy(p8);
                pUL.rotateZ(Math.toRadians(-90));
                pUR.rotateZ(Math.toRadians(-90));
                pUL.translateXYZ(yOff*gamma, yOff, 2*strip_thick);
                pUR.translateXYZ(yOff*gamma, yOff, 2*strip_thick);
                pUL.rotateZ(-view_angle);
                pUR.rotateZ(-view_angle);
                pUL.translateXYZ(0, yhigh, 0);
                pUR.translateXYZ(0, yhigh, 0);
            }
        }
        
        Shape3D boundary = new Shape3D();
        boundary.addFace(new Face3D(pLL, pLR, pUL));
        boundary.addFace(new Face3D(pUR, pUL, pLR));
        
        Plane3D plane = new Plane3D(0, 0, 0, 0, 0, -1);
        
        return new ECLayer(sector, 0, 2, paddles, plane, boundary);
    }
    private static ECLayer createECW(ConstantProvider cp, int sector, int superlayer, int layer) {
        final int numPaddlesPCalVW = 62;
        final int numSinglePaddlesPCalVW = 47;
        final double strip_thick = cp.getDouble("/geometry/pcal/pcal/strip_thick", 0)*0.1;
        final double strip_width = cp.getDouble("/geometry/pcal/pcal/strip_width", 0)*0.1;
        final double max_length = cp.getDouble("/geometry/pcal/WView/max_length", 0)*0.1;
        final double yhigh = cp.getDouble("/geometry/pcal/pcal/yhigh", 0)*0.1;
        final double view_angle = cp.getDouble("/geometry/pcal/pcal/view_angle", 0)*0.1;
        
        List<DetectorPaddle> paddles = new ArrayList();
        Point3D pUL = new Point3D();
        Point3D pUR = new Point3D();
        Point3D pLL = new Point3D();
        Point3D pLR = new Point3D();
        
        // Create the w-view paddles such that paddleId 0 corresponds to the 
        // shortest paddle and the first readout channel
        for (int paddleId = 0; paddleId < numPaddlesEC; ++paddleId) {

            int paddleIdInv = numPaddlesEC-paddleId-1;
            double L1 = calcL1ECVW(layer);
            double width = calcWidthECWV(layer);
            double lengthLonger = calcLengthVW(L1, width, paddleIdInv);
            double yOff = paddleIdInv*width;
            double d2 = layer <= 14? d2innerEC : d2outerEC;

            // Create the points that define the paddle's trapazoidal shape
            // l - left, r - right, u - up, d - down, f - far, n - near
            double topAngle = Math.toRadians(90)-alphaPCal; 
            double top = lengthLonger - width*Math.tan(topAngle);
            Point3D p1 = new Point3D(width, top,          thicknessEC); // l u f
            Point3D p2 = new Point3D(width, -d2,          thicknessEC); // l d f
            Point3D p3 = new Point3D(width, -d2,          0);           // l d n
            Point3D p4 = new Point3D(width, top,          0);           // l u n
            Point3D p5 = new Point3D( 0,    lengthLonger, thicknessEC); // r u f
            Point3D p6 = new Point3D( 0,    -d2,          thicknessEC); // r d f
            Point3D p7 = new Point3D( 0,    -d2,          0);           // r d n
            Point3D p8 = new Point3D( 0,    lengthLonger, 0);           // r u n

            // Create the paddle
            DetectorPaddle paddle = new DetectorPaddle(paddleId, p1, p2, p3, p4, p5, p6, p7, p8);
            // Flip the directon vector
            paddle.getDirection().setXYZ(0, 0, -1);

            // Rotate it so that its direction vector is antiparallel with 
            // the x-axis in an intermediate coordinate system defined as:
            //    the z-axis is the same as the EC z-axis
            //    the x-axis is parallel with the v-axis
            //    the y-axis is points towards the rest of the triangle
            //    the origin is at the apex of the isosceles triangle
            paddle.rotateZ(Math.toRadians(90));

            // Translate the paddle into position in the triangle in the
            // intermediate coordinate system.
            double gamma = -1/Math.tan(Math.toRadians(180)-2*alphaEC);
            paddle.translateXYZ(yOff*gamma, yOff, calcZEC(layer));

            // Transform the coordinate system from from the intermediate 
            // coordinates to EC coordinates
            paddle.rotateZ(-alphaEC);
            paddle.translateXYZ(0, calcYlEC(layer), 0);
            
            // Add the paddle to the list
            paddles.add(paddle);
            
            if(paddleId == 0) {
                pLL.copy(p7);
                pLR.copy(p8);
                pLL.rotateZ(Math.toRadians(90));
                pLR.rotateZ(Math.toRadians(90));
                pLL.translateXYZ(yOff*gamma, yOff, calcZEC(layer));
                pLR.translateXYZ(yOff*gamma, yOff, calcZEC(layer));
                pLL.rotateZ(-alphaEC);
                pLR.rotateZ(-alphaEC);
                pLL.translateXYZ(0, calcYlEC(layer), 0);
                pLR.translateXYZ(0, calcYlEC(layer), 0);
            }
            if(paddleId == numPaddlesPCalVW-1 ) {
                pUL.copy(p3);
                pUR.copy(p4);
                pLL.rotateZ(Math.toRadians(90));
                pUR.rotateZ(Math.toRadians(90));
                pUL.translateXYZ(yOff*gamma, yOff, calcZEC(layer));
                pUR.translateXYZ(yOff*gamma, yOff, calcZEC(layer));
                pUL.rotateZ(-alphaEC);
                pUR.rotateZ(-alphaEC);
                pUL.translateXYZ(0, calcYlEC(layer), 0);
                pUR.translateXYZ(0, calcYlEC(layer), 0);
            }
        }
        
        Shape3D boundary = new Shape3D();
        boundary.addFace(new Face3D(pLL, pLR, pUL));
        boundary.addFace(new Face3D(pUR, pUL, pLR));
        
        Plane3D plane = new Plane3D(0, 0, 0, 0, 0, -1);
        
        return new ECLayer(sector, 0, 2, paddles, plane, boundary);
    }
    */
}
