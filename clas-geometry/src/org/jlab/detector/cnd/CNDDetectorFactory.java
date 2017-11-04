/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.cnd;

import org.jlab.detector.base.ConstantProvider;

/**
 *
 * @author J. Hankins
 */
public class CNDDetectorFactory {
    
    public static CNDDetector createDetector(ConstantProvider cp) {
        CNDDetector detector = new CNDDetector();
        
        for (int layerId=0; layerId<3; layerId++) {
            CNDLayer layer = CNDLayerFactory.createLayer(cp, 0, 0, layerId);
            detector.addLayer(layer);
        }
        
        return detector;
    }
}
