package org.jlab.geom.visualizer;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JMenuItem;
import jv.geom.PgElementSet;
import jv.object.PsMainFrame;
import jv.objectGui.PsToolbar;
import jv.project.PvLightIf;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jv.viewer.PvCamera;
import jv.viewer.PvDisplay;
import jv.viewer.PvViewer;
import org.jlab.geom.base.*;
import org.jlab.geom.component.*;
import org.jlab.geom.prim.*;

/**
 *
 * @author jnhankins
 */
public class CLASVisualizer {
    private final PsMainFrame frame;
    private final PvViewer viewer;
    private final PvDisplay disp;
    private final PvCamera camera;
    private final Map<Object,Color> objs;
    private double transparancy;
    private boolean isWireFrame;
    private boolean isComponentLineVisible;
    private boolean isComponentDirectionVisible;
    private boolean isLayerBoundaryVisible;
    private boolean isLayerPlaneVisible;
    private boolean isReaddNeeded;
    private int radialSteps = 20;
    private double radialStepSize = Math.PI*0.5/20;
    private double pointSize = 5;
    
    public CLASVisualizer() {
        this(0, 0, 800, 600);
    }
    public CLASVisualizer(int x, int y, int width, int height) {
        frame   = new PsMainFrame("CLAS Geometry Visualizer") {
            // Don't let the window title change
            @Override public void setTitle(String title) {} 
        };
        viewer  = new PvViewer(null, frame);
        disp    = (PvDisplay)viewer.getDisplay();
        camera  = (PvCamera)disp.getCamera();
        
        objs = new HashMap();
        transparancy = 0;
        isWireFrame = false;
        isComponentLineVisible = false;
        isLayerBoundaryVisible = false;
        isLayerPlaneVisible = false;
        isReaddNeeded = false;
        
        PsToolbar toolbar = (PsToolbar)frame.getComponent(0);
        
        JMenuItem clearM = new JMenuItem("Clear");
        clearM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
                update();
            }
        });
        toolbar.add(clearM);
        
        JMenuItem antialiasingM = new JMenuItem("AA");
        antialiasingM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disp.setEnabledAntiAlias(!disp.isEnabledAntiAlias());
                update();
            }
        });
        toolbar.add(antialiasingM);
        
        JMenuItem incTransM = new JMenuItem("Trans-");
        incTransM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTransparancy(transparancy-0.25);
                update();
            }
        });
        toolbar.add(incTransM);
        
        JMenuItem decTransM = new JMenuItem("Trans+");
        decTransM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTransparancy(transparancy+0.25);
                update();
            }
        });
        toolbar.add(decTransM);
        
        JMenuItem wireM = new JMenuItem("Wire");
        wireM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setWireFrame(!isWireFrame);
                update();
            }
        });
        toolbar.add(wireM);
        
        JMenuItem boundM = new JMenuItem("Surf");
        boundM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLayerBoundaryVisible(!isLayerBoundaryVisible);
                update();
            }
        });
        toolbar.add(boundM);
        
        JMenuItem planeM = new JMenuItem("Plane");
        planeM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLayerPlaneVisible(!isLayerPlaneVisible);
                update();
            }
        });
        toolbar.add(planeM);
        
        JMenuItem lineM = new JMenuItem("Line");
        lineM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setComponentLineVisible(!isComponentLineVisible);
                update();
            }
        });
        toolbar.add(lineM);
        
        JMenuItem dirM = new JMenuItem("Dir");
        dirM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setComponentDirectionVisible(!isComponentDirectionVisible);
                update();
            }
        });
        toolbar.add(dirM);
        
        toolbar.add(Box.createHorizontalGlue());
        
        frame.add((java.awt.Component)disp, BorderLayout.CENTER);
        frame.pack();
        frame.setBounds(x, y, width, height);
        
        disp.showAxes(true); 
        disp.setEnabled3DLook(false);
        disp.setEnabledAntiAlias(true);
        disp.setAutoCenter(false);
        disp.setBackgroundColor(Color.BLACK);
        disp.getAxes().setAxesColor(Color.YELLOW.brighter().brighter());
        disp.getAxes().setLabelColors(Color.WHITE);
        disp.setLightingModel(PvLightIf.MODEL_SURFACE);
        disp.removeLights();
        disp.setEnabledPainters(false);
