package symboltable;

import java.util.HashSet;

import ast.*;

public class Main {

    ClassDeclaration classDeclaration;
    SimpleNode root;

    public Main(SimpleNode root) {
        this.root = root;
        Node child = root.jjtGetChild(0);


    }

    public void checkErrors() {
        System.out.println("\n\n----------Starting Checking Erros----------\n");
        for(int i = 0 ; i < root.jjtGetNumChildren() ; i++) {
            Node child = root.jjtGetChild(i);
            if(child instanceof ASTClassDeclaration && classDeclaration == null) {
                createClass(child);
            }
        }
    }

    public void createSymbolTable() {
        System.out.println("\n\n----------Starting creating symbol table----------\n");
        System.out.println(classDeclaration);
        showPrefix(1);
        System.out.println("METHODS of class " + classDeclaration.getName());

        for(MethodDeclaration method : classDeclaration.getAllMethods().values()) {
            showPrefix(2);
            System.out.println(method.getType() + " | " + method.getName());

            showPrefix(3);
            System.out.println("Parameters of " + method.getName());
            for(VariableDeclaration parameter : method.getAllParameters()) {
                showPrefix(3);
                System.out.println(parameter.getType() + " | " + parameter.getName());
            }
            System.out.println();
            showPrefix(3);
            System.out.println("Variables of " + method.getName());
            for(VariableDeclaration variable : method.getAllVariables().values()) {
                showPrefix(3);
                System.out.println(variable.getType() + " | " + variable.getName());
            }
        }
    }

    private void showPrefix(Integer number) {
        for(Integer i = 0 ; i < number ; i++) {
            System.out.print("\t");
        }
    }

