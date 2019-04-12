package org.jboss.dependencytreeparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.lang3.StringUtils;
import org.jboss.dependencytreeparser.SourceParser.MethodInfo2;

/**
 *
 * @author panos
 */
public class DependencyTree {

    Object lock = new Object();

    public static void main(String[] args) throws Exception {
        try {
            DependencyTreeMethods.artifacts = DependencyTreeMethods.getArtifacts();
            HashMap<String, String> packages = DependencyTreeMethods.listPackages();

            HashMap<String, ArrayList<String>> TypesNR = new HashMap<>();
            HashMap<String, ArrayList<String>> MethodsNR = new HashMap<>();

            HashMap<String, HashMap<String, MethodInfo2>> sourceClassesMethods = new HashMap<>();
            HashMap<String, HashSet<String>> sourceClassesImports = new HashMap<>();
            HashMap<String, HashMap<String, String>> sourceClassesFields = new HashMap<>();
            HashSet<String> paths = DependencyTreeMethods.getSourceClasses();
            HashMap<String, String> packageClasses = new HashMap<>();

            for (String t : paths) {
                SourceParser.parse(t);
                String clss = t.replaceAll(".java", "");
                if (clss.lastIndexOf("/") > 0) {
                    clss = clss.substring(clss.lastIndexOf("/") + 1);
                }
                sourceClassesFields.put(SourceParser.packageName + "." + clss, new HashMap<String, String>(SourceParser.fields));
                HashMap<String, MethodInfo2> hm = new HashMap<>();
                hm.putAll(SourceParser.methods);
                sourceClassesMethods.put(SourceParser.packageName + "." + clss, hm);
                sourceClassesImports.put(SourceParser.packageName + "." + clss, new HashSet<String>(SourceParser.imports));

                packageClasses.put(SourceParser.packageName + "." + clss, SourceParser.packageName + "." + clss);

            }

            HashMap<String, ArrayList<Class[]>> classes = DependencyTreeMethods.listClasses();

            HashMap<String, HashMap<String, String[]>> methods = DependencyTreeMethods.listMethods();
            HashMap<String, HashMap<String, String[]>> methods22 = new HashMap<>();

            for (String s : methods.keySet()) {
                if (methods.get(s) != null) {
                    for (String m : methods.get(s).keySet()) {
                        if (methods22.get(s) != null) {
                            if (!methods22.get(s).keySet().contains(m)) {
                                methods22.get(s).put(m, methods.get(s).get(m));
                            }
                        } else {
                            methods22.put(s, methods.get(s));
                        }
                    }

                    if (methods.get(s).keySet().contains(s + "_Extensions")) {
                        addExtensions(methods, methods22, s, s);
                    }
                }
            }
            methods = methods22;

            HashMap<String, HashMap<String, String>> fields = DependencyTreeMethods.listFields();

            HashMap<String, ArrayList<String>> usedLibraries = JavaClassParser.testLibraryUsage();
            HashMap<String, HashMap<String, String[]>> localMethods = JavaClassParser.getInternalClassMethods();
            HashMap<String, HashMap<String, String>> localFields = JavaClassParser.getInternalClassFields();
            HashMap<String, HashSet<String>> localClasses = JavaClassParser.getInternalPackagesAndClasses();
            HashMap<String, ArrayList<String>> internalClasses = JavaClassParser.getInternalClasses();
            HashMap<String, ParsedTests> testData = JavaClassParser.getTestData();

            ArrayList<String> acceptedExpressionTranslations = TestsuiteParser.readAcceptedTypesFromFile(System.getProperty("AcceptedTypesFilePath") + "/" + "expressionTranslations.txt");
            HashMap<String, String> expMethodMap = new HashMap<>();

            for (String str : acceptedExpressionTranslations) {
                expMethodMap.put(str.substring(0, str.indexOf(";")), str.substring(str.indexOf(";") + 1));
            }

            ArrayList<String> acceptedMethodsSet = TestsuiteParser.readAcceptedTypesFromFile(System.getProperty("AcceptedTypesFilePath") + "/" + "methodInclusion.txt");
            HashMap<String, ArrayList<String>> acceptedMethodMap = new HashMap<>();

            for (String str : acceptedMethodsSet) {
                if (acceptedMethodMap.get(str.substring(0, str.indexOf(";"))) != null) {
                    acceptedMethodMap.get(str.substring(0, str.indexOf(";"))).add(str.substring(str.indexOf(";") + 1));
                } else {
                    ArrayList<String> arr = new ArrayList();
                    arr.add(str.substring(str.indexOf(";") + 1));
                    acceptedMethodMap.put(str.substring(0, str.indexOf(";")), arr);
                }
            }

            HashMap<String, String> importFields = new HashMap<>();
            HashMap<String, HashMap<String, String[]>> methodsTest2 = new HashMap<>();

            for (String p : sourceClassesMethods.keySet()) {
                HashMap<String, String[]> mp = new HashMap<>();

                HashMap<String, MethodInfo2> it = sourceClassesMethods.get(p);

                for (String keyName : it.keySet()) {
                    MethodInfo2 mdinfo2 = it.get(keyName);
                    String[] params = new String[mdinfo2.paramTypes.size()];
                    int i = 0;
                    for (String par : mdinfo2.paramTypes) {
                        params[i] = par;
                        i++;
                    }

                    mp.put(mdinfo2.name, params);
                    mp.put(mdinfo2.name + "_Return_Type", new String[]{mdinfo2.returnType});
                }

                importFields.putAll(sourceClassesFields.get(p));

                if (mp.size() != 0) {
                    //    System.out.println("packageClasses.get(p) " + packageClasses.get(p));
                    methodsTest2.put(packageClasses.get(p), mp);
                }
                //    }

            }
            for (String key : testData.keySet()) {
                ParsedTests ps = testData.get(key);

                String path = key.toString().substring(0, key.toString().lastIndexOf("."));
                if (sourceClassesFields.get(path) != null) {
                    ps.fields.putAll(sourceClassesFields.get(path));
                }
                if (sourceClassesImports.get(path) != null) {
                    ps.imports.addAll(sourceClassesImports.get(path));
                }

                ArrayList<String> arr = new ArrayList<>();
                for (String str : ps.imports) {
                    if (StringUtils.isAllUpperCase(str.substring(str.lastIndexOf(".") + 1))) {
                        arr.add(str);
                    }
                }

                for (String str : arr) {
                    ps.imports.add(str.substring(0, str.lastIndexOf(".")));
                }

                HashMap<String, HashMap<String, String[]>> methodsTest = new HashMap<>();
                methodsTest.putAll(methodsTest2);
                methodsTest.putAll(localMethods);

                HashMap<String, String> availableImportFields = new HashMap<>();
                availableImportFields.putAll(importFields);

                HashSet<String> availableFields = new HashSet<>();
                HashMap<String, String> availableExtensionFields = new HashMap<>();

                for (String s : localClasses.keySet()) {
                    if (s.contains(path)) {
                        availableFields.addAll(localClasses.get(s));
                    }
                }

                if (ps.extension != null) {
                    if (testData.get(ps.extension) != null) {
                        String ex = ps.extension;
                        do {

                            if (testData.get(ex) != null) {
                                availableFields.addAll(testData.get(ex).fields.keySet());
                                availableExtensionFields.putAll(testData.get(ex).fields);
                                ex = testData.get(ex).extension;
                            } else {
                                ex = null;
                            }
                        } while (ex != null);
                    } else {
                        for (String cl : classes.keySet()) {
                            if (cl.equals(ps.extension)) {
                                if (fields.get(cl) != null) {
                                    availableFields.addAll(fields.get(cl).keySet());
                                    availableExtensionFields.putAll(fields.get(cl));
                                }
                                break;
                            }
                        }

                    }
                }

                HashSet<String> classLibraries = ps.imports;

                if (classLibraries != null) {
                    for (String lib : classLibraries) {

                        for (String cl : classes.keySet()) {
                            if (cl.contains(lib)) {
                                if (!cl.replaceAll(lib + ".", "").equals(cl)) {
                                    availableFields.add(cl.replaceAll(lib + ".", ""));
                                } else {
                                    availableFields.add(lib.substring(lib.lastIndexOf(".") + 1));
                                }
                            }
                        }
                    }
                }

                ArrayList<String> acceptedTypes = TestsuiteParser.readAcceptedTypesFromFile(System.getProperty("AcceptedTypesFilePath") + "/" + "types.txt");
                for (String type : ps.types) {
                    boolean b = false;
                    if (!acceptedTypes.contains(type) && !availableFields.contains(type) && !classLibraries.contains(type) && !internalClasses.keySet().contains(type) && !internalClasses.get(key.toString()).contains(type) && !classes.containsKey(type)) {

                        for (String im : classLibraries) {
                            if (im.contains(type)) {
                                b = true;
                                break;
                            }
                        }

                        if (!b) {
                            if (type.startsWith("java")) {
                                b = true;
                                break;
                            }
                        }

                        if (!b) {
                            String packageName = key.toString().substring(0, key.toString().lastIndexOf("."));
                            for (String locClass : localClasses.get(packageName)) {
                                if (locClass.contains(type)) {
                                    b = true;
                                    break;
                                }
                            }
                        }

                        if (!b) {
                            for (String s : classes.keySet()) {
                                if (s.contains(type) || s.contains(type.replaceFirst("\\.", "\\$"))) {
                                    b = true;
                                    break;
                                }
                            }
                        }

                        if (!b) {
                            if (TypesNR.get(key) == null) {
                                TypesNR.put(key, new ArrayList<>());
                            }
                            TypesNR.get(key).add(type);
                        }
                    }

                }
                for (String typeNotResolved : ps.typesNotResolved) {
                    boolean b = false;
                    if (!acceptedTypes.contains(typeNotResolved) && !availableFields.contains(typeNotResolved) && !classLibraries.contains(typeNotResolved) && !internalClasses.keySet().contains(typeNotResolved) && !internalClasses.get(key.toString()).contains(typeNotResolved)) {
                        for (String im : classLibraries) {
                            if (im.contains(typeNotResolved)) {
                                b = true;
                                break;
                            }
                        }

                        if (!b) {
                            String packageName = key.toString().substring(0, key.toString().lastIndexOf("."));
                            for (String locClass : localClasses.get(packageName)) {
                                if (locClass.contains(typeNotResolved)) {
                                    b = true;
                                    break;
                                }
                            }
                        }

                        if (!b) {
                            if (TypesNR.get(key) == null) {
                                TypesNR.put(key, new ArrayList<>());
                            }
                            TypesNR.get(key).add(typeNotResolved);
                        }
                    }
                }
                HashMap<String, String> rMethods = new HashMap<>();
                ArrayList<String> acceptedMethods = TestsuiteParser.readAcceptedTypesFromFile(System.getProperty("AcceptedTypesFilePath") + "/" + "methods.txt");
                ArrayList<String> acceptedExpressions = TestsuiteParser.readAcceptedTypesFromFile(System.getProperty("AcceptedTypesFilePath") + "/" + "expressions.txt");
                Collections.reverse(ps.methodInvocations);
                HashMap<String, HashMap<String, String[]>> methods2 = new HashMap<String, HashMap<String, String[]>>();
                for (String kk : methodsTest.keySet()) {
                    if (!kk.contains("security")) {
                        methods2.putIfAbsent(kk, methodsTest.get(kk));
                    }
                }

                HashSet<String> hs = new HashSet<>();
                for (String s : ps.imports) {
                    if (Character.isLowerCase(s.substring(s.lastIndexOf(".") + 1).charAt(0)) || (Character.isUpperCase(s.substring(s.lastIndexOf(".") + 1).charAt(0)) && Character.isUpperCase(s.substring(s.lastIndexOf(".") + 1).charAt(1)))) {
                        hs.add(s.substring(0, s.lastIndexOf(".")));
                    } else {
                        hs.add(s);
                    }
                }

                methods2.putAll(DependencyTreeMethods.listUsedMethods(hs, packages));

                HashMap<String, String> lfields = new HashMap();

                for (String p : localFields.keySet()) {
                    if (p.contains(ps.packageName)) {
                        lfields.putAll(localFields.get(p));
                    }
                }

                for (String s : hs) {

                    for (String p : localFields.keySet()) {
                        if (p.contains(s)) {
                            lfields.putAll(localFields.get(p));
                        }
                    }

                    if (methods.get(s) != null) {
                        for (String m : methods.get(s).keySet()) {
                            if (methods2.get(s) != null) {
                                if (!methods2.get(s).keySet().contains(m)) {
                                    methods2.get(s).put(m, methods.get(s).get(m));
                                }
                            } else {
                                methods2.put(s, methods.get(s));
                            }
                        }
                        if (methods.get(s).keySet().contains(s + "_Extensions")) {
                            addExtensions(methods, methods2, s, s, acceptedMethods, acceptedMethodMap, rMethods);
                        }
                    }
                }

                acceptedMethods.addAll(expMethodMap.keySet());

                for (MethodInfo methodInfo : ps.methodInvocations) {

                    if (methodInfo.expression != null && availableExtensionFields.keySet().contains(methodInfo.expression)) {
                        methodInfo.expression = availableExtensionFields.get(methodInfo.expression);
                    } else if (methodInfo.expression != null && availableImportFields.keySet().contains(methodInfo.expression)) {
                        acceptedMethods.add(methodInfo.methodName);
                    }

                    if (methodInfo.expression != null) {
                        for (String s : rMethods.keySet()) {
                            String lastPart = methodInfo.expression;
                            if (methodInfo.expression.contains(".")) {
                                lastPart = methodInfo.expression.substring(methodInfo.expression.lastIndexOf(".") + 1);
                            }
                            if (lastPart.contains("(")) {
                                lastPart = lastPart.substring(0, lastPart.indexOf("("));
                            }
                            if (lastPart.equals(s)) {
                                if (!rMethods.get(s).contains("$")) {
                                    if (rMethods.get(s).trim().compareTo("") != 0) {
                                        methodInfo.expression = rMethods.get(s);
                                    }
                                }
                            }
                        }
                    }

                    if (methodInfo.expression != null && methodInfo.expression.contains(".")) {
                        String outcome = resolveMethods(methodInfo.expression.substring(0, methodInfo.expression.indexOf(".")), methodInfo.expression.substring(methodInfo.expression.indexOf(".") + 1), methods);
                        if (outcome != null && outcome != "") {
                            methodInfo.expression = outcome;
                        }
                    }

                    boolean added = false;

                    for (String str : ps.imports) {

                        if (str.contains(methodInfo.expression)) {
                            if (str.startsWith("java")) {
                                acceptedMethods.add(methodInfo.methodName);
                                rMethods.put(methodInfo.methodName, methodInfo.expression);
                                added = true;
                            }
                        }
                    }

                    if (methodInfo.expression != null && !methodInfo.expression.equals("") && !added) {
                        if (methodInfo.expression.endsWith("]")) {
                            methodInfo.expression = methodInfo.expression.substring(0, methodInfo.expression.lastIndexOf("["));
                        }
                        if (methodInfo.expression.endsWith(">")) {
                            methodInfo.expression = methodInfo.expression.substring(0, methodInfo.expression.indexOf("<"));
                        }

                        if (methods.keySet().contains(methodInfo.expression)) {
                            methods2.put(methodInfo.expression, methods.get(methodInfo.expression));
                        }
                        for (String s : methods2.keySet()) {

                            String s1 = s;

                            if (s1.endsWith(methodInfo.expression)) {
                                if (s.contains(".")) {
                                    s1 = s1.substring(0, s.lastIndexOf(".") + 1);
                                }

                                String ms1 = s.replaceAll(s1, "");
                                if (ms1.compareTo("") != 0 && !ms1.contains(".")) {
                                    for (String meth : methods2.get(s).keySet()) {

                                        if (meth.equals(methodInfo.methodName)) {
                                            acceptedMethods.add(methodInfo.methodName);
                                            if (methods2.get(s) != null && methods2.get(s).get(meth + "_Return_Type") != null && methods2.get(s).get(meth + "_Return_Type")[0].toString().contains("class ")) {
                                                rMethods.put(methodInfo.methodName, methods2.get(s).get(meth + "_Return_Type")[0].toString().replaceAll("class ", ""));
                                            } else if (methods2.get(s) != null && methods2.get(s).get(meth + "_Return_Type") != null && methods2.get(s).get(meth + "_Return_Type")[0].toString().contains("interface ")) {
                                                rMethods.put(methodInfo.methodName, methods2.get(s).get(meth + "_Return_Type")[0].toString().replaceAll("interface ", ""));
                                            } else if (methods2.get(s) != null && methods2.get(s).get(meth + "_Return_Type") != null) {
                                                rMethods.put(methodInfo.methodName, methods2.get(s).get(meth + "_Return_Type")[0].toString());
                                            }
                                        }
                                    }
                                    if (expMethodMap.containsKey(methodInfo.methodName)) {
                                        rMethods.put(methodInfo.methodName, expMethodMap.get(methodInfo.methodName));
                                    }

                                }
                            }
                        }

                        if (methodInfo.expression.startsWith(".")) {
                            methodInfo.expression = methodInfo.expression.replaceFirst(".", "");
                        }
                        if (!rMethods.keySet().contains(methodInfo.expression)) {
                            if (methodInfo.expression.contains(".withName(")) {
                                methodInfo.expression = methodInfo.expression.substring(0, methodInfo.expression.indexOf(".withName(")) + methodInfo.expression.substring(methodInfo.expression.indexOf(".withName(") + 1).substring(methodInfo.expression.substring(methodInfo.expression.indexOf(".withName(") + 1).indexOf(")") + 1);
                            }

                            ArrayList<String> longExpression = new ArrayList<>();
                            longExpression.addAll(Arrays.asList(methodInfo.expression.split("\\.")));

                            if (longExpression.get(0) != null && !Character.isUpperCase(longExpression.get(0).charAt(0))) {
                                String firstEl = longExpression.get(0);
                                if (firstEl.contains("(")) {
                                    firstEl = firstEl.substring(0, firstEl.indexOf("("));
                                }
                                for (String im : ps.imports) {
                                    if (im.endsWith("." + firstEl)) {
                                        String rep = im.replaceAll("." + firstEl, "");
                                        longExpression.add(0, rep.substring(rep.lastIndexOf(".") + 1));
                                    }
                                }
                            }

                            if (longExpression.size() > 1) {
                                if (longExpression.get(0).contains("<")) {
                                    String exp = longExpression.remove(0);
                                    longExpression.add(0, exp.substring(0, exp.indexOf("<")));
                                }
                                if (longExpression.get(1).contains("<")) {
                                    String exp = longExpression.remove(1);
                                    longExpression.add(1, exp.substring(0, exp.indexOf("<")));
                                }
                                while (longExpression.size() > 0 && Character.isLowerCase(longExpression.get(0).charAt(0))) {
                                    longExpression.remove(0);
                                }
                                int prevSize = Integer.MAX_VALUE;
                                while (longExpression.size() > 1 && longExpression.size() < prevSize) {
                                    prevSize = longExpression.size();
                                    if (longExpression.get(0).compareTo("class") == 0 || longExpression.get(1).compareTo("class") == 0 || longExpression.get(0).compareTo("System") == 0 || longExpression.get(0).compareTo("TimeUnit") == 0) {
                                        acceptedMethods.add(methodInfo.methodName);
                                        break;
                                    }

                                    boolean breakValue = false;

                                    for (String s : methods2.keySet()) {
                                        String s1 = s;

                                        if (longExpression.size() > 1) {
                                            if (longExpression.get(1).contains("(")) {
                                                if (!longExpression.get(1).startsWith("as(") && !longExpression.get(1).startsWith("create(") && !longExpression.get(0).equals("ShrinkWrap")) {
                                                    if (!longExpression.get(1).contains(")")) {
                                                        while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                            longExpression.remove(2);
                                                        }
                                                        if (longExpression.size() > 2) {
                                                            longExpression.remove(2);
                                                        }
                                                    }

                                                    String exp = longExpression.remove(1);
                                                    longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                } else {
                                                    String exp = longExpression.remove(1);
                                                    longExpression.add(1, exp.substring(exp.indexOf("(") + 1));

                                                    if (!exp.contains(")")) {
                                                        while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                            longExpression.remove(2);
                                                        }
                                                        longExpression.remove(2);
                                                    }
                                                    longExpression.remove(0);
                                                }
                                            }
                                        }

                                        if (longExpression.size() > 1 && longExpression.get(0).length() > 0 && Character.isUpperCase(longExpression.get(0).charAt(0)) && Character.isUpperCase(longExpression.get(1).charAt(0)) && !Character.isUpperCase(longExpression.get(1).charAt(1))) {
                                            String exp1 = longExpression.remove(0);
                                            String exp2 = longExpression.remove(0);
                                            longExpression.add(0, exp1 + "$" + exp2);
                                        }

                                        while (longExpression.size() > 0 && longExpression.get(0).length() > 0 && longExpression.get(0).length() > 0 && Character.isLowerCase(longExpression.get(0).charAt(0))) {
                                            longExpression.remove(0);
                                        }

                                        if (longExpression.size() > 1) {

                                            if (s1.endsWith(longExpression.get(0))) {
                                                if (longExpression.get(1).contains("(")) {

                                                    if (!longExpression.get(1).startsWith("as(") && !longExpression.get(1).startsWith("create(") && !longExpression.get(0).equals("ShrinkWrap")) {
                                                        if (!longExpression.get(1).contains(")")) {
                                                            while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                                longExpression.remove(2);
                                                            }
                                                            if (longExpression.size() > 2) {
                                                                longExpression.remove(2);
                                                            }
                                                        }
                                                        String exp = longExpression.remove(1);
                                                        longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                    } else {
                                                        String exp = longExpression.remove(1);
                                                        longExpression.add(1, exp.substring(exp.indexOf("(") + 1));

                                                        if (!exp.contains(")")) {
                                                            while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                                longExpression.remove(2);
                                                            }
                                                            longExpression.remove(2);
                                                        }
                                                        longExpression.remove(0);
                                                    }
                                                }
                                                if (longExpression.get(0).contains("<")) {
                                                    String exp = longExpression.remove(0);
                                                    longExpression.add(0, exp.substring(0, exp.indexOf("<")));
                                                }
                                                if (longExpression.get(1).contains("<")) {
                                                    String exp = longExpression.remove(1);
                                                    longExpression.add(1, exp.substring(0, exp.indexOf("<")));
                                                }
                                                String rt = longExpression.get(1) + "_Return_Type";
                                                if (methods2.get(s).get(rt) != null) {
                                                    longExpression.remove(0);
                                                    String exp = longExpression.remove(0);
                                                    if (methods2.get(s).get(exp + "_Return_Type")[0] != null) {
                                                        if (methods2.get(s) != null && methods2.get(s).get(exp + "_Return_Type") != null && methods2.get(s).get(exp + "_Return_Type")[0].toString().contains("class ")) {
                                                            longExpression.add(0, methods2.get(s).get(exp + "_Return_Type")[0].toString().replaceAll("class ", ""));
                                                        } else if (methods2.get(s) != null && methods2.get(s).get(exp + "_Return_Type") != null && methods2.get(s).get(exp + "_Return_Type")[0].toString().contains("interface ")) {
                                                            longExpression.add(0, methods2.get(s).get(exp + "_Return_Type")[0].toString().replaceAll("interface ", ""));
                                                        } else if (methods2.get(s) != null && methods2.get(s).get(exp + "_Return_Type") != null) {
                                                            longExpression.add(0, methods2.get(s).get(exp + "_Return_Type")[0].toString());
                                                        }

                                                        if (longExpression.get(0).contains(".")) {
                                                            String expr = longExpression.remove(0);
                                                            longExpression.add(0, expr.substring(expr.lastIndexOf(".") + 1));
                                                        }

                                                        if (longExpression.size() > 1) {
                                                            if (longExpression.get(1).contains("(")) {
                                                                exp = longExpression.remove(1);
                                                                longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                                if (!exp.contains(")")) {
                                                                    while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                                        longExpression.remove(2);
                                                                    }
                                                                }
                                                                if (longExpression.size() > 2 && longExpression.get(2).contains(")") && !longExpression.get(2).contains("(")) {
                                                                    longExpression.remove(2);
                                                                }

                                                            }
                                                            if (expMethodMap.containsKey(longExpression.get(1))) {
                                                                longExpression.remove(0);
                                                                exp = longExpression.remove(0);
                                                                longExpression.add(0, expMethodMap.get(exp));
                                                            }
                                                        }
                                                    }

                                                } else if (longExpression.size() > 1) {

                                                    if (longExpression.get(1).contains("(")) {
                                                        String exp = longExpression.remove(1);
                                                        longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                        if (!exp.contains(")")) {
                                                            while (longExpression.size() > 2 && !longExpression.get(2).contains(")")) {
                                                                longExpression.remove(2);
                                                            }
                                                        }
                                                        if (longExpression.size() > 2 && longExpression.get(2).contains(")") && !longExpression.get(2).contains("(")) {
                                                            longExpression.remove(2);
                                                        }

                                                    }

                                                    if (longExpression.size() > 1 && lfields.keySet().contains(longExpression.get(1))) {
                                                        longExpression.remove(0);
                                                        String exp = longExpression.remove(0);
                                                        longExpression.add(0, lfields.get(exp));
                                                        if (longExpression.size() > 0 && longExpression.get(0).contains("<")) {
                                                            exp = longExpression.remove(0);
                                                            longExpression.add(0, exp.substring(0, exp.indexOf("<")));
                                                        }
                                                        if (longExpression.size() > 1 && longExpression.get(1).contains("(")) {
                                                            exp = longExpression.remove(1);
                                                            longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                        }
                                                    }

                                                    if (longExpression.size() > 1 && expMethodMap.containsKey(longExpression.get(1))) {
                                                        longExpression.remove(0);
                                                        String exp = longExpression.remove(0);
                                                        longExpression.add(0, expMethodMap.get(exp));
                                                    }

                                                }
                                            }
                                        } else {
                                            breakValue = true;
                                        }
                                    }

                                    if (breakValue) {
                                        break;
                                    }
                                }

                                prevSize = Integer.MAX_VALUE;
                                if (longExpression.size() > 1) {
                                    while (longExpression.size() > 1 && longExpression.size() < prevSize) {
                                        prevSize = longExpression.size();
                                        if (longExpression.get(0).compareTo("class") == 0 || longExpression.get(1).compareTo("class") == 0 || longExpression.get(0).compareTo("System") == 0 || longExpression.get(0).compareTo("TimeUnit") == 0) {
                                            acceptedMethods.add(methodInfo.methodName);
                                            break;
                                        }

                                        boolean breakValue = false;

                                        for (String s : methods.keySet()) {
                                            String s1 = s;

                                            if (longExpression.size() > 1) {
                                                if (longExpression.get(1).contains("(")) {
                                                    if (!longExpression.get(1).startsWith("as(") && !longExpression.get(1).startsWith("create(") && !longExpression.get(0).equals("ShrinkWrap")) {
                                                        if (!longExpression.get(1).contains(")")) {
                                                            while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                                longExpression.remove(2);
                                                            }
                                                            if (longExpression.size() > 2) {
                                                                longExpression.remove(2);
                                                            }
                                                        }

                                                        String exp = longExpression.remove(1);
                                                        longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                    } else {
                                                        String exp = longExpression.remove(1);
                                                        longExpression.add(1, exp.substring(exp.indexOf("(") + 1));

                                                        if (!exp.contains(")")) {
                                                            while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                                longExpression.remove(2);
                                                            }
                                                            longExpression.remove(2);
                                                        }
                                                        longExpression.remove(0);
                                                    }
                                                }
                                            }

                                            if (longExpression.size() > 1 && longExpression.get(0).length() > 0 && Character.isUpperCase(longExpression.get(0).charAt(0)) && Character.isUpperCase(longExpression.get(1).charAt(0)) && !Character.isUpperCase(longExpression.get(1).charAt(1))) {
                                                String exp1 = longExpression.remove(0);
                                                String exp2 = longExpression.remove(0);
                                                longExpression.add(0, exp1 + "$" + exp2);
                                            }

                                            while (longExpression.size() > 0 && longExpression.get(0).length() > 0 && Character.isLowerCase(longExpression.get(0).charAt(0))) {
                                                longExpression.remove(0);
                                            }

                                            if (longExpression.size() > 1) {

                                                if (s1.endsWith(longExpression.get(0))) {
                                                    if (longExpression.get(1).contains("(")) {

                                                        if (!longExpression.get(1).startsWith("as(") && !longExpression.get(1).startsWith("create(") && !longExpression.get(0).equals("ShrinkWrap")) {
                                                            if (!longExpression.get(1).contains(")")) {
                                                                while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                                    longExpression.remove(2);
                                                                }
                                                                if (longExpression.size() > 2) {
                                                                    longExpression.remove(2);
                                                                }
                                                            }
                                                            String exp = longExpression.remove(1);
                                                            longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                        } else {
                                                            String exp = longExpression.remove(1);
                                                            longExpression.add(1, exp.substring(exp.indexOf("(") + 1));

                                                            if (!exp.contains(")")) {
                                                                while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                                    longExpression.remove(2);
                                                                }
                                                                longExpression.remove(2);
                                                            }
                                                            longExpression.remove(0);
                                                        }
                                                    }
                                                    if (longExpression.get(0).contains("<")) {
                                                        String exp = longExpression.remove(0);
                                                        longExpression.add(0, exp.substring(0, exp.indexOf("<")));
                                                    }
                                                    if (longExpression.get(1).contains("<")) {
                                                        String exp = longExpression.remove(1);
                                                        longExpression.add(1, exp.substring(0, exp.indexOf("<")));
                                                    }
                                                    String rt = longExpression.get(1) + "_Return_Type";
                                                    if (methods.get(s).get(rt) != null) {
                                                        longExpression.remove(0);
                                                        String exp = longExpression.remove(0);
                                                        if (methods.get(s).get(exp + "_Return_Type")[0] != null) {
                                                            if (methods.get(s) != null && methods.get(s).get(exp + "_Return_Type") != null && methods.get(s).get(exp + "_Return_Type")[0].toString().contains("class ")) {
                                                                longExpression.add(0, methods.get(s).get(exp + "_Return_Type")[0].toString().replaceAll("class ", ""));
                                                            } else if (methods.get(s) != null && methods.get(s).get(exp + "_Return_Type") != null && methods.get(s).get(exp + "_Return_Type")[0].toString().contains("interface ")) {
                                                                longExpression.add(0, methods.get(s).get(exp + "_Return_Type")[0].toString().replaceAll("interface ", ""));
                                                            } else if (methods.get(s) != null && methods.get(s).get(exp + "_Return_Type") != null) {
                                                                longExpression.add(0, methods.get(s).get(exp + "_Return_Type")[0].toString());
                                                            }

                                                            if (longExpression.get(0).contains(".")) {
                                                                String expr = longExpression.remove(0);
                                                                longExpression.add(0, expr.substring(expr.lastIndexOf(".") + 1));
                                                            }

                                                            if (longExpression.size() > 1) {
                                                                if (longExpression.get(1).contains("(")) {
                                                                    exp = longExpression.remove(1);
                                                                    longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                                    if (!exp.contains(")")) {
                                                                        while (longExpression.size() > 2 && (!longExpression.get(2).contains(")") || (longExpression.get(2).contains("(") && longExpression.get(2).contains(")")))) {
                                                                            longExpression.remove(2);
                                                                        }
                                                                    }
                                                                    if (longExpression.size() > 2 && longExpression.get(2).contains(")") && !longExpression.get(2).contains("(")) {
                                                                        longExpression.remove(2);
                                                                    }

                                                                }
                                                                if (expMethodMap.containsKey(longExpression.get(1))) {
                                                                    longExpression.remove(0);
                                                                    exp = longExpression.remove(0);
                                                                    longExpression.add(0, expMethodMap.get(exp));
                                                                }
                                                            }
                                                        }

                                                    } else if (longExpression.size() > 1) {
                                                        if (longExpression.get(1).contains("(")) {
                                                            String exp = longExpression.remove(1);
                                                            longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                            if (!exp.contains(")")) {
                                                                while (longExpression.size() > 2 && !longExpression.get(2).contains(")")) {
                                                                    longExpression.remove(2);
                                                                }
                                                            }
                                                            if (longExpression.size() > 2 && longExpression.get(2).contains(")") && !longExpression.get(2).contains("(")) {
                                                                longExpression.remove(2);
                                                            }

                                                        }

                                                        if (longExpression.size() > 1 && lfields.keySet().contains(longExpression.get(1))) {
                                                            longExpression.remove(0);
                                                            String exp = longExpression.remove(0);
                                                            longExpression.add(0, lfields.get(exp));
                                                            if (longExpression.size() > 0 && longExpression.get(0).contains("<")) {
                                                                exp = longExpression.remove(0);
                                                                longExpression.add(0, exp.substring(0, exp.indexOf("<")));
                                                            }
                                                            if (longExpression.size() > 1 && longExpression.get(1).contains("(")) {
                                                                exp = longExpression.remove(1);
                                                                longExpression.add(1, exp.substring(0, exp.indexOf("(")));
                                                            }
                                                        }

                                                        if (longExpression.size() > 1 && expMethodMap.containsKey(longExpression.get(1))) {
                                                            longExpression.remove(0);
                                                            String exp = longExpression.remove(0);
                                                            longExpression.add(0, expMethodMap.get(exp));
                                                        }

                                                    }
                                                }
                                            } else {
                                                breakValue = true;
                                            }
                                        }

