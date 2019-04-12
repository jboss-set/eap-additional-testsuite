/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.dependencytreeparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author panos
 */
public class DependencyTreeMethods {

    public static HashMap<String, String> jarClassPaths = new HashMap<>();
    public static HashSet<Artifact> artifacts = new HashSet<>();
    public static HashSet<String> testsuiteArtifactsPaths = new HashSet<>();
    public static HashSet<String> unloadedClasses = new HashSet<>();

    public static void printDependencies() throws IOException {
        String filePath = System.getProperty("DependencyTreeFilePath");
        boolean deleteAll = true;
        String keyWord = "--- maven-dependency-plugin";
        String keyWord2 = "--------------------------------------------------";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if (line.trim().isEmpty() || line.contains(keyWord2)) {
                    deleteAll = true;
                }

                if (!deleteAll) {
                    if (line.contains("+") || line.contains("\\")) {
                        sb.append(line.replaceAll("\\+", "").replaceFirst("-", "").replaceAll("\\|", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("INFO", "").replace("\\", "").trim());
                    } else {
                        sb.append(line.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("INFO", "").trim());
                    }
                    sb.append(System.lineSeparator());
                }

                line = br.readLine();

                if (line != null && line.contains(keyWord)) {
                    deleteAll = false;
                    line = br.readLine();
                }

            }
            String everything = sb.toString();
            System.out.println(everything);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
    }

