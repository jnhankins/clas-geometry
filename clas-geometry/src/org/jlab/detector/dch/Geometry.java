package org.jlab.detector.dch;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.util.ArrayList;
import java.util.List;
import org.jlab.geom.Point3D;

//import org.jMath.Vector.threeVec;
//
//import trackfitter.surface.Plane;


public class Geometry {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// From:
	// mestayer - Jan. 14, 2008 -
	// update - Feb. 6 - extend the outputs to include
	// parameters of the endplates and top and bottom plates of
	// the chambers as well as the gas-bag planes
	// update - Feb. 27 - making subroutines for common tasks
	// like finding the intersection of a line and a plane
	// update - Mar. 4 - extending calculations to include the
	// parameters of the "mother volume" which is essentially
	// the shape of one region's dc "box"
	// update - June 18 - cleaning up things due to my switch from
	//  6 layers (sense only) to 8 layer (guards and sense)
	// update - July 21 - adding explanation about generalized trapezoid
	//  layer volumes
	// update - September 3, 2008 - add "mini-stagger"
	//
	//_
	//      units are cm, deg, coordinate system is the "internal
	//      dc coordinate system", as defined in dc_geometry12.latex,
	//      cartesian coord. system: z along beam axis, x outward in
	//      sector mid-plane
	//
	//
	//  the essential 7 parameters (with 6 values each for the 6 s.l.'s)
	//  (definitions and explanations given below)
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	static final double[] ministagger = new double[6];
	static final double[] rlyr = new double[6];
	static final double[] thopen = new double[6];
	public static final double[] thtilt = new double[6];
	public static final double[] thster = new double[6];
	static final double[] thmin = new double[6];
	static final double[] d = new double[6];
	static final double[] xe = new double[6];
	static double r;
	static double[] frontgap = new double[3];
	static double[] midgap = new double[3];
	static double[] backgap = new double[3];
	double[] rfront = new double[3];
	double[] rback = new double[3];
	static double[] regthickness = new double[3];
	//  indices to wires, planes, superlayers, layers
	int i;
	static int iwir;
	int iplane;
	static int ireg;
	static int isup;
	static int ilayer;
	static int isup1;
	static int isup2;
	static int ifirst;
	//  guard wire "marked" in a superlayer, corresponding to thmin
	static int[] ifirstwir = new int[6];
	static double sup;
	static double layer;
	static double wir;
	//  direction cosines of wire plane normals and wire directions
	static double[] cplane = new double[3];
	static double[] cwire = new double[3];
	//  a test parameter, equals zero if a certain point is in a certain plane
	double plane;
	//  dir. cosines of normals to rh and lh endplates
	static double[] crhplate = new double[3];
	static double[] clhplate = new double[3];
	static double[]  xplates = new double[3];
	double r1dist;
	static double x0mid;
	static double y0mid;
	static double z0mid;
	static double x1mid;
	static double y1mid;
	static double z1mid;
	double numxr,numyr,numzr,numxl,numyl,numzl;
	double denomxr,denomyr,denomzr,denomxl,denomyl,denomzl;
	static double dist1mid;
	static double dw;
	static double dw2;
	static double[][] xmid = new double[3][114];
	static double[] angmid = new double[114];
	double[][] xcenter = new double[3][114];
	double[][] wirespan = new double[6][8];
	double[][] lyrangle = new double[6][8];
	double angle;
	//double[] xpoint1 = new double[3];
	//double[] xpoint2 = new double[3];
	//double[] xpoint3 = new double[3];
	//double wir2wir;
	//public static final double[] xmidwire = new double[3];
	//public static final double[][] xright = new double[3][114];
	//public static final double[][] xleft = new double[3][114];
	//public static final double[] xrightwire = new double[3];
	//public static final double[] xleftwire = new double[3];
	//double wirelenr,wirelenl;
	double[] wirelength = new double[114];
	static double[] layerthickness = new double[6];
	static double lyrthick;
	//static double degrad;
	double pi;
	//double c,s,x,y,z,xrtpl,yrtpl,zrtpl,xlfpl,ylfpl,zlfpl;
	//double delx1,delx2,dely,xx1,xx2,xz1,xz2;
	//public static final double[][] delx1lyr = new double[6][8];
	//public static final double[][] delx2lyr = new double[6][8];
	//public static final double[][] delylyr = new double[6][8];
	//public static final double[][] center_m = new double[3][3];
	//double dx,dy,dz;
	//public static final double[][][] xlyr = new double[3][6][8];
	//public static final double[][][] delxlyr = new double[3][6][8];
	static double ctilt;
	static double stilt;
	static double cster;
	//double sum1, sum2;
	static double sster;

