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
public enum DetectorTypes {
    
    UNDEFINED (0,0,0,0,0,"undefined","undefined"),
    FTOF      (1,6,3,1,63,"FTOF","scintilator"),
    FTOF1A    (2,6,1,1,23,"FTOF1A","scintilator"),
    FTOF1B    (3,6,1,1,62,"FTOF1B","scintilator"),
    FTOF2B    (4,6,1,1,5,"FTOF2B","scintilator"),
    DC        (5,6,6,6,112,"DC","wire"),
    EC        (6,6,3,3,39,"EC","scintilator"),
    PCAL      (7,6,3,3,77,"PCAL","scintilator"),
    CND       (8,1,3,1,48,"CND","scintilator"),
    FTCAL     (9,1,1,1,484,"FTCAL","ledglass");
    
    private final int detectorId;
    private final int detectorSectors;
    private final int detectorSuperLayers;
    private final int detectorLayers;
    private final int detectorComponents;
    private final String detectorName;
    private final String componentType;
    
    DetectorTypes(int did, int _sectors, int _superlayers, int _layers, 
            int _components,
            String name, String ctype){
        detectorId = did;
        detectorSectors = _sectors;
        detectorSuperLayers = _superlayers;
        detectorLayers      = _layers;
        detectorComponents = _components;
        detectorName = name;
        componentType = ctype;
    }
    
    public int id(){
        return detectorId;
    }
    public int sectors(){
        return detectorSectors;
    }
    
    public int superLayers(){
        return detectorSuperLayers;
    }
    
    public int layers(){
        return detectorLayers;
    }
    
    public int components(){
        return detectorComponents;
    }
    
    public String getName(){
        return detectorName;
    }
    
    public static DetectorTypes getType(String type){
        for(DetectorTypes dt: DetectorTypes.values()){
            if (dt.getName().equals(type.trim())) return dt;
        }
        return UNDEFINED;
    }
    
    public static DetectorTypes getType(int _id){
        for(DetectorTypes dt: DetectorTypes.values()){
            if (dt.id()==_id) return dt;
        }
        return UNDEFINED;
    }
}
