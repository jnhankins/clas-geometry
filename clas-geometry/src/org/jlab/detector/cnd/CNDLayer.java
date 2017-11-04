/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.cnd;

import java.util.List;
import org.jlab.detector.impl.AbstractDetectorLayer;
import org.jlab.detector.impl.DetectorPaddle;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Shape3D;

/**
 *
 * @author J. Hankins
 */
public class CNDLayer extends AbstractDetectorLayer {
    public CNDLayer(
            int sector, int superlayer, int layer,
            List<DetectorPaddle> paddles, Plane3D plane, Shape3D boundary) {
        super("CND", sector, superlayer, layer, paddles, plane, boundary, false);
    }
    
    @Override
    public void show() {
        System.out.println(toString());
    }
    
    @Override
    public String toString() {
        return String.format("Layer: %8s %2d %2d %d",
            getDetectorName(),
            getSuperLayerId(),
            getLayerId(),
            getSectorId());
    }
}