    public static HashSet<Artifact> getArtifacts() throws IOException {
        String filePath = System.getProperty("DependencyTreeFilePath");
        boolean deleteAll = true;
        String keyWord = "--- maven-dependency-plugin";
        String keyWord2 = "--------------------------------------------------";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if (line.trim().isEmpty() || line.contains(keyWord2)) {
                    deleteAll = true;
                }

                if (!deleteAll) {
                    Artifact art = new Artifact();
                    String[] parts;
                    if (line.contains("+") || line.contains("\\")) {
                        parts = line.replaceAll("\\+", "").replaceFirst("-", "").replaceAll("\\|", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("INFO", "").replace("\\", "").trim().split(":");
                    } else {
                        parts = line.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("INFO", "").trim().split(":");
                    }

                    if (parts.length == 5) {
                        art.artifactId = parts[0];
                        art.groupId = parts[1];
                        art.type = parts[2];
                        art.version = parts[3];
                        art.scope = parts[4];

                        artifacts.add(art);
                    } else if (parts.length == 6) {
                        art.artifactId = parts[0];
                        art.groupId = parts[1];
                        art.type = parts[2];
                        art.version = parts[4];
                        art.scope = parts[5];

                        artifacts.add(art);
                    }

                }

                line = br.readLine();

                if (line != null && line.contains(keyWord)) {
                    deleteAll = false;
                    line = br.readLine();
                }

            }

            String filePath2 = System.getProperty("ExternalDependencyPath");
            artifacts = addExternalLibraries(artifacts, filePath2);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            br.close();
            return artifacts;
        }
    }

    public static HashSet<String> addExcludedLibraries() throws IOException {
        HashSet<String> excludedLibraries = new HashSet<>();
        String filePath = System.getProperty("ExcludedDependenciesPath");

        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(filePath);
            if (fr != null) {
                br = new BufferedReader(fr);
                String line = br.readLine();

                while (line != null) {
                    excludedLibraries.add(line);
                    line = br.readLine();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
            return excludedLibraries;
        }
    }

    public static HashSet<Artifact> addExternalLibraries(HashSet<Artifact> artifacts, String filePath) throws IOException {
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(filePath);
            if (fr != null) {
                br = new BufferedReader(fr);
                String line = br.readLine();

                while (line != null) {
                    Artifact art = new Artifact();
                    String[] parts;
                    parts = line.trim().split(":");

                    if (parts.length == 4) {
                        art.artifactId = parts[0];
                        art.groupId = parts[1];
                        art.type = parts[2];
                        art.version = parts[3];

                        artifacts.add(art);
                    }

                    line = br.readLine();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
            return artifacts;
        }
    }

    public static HashMap<String, String> listJarClassPaths() {
        HashMap<String, String> jarClassPaths = new HashMap<>();

        try {
            String repoPath = System.getProperty("MavenRepoPath");

            for (Artifact ar : artifacts) {
                if (ar.type.contains("jar")) {
                    jarClassPaths.putAll(DependencyTreeMethods.listJarClassPaths(repoPath + "/" + ar.artifactId.replaceAll("\\.", "//") + "/" + ar.groupId + "/" + ar.version + "/" + ar.groupId + "-" + ar.version + ".jar"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return jarClassPaths;
        }
    }

    public static HashMap<String, String> listFieldsOfJarClass(String path, String className) {
        HashMap<String, String> jarClassFields = new HashMap<>();

        try {
            if (path != null) {
                JarFile jarFile = new JarFile(path);

                URL[] urls = {new URL("jar:file:" + path + "!/")};
                URLClassLoader cl = URLClassLoader.newInstance(urls);

                Enumeration<JarEntry> allEntries = jarFile.entries();
                while (allEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) allEntries.nextElement();
                    String name = entry.getName();

                    if (name.replaceAll("/", ".").replaceAll("-", ".").contains(className) && name.endsWith(".class") && !entry.isDirectory()) {
                        name = name.substring(0, name.lastIndexOf(".class"));
                        name = name.replaceAll("/", ".");
                        name = name.replaceAll("-", ".");

                        try {
                            Class clas = cl.loadClass(name);

                            for (Class<?> c = clas; c != null; c = c.getSuperclass()) {
                                for (Field f : c.getFields()) {
                                    if (!Modifier.toString(f.getModifiers()).contains("private")) {
                                        if (!jarClassFields.keySet().contains(f)) {
                                            jarClassFields.put(f.getName(), f.getType().toString());
                                        }
                                    }
                                }
                            }

                        } catch (Exception ex) {
                            //    ex.printStackTrace();
                        }
                    }

                }
                jarFile.close();
            }
        } catch (Exception e) {
            //    e.printStackTrace();
            System.out.println(path + " is not available.");
        } finally {
            return jarClassFields;
        }
    }

    public static HashMap<String, ArrayList<Class[]>> listClasses() {
        HashMap<String, ArrayList<Class[]>> jarClasses = new HashMap<>();

        try {
            String repoPath = System.getProperty("MavenRepoPath");

            for (Artifact ar : artifacts) {
                if (ar.type.contains("jar")) {
                    jarClasses.putAll(DependencyTreeMethods.listClasses(repoPath + "/" + ar.artifactId.replaceAll("\\.", "//") + "/" + ar.groupId + "/" + ar.version + "/" + ar.groupId + "-" + ar.version + ".jar"));
                    jarClasses.putAll(DependencyTreeMethods.listJarClasses(repoPath + "/" + ar.artifactId.replaceAll("\\.", "//") + "/" + ar.groupId + "/" + ar.version + "/" + ar.groupId + "-" + ar.version + ".jar"));
                    jarClassPaths.putAll(listJarClassPaths(repoPath + "/" + ar.artifactId.replaceAll("\\.", "//") + "/" + ar.groupId + "/" + ar.version + "/" + ar.groupId + "-" + ar.version + ".jar"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return jarClasses;
        }
    }

    public static HashMap<String, HashMap<String, String[]>> listMethods() {
        HashMap<String, HashMap<String, String[]>> classMethods = new HashMap<>();

        try {
            String repoPath = System.getProperty("MavenRepoPath");

            for (Artifact ar : artifacts) {
                if (ar.type.contains("jar")) {
                    classMethods.putAll(DependencyTreeMethods.listClassMethods2(repoPath + "/" + ar.artifactId.replaceAll("\\.", "//") + "/" + ar.groupId + "/" + ar.version + "/" + ar.groupId + "-" + ar.version + ".jar"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return classMethods;
        }
    }

    public static HashMap<String, HashMap<String, String[]>> listUsedMethods(HashSet<String> usedLibraries, HashMap<String, String> packages) {
        HashMap<String, HashMap<String, String[]>> usedMethods = new HashMap<>();

        try {

            for (String lb : usedLibraries) {
                usedMethods.putAll(DependencyTreeMethods.listUsedClassMethods(packages.get(lb), lb));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return usedMethods;
        }
    }

    public static HashMap<String, HashMap<String, String[]>> listUsedMethods2(String usedLibrary, HashMap<String, String> packages) {
        HashMap<String, HashMap<String, String[]>> usedMethods = new HashMap<>();

        try {

            for (String lb : packages.keySet()) {
                if (lb.contains("$")) {
                    lb = lb.substring(0, lb.indexOf("$"));
                }
                if (lb.contains(".")) {
                    lb = lb.subSequence(0, lb.lastIndexOf(".")).toString();
                }

                if (lb.equals(usedLibrary)) {
                    usedMethods.putAll(DependencyTreeMethods.listUsedClassMethods(packages.get(lb), lb));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return usedMethods;
        }
    }

    public static HashMap<String, HashMap<String, String[]>> listUsedTestMethods(HashSet<String> usedLibraries, String path) {
        HashMap<String, HashMap<String, String[]>> usedMethods = new HashMap<>();

        try {

            for (String lb : usedLibraries) {
                usedMethods.putAll(DependencyTreeMethods.listUsedClassMethods(path, lb));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return usedMethods;
        }
    }

    public static HashMap<String, HashMap<String, String>> listFields() {
        HashMap<String, HashMap<String, String>> classFields = new HashMap<>();

        try {
            String repoPath = System.getProperty("MavenRepoPath");

            for (Artifact ar : artifacts) {
                if (ar.type.contains("jar")) {
                    classFields.putAll(DependencyTreeMethods.listClassFields(repoPath + "/" + ar.artifactId.replaceAll("\\.", "//") + "/" + ar.groupId + "/" + ar.version + "/" + ar.groupId + "-" + ar.version + ".jar"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return classFields;
        }
    }

    public static HashMap<String, String> listPackages() {
        HashMap<String, String> jarPackages = new HashMap<>();

        try {
            String repoPath = System.getProperty("MavenRepoPath");

            for (Artifact ar : artifacts) {
                if (ar.type.contains("jar")) {
                    testsuiteArtifactsPaths.add(repoPath + "/" + ar.artifactId.replaceAll("\\.", "//") + "/" + ar.groupId + "/" + ar.version + "/" + ar.groupId + "-" + ar.version + ".jar");
                    DependencyTreeMethods.listJarPackages(repoPath + "/" + ar.artifactId.replaceAll("\\.", "//") + "/" + ar.groupId + "/" + ar.version + "/" + ar.groupId + "-" + ar.version + ".jar", jarPackages);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return jarPackages;
        }
    }

    public static HashSet<String> listAvailablePackages() {
        HashSet<String> packages = new HashSet<String>();

        HashMap<String, String> jarPackages = DependencyTreeMethods.listPackages();

        for (String jc : jarPackages.keySet()) {
            if (jc.lastIndexOf(".") != -1) {
                String packageName = jc.substring(0, jc.lastIndexOf("."));
                if (!packages.contains(packageName)) {
                    packages.add(packageName);
                }
            }
        }

        return packages;
    }

    public static HashSet<String> getSourceClasses() {
        HashSet<String> testClasses = new HashSet<>();

        String serverTestPath = System.getProperty("ServerDir");
        String coreTestPath = System.getProperty("CoreDir");

        JavaClassParser.fileDiscovery(serverTestPath);
        testClasses.addAll(JavaClassParser.filesTest);
        JavaClassParser.filesTest.clear();
        JavaClassParser.fileDiscovery(coreTestPath);
        testClasses.addAll(JavaClassParser.filesTest);
        JavaClassParser.filesTest.clear();

        return testClasses;
    }

    private static HashMap<String, ArrayList<Class[]>> listClasses(String path) {
        HashMap<String, ArrayList<Class[]>> classes = new HashMap<>();

        try {
            if (path != null) {
                String dir = path.replaceAll(".jar", "");
                dir = dir.replaceAll("//", "/");

                ArrayList<String> commands = new ArrayList<String>();
                commands.add("mkdir");
                commands.add("-m777");
                commands.add(dir);
                ProcessBuilder pb = new ProcessBuilder(commands);
                Process p = pb.start();
                while (p.isAlive());
                p.destroy();

                try {
                    JarExtract.jarExtract(Paths.get(path), Paths.get(dir));
                } catch (Exception e) {
                    //    System.out.println(dir + " already exists ...");
                }

                if (!Files.exists(Paths.get(dir + "/jarClasses.txt"))) {
                    commands = new ArrayList<String>();
                    commands.add("bash");
                    commands.add("-c");
                    commands.add("cd " + dir + " ; find -name '*.class' > jarClasses.txt ; chmod 777 jarClasses.txt");
                    pb = new ProcessBuilder(commands);
                    p = pb.start();
                    while (p.isAlive());
                    p.destroy();
                }

                classes.putAll(getParsedJarClasses(dir + "/jarClasses.txt"));

            }
        } catch (Exception e) {
            System.out.println(path + " is not available.");
            //    e.printStackTrace();
        } finally {
            return classes;
        }
    }

    private static HashMap<String, String> listJarPackages(String path, HashMap<String, String> jarPackages) {

        try {
            if (path != null) {
                JarFile jarFile = new JarFile(path);

                URL[] urls = {new URL("jar:file:" + path + "!/")};

                Enumeration allEntries = jarFile.entries();
                while (allEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) allEntries.nextElement();
                    String name = entry.getName();

                    if (name.contains(".class")) {
                        jarPackages.put(name.substring(0, name.lastIndexOf(".")).replaceAll("/", "."), path);
                    } else {
                        jarPackages.put(name.replaceAll("/", "."), path);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(path + " is not available.");
            //  e.printStackTrace();
        } finally {
            return jarPackages;
        }
    }

    private static HashMap<String, String> listJarClassPaths(String path) {
        HashMap<String, String> jarClasses = new HashMap<>();

        try {
            if (path != null) {
                JarFile jarFile = new JarFile(path);

                URL[] urls = {new URL("jar:file:" + path + "!/")};

                Enumeration allEntries = jarFile.entries();
                while (allEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) allEntries.nextElement();
                    String name = entry.getName();

                    if (name.contains(".class")) {
                        jarClasses.put(name.replaceAll("/", ".").replaceAll(".class", ""), path);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(path + " is not available.");
            //  e.printStackTrace();
        } finally {
            return jarClasses;
        }
    }

    private static HashMap<String, ArrayList<Class[]>> listJarClasses(String path) {
        HashMap<String, ArrayList<Class[]>> jarClasses = new HashMap<>();

        try {
            if (path != null) {
                JarFile jarFile = new JarFile(path);

                URL[] urls = {new URL("jar:file:" + path + "!/")};
                URLClassLoader cl = URLClassLoader.newInstance(urls);

                Enumeration allEntries = jarFile.entries();
                while (allEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) allEntries.nextElement();
                    String name = entry.getName();

                    if (name.contains(".class") && !name.contains("$")) {
                        name = name.substring(0, name.lastIndexOf(".class"));
                        name = name.replaceAll("/", ".");
                        name = name.replaceAll("-", ".");
                        try {
                            Class clas = cl.loadClass(name);
                            Constructor[] constructors = clas.getConstructors();
                            ArrayList<Class[]> constructorParams = new ArrayList<>();
                            for (Constructor c : constructors) {
                                Class[] parameterTypes = c.getParameterTypes();
                                constructorParams.add(parameterTypes);
                            }
                            jarClasses.put(name, constructorParams);
                        } catch (Exception ex) {
                            //    ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println(path + " is not available.");
        } finally {
            return jarClasses;
        }
    }

    private static HashMap<String, HashMap<String, String[]>> listClassMethods(String path) {
        HashMap<String, HashMap<String, String[]>> classMethods = new HashMap<>();

        try {
            if (path != null) {
                JarFile jarFile = new JarFile(path);

                URL[] urls = {new URL("jar:file:" + path + "!/")};
                URLClassLoader cl = URLClassLoader.newInstance(urls);

                Enumeration allEntries = jarFile.entries();
                while (allEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) allEntries.nextElement();

                    String name = entry.getName();

                    if (entry.isDirectory() || !name.endsWith(".class")) {
                        continue;
                    }

                    if (name.contains(".class")) {
                        String name2 = name;
                        name2 = name2.substring(0, name.lastIndexOf(".class"));
                        name2 = name2.replaceAll("/", ".");
                        name2 = name2.replaceAll("-", ".");

                        try {
                            Class clas = cl.loadClass(name);

                            HashMap<String, String[]> allMethods = new HashMap<>();

                            for (Class<?> c = clas; c != null; c = c.getSuperclass()) {
                                for (Method method : c.getMethods()) {

                                    if (!Modifier.toString(method.getModifiers()).contains("private")) {
                                        String[] params = new String[method.getParameterTypes().length];
                                        int j = 0;
                                        for (Class cc : method.getParameterTypes()) {
                                            params[j] = cc.toString();
                                            j++;
                                        }
                                        allMethods.put(method.getName(), params);
                                        allMethods.put(method.getName() + "_Return_Type", new String[]{method.getReturnType().toString()});
                                    }
                                }
                            }

                            classMethods.put(name, allMethods);
                        } catch (Exception ex) {
                            //        ex.printStackTrace();
                        }
                    }

                }
            }
        } catch (Exception e) {
            //    e.printStackTrace();
            System.out.println(path + " is not available.");
        } finally {
            return classMethods;
        }
    }

    private static HashMap<String, HashMap<String, String[]>> listClassMethods2(String path) {
        HashMap<String, HashMap<String, String[]>> classMethods = new HashMap<>();

        try {
            if (path != null) {
                String dir = path.replaceAll(".jar", "");
                dir = dir.replaceAll("//", "/");

                ArrayList<String> commands = new ArrayList<String>();
                commands.add("mkdir");
                commands.add("-m777");
                commands.add(dir);
                ProcessBuilder pb = new ProcessBuilder(commands);
                Process p = pb.start();
                while (p.isAlive());
                p.destroy();

                try {
                    JarExtract.jarExtract(Paths.get(path), Paths.get(dir));
                } catch (Exception e) {
                    //    System.out.println(dir + " already exists ...");
                }

                if (!Files.exists(Paths.get(dir + "/jarMethods.txt"))) {
                    commands = new ArrayList<String>();
                    commands.add("bash");
                    commands.add("-c");
                    commands.add("cd " + dir + " ; find -name '*.class' | xargs javap -p > jarMethods.txt ; chmod 777 jarMethods.txt");
                    pb = new ProcessBuilder(commands);
                    p = pb.start();
                    while (p.isAlive());
                    p.destroy();
                }

                classMethods.putAll(getParsedJarMethods(dir + "/jarMethods.txt"));

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(path + " is not available.");
            //    e.printStackTrace();
        } finally {
            return classMethods;
        }
    }

    private static HashMap<String, ArrayList<Class[]>> getParsedJarClasses(String file) {
        HashMap<String, ArrayList<Class[]>> classes = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = br.readLine();

            while (line != null) {
                line = line.replaceAll(".class", "");
                line = line.replaceAll("/", ".");
                classes.put(line.replaceFirst("..", ""), null);
                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println("getParsedJarClasses : problem with file " + file);
            //    e.printStackTrace();
        }

        return classes;
    }

    private static HashMap<String, HashMap<String, String[]>> getParsedJarMethods(String file) {
        HashMap<String, HashMap<String, String[]>> classMethods = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = br.readLine();
            String className = "";
            HashMap<String, String[]> methods = null;

            boolean record = false;
            while (line != null) {
                String[] params = null;
                String methodName = "";
                String returnType = "";
                String[] extensions = null;
                String[] extensions2 = null;
                String template = "";
                if (record && !line.contains("private ") && !line.contains("}") && !line.contains("class ") && !line.contains("interface ")) {
                    if (line.contains(")")) {
                        line = line.substring(0, line.lastIndexOf(")"));
                        params = line.substring(line.indexOf("(") + 1).split(", ");
                        line = line.substring(0, line.indexOf("("));
                    } else if (line.contains("(")) {
                        line = line.substring(0, line.indexOf("("));
                    } else {
                        line = line.replaceAll(";", "");
                    }
                    methodName = line.substring(line.lastIndexOf(" ") + 1);
                    methods.put(methodName, params);

                    returnType = line.substring(0, line.lastIndexOf(methodName));

                    if (returnType.contains("<")) {
                        returnType = returnType.substring(0, returnType.indexOf("<"));
                    }
                    if (returnType.trim().contains(" ")) {
                        returnType = returnType.trim();
                        returnType = returnType.substring(returnType.lastIndexOf(" "));
                    }
                    if (returnType.contains("[")) {
                        returnType = returnType.substring(0, returnType.indexOf("["));
                    }
                    if (returnType.contains(".")) {
                        returnType = returnType.substring(returnType.lastIndexOf(".") + 1);
                    }
                    if (returnType.trim().equals("T")) {
                        returnType = template;
                    }

                    methods.put(methodName + "_Return_Type", new String[]{returnType});

                }

                if ((line.contains("class ") || line.contains("interface ")) && line.contains("{")) {
                    record = true;
                    template = "";
                    if (line.contains("class ")) {
                        className = line.substring(line.indexOf("class") + 6);
                    }

                    if (line.contains("interface ")) {
                        className = line.substring(line.indexOf("interface") + 10);
                    }

                    if (className.contains("extends")) {
                        String extension = className.substring(className.lastIndexOf("extends") + 8);
                        extension = extension.replaceAll("\\{", "");
                        if (extension.contains("implements")) {
                            extension = extension.substring(0, extension.indexOf(" implements"));
                        }
                        extension = extension.trim();
                        extensions = extension.split(",");
                        for (int i = 0; i < extensions.length; i++) {
                            if (extensions[i].contains("<")) {
                                extensions[i] = extensions[i].substring(0, extensions[i].indexOf("<"));
                            } else if (!extensions[i].contains("<") && extensions[i].contains(">")) {
                                extensions[i] = null;
                            }
                        }
                    }
                    if (className.contains("implements")) {
                        String extension2 = className.substring(className.lastIndexOf("implements") + 11);
                        extension2 = extension2.replaceAll("\\{", "");
                        extension2 = extension2.trim();
                        extensions2 = extension2.split(",");
                        for (int i = 0; i < extensions2.length; i++) {
                            if (extensions2[i].contains("<")) {
                                extensions2[i] = extensions2[i].substring(0, extensions2[i].indexOf("<"));
                            } else if (!extensions2[i].contains("<") && extensions2[i].contains(">")) {
                                extensions2[i] = null;
                            }
                        }
                    }

                    if (className.contains(" ")) {
                        className = className.substring(0, className.indexOf(" "));
                    }
                    if (className.contains("<")) {
                        template = className.substring(className.indexOf("<") + 1);
                        template = template.replaceAll(">", "");
                        className = className.substring(0, className.indexOf("<"));
                    }

                    methods = new HashMap<>();

                    if (extensions != null && extensions2 != null) {
                        String[] both = Arrays.copyOf(extensions, extensions.length + extensions2.length);
                        System.arraycopy(extensions2, 0, both, extensions.length, extensions2.length);
                        methods.put(className + "_Extensions", both);
                    } else if (extensions != null) {
                        methods.put(className + "_Extensions", extensions);
                    } else if (extensions2 != null) {
                        methods.put(className + "_Extensions", extensions2);
                    }
                } else if (line.contains("}")) {
                    record = false;
                    classMethods.put(className, methods);
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println("getParsedJarMethods : problem with file " + file);
            //    e.printStackTrace();
        }

        return classMethods;
    }

    public static HashSet<String> getDataClasses(String path) {
        HashSet<String> testClasses = new HashSet<>();

        JavaClassParser.classFileDiscovery(path);
        testClasses.addAll(JavaClassParser.filesTest);
        JavaClassParser.filesTest.clear();

        return testClasses;
    }

    private static void parseInterfaces(Class c, String name, HashMap<String, HashMap<String, String[]>> classMethods) throws MalformedURLException, IOException {
        String path2 = jarClassPaths.get(c.getName());
        if (path2 != null) {
            JarFile jarFile2 = new JarFile(path2);

            URL[] urls2 = {new URL("jar:file:" + path2 + "!/")};
            URLClassLoader cl2 = URLClassLoader.newInstance(urls2);

            Enumeration allEntries2 = jarFile2.entries();
            while (allEntries2.hasMoreElements()) {
                JarEntry entry2 = (JarEntry) allEntries2.nextElement();
                String name2 = entry2.getName();

                if (name2.contains(".class")) {
                    name2 = name2.substring(0, name2.lastIndexOf(".class"));
                    name2 = name2.replaceAll("/", ".");
                    name2 = name2.replaceAll("-", ".");

                    try {
                        Class clas2 = cl2.loadClass(name2);

                        HashMap<String, String[]> allMethods2 = new HashMap<>();

                        for (Class<?> c2 = clas2; c2 != null; c2 = c2.getSuperclass()) {
                            for (Method method : c2.getMethods()) {

                                if (!Modifier.toString(method.getModifiers()).contains("private")) {
                                    String[] params = new String[method.getParameterTypes().length];
                                    int j = 0;
                                    for (Class cc : method.getParameterTypes()) {
                                        params[j] = cc.toString();
                                        j++;
                                    }
                                    allMethods2.put(method.getName(), params);
                                    allMethods2.put(method.getName() + "_Return_Type", new String[]{method.getReturnType().toString()});
                                }
                            }
                        }

                        for (Class c2 : clas2.getInterfaces()) {
                            parseInterfaces(c2, name, classMethods);
                        }

                        classMethods.put(name, allMethods2);
                    } catch (Exception ex) {
                        //    ex.printStackTrace();
                    }
                }

            }
        }
    }

    private static HashMap<String, HashMap<String, String[]>> listUsedClassMethods(String path, String lib) {
        HashMap<String, HashMap<String, String[]>> classMethods = new HashMap<>();

        try {
            if (path != null) {
                JarFile jarFile = new JarFile(path);

                URL[] urls = {new URL("jar:file:" + path + "!/")};
                URLClassLoader cl = URLClassLoader.newInstance(urls);

                Enumeration allEntries = jarFile.entries();
                //    System.out.println("path : " + path);
                while (allEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) allEntries.nextElement();
                    String name = entry.getName();

                    String packageName = name.replaceAll("-", ".");
                    if (name.contains(".class") && packageName.replaceAll("/", ".").contains(lib)) {
                        //       System.out.println("package name 2 : " + packageName);
                        name = name.substring(0, name.lastIndexOf(".class"));
                        name = name.replaceAll("/", ".");
                        name = name.replaceAll("-", ".");

                        try {
                            Class clas = cl.loadClass(name);

                            HashMap<String, String[]> allMethods = new HashMap<>();

                            for (Class<?> c = clas; c != null; c = c.getSuperclass()) {

                                for (Method method : c.getMethods()) {

                                    if (!Modifier.toString(method.getModifiers()).contains("private")) {

                                        String[] params = new String[method.getParameterTypes().length];
                                        int j = 0;
                                        for (Class cc : method.getParameterTypes()) {
                                            params[j] = cc.toString();
                                            j++;
                                        }
                                        allMethods.put(method.getName(), params);
                                        allMethods.put(method.getName() + "_Return_Type", new String[]{method.getReturnType().toString()});
                                    }
                                }
                            }
                            classMethods.put(name, allMethods);
                        } catch (Exception ex) {
                            //    ex.printStackTrace();
                        }
                    }

                }
            }
        } catch (Exception e) {
            //    e.printStackTrace();
            System.out.println(path + " is not available.");
        } finally {
            return classMethods;
        }
    }

    private static HashMap<String, HashMap<String, String>> listClassFields(String path) {
        HashMap<String, HashMap<String, String>> classFields = new HashMap<>();

        try {
            if (path != null) {
                JarFile jarFile = new JarFile(path);

                URL[] urls = {new URL("jar:file:" + path + "!/")};
                URLClassLoader cl = URLClassLoader.newInstance(urls);

                Enumeration<JarEntry> allEntries = jarFile.entries();
                while (allEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) allEntries.nextElement();
                    String name = entry.getName();

                    //        System.out.println("Entry Name : " + name);
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        name = name.substring(0, name.lastIndexOf(".class"));
                        name = name.replaceAll("/", ".");
                        name = name.replaceAll("-", ".");

                        try {
                            Class clas = cl.loadClass(name);

                            HashMap<String, String> allFields = new HashMap<>();

                            for (Class<?> c = clas; c != null; c = c.getSuperclass()) {
                                for (Field f : c.getFields()) {
                                    if (!Modifier.toString(f.getModifiers()).contains("private")) {
                                        if (!allFields.keySet().contains(f)) {
                                            allFields.put(f.getName(), f.getType().toString());
                                        }
                                    }
                                }
                            }
                            classFields.put(name, allFields);
                        } catch (Exception ex) {
                            //    ex.printStackTrace();
                        }
                    }

                }
                jarFile.close();
            }
        } catch (Exception e) {
            //    e.printStackTrace();
            System.out.println(path + " is not available.");
        } finally {
            return classFields;
        }
    }

}

class Artifact {

    String groupId;
    String artifactId;
    String version;
    String type;
    String scope;
}
