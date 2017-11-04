/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *

package org.jlab.detector.ft;

import java.util.HashMap;
import org.jlab.detector.impl.AbstractCLASDetectorGeometry;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.impl.DetectorPaddle;
import org.jlab.detector.base.DetectorTypes;
import static org.jlab.detector.ft.CLASFTGeometryProperties.*;
import org.w3c.dom.Document;

/**
 *
 * @author J. Hankins
 *
public class CLASFTDetector extends AbstractCLASDetectorGeometry {
    
    @Override
    public String getName() {
        return "FTCAL";
    }
    
    @Override
    protected HashMap<DetectorDescriptor, DetectorPaddle> initDetectorMap() {
        HashMap<DetectorDescriptor, DetectorPaddle> detectorMap = new HashMap<>();
        
        // Detectors are arranged in a 22x22 grid. In the following array a 1
        // denotes the presence of a detector while a 0 denotes an absence.
        final char[][] geom = new char[][] {
        //  11 10  9  8  7  6  5  4  3  2  1 -1 -2 -3 -4 -5 -6 -7 -8 -9-10-11
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0}, // 11 00
            {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0}, // 10 01
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0}, //  9 02
            {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0}, //  8 03
            {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0}, //  7 04
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0}, //  6 05
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0}, //  5 06
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0}, //  4 07
            {1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1}, //  3 08
            {1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1}, //  2 09
            {1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1}, //  1 10
            {1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1}, // -1 11
            {1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1}, // -2 12
            {1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1}, // -3 13
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0}, // -4 14
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0}, // -5 15
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0}, // -6 16
            {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0}, // -7 17
            {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0}, // -8 18
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0}, // -9 19
            {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0}, //-10 20
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0}, //-11 21
        };
        
        for (int row=0; row<22; ++row) {
            for (int col=0; col<22; ++col) {
                if (geom[row][col] == 1) {
                    int componentId = row*22+col;
                    int idX = col<11 ? 11-col : 10-col;
                    int idY = row<11 ? 11-row : 10-row;                    
                    double x = Vwidth*(Math.abs(idX)-0.5)*Math.signum(idX);
                    double y = Vwidth*(Math.abs(idY)-0.5)*Math.signum(idY);
                    double z = Cfront + Clength/2;
                    
                    DetectorPaddle paddle = new DetectorPaddle(Cwidth, Cwidth, Clength);
                    paddle.getDirection().setXYZ(0, 0, 1);
                    paddle.getLine().rotateX(Math.toRadians(90.0));
                    paddle.translateXYZ(x, y, z);
                    paddle.getMidpoint().set(x, y, z);
                    
                    DetectorDescriptor desc = new DetectorDescriptor(
                            DetectorTypes.FTCAL.id(), 0, 0, 0, componentId);
                    
                    detectorMap.put(desc, paddle);
                }
            }
        }
        return detectorMap;
    }

    @Override
    protected HashMap<DetectorDescriptor, DetectorPaddle> initDetectorMapFromXML(Document doc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
*/