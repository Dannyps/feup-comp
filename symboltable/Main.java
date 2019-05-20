package symboltable;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.io.File;

import ast.*;

public class Main {

    ClassDeclaration classDeclaration;
    SimpleNode root;

    private FileWriter f = null;
    private int identLevel = 0;

    private Path outputDir = Paths.get("output");

    public Main(SimpleNode root) {
        this.root = root;
        Node child = root.jjtGetChild(0);
    }

    /**
     * @return the classDeclaration
     */
    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    public void checkErrors() {
        System.out.println("\n\n----------Starting Checking Erros----------\n");
        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            Node child = root.jjtGetChild(i);
            if (child instanceof ASTClassDeclaration && classDeclaration == null) {
                createClass(child);
            }
        }
    }

    public void createSymbolTable() {
        System.out.println("\n\n----------Starting creating symbol table----------\n");
        System.out.println(classDeclaration);
        showPrefix(1);
        System.out.println("METHODS of class " + classDeclaration.getName());

        for (MethodDeclaration method : classDeclaration.getAllMethods().values()) {
            showPrefix(2);
            System.out.println(method.getType() + " | " + method.getName());

            showPrefix(3);
            System.out.println("Parameters of " + method.getName());
            for (VariableDeclaration parameter : method.getAllParameters()) {
                showPrefix(3);
                System.out.println(parameter.getType() + " | " + parameter.getName());
            }
            System.out.println();
            showPrefix(3);
            System.out.println("Variables of " + method.getName());
            for (VariableDeclaration variable : method.getAllVariables().values()) {
                showPrefix(3);
                System.out.println(variable.getType() + " | " + variable.getName());
            }
        }
        this.closeFile();

    }

    private void showPrefix(Integer number) {
        for (Integer i = 0; i < number; i++) {
            System.out.print("\t");
        }
    }

    private void showChilds(Node node, Descriptor descriptor) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);

            if (descriptor instanceof MethodDeclaration) {
                if (!((MethodDeclaration) descriptor).getWritedLocals()) {
                    if (!(child instanceof ASTVarDeclaration)) {
                        ((MethodDeclaration) descriptor).setWritedLocals(true);
                        toFile(".limit locals "
                                + ((MethodDeclaration) descriptor).getAllParametersAndVariables().size());
                        toFile("");
                    }
                }
            }

            if (child instanceof ASTVarDeclaration) {
                createVariable(descriptor, child);
            } else if (child instanceof ASTMainDeclaration) {
                checkMain(child, descriptor);
            } else if (child instanceof ASTMethodDeclaration) {
                createMethod(child);
            } else if (child instanceof ASTMethodParameter) {
                System.out.println("Parameter");
                createParameter(descriptor, child);
            } else if (child instanceof ASTMethodBody) {
                createMethodBody(child, descriptor);
            } else if (child instanceof ASTMethodReturn) {
                checkReturn(child, node);
            } else if (child instanceof ASTEqual) {
                initiateVariable(child, descriptor);
            } else if (child instanceof ASTDot) {
                if (child.jjtGetNumChildren() == 3) {
                    checkMethodWithDot(child, descriptor);
                } else if (child.jjtGetNumChildren() == 2) {
                    checkArrayLength(child, descriptor);
                }
            } /*
               * else if (child instanceof ASTTerm) { SimpleNode array = (SimpleNode) child;
               * SimpleNode equal = (SimpleNode) node.jjtGetChild(i + 1); i++;
               * MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
               * VariableDeclaration variableDeclaration = getVariable(methodDeclaration,
               * array.str); if (variableDeclaration.getIsArray() && (equal instanceof
               * ASTEqual)) { initiateArrayPosition(methodDeclaration, variableDeclaration,
               * equal); } else { initiateVariableWithArray(methodDeclaration,
               * variableDeclaration, equal); } }
               */ else if (child instanceof ASTIf) {
                checkIf(child, descriptor);
            // } else if (child instanceof ASTLess || child instanceof ASTAnd) {
            //     MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            //     SimpleNode[] simpleNodes = new SimpleNode[1];
            //     simpleNodes[0] = (SimpleNode) child;
            //     checkVariableBoolean(methodDeclaration, null, simpleNodes);
            } else if (child instanceof ASTWhile) {
                checkWhile(child, descriptor);
            }
        }

        if (descriptor instanceof MethodDeclaration) {
            if (!((MethodDeclaration) descriptor).getWritedLocals()) {
                ((MethodDeclaration) descriptor).setWritedLocals(true);
                this.toFile(".limit locals " + ((MethodDeclaration) descriptor).getAllParametersAndVariables().size());
                toFile("");
            }
        }
    }

    private void checkMain(Node node, Descriptor descriptor) {
        System.out.println(node);

        String methodLine = ".method public static main([Ljava/lang/String;)V"; // all methods are public in Java--
        toFile(methodLine);

        Descriptor descriptor2 = classDeclaration.addMethod(node, "void " + node.toString());
        classDeclaration.getAllMethods().get("main()").addParameter(node,
                node.toString().split(" ")[3].split(":")[1] + " " + node.toString().split(" ")[4]);

        if (node.jjtGetNumChildren() == 1) {
            if (node.jjtGetChild(0) instanceof ASTMethodBody) {
                createMethodBody(node.jjtGetChild(0), descriptor2);
            }
        }

        toFile("");

        toFile("getstatic java/lang/System/out Ljava/io/PrintStream;");
        toFile("iload 0");
        toFile("invokevirtual java/io/PrintStream/println(I)V");

        toFile("getstatic java/lang/System/out Ljava/io/PrintStream;");
        toFile("iload 1");
        toFile("invokevirtual java/io/PrintStream/println(I)V");

        this.toFile("");
        this.toFile("return");
        this.toFile(".end method");
        this.toFile("");
    }

    private void checkIf(Node node, Descriptor descriptor) {
        System.out.println(node);
        if (node.jjtGetNumChildren() == 3) {
            if (node.jjtGetChild(0) instanceof ASTIfCondition) {
                checkIfCondition(node.jjtGetChild(0), descriptor);
            }
            if (node.jjtGetChild(1) instanceof ASTIfBody) {
                checkIfBody(node.jjtGetChild(1), descriptor);
            }
            if (node.jjtGetChild(2) instanceof ASTElseBody) {
                checkElseBody(node.jjtGetChild(2), descriptor);
            }
        }
    }

    private void checkIfCondition(Node node, Descriptor descriptor) {
        System.out.println(node);
        SimpleNode[] childs = new SimpleNode[1];
        childs[0] = (SimpleNode) node.jjtGetChild(0);
        checkVariableBoolean((MethodDeclaration) descriptor, null, childs, true);
        toFile("ldc 0");
        toFile("if_icmpeq ifBody");
        toFile("goto elseBody");
    }

    private void checkIfBody(Node node, Descriptor descriptor) {
        toFile("ifBody:");
        System.out.println(node);
        showChilds(node, descriptor);
        toFile("goto end");
    }

    private void checkElseBody(Node node, Descriptor descriptor) {
        toFile("elseBody:");
        System.out.println(node);
        showChilds(node, descriptor);
        toFile("end:");
    }

    private void checkWhile(Node node, Descriptor descriptor) {
        System.out.println(node);

        Random random = new Random();

        Long time = System.currentTimeMillis();
        Integer t = random.nextInt(1000);
        String x = String.valueOf(time*t);

        if (node.jjtGetNumChildren() == 2) {

            toFile("while_" + x + ":");
            

            if (node.jjtGetChild(0) instanceof ASTWhileCondition) {
                checkWhileCondition(node.jjtGetChild(0), descriptor);
                toFile("ldc 0");
                toFile("if_icmpne whileBody");
                toFile("goto endWhile");
            }
            if (node.jjtGetChild(1) instanceof ASTWhileBody) {
                toFile("whileBody:");
                checkWhileBody(node.jjtGetChild(1), descriptor);
                toFile("goto while_" + x);
            }

            toFile("endWhile:");
        }
    }

    private void checkWhileCondition(Node node, Descriptor descriptor) {
        System.out.println(node);
        SimpleNode[] childs = new SimpleNode[1];
        childs[0] = (SimpleNode) node.jjtGetChild(0);
        checkVariableBoolean((MethodDeclaration) descriptor, null, childs, true);
    }

    private void checkWhileBody(Node node, Descriptor descriptor) {
        System.out.println(node);
        showChilds(node, descriptor);
    }

    private void initiateArrayPosition(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode equal) {
        SimpleNode[] position = new SimpleNode[1];
        position[0] = (SimpleNode) equal.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        SimpleNode[] value = new SimpleNode[1];
        value[0] = (SimpleNode) equal.jjtGetChild(1);
        toFile("aload " + variableDeclaration.getIndex());
        checkVariableInt(methodDeclaration, variableDeclaration, position);
        checkVariableInt(methodDeclaration, variableDeclaration, value);
        toFile("iastore");
        System.out.println("position " + position[0].str + " of array " + variableDeclaration.getName()
                + " has been initialized with value " + value[0].str);
    }

    private void initiateVariableWithArray(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode bracket) {
        SimpleNode[] array = new SimpleNode[1];
        array[0] = bracket;
        SimpleNode[] position = new SimpleNode[1];
        position[0] = (SimpleNode) bracket.jjtGetChild(0).jjtGetChild(0);
        VariableDeclaration var = getVariable(methodDeclaration, array[0].str);
        if (var.getInitiated()) {
            if (variableDeclaration != null && variableDeclaration.getType().equals("int")) {

                variableDeclaration.setInitiated(true);
                System.out.println("Variable " + variableDeclaration.getName() + " has been assigned at position "
                        + position[0].str + " of array " + var.getName());
            }
        }
        toFile("aload " + var.getIndex());
        checkVariableInt(methodDeclaration, var, position);
        toFile("iaload");

    }

    private void checkArrayLength(Node node, Descriptor descriptor) {
        SimpleNode[] childs = new SimpleNode[2];
        childs[0] = (SimpleNode) node.jjtGetChild(0);
        childs[1] = (SimpleNode) node.jjtGetChild(1);
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, childs[0].str);
        if (childs[1].str.equals("length")) {
            if (variableDeclaration.getIsArray()) {
                if (variableDeclaration.getInitiated()) {
                    toFile("aload " + getVariable(methodDeclaration, variableDeclaration.getName()).getIndex());
                    toFile("arraylength");
                    return;
                } else {
                    String errorMessage = "Array " + childs[0].str + " already not be initialized";
                    showError(errorMessage);
                }
            } else {
                String errorMessage = "Variable " + childs[0].str + " is not one array to use method length";
                showError(errorMessage);
            }
        } else {
            String errorMessage = "Arrays only have method length";
            showError(errorMessage);
        }
    }

    private void createClass(Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1];
        System.out.println("found one class --> " + node);
        if (classDeclaration != null) {
            System.err.println("Error! Class '" + name + "' already exists!");
            System.exit(-1);
        }
        classDeclaration = new ClassDeclaration(node, name);

        this.openFile(classDeclaration.getName());

        this.toFile(".source " + classDeclaration.getName() + ".jmm");
        this.toFile(".class public " + classDeclaration.getName());
        this.toFile(".super java/lang/Object"); // TODO check if class extends
        this.toFile("");

        showChilds(node, classDeclaration);
    }

    private void createVariable(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];
        System.out.println("Found one variable --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);

        if (descriptor instanceof ClassDeclaration) {
            ClassDeclaration classDeclaration = (ClassDeclaration) descriptor;
            if (!classDeclaration.haveVariable(name)) {
                classDeclaration.addVariable(node, typeAndName);
            } else {
                String errorMessage = "Class " + classDeclaration.getName() + " already have variable with name "
                        + name;
                showError(errorMessage);
            }
        } else if (descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if (!methodDeclaration.haveVariable(name) && !methodDeclaration.haveParameter(name)) {
                methodDeclaration.addVariable(node, typeAndName);
            } else {
                String errorMessage = "Method " + methodDeclaration.getName() + " already have variable with name "
                        + name;
                showError(errorMessage);
            }
        }
    }

    private void createParameter(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];
        System.out.println("Found one parameter --> " + node);
        if (descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if (!methodDeclaration.haveParameter(name)) {
                System.out.println("Add variable " + node + " at " + descriptor);
                methodDeclaration.addParameter(node, typeAndName);
            } else {
                String errorMessage = methodDeclaration.getName() + " already have parameter with name " + name;
                showError(errorMessage);
            }
        }
    }

    private void createMethod(Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];

        String methodLine = ".method public"; // all methods are public in Java--
        methodLine += " " + name.substring(0, name.length() - 2); // the method name without parentheses
        try {
            methodLine += "()" + getSignature(typeAndName.split(" ")[0]);
            toFile(methodLine);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Found one method --> " + node);
        System.out.println("Add method " + node + " at " + classDeclaration.getNode());

        if (!classDeclaration.haveMethod(name)) {
            Descriptor descriptor = classDeclaration.addMethod(node, typeAndName);
            showChilds(node, descriptor);
        } else {
            String errorMessage = "Class " + classDeclaration.getName() + " already have method with name " + name;
            showError(errorMessage);
        }

        this.toFile("");
        try {
            if(getSignature(classDeclaration.getAllMethods().get(name).getType()).equals("[I")) {
                this.toFile("areturn");

            } else {
                this.toFile(getSignature(classDeclaration.getAllMethods().get(name).getType()).toLowerCase() + "return");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.toFile(".end method");
        this.toFile("");
    }

    private void createMethodBody(Node node, Descriptor descriptor) {
        System.out.println("Create method body at " + node.jjtGetParent());
        this.toFile(".limit stack " + 1 + ((MethodDeclaration) descriptor).getAllParameters().size());
        ((MethodDeclaration) descriptor).setWritedLocals(false);
        showChilds(node, descriptor);
    }

    private void checkReturn(Node node, Node parent) {
        toFile("");
        System.out.println("----------------- RETURN -------------------");
        String methodName = parent.toString().split(":")[1].split(" ")[1];
        System.out.println("Method " + methodName + " return");
        SimpleNode simpleNode = (SimpleNode) node.jjtGetChild(0);

        if (classDeclaration.haveMethod(methodName)) {
            MethodDeclaration methodDeclaration = classDeclaration.getAllMethods().get(methodName);
            SimpleNode[] simpleNodes = new SimpleNode[1];
            simpleNodes[0] = simpleNode;

            if (node.jjtGetNumChildren() == 1) {
                if (node.jjtGetChild(0) instanceof ASTDot) {
                    if (node.jjtGetChild(0).jjtGetNumChildren() == 2) {
                        if (methodDeclaration.getType().equals("int")) {
                            checkArrayLength(simpleNode, methodDeclaration);
                        }
                    } else if (node.jjtGetChild(0).jjtGetNumChildren() == 3) {
                        checkMethodWithDot(simpleNode, methodDeclaration);
                    }
                } else if (node.jjtGetChild(0) instanceof ASTBracket) {
                    System.out.println("here");
                    initiateVariableWithArray(methodDeclaration, null, simpleNode);
                } else {
                    if (methodDeclaration.getType().equals("int")) {
                        checkVariableInt(methodDeclaration, null, simpleNodes);
                        return;
                    } else if (methodDeclaration.getType().equals("boolean")) {
                        checkVariableBoolean(methodDeclaration, null, simpleNodes, true);
                        return;
                    } else if (methodDeclaration.getType().equals("int[]")) {
                        checkVariableIntArray(methodDeclaration, null, simpleNodes);
                        return;
                    }
                }
            } else if (node.jjtGetNumChildren() == 2) {
                simpleNodes = new SimpleNode[2];
                simpleNodes[0] = simpleNode;
                simpleNodes[1] = (SimpleNode) node.jjtGetChild(1);

                toFile("aload " + methodDeclaration.getAllVariables().get(simpleNodes[0].str).getIndex());
                checkVariableIntArray(methodDeclaration, null, simpleNodes);
                toFile("iaload");
                return;
            }
        } else {
            String errorMessage = classDeclaration.getName() + " dont have method " + methodName;
            showError(errorMessage);
        }
    }

    private void checkMethodWithDot(Node node, Descriptor descriptor) {
        System.out.println("Function with dot");
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        SimpleNode[] simpleNodes = new SimpleNode[1];
        simpleNodes[0] = (SimpleNode) node;
        checkMethodAndParameters(methodDeclaration, null, simpleNodes);
    }

    private void initiateVariable(Node node, Descriptor descriptor) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        SimpleNode child = (SimpleNode) node.jjtGetChild(0);

        System.out.println("Initiate one variable " + child.str);

        SimpleNode[] simpleNodes = new SimpleNode[1];
        simpleNodes[0] = (SimpleNode) node.jjtGetChild(1);

        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, child.str);

        if (simpleNodes[0] instanceof ASTDot) {
            if (simpleNodes[0].jjtGetNumChildren() == 3) {
                checkMethodAndParameters(methodDeclaration, variableDeclaration, simpleNodes);
            } else if (child.jjtGetNumChildren() == 2) {
                checkArrayLength(child, descriptor);
            }
        } else {
            if (variableDeclaration.getType().equals("int")) {
                checkVariableInt(methodDeclaration, variableDeclaration, simpleNodes);
                toFile("istore " + ((MethodDeclaration) descriptor).getAllVariables().get(child.str).getIndex());

            } else if (variableDeclaration.getType().equals("boolean")) {
                checkVariableBoolean(methodDeclaration, variableDeclaration, simpleNodes, false);
                toFile("istore " + ((MethodDeclaration) descriptor).getAllVariables().get(child.str).getIndex());

            } else if (variableDeclaration.getType().equals("int[]")) {
                System.out.println(child.str);
                System.out.println(simpleNodes[0].jjtGetNumChildren());
                System.out.println(node.jjtGetChild(1).jjtGetNumChildren());
                System.out.println(node.jjtGetChild(0).jjtGetNumChildren());

                if(simpleNodes[0].jjtGetNumChildren() == 0 && node.jjtGetChild(0).jjtGetNumChildren() == 0) {
                    toFile("");
                    if(!variableDeclaration.getInitiated()) {
                        String errorMessage = "Variable " + variableDeclaration.getName() + " hasn't been initialized yet";
                        showError(errorMessage);
                    }
                    toFile("aload " + variableDeclaration.getIndex());
                    simpleNodes[0] = (SimpleNode) node.jjtGetChild(1);
                    checkVariableIntArray(methodDeclaration, variableDeclaration, simpleNodes);
                    toFile("");
                } else if(node.jjtGetChild(1).jjtGetNumChildren() > 0) {

                    if(node.jjtGetChild(1).jjtGetChild(0) instanceof ASTNewArray) {
                        System.out.println("here1");
                        createArray(methodDeclaration, variableDeclaration, (SimpleNode) node.jjtGetChild(1).jjtGetChild(0));
                    } 
                } else if(node.jjtGetChild(0).jjtGetChild(0) instanceof ASTBracket) {
                    System.out.println("here2");
                    String name = child.str;
                    variableDeclaration = getVariable(methodDeclaration, name);
                    initiateArrayPosition(methodDeclaration, variableDeclaration, (SimpleNode) node);
                }else {
                    simpleNodes[0] = (SimpleNode) node.jjtGetChild(1);
                    checkVariableIntArray(methodDeclaration, variableDeclaration, simpleNodes);
                }
            }
        }
    }

    private void checkMethodAndParameters(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode[] simpleNodes) {
        SimpleNode s1 = (SimpleNode) simpleNodes[0].jjtGetChild(0);
        SimpleNode s2 = (SimpleNode) simpleNodes[0].jjtGetChild(1);
        s2.str += "()";
        if (classDeclaration.getName().equals(s1.str)) {
            if (classDeclaration.haveMethod(s2.str)) {
                MethodDeclaration method = classDeclaration.getAllMethods().get(s2.str);
                if (variableDeclaration != null) {
                    if (variableDeclaration.getType().equals(method.getType())) {
                        for (Integer i = 0; i < simpleNodes[0].jjtGetChild(2).jjtGetNumChildren(); i++) {
                            SimpleNode[] newChilds = new SimpleNode[1];
                            newChilds[0] = (SimpleNode) simpleNodes[0].jjtGetChild(2).jjtGetChild(i);
                            VariableDeclaration var = (VariableDeclaration) method.getAllParameters().get(i);
                            if (var.getType().equals("int")) {
                                checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                            } else if (var.getType().equals("boolean")) {
                                checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds, false);
                            } else if (var.getType().equals("int[]")) {
                                checkVariableIntArray(methodDeclaration, variableDeclaration, newChilds);
                            }
                        }
                        variableDeclaration.setInitiated(true);
                        return;
                    } else {
                        this.showReturnError(method, variableDeclaration);
                    }
                } else {
                    for (Integer i = 0; i < simpleNodes[0].jjtGetChild(2).jjtGetNumChildren(); i++) {
                        SimpleNode[] newChilds = new SimpleNode[1];
                        newChilds[0] = (SimpleNode) simpleNodes[0].jjtGetChild(2).jjtGetChild(i);
                        VariableDeclaration var = (VariableDeclaration) method.getAllParameters().get(i);
                        if (var.getType().equals("int")) {
                            checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                        } else if (var.getType().equals("boolean")) {
                            checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds, false);
                        } else if (var.getType().equals("int[]")) {
                            checkVariableIntArray(methodDeclaration, variableDeclaration, newChilds);
                        }
                    }
                }
            }
        }
    }

    private void checkVariableIntArray(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode[] simpleNodes) {

        if (variableDeclaration == null) {
            String name = simpleNodes[0].str;
            VariableDeclaration var = getVariable(methodDeclaration, name);

            if (var.getInitiated()) {
                if (simpleNodes.length == 1) {
                    if (methodDeclaration.getType().equals(var.getType())) {
                        toFile("aload " + var.getIndex());
                        return;
                    } else {
                        String errorMessage = "Parameter of method " + methodDeclaration.getName() + " ("
                                + methodDeclaration.getType() + ") is not of the same type of " + var.getName() + " ("
                                + var.getType() + ")";
                        showError(errorMessage);
                    }
                } else if (simpleNodes.length == 2) {
                    SimpleNode[] childs = new SimpleNode[1];
                    childs[0] = simpleNodes[1];
                    checkVariableInt(methodDeclaration, variableDeclaration, childs);
                }
            } else {
                String errorMessage = "Variable " + var.getName() + " hasn't been initialized yet, and arrays need a memory alocation";
                showError(errorMessage);
            }
        } else {
            String name = simpleNodes[0].str;
            VariableDeclaration dest = getVariable(methodDeclaration, name);

            if(dest.getIsArray()) {
                if(dest.getInitiated()) {
                    variableDeclaration.setInitiated(true);
                    toFile("astore " + dest.getIndex());
                } else {
                    String errorMessage = "The variable " + name + " dont be initialized";
                    showError(errorMessage);
                }
            } else {
                String errorMessage = "The variable " + name + " is not a array";
                showError(errorMessage);
            }
        }
    }

    private void createArray(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode newArray) {
        SimpleNode[] childs = new SimpleNode[1];
        childs[0] = (SimpleNode) newArray.jjtGetChild(0);

        checkVariableInt(methodDeclaration, null, childs);
        variableDeclaration.setInitiated(true);
        toFile("newarray int");
        toFile("astore " + variableDeclaration.getIndex());
    }

    private VariableDeclaration getVariable(MethodDeclaration methodDeclaration, String name) {
        VariableDeclaration variableDeclaration = null;
        if (name != null) {
            if (methodDeclaration.haveParameter(name)) {
                variableDeclaration = methodDeclaration.getParameter(name);
            } else if (methodDeclaration.haveVariable(name)) {
                variableDeclaration = methodDeclaration.getAllVariables().get(name);
            } else if (classDeclaration.haveVariable(name)) {
                variableDeclaration = classDeclaration.getAllVariables().get(name);
            } else {
                String errorMessage = "The variable " + name + " is not set in the scope of the method "
                        + methodDeclaration.getNode() + " nor in the scope of the class " + classDeclaration.getNode();
                showError(errorMessage);
            }
        }
        return variableDeclaration;
    }

    private void checkVariableInt(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode[] simpleNodes) {
        if (simpleNodes.length == 1) {
            try {
                Integer number = Integer.parseInt(simpleNodes[0].str);
                toFile("ldc " + number);
                if (variableDeclaration != null && !variableDeclaration.getInitiated()) {
                    variableDeclaration.setInitiated(true);
                }
                return;
            } catch (NumberFormatException e) {
                if (simpleNodes[0].op >= 1 && simpleNodes[0].op <= 4) {
                    System.out.println("Found sub operation " + MyConstants.ops[simpleNodes[0].op - 1]);
                    SimpleNode[] childs = new SimpleNode[1];
                    childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                    checkVariableInt(methodDeclaration, variableDeclaration, childs);
                    childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                    checkVariableInt(methodDeclaration, variableDeclaration, childs);
                    toFileOperation(simpleNodes[0].op - 1);
                } else if(simpleNodes[0] instanceof ASTDot) {
                    if(simpleNodes[0].jjtGetNumChildren() == 2) {
                        checkArrayLength(simpleNodes[0], methodDeclaration);
                    } else if(simpleNodes[0].jjtGetNumChildren() == 3) {

                    }
                } else if (simpleNodes[0].jjtGetNumChildren() > 0 && simpleNodes[0].jjtGetChild(0) instanceof ASTBracket) {
                    initiateVariableWithArray(methodDeclaration, variableDeclaration, simpleNodes[0]);
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[0].str, "int");
                    VariableDeclaration varDest = methodDeclaration.getAllVariables().get(simpleNodes[0].str);
                    if (variableDeclaration != null && !variableDeclaration.getInitiated()) {
                        variableDeclaration.setInitiated(true);
                    }
                    toFile("iload " + varDest.getIndex());
                }
            }
        }
    }

    private void toFileOperation(Integer index) {
        switch (MyConstants.ops[index]) {
        case "+": {
            toFile("iadd");
        }
            break;
        case "-": {
            toFile("isub");
        }
            break;
        case "*": {
            toFile("imul");
        }
            break;
        case "/": {
            toFile("idiv");
        }
            break;
        }
    }

    private void checkVariableBoolean(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode[] simpleNodes, Boolean comparation) {
        System.out.println(simpleNodes.length);
        System.out.println(simpleNodes[0].str);

        if (simpleNodes.length == 1) {
            if (simpleNodes[0].str != null && simpleNodes[0].str.equals("true")) {
                if (variableDeclaration != null) {
                    variableDeclaration.setInitiated(true);
                } else {
                    String errorMessage = "Is NULL";
                    showError(errorMessage);
                }
                toFile("ldc 1");
            } else if(simpleNodes[0].str != null && simpleNodes[0].str.equals("false")) {
                if (variableDeclaration != null) {
                    variableDeclaration.setInitiated(true);
                } else {
                    String errorMessage = "Is NULL";
                    showError(errorMessage);
                }
                toFile("ldc 0");
            } else if (simpleNodes[0] instanceof ASTLess) {
                System.out.println("Found sub operation " + MyConstants.ops[simpleNodes[0].op - 1]);
                SimpleNode[] childs = new SimpleNode[1];
                childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                checkVariableInt(methodDeclaration, variableDeclaration, childs);

                childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                checkVariableInt(methodDeclaration, variableDeclaration, childs);

                Random random = new Random();

                Long time = System.currentTimeMillis();
                Integer t = random.nextInt(1000);
                String x = String.valueOf(time*t);
                
                toFile("if_icmplt isTrue_" + x);
                toFile("goto isFalse_" + x);
    
                toFile("isTrue_" + x + ":");
                toFile("ldc 1");
                toFile("goto endLess_" + x);
                toFile("isFalse_" + x + ":");
                toFile("ldc 0");
                toFile("endLess_" + x + ":");

            } else if (simpleNodes[0].op.equals(MyConstants.AND)) {
                SimpleNode[] childs = new SimpleNode[1];
                childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                checkVariableBoolean(methodDeclaration, variableDeclaration, childs, comparation);

                childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                checkVariableBoolean(methodDeclaration, variableDeclaration, childs, comparation);

                toFile("iand");

            } else {
                checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[0].str, "boolean");
            }
        }
    }

    private void checkIfExistVariableAndYourType(MethodDeclaration methodDeclaration, String name, String type) {
        if (classDeclaration.haveMethod(name)) {
            if (classDeclaration.getAllMethods().get(name).getType().equals(type)) {
            } else {
                String errorMessage = "Type of " + name + "is not " + type;
                showError(errorMessage);
            }
        } else if (methodDeclaration.haveVariable(name)) {
            if (methodDeclaration.getAllVariables().get(name).getType().equals(type)) {
                methodDeclaration.getAllVariables().get(name).setInitiated(true);
            } else {
                String errorMessage = "Type of " + name + " is not " + type;
                showError(errorMessage);
            }
        } else if (methodDeclaration.haveParameter(name)) {
        } else if (classDeclaration.haveVariable(name)) {
            if (classDeclaration.getAllVariables().get(name).getType().equals(type)) {
                classDeclaration.getAllVariables().get(name).setInitiated(true);
            } else {
                String errorMessage = "Type of " + name + "is not " + type;
                showError(errorMessage);
            }
        } else {
            String errorMessage = "Variable " + name + " is not declared";
            showError(errorMessage);
        }
    }

    private void showError(String errorMessage) {
        System.out.println();
        System.out.println("ERROR --> " + errorMessage);
        System.out.println();
        System.exit(-1);
    }

    private void showReturnError(MethodDeclaration m, VariableDeclaration v) {
        String errorMessage = "Method " + m.getName() + " should return " + m.getType() + ", returning " + v.getType()
                + " instead on " + v.getName();
        showError(errorMessage);
    }

    private void toFile(String s) {
        if (this.f != null) {
            try {
                if (s.startsWith(".end method")) {
                    this.identLevel--;
                }
                f.write("\t".repeat(this.identLevel) + s + "\n");
                if (s.startsWith(".method")) {
                    this.identLevel++;
                }
            } catch (Exception e) {
            }
        } else {
            System.err.println("File was unexpectedely closed! Couldn't write: " + s);
            System.exit(-4);
        }
    }

    private boolean openFile(String cname) {
        try {
            if (!Files.exists(this.outputDir)) {
                Files.createDirectories(this.outputDir);
            }
            this.f = new FileWriter("output" + File.separator + cname + ".j");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void closeFile() {
        try {
            this.f.close();
        } catch (Exception e) {
        }
        return;
    }

    private static String getSignature(String returnType) throws Exception {
        String ret;
        switch (returnType) {
        case "boolean":
            ret = "Z";
            break;
        case "byte":
            ret = "B";
            break;
        case "char":
            ret = "C";
            break;
        case "short":
            ret = "S";
            break;
        case "int":
            ret = "I";
            break;
        case "long":
            ret = "J";
            break;
        case "flat":
            ret = "F";
            break;
        case "double":
            ret = "D";
            break;
        case "void":
            ret = "V";
            break;
        default:
            if (returnType.substring(returnType.length() - 2).equals("[]")) {
                // this is an array type
                return "[" + getSignature(returnType.substring(0, returnType.length() - 2));
            } else {
                // TODO might be a Fully qualiffied class name
                return "L";
            }
            // throw new Exception("The passed returnType is not valid! Got " + returnType);
        }

        return ret;
    }
}