/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ec;

import org.jlab.detector.base.ConstantProvider;
import org.jlab.geom.Transformation3D;

/**
 *
 * @author J. Hankins
 */
public class ECDetectorFactory {
    
    public static ECDetector createDetector(ConstantProvider cp) {
        ECDetector detector = new ECDetector();
        
        final double thtilt = Math.toRadians(cp.getDouble("/geometry/pcal/pcal/thtilt", 0));
        final double dist2tgt = cp.getDouble("/geometry/pcal/pcal/dist2tgt", 0)*0.1;
        
        Transformation3D trans = new Transformation3D();
        trans.rotateZ(Math.toRadians(-90));
        trans.translateXYZ(0, 0, dist2tgt);
        trans.rotateY(thtilt);
        
        for (int sectorId=0; sectorId<6; sectorId++) {
            for (int superlayerId=0; superlayerId<1; superlayerId++) { // TODO adjust bounds to include Inner and Outer EC
                for (int layerId=0; layerId<3; layerId++) {
                    ECLayer layer = ECLayerFactory.createLayer(cp, sectorId, superlayerId, layerId);
                    layer.setTransformation(trans.copy().rotateZ(Math.toRadians(60*sectorId)));
                    detector.addLayer(layer);
                }
            }
        }
        
        return detector;
    }
}
