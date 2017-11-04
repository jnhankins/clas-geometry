/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ec;

import org.jlab.detector.impl.AbstractDetectorGeometry;

/**
 *
 * @author J. Hankins
 */
public class ECDetector extends AbstractDetectorGeometry<ECLayer> {
    
    protected ECDetector() {
        super("EC");
    }
    
    @Override
    protected void addLayer(ECLayer layer) {
        super.addLayer(layer);
    }
}