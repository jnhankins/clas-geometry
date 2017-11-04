/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.cnd;

import java.util.ArrayList;
import java.util.List;
import org.jlab.detector.base.ConstantProvider;
import static org.jlab.detector.cnd.CLASCNDGeometryProperties.*;
import org.jlab.detector.impl.DetectorPaddle;
import org.jlab.geom.Face3D;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.geom.Shape3D;

/**
 *
 * @author J. Hankins
 */
public class CNDLayerFactory {

    public static CNDLayer createLayer(ConstantProvider cp, int sector, int superlayer, int layer) {
        double widthT = tx[layer];
        double widthB = bx[layer];
        double len = length[layer];
        double z = z0[layer];
        
        List<DetectorPaddle> paddles = new ArrayList();
        
        // Start the component # at 47 since the left paddle of the 0th 
        // block is component #47, then wrap back to 0 as soon as the left
        // paddle of the 0th block is added.
        int component = 47;
        for (int block=0; block<24; ++block) {
            // Left Paddle, with right edge at x = 0
            Point3D p1L = new Point3D(widthT,  0,    dR*0.5); // + + + 
            Point3D p2L = new Point3D(widthT, -len,  dR*0.5); // + - +
            Point3D p3L = new Point3D(widthB, -len, -dR*0.5); // + - -
            Point3D p4L = new Point3D(widthB,  0,   -dR*0.5); // + + -
            Point3D p5L = new Point3D(0,       0,    dR*0.5); // - + +
            Point3D p6L = new Point3D(0,      -len,  dR*0.5); // - - +
            Point3D p7L = new Point3D(0,      -len, -dR*0.5); // - - -
            Point3D p8L = new Point3D(0,       0,   -dR*0.5); // - + -
            DetectorPaddle lPaddle = new DetectorPaddle(component, p1L, p2L, p3L, p4L, p5L, p6L, p7L, p8L);
            component = block==0? 0 : component+1;
            // Right Paddle, with left edge at x = 0
            Point3D p1R = new Point3D(0,        0,    dR*0.5); // + + + 
            Point3D p2R = new Point3D(0,       -len,  dR*0.5); // + - +
            Point3D p3R = new Point3D(0,       -len, -dR*0.5); // + - -
            Point3D p4R = new Point3D(0,        0,   -dR*0.5); // + + -
            Point3D p5R = new Point3D(-widthT,  0,    dR*0.5); // - + +
            Point3D p6R = new Point3D(-widthT, -len,  dR*0.5); // - - +
            Point3D p7R = new Point3D(-widthB, -len, -dR*0.5); // - - -
            Point3D p8R = new Point3D(-widthB,  0,   -dR*0.5); // - + -
            DetectorPaddle rPaddle = new DetectorPaddle(component, p1R, p2R, p3R, p4R, p5R, p6R, p7R, p8R);
            component = component+1;
            
            // Move the paddles into position relative to eachother
            lPaddle.translateXYZ( gp/2, 0, 0);
            rPaddle.translateXYZ(-gp/2, 0, 0);
            // Rotate the paddles to be parallel with the beam
            lPaddle.rotateX(Math.toRadians(-90));
            rPaddle.rotateX(Math.toRadians(-90));
            // Translate the paddles up to their proper radial distance and
            // along the z-axis
            double r = r0 + (dR + gl) * layer + dR/2;
            lPaddle.translateXYZ(0, r, -z);
            rPaddle.translateXYZ(0, r, -z);
            // Rotate the paddles into their final position
            double theta = block*phi - Math.toRadians(90);
            lPaddle.rotateZ(theta);
            rPaddle.rotateZ(theta);
            
            // Add the paddles to the list
            paddles.add(lPaddle);
            paddles.add(rPaddle);
        }
        
        
        Plane3D frontPlane = new Plane3D(0, 0, z, 0, 0, -1);
        Plane3D backPlane  = new Plane3D(0, 0, z-len, 0, 0, -1);
        
        Shape3D boundary = new Shape3D();
        for (int block=0; block<24; ++block) {
            Plane3D plane0 = paddles.get(block*2).getPlane();
            Plane3D plane1 = paddles.get(((block+1)*2)%paddles.size()).getPlane();
            Plane3D plane2 = paddles.get(((block+2)*2)%paddles.size()).getPlane();
            Point3D p0 = Plane3D.intersection(plane0, plane1, frontPlane);
            Point3D p1 = Plane3D.intersection(plane0, plane1, backPlane);
            Point3D p2 = Plane3D.intersection(plane1, plane2, frontPlane);
            Point3D p3 = Plane3D.intersection(plane1, plane2, backPlane);
            Face3D f1 = new Face3D(p0, p1, p2);
            Face3D f2 = new Face3D(p3, p2, p1);
            boundary.addFace(f1);
            boundary.addFace(f2);
        }
        
        return new CNDLayer(0, 0, layer, paddles, null, boundary);
    }
}