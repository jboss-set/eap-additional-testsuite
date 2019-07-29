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
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

/**
 *
 * @author panos
 */
public class TestsuiteParser {

    static HashSet<String> types = new HashSet<>();
    static HashMap<String, String> fields = new HashMap<>();
    static HashMap<String, String[]> methods = new HashMap<>();
    static HashSet<String> imports = new HashSet<>();
    static HashSet<String> typesNotResolved = new HashSet<>();
    static HashSet<String> methodsNotResolved = new HashSet<>();
    static ArrayList<ClassInfo> classInstanceCreations = new ArrayList<>();
    static ArrayList<MethodInfo> methodInvocations = new ArrayList<>();
    static String packageName;

    static HashMap<String, String> importedClassFields = new HashMap<>();

    public static HashMap<String, String[]> getMethods() {
        return methods;
    }

    public static void setMethods(HashMap<String, String[]> methods) {
        TestsuiteParser.methods = methods;
    }

    public static String getPackageName() {
        return packageName;
    }

    public static void setPackageName(String packageName) {
        TestsuiteParser.packageName = packageName;
    }

    public static HashMap<String, String> getImportedClassFields() {
        return importedClassFields;
    }

    public static void setImportedClassFields(HashMap<String, String> importedClassFields) {
        TestsuiteParser.importedClassFields = importedClassFields;
    }

    public static HashMap<String, String> getFields() {
        return fields;
    }

    public static void setFields(HashMap<String, String> fields) {
        TestsuiteParser.fields = fields;
    }

    public static HashSet<String> getTypes() {
        return types;
    }

    public static HashSet<String> getImports() {
        return imports;
    }

    public static void setImports(HashSet<String> imports) {
        TestsuiteParser.imports = imports;
    }

    public static void setTypes(HashSet<String> types) {
        TestsuiteParser.types = types;
    }

    public static ArrayList<ClassInfo> getClassInstanceCreations() {
        return classInstanceCreations;
    }

    public static void setClassInstanceCreations(ArrayList<ClassInfo> classInstanceCreations) {
        TestsuiteParser.classInstanceCreations = classInstanceCreations;
    }

    public static ArrayList<MethodInfo> getMethodInvocations() {
        return methodInvocations;
    }

    public static void setMethodInvocations(ArrayList<MethodInfo> methodInvocations) {
        TestsuiteParser.methodInvocations = methodInvocations;
    }

