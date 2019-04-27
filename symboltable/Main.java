package symboltable;

import java.util.HashSet;

import ast.*;

public class Main {

    static ClassDeclaration classDeclaration;

    public Main() {}

    public static void createSymbolTable(SimpleNode root) {
        System.out.println("\n\n----------Starting Symbol Table ----------\n");
        for(int i = 0 ; i < root.jjtGetNumChildren() ; i++) {
            Node child = root.jjtGetChild(i);
            String[] splited = child.toString().split(":");
            String type = splited[0];

            if(type.equals("Class declaration")) {
                String name = splited[1];
                createClass(child);
            }
        }
    }

    private static void showChilds(Node node, Descriptor descriptor) {
        for(int i = 0 ; i < node.jjtGetNumChildren() ; i++) {
            Node child = node.jjtGetChild(i);
            if(child instanceof ASTVarDeclaration) {
                createVariable(descriptor, child);
            } else if(child instanceof ASTMethodDeclaration) {
                createMethod(child);
            } else if(child instanceof ASTMethodParameter) {
                System.out.println("Parameter");
                createParameter(descriptor, child);
            } else if(child instanceof ASTMethodBody) {
                createMethodBody(child, descriptor);
            } else if(child instanceof ASTMethodReturn) {
                checkReturn(child, node);
            } else if(child instanceof ASTEqual) {
                initiateVariable(child, descriptor);
            } else if(child instanceof ASTDot) {
                if(child.jjtGetNumChildren() == 3) {
                    checkMethodWithDot(child, descriptor);
                } else if(child.jjtGetNumChildren() == 2) {
                    checkArrayLength(child, descriptor);
                }
            }
        }
    }