//        disp.setEnabledClip(true);
//        disp.setNearClip(1E20);
        
//        disp.setAutoRotation(new PdVector(0, 0, 1), 0);
//        disp.setEnabledAnimation(false);
//        disp.setEnabledDoubleBuffer(true);
//        disp.setEnabledInspection(false);
//        disp.setEnabledLocalTransform(false);
//        disp.setEnabledZBuffer(true);
        
        camera.setFullPosition(
                new PdVector(0,0,0), 
                new PdVector(0, 0, -1000), 
                new PdVector(0, 1, 0));
        
        add_(new RectangularComponent(-1,1,1,1), Color.WHITE);
    }
    
    public void clear() {
        objs.clear();
        disp.removeGeometries();
        add_(new RectangularComponent(-1,1,1,1), Color.WHITE);
    }
    
    public boolean isVisible() {
        return frame.isVisible();
    }
    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }
    
    public boolean isAntiAliased() {
        return disp.isEnabledAntiAlias();
    }
    public void setAntiAliased(boolean isAntiAliased) {
        if (disp.isEnabledAntiAlias() != isAntiAliased) {
            disp.setEnabledAntiAlias(isAntiAliased);
        }
    }
    
    public double getTransparancy() {
        return transparancy;
    }
    public void setTransparancy(double alpha) {
        alpha = Math.max(0, Math.min(1, alpha));
        if (this.transparancy != alpha) {
            this.transparancy = alpha;
            isReaddNeeded = true;
        }
    }
    
    public boolean isWireFrame() {
        return isWireFrame;
    }
    public void setWireFrame(boolean isWireFrame) {
        if(this.isWireFrame != isWireFrame) {
            this.isWireFrame = isWireFrame;
            isReaddNeeded = true;
        }
    }
    
    public boolean isComponentLineVisible() {
        return isComponentLineVisible;
    }
    public void setComponentLineVisible(boolean isComponentLineVisible) {
        if(this.isComponentLineVisible != isComponentLineVisible) {
            this.isComponentLineVisible = isComponentLineVisible;
            isReaddNeeded = true;
        }
    }
    
    public boolean isComponentDirectionVisible() {
        return isComponentDirectionVisible;
    }
    public void setComponentDirectionVisible(boolean isComponentDirectionVisible) {
        if(this.isComponentDirectionVisible != isComponentDirectionVisible) {
            this.isComponentDirectionVisible = isComponentDirectionVisible;
            isReaddNeeded = true;
        }
    }
    
    public boolean isLayerBoundaryVisible() {
        return isLayerBoundaryVisible;
    }
    public void setLayerBoundaryVisible(boolean isLayerBoundaryVisible) {
        if(this.isLayerBoundaryVisible != isLayerBoundaryVisible) {
            this.isLayerBoundaryVisible = isLayerBoundaryVisible;
            isReaddNeeded = true;
        }
    }
    
    public boolean isLayerPlaneVisible() {
        return isLayerPlaneVisible;
    }
    public void setLayerPlaneVisible(boolean isLayerPlaneVisible) {
        if(this.isLayerPlaneVisible != isLayerPlaneVisible) {
            this.isLayerPlaneVisible = isLayerPlaneVisible;
            isReaddNeeded = true;
        }
    }
   
    public Color getBackgroundColor() {
        return disp.getBackgroundColor();
    }
    public void setBackgroundColor(Color background) {
        disp.setBackground(background);
    }
    
    public int getNumRadialSteps() {
        return radialSteps;
    }
    public void setNumRadialSteps(int radialSteps) {
        this.radialSteps = radialSteps;
        radialStepSize = Math.PI*0.5/radialSteps;
    }
    
    public double getPointSize() {
        return pointSize;
    }
    public void setPointSize(double pointSize) {
        this.pointSize = pointSize;
    }
    
    
    public PsMainFrame getFrame() {
        return frame;
    }
    public PvViewer getViewer() {
        return viewer;
    }
    public PvDisplay getDisplay() {
        return disp;
    }
    public PvCamera getCamera() {
        return camera;
    }
    public Canvas getCanvas() {
        return disp.getCanvas();
    }
    
    public boolean remove(Object obj) {
        return objs.remove(obj) != null;
    }
    
    public void update() {
        if (isReaddNeeded) {
            disp.removeGeometries();
            add_(new RectangularComponent(-1,1,1,1), Color.WHITE);
            for (Map.Entry<Object,Color> e : objs.entrySet()) {
                Object obj = e.getKey();
                Color color = e.getValue();
                if (obj instanceof Detector) {
                    add_((Detector)obj, color);
                } else if (obj instanceof Sector) {
                    add_((Sector)obj, color);
                } else if (obj instanceof Superlayer) {
                    add_((Superlayer)obj, color);
                } else if (obj instanceof Layer) {
                    add_((Layer)obj, color);
                } else if (obj instanceof Component) {
                    add_((Component)obj, color);
                } else if (obj instanceof Shape3D) {
                    add_((Shape3D)obj, color);
                } else if (obj instanceof Face3D) {
                    add_((Face3D)obj, color);
                } else if (obj instanceof Path3D) {
                    add_((Path3D)obj, color);
                } else if (obj instanceof Line3D) {
                    add_((Line3D)obj, color);
                } else if (obj instanceof Point3D) {
                    add_((Point3D)obj, color);
                } else if (obj instanceof Arc3D) {
                    add_((Arc3D)obj, color);
                } else if (obj instanceof Sector3D) {
                    add_((Sector3D)obj, color);
                } else if (obj instanceof Cylindrical3D) {
                    add_((Cylindrical3D)obj, color);
                }
            }
            isReaddNeeded = false;
        }
        disp.recomputeAxes();
        disp.getCanvas().repaint();
    }
    
    public void add(Detector detector, Color color) {
        objs.put(detector, color);
        add_(detector, color);
    }
    public void add(Sector sector, Color color) {
        objs.put(sector, color);
        add_(sector, color);
    }
    public void add(Superlayer superlayer, Color color) {
        objs.put(superlayer, color);
        add_(superlayer, color);
    }
    public void add(Layer layer, Color color) {
        objs.put(layer, color);
        add_(layer, color);
    }
    public void add(Component component, Color color) {
        objs.put(component, color);
        add_(component, color);
    }
    public void add(Shape3D shape, Color color) {
        objs.put(shape, color);
        add_(shape, color);
    }
    public void add(Face3D face, Color color) {
        objs.put(face, color);
        add_(face, color);
    }
    public void add(Path3D path, Color color) {
        objs.put(path, color);
        add_(path, color);
    }
    public void add(Line3D line, Color color) {
        objs.put(line, color);
        add_(line, color);
    }
    public void add(Point3D point, Color color) {
        objs.put(point, color);
        add_(point, color);
    }
    public void add(Sector3D sector, Color color) {
        objs.put(sector, color);
        add_(sector, color);
    }
    public void add(Cylindrical3D cylind, Color color) {
        objs.put(cylind, color);
        add_(cylind, color);
    }
    public void add(Arc3D arc, Color color) {
        objs.put(arc, color);
        add_(arc, color);
    }
    
    private void add_(Detector<?> detector, Color color) {
        for (Sector sector : detector.getAllSectors()) {
            add_(sector, color);
        }
    }
    private void add_(Sector<?> sector, Color color) {
        Color bright = brighten(color, 120);
        for (Superlayer superlayer : sector.getAllSuperlayers()) {
            if (superlayer.getSuperlayerId()%2 == 0) {
                add_(superlayer, color);
            } else {
                add_(superlayer, bright);
            }
        }
    }
    private void add_(Superlayer<?> superlayer, Color color) {
        Color bright = brighten(color, 60);
        for (Layer layer : superlayer.getAllLayers()) {
            if (layer.getLayerId()%2 == 0) {
                add_(layer, color);
            } else {
                add_(layer, bright);
            }
        }
    }
    private void add_(Layer<?> layer, Color color) {
        for (Component component : layer.getAllComponents()) {
            add_(component, color);
        }
        
        if (isLayerBoundaryVisible) {
            boolean w = isWireFrame;
            isWireFrame = false;
            double a = transparancy;
            transparancy /= 4;
            
            Shape3D boundary = layer.getBoundary();
            if (boundary != null) {
                for (int f=0; f<boundary.size(); f++) {
                    add_(boundary.face(f), color);
                }
            }
            
            isWireFrame = w;
            transparancy = a;
        }
        
        if (isLayerPlaneVisible) {
            
            Plane3D plane = layer.getPlane();
            Point3D point = plane.point();
            Vector3D normal = plane.normal();
            if (!normal.unit()) {
                System.err.println("CLASViewer: add_(Layer<?> layer, Color color): "+layer.getType()+": the layer plane's normal is a null vector");
            } else {
                double a = transparancy;
                transparancy = 0;
                
                final double scale = 50;
                
                normal.scale(scale);
                Point3D p0 = new Point3D(point.x() + normal.x(), point.y() + normal.y(), point.z() + normal.z());
                Point3D p1 = new Point3D();
                
                Vector3D v = new Vector3D();
                Line3D line = new Line3D(point, p0);
                add_(line, Color.WHITE);
                
                v.copy(normal);
                v.negative();
                v.rotateX(Math.toRadians(-20));
                line.set(p0, v);
                plane.intersection(line, p1);
                
                v = point.vectorTo(p1);
                v.unit();
                v.scale(scale);
                
                line.set(point, v);
                line.setOrigin(line.lerpPoint(-1));
                add_(line, Color.WHITE);
                
                v = normal.cross(v);
                
                line.set(point, v);
                line.setOrigin(line.lerpPoint(-1));
                add_(line, Color.WHITE);
                
                transparancy = a;
            }
            
        }
    }
    private void add_(Component component, Color color) {
        if (component instanceof PrismaticComponent) {
            PrismaticComponent prism = (PrismaticComponent)component;
            PgElementSet geom = new PgElementSet(3);
            geom.setGlobalElementColor(color);
            geom.setGlobalEdgeColor(color.darker());

            Shape3D shape = prism.getVolumeShape();
            int npoints = (shape.size()+4)/4;
            for(int p=0; p<prism.getNumVolumePoints(); p++) {
                Point3D pt = prism.getVolumePoint(p);
                geom.addVertex(new PdVector(pt.x(), pt.y(), pt.z()));
            }
            
            if (isWireFrame) {
                for (int i=0; i<npoints; i++) {
                    geom.addElement(new PiVector(i, i, i+npoints));
                    geom.addElement(new PiVector(i, i, (i+1)%npoints));
                    geom.addElement(new PiVector(i+npoints, i+npoints, (i+1)%npoints + npoints));
                }
            } else {
                if (transparancy < 1) {
                    int vecb[] = new int[npoints];
                    int vect[] = new int[npoints];
                    for (int i=0; i<npoints; i++) {
                        geom.addElement(new PiVector(i, i+npoints, (i+1)%npoints+npoints, (i+1)%npoints));
                        vecb[i] = i;
                        vect[npoints-i-1] = i+npoints;
                    }
                    geom.addElement(new PiVector(vecb));
                    geom.addElement(new PiVector(vect));
                    geom.makeElementNormals();
                    geom.showBackface(false);
                    if (transparancy > 0) {
                        geom.setTransparency(transparancy);
                        geom.showTransparency(true);
                    }
                }
            }
            
            disp.addGeometry(geom);
            
            if (isComponentLineVisible) {
                add_(prism.getLine(), Color.WHITE);
            }
        
            if (isComponentDirectionVisible) {
                Point3D p = prism.getLine().end();
                Vector3D v = new Vector3D(prism.getDirection());
                v.unit();
                v.scale(25);
                add_(new Line3D(p, v), Color.WHITE);
            }
            
        } else {
            add_(component.getVolumeShape(), color);
        }
    }
    private void add_(Shape3D shape, Color color) {
        PgElementSet geom = new PgElementSet(3);
        geom.setGlobalElementColor(color);
        geom.setGlobalEdgeColor(color.darker());
        for (int f=0; f<shape.size(); f++) {
            Point3D p0 = shape.face(f).point(0);
            Point3D p1 = shape.face(f).point(1);
            Point3D p2 = shape.face(f).point(2);
            geom.addVertex(new PdVector(p0.x(), p0.y(), p0.z()));
            geom.addVertex(new PdVector(p1.x(), p1.y(), p1.z()));
            geom.addVertex(new PdVector(p2.x(), p2.y(), p2.z()));
            if (isWireFrame) {
                geom.addElement(new PiVector(3*f+0, 3*f+0, 3*f+1));
                geom.addElement(new PiVector(3*f+1, 3*f+1, 3*f+2));
                geom.addElement(new PiVector(3*f+2, 3*f+2, 3*f+0));
            } else {
                if (transparancy < 1) {
                    geom.addElement(new PiVector(3*f, 3*f+1, 3*f+2));
                    if (transparancy > 0) {
                        geom.setTransparency(transparancy);
                        geom.showTransparency(true);
                    }
                }
            }
        }
        geom.makeElementNormals();
        geom.showBackface(false);
        disp.addGeometry(geom);
    }
    private void add_(Face3D face, Color color) {
        PgElementSet geom = new PgElementSet(3);
        geom.setGlobalElementColor(color);
        geom.setGlobalEdgeColor(color.darker());
        
        Point3D p0 = face.point(0);
        Point3D p1 = face.point(1);
        Point3D p2 = face.point(2);
        geom.addVertex(new PdVector(p0.x(), p0.y(), p0.z()));
        geom.addVertex(new PdVector(p1.x(), p1.y(), p1.z()));
        geom.addVertex(new PdVector(p2.x(), p2.y(), p2.z()));
        if (isWireFrame) {
            geom.addElement(new PiVector(0, 0, 1));
            geom.addElement(new PiVector(1, 1, 2));
            geom.addElement(new PiVector(2, 2, 0));
        } else {
            if (transparancy < 1) {
                geom.addElement(new PiVector(0, 1, 2));
                if (transparancy > 0) {
                    geom.setTransparency(transparancy);
                    geom.showTransparency(true);
                }
            }
        }
        
        disp.addGeometry(geom);
    }
    private void add_(Path3D path, Color color) {
        for (int l=0; l<path.getNumLines(); l++) {
            add_(path.getLine(l), color);
        }
    }
    private void add_(Line3D line, Color color) {
        PgElementSet geom = new PgElementSet(3);
        geom.setGlobalElementColor(color);
        geom.setGlobalEdgeColor(color);
        Point3D p0 = line.origin();
        Point3D p1 = line.end();
        geom.addVertex(new PdVector(p0.x(), p0.y(), p0.z()));
        geom.addVertex(new PdVector(p1.x(), p1.y(), p1.z()));
        geom.addElement(new PiVector(0, 0, 1));
        disp.addGeometry(geom);
    }
    private void add_(Point3D point, Color color) {
        double t = transparancy;
        boolean w = isWireFrame;
        transparancy = 0;
        isWireFrame = false;
        RectangularComponent comp = new RectangularComponent(-1,pointSize,pointSize,pointSize);
        comp.translateXYZ(point.x(), point.y(), point.z());
        add_(comp,color);
        transparancy = t;
        isWireFrame = w;
    }
    private void add_(Sector3D sector, Color color) {
        PgElementSet geom = new PgElementSet(3);
        geom.setGlobalElementColor(color);
        geom.setGlobalEdgeColor(color.darker());
        
        Arc3D outerArc = sector.outerArc();
        Arc3D innerArc = sector.innerArc();
        Point3D p;
        p = innerArc.origin();
        geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        p = outerArc.origin();
        geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        double theta = outerArc.theta();
        double t;
        int i;
        for(i=1; (t = i*radialStepSize)<theta; i++) {
            p = innerArc.point(t);
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
            p = outerArc.point(t);
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        }
        if (t <= theta) {
            p = innerArc.end();
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
            p = outerArc.end();
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        }
        if (isWireFrame) {
            geom.addElement(new PiVector(0, 0, 1));
            for(i=0; (t = ((i/2)+1)*radialStepSize)<theta; i+=2) {
                geom.addElement(new PiVector(i, i, i+2));
                geom.addElement(new PiVector(i+1, i+1, i+3));
            }
            if (t <= theta) {
                geom.addElement(new PiVector(i, i, i+2));
                geom.addElement(new PiVector(i+1, i+1, i+3));
            }
            geom.addElement(new PiVector(i+2, i+2, i+3));
        } else {
            if (transparancy < 1) {
                for(i=0; (t = ((i/2)+1)*radialStepSize)<theta; i+=2) {
                    geom.addElement(new PiVector(i, i+3, i+1));
                    geom.addElement(new PiVector(i, i+2, i+3));
                }
                if (t <= theta) {
                    geom.addElement(new PiVector(i, i+3, i+1));
                    geom.addElement(new PiVector(i, i+2, i+3));
                }
                if (transparancy > 0) {
                    geom.setTransparency(transparancy);
                    geom.showTransparency(true);
                }
            }
        }
        
        disp.addGeometry(geom);
    }
    private void add_(Cylindrical3D cylind, Color color) {
        PgElementSet geom = new PgElementSet(3);
        geom.setGlobalElementColor(color);
        geom.setGlobalEdgeColor(color.darker());
        
        Arc3D outerArc = cylind.baseArc();
        Arc3D innerArc = cylind.highArc();
        Point3D p;
        p = innerArc.origin();
        geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        p = outerArc.origin();
        geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        double theta = outerArc.theta();
        double t;
        int i;
        for(i=1; (t = i*radialStepSize)<theta; i++) {
            p = innerArc.point(t);
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
            p = outerArc.point(t);
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        }
        if (t <= theta) {
            p = innerArc.end();
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
            p = outerArc.end();
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        }
        if (isWireFrame) {
            geom.addElement(new PiVector(0, 0, 1));
            for(i=0; (t = ((i/2)+1)*radialStepSize)<theta; i+=2) {
                geom.addElement(new PiVector(i, i, i+2));
                geom.addElement(new PiVector(i+1, i+1, i+3));
            }
            if (t <= theta) {
                geom.addElement(new PiVector(i, i, i+2));
                geom.addElement(new PiVector(i+1, i+1, i+3));
            }
            geom.addElement(new PiVector(i+2, i+2, i+3));
        } else {
            if (transparancy < 1) {
                for(i=0; (t = ((i/2)+1)*radialStepSize)<theta; i+=2) {
                    geom.addElement(new PiVector(i, i+3, i+1));
                    geom.addElement(new PiVector(i, i+2, i+3));
                }
                if (t <= theta) {
                    geom.addElement(new PiVector(i, i+3, i+1));
                    geom.addElement(new PiVector(i, i+2, i+3));
                }
                if (transparancy > 0) {
                    geom.setTransparency(transparancy);
                    geom.showTransparency(true);
                }
            }
        }
        
        disp.addGeometry(geom);
    }
    private void add_(Arc3D arc, Color color) {
        PgElementSet geom = new PgElementSet(3);
        geom.setGlobalElementColor(color);
        geom.setGlobalEdgeColor(color);
        Point3D p = arc.origin();
        geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
        double theta = arc.theta();
        System.out.println("! "+Math.toDegrees(theta));
        double t;
        Vector3D normal = arc.normal();
        int i;
        for(i=1; (t = i*radialStepSize)<theta; i++) {
            p = arc.point(t);
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
            geom.addElement(new PiVector(i-1, i, i));
        }
        if (t <= theta) {
            p = arc.end();
            geom.addVertex(new PdVector(p.x(), p.y(), p.z()));
            geom.addElement(new PiVector(i-1, i, i));
        }
        disp.addGeometry(geom);
    }
    
    
    public static Color brighten(Color color, int c) {
        return new Color(
                Math.max(0, Math.min(255, color.getRed()+c)),
                Math.max(0, Math.min(255, color.getGreen()+c)),
                Math.max(0, Math.min(255, color.getBlue()+c)));
    }
}
