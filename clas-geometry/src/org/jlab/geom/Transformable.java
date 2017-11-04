/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom;

/**
 *
 * @author J. Hankins
 */
public interface Transformable {
    public void translateXYZ(double dx, double dy, double dz);
    public void rotateX(double angle);
    public void rotateY(double angle);
    public void rotateZ(double angle);
}