    public static void parse(String str) throws IOException {

        types.clear();
        fields.clear();
        methods.clear();
        classInstanceCreations.clear();
        typesNotResolved.clear();
        methodsNotResolved.clear();
        methodInvocations.clear();
        imports.clear();
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(readFileToString(str).toCharArray());
    
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        HashMap<String, String> declarations = new HashMap();

        cu.accept(new ASTVisitor() {

            Set names = new HashSet();
            int blockCount = 0;

            public boolean visit(FieldDeclaration node) {
                String name = node.fragments().get(0).toString().split("=")[0].trim();
                String type = node.getType().toString();

                declarations.put(name, type);
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
                        } else {
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

                return false;
            }
            
            public boolean visit(EnumConstantDeclaration node) {
                String type = node.getName().toString();
                System.out.println("ccccc : " + type);
                List<String> constants = node.arguments();
                System.out.println("ccccc : " + type + "." + constants.toString());

                for(String f : constants) {
                    System.out.println("ccccc : " + type + "." + f);
                    declarations.put(type + "." + f, type);
                    if (!node.modifiers().toString().contains("private")) {
                        fields.put(type + "." + f, type);
                    }
                }

                return false;
            }

            public boolean visit(MethodDeclaration node) {
                if (node.getName().getIdentifier() != null) {
                    String returnType = null;
                    if (node.getReturnType2() == null) {
                        returnType = "";
                    } else {
                        returnType = node.getReturnType2().toString();
                    }

                    HashMap<String, String> bDeclarations = new HashMap();
                    bDeclarations.putAll(importedClassFields);
                    bDeclarations.putAll(declarations);

                    methods.put(node.getName().toString() + "_Return_Type", new String[]{returnType});
                    String[] methodParams = new String[node.parameters().size()];
                    List params = node.parameters();
                    int i = 0;
                    for (Object s : params) {
                        String type = ((SingleVariableDeclaration) s).getType().toString();

                        bDeclarations.put(((SingleVariableDeclaration) s).getName().toString(), type);

                        ArrayList<String> types0 = new ArrayList<>();

                        String type2 = null;
                        String typeF = null;
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
                                } else {
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
                                typeF = type;
                            } else {
                                type = null;
                            }
                        } while (type != null);

                        methodParams[i++] = typeF;

                    }

                    methods.put(node.getName().toString() + "_Return_Type", methodParams);

                    Block block = node.getBody();

                    blockIterate(block, cu, bDeclarations);
                }
                return false;
            }

            public boolean visit(ImportDeclaration node) {

                imports.add(node.getName().toString());

                if (DependencyTreeMethods.jarClassPaths.containsKey(node.getName().toString())) {
                    importedClassFields = DependencyTreeMethods.listFieldsOfJarClass(DependencyTreeMethods.jarClassPaths.get(node.getName().toString()), node.getName().toString());
                    fields.putAll(importedClassFields);
                }

                return false;
            }

            public boolean visit(PackageDeclaration node) {

                packageName = node.getName().toString();

                return true;
            }
        });

    }

    public static HashSet<String> getTypesNotResolved() {
        return typesNotResolved;
    }

    public static void setTypesNotResolved(HashSet<String> typesNotResolved) {
        TestsuiteParser.typesNotResolved = typesNotResolved;
    }

    public static HashSet<String> getMethodsNotResolved() {
        return methodsNotResolved;
    }

    public static void setMethodsNotResolved(HashSet<String> methodsNotResolved) {
        TestsuiteParser.methodsNotResolved = methodsNotResolved;
    }

    private static void blockIterate(final Block block, final CompilationUnit cu, HashMap<String, String> blockDeclarations) {
        List<Statement> statements = new ArrayList<>();
        HashMap<String, String> bDeclarations = new HashMap();
        bDeclarations.putAll(blockDeclarations);

        if (block != null) {
            statements = block.statements();
        }

        for (Statement s : statements) {

            s.accept(new ASTVisitor() {

                public boolean visit(SingleVariableDeclaration node) {
                    String name = node.getName().toString();
                    String type = node.getType().toString();

                    bDeclarations.put(name, type);

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
                            } else {
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

                public boolean visit(VariableDeclarationStatement node) {
                    String name = node.fragments().get(0).toString().split("=")[0].trim();
                    String type = node.getType().toString();

                    bDeclarations.put(name, type);

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
                            } else {
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

                public boolean visit(SimpleType node) {

                    String type = node.getName().toString();
                    String name = node.getName().toString();

                    bDeclarations.put(name, type);

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
                            } else {
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

                public boolean visit(ClassInstanceCreation node) {

                    ClassInfo clInfo = new ClassInfo();
                    clInfo.className = node.getType().toString();

                    List params = node.arguments();
                    for (Object s : params) {
                        boolean resolved = true;
                        String arg = ((Expression) s).toString();

                        if (arg.contains("[") && !arg.contains("\"")) {
                            arg = arg.substring(0, arg.indexOf("["));
                        }
                        if (arg.startsWith("\"") && arg.endsWith("\"")) {
                            arg = "String";
                        } else if (arg.startsWith("\'") && arg.endsWith("\'")) {
                            arg = "Character";
                        } else if (arg.contains("+") && arg.contains("\"")) {
                            arg = "String";
                        } else if (arg.contains("instanceof")) {
                            arg = arg.substring(arg.indexOf("instanceof") + 11);
                        } else if (bDeclarations.containsKey(arg)) {
                            arg = bDeclarations.get(arg);
                        } else if (arg.equals("true") || arg.equals("false")) {
                            arg = "Boolean";
                        } else if (arg.contains("==") || arg.contains(">") || arg.contains("<") || arg.contains("!=") || arg.contains(">=") || arg.contains("<=")) {
                            arg = "Boolean";
                        } else if (arg.contains("TimeUnit.SECONDS")) {
                            arg = "Numeric";
                        } else if (arg.contains(".class") && !arg.contains(".className")) {
                            arg = arg.replaceAll(".class", "");
                        } else if (arg.startsWith("new ")) {
                            arg = arg.replaceAll("new ", "");
                            if (arg.contains("(")) {
                                arg = arg.substring(0, arg.indexOf("("));
                            } else if (arg.contains("[")) {
                                arg = arg.substring(0, arg.indexOf("["));
                            }
                        } else if (NumberUtils.isNumber(arg)) {
                            arg = "Numeric";
                        } else if (arg.contains("-") || arg.contains("+") || arg.contains("*")) {
                            arg = "Numeric";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("String")) {
                            arg = "String";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".is")) {
                            arg = "Boolean";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".contains")) {
                            arg = "Boolean";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Int")) {
                            arg = "Integer";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Double")) {
                            arg = "Double";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Boolean")) {
                            arg = "Boolean";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".equals")) {
                            arg = "Boolean";
                        } else {
                            resolved = false;
                            if (!arg.contains(".") && !arg.contains("(") && !arg.contains(")")) {
                                typesNotResolved.add(arg);
                            } else {
                                methodsNotResolved.add(arg);
                            }
                        }

                        clInfo.params.add(arg);
                        clInfo.isResolvedParam.add(resolved);
                    }

                    classInstanceCreations.add(clInfo);

                    return true;
                }

                public boolean visit(ConstructorInvocation node) {

                    List params = node.arguments();
                    for (Object s : params) {
                        boolean resolved = true;
                        String arg = ((Expression) s).toString();
                        if (arg.contains("[") && !arg.contains("\"")) {
                            arg = arg.substring(0, arg.indexOf("["));
                        }
                        if (arg.startsWith("\"") && arg.endsWith("\"")) {
                            arg = "String";
                        } else if (arg.startsWith("\'") && arg.endsWith("\'")) {
                            arg = "Character";
                        } else if (arg.contains("+") && arg.contains("\"")) {
                            arg = "String";
                        } else if (arg.contains("instanceof")) {
                            arg = arg.substring(arg.indexOf("instanceof") + 11);
                        } else if (bDeclarations.containsKey(arg)) {
                            arg = bDeclarations.get(arg);
                        } else if (arg.equals("true") || arg.equals("false")) {
                            arg = "Boolean";
                        } else if (arg.contains("==") || arg.contains(">") || arg.contains("<") || arg.contains("!=") || arg.contains(">=") || arg.contains("<=")) {
                            arg = "Boolean";
                        } else if (arg.contains("TimeUnit.SECONDS")) {
                            arg = "Numeric";
                        } else if (arg.contains(".class") && !arg.contains(".className")) {
                            arg = arg.replaceAll(".class", "");
                        } else if (arg.startsWith("new ")) {
                            arg = arg.replaceAll("new ", "");
                            if (arg.contains("(")) {
                                arg = arg.substring(0, arg.indexOf("("));
                            } else if (arg.contains("[")) {
                                arg = arg.substring(0, arg.indexOf("["));
                            }
                        } else if (NumberUtils.isNumber(arg)) {
                            arg = "Numeric";
                        } else if (arg.contains("-") || arg.contains("+") || arg.contains("*")) {
                            arg = "Numeric";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("String")) {
                            arg = "String";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".is")) {
                            arg = "Boolean";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".contains")) {
                            arg = "Boolean";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Int")) {
                            arg = "Integer";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Double")) {
                            arg = "Double";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Boolean")) {
                            arg = "Boolean";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".equals")) {
                            arg = "Boolean";
                        } else {
                            resolved = false;
                            if (!arg.contains(".") && !arg.contains("(") && !arg.contains(")")) {
                                typesNotResolved.add(arg);
                            } else {
                                methodsNotResolved.add(arg);
                            }
                        }

                    }

                    return true;
                }

                public boolean visit(MethodInvocation node) {

                    MethodInfo mInfo = new MethodInfo();
                    mInfo.expression = node.getExpression() != null ? node.getExpression().toString() : "";
                    mInfo.initialexpression = mInfo.expression;
                    mInfo.methodName = node.getName().toString();

                    boolean methodUnResolved = false;

                    if (node.getExpression() != null) {
                        mInfo.expression = node.getExpression().toString().replaceAll("this.", "");
                        if (mInfo.expression.startsWith("\"") && mInfo.expression.endsWith("\"")) {
                            mInfo.expression = "String";
                        } else if (mInfo.expression.equals("this")) {
                            mInfo.expression = "";
                        } else if (mInfo.expression.startsWith("new ")) {
                            mInfo.expression = mInfo.expression.replaceAll("new ", "");
                            if (mInfo.expression.contains("(")) {
                                mInfo.expression = mInfo.expression.substring(0, mInfo.expression.indexOf("("));
                            }
                        }
                        if (mInfo.expression.contains("[")) {
                            mInfo.expression = mInfo.expression.substring(0, mInfo.expression.indexOf("["));
                        }
                        if (!bDeclarations.containsKey(mInfo.expression)) {
                            methodsNotResolved.add(node.getExpression().toString() + "." + node.getName() + "(" + node.arguments() + ")");
                            methodUnResolved = true;
                        } else if (bDeclarations.get(mInfo.expression) != null) {
                            if (!bDeclarations.get(mInfo.expression).contains("<")) {
                                mInfo.expression = bDeclarations.get(mInfo.expression);
                            } else {
                                mInfo.expression = bDeclarations.get(mInfo.expression).substring(0, bDeclarations.get(mInfo.expression).indexOf("<"));
                            }
                        }

                        if (mInfo.expression.contains(".") && Character.isLowerCase(mInfo.expression.substring(0, mInfo.expression.indexOf(".")).charAt(0)) && bDeclarations.containsKey(mInfo.expression.substring(0, mInfo.expression.indexOf(".")))) {
                            mInfo.expression = bDeclarations.get(mInfo.expression.substring(0, mInfo.expression.indexOf("."))) + mInfo.expression.substring(mInfo.expression.indexOf("."));
                        }
                    }

                    List params = node.arguments();
                //    System.out.println("params : " + params);
                    for (Object s : params) {
                        boolean resolved = true;
                        String arg = ((Expression) s).toString();
                        mInfo.initialparams.add(arg);
                        if (arg.contains("[") && arg.indexOf("]")==arg.length()-1 && !arg.contains("\"")) {
                            arg = arg.substring(0, arg.indexOf("["));
                        }
                        if(arg.startsWith("this."))
                                arg = arg.replaceFirst("this.", "");
                        if(arg.startsWith("class "))
                                arg = arg.replaceFirst("class ", "");
                        if (arg.startsWith("\"") && arg.endsWith("\"")) {
                            arg = "String";
                        } else if(arg.startsWith("(")) {
                            arg = arg.substring(1, arg.indexOf(")"));
                        } else if (arg.startsWith("\'") && arg.endsWith("\'")) {
                            arg = "Character";
                        } else if (arg.contains("+") && arg.contains("\"")) {
                            arg = "String";
                        } else if (arg.contains("instanceof")) {
                            arg = arg.substring(arg.indexOf("instanceof") + 11);
                        } else if (bDeclarations.containsKey(arg)) {
                            arg = bDeclarations.get(arg);
                        } else if (arg.equals("true") || arg.equals("false")) {
                            arg = "Boolean";
                        } else if (arg.contains("==") || arg.contains(">") || arg.contains("<") || arg.contains("!=") || arg.contains(">=") || arg.contains("<=") || arg.contains("||")) {
                            arg = "Boolean";
                        } else if (arg.contains("TimeUnit.SECONDS")) {
                            arg = "Numeric";
                        } else if (arg.contains(".class") && !arg.contains(".className")) {
                            arg = arg.replaceAll(".class", "");
                        } else if (arg.startsWith("new ")) {
                            arg = arg.replaceAll("new ", "");
                            if (arg.contains("(")) {
                                arg = arg.substring(0, arg.indexOf("("));
                            } else if (arg.contains("[")) {
                                arg = arg.substring(0, arg.indexOf("[]")+2);
                            }
                        } else if (NumberUtils.isNumber(arg)) {
                            arg = "Numeric";
                        } else if (arg.contains("-") || arg.contains("+") || arg.contains("*")) {
                            arg = "Numeric";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("String")) {
                            arg = "String";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".is")) {
                            arg = "Boolean";
                        } else if (arg.contains(".contains") ) {
                            arg = arg.substring(0,arg.lastIndexOf("("));
                            if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".contains(")) 
                                arg = "Boolean";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Int")) {
                            arg = "Integer";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Double")) {
                            arg = "Double";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).contains("Boolean")) {
                            arg = "Boolean";
                        } else if (arg.contains(".") && arg.substring(arg.lastIndexOf(".")).startsWith(".equals")) {
                            arg = "Boolean";
                        } else {
                            resolved = false;
                            if (!arg.contains(".") && !arg.contains("(") && !arg.contains(")")) {
                                typesNotResolved.add(arg);
                            } else if (!methodUnResolved) {
                                methodsNotResolved.add(arg);
                                methodUnResolved = true;
                            }
                        }

                        mInfo.params.add(arg);
                        mInfo.isResolvedParam.add(resolved);

                    }

                //    System.out.println("Invocation : " + node.getExpression() + " " + mInfo.expression +  " " + mInfo.methodName + " " + mInfo.params);
                    methodInvocations.add(mInfo);

                    return true;
                }

                public boolean visit(Block node) {
                    if (node != null) {

                        blockIterate(node, cu, bDeclarations);

                    }
                    return false;
                }
            });

        }
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

    public static ArrayList<String> readAcceptedTypesFromFile(String filePath) throws IOException {
        ArrayList<String> acceptedTypes = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            acceptedTypes.add(line);
        }

        reader.close();

        return acceptedTypes;
    }

    public static ArrayList<String> loadFieldsFromFile(String filePath) throws IOException {
        ArrayList<String> addedFields = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            addedFields.add(line);
        }

        reader.close();

        return addedFields;
    }
}

class ClassInfo {

    public String className = "";
    public ArrayList<String> params = new ArrayList<>();
    public ArrayList<Boolean> isResolvedParam = new ArrayList<>();
}

class MethodInfo {

    public String methodName = "";
    public String expression = "";
    public String initialexpression = "";
    public ArrayList<String> params = new ArrayList<>();
    public ArrayList<String> initialparams = new ArrayList<>();
    public ArrayList<Boolean> isResolvedParam = new ArrayList<>();
}
