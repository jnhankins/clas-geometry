/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.geom.detector;

import org.jlab.geom.Shape3D;
import org.jlab.geom.ShapeGeometry;

/**
 *
 * @author gavalian
 */
public class CLASFTOFGeometry extends CLASDetectorGeometry {

    public CLASFTOFGeometry(){
        super("FTOF_1a");
    }
    
    @Override
    public void initGeometry() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //if(this.getDouble("geometry/ftof/panel1a/panel/paddlewidth")!=null&&
        //this.getDouble("FTOF/Panel1a/Y")!=null&&
        //        this.getDouble("FTOF/Panel1a/L")!=null){
        //    calculateShapes();
        //} else {
        //    System.err.println("[FTOF:Geometry] ---> ERROR. variables [FTOF/Panel1a/X]"
        //    + " [FTOF/Panel1a/Y] " + " [FTOF/Panel1a/L] required but missing.");
        //    this.show();
        //}
        this.calculateShapes();
    }
    
    void calculateShapes(){        
       for(int loop = 0; loop < 6; loop++) 
           this.calculateShapesForSector(loop);
    }
    
    void calculateShapesForSector(int sector){
        double[] paddlewidth    = this.getDouble("geometry/ftof/panel1a/panel/paddlewidth");
        double[] paddlethickness = this.getDouble("geometry/ftof/panel1a/panel/paddlethickness");
        double[] thmin          = this.getDouble("geometry/ftof/panel1a/panel/thmin");
        double[] thtilt         = this.getDouble("geometry/ftof/panel1a/panel/thtilt");
        double[] gap            = this.getDouble("geometry/ftof/panel1a/panel/gap");        
        double[] Length = this.getDouble("geometry/ftof/panel1a/paddles/Length");
        double[] Slope  = this.getDouble("geometry/ftof/panel1a/paddles/Slope");
        
        int nrows = Length.length;
        double R = 724.0;
        
        double ThetaMin  = Math.toRadians(thmin[0]);
        double ThetaTilt = Math.toRadians(thtilt[0]);
        double rotationAngle = Math.toRadians(60.0*sector);
        
        for(int loop = 0; loop < nrows; loop++){
            
            Shape3D scintilator = ShapeGeometry.getBox(
                    //Length[loop],
                    paddlethickness[0], 
                    Length[loop]/2.0,
                    paddlewidth[0]);
            
            double xpos = R*Math.sin(ThetaMin) + (
                    (loop)*(paddlethickness[0]+gap[0]) + 
                    0.5*paddlethickness[0])*Math.cos(ThetaTilt);
            double ypos = 0.0;
            double zpos = R*Math.cos(ThetaMin) - ( loop*(paddlethickness[0]+gap[0]) 
                    + 0.5*paddlethickness[0])*Math.sin(ThetaTilt);
                    //+ 0.5*paddlethikness[0]) * Math.sin(thtilt[0])
            //System.out.println("paddle = " + loop + " x = " + xpos + " z = " + zpos);
            //xpos = (paddlewidth[0] + 10.*gap[0])*loop ;
            //System.out.println("---> \t paddle = " + loop + " x = " + xpos + " z = " + zpos);
            scintilator.moveTo(xpos,0.0, zpos);
            scintilator.rotateZ(rotationAngle);
            //if(sector==0) scintilator.setColor(255, 0, 0);
            //scintilator.moveTo(xpos, ypos, zpos);
            this.addShape(scintilator);
        }
    }
}
