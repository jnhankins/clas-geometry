/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.detector.base;

/**
 *
 * @author gavalian
 */
public class DetectorDescriptor {
    
    private int DetectorId;
    private int Sector = 0;
    private int SuperLayer = 0;
    private int Layer  = 0;
    private int Component = 0;
    private Boolean validEntry = false;
    
    public DetectorDescriptor(){
        
    }
    
    public DetectorDescriptor(String detname,
            int _sector, int _superlayer, int _layer){
        
        this.set(detname, _sector, _superlayer, _layer, 0);
    }
    
    public DetectorDescriptor(int _detectorid,
            int _sector, int _superlayer, int _layer){
        
        this.set(_detectorid, _sector, _superlayer, _layer, 0);
    }
    
    public DetectorDescriptor(int _detectorid,
            int _sector, int _superlayer, int _layer, int _component){
        this.set(_detectorid, _sector, _superlayer, _layer, _component);
        
    }
    
    public final void set(int _detectorid,
            int _sector, int _superlayer, int _layer, int _component){
        
        DetectorId = _detectorid;
        Sector     = _sector;
        SuperLayer = _superlayer;
        Layer      = _layer;
        Component  = _component;
        validEntry = true;
        
    }
    
    public final void set(String detectorname,
            int _sector, int _superlayer, int _layer, int _component){
        DetectorTypes type = DetectorTypes.getType(detectorname);
        if(type == DetectorTypes.UNDEFINED){
            System.err.println("[DetectorDescriptor] ----> ERROR: unknown detector type ["
            + detectorname + "]");
            return;
        }
        DetectorId = type.id();
        Sector     = _sector;
        SuperLayer = _superlayer;
        Layer      = _layer;
        Component  = _component;
        validEntry = true;
    }
    
    public boolean isValid(){
        
        DetectorTypes type = DetectorTypes.getType(DetectorId);
        if(type == DetectorTypes.FTOF){
            if(Layer!=0) return false;
            if(SuperLayer<0||SuperLayer>2){
                return false;
            } else {
                if(SuperLayer==0){
                    DetectorTypes subtype = DetectorTypes.getType("FTOF1A");
                    if(Component<0||Component>=subtype.components()) return false;
                }
                if(SuperLayer==1){
                    DetectorTypes subtype = DetectorTypes.getType("FTOF1B");
                    if(Component<0||Component>=subtype.components()) return false;
                }
                if(SuperLayer==2){
                    DetectorTypes subtype = DetectorTypes.getType("FTOF2B");
                    if(Component<0||Component>=subtype.components()) return false;
                }
            }
            return true;
        }
        
        if(type == DetectorTypes.DC){
            if(Sector<0||Sector>=type.sectors()) return false;
            if(SuperLayer<0||SuperLayer>=type.superLayers()) return false;
            if(Layer<0||Layer>=type.layers()) return false;
            if(Component<0||Component>=type.components()) return false;
            return true;
        }
        
        // Part with FT-calorimeter
        // number of paddles = 332
        //return validEntry;
        return true;
    }
    
    
    public int getDetectorID(){
        return DetectorId;
    }
    
    public int getSector(){
        return Sector;
    }
    
    public int getSuperLayer(){
        return SuperLayer;
    }
    
    public int getLayer(){
        return Layer;
    }
    
    public int getComponent(){
        return Component;
    }
    
    public void copy(DetectorDescriptor desc){
        this.set(desc.getDetectorID(), 
                desc.getSector(), desc.getSuperLayer(),
                desc.getLayer(), desc.getComponent());
    }
    
    public static int getHashCode(int did, int sct, int spl, int l, int c){
        return 100000*(did*10+sct) + 10000*spl + 1000 * l
                + c;
    }
    
    @Override
    public int hashCode(){
        return  100000*(DetectorId*10+Sector) + 10000*SuperLayer + 1000 * Layer
                + Component;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DetectorDescriptor other = (DetectorDescriptor) obj;
        if (this.DetectorId != other.DetectorId) {
            return false;
        }
        if (this.Sector != other.Sector) {
            return false;
        }
        if (this.SuperLayer != other.SuperLayer) {
            return false;
        }
        if (this.Layer != other.Layer) {
            return false;
        }
        if (this.Component != other.Component) {
            return false;
        }
        return true;
    }
    
    
}