	// wire x at midplane  in tilted coord syst            [sl][l][wi]
	public static final double[][][] XMIDPLANE = new double[6][6][112];
	public static final double[][][] YMIDPLANE = new double[6][6][112];
	public static final double[][][] ZMIDPLANE = new double[6][6][112];

	// wire direction in tilted coord syst                [sl][l][wi]
	public static final double[][][] XWIREDIR = new double[6][6][112];
	public static final double[][][] YWIREDIR = new double[6][6][112];
	public static final double[][][] ZWIREDIR = new double[6][6][112];

	public static final double[][] NORMALDISTTOPLANE = new double[6][6];
//	public static final List<Plane> DCTILTEDPLANES = new ArrayList<Plane>(36);
//	public static final List<Plane> DCTILTEDMIDDLPLANES = new ArrayList<Plane>(3);
	
	
	public static boolean isGeometryLoaded = false;


	public static synchronized void Load() {

		if (isGeometryLoaded) return;

		//--------------------------------------------------------------------------
		// Here is where we load in the values of the essential geometry parameters
		//--------------------------------------------------------------------------
		// ministagger is the distance that the sense wire is moved to the "right" or to
		// the "left" relative to its symmetric position wrt the six surrounding field wires
		ministagger[0] = 0.;
		ministagger[1] = 0.;
		ministagger[2] = 0.;
		ministagger[3] = 0.;
		//ministagger[4] =  0.030;
		//ministagger[5] = -0.030;
		ministagger[4] = 0.;
		ministagger[5] = 0.;

		// rlyr is the distance from the target to the first guard wire plane in each superlayer
		rlyr[0] = 228.08;
		rlyr[1] = 0.;
		rlyr[2] = 348.09;
		rlyr[3] = 0.;
		rlyr[4] = 450.;
		rlyr[5] = 0.;

		// frontgap is the distance between the upstream gas bag and the
		// first guard wire layer (assuming a flat gas bag!) - for each region
		frontgap[0] = 2.5;
		frontgap[1] = 2.5;
		frontgap[2] = 2.5;

		// midgap is the distance between the last guard wire layer of one superlayer
		// and the first guard wire layer of the next superlayer - for each region
		midgap[0] = 2.5;
		midgap[1] = 7.0;
		midgap[2] = 2.5;

		// backgap is the distance between the last guard wire layer of a region and
		// the downstream gas bag - for each region
		backgap[0] = 2.5; backgap[1] = 2.5; backgap[2] = 2.5;
		// thopen is the opening angle between endplate planes in each superlayer
		thopen[0] = 59.;
		thopen[1] = 59.;
		thopen[2] = 60.;
		thopen[3] = 60.;
		thopen[4] = 59.;
		thopen[5] = 59.;

		// thtilt is the tilt angle (relative to z) of the six superlayers
		thtilt[0] = 25.;
		thtilt[1] = 25.;
		thtilt[2] = 25.;
		thtilt[3] = 25.;
		thtilt[4] = 25.;
		thtilt[5] = 25.;

		// thster is the stereo angle of the wires in the six superlayers
		// note: the is the angle of rotation about the normal to the wire plane
		thster[0] = -6.;
		thster[1] = 6.;
		thster[2] = -6.;
		thster[3] = 6.;
		thster[4] = -6.;
		thster[5] = 6.;

		// thmin is the polar angle to the first guard wire's "mid-point" where the
		// wire mid-point is the intersection of the wire with the chamber mid-plane
		thmin[0] = 4.55;
		thmin[1] = 4.55;
		thmin[2] = 4.55;
		thmin[3] = 4.55;
		thmin[4] = 4.5;
		thmin[5] = 4.899;

		// d is the distance between wire planes
		d[0] = 0.3862;
		d[1] = 0.4042;
		d[2] = 0.6219;
		d[3] = 0.6586;
		d[4] = 0.86;
		d[5] = 0.9;

		// xe is the distance between the line of intersection of the two endplate
		// planes and the beam line (NOTE: this intersection line is parallel to the beamline)
		xe[0] = 7.2664;
		xe[1] = 7.2664;
		xe[2] = 16.2106;
		xe[3] = 16.2106;
		xe[4] = 7.2664;
		xe[5] = 7.2664;

		// first "marked" wire in a guard layer; corresponding to thmin; because wires
		// can be too short or too close to the nose plate, this is the first wire
		// actually strung; i.e. if ifirstwir=2, it means that the layer starts from
		// wire 2, and not from wire 1
		ifirstwir[0] = 1;
		ifirstwir[1] = 1;
		ifirstwir[2] = 1;
		ifirstwir[3] = 1;
		ifirstwir[4] = 1;
		ifirstwir[5] = 1;

		//degrad = 57.2957795130823229;
		ifirst= 0;
        
		//-------------------------------------------------------------------------
		// NOW, CALCULATE THE OUTER CHAMBER BOUNDARIES
		//
		// calculate the distance from the target to the first guard wire plane of the
		// second superlayer in each chamber from the gap values
		//
		for(ireg=1; ireg<=3; ireg++) {
			isup1=2*ireg-1;
			isup2=isup1+1;
			rlyr[isup2-1]=rlyr[isup1-1]+21.*d[isup1-1]+midgap[ireg-1];
			layerthickness[isup1-1] = 3.*d[isup1-1];
			layerthickness[isup2-1] = 3.*d[isup2-1];
			regthickness[ireg-1] = frontgap[ireg-1] + midgap[ireg-1] + backgap[ireg-1]+21.*d[isup1-1]+21.*d[isup2-1];
		}

		// LOOP OVER SUPERLAYERS

		for(isup=1; isup<=6; isup++) {
			sup=(double)isup;

			// calculate some commonly used expressions
			ctilt=cos(Math.toRadians(thtilt[isup-1]));
			stilt=sin(Math.toRadians(thtilt[isup-1]));
			cster=cos(Math.toRadians(thster[isup-1]));
			sster=sin(Math.toRadians(thster[isup-1]));

			// dw is the characteristic distance between sense wires
			dw = d[isup-1]*4.*cos(Math.toRadians(30.));

			// dw2 is the distance between the wire 'mid-points' which are the
			// intersections of the wires with the chamber mid-plane
			dw2=dw/cster;

			// calculate wire direction cosines
			cwire[0] = -sster*ctilt;
			cwire[1] = cster;
			cwire[2] = sster*stilt;

			// calculate direction cosines of wire planes
			cplane[0] = stilt;
			cplane[1] = 0.;
			cplane[2] = ctilt;

			// calculate direction cosines of right-hand endplate
			crhplate[0]=sin(Math.toRadians(thopen[isup-1]/2.));
			crhplate[1]=cos(Math.toRadians(thopen[isup-1]/2.));
			crhplate[2]=0.;

			// calculate direction cosines of rleft-hand endplate
			clhplate[0] = sin(Math.toRadians(thopen[isup-1]/2.));
			clhplate[1] = -cos(Math.toRadians(thopen[isup-1]/2.));
			clhplate[2] = 0.;

			// input a common point on the right-hand and left-hand endplate
			// - we have chosen a point at y,z =0; i.e. the x-distance from the
			// beamline to the intersection line of the two endplates
			xplates[0] = xe[isup-1];
			xplates[1] = 0.;
			xplates[2] = 0.;


			// now, calculate the midpoint posn. of the first guard wire in
			// the first guard layer, using the angle, thmin, as defined for
			// the "ifirstwir" guard wire in that superlayer
			// where "mid-point" is the intersection of the first wire w/ the mid-plane
			//
			// What is the FIRST WIRE?
			// X0mid is the position of the first "marked" or "fiducial" wire;
			// the one whose "mid-point" is at a polar angle of THMIN;
			// in the first layer which is a GUARD WIRE LAYER.
			// ifirstwir(sup) is the integer marker of which wire this is
			//r=rlyr[isup-1];
			dist1mid = rlyr[isup-1]/cos(Math.toRadians((thtilt[isup-1]-thmin[isup-1])));
			x0mid = dist1mid*sin(Math.toRadians(thmin[isup-1]))- ((double)ifirstwir[isup-1]-1.)*dw2*ctilt;
			y0mid = 0.;
			z0mid = dist1mid*cos(Math.toRadians(thmin[isup-1])) + ((double)ifirstwir[isup-1]-1.)*dw2*ctilt;
            
            
            
//            if (isup == 1) {
//                Point3D pt = new Point3D(x0mid, 0, z0mid);
//                pt.rotateY(Math.toRadians(-25));
//                System.out.println("g\t"+pt.x()+"\t"+pt.z());
//            }
			
			// calculate the thickness of a geant4 "layer"; where a geant4 layer
			// is 3 layer thicknesses
			lyrthick=layerthickness[isup-1];

			// LOOP OVER LAYERS (8 layers; 1 guard, 6 sense, 1 guard)
			for(ilayer=1; ilayer<=8; ilayer++) {
				layer = (double)ilayer;
				// first, calculate the distance to the layer in question from the
				// first layer
				r=(layer-1.)*3.*d[isup-1];
				// normal dist to layr plane
				if(ilayer>1 && ilayer<8)
					NORMALDISTTOPLANE[isup-1][ilayer-2] = rlyr[isup-1]+r;
				// now, calculate the midpoint posn. of the 1st wire in the layer
				// where "mid-point" is the intersection of the first wire w/ the mid-plane
				x1mid = x0mid+stilt*r;
				y1mid = 0.;
				z1mid = z0mid+ctilt*r;

				// now, put in the "brick-wall" stagger: layer-to-layer;
				// also add the "mini-stagger"
				if(ilayer%2 == 0)
				{
					x1mid = x1mid+0.5*dw2*ctilt+ministagger[isup-1]*ctilt;
					y1mid = 0.;
					z1mid = z1mid-0.5*dw2*stilt-ministagger[isup-1]*stilt;
				}

				// LOOP OVER WIRES (in each layer, 1 guard, 112 sense, 1 guard)
				for(iwir = 1; iwir<=114; iwir++)
				{
					//wir = (double)iwir;

					// xmid[], are the wire "mid-points"
					xmid[0][iwir-1] = x1mid+(double)(iwir-1)*dw2*ctilt;
					xmid[1][iwir-1] = 0.;
					xmid[2][iwir-1] = z1mid-(double)(iwir-1)*dw2*stilt;
                    
                    if ((isup<=4) && (ilayer == 2) && iwir>1 && iwir < 14) {
                        Point3D pt = new Point3D(xmid[0][iwir-1], 0, xmid[2][iwir-1]);
                        pt.rotateY(Math.toRadians(-25));
                        System.out.println(isup+" "+(ilayer-1)+" "+(iwir-1)+"\t"+pt.x()+"\t"+pt.z());
                    }
                    
					if(ilayer>1 && ilayer<8 && iwir>1 && iwir<114)
					{
						XMIDPLANE[isup-1][ilayer-2][iwir-2] = xmid[0][iwir-1];
						YMIDPLANE[isup-1][ilayer-2][iwir-2] = xmid[1][iwir-1];
						ZMIDPLANE[isup-1][ilayer-2][iwir-2] = xmid[2][iwir-1];
						
						// in the tilted coordinate system
						double X = XMIDPLANE[isup-1][ilayer-2][iwir-2];
				        double Z = ZMIDPLANE[isup-1][ilayer-2][iwir-2];
				        double Xr = X*Math.cos(Math.toRadians(-25.))+Z*Math.sin(Math.toRadians(-25.));		
				 		double Zr = Z*Math.cos(Math.toRadians(-25.))-X*Math.sin(Math.toRadians(-25.));
				 		
				 		 
						XMIDPLANE[isup-1][ilayer-2][iwir-2] = Xr;
						ZMIDPLANE[isup-1][ilayer-2][iwir-2] = Zr;
						
						XWIREDIR[isup-1][ilayer-2][iwir-2] = -sster;
						YWIREDIR[isup-1][ilayer-2][iwir-2] = cster;
						ZWIREDIR[isup-1][ilayer-2][iwir-2] = 0;
						
					}
				}
			}
		}

//		int index = 0;
//		// getting the planes at each layer
//		for(int slrIdx = 0; slrIdx<6; slrIdx++) {
//			for(int lrIdx = 0; lrIdx<6; lrIdx++) {
//
//				// using the first wire in the mid plane as a reference point
//				threeVec refPtonPlane =  new threeVec(0,0,NORMALDISTTOPLANE[slrIdx][lrIdx]);
//
//				Plane layerPlane = new Plane(refPtonPlane, new threeVec(0,0,1));
//				DCTILTEDPLANES.add(index, layerPlane);
//				index++;
//			}
//		}
//
//		// getting the planes at the middle plane inbetween the superlayers
//		Plane layerPlaneinReg1 = new Plane(new threeVec(0,0,(NORMALDISTTOPLANE[1][0]+NORMALDISTTOPLANE[0][5])/2.), new threeVec(0,0,1));
//		Plane layerPlaneinReg2 = new Plane(new threeVec(0,0,(NORMALDISTTOPLANE[3][0]+NORMALDISTTOPLANE[2][5])/2.), new threeVec(0,0,1));
//		Plane layerPlaneinReg3 = new Plane(new threeVec(0,0,(NORMALDISTTOPLANE[5][0]+NORMALDISTTOPLANE[4][5])/2.), new threeVec(0,0,1));
//
//		DCTILTEDMIDDLPLANES.add(0, layerPlaneinReg1);
//		DCTILTEDMIDDLPLANES.add(1, layerPlaneinReg2);
//		DCTILTEDMIDDLPLANES.add(2, layerPlaneinReg3);

		// mark the geometry as loaded
		isGeometryLoaded = true;
	}


