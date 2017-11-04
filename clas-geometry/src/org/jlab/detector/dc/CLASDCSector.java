/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.dc;

import java.util.HashMap;

/**
 *
 * @author gavalian
 */
public class CLASDCSector {
    private HashMap<Integer,CLASDCLayer> sectorLayers;
    private Integer dcSector = 0;
    public CLASDCSector(int sector){
        dcSector = sector;
    }
    
    public void addLayer(CLASDCLayer layer){
        
    }
}
