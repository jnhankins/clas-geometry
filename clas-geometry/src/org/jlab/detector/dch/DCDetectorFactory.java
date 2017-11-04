/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.dch;

import org.jlab.detector.base.ConstantProvider;
import org.jlab.geom.Transformation3D;

/**
 *
 * @author J. Hankins
 */
public class DCDetectorFactory {
    
    
    public static DCDetector createTiltedDetector(ConstantProvider cp) {
        DCDetector detector = new DCDetector();
        for (int superlayerId=0; superlayerId<6; superlayerId++) {
            for (int layerId=0; layerId<6; layerId++) {
                for (int sectorId=0; sectorId<6; sectorId++) {
                    DCLayer layer = DCLayerFactory.createLayer(cp, sectorId, superlayerId, layerId);
                    detector.addLayer(layer);
                }
            }
        }
        return detector;
    }
    
    public static DCDetector createDetector(ConstantProvider cp) {
        DCDetector detector = new DCDetector();
        
        for (int superlayerId=0; superlayerId<6; superlayerId++) {
            for (int layerId=0; layerId<6; layerId++) {

                double thtilt    = Math.toRadians(cp.getDouble("/geometry/dc/region/thtilt", superlayerId/2));

                Transformation3D trans = new Transformation3D();
                trans.rotateY(thtilt);

                for (int sectorId=0; sectorId<6; sectorId++) {

                    DCLayer layer = DCLayerFactory.createLayer(cp, sectorId, superlayerId, layerId);

                    layer.setTransformation(trans.copy().rotateZ(Math.toRadians(60*sectorId)));

                    detector.addLayer(layer);
                }
            }
        }
        
        return detector;
    }    
}