	public static void main (String arg[]) {
		Geometry.Load();
		 for (int i = 0; i<6; i++) 
			 System.out.println("rlyr[] "+rlyr[i]);
		    // for (int i2 = 0; i2<6; i2++)
		        // System.out.println(Geometry.XMIDPLANE[0][0][19]+"	"+Geometry.ZMIDPLANE[0][0][19]);
		         double X = Geometry.XMIDPLANE[0][0][0];
		         double Y = Geometry.YMIDPLANE[0][0][0];
		         double Z = Geometry.ZMIDPLANE[0][0][0];
		         double Xr = X*Math.cos(Math.toRadians(25.))+Z*Math.sin(Math.toRadians(25.));		
		 		 double Zr = Z*Math.cos(Math.toRadians(25.))-X*Math.sin(Math.toRadians(25.));
		 		System.out.println(Xr+"	"+Y+"	"+Zr);
		 		  X = Geometry.XMIDPLANE[0][1][0];
		          Y = Geometry.YMIDPLANE[0][1][0];
		          Z = Geometry.ZMIDPLANE[0][1][0];
		          Xr = X*Math.cos(Math.toRadians(25.))+Z*Math.sin(Math.toRadians(25.));		
		 		  Zr = Z*Math.cos(Math.toRadians(25.))-X*Math.sin(Math.toRadians(25.));
		 		System.out.println(Xr+"	"+Y+"	"+Zr);
		 		
		 		
	}
}