    private static void checkArrayLength(Node node, Descriptor descriptor) {
        SimpleNode[] childs = new SimpleNode[2];
        childs[0] = (SimpleNode)node.jjtGetChild(0);
        childs[1] = (SimpleNode)node.jjtGetChild(1);
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, childs[0].str);
        if(childs[1].str.equals("length")) {
            if(variableDeclaration.getIsArray()) {
                if(variableDeclaration.getInitiated()) {
                    return;
                } else {
                    String errorMessage = "Array " + childs[0].str  + " already not be initialized";
                    showError(errorMessage);
                }
            } else {
                String errorMessage = "Variable " + childs[0].str  + " is not one array to use method length";
                showError(errorMessage);
            }
        } else {
            String errorMessage = "Arrays only have method length";
            showError(errorMessage);
        }
    }

    private static void createClass(Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1];
        System.out.println("found one class --> " + node);
        if(classDeclaration != null){
            System.err.println("Error! Class '" + name + "' already exists!");
            System.exit(-1);
        }
        classDeclaration = new ClassDeclaration(node, name);
        showChilds(node, classDeclaration);
    }

    private static void createVariable(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];
        System.out.println("Found one variable --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);
        
        if(descriptor instanceof ClassDeclaration) {
            ClassDeclaration classDeclaration = (ClassDeclaration) descriptor;
            if(!classDeclaration.haveVariable(name)) {
                classDeclaration.addVariable(node, typeAndName);
            } else {
                String errorMessage = "Class " + classDeclaration.getName() + " already have variable with name " + name;
                showError(errorMessage);                
            }
        } else if(descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if(!methodDeclaration.haveVariable(name) && !methodDeclaration.haveParameter(name)) {
                if(!classDeclaration.haveVariable(name)) {
                    methodDeclaration.addVariable(node, typeAndName);
                } else {
                    String errorMessage = "Class " + classDeclaration.getName() + " already have variable with name " + name;
                    showError(errorMessage);                
                }
            } else {
                String errorMessage = "Method " + methodDeclaration.getName() + " already have variable with name " + name;
                showError(errorMessage);
            }
        }
    }

    private static void createParameter(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];     
        System.out.println("Found one parameter --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);
        if(descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if(!methodDeclaration.haveParameter(name)) {
                methodDeclaration.addParameter(node, typeAndName);
            } else {
                String errorMessage = methodDeclaration.getName() + " already have parameter with name " + name;
                showError(errorMessage);
            }
        }
    }

    private static void createMethod(Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];
        System.out.println("Found one method --> " + node);
        System.out.println("Add method " + node + " at " + classDeclaration.getNode());
        
        if(!classDeclaration.haveMethod(name)) {
            Descriptor descriptor = classDeclaration.addMethod(node, typeAndName);
            showChilds(node, descriptor);
        } else {
            String errorMessage = "Class " + classDeclaration.getName() + " already have method with name " + name;
            showError(errorMessage);
        }
    }

    private static void createMethodBody(Node node, Descriptor descriptor) {
        System.out.println("Create method body at " + node.jjtGetParent());
        showChilds(node, descriptor);
    }

    private static void checkReturn(Node node, Node parent) {
        System.out.println("----------------- RETURN -------------------");
        String methodName = parent.toString().split(":")[1].split(" ")[1];
        System.out.println("Method " + methodName + " return");
        SimpleNode simpleNode = (SimpleNode) node.jjtGetChild(0);
        if(classDeclaration.haveMethod(methodName)) {
            MethodDeclaration methodDeclaration = classDeclaration.getAllMethods().get(methodName);       
            if(simpleNode.jjtGetNumChildren() == 0) {
                if(methodDeclaration.getType().equals("int")) {
                    try {
                        Integer number = Integer.parseInt(simpleNode.str);
                        return;
                    } catch(NumberFormatException e) {
                        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, simpleNode.str);
                        if(variableDeclaration.getType().equals("int")) {
                            if(variableDeclaration.getInitiated()) {
                                return;
                            } else {
                                String errorMessage = "Variable " + variableDeclaration.getName() + " is not initialized";
                                showError(errorMessage);
                            }
                        } else {
                            String errorMessage = "Method " + methodName + " return type " + methodDeclaration.getType() + " and variable " + variableDeclaration.getName() + " is type " + variableDeclaration.getType();
                            showError(errorMessage);
                        }
                    }
                } else if(methodDeclaration.getType().equals("boolean")) {
                    if(simpleNode.str.equals("true") || simpleNode.str.equals("false")) {
                        return;
                    } else {
                        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, simpleNode.str);
                        if(variableDeclaration.getType().equals("boolean")) {
                            if(variableDeclaration.getInitiated()) {
                                return;
                            } else {
                                String errorMessage = "Variable " + variableDeclaration.getName() + " is not initialized";
                                showError(errorMessage);
                            }
                        } else {
                            String errorMessage = "Method " + methodName + " return type " + methodDeclaration.getType() + " and variable " + variableDeclaration.getName() + " is type " + variableDeclaration.getType();
                            showError(errorMessage);
                        }
                    }
                }
            } else if(simpleNode.jjtGetNumChildren() == 2 && !(simpleNode instanceof ASTDot)) {
                SimpleNode[] childs = new SimpleNode[2];
                childs[0] = (SimpleNode) simpleNode.jjtGetChild(0);
                childs[1] = (SimpleNode) simpleNode.jjtGetChild(1);

                if(methodDeclaration.getType().equals("int")) {
                    if(simpleNode.op >= 1 && simpleNode.op <= 4) {
                        checkVariableInt(methodDeclaration, null, childs);
                    }
                } else if(methodDeclaration.getType().equals("boolean")) {
                    if(simpleNode.op.equals(MyConstants.AND)) {
                        checkVariableBoolean(methodDeclaration, null, childs);
                    } else if(simpleNode.op.equals(MyConstants.LESS)) {
                        checkVariableInt(methodDeclaration, null, childs);
                    }
                }
            } else if(simpleNode.jjtGetNumChildren() >= 2) {
                if(simpleNode.jjtGetNumChildren() == 2) {
                    checkArrayLength(simpleNode, methodDeclaration);
                } else if(simpleNode.jjtGetNumChildren() == 3) {
                    checkMethodWithDot(simpleNode, methodDeclaration);
                }
            }
        } else {
            String errorMessage = classDeclaration.getName() + " dont have method " + methodName;
            showError(errorMessage);
        }
    }

    private static void checkMethodWithDot(Node node, Descriptor descriptor) {
        System.out.println("Function with dot");
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        SimpleNode child = (SimpleNode)node.jjtGetChild(0);
        SimpleNode[] simpleNodes = new SimpleNode[1];
        simpleNodes[0] = (SimpleNode)node;
        checkMethodAndParameters(methodDeclaration, null, simpleNodes);
    }

    private static void initiateVariable(Node node, Descriptor descriptor) {
        System.out.println("Initiate one variable");
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        SimpleNode child = (SimpleNode)node.jjtGetChild(0);

        SimpleNode[] simpleNodes = new SimpleNode[1];
        simpleNodes[0] = (SimpleNode)node.jjtGetChild(1);

        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, child.str);

        if(simpleNodes[0] instanceof ASTDot) {
            if(simpleNodes[0].jjtGetNumChildren() == 3) {
                checkMethodAndParameters(methodDeclaration, variableDeclaration, simpleNodes);
            } else if(child.jjtGetNumChildren() == 2) {
                checkArrayLength(child, descriptor);
            }
        } else {
            if(variableDeclaration.getType().equals("int")) {
                checkVariableInt(methodDeclaration, variableDeclaration, simpleNodes);
            } else if(variableDeclaration.getType().equals("boolean")) {
                checkVariableBoolean(methodDeclaration, variableDeclaration, simpleNodes);
            } else if(variableDeclaration.getType().equals("int[]")) {
                SimpleNode[] childs = new SimpleNode[1];
                childs[0] = (SimpleNode)node.jjtGetChild(1).jjtGetChild(0);
                checkVariableInt(methodDeclaration, variableDeclaration, childs);
                variableDeclaration.setInitiated(true, 0);

                //checkVariableIntArray(methodDeclaration, variableDeclaration, simpleNodes);
            }
        }
    }

    private static void checkMethodAndParameters(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode[] simpleNodes) {
        SimpleNode s1 = (SimpleNode)simpleNodes[0].jjtGetChild(0);
        SimpleNode s2 = (SimpleNode)simpleNodes[0].jjtGetChild(1);
        s2.str += "()";
        if(classDeclaration.getName().equals(s1.str)) {
            if(classDeclaration.haveMethod(s2.str)) {
                MethodDeclaration method = classDeclaration.getAllMethods().get(s2.str);
                if(variableDeclaration != null) {
                    if(variableDeclaration.getType().equals(method.getType())) {
                        for(Integer i = 0 ; i < simpleNodes[0].jjtGetChild(2).jjtGetNumChildren() ; i++) {
                            SimpleNode[] newChilds = new SimpleNode[1];
                            newChilds[0] = (SimpleNode)simpleNodes[0].jjtGetChild(2).jjtGetChild(i);
                            VariableDeclaration var = (VariableDeclaration)method.getAllParameters().values().toArray()[0];
                            if(((VariableDeclaration)method.getAllParameters().values().toArray()[i]).getType().equals("int")) {
                                checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                            } else if(((VariableDeclaration)method.getAllParameters().values().toArray()[i]).getType().equals("boolean")) {
                                checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds);
                            } else if(((VariableDeclaration)method.getAllParameters().values().toArray()[i]).getType().equals("int[]")) {
                                //checkVariableIntArray(methodDeclaration, variableDeclaration, newChilds);
                            }
                        }
                        variableDeclaration.setInitiated(true);                        
                        return;
                    } else {
                        String errorMessage = "Method " + method.getName() + " dont have return type equal than variable " + variableDeclaration.getName();
                        showError(errorMessage);
                    }
                } else {
                    for(Integer i = 0 ; i < simpleNodes[0].jjtGetChild(2).jjtGetNumChildren() ; i++) {
                        SimpleNode[] newChilds = new SimpleNode[1];
                        newChilds[0] = (SimpleNode)simpleNodes[0].jjtGetChild(2).jjtGetChild(i);
                        if(((VariableDeclaration)method.getAllParameters().values().toArray()[i]).getType().equals("int")) {
                            checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                        } else if(((VariableDeclaration)method.getAllParameters().values().toArray()[i]).getType().equals("boolean")) {
                            checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds);
                        } else if(((VariableDeclaration)method.getAllParameters().values().toArray()[i]).getType().equals("int[]")) {
                            //checkVariableIntArray(methodDeclaration, variableDeclaration, newChilds);
                        }
                    }
                }
            }
        }
    }
    
    private static VariableDeclaration getVariable(MethodDeclaration methodDeclaration, String name) {
        VariableDeclaration variableDeclaration = null;
        if(name != null) {
            if(methodDeclaration.haveParameter(name)) {
                variableDeclaration = methodDeclaration.getAllParameters().get(name);
            } else if(methodDeclaration.haveVariable(name)) {
                variableDeclaration = methodDeclaration.getAllVariables().get(name);
            } else if(classDeclaration.haveVariable(name)) {
                variableDeclaration = classDeclaration.getAllVariables().get(name);
            } else {
                String errorMessage = "Variable " + name + " dont exist on method " + methodDeclaration.getNode() + " and not in class " + classDeclaration.getNode();
                showError(errorMessage);
            }
        }
        return variableDeclaration;
    }

    private static void checkVariableInt(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode[] simpleNodes) {
        if(simpleNodes.length == 1) {
            try {
                Integer number = Integer.parseInt(simpleNodes[0].str);
                variableDeclaration.setInitiated(true);
                return;
            } catch(NumberFormatException e) {
                if(simpleNodes[0].op >= 1 && simpleNodes[0].op <= 4) {
                    System.out.println("Found sub operation " + MyConstants.ops[simpleNodes[0].op - 1]);
                    SimpleNode[] childs = new SimpleNode[2];
                    childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                    childs[1] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                    checkVariableInt(methodDeclaration, variableDeclaration, childs);
                } else if(simpleNodes[0] instanceof ASTDot) {
                    if(simpleNodes[0].jjtGetNumChildren() == 3) {
                        checkMethodWithDot(simpleNodes[0], methodDeclaration);
                    } else if(simpleNodes[0].jjtGetNumChildren() == 2) {
                        checkArrayLength(simpleNodes[0], methodDeclaration);
                    }
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[0].str, "int");
                }
            }
            if(variableDeclaration != null) {
                variableDeclaration.setInitiated(true);
            }
        } else if(simpleNodes.length == 2) {
            SimpleNode[] childs = new SimpleNode[2];
            childs[0] = (SimpleNode) simpleNodes[0];
            childs[1] = (SimpleNode) simpleNodes[1];
            try {
                Integer leftSideNumber = Integer.parseInt(childs[0].str);
                try {
                    //dois inteiros
                    Integer rightSideNumber = Integer.parseInt(childs[1].str);
                } catch (NumberFormatException e1) {
                    //recursividade a direita
                    if(childs[1].op >= 1 && childs[1].op <= 4) {
                        SimpleNode[] newChilds = new SimpleNode[2];
                        newChilds[0] = (SimpleNode) childs[1].jjtGetChild(0);
                        newChilds[1] = (SimpleNode) childs[1].jjtGetChild(1);
                        checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                    } else {
                        //variavel a direita
                        checkIfExistVariableAndYourType(methodDeclaration, childs[1].str, "int");
                    }
                }

                if(variableDeclaration != null) {
                    variableDeclaration.setInitiated(true);
                }
                return;
            } catch (NumberFormatException e1) {
                try {
                    //inteiro lado direito
                    Integer rightSideNumber = Integer.parseInt(childs[1].str);
                    //recursividade a esquerda
                    if(childs[0].op >= 1 && childs[0].op <= 4) {
                        SimpleNode[] newChilds = new SimpleNode[2];
                        newChilds[0] = (SimpleNode) childs[0].jjtGetChild(0);
                        newChilds[1] = (SimpleNode) childs[0].jjtGetChild(1);
                        checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                    } else {
                        //variavel a esquerda
                        checkIfExistVariableAndYourType(methodDeclaration, childs[0].str, "int");
                    }
                    if(variableDeclaration != null) {
                        variableDeclaration.setInitiated(true);
                    }
                } catch (NumberFormatException e2) {
                    //duas strings
                    if(childs[0].op >= 1 && childs[0].op <= 4) {
                        SimpleNode[] newChilds = new SimpleNode[2];
                        newChilds[0] = (SimpleNode) childs[0].jjtGetChild(0);
                        newChilds[1] = (SimpleNode) childs[0].jjtGetChild(1);
                        checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                    } else {
                        //variavel a esquerda
                        checkIfExistVariableAndYourType(methodDeclaration, childs[0].str, "int");
                    }

                    if(childs[1].op >= 1 && childs[1].op <= 4) {
                        SimpleNode[] newChilds = new SimpleNode[2];
                        newChilds[0] = (SimpleNode) childs[1].jjtGetChild(0);
                        newChilds[1] = (SimpleNode) childs[1].jjtGetChild(1);
                        checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                    } else {
                        //variavel a direita
                        checkIfExistVariableAndYourType(methodDeclaration, childs[1].str, "int");
                    }

                    if(variableDeclaration != null) {
                        variableDeclaration.setInitiated(true);
                    }
                }
            }
        }
    }

    private static void checkVariableBoolean(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode[] simpleNodes) {
        if(simpleNodes.length == 1) {
            if(simpleNodes[0].str.equals("true") || simpleNodes[0].str.equals("false")) {
                if(variableDeclaration != null) {
                    variableDeclaration.setInitiated(true);
                    return;
                } else {
                    String errorMessage = "Is NULL";
                    showError(errorMessage);
                }
            } else if(simpleNodes[0].op.equals(MyConstants.LESS)) {
                System.out.println("Found sub operation " + MyConstants.ops[simpleNodes[0].op - 1]);
                SimpleNode[] childs = new SimpleNode[2];
                childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                childs[1] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                checkVariableInt(methodDeclaration, variableDeclaration, childs);
            } else if(simpleNodes[0].op.equals(MyConstants.AND)) {
                SimpleNode[] childs = new SimpleNode[2];
                childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                childs[1] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                checkVariableBoolean(methodDeclaration, variableDeclaration, childs);
            } else {
                checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[0].str, "boolean");
            }
        } else if(simpleNodes.length == 2) {
            SimpleNode[] childs = new SimpleNode[2];
            childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
            childs[1] = (SimpleNode) simpleNodes[0].jjtGetChild(1);

            if(simpleNodes[0].str.equals("true") || simpleNodes[0].str.equals("false")) {
                if(simpleNodes[1].str.equals("true") || simpleNodes[1].str.equals("false")) {
                    variableDeclaration.setInitiated(true);
                    return;
                } else if(simpleNodes[1].op.equals(MyConstants.LESS)) {
                    SimpleNode[] newChilds = new SimpleNode[2];
                    newChilds[0] = (SimpleNode) simpleNodes[1].jjtGetChild(0);
                    newChilds[1] = (SimpleNode) simpleNodes[1].jjtGetChild(1);
                    checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                } else if(simpleNodes[1].op.equals(MyConstants.AND)) {
                    SimpleNode[] newChilds = new SimpleNode[2];
                    newChilds[0] = (SimpleNode) simpleNodes[1].jjtGetChild(0);
                    newChilds[1] = (SimpleNode) simpleNodes[1].jjtGetChild(1);
                    checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds);
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[1].str, "boolean");
                }
            } else if(simpleNodes[1].str.equals("true") || simpleNodes[1].str.equals("false")) {
                if(simpleNodes[0].op.equals(MyConstants.LESS)) {
                    SimpleNode[] newChilds = new SimpleNode[2];
                    newChilds[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                    newChilds[1] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                    checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                } else if(simpleNodes[0].op.equals(MyConstants.AND)) {
                    SimpleNode[] newChilds = new SimpleNode[2];
                    newChilds[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                    newChilds[1] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                    checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds);
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[0].str, "boolean");
                }
            } else {
                if(simpleNodes[0].op.equals(MyConstants.LESS)) {
                    SimpleNode[] newChilds = new SimpleNode[2];
                    newChilds[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                    newChilds[1] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                    checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                } else if(simpleNodes[0].op.equals(MyConstants.AND)) {
                    SimpleNode[] newChilds = new SimpleNode[2];
                    newChilds[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                    newChilds[1] = (SimpleNode) simpleNodes[0].jjtGetChild(1);
                    checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds);
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[0].str, "boolean");
                }

                if(simpleNodes[1].op.equals(MyConstants.LESS)) {
                    SimpleNode[] newChilds = new SimpleNode[2];
                    newChilds[0] = (SimpleNode) simpleNodes[1].jjtGetChild(0);
                    newChilds[1] = (SimpleNode) simpleNodes[1].jjtGetChild(1);
                    checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                } else if(simpleNodes[1].op.equals(MyConstants.AND)) {
                    SimpleNode[] newChilds = new SimpleNode[2];
                    newChilds[0] = (SimpleNode) simpleNodes[1].jjtGetChild(0);
                    newChilds[1] = (SimpleNode) simpleNodes[1].jjtGetChild(1);
                    checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds);
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[1].str, "boolean");
                }
            }
        }
    }

    private static void checkIfExistVariableAndYourType(MethodDeclaration methodDeclaration, String name, String type) { 
        if(classDeclaration.haveMethod(name)) {
            if(classDeclaration.getAllMethods().get(name).getType().equals(type)) {
            } else {
                String errorMessage = "Type of " + name + "is not " + type;
                showError(errorMessage);
            }
        } else if(methodDeclaration.haveVariable(name)) {
            if(methodDeclaration.getAllVariables().get(name).getType().equals(type)) {
                methodDeclaration.getAllVariables().get(name).setInitiated(true);                
                // if(methodDeclaration.getAllVariables().get(name).getInitiated()) {
                //     methodDeclaration.getAllVariables().get(name).setInitiated(true);
                //     return;
                // } else {
                //     String errorMessage = "Variable " + name + " from method " + methodDeclaration.getNode() + " is not initialized";
                //     showError(errorMessage);
                // }
            } else {
                String errorMessage = "Type of " + name + " is not " + type;
                showError(errorMessage);
            }
        } else if(classDeclaration.haveVariable(name)) {
            if(classDeclaration.getAllVariables().get(name).getType().equals(type)) {
                classDeclaration.getAllVariables().get(name).setInitiated(true);                
                // if(classDeclaration.getAllVariables().get(name).getInitiated()) {
                //     classDeclaration.getAllVariables().get(name).setInitiated(true);
                //     return;
                // } else {
                //     String errorMessage = "Variable " + name + " from class " + classDeclaration.getNode() + "is not initialized";
                //     showError(errorMessage);
                // }
            } else {
                String errorMessage = "Type of " + name + "is not " + type;
                showError(errorMessage);
            }
        } else {
            String errorMessage = "Variable " + name + " dont be declared";
            showError(errorMessage);
        }
    }

    private static void showError(String errorMessage) {
        System.out.println();
        System.out.println("ERROR --> " + errorMessage);
        System.out.println();
        System.exit(-1);
    }
}