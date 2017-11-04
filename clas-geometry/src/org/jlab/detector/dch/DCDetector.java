/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.dch;

import org.jlab.detector.impl.AbstractDetectorGeometry;
import org.jlab.geom.Plane3D;
import org.jlab.geom.Point3D;
import org.jlab.vector.Vector3D;

/**
 *
 * @author J. Hankins
 */
public class DCDetector extends AbstractDetectorGeometry<DCLayer> {
    protected DCDetector() {
        super("DC");
    }
    
    public Plane3D getMiddlePlane(int sector, int region) {
        if(!(0<=region && region<3))
            throw new IllegalArgumentException("Error: region should be 0, 1, or 2");
        
        Point3D p1 = getLayer(sector, region*2,   5).getMidpoint(0);
        Point3D p2 = getLayer(sector, region*2+1, 0).getMidpoint(0);
        Point3D pM = new Point3D();
        pM.copy(p1);
        pM.combine(p2);
        
        Vector3D n = new Vector3D();
        n.copy(getLayer(sector, region*2, 5).getPlane().normal());
        n.negative();
        
        return new Plane3D(pM, n);
    }
    
    @Override
    protected void addLayer(DCLayer layer) {
        super.addLayer(layer);
    }
}
