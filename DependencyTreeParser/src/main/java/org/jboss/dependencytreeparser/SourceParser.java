package org.jboss.dependencytreeparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

/**
 *
 * @author panos
 */
public class SourceParser {

    static HashMap<String, String> fields = new HashMap<>();
    static HashSet<String> imports = new HashSet<>();
    static HashMap<String, MethodInfo2> methods = new HashMap<>();
    static HashSet<String> types = new HashSet<>();
    static String packageName;

    public static HashMap<String, String> getFields() {
        return fields;
    }

    public static void setFields(HashMap<String, String> fields) {
        SourceParser.fields = fields;
    }

    public static HashSet<String> getImports() {
        return imports;
    }

    public static void setImports(HashSet<String> imports) {
        SourceParser.imports = imports;
    }

    public static String getPackageName() {
        return packageName;
    }

    public static void setPackageName(String packageName) {
        SourceParser.packageName = packageName;
    }

    public static void parse(String str) throws IOException {

        fields.clear();
        methods.clear();
        imports.clear();
        types.clear();
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(readFileToString(str).toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {

            Set names = new HashSet();
            int blockCount = 0;

            public boolean visit(FieldDeclaration node) {
                String name = node.fragments().get(0).toString().split("=")[0].trim();
                String type = node.getType().toString();

                if (!node.modifiers().toString().contains("private")) {
                    fields.put(name, type);
                }

                ArrayList<String> types0 = new ArrayList<>();

                String type2 = null;
                do {
                    if (type.contains("[")) {
                        type = type.replaceAll("\\[\\]", "");
                        if (type.contains("[")) {
                            type = type.substring(0, type.indexOf("["));
                        }
                    }

                    if (type.contains("<")) {
                        String type3 = type;
                        type = type.substring(0, type.indexOf("<"));
                        if (type3.substring(type3.indexOf("<") + 1).startsWith("<>") || type3.substring(type.indexOf("<") + 1).startsWith("<T>")) {
                            type2 = null;
                        } else if (type3.indexOf("<") >= 0 && type3.indexOf(">") >= 0 && type3.indexOf("<") < type3.indexOf(">")) {
                            type2 = type3.substring(type3.indexOf("<") + 1, type3.lastIndexOf(">"));
                            if (type2.contains(",")) {
                                if (type2.substring(0, type2.indexOf(",")).contains("<")) {
                                    types0.add(type2);
                                } else {
                                    types0.add(type2.substring(0, type2.indexOf(",")));
                                    types0.add(type2.substring(type2.indexOf(",") + 1));
                                }
                            } else {
                                types0.add(type2);
                            }
                        }

                    }

                    types.addAll(Arrays.asList(type.split(" extends ")));
                    if (types0.size() != 0) {
                        type = types0.remove(0);
                    } else {
                        type = null;
                    }
                } while (type != null);

                return true;
            }

            public boolean visit(MethodDeclaration node) {
                if (node.getName().getIdentifier() != null) {

                    MethodInfo2 mf = new MethodInfo2();
                    mf.name = node.getName().toString();
                    if (node.getReturnType2() != null) {
                        mf.returnType = node.getReturnType2().toString();
                    } else {
                        mf.returnType = null;
                    }

                    List params = node.parameters();
                    for (Object s : params) {
                        String type = ((SingleVariableDeclaration) s).getType().toString();

                        ArrayList<String> types0 = new ArrayList<>();

                        String type2 = null;
                        do {
                            if (type.contains("[")) {
                                type = type.replaceAll("\\[\\]", "");
                                if (type.contains("[")) {
                                    type = type.substring(0, type.indexOf("["));
                                }
                            } else if (type.contains("<")) {
                                String type3 = type;
                                type = type.substring(0, type.indexOf("<"));
                                if (type3.substring(type3.indexOf("<") + 1).startsWith("<>") || type3.substring(type.indexOf("<") + 1).startsWith("<T>")) {
                                    type2 = null;
                                } else if (type3.indexOf("<") >= 0 && type3.indexOf(">") >= 0 && type3.indexOf("<") < type3.indexOf(">")) {
                                    type2 = type3.substring(type3.indexOf("<") + 1, type3.lastIndexOf(">"));
                                    if (type2.contains(",")) {
                                        if (type2.substring(0, type2.indexOf(",")).contains("<")) {
                                            types0.add(type2);
                                        } else {
                                            types0.add(type2.substring(0, type2.indexOf(",")));
                                            types0.add(type2.substring(type2.indexOf(",") + 1));
                                        }
                                    } else {
                                        types0.add(type2);
                                    }
                                }

                            }

                            types.addAll(Arrays.asList(type.split(" extends ")));
                            if (types0.size() != 0) {
                                type = types0.remove(0);
                            } else {
                                type = null;
                            }
                        } while (type != null);

                    }

                    mf.paramTypes = types;

                    methods.put(mf.name, mf);
                }
                return true;
            }

            public boolean visit(ImportDeclaration node) {

                imports.add(node.getName().toString());

                return true;
            }

            public boolean visit(PackageDeclaration node) {

                packageName = node.getName().toString();

                return true;
            }
        });

    }

    private static String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        return fileData.toString();
    }

    static class MethodInfo2 {

        String name;
        String returnType;
        HashSet<String> paramTypes;
    }

}
