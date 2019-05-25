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

    /**
     * @brief percorrer os filhos todos do nó passado como parametro
     * 
     * @param node       nó do qual se pretende explorar os seus filhos
     * @param descriptor é uma class ou um método do qual estamos a analisar os seu
     *                   filhos
     */
    private void showChilds(Node node, Descriptor descriptor) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);

            if (descriptor instanceof MethodDeclaration) {
                if (!((MethodDeclaration) descriptor).getWritedLocals()) {
                    if (!(child instanceof ASTVarDeclaration)) {
                        ((MethodDeclaration) descriptor).setWritedLocals(true);
                        Integer total = ((MethodDeclaration) descriptor).getAllParametersAndVariables().size() + 1;
                        toFile(".limit locals " + total);
                        toFile("");

                        if (((MethodDeclaration) descriptor).getName().equals("main()")) {
                            toFile("new " + classDeclaration.getName());
                            toFile("dup");
                            toFile("invokespecial " + classDeclaration.getName() + "/<init>()V");
                            toFile("astore 0");
                            toFile("");
                        }
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
                createParameter(descriptor, child);
            } else if (child instanceof ASTMethodBody) {
                createMethodBody(child, descriptor);
            } else if (child instanceof ASTMethodReturn) {
                checkReturn(child, node);
            } else if (child instanceof ASTEqual) {
                initiateVariable(child, descriptor);
            } else if (child instanceof ASTDot) {
                if (child.jjtGetNumChildren() == 3) {
                    checkMethodWithDot(null, child, descriptor);
                } else if (child.jjtGetNumChildren() == 2) {
                    checkArrayLength(child, descriptor);
                }
            } else if (child instanceof ASTIf) {
                checkIf(child, descriptor);
            } else if (child instanceof ASTWhile) {
                checkWhile(child, descriptor);
            }
        }
        if (descriptor instanceof MethodDeclaration) {
            if (!((MethodDeclaration) descriptor).getWritedLocals()) {
                ((MethodDeclaration) descriptor).setWritedLocals(true);
                Integer total = ((MethodDeclaration) descriptor).getAllParametersAndVariables().size() + 1;
                this.toFile(".limit locals " + total);
                toFile("");
            }
        }
    }

    /**
     * @brief verifica se a função main não tem erros e gera os byte codes da mesma
     * 
     * @param node       nó com o inicio da declaração da função main
     * @param descriptor objeto ClassDeclaration á qual o main vai pertencer
     */
    private void checkMain(Node node, Descriptor descriptor) {
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

        this.toFile("");
        this.toFile("return");
        this.toFile(".end method");
        this.toFile("");

        if (!classDeclaration.getWritedConstructor()) {
            classDeclaration.setWritedConstructor(true);
            toFile(".method public <init>()V");

            toFile("aload 0");
            toFile("invokespecial java/lang/Object/<init>()V");
            toFile("return");
            toFile(".end method");

            this.toFile("");
        }
    }

    /**
     * @brief verifica se o nó corresponde a um if e faz as chamadas as funções
     *        necessárias verificação do if
     * 
     * @param node       nó com o inicio da declaração de um if
     * @param descriptor objeto MethodDeclaration ao qual o if pertence
     */
    private void checkIf(Node node, Descriptor descriptor) {
        if (node.jjtGetNumChildren() == 3 && node instanceof ASTIf) {
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

    /**
     * @brief chama a função para verificar se a condição do if é válida
     * 
     * @param node       nó com o inicio da condição do if
     * @param descriptor objeto MethodDeclaration ao qual o if pertence
     */
    private void checkIfCondition(Node node, Descriptor descriptor) {
        SimpleNode[] childs = new SimpleNode[1];
        childs[0] = (SimpleNode) node.jjtGetChild(0);
        checkVariableBoolean((MethodDeclaration) descriptor, null, childs, true);
        toFile("ldc 0");
        toFile("if_icmpne ifBody");
        toFile("goto elseBody");
    }

    /**
     * @brief chama a função para percorrer todos os filhos do corpo do if e gera os
     *        byte codes para o corpo do if
     * 
     * @param node       nó com o inicio do corpo do if
     * @param descriptor objeto MethodDeclaration ao qual o if pertence
     */
    private void checkIfBody(Node node, Descriptor descriptor) {
        toFile("ifBody:");
        showChilds(node, descriptor);
        toFile("goto end");
    }

    /**
     * @brief chama a função para percorrer todos os filhos do corpo do if e gera os
     *        byte codes para o corpo do else
     * 
     * @param node       nó com o inicio do corpo do else
     * @param descriptor objeto MethodDeclaration ao qual o else pertence
     */
    private void checkElseBody(Node node, Descriptor descriptor) {
        toFile("elseBody:");
        showChilds(node, descriptor);
        toFile("end:");
    }

    /**
     * @brief verifica se o nó corresponde a um while e faz as chamadas as funções
     *        necessárias para verificação do while e gerar os seus byte codes
     * 
     * @param node       nó com o inicio da declaração de um while
     * @param descriptor objeto MethodDeclaration ao qual o while pertence
     */
    private void checkWhile(Node node, Descriptor descriptor) {
        Random random = new Random();

        Long time = System.currentTimeMillis();
        Integer t = random.nextInt(1000);
        String x = String.valueOf(time * t);

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

    /**
     * @brief chama a função para verificar se a condição do while é válida
     * 
     * @param node       nó com o inicio da condição do while
     * @param descriptor objeto MethodDeclaration ao qual o while pertence
     */
    private void checkWhileCondition(Node node, Descriptor descriptor) {
        SimpleNode[] childs = new SimpleNode[1];
        childs[0] = (SimpleNode) node.jjtGetChild(0);
        checkVariableBoolean((MethodDeclaration) descriptor, null, childs, true);
    }

    /**
     * @brief chama a função para percorrer todos os filhos do corpo do while
     * 
     * @param node       nó com o inicio do corpo do if
     * @param descriptor objeto MethodDeclaration ao qual o if pertence
     */
    private void checkWhileBody(Node node, Descriptor descriptor) {
        showChilds(node, descriptor);
    }

    /**
     * @brief inicia uma posicao de uma array com um dado valor que esta no
     *        SimpleNode equal
     * 
     * @param methodDeclaration   método no qual a posição do array está a ser
     * @param variableDeclaration variavel pertencente ao array
     * @param equal               nó com o conteudo com que a posicao do array vai
     *                            ser instanciado
     */
    private void initiateArrayPosition(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode equal) {
        SimpleNode[] position = new SimpleNode[1];
        position[0] = (SimpleNode) equal.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        SimpleNode[] value = new SimpleNode[1];
        value[0] = (SimpleNode) equal.jjtGetChild(1);

        if (variableDeclaration.getIsClassVariable()) {
            toFile("aload 0");
            try {
                toFile("getfield " + classDeclaration.getName() + "/" + variableDeclaration.getName() + " "
                        + getSignature(variableDeclaration.getType()));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else {
            toFile("iload " + variableDeclaration.getIndex());
        }
        checkVariableInt(methodDeclaration, variableDeclaration, position);
        checkVariableInt(methodDeclaration, variableDeclaration, value);
        toFile("iastore");
        System.out.println("position " + position[0].str + " of array " + variableDeclaration.getName()
                + " has been initialized with value " + value[0].str);
    }

    /**
     * @brief inicia uma variavel com a posicao de um array
     * 
     * @param methodDeclaration   método no qual a posição do array está a ser
     * @param variableDeclaration variavel que vai ser instanciada
     * @param equal               nó com o conteudo do nome do array a o respetivo
     *                            indice
     */
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
        if (var.getIsClassVariable()) {
            toFile("aload 0");
            try {
                toFile("getfield " + classDeclaration.getName() + "/" + var.getName() + " "
                        + getSignature(var.getType()));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else {
            toFile("aload " + var.getIndex());
        }
        checkVariableInt(methodDeclaration, var, position);
        toFile("iaload");
    }

    /**
     * @brief verifica se os filhos do node correspondem ao nome de uma variavel do
     *        tipo array e se o segundo filho é 'length' e gera os bytecodes
     *        adequados
     * 
     * @param node       no com o ponto para explorar os filhos
     * @param descriptor metodo onde esta a ser usado
     */
    private void checkArrayLength(Node node, Descriptor descriptor) {
        SimpleNode[] childs = new SimpleNode[2];
        childs[0] = (SimpleNode) node.jjtGetChild(0);
        childs[1] = (SimpleNode) node.jjtGetChild(1);
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, childs[0].str);
        if (childs[1].str.equals("length")) {
            if (variableDeclaration.getIsArray()) {
                if (variableDeclaration.getInitiated()) {

                    if (variableDeclaration.getIsClassVariable()) {
                        toFile("aload 0");
                        try {
                            toFile("getfield " + classDeclaration.getName() + "/" + variableDeclaration.getName() + " "
                                    + getSignature(variableDeclaration.getType()));
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    } else {
                        toFile("aload " + variableDeclaration.getIndex());
                    }
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

    /**
     * @brief instancia a class do ficehiro
     * 
     * @param node no com a declaracao de uma class
     */
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

    /**
     * @brief cria uma variavel com o nome e tipo contidos no parametro node e
     *        adiciona a class ou respetivo metodo passado no descriptor
     * 
     * @param descriptor metodo ou class onde a variavel esta a ser declarada
     * @param node       no com o nome e tipo da variavel
     */
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

                VariableDeclaration var = classDeclaration.getAllVariables().get(typeAndName.split(" ")[1]);

                try {
                    toFile(".field private " + var.getName() + " " + getSignature(var.getType()));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                toFile("");
            } else {
                String errorMessage = "Class " + classDeclaration.getName() + " already have variable with name "
                        + name;
                showError(errorMessage);
            }
        } else if (descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if (!methodDeclaration.haveVariable(name) && !methodDeclaration.haveParameter(name)) {
                methodDeclaration.addVariable(node, typeAndName, classDeclaration);
            } else {
                String errorMessage = "Method " + methodDeclaration.getName() + " already have variable with name "
                        + name;
                showError(errorMessage);
            }
        }
    }

    /**
     * @brief cria um parametro com o nome e tipo passados no parametro node e
     *        insere no respetivo metodo passado no descriptor
     * 
     * @param descriptor metodo onde o parametro esta a ser declarada
     * @param node       no com o nome e tipo do parametro
     */
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

    /**
     * @brief verifica se o metodo esta bem declarado e chama as respetivas funções
     *        para percorrer os seus filhos e gera os bytecodes desse método
     * 
     * @param node nó com a inicio da declaração de um método
     */
    private void createMethod(Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];

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
            if (getSignature(classDeclaration.getAllMethods().get(name).getType()).equals("[I")) {
                this.toFile("areturn");
            } else if (classDeclaration.getAllMethods().get(name).getType().equals("boolean")) {
                this.toFile("ireturn");
            } else {
                toFile(getSignature(classDeclaration.getAllMethods().get(name).getType()).toLowerCase() + "return");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.toFile(".end method");
        this.toFile("");
    }

    /**
     * @brief verifica se o corpo do metodo esta correto e gera os bytecodes
     *        necessarios
     * 
     * @param node       no com o inicio do corpo de uma metodo
     * @param descriptor class principal onde vai ser adiciona o metodo
     */
    private void createMethodBody(Node node, Descriptor descriptor) {
        System.out.println("Create method body at " + node.jjtGetParent());
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        if (!methodDeclaration.getName().equals("main()")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(".method public ");
            stringBuilder.append(methodDeclaration.getName().substring(0, methodDeclaration.getName().length() - 1));

            try {
                for (VariableDeclaration parameter : methodDeclaration.getAllParameters()) {
                    stringBuilder.append(getSignature(parameter.getType()));
                }

                stringBuilder.append(")");
                stringBuilder.append(getSignature(methodDeclaration.getType()));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            toFile(stringBuilder.toString());
        }

        this.toFile(".limit stack " + 1 + methodDeclaration.getAllParameters().size());
        ((MethodDeclaration) descriptor).setWritedLocals(false);
        showChilds(node, descriptor);
    }

    /**
     * @atraves do seu pai verifica se o return e compativel com o tipo de return
     *          declarado no pai e gera os bytecodes necessarios
     * 
     * @param node   no com o inicio do retorno de um metodo
     * @param parent no com o pai do return que vai ser o respetivo metodo
     */
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
                        checkMethodWithDot(null, simpleNode, methodDeclaration);
                    }
                } else if (node.jjtGetChild(0) instanceof ASTBracket) {
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

                VariableDeclaration var = methodDeclaration.getVariable(simpleNodes[0].str);

                if (var.getIsClassVariable()) {
                    toFile("aload 0");
                    try {
                        toFile("getfield " + classDeclaration.getName() + "/" + var.getName() + " "
                                + getSignature(var.getType()));
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    toFile("aload " + var.getIndex());
                }
                checkVariableIntArray(methodDeclaration, null, simpleNodes);
                toFile("iaload");
                return;
            }
        } else {
            String errorMessage = classDeclaration.getName() + " dont have method " + methodName;
            showError(errorMessage);
        }
    }

    /**
     * @brief cria uma variavel do mesmo tipo que a class principal
     * 
     * @param methodDeclaration   metodo on a a variavel do tipo class esta a ser
     *                            criada
     * @param node                no com o ponto onde tem as informacoes para
     *                            instanciar a variavel
     * @param variableDeclaration variavel onde vai ser guardada a instanciação da
     *                            class
     */
    private void checkNewClassMethod(MethodDeclaration methodDeclaration, Node node,
            VariableDeclaration variableDeclaration) {
        SimpleNode dot = (SimpleNode) node;
        SimpleNode astNew = (SimpleNode) node.jjtGetChild(0).jjtGetChild(0);
        SimpleNode className = (SimpleNode) astNew.jjtGetChild(0);
        SimpleNode methodName = (SimpleNode) node.jjtGetChild(1);
        methodName.str += "()";
        SimpleNode parenteses = (SimpleNode) node.jjtGetChild(2);

        toFile("new " + classDeclaration.getName());
        toFile("dup");
        toFile("invokespecial " + classDeclaration.getName() + "/<init>()V");
        toFile("astore 0");
        toFile("");

        SimpleNode[] simpleNodes = new SimpleNode[3];
        simpleNodes[0] = className;
        simpleNodes[1] = methodName;
        simpleNodes[2] = parenteses;

        checkMethodAndParameters(methodDeclaration, variableDeclaration, simpleNodes);
    }

    private void checkNewClassWithoutDot(MethodDeclaration methodDeclaration, Node node,
            VariableDeclaration variableDeclaration) {
        SimpleNode equal = (SimpleNode) node;
        SimpleNode astNew = (SimpleNode) node.jjtGetChild(0);
        SimpleNode className = (SimpleNode) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0);

        if (className.str.equals(classDeclaration.getName())) {
            toFile("new " + classDeclaration.getName());
            toFile("dup");
            toFile("invokespecial " + classDeclaration.getName() + "/<init>()V");
            toFile("astore " + variableDeclaration.getIndex());
            toFile("");
        }

        variableDeclaration.setInitiated(true);
    }

    /**
     * @brief se a variavelDeclaration nao for null chama a funcao e guarda o
     *        retorno nessa variavel se a variavel for null so faz a chamada a
     *        respetiva funcao
     * 
     * @param variableDeclaration variavel onde vai ser guardado o valor de retorno
     *                            da funcao a variavel pode existir ou nao
     * @param node                no com o ponto ionde tem o nome da class e o
     *                            metodo e os parametros do metodo
     * @param descriptor          metodo onde esta a ser usada a funcao
     */
    private void checkMethodWithDot(VariableDeclaration variableDeclaration, Node node, Descriptor descriptor) {
        System.out.println("Function with dot");
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;

        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetNumChildren() > 0
                && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTNew) {
            SimpleNode[] simpleNodes = new SimpleNode[1];
            simpleNodes[0] = (SimpleNode) node;
            checkNewClassMethod(methodDeclaration, simpleNodes[0], variableDeclaration);
        } else {
            String name = ((SimpleNode) node.jjtGetChild(0)).str;
            SimpleNode[] simpleNodes = new SimpleNode[3];
            simpleNodes[0] = (SimpleNode) node.jjtGetChild(0);
            simpleNodes[1] = (SimpleNode) node.jjtGetChild(1);
            simpleNodes[1].str += "()";
            simpleNodes[2] = (SimpleNode) node.jjtGetChild(2);

            checkMethodAndParameters(methodDeclaration, variableDeclaration, simpleNodes);
        }
    }

    /**
     * @brief instancia uma variavel com o respetivo valor
     * 
     * @param node       no com o igual onde tem o nome da variavel e o respetivo
     *                   valor/variavel que se pretende atribuir a variavel
     * @param descriptor class ou metodo onde a variavel esta a ser instanciada
     */
    private void initiateVariable(Node node, Descriptor descriptor) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        SimpleNode child = (SimpleNode) node.jjtGetChild(0);

        System.out.println("Initiate one variable " + child.str);
        SimpleNode[] simpleNodes = new SimpleNode[1];
        simpleNodes[0] = (SimpleNode) node.jjtGetChild(1);
        VariableDeclaration variableDeclaration = getVariable(methodDeclaration, child.str);

        if (node.jjtGetNumChildren() > 1 && node.jjtGetChild(1).jjtGetNumChildren() > 0
                && node.jjtGetChild(1).jjtGetChild(0) instanceof ASTNew) {
            simpleNodes[0] = (SimpleNode) node;
            checkNewClassWithoutDot(methodDeclaration, simpleNodes[0], variableDeclaration);
        } else if (simpleNodes[0] instanceof ASTDot) {
            if (simpleNodes[0].jjtGetNumChildren() > 0 && simpleNodes[0].jjtGetChild(0).jjtGetNumChildren() > 0
                    && simpleNodes[0].jjtGetChild(0).jjtGetChild(0) instanceof ASTNew) {
                checkNewClassMethod(methodDeclaration, simpleNodes[0], variableDeclaration);
                if(((MethodDeclaration) descriptor).getVariable(child.str).getIsClassVariable()) {
                    toFile("aload 0");
                    try {
                        toFile("putfield " + classDeclaration.getName() + "/" + variableDeclaration.getName() + " "
                        + getSignature(variableDeclaration.getType()));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    toFile("istore " + ((MethodDeclaration) descriptor).getVariable(child.str).getIndex());
                }
            } else if (simpleNodes[0].jjtGetNumChildren() == 3) {
                checkMethodWithDot(variableDeclaration, simpleNodes[0], descriptor);
                toFile("istore " + ((MethodDeclaration) descriptor).getVariable(child.str).getIndex());
            } else if (child.jjtGetNumChildren() == 2) {
                checkArrayLength(child, descriptor);
            }
        } else {
            if (variableDeclaration.getType().equals("int")) {
                checkVariableInt(methodDeclaration, variableDeclaration, simpleNodes);
                if(((MethodDeclaration) descriptor).getVariable(child.str).getIsClassVariable()) {
                    toFile("aload 0");
                    try {
                        toFile("putfield " + classDeclaration.getName() + "/" + variableDeclaration.getName() + " "
                        + getSignature(variableDeclaration.getType()));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    toFile("istore " + ((MethodDeclaration) descriptor).getVariable(child.str).getIndex());
                }
            } else if (variableDeclaration.getType().equals("boolean")) {
                checkVariableBoolean(methodDeclaration, variableDeclaration, simpleNodes, false);
                if(((MethodDeclaration) descriptor).getVariable(child.str).getIsClassVariable()) {
                    toFile("aload 0");
                    try {
                        toFile("putfield " + classDeclaration.getName() + "/" + variableDeclaration.getName() + " "
                        + getSignature(variableDeclaration.getType()));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    toFile("istore " + ((MethodDeclaration) descriptor).getVariable(child.str).getIndex());
                }
            } else if (variableDeclaration.getType().equals("int[]")) {
                if (simpleNodes[0].jjtGetNumChildren() == 0 && node.jjtGetChild(0).jjtGetNumChildren() == 0) {
                    toFile("");
                    if (!variableDeclaration.getInitiated()) {
                        String errorMessage = "Variable " + variableDeclaration.getName()
                                + " hasn't been initialized yet";
                        showError(errorMessage);
                    }

                    if (variableDeclaration.getIsClassVariable()) {
                        toFile("aload 0");
                        try {
                            toFile("getfield " + classDeclaration.getName() + "/" + variableDeclaration.getName() + " "
                                    + getSignature(variableDeclaration.getType()));
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    } else {
                        toFile("aload " + variableDeclaration.getIndex());
                    }
                    simpleNodes[0] = (SimpleNode) node.jjtGetChild(1);
                    checkVariableIntArray(methodDeclaration, variableDeclaration, simpleNodes);
                    toFile("");
                } else if (node.jjtGetChild(1).jjtGetNumChildren() > 0) {
                    if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTNewArray) {
                        createArray(methodDeclaration, variableDeclaration,
                                (SimpleNode) node.jjtGetChild(1).jjtGetChild(0));
                    }
                } else if (node.jjtGetChild(0).jjtGetChild(0) instanceof ASTBracket) {
                    String name = child.str;
                    variableDeclaration = getVariable(methodDeclaration, name);
                    initiateArrayPosition(methodDeclaration, variableDeclaration, (SimpleNode) node);
                } else {
                    simpleNodes[0] = (SimpleNode) node.jjtGetChild(1);
                    checkVariableIntArray(methodDeclaration, variableDeclaration, simpleNodes);
                }
            }
        }
    }

    /**
     * @brief quando se faz uma chamada a uma funcao verifica se os o metodo retorna
     *        o mesmo tipo que a variableDeclaration caso naso seja null, e verifica
     *        se todos os parametros passados correspondem aos existentes na funcao
     * 
     * @param methodDeclaration   metodo onde esta a ser usado
     * @param variableDeclaration variavel a atribuir o retorno da funcao
     * @param simpleNodes         array com os nos da class, metodo e os parametos
     *                            passados ao metodo
     */
    private void checkMethodAndParameters(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode[] simpleNodes) {
        StringBuilder stringBuilder = new StringBuilder();
        VariableDeclaration var2 = getVariable(methodDeclaration, simpleNodes[0].str);
        MethodDeclaration method = classDeclaration.getAllMethods().get(simpleNodes[1].str);

        if (classDeclaration.getName().equals(simpleNodes[0].str) || simpleNodes[0].str.equals("this")
                || (var2 != null && var2.getIsClassInstance())) {
            if (classDeclaration.haveMethod(simpleNodes[1].str)) {

                if (var2 != null && var2.getIsClassInstance()) {
                    toFile("aload " + var2.getIndex());
                } else {
                    toFile("aload 0");
                }

                stringBuilder.append("invokevirtual ");
                stringBuilder.append(classDeclaration.getName());
                stringBuilder.append("/");
                stringBuilder.append(simpleNodes[1].getStr().substring(0, simpleNodes[1].getStr().length() - 2));
                stringBuilder.append("(");
            }
        } else {
            stringBuilder.append("invokestatic ");
            stringBuilder.append(simpleNodes[0].getStr());
            stringBuilder.append("/");
            stringBuilder.append(simpleNodes[1].getStr().substring(0, simpleNodes[1].getStr().length() - 2));
            stringBuilder.append("(");
        }

        if (simpleNodes[2] instanceof ASTParenteses) {
            if (simpleNodes[2].jjtGetNumChildren() > 0 && simpleNodes[2].jjtGetChild(0) instanceof ASTDot) {
                System.out.println("*************************************");

                checkMethodWithDot(variableDeclaration, simpleNodes[2].jjtGetChild(0), methodDeclaration);

                SimpleNode n = (SimpleNode) simpleNodes[2].jjtGetChild(0).jjtGetChild(1);
                MethodDeclaration m = classDeclaration.getAllMethods().get(n.str);

                System.out.println(n.str);

                try {
                    if (m != null) {
                        stringBuilder.append(getSignature(m.getType()));
                    } else if (variableDeclaration != null) {
                        stringBuilder.append(getSignature(variableDeclaration.getType()));
                    }
                    stringBuilder.append(")");
                    if (method != null) {
                        stringBuilder.append(getSignature(method.getType()));
                    } else if (variableDeclaration != null) {
                        stringBuilder.append(getSignature(variableDeclaration.getType()));
                    } else {
                        stringBuilder.append("V");
                    }
                    toFile(stringBuilder.toString());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                System.out.println("..............................................");

                if (variableDeclaration != null) {
                    if (method != null) {
                        if (variableDeclaration.getType().equals(method.getType())) {
                            if (method.getAllParameters().size() == simpleNodes[2].jjtGetNumChildren()) {
                                for (Integer i = 0; i < method.getAllParameters().size(); i++) {
                                    if (method.getAllParameters().get(i).getType().equals("int")) {
                                        SimpleNode[] childs = new SimpleNode[1];
                                        childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                        checkVariableInt(methodDeclaration, null, childs);
                                    } else if (method.getAllParameters().get(i).getType().equals("boolean")) {
                                        SimpleNode[] childs = new SimpleNode[1];
                                        childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                        checkVariableBoolean(methodDeclaration, null, childs, false);
                                    } else if (method.getAllParameters().get(i).getType().equals("int[]")) {
                                        SimpleNode[] childs = new SimpleNode[1];
                                        childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                        checkVariableIntArray(methodDeclaration, null, childs);
                                    }

                                    try {
                                        stringBuilder.append(getSignature(method.getAllParameters().get(i).getType()));
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
                        for (Integer i = 0; i < simpleNodes[2].jjtGetNumChildren(); i++) {
                            SimpleNode child = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                            VariableDeclaration var = methodDeclaration.getVariable(child.str);
                            if (var != null) {
                                if (var.getType().equals("int")) {
                                    SimpleNode[] childs = new SimpleNode[1];
                                    childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                    checkVariableInt(methodDeclaration, null, childs);
                                } else if (var.getType().equals("boolean")) {
                                    SimpleNode[] childs = new SimpleNode[1];
                                    childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                    checkVariableBoolean(methodDeclaration, null, childs, false);
                                } else if (var.getType().equals("int[]")) {
                                    SimpleNode[] childs = new SimpleNode[1];
                                    childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                    checkVariableIntArray(methodDeclaration, null, childs);
                                }
                                try {
                                    stringBuilder.append(getSignature(var.getType()));
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } else if (child.str != null && child.str.equals("true")) {
                                toFile("ldc 1");
                                stringBuilder.append("I");
                            } else if (child.str != null && child.str.equals("false")) {
                                toFile("ldc 0");
                                stringBuilder.append("I");
                            } else if (child.op > 0 && child.op <= 4) {
                                SimpleNode[] childs = new SimpleNode[1];
                                childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                checkVariableInt(methodDeclaration, null, childs);
                                stringBuilder.append("I");
                            } else {
                                try {
                                    Integer number = Integer.parseInt(child.str);
                                    toFile("ldc " + number.toString());
                                    stringBuilder.append("I");
                                } catch (Exception e) {
                                    String errorMessage = "parameter unvailable";
                                    showError(errorMessage);
                                }
                            }
                        }
                    }
                } else {
                    for (Integer i = 0; i < simpleNodes[2].jjtGetNumChildren(); i++) {
                        SimpleNode child = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                        VariableDeclaration var = methodDeclaration.getVariable(child.str);
                        if (var != null) {
                            if (var.getType().equals("int")) {
                                SimpleNode[] childs = new SimpleNode[1];
                                childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                checkVariableInt(methodDeclaration, null, childs);
                            } else if (var.getType().equals("boolean")) {
                                SimpleNode[] childs = new SimpleNode[1];
                                childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                checkVariableBoolean(methodDeclaration, null, childs, false);
                            } else if (var.getType().equals("int[]")) {
                                SimpleNode[] childs = new SimpleNode[1];
                                childs[0] = (SimpleNode) simpleNodes[2].jjtGetChild(i);
                                checkVariableIntArray(methodDeclaration, null, childs);
                            }
                            try {
                                stringBuilder.append(getSignature(var.getType()));
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else if (child.str != null && child.str.equals("true")) {
                            toFile("ldc 1");
                            stringBuilder.append("I");
                        } else if (child.str != null && child.str.equals("false")) {
                            toFile("ldc 0");
                            stringBuilder.append("I");
                        } else {
                            try {
                                Integer number = Integer.parseInt(child.str);
                                toFile("ldc " + number.toString());
                                stringBuilder.append("I");
                            } catch (Exception e) {
                                String errorMessage = "parameter unvailable";
                                showError(errorMessage);
                            }
                        }
                    }

                }
                try {
                    stringBuilder.append(")");
                    if (method != null) {
                        stringBuilder.append(getSignature(method.getType()));
                    } else if (variableDeclaration != null) {
                        stringBuilder.append(getSignature(variableDeclaration.getType()));
                    } else {
                        stringBuilder.append("V");
                    }
                    toFile(stringBuilder.toString());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * @brief verifica se uma array esta a ser bem declarado, e gera os bytecodes
     *        necessarios
     * 
     * @param methodDeclaration   metodo onde o array esta a ser usado
     * @param variableDeclaration variavel se nao null a atribuir o valor do array
     * @param simpleNodes         array com o no do inicio da declaracao do array
     */
    private void checkVariableIntArray(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode[] simpleNodes) {

        if (variableDeclaration == null) {
            String name = simpleNodes[0].str;
            VariableDeclaration var = getVariable(methodDeclaration, name);

            if (var.getInitiated()) {
                if (simpleNodes.length == 1) {
                    if (methodDeclaration.getType().equals(var.getType())) {

                        if (var.getIsClassVariable()) {
                            toFile("aload 0");
                            try {
                                toFile("getfield " + classDeclaration.getName() + "/" + var.getName() + " "
                                        + getSignature(var.getType()));
                            } catch (Exception e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        } else {
                            toFile("aload " + variableDeclaration.getIndex());
                        }
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
                String errorMessage = "Variable " + var.getName()
                        + " hasn't been initialized yet, and arrays need a memory alocation";
                showError(errorMessage);
            }
        } else {
            String name = simpleNodes[0].str;
            VariableDeclaration dest = getVariable(methodDeclaration, name);

            if (dest.getIsArray()) {
                if (dest.getInitiated()) {
                    variableDeclaration.setInitiated(true);

                    if (dest.getIsClassVariable()) {
                        toFile("aload 0");
                        try {
                            toFile("putfield " + classDeclaration.getName() + "/" + dest.getName() + " "
                                    + getSignature(dest.getType()));
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    } else {
                        toFile("astore " + dest.getIndex());
                    }
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

    /**
     * @brief cria um array e gera os bytecodes necessarios
     * 
     * @param methodDeclaration   metodo onde o array existe
     * @param variableDeclaration variavel se nao null onde vai ser guardado o valor
     *                            do array
     * @param newArray            no com o inicio da criacao de uma array
     */
    private void createArray(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode newArray) {
        SimpleNode[] childs = new SimpleNode[1];
        childs[0] = (SimpleNode) newArray.jjtGetChild(0);

        if (variableDeclaration.getIsClassVariable()) {
            toFile("aload 0");
        }

        checkVariableInt(methodDeclaration, null, childs);
        variableDeclaration.setInitiated(true);
        toFile("newarray int");
        if (variableDeclaration.getIsClassVariable()) {
            try {
                toFile("putfield " + classDeclaration.getName() + "/" + variableDeclaration.getName() + " "
                        + getSignature(variableDeclaration.getType()));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            toFile("astore " + variableDeclaration.getIndex());
        }
    }

    /**
     * retorna a variavel com o nome 'name' do metodo 'methodDeclaration' caso
     * exista, se nao retorna null
     */
    private VariableDeclaration getVariable(MethodDeclaration methodDeclaration, String name) {
        VariableDeclaration variableDeclaration = null;
        if (name != null) {
            if (methodDeclaration.haveParameter(name)) {
                variableDeclaration = methodDeclaration.getParameter(name);
            } else if (methodDeclaration.haveVariable(name)) {
                variableDeclaration = methodDeclaration.getVariable(name);
            } else if (classDeclaration.haveVariable(name)) {
                variableDeclaration = classDeclaration.getAllVariables().get(name);
            } /*
               * else { String errorMessage = "The variable " + name +
               * " is not set in the scope of the method " + methodDeclaration.getNode() +
               * " nor in the scope of the class " + classDeclaration.getNode();
               * showError(errorMessage); }
               */
        }
        return variableDeclaration;
    }

    /**
     * @brief verifica recursivamente se todas as variaveis/valores sao do tipo int
     *        e gera os bytecodes necessarios
     * 
     * @param methodDeclaration   metodo a que a variavel pertence
     * @param variableDeclaration nome da variavel a instanciar se nao for null
     * @param simpleNodes         no com o inicio das operaçoes
     */
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
                    SimpleNode[] left = new SimpleNode[1];
                    left[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0);
                    SimpleNode[] rigth = new SimpleNode[1];
                    rigth[0] = (SimpleNode) simpleNodes[0].jjtGetChild(1);

                    // otimização stack
                    if (left[0].op != 0 || rigth[0].op == 0) {
                        checkVariableInt(methodDeclaration, variableDeclaration, left);
                        checkVariableInt(methodDeclaration, variableDeclaration, rigth);
                    } else if (rigth[0].op != 0) {
                        checkVariableInt(methodDeclaration, variableDeclaration, rigth);
                        checkVariableInt(methodDeclaration, variableDeclaration, left);
                    }
                    toFileOperation(simpleNodes[0].op - 1);
                } else if (simpleNodes[0] instanceof ASTDot) {
                    if (simpleNodes[0].jjtGetNumChildren() == 2) {
                        checkArrayLength(simpleNodes[0], methodDeclaration);
                    } else if (simpleNodes[0].jjtGetNumChildren() == 3) {
                        checkMethodWithDot(variableDeclaration, simpleNodes[0], methodDeclaration);
                    }
                } else if (simpleNodes[0].jjtGetNumChildren() > 0
                        && simpleNodes[0].jjtGetChild(0) instanceof ASTBracket) {
                    initiateVariableWithArray(methodDeclaration, variableDeclaration, simpleNodes[0]);
                } else if (simpleNodes[0].jjtGetNumChildren() > 0
                        && simpleNodes[0].jjtGetChild(0) instanceof ASTParenteses) {
                    SimpleNode[] childs = new SimpleNode[1];
                    childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0).jjtGetChild(0);
                    checkVariableInt(methodDeclaration, variableDeclaration, childs);
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[0].str, "int");
                    VariableDeclaration varDest = methodDeclaration.getVariable(simpleNodes[0].str);
                    if (variableDeclaration != null && !variableDeclaration.getInitiated()) {
                        variableDeclaration.setInitiated(true);
                    }

                    if (varDest.getIsClassVariable()) {
                        try {
                            toFile("putfield " + classDeclaration.getName() + "/" + varDest.getName() + " "
                                    + getSignature(varDest.getType()));
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    } else {
                        toFile("iload " + varDest.getIndex());
                    }
                }
            }
        }
    }

    /**
     * @brief gera os bytecodes para as operações de +, -, *, /
     * 
     * @param index indice da respetiva operacao
     */
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

    /**
     * @brief verifica recursivamente se todas as variaveis/valores sao do tipo
     *        boolean e gera os bytecodes necessarios
     * 
     * @param methodDeclaration   metodo a que a variavel pertence
     * @param variableDeclaration nome da variavel a instanciar se nao for null
     * @param simpleNodes         no com o inicio das operaçoes
     */
    private void checkVariableBoolean(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration,
            SimpleNode[] simpleNodes, Boolean comparation) {
        if (simpleNodes.length == 1) {
            if (simpleNodes[0].str != null && simpleNodes[0].str.equals("true")) {
                if (variableDeclaration != null) {
                    variableDeclaration.setInitiated(true);
                } else {
                    String errorMessage = "Is NULL";
                    showError(errorMessage);
                }
                toFile("ldc 1");
            } else if (simpleNodes[0].str != null && simpleNodes[0].str.equals("false")) {
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
                String x = String.valueOf(time * t);

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

            } else if (simpleNodes[0] instanceof ASTDot) {
                if (simpleNodes[0].jjtGetNumChildren() == 2) {
                    checkArrayLength(simpleNodes[0], methodDeclaration);
                } else if (simpleNodes[0].jjtGetNumChildren() == 3) {

                }
                checkMethodWithDot(null, simpleNodes[0], methodDeclaration);
            } else if (simpleNodes[0].jjtGetNumChildren() > 0
                    && simpleNodes[0].jjtGetChild(0) instanceof ASTParenteses) {
                SimpleNode[] childs = new SimpleNode[1];
                childs[0] = (SimpleNode) simpleNodes[0].jjtGetChild(0).jjtGetChild(0);
                checkVariableBoolean(methodDeclaration, variableDeclaration, childs, comparation);
            } else {
                checkIfExistVariableAndYourType(methodDeclaration, simpleNodes[0].str, "boolean");
                VariableDeclaration varDest = methodDeclaration.getVariable(simpleNodes[0].str);
                if (variableDeclaration != null && !variableDeclaration.getInitiated()) {
                    variableDeclaration.setInitiated(true);
                }
                toFile("iload " + varDest.getIndex());
            }
        }
    }

    /**
     * 
     * @brief verifica se no metodo existe a variavel com o nome 'name' e se o seu
     *        tipo coresponde ao tipo 'type'
     * 
     * @param methodDeclaration metodo onde devera existir a variavel
     * @param name              nome da variavel que se pretende ver se existe
     * @param type              tipo da variavel que se pretende
     */
    private void checkIfExistVariableAndYourType(MethodDeclaration methodDeclaration, String name, String type) {
        if (classDeclaration.haveMethod(name)) {
            if (classDeclaration.getAllMethods().get(name).getType().equals(type)) {
            } else {
                String errorMessage = "Type of " + name + "is not " + type;
                showError(errorMessage);
            }
        } else if (methodDeclaration.haveVariable(name)) {
            if (methodDeclaration.getVariable(name).getType().equals(type)) {
                methodDeclaration.getVariable(name).setInitiated(true);
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

    /**
     * @brief quando aluma coisa nao esta de acordo, mostra a mensagem de error e
     *        encerra o programa
     * 
     * @param errorMessage mensagem de erro
     */
    private void showError(String errorMessage) {
        System.out.println();
        System.out.println("ERROR --> " + errorMessage);
        System.out.println();
        System.exit(-1);
    }

    /**
     * @brief mostra um erro quando a variavel/valor que esta a ser retornado nao
     *        corresponde ao mesmo da funcao
     * 
     * @param m metodo onde tem o erro
     * @param v varavel que esta a ser retornada
     */
    private void showReturnError(MethodDeclaration m, VariableDeclaration v) {
        String errorMessage = "Method " + m.getName() + " should return " + m.getType() + ", returning " + v.getType()
                + " instead on " + v.getName();
        showError(errorMessage);
    }

    /**
     * @brief escreve no ficheiro com os bytecodes a string s passada como parametro
     * 
     * @param s string a ser escrita no ficheiro
     */
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

    /**
     * @brief abre o ficheiro onde vao ser gerados os bytecodes
     * 
     * @param cname nome do ficheiro
     * @return true no caso de o ficheiro ser aberto corretamente e false no caso de
     *         erro
     */
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

    /**
     * @brief fecha o ficheir onde forem gerados os bytecodes
     */
    private void closeFile() {
        try {
            this.f.close();
        } catch (Exception e) {
        }
        return;
    }

    /**
     * @brief comforme o tipo passado como parametro retorna a assinatura a usar na
     *        geracao de bytecodes
     * 
     * @param returnType tipo que se pretende a assinatura
     * @return string com a respetiva assinatura
     * @throws Exception
     */
    private static String getSignature(String returnType) throws Exception {
        String ret;
        switch (returnType) {
        case "boolean":
            ret = "I";
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