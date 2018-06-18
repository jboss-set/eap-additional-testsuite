/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.dependencytreeparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author panos
 */
public class DependencyTreeMethods {
    
    public static void printDependencies() throws IOException {
        String filePath = System.getProperty("DependencyTreeFilePath");
        boolean deleteAll =true;
        String keyWord = "--- maven-dependency-plugin";
        String keyWord2 = "--------------------------------------------------";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if(line.trim().isEmpty() || line.contains(keyWord2))
                    deleteAll = true;
                
                if(!deleteAll) {
                    if(line.contains("+") || line.contains("\\"))
                        sb.append(line.replaceAll("\\+", "").replaceFirst("-", "").replaceAll("\\|", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("INFO", "").replace("\\", "").trim());
                    else
                        sb.append(line.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("INFO", "").trim());
                    sb.append(System.lineSeparator());
                }
                
                line = br.readLine();
                
                if(line!=null && line.contains(keyWord)) {
                    deleteAll = false;
                    line = br.readLine();
                }
                
                
            }
            String everything = sb.toString();
            System.out.println(everything);
        } catch(Exception e) {
            e.printStackTrace();
        }finally {
            br.close();
        }
    }
    
    public static ArrayList<Artifact> getArtifacts() throws IOException {
        String filePath = System.getProperty("DependencyTreeFilePath");
        ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
        boolean deleteAll =true;
        String keyWord = "--- maven-dependency-plugin";
        String keyWord2 = "--------------------------------------------------";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if(line.trim().isEmpty() || line.contains(keyWord2))
                    deleteAll = true;
                
                if(!deleteAll) {
                    Artifact art = new Artifact();
                    String[] parts;
                    if(line.contains("+") || line.contains("\\"))
                        parts = line.replaceAll("\\+", "").replaceFirst("-", "").replaceAll("\\|", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("INFO", "").replace("\\", "").trim().split(":");
                    else
                        parts = line.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("INFO", "").trim().split(":");
                    
                    if (parts.length==5) {
                        art.artifactId = parts[0];
                        art.groupId = parts[1];
                        art.type = parts[2];
                        art.version = parts[3];
                        
                        artifacts.add(art);
                    }else if(parts.length==6) {
                        art.artifactId = parts[0];
                        art.groupId = parts[1];
                        art.type = parts[2];
                        art.version = parts[4];
                    }
                    
                    
                }
                
                line = br.readLine();
                
                if(line!=null && line.contains(keyWord)) {
                    deleteAll = false;
                    line = br.readLine();
                }
                
                
            }
            
        /*    for(Artifact a:artifacts){
                System.out.println(a.artifactId + " " + a.groupId + " " + a.version);
            }*/
            
        } catch(Exception e) {
            e.printStackTrace();
        }finally {
            br.close();
            return artifacts;
        }
    }
    
    public static ArrayList<String> listClasses(){
        ArrayList<String> jarClasses = new ArrayList<>();
         
        try {
            ArrayList<Artifact> artifacts = DependencyTreeMethods.getArtifacts();
            String repoPath = System.getProperty("MavenRepoPath");
            
            for(Artifact ar : artifacts) {
                if(ar.type.contains("jar")) {
                //    System.out.println(repoPath + ar.artifactId.replaceAll("//.", "//")+"/"+ar.groupId+"/"+ar.version+"/"+ar.groupId + "-" + ar.version+".jar");
                    jarClasses.addAll(DependencyTreeMethods.listJars(repoPath + "/"+ ar.artifactId.replaceAll("\\.", "//")+"/"+ar.groupId+"/"+ar.version+"/"+ar.groupId + "-" + ar.version+".jar"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return jarClasses;
        }
    }
    
    public static HashSet<String> listPackages(){
        HashSet<String> packages = new HashSet<String>();
        
        ArrayList<String> jarClasses = DependencyTreeMethods.listClasses();
        
        for(String jc : jarClasses){
            if(jc.contains(".class") && jc.lastIndexOf("/")!=-1) {
                String packageName = jc.substring(0, jc.lastIndexOf("/")).replaceAll("/", ".");
           //     System.out.println("packageName : " + packageName);
                    if(!packages.contains(packageName))
                        packages.add(packageName);
            }
        }
        
        return packages;
    }
    
    private static ArrayList<String> listJars(String path) {
        ArrayList<String> jarClasses = new ArrayList<>();
        
        try{
            if (path!=null) {
                JarFile jarFile = new JarFile(path);
                Enumeration allEntries = jarFile.entries();
                while (allEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) allEntries.nextElement();
                    String name = entry.getName();
                    jarClasses.add(name);
                //    System.out.println(name);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            return jarClasses;
        }
    }

}

class Artifact {
    String groupId;
    String artifactId;
    String version;
    String type;
}
