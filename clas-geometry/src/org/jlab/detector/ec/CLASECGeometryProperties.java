/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.ec;

/**
 *
 * @author J. Hankins
 */
public class CLASECGeometryProperties {
    /**
     * The total number of scintillator paddles.
     */
    public static final int numPaddlesPCalU = 68;
    public static final int numPaddlesPCalVW = 62;
    public static final int numPaddlesEC = 36;
    
    /**
     * For the u-view, paddles #1-52 (the shortest paddles) are each 
     * individually attached to a single PMT, while paddles #53-68 (the 
     * longer paddles) are grouped into adjacent pairs, and each pair is 
     * attached a single PMT.
     */
    public static final int numSinglePaddlesPCalU = 52;
    public static final int numDoublePaddlesPCalU = 16;
    
    /**
     * For the v- and w- views, paddles #1-15 (the shortest paddles) are 
     * grouped into adjacent pairs, and each pair is attached a single PMT,
     * while paddles #16-62 (the longer paddles) are each individually 
     * attached to a single PMT.
     */ 
    public static final int numDoublePaddlesPCalVW = 15;
    public static final int numSinglePaddlesPCalVW = 47;
    
    /**
     * The design width of each paddle.
     */
    public static final double wPCal =  4.5; //cm
    public static final double w0ECU = 10.3655;
    public static final double dwECU = 0.02476;
    public static final double w0ECVW = 9.4701;
    public static final double dwECVW = 0.02256;
    
    /**
     * The design thickness of each layer.
     */
    public static final double thicknessPCal = 1.0; //cm
    public static final double thicknessEC   = 1.2381;
    
    /**
     * The y coordinate of the longest edge of the longest paddle in the PCAL
     * coordinate system.
     */
    public static final double yhPCal = 94.4; // cm
    public static final double yh0EC = 189.956;
    public static final double dyhEC = 0.45419;
    
    /**
     * The y coordinate of the apex of the isosceles triangle in the PCAL/EC
     * coordinate system.
     */
    public static final double ylPCal = -290.8; // cm
    public static final double yl0EC = -182.974;
    public static final double dylEC = -0.43708;
    
    /**
     * The base angle of the isosceles triangle.
     */
    public static final double alphaPCal = Math.toRadians(62.9);
    public static final double alphaEC = Math.toRadians(62.9);
    
    
    /**
     * The length of the longest paddle.
     */
    public static final double L1PCalU  = 394.2; // cm (aka "b")
    public static final double L1PCalVW = 432.7; // cm
    
    /**
     * The angle between the detector's local coordinate system's z-axis and the
     * sector coordinate system's z-axis.
     */
    public static final double betaPCal = Math.toRadians(25);
    public static final double thetaEC = Math.toRadians(25);
    
    /**
     * The x-offset between PCAL and sector coordinates.
     */
    public static final double x0PCal = 294.9;
    
    /**
     * The y-offset between PCAL and sector coordinates.
     */
    public static final double y0PCal = 0;
    
    /**
     * The z-offset between PCAL and sector coordinates.
     */
    public static final double z0PCal = 632.4;
    
    /**
     * The distance between the origin of the EC coordinate system and the
     * target.
     */
    public static final double L0EC = 510.32;    
    
    /**
     * The amount that the longer edge of the rectangular end the paddle sticks
     * out beyond the triangle.
     */
    public static final double d2innerEC = 3.64;
    public static final double d2outerEC = 2.54;
}
