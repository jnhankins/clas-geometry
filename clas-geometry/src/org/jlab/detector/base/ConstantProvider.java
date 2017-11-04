/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.base;

/**
 * This is an interface for providing constants for geometry objects.
 * @author gavalian
 */
public interface ConstantProvider {
    boolean hasConstant(String name);
    int     length(String name);
    double  getDouble(String name, int row);
    int     getInteger(String name, int row);
}
