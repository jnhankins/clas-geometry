/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.base;

import java.util.ArrayList;
import org.jlab.geom.Path3D;

/**
 *
 * @author gavalian
 */
public interface DetectorGeometry<LayerType extends DetectorLayer> {
    
    String getName();
    LayerType getLayer(int sector, int superlayer, int layer);
    
    ArrayList<DetectorHit> getLayerHits(Path3D path);
    ArrayList<DetectorHit> getHits(Path3D path);
    
    public void show();
}
