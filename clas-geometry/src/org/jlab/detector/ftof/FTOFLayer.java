/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ftof;

import java.util.List;
import org.jlab.detector.impl.AbstractDetectorLayer;
import org.jlab.detector.impl.DetectorPaddle;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Shape3D;

/**
 *
 * @author J. Hankins
 */
public class FTOFLayer extends AbstractDetectorLayer {
    protected FTOFLayer(
            int sector, int superlayer, int layer,
            List<DetectorPaddle> paddles, Plane3D plane, Shape3D boundary) {
        super("FTOF", sector, superlayer, layer, paddles, plane, boundary, true);
    }
    
    @Override
    public void show() {
        System.out.println(toString());
    }
    
    @Override
    public String toString() {
        return String.format("Layer: %8s %2d %2d %2d (%7.1f %7.1f %7.1f) (%7.1f %7.1f %7.1f) (%7.1f %7.1f %7.1f) (%7.1f %7.1f %7.1f)",
            getDetectorName(),
            getSectorId(),
            getSuperLayerId(),
            getLayerId(),
            getBoundary().face(0).point(0).x(),
            getBoundary().face(0).point(0).y(),
            getBoundary().face(0).point(0).z(),
            getBoundary().face(0).point(1).x(),
            getBoundary().face(0).point(1).y(),
            getBoundary().face(0).point(1).z(),
            getBoundary().face(0).point(2).x(),
            getBoundary().face(0).point(2).y(),
            getBoundary().face(0).point(2).z(),
            getBoundary().face(1).point(0).x(),
            getBoundary().face(1).point(0).y(),
            getBoundary().face(1).point(0).z());
    }
}