    private void showChilds(Node node, Descriptor descriptor) {
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
            } else if(child instanceof ASTTerm) {
                SimpleNode array = (SimpleNode)child;
                SimpleNode equal = (SimpleNode)node.jjtGetChild(i + 1);
                i++;
                MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
                VariableDeclaration variableDeclaration = getVariable(methodDeclaration, array.str);
                if(variableDeclaration.getIsArray() && (equal instanceof ASTEqual)) {
                    initiateArrayPosition(methodDeclaration, variableDeclaration, equal);
                } else {
                    initiateVariableWithArray(methodDeclaration, variableDeclaration, equal);
                }
            } else if(child instanceof ASTIf) {
                checkIf(child, descriptor);
            } else if(child instanceof ASTLess || child instanceof ASTAnd) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
                SimpleNode[] simpleNodes = new SimpleNode[1];
                simpleNodes[0] = (SimpleNode) child;
                checkVariableBoolean(methodDeclaration, null, simpleNodes);
            } else if(child instanceof ASTWhile) {
                checkWhile(child, descriptor);
            } else if(child instanceof ASTMainDeclaration) {
                checkMain(child, descriptor);
            }
        }
    }

    private void checkMain(Node node, Descriptor descriptor) {
        System.out.println(node);

        Descriptor descriptor2 = classDeclaration.addMethod(node, "void " + node.toString());
        //classDeclaration.getAllMethods().get("Main declaration").addParameter(node, "args");

        if(node.jjtGetNumChildren() == 1) {
            if(node.jjtGetChild(0) instanceof ASTMethodBody) {
                createMethodBody(node.jjtGetChild(0), descriptor2);
            }
        }
    }

    private void checkIf(Node node, Descriptor descriptor) {
        System.out.println(node);
        if(node.jjtGetNumChildren() == 3) {
            if(node.jjtGetChild(0) instanceof ASTIfCondition) {
                checkIfCondition(node.jjtGetChild(0), descriptor);
            }
            if(node.jjtGetChild(1) instanceof ASTIfBody) {
                checkIfBody(node.jjtGetChild(1), descriptor);
            }
            if(node.jjtGetChild(2) instanceof ASTElseBody) {
                checkElseBody(node.jjtGetChild(2), descriptor);
            }
        }
    }

    private void checkIfCondition(Node node, Descriptor descriptor) {
        System.out.println(node);
        showChilds(node, descriptor);
    }

    private void checkIfBody(Node node, Descriptor descriptor) {
        System.out.println(node);
        showChilds(node, descriptor);
    }

    private void checkElseBody(Node node, Descriptor descriptor) {
        System.out.println(node);
        showChilds(node, descriptor);
    }

    private void checkWhile(Node node, Descriptor descriptor) {
        System.out.println(node);

        if(node.jjtGetNumChildren() == 2) {
            if(node.jjtGetChild(0) instanceof ASTWhileCondition) {
                checkWhileCondition(node.jjtGetChild(0), descriptor);
            }
            if(node.jjtGetChild(1) instanceof ASTWhileBody) {
                checkWhileBody(node.jjtGetChild(1), descriptor);
            }
        }
    }

    private void checkWhileCondition(Node node, Descriptor descriptor) {
        System.out.println(node);
        showChilds(node, descriptor);
    }

    private void checkWhileBody(Node node, Descriptor descriptor) {
        System.out.println(node);
        showChilds(node, descriptor);
    }

    private void initiateArrayPosition(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode equal) {
        SimpleNode[] position = new SimpleNode[1];
        position[0] = (SimpleNode) equal.jjtGetChild(0);
        SimpleNode[] value = new SimpleNode[1];
        value[0] = (SimpleNode) equal.jjtGetChild(1);
        checkVariableInt(methodDeclaration, variableDeclaration, position);
        checkVariableInt(methodDeclaration, variableDeclaration, value);
        System.out.println("position " + position[0].str + " of array " + variableDeclaration.getName() + " has been initialized with value " + value[0].str);
    }

    private void initiateVariableWithArray(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode equal) {
        SimpleNode[] array = new SimpleNode[1];
        array[0] = (SimpleNode) equal.jjtGetChild(0);
        SimpleNode[] position = new SimpleNode[1];
        position[0] = (SimpleNode) equal.jjtGetChild(1);
        if(variableDeclaration.getType().equals("int")) {
            VariableDeclaration var = getVariable(methodDeclaration, array[0].str);
            if(var.getInitiated()) {
                checkVariableInt(methodDeclaration, var, position);
                variableDeclaration.setInitiated(true);
                System.out.println("Variable " + variableDeclaration.getName() + " has been assigned at position " + position[0].str + " of array " + var.getName());
            }
        }
    }

    private void checkArrayLength(Node node, Descriptor descriptor) {
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

    private void createClass(Node node) {
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

    private void createVariable(Descriptor descriptor, Node node) {
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

    private void createParameter(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];     
        System.out.println("Found one parameter --> " + node);
        if(descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if(!methodDeclaration.haveParameter(name)) {
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

    private void createMethodBody(Node node, Descriptor descriptor) {
        System.out.println("Create method body at " + node.jjtGetParent());
        showChilds(node, descriptor);
    }

    private void checkReturn(Node node, Node parent) {
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
                                this.showReturnError(methodDeclaration, variableDeclaration);
                            }
                        } else {
                            this.showReturnError(methodDeclaration, variableDeclaration);
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
                                this.showReturnError(methodDeclaration, variableDeclaration);
                            }
                        } else {
                            this.showReturnError(methodDeclaration, variableDeclaration);
                        }
                    }
                } else if(methodDeclaration.getType().equals("int[]")) {
                    VariableDeclaration variableDeclaration = getVariable(methodDeclaration, simpleNode.str);
                    if(variableDeclaration.getType().equals("int[]")) {
                        if(variableDeclaration.getInitiated()) {
                            return;
                        } else {
                            String errorMessage = "Variable " + variableDeclaration.getName() + " is not initialized";
                            showError(errorMessage);
                        }
                    } else {
                        this.showReturnError(methodDeclaration, variableDeclaration);
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
                    if(methodDeclaration.getType().equals("int")) {
                        checkArrayLength(simpleNode, methodDeclaration);
                    } else {
                        String errorMessage = "Length of array is not possible to return on method " + methodName;
                        showError(errorMessage);
                    }
                } else if(simpleNode.jjtGetNumChildren() == 3) {
                    checkMethodWithDot(simpleNode, methodDeclaration);
                }
            }
        } else {
            String errorMessage = classDeclaration.getName() + " dont have method " + methodName;
            showError(errorMessage);
        }
    }

    private void checkMethodWithDot(Node node, Descriptor descriptor) {
        System.out.println("Function with dot");
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        SimpleNode child = (SimpleNode)node.jjtGetChild(0);
        SimpleNode[] simpleNodes = new SimpleNode[1];
        simpleNodes[0] = (SimpleNode)node;
        checkMethodAndParameters(methodDeclaration, null, simpleNodes);
    }

    private void initiateVariable(Node node, Descriptor descriptor) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        SimpleNode child = (SimpleNode)node.jjtGetChild(0);

        System.out.println("Initiate one variable " + child.str);


        SimpleNode[] simpleNodes = new SimpleNode[1];
        simpleNodes[0] = (SimpleNode)node.jjtGetChild(1);

        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, child.str);

        if(simpleNodes[0] instanceof ASTDot) {
            if(simpleNodes[0].jjtGetNumChildren() == 3) {
                checkMethodAndParameters(methodDeclaration, variableDeclaration, simpleNodes);
            } else if(child.jjtGetNumChildren() == 2) {
                checkArrayLength(child, descriptor);
            }
        } 
        else {
            if(variableDeclaration.getType().equals("int")) {
                checkVariableInt(methodDeclaration, variableDeclaration, simpleNodes);
            } else if(variableDeclaration.getType().equals("boolean")) {
                checkVariableBoolean(methodDeclaration, variableDeclaration, simpleNodes);
            } else if(variableDeclaration.getType().equals("int[]")) {
                if(variableDeclaration.getIsArray()) {
                    if(node.jjtGetNumChildren() == 2) {
                        SimpleNode[] childs = new SimpleNode[1];
                        childs[0] = (SimpleNode)node.jjtGetChild(1).jjtGetChild(0);
                        checkVariableInt(methodDeclaration, variableDeclaration, childs);
                    }
                }
            }
        }
    }

    private void checkMethodAndParameters(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode[] simpleNodes) {
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
                            VariableDeclaration var = (VariableDeclaration)method.getAllParameters().get(i);
                            if(var.getType().equals("int")) {
                                checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                            } else if(var.getType().equals("boolean")) {
                                checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds);
                            } else if(var.getType().equals("int[]")) {
                                checkVariableIntArray(methodDeclaration, variableDeclaration, newChilds);
                            }
                        }
                        variableDeclaration.setInitiated(true);                        
                        return;
                    } else {
                        this.showReturnError(method, variableDeclaration);
                    }
                } else {
                    for(Integer i = 0 ; i < simpleNodes[0].jjtGetChild(2).jjtGetNumChildren() ; i++) {
                        SimpleNode[] newChilds = new SimpleNode[1];
                        newChilds[0] = (SimpleNode)simpleNodes[0].jjtGetChild(2).jjtGetChild(i);
                        VariableDeclaration var = (VariableDeclaration)method.getAllParameters().get(i);
                        if(var.getType().equals("int")) {
                            checkVariableInt(methodDeclaration, variableDeclaration, newChilds);
                        } else if(var.getType().equals("boolean")) {
                            checkVariableBoolean(methodDeclaration, variableDeclaration, newChilds);
                        } else if(var.getType().equals("int[]")) {
                            checkVariableIntArray(methodDeclaration, variableDeclaration, newChilds);
                        }
                    }
                }
            }
        }
    }

    private void checkVariableIntArray(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode[] simpleNodes) {
        if(variableDeclaration == null) {
            String name = simpleNodes[0].str;
            VariableDeclaration var = getVariable(methodDeclaration, name);
            if(methodDeclaration.getType().equals(var.getType())) {
                if(var.getInitiated()) {
                    return;
                } else {
                    String errorMessage = "Variable " + var.getName() + " hasn't been initialized yet";
                    showError(errorMessage);
                }
            } else {
                String errorMessage = "Parameter of method " + methodDeclaration.getName() + " ("+methodDeclaration.getType()+") is not of the same type of " + var.getName() +" ("+var.getType()+")";
                showError(errorMessage);
            }
        }
    }
    
    private VariableDeclaration getVariable(MethodDeclaration methodDeclaration, String name) {
        VariableDeclaration variableDeclaration = null;
        if(name != null) {
            if(methodDeclaration.haveParameter(name)) {
                variableDeclaration = methodDeclaration.getParameter(name);
            } else if(methodDeclaration.haveVariable(name)) {
                variableDeclaration = methodDeclaration.getAllVariables().get(name);
            } else if(classDeclaration.haveVariable(name)) {
                variableDeclaration = classDeclaration.getAllVariables().get(name);
            } else {
                String errorMessage = "The variable " + name + " is not set in the scope of the method " + methodDeclaration.getNode() + " nor in the scope of the class " + classDeclaration.getNode();
                showError(errorMessage);
            }
        }
        return variableDeclaration;
    }

    private void checkVariableInt(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode[] simpleNodes) {
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

                    if(childs[0] instanceof ASTTerm) {
                        if(childs[0].jjtGetNumChildren() > 0) {
                            if(childs[0].jjtGetChild(0) instanceof ASTParenteses) {
                                childs[0] = ((SimpleNode)childs[0].jjtGetChild(0).jjtGetChild(0));
                            }
                        }
                    }
                    if(childs[1].jjtGetNumChildren() > 0) {
                        childs[1] = (SimpleNode) childs[1].jjtGetChild(0);
                    }
                    if(childs[0].jjtGetNumChildren() > 0) {
                        childs[0] = (SimpleNode) childs[0].jjtGetChild(0);
                    }
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
            if(childs[1].jjtGetNumChildren() > 0) {
                childs[1] = (SimpleNode) childs[1].jjtGetChild(0);
            }
            if(childs[0].jjtGetNumChildren() > 0) {
                childs[0] = (SimpleNode) childs[0].jjtGetChild(0);
            }
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

    private void checkVariableBoolean(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode[] simpleNodes) {
        System.out.println(simpleNodes.length);
        
        if(simpleNodes.length == 1) {
            if(simpleNodes[0].str != null && (simpleNodes[0].str.equals("true") || simpleNodes[0].str.equals("false"))) {
                if(variableDeclaration != null) {
                    variableDeclaration.setInitiated(true);
                    return;
                } else {
                    String errorMessage = "Is NULL";
                    showError(errorMessage);
                }
            } else if(simpleNodes[0] instanceof ASTLess) {
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
                    System.out.println("------------------" + simpleNodes[0]);
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

    private void checkIfExistVariableAndYourType(MethodDeclaration methodDeclaration, String name, String type) { 
        if(classDeclaration.haveMethod(name)) {
            if(classDeclaration.getAllMethods().get(name).getType().equals(type)) {
            } else {
                String errorMessage = "Type of " + name + "is not " + type;
                showError(errorMessage);
            }
        } else if(methodDeclaration.haveVariable(name)) {
            if(methodDeclaration.getAllVariables().get(name).getType().equals(type)) {
                methodDeclaration.getAllVariables().get(name).setInitiated(true);                
            } else {
                String errorMessage = "Type of " + name + " is not " + type;
                showError(errorMessage);
            }
        } else if(methodDeclaration.haveParameter(name)) {
        } else if(classDeclaration.haveVariable(name)) {
            if(classDeclaration.getAllVariables().get(name).getType().equals(type)) {
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
    
    private void showReturnError(MethodDeclaration m, VariableDeclaration v){
        String errorMessage = "Method " + m.getName() + " should return "+m.getType()+", returning "+v.getType()+" instead on " + v.getName();
        showError(errorMessage);
    }

}