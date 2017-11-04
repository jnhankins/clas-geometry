/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.cnd;

import org.jlab.detector.impl.AbstractDetectorGeometry;

/**
 *
 * @author J. Hankins
 */
public class CNDDetector extends AbstractDetectorGeometry<CNDLayer> {
    
    public CNDDetector() {
        super("CND");
    }

    @Override
    protected void addLayer(CNDLayer layer) {
        super.addLayer(layer);
    }
}
