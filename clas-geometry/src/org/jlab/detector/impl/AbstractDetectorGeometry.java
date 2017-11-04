/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.base.DetectorGeometry;
import org.jlab.detector.base.DetectorHit;
import org.jlab.detector.base.DetectorLayer;
import org.jlab.geom.Path3D;

/**
 *
 * @author J. Hankinks
 * @param <LayerType> The specific type of layer this detector uses.
 */
public abstract class AbstractDetectorGeometry<LayerType extends DetectorLayer> 
        implements DetectorGeometry<LayerType> {
    private final String detectorName;
    private final HashMap<Integer,LayerType> layers;
    
    protected AbstractDetectorGeometry(String name) {
        if (name == null)
            throw new IllegalArgumentException("Error: name cannot be null");
        this.detectorName = name;
        this.layers = new HashMap();
    }
    
    protected void addLayer(LayerType layer) {
        if (layer == null)
            throw new IllegalArgumentException("Error: layer cannot be null");
        int sectorId = layer.getSectorId();
        int superlayerId = layer.getSuperLayerId();
        int layerId = layer.getLayerId();
        
        if (sectorId < 0 || sectorId>=6)
            throw new IllegalArgumentException("Error: layer's sectorId should be in range [0,5], but layer.getSectorId()="+sectorId);
        if (superlayerId < 0)
            throw new IllegalArgumentException("Error: layer.getSuperLayerId() cannot be negative");
        if (layerId < 0)
            throw new IllegalArgumentException("Error: layer.getLayerId() cannot be negative");
        
        int hash = DetectorDescriptor.getHashCode(0, sectorId, superlayerId, layerId, 0);
        layers.put(hash, layer);           
    }

    @Override
    public String getName() {
        return detectorName;
    }

    @Override
    public ArrayList<DetectorHit> getLayerHits(Path3D path) {
        ArrayList<DetectorHit> hits = new ArrayList();
        for (DetectorLayer layer : layers.values()) {
            DetectorHit hit = layer.getLayerHit(path);
            if (hit != null) {
                hits.add(hit);
            }
        }
        return hits;
    }

    @Override
    public ArrayList<DetectorHit> getHits(Path3D path) {
        ArrayList<DetectorHit> hits = new ArrayList();
        for (DetectorLayer layer : layers.values())
            hits.addAll(layer.getHits(path));
        return hits;
    }

    @Override
    public LayerType getLayer(int sector, int superlayer, int layer) {
        LayerType lay = layers.get(DetectorDescriptor.getHashCode(0, sector, superlayer, layer, 0));
        if (lay == null)
            System.out.println("Warning: No such layer: sector="+sector+" superlayer="+superlayer+" layer="+layer);
        return lay;
    }
    
    @Override
    public void show() {
        List<LayerType> layerList = new ArrayList(layers.values());
        Collections.sort(layerList, new Comparator<LayerType>(){
            @Override
            public int compare(LayerType a, LayerType b) {
                int dif;
                dif = a.getSectorId()-b.getSectorId();
                if(dif!=0) return dif;
                dif = a.getSuperLayerId()-b.getSuperLayerId();
                if(dif!=0) return dif;
                dif = a.getLayerId()-b.getLayerId();
                return dif;
            }
        });
        for (DetectorLayer layer : layerList) {
            layer.show();
        }
    }
}