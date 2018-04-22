/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.ExamineTimetablingManager;

/**
 * Class read/write file
 *
 * @author quancq
 */
public class DataIO {

    /**
     *
     * @param path is path where to write file
     * @param etm is object order to serialize to file
     */
    public static void writeObject(String path, ExamineTimetablingManager etm) {

        try (FileOutputStream fos = new FileOutputStream(path)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(etm);

            oos.close();
            fos.close();

            System.out.println("\nWrite ETM to file " + path + " done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static ExamineTimetablingManager readObject(String path){
        
        ExamineTimetablingManager etm = null;
        
        try(FileInputStream fis = new FileInputStream(path)){
            ObjectInputStream ois = new ObjectInputStream(fis);
            
            etm = (ExamineTimetablingManager)(ois.readObject());
            
            System.out.println("\nRead ETM from " + path + " done");
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return etm;
    }
}
