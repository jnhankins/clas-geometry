/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.cnd;

/**
 *
 * @author J. Hankins
 */
public class CLASCNDGeometryProperties {
    /**
     * The inner radius of the CND.
     */
    public static final double r0 = 29;
    
    /**
     * Thickness of each detector layer.
     */
    public static final double dR = 3;
    
    /**
     * The length of the vertical gap between each layer.
     */
    public static final double gl = 0.1;
    
    /**
     * The length of the azimuthal gap between pairs of paddles in a block.
     */ 
    public static final double gp = 0.05;
    
    /**
     * The opening angle of the trapezoidal blocks (not the opening angle of
     * an individual paddle).
     */
    public static final double phi = Math.toRadians(15);
    
    /**
     * Half of the length of the longer base of the trapezoid for the layer.
     */
    public static final double[] tx = { 3.987, 4.395, 4.803 };
    
    /**
     * Half of the length of the shorter base of the trapezoid for the layer.
     */
    public static final double[] bx = { 3.592, 4.0, 4.408 };
    
    /**
     * The offset of the center of paddles for the layer along the z-axis.
     */
    public static final double[] z0 = { 39.596, 38.196, 38.196 };
    
    /**
     * The length of the layer along the z-axis.
     */
    public static final double[] length = { 66.572, 70.0, 73.428 };
}
