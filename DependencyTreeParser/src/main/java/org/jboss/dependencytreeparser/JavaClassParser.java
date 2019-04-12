/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.dependencytreeparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author panos
 */
public class JavaClassParser {

    static HashMap<String, ArrayList<String>> testLibraries = new HashMap<>();
    static HashMap<String, HashSet<String>> internalPackagesAndClasses = new HashMap<>();
    static HashMap<String, HashMap<String, String[]>> internalClassMethods = new HashMap<>();
    static HashMap<String, HashMap<String, String>> internalClassFields = new HashMap<>();
    static HashMap<String, ArrayList<String>> internalClasses = new HashMap<>();
    static HashMap<String, ParsedTests> testData = new HashMap<String, ParsedTests>();
    static HashSet<String> filesTest = new HashSet<>();

    public static HashMap<String, ArrayList<String>> testLibraryUsage() {
        String basedir = System.getProperty("BaseDir");
        String sourcePath = System.getProperty("SourcePath");
        String server = System.getProperty("Server");
        String version = System.getProperty("Version");
        String versionOrderDir = System.getProperty("VersionOrderDir");
        String disableSnapshotVersions = System.getProperty("DisableSnapshotVersions");

        HashMap<String, ArrayList<String>> testLibraries = fileProcessing(basedir, sourcePath, server, version, versionOrderDir, "@EapAdditionalTestsuite", disableSnapshotVersions);

        return testLibraries;
    }

    public static HashMap<String, ParsedTests> getTestData() {
        return testData;
    }

    public static void setTestData(HashMap<String, ParsedTests> testData) {
        JavaClassParser.testData = testData;
    }

    public static HashMap<String, ArrayList<String>> getInternalClasses() {
        return internalClasses;
    }

    public static void setInternalClasses(HashMap<String, ArrayList<String>> internalClasses) {
        JavaClassParser.internalClasses = internalClasses;
    }

    public static HashMap<String, ArrayList<String>> getTestLibraries() {
        return testLibraries;
    }

    public static HashMap<String, HashSet<String>> getInternalPackagesAndClasses() {
        return internalPackagesAndClasses;
    }

    public static HashMap<String, HashMap<String, String[]>> getInternalClassMethods() {
        return internalClassMethods;
    }

    public static HashMap<String, HashMap<String, String>> getInternalClassFields() {
        return internalClassFields;
    }

    private static void readInternalPackagesAndClasses(FileData fd) {
        if (internalPackagesAndClasses.get(fd.packageName.replaceAll("/", ".")) == null) {
            internalPackagesAndClasses.put(fd.packageName.replaceAll("/", "."), new HashSet<>());
        }
        internalPackagesAndClasses.get(fd.packageName.replaceAll("/", ".")).add(fd.fileName.replaceAll("\\.java", ""));
    }

