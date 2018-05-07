
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public static ExamineTimetablingManager readObject(String path) {

        ExamineTimetablingManager etm = null;

        try (FileInputStream fis = new FileInputStream(path)) {
            ObjectInputStream ois = new ObjectInputStream(fis);

            etm = (ExamineTimetablingManager) (ois.readObject());

            System.out.println("\nRead ETM from " + path + " done");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return etm;
    }

    public static boolean makeDir(String pathFile) {
        Path path = Paths.get(DataIO.getAbsolutePath(pathFile));
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("created new dir " + path);
                return true;
            } catch (Exception e) {
                //fail to create directory
                e.printStackTrace();
                System.out.println("ERROR create new dir " + path);
                return false;
            }
        }
        return true;
    }
    
    public static String getAbsolutePath(String path){
        return new File(path).getAbsolutePath();
    }
    
    public static void writeFileExcel(String path, ArrayList<ArrayList<String>> output){
        try(FileWriter fw = new FileWriter(path)){
            BufferedWriter bw = new BufferedWriter(fw);
            
            for(ArrayList<String> list : output){
                for(String str : list){
                    fw.write(str + " ");
                }
                fw.write("\n");
            }
            
            bw.close();
            fw.close();
            System.out.println("\nWrite excel " + path + " done");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void writeStringToFile(String path, String content){
        
        try(FileWriter fw = new FileWriter(path)){
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write(content);
            
            bw.close();
            fw.close();
            System.out.println("\nWrite info data " + path + " done");
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    
}
