/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ftof;

import org.jlab.detector.impl.AbstractDetectorGeometry;

/**
 *
 * @author J. Hankins
 */
public class FTOFDetector extends AbstractDetectorGeometry<FTOFLayer> {
    protected FTOFDetector() {
        super("FTOF");
    }
    
    @Override
    protected void addLayer(FTOFLayer layer) {
        super.addLayer(layer);
    }
}
