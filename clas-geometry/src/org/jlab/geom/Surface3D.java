/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom;

/**
 *
 * @author gavalian
 */
public class Surface3D {
    private Face3D  face_right = new Face3D();
    private Face3D  face_left  = new Face3D();
    
    public Surface3D(Face3D face1, Face3D face2){
        face_right.set(
                face1.point(0).x(),face1.point(0).y(),face1.point(0).z(),
                face1.point(1).x(),face1.point(1).y(),face1.point(1).z(),
                face1.point(2).x(),face1.point(2).y(),face1.point(2).z()
                );
        face_left.set(
                face2.point(0).x(),face2.point(0).y(),face2.point(0).z(),
                face2.point(1).x(),face2.point(1).y(),face2.point(1).z(),
                face2.point(2).x(),face2.point(2).y(),face2.point(2).z()
                );
    }    
}
