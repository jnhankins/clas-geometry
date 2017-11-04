/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom.ui;

import java.awt.Dimension;
import javax.swing.JFrame;
import org.jlab.clas12.dbdata.DataBaseLoader;
import org.jlab.detector.base.ConstantProvider;
import org.jlab.detector.base.DetectorLayer;
import org.jlab.detector.dch.DCDetectorFactory;
import org.jlab.detector.dch.DCLayerFactory;
import org.jlab.geom.Line3D;

/**
 *
 * @author gavalian
 */
public class GeometryFrame extends JFrame {
    private double xmin = -100;
    private double xmax =  100;
    private double ymin = -100;
    private double ymax =  100;
    GeometryPanel panel = null;
    public GeometryFrame(int xsize, int ysize,double xm, double ym){
        initUI(xsize,ysize,xm,ym);
    }
    public void addLineXY(Line3D line){
        panel.addLineXY(line);
    }
    
    private void initUI(int xsize, int ysize, double xm, double ym){
        setTitle("Geometry Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new GeometryPanel(xsize,ysize,xm,ym);
        panel.setSize(xsize,ysize);
        add(panel);
        setSize(xsize,ysize);
        panel.repaint();
        this.setPreferredSize(new Dimension(xsize,ysize));
        setLocationRelativeTo(null); 
        this.pack();
    }
    
    public static void main(String[] args){
        GeometryFrame frame = new GeometryFrame(800,800,250.0,250.0);
        frame.setVisible(true);
        System.err.println("adding lines");
        ConstantProvider data = DataBaseLoader.getDriftChamberConstants();
        DetectorLayer dcLayer = DCLayerFactory.createLayer(data, 0,0,0);
                //.createLayer(data,0,0,0);
        for(int loop = 0; loop < dcLayer.numberOfComponents(); loop++){
            frame.addLineXY(dcLayer.getLine(loop));
        }
        frame.repaint();
        
        DetectorLayer dcLayerR = DCLayerFactory.createLayer(data, 0,1,0);
                //.createLayer(data,0,0,0);
        for(int loop = 0; loop < dcLayerR.numberOfComponents(); loop++){
            frame.addLineXY(dcLayerR.getLine(loop));
        }
        frame.repaint();
        //frame.addLineXY(new Line3D(-300,-300,0,300,300,0));
    }
}