                                        if (breakValue) {
                                            break;
                                        }
                                    }
                                }

                            }

                            if (longExpression.size() == 1) {

                                if (longExpression.get(0).contains("<")) {
                                    String exp = longExpression.remove(0);
                                    longExpression.add(0, exp.substring(0, exp.indexOf("<")));
                                }

                                if (longExpression.get(0).startsWith("(")) {
                                    String exp = longExpression.remove(0);
                                    exp = exp.substring(0, exp.indexOf(")"));
                                    exp = exp.substring(exp.lastIndexOf("(") + 1);
                                    longExpression.add(0, exp);
                                }
                                boolean exists = false;
                                for (String c : methods2.keySet()) {
                                    if (c.contains(longExpression.get(0))) {
                                        if ((methods2.get(c).keySet().contains(methodInfo.methodName)) || (acceptedMethodMap.get(longExpression.get(0)) != null && acceptedMethodMap.get(longExpression.get(0)).contains(methodInfo.methodName))) {
                                            acceptedMethods.add(methodInfo.methodName);
                                            if (methods2.get(c).get(longExpression.get(0) + "_Return_Type") != null && methods2.get(c).get(longExpression.get(0) + "_Return_Type")[0] != null) {
                                                if (methods2.get(c) != null && methods2.get(c).get(longExpression.get(0) + "_Return_Type") != null && methods2.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString().contains("class ")) {
                                                    rMethods.put(methodInfo.methodName, methods2.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString().replaceAll("class ", ""));
                                                } else if (methods2.get(c) != null && methods2.get(c).get(longExpression.get(0) + "_Return_Type") != null && methods2.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString().contains("interface ")) {
                                                    rMethods.put(methodInfo.methodName, methods2.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString().replaceAll("interface ", ""));
                                                } else if (methods2.get(c) != null && methods2.get(c).get(longExpression.get(0) + "_Return_Type") != null) {
                                                    rMethods.put(methodInfo.methodName, methods2.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString());
                                                }
                                            }
                                            exists = true;

                                            break;
                                        }
                                    }
                                }

                                if (!exists && lfields.keySet().contains(methodInfo.methodName)) {
                                    acceptedMethods.add(methodInfo.methodName);
                                    exists = true;
                                }

                                if (!exists) {
                                    if ((acceptedMethodMap.get(longExpression.get(0)) != null && acceptedMethodMap.get(longExpression.get(0)).contains(methodInfo.methodName)) || longExpression.get(0).equals("T") || longExpression.get(0).equals("A")) {
                                        acceptedMethods.add(methodInfo.methodName);
                                        exists = true;
                                    }

                                }

                                if (!exists) {

                                    for (String c : methods.keySet()) {

                                        if (c.contains(longExpression.get(0))) {

                                            if ((methods.get(c).keySet().contains(methodInfo.methodName)) || (acceptedMethodMap.get(longExpression.get(0)) != null && acceptedMethodMap.get(longExpression.get(0)).contains(methodInfo.methodName))) {
                                                acceptedMethods.add(methodInfo.methodName);
                                                if (methods.get(c).get(longExpression.get(0) + "_Return_Type") != null && methods.get(c).get(longExpression.get(0) + "_Return_Type")[0] != null) {
                                                    if (methods.get(c) != null && methods.get(c).get(longExpression.get(0) + "_Return_Type") != null && methods.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString().contains("class ")) {
                                                        rMethods.put(methodInfo.methodName, methods.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString().replaceAll("class ", ""));
                                                    } else if (methods.get(c) != null && methods.get(c).get(longExpression.get(0) + "_Return_Type") != null && methods.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString().contains("interface ")) {
                                                        rMethods.put(methodInfo.methodName, methods.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString().replaceAll("interface ", ""));
                                                    } else if (methods.get(c) != null && methods.get(c).get(longExpression.get(0) + "_Return_Type") != null) {
                                                        rMethods.put(methodInfo.methodName, methods.get(c).get(longExpression.get(0) + "_Return_Type")[0].toString());
                                                    }

                                                }
                                                exists = true;

                                                break;
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                    String c = key.toString();

                    for (String s : localMethods.keySet()) {
                        if (s.contains(c) || s.contains(ps.packageName)) {
                            for (String meth : localMethods.get(s).keySet()) {
                                if (meth.equals(methodInfo.methodName)) {
                                    acceptedMethods.add(methodInfo.methodName);
                                }
                            }
                        }

                    }

                    if (availableFields.contains(c) || availableExtensionFields.containsKey(c)) {
                        acceptedMethods.add(methodInfo.methodName);
                    }

                    String libName = null;
                    if (methodInfo.expression != null) {
                        for (String s : ps.imports) {
                            if (s.endsWith(methodInfo.expression)) {
                                libName = s;
                                break;
                            }
                        }
                    }

                    if (methodInfo.expression != null) {
                        if (methodInfo.expression.endsWith(".getClass")) {
                            acceptedMethods.add(methodInfo.methodName);
                            rMethods.put(methodInfo.methodName, "Object");
                        }

                        if (methodInfo.expression.startsWith("java.") || methodInfo.expression.startsWith("javax.") || (libName != null && libName.startsWith("java.")) || (libName != null && libName.startsWith("javax."))) {
                            acceptedMethods.add(methodInfo.methodName);
                            rMethods.put(methodInfo.methodName, "Object");
                        }

                        if (acceptedExpressions.contains(methodInfo.expression)) {
                            acceptedMethods.add(methodInfo.methodName);
                            rMethods.put(methodInfo.methodName, "Object");
                        }

                        for (String s : acceptedMethodMap.keySet()) {
                            if (methodInfo.expression.equals(s) && (acceptedMethodMap.get(s).contains(methodInfo.methodName))) {
                                acceptedMethods.add(methodInfo.methodName);
                                rMethods.put(methodInfo.methodName, methodInfo.expression);
                            }
                        }
                    }

                    int m = 0;
                    for (String param : methodInfo.params) {
                        if (param.contains(".")) {
                            param = param.substring(param.lastIndexOf(".") + 1);
                        }
                        if ((availableImportFields.containsKey(param) || availableFields.contains(param) || classLibraries.contains(param) || acceptedMethods.contains(param) || param == null || param.equals("this")) && !methodInfo.isResolvedParam.get(m)) {
                            methodInfo.isResolvedParam.set(m, Boolean.TRUE);
                        }
                        m++;
                    }
                    if (!acceptedMethods.contains(methodInfo.methodName) /*|| methodInfo.isResolvedParam.contains("false")*/) {
                        System.out.println("----" + methodInfo.methodName + " " + methodInfo.expression + " " + methodInfo.params.toString() + " " + methodInfo.isResolvedParam.toString());
                        if (MethodsNR.get(key) == null) {
                            MethodsNR.put(key, new ArrayList<>());
                        }
                        MethodsNR.get(key).add(methodInfo.methodName);
                    }
                }

            }

            HashMap<String, HashSet<String>> internalPackagesAndClasses = JavaClassParser.getInternalPackagesAndClasses();

            HashSet<String> excludedLibraries = DependencyTreeMethods.addExcludedLibraries();
            HashSet<String> allPackages = DependencyTreeMethods.listAvailablePackages();

            int found = 0;
            int notFound = 0;
            HashSet<String> foundArray = new HashSet<>();
            HashSet<String> notfoundArray = new HashSet<>();
            for (String s : usedLibraries.keySet()) {
                if (allPackages.contains(s) || internalPackagesAndClasses.keySet().contains(s)) {
                    found++;
                    foundArray.add(s);
                } else {
                    boolean excluded = false;

                    for (String d : excludedLibraries) {
                        if (s.startsWith(d)) {
                            excluded = true;
                            break;
                        }

                    }

                    if (!excluded) {
                        notFound++;
                        notfoundArray.add(s);
                    }
                }
            }

            System.out.println("LIBRARIES found : " + found + " , LIBRARIES notFound : " + notFound);

            System.out.println("LIBRARIES Not Found : ");
            for (String s : notfoundArray) {
                System.out.println("Not Found : " + s);
            }

            System.out.println("TYPES not resolved : ");
            for (String s : TypesNR.keySet()) {
                if (TypesNR.get(s) != null) {
                    for (String t : TypesNR.get(s)) {
                        System.out.println("Type : " + t + " , Class : " + s);
                    }
                }
            }

            System.out.println("METHODS not resolved : ");
            for (String s : MethodsNR.keySet()) {
                if (MethodsNR.get(s) != null) {
                    for (String m : MethodsNR.get(s)) {
                        System.out.println("Method : " + m + " , Class : " + s);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String resolveMethods(String className, String methodName, HashMap<String, HashMap<String, String[]>> methods) {
        String returnName = null;

        String methName = methodName;
        if (methodName.contains(".")) {
            methName = methodName.substring(methodName.indexOf(".") + 1);
        }
        if (methName != null && methName.contains("(")) {
            methName = methName.substring(0, methName.indexOf("("));
        }

        if (methName != null && className != null) {

            for (String pth : methods.keySet()) {
                if (pth.contains(className)) {
                    for (String mmm : methods.get(pth).keySet()) {
                        if (mmm.equals(methName)) {
                            String className2 = null;

                            if (methName.contains("[")) {
                                methodName = methName.substring(0, methodName.indexOf("["));
                            }

                            String classNamePrev = methods.get(pth).get(methName + "_Return_Type")[0];
                            if (methods.get(pth).get(methName + "_Return_Type") != null) {
                                className2 = methods.get(pth).get(methName + "_Return_Type")[0];

                                if (!className2.equals(classNamePrev)) {
                                    if (className2 != null && className2.startsWith(".")) {
                                        className2 = className2.replaceFirst(".", "");
                                    }
                                    returnName = resolveMethods(className2, methName, methods);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            returnName = className;
        }

        return returnName;
    }

    private static void addExtensions(HashMap<String, HashMap<String, String[]>> methods, HashMap<String, HashMap<String, String[]>> methods2, String extensionName, String extNameConst, ArrayList<String> acceptedMethods, HashMap<String, ArrayList<String>> acceptedMethodMap, HashMap<String, String> rMethods) {
        if (methods.get(extensionName) != null && methods.get(extensionName).get(extensionName + "_Extensions") != null) {
            for (String e : methods.get(extensionName).get(extensionName + "_Extensions")) {
                if (e != null && e.compareTo(extensionName) != 0) {
                    if (methods.get(e) != null) {
                        if (methods2.get(extNameConst) == null) {
                            methods2.put(extNameConst, methods.get(e));
                        } else {
                            for (String m : methods.get(e).keySet()) {
                                if (methods2.get(extNameConst).get(m) == null) {
                                    methods2.get(extNameConst).put(m, methods.get(e).get(m));
                                }
                            }
                        }

                        addExtensions(methods, methods2, e, extNameConst, acceptedMethods, acceptedMethodMap, rMethods);
                    } else {
                        for (String s : acceptedMethodMap.keySet()) {
                            if (e.equals(s)) {
                                acceptedMethods.addAll(acceptedMethodMap.get(s));
                                for (String ss : acceptedMethodMap.get(s)) {
                                    rMethods.put(ss, extNameConst);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void addExtensions(HashMap<String, HashMap<String, String[]>> methods, HashMap<String, HashMap<String, String[]>> methods2, String extensionName, String extNameConst) {
        if (methods.get(extensionName) != null && methods.get(extensionName).get(extensionName + "_Extensions") != null) {
            for (String e : methods.get(extensionName).get(extensionName + "_Extensions")) {
                if (e != null && e.compareTo(extensionName) != 0) {
                    if (methods.get(e) != null) {
                        if (methods2.get(extNameConst) == null) {
                            methods2.put(extNameConst, methods.get(e));
                        } else {
                            for (String m : methods.get(e).keySet()) {
                                if (methods2.get(extNameConst).get(m) == null) {
                                    methods2.get(extNameConst).put(m, methods.get(e).get(m));
                                }
                            }
                        }

                        addExtensions(methods, methods2, e, extNameConst);
                    }
                }
            }
        }
    }

}
