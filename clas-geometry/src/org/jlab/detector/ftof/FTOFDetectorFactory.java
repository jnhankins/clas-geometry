/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ftof;

import java.util.ArrayList;
import java.util.List;
import org.jlab.detector.base.ConstantProvider;
import org.jlab.geom.Transformation3D;

/**
 *
 * @author J. Hankins
 */
public class FTOFDetectorFactory {
    
    public static FTOFDetector createDetector(ConstantProvider cp) {
        FTOFDetector detector = new FTOFDetector();
        
        for (int superlayer=0; superlayer<3; superlayer++) {
            
            String layerStr = null;
            if      (superlayer == 0) layerStr = "panel1a";
            else if (superlayer == 1) layerStr = "panel1b";
            else if (superlayer == 2) layerStr = "panel2";
            
            double thtilt           = Math.toRadians(cp.getDouble("/geometry/ftof/"+layerStr+"/panel/thtilt", 0)); 
            double thmin            = Math.toRadians(cp.getDouble("/geometry/ftof/"+layerStr+"/panel/thmin", 0)); 
            double dist2edge        = cp.getDouble("/geometry/ftof/"+layerStr+"/panel/dist2edge", 0);
            
            double dx = dist2edge*Math.sin(thmin);
            double dz = dist2edge*Math.cos(thmin);
            
            Transformation3D trans = new Transformation3D();
            trans.rotateY(thtilt);
            trans.translateXYZ(dx, 0, dz);
            
            for (int sector=0; sector<6; sector++) {
                
                FTOFLayer layer = FTOFLayerFactory.createLayer(cp, sector, superlayer, 0);
                
                layer.setTransformation(trans.copy().rotateZ(Math.toRadians(60*sector)));
                
                detector.addLayer(layer);
            }
        }
        
        return detector;
    }    
}
