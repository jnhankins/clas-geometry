/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom;

import java.util.ArrayList;

/**
 *
 * @author J. Hankins
 */
public class Transformation3D {
    private interface Transformation {
        public abstract void forward(Transformable t);
        public abstract void reverse(Transformable t);
    }
    private final ArrayList<Transformation> transformations = new ArrayList();
    
    public Transformation3D copy() {
        Transformation3D trans = new Transformation3D();
        trans.transformations.addAll(transformations);
        return trans;
    }
    
    public Transformation3D translateXYZ(final double x, final double y, final double z) {
        transformations.add(new Transformation() {
            @Override
            public void forward(Transformable t) {
                t.translateXYZ(x, y, z);
            }
            @Override
            public void reverse(Transformable t) {
                t.translateXYZ(-x, -y, -z);
            }
        });
        return this;
    }
    public Transformation3D rotateX(final double angle) {
        transformations.add(new Transformation() {
            @Override
            public void forward(Transformable t) {
                t.rotateX(angle);
            }
            @Override
            public void reverse(Transformable t) {
                t.rotateX(-angle);
            }
        });
        return this;
    }
    public Transformation3D rotateY(final double angle) {
        transformations.add(new Transformation() {
            @Override
            public void forward(Transformable t) {
                t.rotateY(angle);
            }
            @Override
            public void reverse(Transformable t) {
                t.rotateY(-angle);
            }
        });
        return this;
    }
    public Transformation3D rotateZ(final double angle) {
        transformations.add(new Transformation() {
            @Override
            public void forward(Transformable t) {
                t.rotateZ(angle);
            }
            @Override
            public void reverse(Transformable t) {
                t.rotateZ(-angle);
            }
        });
        return this;
    }
    
    public void forwardTransform(Transformable t) {
        for (int i=0; i<transformations.size(); i++)
            transformations.get(i).forward(t);
    }
    public void reverseTransform(Transformable t) {
        for (int i=transformations.size()-1; i>=0; i--)
            transformations.get(i).reverse(t);
    }
}
