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
public class CLASDCGeometry {
    private HashMap<Integer,CLASDCSector> dcSectors;
    
    public void initXML(){
        int sector = 0;
        int superlayer = 0;
        int layer = 0;
        
        
        if(dcSectors.containsKey(sector)==false){
            dcSectors.put(sector, new CLASDCSector(sector));            
        }
        
        CLASDCLayer dclayer = new CLASDCLayer(sector,superlayer,layer);
        //layer.initialize(x1, y1, z1, x2, y2, z2);
        dcSectors.get(sector).addLayer(dclayer);
        
    }
}
