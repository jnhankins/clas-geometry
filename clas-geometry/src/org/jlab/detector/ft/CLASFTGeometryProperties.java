/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ft;

import org.jlab.detector.base.ConstantProvider;

/**
 *
 * @author J. Hankins
 */
public class CLASFTGeometryProperties implements ConstantProvider {
    /**
     * Nominal width of the crystals.
     */
    public static final double Cwidth = 1.5;
    
    /**
     * Nominal length of the crystals.
     */
    public static final double Clength = 20.0;
    
    /**
     * Distance of the crystal front face from the CLAS12 center.
     */
    public static final double Cfront = 189.8;
    
    /**
     * Width of each crystal assembly.
     */
    public static final double Vwidth = 1.53;
    
//    /**
//     * Air gap between crystals.
//     */
//    public static final double AGap = 0.017;
//    
//    /**
//     * Thickness of the VM2000 wrappings.
//     */
//    public static final double VM2000 = 0.0065;

    @Override
    public boolean hasConstant(String name) {
        return name.compareTo("Cwidth")==0||name.compareTo("Cfront")==0||
                name.compareTo("Clength")==0||name.compareTo("Vwidth")==0;
    }

    @Override
    public int length(String name) {
        if(this.hasConstant(name)) return 1;
        return 0;
    }

    @Override
    public double getDouble(String name, int row) {
        if(this.hasConstant(name)==false||row!=0){
            return 0.0;
        }
        if(name.compareTo("Cwidth")==0) return Cwidth;
        if(name.compareTo("Clength")==0) return Clength;
        if(name.compareTo("Cfromt")==0) return Cfront;
        if(name.compareTo("Vwidth")==0) return Vwidth;
        return 0.0;
    }

    @Override
    public int getInteger(String name, int row) {
        System.err.println("(FTCAL):: does not have any integer constants.");
        return 0;
    }
}
