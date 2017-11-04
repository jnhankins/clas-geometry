/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.base;

import org.jlab.geom.Point3D;

/**
 *
 * @author gavalian
 */
public class DetectorHit {
    
    private DetectorDescriptor detectorInfo = new DetectorDescriptor();
    private Point3D            hitPosition  = new Point3D();
    private Point3D            hitError     = new Point3D();
    private double             hitTime      = 0.0;
    private double             hitEnergy    = 0.0;
    
    public DetectorHit(DetectorDescriptor desc, Point3D pos, Point3D error){
        detectorInfo.copy(desc);
        hitPosition.copy(pos);
        hitError.copy(error);
    }
    
    public DetectorDescriptor getDescriptor(){
        return detectorInfo;
    }
    
    public Point3D position(){
        return hitPosition;        
    }
    
    public Point3D error(){
        return hitError;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("%-15s : %s\n", "Detector",
                DetectorTypes.getType(detectorInfo.getDetectorID()).getName()));
        str.append(String.format("%-15s : %d\n", "Detector ID",
                detectorInfo.getDetectorID()));
        str.append(String.format("%-15s : %d\n", "Sector",detectorInfo.getSector()));
        str.append(String.format("%-15s : %d\n", "SuperLayer",detectorInfo.getSuperLayer()));
        str.append(String.format("%-15s : %d\n", "Layer",detectorInfo.getLayer()));
        str.append(String.format("%-15s : %d\n", "Component",detectorInfo.getComponent()));
        str.append(String.format("%-15s : %12.5f %12.5f %12.5f\n", "Position",
                hitPosition.x(),hitPosition.y(),hitPosition.z()));
        str.append(String.format("%-15s : %12.5f %12.5f %12.5f\n", "Error",
                hitError.x(),hitError.y(),hitError.z()));
        str.append(String.format("%-15s : %12.5f\n", "Time (ns)",hitTime));
        str.append(String.format("%-15s : %12.5f\n", "Energy",hitEnergy));
        
        return str.toString();
    }
}