    private static void readInternalClassMethods(String file, FileData fd) throws IOException {
        InputStream in = null;
        CompilationUnit cu = null;
        try {
            in = new FileInputStream(file);
            cu = JavaParser.parse(in);

            if (internalClassMethods.get(fd.packageName.replaceAll("/", ".") + "." + fd.fileName.replaceAll("\\.java", "")) == null) {
                internalClassMethods.put(fd.packageName.replaceAll("/", ".") + "." + fd.fileName.replaceAll("\\.java", ""), new HashMap<String, String[]>());
            }

            NodeList<TypeDeclaration<?>> types = cu.getTypes();
            for (TypeDeclaration<?> type : types) {
                NodeList<BodyDeclaration<?>> members = type.getMembers();
                for (BodyDeclaration<?> member : members) {
                    if (member instanceof MethodDeclaration) {
                        MethodDeclaration method = (MethodDeclaration) member;
                        HashMap<String, String[]> arg = internalClassMethods.get(fd.packageName.replaceAll("/", ".") + "." + fd.fileName.replaceAll("\\.java", ""));
                        String[] params = new String[method.getParameters().size()];

                        for (int i = 0; i < method.getParameters().size(); i++) {
                            params[i] = method.getParameters().get(i).getTypeAsString();
                        }

                        arg.put(method.getName().toString(), params);
                        arg.put(method.getName().toString() + "_Return_Type", new String[]{method.getTypeAsString()});
                    }
                    if (member instanceof ConstructorDeclaration) {
                        ConstructorDeclaration construct = (ConstructorDeclaration) member;
                        HashMap<String, String[]> arg = internalClassMethods.get(fd.packageName.replaceAll("/", ".") + "." + fd.fileName.replaceAll("\\.java", ""));
                        String[] params = new String[construct.getParameters().size()];

                        for (int i = 0; i < construct.getParameters().size(); i++) {
                            params[i] = construct.getParameters().get(i).getTypeAsString();
                        }

                        ((HashMap<String, String[]>) arg).put(construct.getName().toString() + "_Constructor", params);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            in.close();
        }
    }

    private static void readInternalClassFields(String file, FileData fd) throws IOException {
        InputStream in = null;
        CompilationUnit cu = null;
        try {
            in = new FileInputStream(file);
            cu = JavaParser.parse(in);

            if (internalClassFields.get(fd.packageName.replaceAll("/", ".") + "." + fd.fileName.replaceAll("\\.java", "")) == null) {
                internalClassFields.put(fd.packageName.replaceAll("/", ".") + "." + fd.fileName.replaceAll("\\.java", ""), new HashMap<String, String>());
            }

            NodeList<TypeDeclaration<?>> types = cu.getTypes();
            for (TypeDeclaration<?> type : types) {
                NodeList<BodyDeclaration<?>> members = type.getMembers();
                for (BodyDeclaration<?> member : members) {
                    if (member instanceof FieldDeclaration) {
                        FieldDeclaration field = (FieldDeclaration) member;
                        HashMap<String, String> arg = internalClassFields.get(fd.packageName.replaceAll("/", ".") + "." + fd.fileName.replaceAll("\\.java", ""));

                        if (!field.getModifiers().contains("private")) {
                            arg.put(field.getVariables().get(0).getName().toString(), field.getVariables().get(0).getType().toString());
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            in.close();
        }
    }

    public static HashMap<String, ArrayList<String>> fileProcessing(String basedir, String sourcePath, String server, String version, String versionOrderDir, String searchString, String disableSnapshotVersions) {
        File folder = new File(sourcePath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return null;
        }

        try {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    fileProcessing(basedir, file.getAbsolutePath(), server, version, versionOrderDir, searchString, disableSnapshotVersions);
                } else {
                    ArrayList<FileData> output = checkFileForAnnotation(file.getAbsolutePath(), "@EapAdditionalTestsuite", server);
                    for (FileData dest : output) {
                        if (dest.minVersion != null) {
                            boolean isSnapshot = false;
                            if (disableSnapshotVersions != null && disableSnapshotVersions.contains("true")) {
                                isSnapshot = version.contains("SNAPSHOT");
                            }
                            String[] versionRelease = version.split("-");
                            int verRelease1 = 0;
                            String[] verPart = versionRelease[0].split("\\.");
                            if (verPart.length > 2) {
                                verRelease1 = Integer.parseInt(verPart[0] + verPart[1] + verPart[2]);
                            }
                            String[] subVersions = dest.minVersion.split("-");
                            verPart = subVersions[0].split("\\.");
                            int verRelease2 = 0;
                            if (verPart.length > 2) {
                                verRelease2 = Integer.parseInt(verPart[0] + verPart[1] + verPart[2]);
                            }

                            int verRelease3 = 0;
                            String[] subVersionsMax = null;

                            if (dest.maxVersion != null) {
                                subVersionsMax = dest.maxVersion.split("-");
                                verPart = subVersionsMax[0].split("\\.");

                                if (verPart.length > 2) {
                                    verRelease3 = Integer.parseInt(verPart[0] + verPart[1] + verPart[2]);
                                }
                            }

                            if ((subVersions.length >= 1 && verRelease1 == verRelease2)) {

                                String[] vf = new String[2];
                                if (verRelease1 == verRelease2 && subVersions[0].split("\\.").length > 3) {
                                    vf[0] = subVersions[0].substring(0, subVersions[0].lastIndexOf("."));
                                    vf[1] = subVersions[0].substring(subVersions[0].lastIndexOf(".") + 1);
                                } else {
                                    vf[0] = subVersions[0];
                                    vf[1] = null;
                                }

                                File versionFolder = new File(basedir + "/" + versionOrderDir + "/" + server + "/" + vf[0]);
                                if (vf != null && versionFolder.exists()) {
                                    String versionsString = readFile(basedir + "/" + versionOrderDir + "/" + server + "/" + vf[0]);
                                    if (versionsString != null) {
                                        String[] versions = null;
                                        if (vf[1] != null) {
                                            versions = versionsString.substring(versionsString.indexOf(vf[1])).split(",");
                                        } else {
                                            versions = versionsString.split(",");
                                        }
                                        for (String versionPart : versionRelease) {
                                            if (versionPart.contains(".")) {
                                                String[] versionNums = versionPart.split("\\.");
                                                String lastPart = versionNums[versionNums.length - 1];
                                                if (!lastPart.matches("[0-9]+")) {
                                                    for (String ver : versions) {
                                                        if (lastPart.contains(ver)) {
                                                            if (!(ver.equals(lastPart) && isSnapshot)) {
                                                                if ((verRelease3 == 0 || verRelease1 < verRelease3)) {
                                                                    copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName), false);
                                                                    String f = basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName;
                                                                    String extesnion = readTestLibrariesFromFile(f, testLibraries, searchString, basedir + "/" + dest.fileBaseDir + "/");
                                                                    readInternalPackagesAndClasses(dest);
                                                                    readInternalClassMethods(f, dest);
                                                                    readInternalClassFields(f, dest);
                                                                    TestsuiteParser.parse(f);
                                                                    ParsedTests ps = new ParsedTests();
                                                                    ps.extension = extesnion;
                                                                    ps.types.addAll(TestsuiteParser.getTypes());
                                                                    ps.fields.putAll(TestsuiteParser.getFields());
                                                                    ps.methods.putAll(TestsuiteParser.getMethods());
                                                                    ps.imports.addAll(TestsuiteParser.getImports());
                                                                    ps.classInstanceCreations.addAll(TestsuiteParser.getClassInstanceCreations());
                                                                    ps.methodInvocations.addAll(TestsuiteParser.getMethodInvocations());
                                                                    ps.typesNotResolved.addAll(TestsuiteParser.getTypesNotResolved());
                                                                    ps.methodsNotResolved.addAll(TestsuiteParser.getMethodsNotResolved());
                                                                    ps.packageName = TestsuiteParser.packageName;
                                                                    testData.put(dest.packageName.replaceAll("/", ".") + "." + dest.fileName.replace(".java", ""), ps);
                                                                } else if (verRelease1 == verRelease3) {
                                                                    procedure0(file, dest, server, basedir, versionOrderDir, verRelease1, verRelease3, subVersionsMax, versionRelease, isSnapshot, searchString);
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    if (!(verRelease1 == verRelease2 && isSnapshot)) {
                                                        if ((verRelease3 == 0 || verRelease1 < verRelease3)) {
                                                            copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName), false);
                                                            String f = basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName;
                                                            String extesnion = readTestLibrariesFromFile(f, testLibraries, searchString, basedir + "/" + dest.fileBaseDir + "/");
                                                            readInternalPackagesAndClasses(dest);
                                                            readInternalClassMethods(f, dest);
                                                            readInternalClassFields(f, dest);
                                                            TestsuiteParser.parse(f);
                                                            ParsedTests ps = new ParsedTests();
                                                            ps.extension = extesnion;
                                                            ps.types.addAll(TestsuiteParser.getTypes());
                                                            ps.fields.putAll(TestsuiteParser.getFields());
                                                            ps.methods.putAll(TestsuiteParser.getMethods());
                                                            ps.imports.addAll(TestsuiteParser.getImports());
                                                            ps.classInstanceCreations.addAll(TestsuiteParser.getClassInstanceCreations());
                                                            ps.methodInvocations.addAll(TestsuiteParser.getMethodInvocations());
                                                            ps.typesNotResolved.addAll(TestsuiteParser.getTypesNotResolved());
                                                            ps.methodsNotResolved.addAll(TestsuiteParser.getMethodsNotResolved());
                                                            ps.packageName = TestsuiteParser.packageName;
                                                            testData.put(dest.packageName.replaceAll("/", ".") + "." + dest.fileName.replace(".java", ""), ps);
                                                        } else if (verRelease1 == verRelease3) {
                                                            procedure0(file, dest, server, basedir, versionOrderDir, verRelease1, verRelease3, subVersionsMax, versionRelease, isSnapshot, searchString);
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            } else if (verRelease1 >= verRelease2) {
                                if (!(verRelease1 == verRelease2 && isSnapshot)) {
                                    if (verRelease3 == 0 || verRelease1 < verRelease3) {
                                        if (verRelease1 != verRelease2 || !isSnapshot) {
                                            copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName), false);
                                            String f = basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName;
                                            String extesnion = readTestLibrariesFromFile(f, testLibraries, searchString, basedir + "/" + dest.fileBaseDir + "/");
                                            readInternalPackagesAndClasses(dest);
                                            readInternalClassMethods(f, dest);
                                            readInternalClassFields(f, dest);
                                            TestsuiteParser.parse(f);
                                            ParsedTests ps = new ParsedTests();
                                            ps.extension = extesnion;
                                            ps.types.addAll(TestsuiteParser.getTypes());
                                            ps.fields.putAll(TestsuiteParser.getFields());
                                            ps.methods.putAll(TestsuiteParser.getMethods());
                                            ps.imports.addAll(TestsuiteParser.getImports());
                                            ps.classInstanceCreations.addAll(TestsuiteParser.getClassInstanceCreations());
                                            ps.methodInvocations.addAll(TestsuiteParser.getMethodInvocations());
                                            ps.typesNotResolved.addAll(TestsuiteParser.getTypesNotResolved());
                                            ps.methodsNotResolved.addAll(TestsuiteParser.getMethodsNotResolved());
                                            ps.packageName = TestsuiteParser.packageName;
                                            testData.put(dest.packageName.replaceAll("/", ".") + "." + dest.fileName.replace(".java", ""), ps);
                                        }
                                    } else if (verRelease1 == verRelease3) {
                                        procedure0(file, dest, server, basedir, versionOrderDir, verRelease1, verRelease3, subVersionsMax, versionRelease, isSnapshot, searchString);
                                    }
                                }
                            }
                        } else {
                            copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName), false);
                            String f = basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName;
                            String extesnion = readTestLibrariesFromFile(f, testLibraries, searchString, basedir + "/" + dest.fileBaseDir + "/");
                            readInternalPackagesAndClasses(dest);
                            readInternalClassMethods(f, dest);
                            readInternalClassFields(f, dest);
                            TestsuiteParser.parse(f);
                            ParsedTests ps = new ParsedTests();
                            ps.extension = extesnion;
                            ps.types.addAll(TestsuiteParser.getTypes());
                            ps.fields.putAll(TestsuiteParser.getFields());
                            ps.methods.putAll(TestsuiteParser.getMethods());
                            ps.imports.addAll(TestsuiteParser.getImports());
                            ps.classInstanceCreations.addAll(TestsuiteParser.getClassInstanceCreations());
                            ps.methodInvocations.addAll(TestsuiteParser.getMethodInvocations());
                            ps.typesNotResolved.addAll(TestsuiteParser.getTypesNotResolved());
                            ps.methodsNotResolved.addAll(TestsuiteParser.getMethodsNotResolved());
                            ps.packageName = TestsuiteParser.packageName;
                            testData.put(dest.packageName.replaceAll("/", ".") + "." + dest.fileName.replace(".java", ""), ps);
                        }
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return testLibraries;
        }
    }

    private static void procedure0(File file, FileData dest, String server, String basedir, String versionOrderDir, int verRelease1, int verRelease3, String[] subVersionsMax, String[] versionRelease, boolean isSnapshot, String searchString) throws IOException, ClassNotFoundException {
        if (verRelease1 == verRelease3) {

            String[] vf = new String[2];
            if (subVersionsMax != null && verRelease1 == verRelease3 && subVersionsMax[0].split("\\.").length > 3) {
                vf[0] = subVersionsMax[0].substring(0, subVersionsMax[0].lastIndexOf("."));
                vf[1] = subVersionsMax[0].substring(subVersionsMax[0].lastIndexOf(".") + 1);
            } else {
                vf[0] = subVersionsMax[0];
                vf[1] = null;
            }

            File versionFolder = new File(basedir + "/" + versionOrderDir + "/" + server + "/" + vf[0]);
            if (vf != null && versionFolder.exists()) {
                String versionsString = readFile(basedir + "/" + versionOrderDir + "/" + server + "/" + vf[0]);
                if (versionsString != null) {
                    String[] versions = null;
                    if (vf[1] != null) {
                        versions = versionsString.substring(0, versionsString.indexOf(vf[1])).concat(vf[1]).split(",");
                    } else {
                        versions = versionsString.split(",");
                    }
                    for (String versionPart : versionRelease) {
                        if (versionPart.contains(".")) {
                            String[] versionNums = versionPart.split("\\.");
                            String lastPart = versionNums[versionNums.length - 1];
                            if (!lastPart.matches("[0-9]+")) {
                                for (String ver : versions) {
                                    if (lastPart.contains(ver) && !(ver.equals(lastPart) && isSnapshot)) {
                                        copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName), false);
                                        String f = basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName;
                                        String extesnion = readTestLibrariesFromFile(f, testLibraries, searchString, basedir + "/" + dest.fileBaseDir + "/");
                                        readInternalPackagesAndClasses(dest);
                                        readInternalClassMethods(f, dest);
                                        readInternalClassFields(f, dest);
                                        TestsuiteParser.parse(f);
                                        ParsedTests ps = new ParsedTests();
                                        ps.extension = extesnion;
                                        ps.types.addAll(TestsuiteParser.getTypes());
                                        ps.fields.putAll(TestsuiteParser.getFields());
                                        ps.methods.putAll(TestsuiteParser.getMethods());
                                        ps.imports.addAll(TestsuiteParser.getImports());
                                        ps.classInstanceCreations.addAll(TestsuiteParser.getClassInstanceCreations());
                                        ps.methodInvocations.addAll(TestsuiteParser.getMethodInvocations());
                                        ps.typesNotResolved.addAll(TestsuiteParser.getTypesNotResolved());
                                        ps.methodsNotResolved.addAll(TestsuiteParser.getMethodsNotResolved());
                                        ps.packageName = TestsuiteParser.packageName;
                                        testData.put(dest.packageName.replaceAll("/", ".") + "." + dest.fileName.replace(".java", ""), ps);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (verRelease1 <= verRelease3) {
                if (!(verRelease1 == verRelease3 && isSnapshot)) {
                    copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName), false);
                    String f = basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName;
                    String extesnion = readTestLibrariesFromFile(f, testLibraries, searchString, basedir + "/" + dest.fileBaseDir + "/");
                    readInternalPackagesAndClasses(dest);
                    readInternalClassMethods(f, dest);
                    readInternalClassFields(f, dest);
                    TestsuiteParser.parse(f);
                    ParsedTests ps = new ParsedTests();
                    ps.extension = extesnion;
                    ps.types.addAll(TestsuiteParser.getTypes());
                    ps.fields.putAll(TestsuiteParser.getFields());
                    ps.methods.putAll(TestsuiteParser.getMethods());
                    //    System.out.println("ps fields : " + TestsuiteParser.getFields().keySet().toString() + " " + ps.fields.keySet().toString());
                    ps.imports.addAll(TestsuiteParser.getImports());
                    ps.classInstanceCreations.addAll(TestsuiteParser.getClassInstanceCreations());
                    ps.methodInvocations.addAll(TestsuiteParser.getMethodInvocations());
                    ps.typesNotResolved.addAll(TestsuiteParser.getTypesNotResolved());
                    ps.methodsNotResolved.addAll(TestsuiteParser.getMethodsNotResolved());
                    ps.packageName = TestsuiteParser.packageName;
                    testData.put(dest.packageName.replaceAll("/", ".") + "." + dest.fileName.replace(".java", ""), ps);
                }
            }
        }

    }

    public static void fileDiscovery(String sourcePath) {
        File folder = new File(sourcePath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        try {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    fileDiscovery(file.getAbsolutePath());
                } else if (file.getAbsolutePath().endsWith(".java")) {
                    filesTest.add(file.getAbsolutePath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void classFileDiscovery(String sourcePath) {
        File folder = new File(sourcePath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        try {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    classFileDiscovery(file.getAbsolutePath());
                } else if (file.getAbsolutePath().endsWith(".class")) {
                    filesTest.add(file.getAbsolutePath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String readTestLibrariesFromFile(String file, HashMap<String, ArrayList<String>> testLibraries, String searchString, String filePrefix) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String extension = null;
        String packageName = "";
        ArrayList<String> inclasses = new ArrayList<>();

        try {
            String lineEx = br.readLine();
            while (lineEx != null) {
                if (lineEx.contains("extends") && lineEx.contains("class")) {
                    extension = lineEx.substring(lineEx.indexOf("extends") + 7).trim().split(" ")[0];
                    break;
                } else if (lineEx.contains("implements") && lineEx.contains("class")) {
                    extension = lineEx.substring(lineEx.indexOf("implements") + 10).trim().split(" ")[0];
                    break;
                }
                lineEx = br.readLine();
            }
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                if (line.trim().startsWith("package")) {
                    String library = line.trim().replaceFirst("package ", "").replaceAll(";", "");
                    packageName = library;
                } else if (line.trim().startsWith("import")) {
                    String library = line.trim().replaceFirst("import", "").trim().replaceFirst("static", "").trim().replaceAll(";", "");
                    if (extension != null && library.contains(extension)) {
                        extension = library.substring(0, library.indexOf(extension) + extension.length());
                    }
                    String lib2 = library;
                    String className = null;
                    while (lib2.lastIndexOf(".") != -1) {
                        if (Character.isUpperCase(lib2.substring(lib2.lastIndexOf(".") + 1).charAt(0))) {
                            library = lib2.substring(0, lib2.lastIndexOf("."));
                            className = lib2.substring(lib2.lastIndexOf(".") + 1);
                        }
                        lib2 = lib2.substring(0, lib2.lastIndexOf("."));
                    }
                    if (!testLibraries.keySet().contains(library)) {
                        if (testLibraries.get(library) == null) {
                            ArrayList<String> al = new ArrayList<>();
                            if (className != null) {
                                al.add(className);
                            }
                            testLibraries.put(library, al);
                        }
                    } else if (className != null) {
                        if (!testLibraries.get(library).contains(className)) {
                            testLibraries.get(library).add(className);
                        }
                    }
                } else if (line.contains("class") && !line.contains(".class") && line.contains("{") && !line.contains("}")) {
                    String[] parts = line.split(" ");
                    int i = 0;
                    for (String s : parts) {
                        if (s.contains("class")) {
                            inclasses.add(parts[i + 1]);
                            break;
                        }
                        i++;
                    }
                }

                line = br.readLine();
            }

            internalClasses.put(file.replaceAll(filePrefix, "").replaceAll("/", ".").replaceAll(".java", ""), inclasses);
        } finally {
            br.close();
            if (extension != null && !extension.contains(".")) {
                extension = packageName + "." + extension;
            }
            return extension;
        }
    }

    private static String readFile(String file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        return sb.toString();
    }

    private static ArrayList<FileData> checkFileForAnnotation(String file, String annotationName, String server) throws ClassNotFoundException {
        String[] destinations = null;
        String annotationLine = null;
        ArrayList<FileData> result = new ArrayList<FileData>();
        String packageName = "";
        File f = new File(file);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("package")) {
                    packageName = line.replaceAll("package ", "").replaceAll(";", "").trim();
                }
                if (line.contains(annotationName)) {
                    annotationLine = line;
                    destinations = annotationLine.split("\"");
                    for (String path : destinations) {
                        if (!path.contains(",") && path.contains("/" + server + "/")) {
                            if (!path.contains("#") && !path.contains("*")) {
                                result.add(new FileData(f.getName(), packageName.replaceAll("\\.", "/"), path, null, null));
                            } else if (!path.contains("*")) {
                                String[] pathVersion = path.split("#");
                                result.add(new FileData(f.getName(), packageName.replaceAll("\\.", "/"), pathVersion[0], pathVersion[1], null));
                            } else {
                                String[] pathVersion = path.split("\\*");
                                String[] pathVersion2 = pathVersion[0].split("#");
                                result.add(new FileData(f.getName(), packageName.replaceAll("\\.", "/"), pathVersion2[0], pathVersion2[1], pathVersion[1]));
                            }
                        }
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return result;
    }
    
    private static void ensureTargetDirectoryExists(File aTargetDir) {
        if (!aTargetDir.exists()) {
            aTargetDir.mkdirs();
        }
    }
    
    private static void copyWithStreams(File aSourceFile, File aTargetFile, boolean aAppend) {
        ensureTargetDirectoryExists(aTargetFile.getParentFile());
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            try {
                byte[] bucket = new byte[32 * 1024];
                inStream = new BufferedInputStream(new FileInputStream(aSourceFile));
                outStream = new BufferedOutputStream(new FileOutputStream(aTargetFile, aAppend));
                int bytesRead = 0;
                while (bytesRead != -1) {
                    bytesRead = inStream.read(bucket); //-1, 0, or more
                    if (bytesRead > 0) {
                        outStream.write(bucket, 0, bytesRead);
                    }
                }
            } finally {
                if (inStream != null) {
                    inStream.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

class FileData {

    protected String fileName;
    protected String packageName;
    protected String fileBaseDir;
    protected String minVersion;
    protected String maxVersion;
    protected int lineNum;

    public FileData(String fileName, String packageName, String fileBaseDir, String minVersion, String maxVersion) {
        this.fileName = fileName;
        this.packageName = packageName;
        this.fileBaseDir = fileBaseDir;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    public FileData(String fileName, String packageName, String fileBaseDir, String minVersion, String maxVersion, int lineNum) {
        this.fileName = fileName;
        this.packageName = packageName;
        this.fileBaseDir = fileBaseDir;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.lineNum = lineNum;
    }

}

class MethodVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        String[] params = new String[n.getParameters().size()];

        for (int i = 0; i < n.getParameters().size(); i++) {
            params[i] = n.getParameters().get(i).getTypeAsString();
        }

        ((HashMap<String, String[]>) arg).put(n.getName().toString(), params);
        ((HashMap<String, String[]>) arg).put(n.getName().toString() + "_Return_Type", new String[]{n.getTypeAsString()});

    }

    @Override
    public void visit(ConstructorDeclaration n, Object arg) {
        String[] params = new String[n.getParameters().size()];

        for (int i = 0; i < n.getParameters().size(); i++) {
            params[i] = n.getParameters().get(i).getTypeAsString();
        }

        ((HashMap<String, String[]>) arg).put(n.getName().toString() + "_Constructor", params);

    }

}

class ParsedTests {

    public String extension = null;
    public HashSet<String> types = new HashSet<>();
    public HashMap<String, String> fields = new HashMap<>();
    public HashMap<String, String[]> methods = new HashMap<>();
    public HashSet<String> imports = new HashSet<>();
    public HashSet<String> typesNotResolved = new HashSet<>();
    public HashSet<String> methodsNotResolved = new HashSet<>();
    public String packageName = null;
    public ArrayList<ClassInfo> classInstanceCreations = new ArrayList<>();
    public ArrayList<MethodInfo> methodInvocations = new ArrayList<>();
}
