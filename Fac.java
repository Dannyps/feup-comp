/* Fac.java */
/* Generated By:JJTree&JavaCC: Do not edit this line. Fac.java */
import java.io.FileReader;
import java.io.FileNotFoundException;

public class Fac/*@bgen(jjtree)*/implements FacTreeConstants, FacConstants {/*@bgen(jjtree)*/
  protected static JJTFacState jjtree = new JJTFacState();public int computeFac(SimpleNode node) {
        return 0;
    }

    public static void main(String[] args) throws ParseException {

        try {
            FileReader fileReader = new FileReader("text.txt");
            Fac fac = new Fac(fileReader);
            SimpleNode root = fac.Program();

            root.dump("");

            //System.out.println("Valor da expressão: "+fac.computeFac(root));
            System.exit(0);

        } catch (FileNotFoundException e) {
            System.exit(0);
            //TODO: handle exception
        }
        //Fac fac = new Fac(System.in);
    }

    public static void showError(ParseException e, String errorMessage) {
        System.out.println(errorMessage);
    }

  static final public SimpleNode Program() throws ParseException {/*@bgen(jjtree) Program */
  ASTProgram jjtn000 = new ASTProgram(JJTPROGRAM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      ClassDeclaration();
      jj_consume_token(0);
jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
{if ("" != null) return jjtn000;}
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public void ClassDeclaration() throws ParseException {/*@bgen(jjtree) ClassDeclaration */
  ASTClassDeclaration jjtn000 = new ASTClassDeclaration(JJTCLASSDECLARATION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(CLASS);
      jj_consume_token(IDENTIFIER);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case EXTENDS:{
        jj_consume_token(EXTENDS);
        jj_consume_token(IDENTIFIER);
        break;
        }
      default:
        jj_la1[0] = jj_gen;
        ;
      }
      jj_consume_token(OPEN_CURLY_BRACKET);
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case INT:
        case BOOLEAN:
        case IDENTIFIER:{
          ;
          break;
          }
        default:
          jj_la1[1] = jj_gen;
          break label_1;
        }
        VarDeclaration();
      }
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case CLASSVISIBILITY:{
          ;
          break;
          }
        default:
          jj_la1[2] = jj_gen;
          break label_2;
        }
        jj_consume_token(CLASSVISIBILITY);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case STATIC:{
          MainDeclaration();
          break;
          }
        case INT:
        case BOOLEAN:
        case IDENTIFIER:{
          MethodDeclaration();
          break;
          }
        default:
          jj_la1[3] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      jj_consume_token(CLOSE_CURLY_BRACKET);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void VarDeclaration() throws ParseException {/*@bgen(jjtree) VarDeclaration */
                         ASTVarDeclaration jjtn000 = new ASTVarDeclaration(JJTVARDECLARATION);
                         boolean jjtc000 = true;
                         jjtree.openNodeScope(jjtn000);Token ident;
    try {
      Type();
      ident = jj_consume_token(IDENTIFIER);
      jj_consume_token(PVIRG);
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  static final public void MainDeclaration() throws ParseException {/*@bgen(jjtree) MainDeclaration */
  ASTMainDeclaration jjtn000 = new ASTMainDeclaration(JJTMAINDECLARATION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(STATIC);
      jj_consume_token(VOID);
      jj_consume_token(MAIN);
      jj_consume_token(OPEN_PARENTHESES);
      jj_consume_token(STRING);
      jj_consume_token(OPEN_BRACKET);
      jj_consume_token(CLOSE_BRACKET);
      jj_consume_token(IDENTIFIER);
      jj_consume_token(CLOSE_PARENTHESES);
      jj_consume_token(OPEN_CURLY_BRACKET);
      FunctionBody();
      jj_consume_token(CLOSE_CURLY_BRACKET);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void MethodDeclaration() throws ParseException {/*@bgen(jjtree) MethodDeclaration */
  ASTMethodDeclaration jjtn000 = new ASTMethodDeclaration(JJTMETHODDECLARATION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Type();
      jj_consume_token(IDENTIFIER);
      jj_consume_token(OPEN_PARENTHESES);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case INT:
      case BOOLEAN:
      case IDENTIFIER:{
        Type();
        jj_consume_token(IDENTIFIER);
        label_3:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case VIRG:{
            ;
            break;
            }
          default:
            jj_la1[4] = jj_gen;
            break label_3;
          }
          jj_consume_token(VIRG);
          Type();
          jj_consume_token(IDENTIFIER);
        }
        break;
        }
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      jj_consume_token(CLOSE_PARENTHESES);
      jj_consume_token(OPEN_CURLY_BRACKET);
      FunctionBody();
      jj_consume_token(RETURN);
      Expression();
      jj_consume_token(PVIRG);
      jj_consume_token(CLOSE_CURLY_BRACKET);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void FunctionBody() throws ParseException {
    label_4:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_4;
      }
      VarDeclaration();
    }
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case IF:
      case WHILE:
      case OPEN_CURLY_BRACKET:
      case OPEN_PARENTHESES:
      case NEW:
      case THIS:
      case TRUE:
      case FALSE:
      case NOT:
      case INTEGERLITERAL:
      case IDENTIFIER:{
        ;
        break;
        }
      default:
        jj_la1[6] = jj_gen;
        break label_5;
      }
      Statement();
    }
  }

  static final public void Type() throws ParseException {/*@bgen(jjtree) Type */
               ASTType jjtn000 = new ASTType(JJTTYPE);
               boolean jjtc000 = true;
               jjtree.openNodeScope(jjtn000);Token type;
    try {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case BOOLEAN:{
        type = jj_consume_token(BOOLEAN);
        break;
        }
      case INT:{
        type = jj_consume_token(INT);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case OPEN_BRACKET:{
          jj_consume_token(OPEN_BRACKET);
          jj_consume_token(CLOSE_BRACKET);
          break;
          }
        default:
          jj_la1[7] = jj_gen;
          ;
        }
        break;
        }
      case IDENTIFIER:{
        type = jj_consume_token(IDENTIFIER);
        break;
        }
      default:
        jj_la1[8] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void Statement() throws ParseException {/*@bgen(jjtree) Statement */
  ASTStatement jjtn000 = new ASTStatement(JJTSTATEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case OPEN_CURLY_BRACKET:{
        jj_consume_token(OPEN_CURLY_BRACKET);
        label_6:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case IF:
          case WHILE:
          case OPEN_CURLY_BRACKET:
          case OPEN_PARENTHESES:
          case NEW:
          case THIS:
          case TRUE:
          case FALSE:
          case NOT:
          case INTEGERLITERAL:
          case IDENTIFIER:{
            ;
            break;
            }
          default:
            jj_la1[9] = jj_gen;
            break label_6;
          }
          Statement();
        }
        jj_consume_token(CLOSE_CURLY_BRACKET);
        break;
        }
      case IF:{
        If();
        break;
        }
      case WHILE:{
        While();
        break;
        }
      case OPEN_PARENTHESES:
      case NEW:
      case THIS:
      case TRUE:
      case FALSE:
      case NOT:
      case INTEGERLITERAL:
      case IDENTIFIER:{
        Expression();
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case PVIRG:{
          jj_consume_token(PVIRG);
          break;
          }
        case EQUAL:{
          jj_consume_token(EQUAL);
          Expression();
          jj_consume_token(PVIRG);
          break;
          }
        default:
          jj_la1[10] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
        }
      default:
        jj_la1[11] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void If() throws ParseException {
    try {
      jj_consume_token(IF);
      jj_consume_token(OPEN_PARENTHESES);
      Expression();
      jj_consume_token(CLOSE_PARENTHESES);
      Statement();
      jj_consume_token(ELSE);
      Statement();
    } catch (ParseException e) {
showError(e, "if error");
        System.exit(0);
    }
  }

  static final public void While() throws ParseException {/*@bgen(jjtree) While */
  ASTWhile jjtn000 = new ASTWhile(JJTWHILE);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      try {
        jj_consume_token(WHILE);
        jj_consume_token(OPEN_PARENTHESES);
        WhileCondition();
        jj_consume_token(CLOSE_PARENTHESES);
        WhileBody();
      } catch (ParseException e) {
showError(e, "while error");
        System.exit(0);
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void WhileCondition() throws ParseException {/*@bgen(jjtree) WhileCondition */
  ASTWhileCondition jjtn000 = new ASTWhileCondition(JJTWHILECONDITION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Expression();
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  static final public void WhileBody() throws ParseException {/*@bgen(jjtree) WhileBody */
  ASTWhileBody jjtn000 = new ASTWhileBody(JJTWHILEBODY);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Statement();
    } catch (Throwable jjte000) {
if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  static final public void Term() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case INTEGERLITERAL:{
      jj_consume_token(INTEGERLITERAL);
      break;
      }
    case TRUE:{
      jj_consume_token(TRUE);
      break;
      }
    case FALSE:{
      jj_consume_token(FALSE);
      break;
      }
    case IDENTIFIER:{
      jj_consume_token(IDENTIFIER);
      break;
      }
    case THIS:{
      jj_consume_token(THIS);
      break;
      }
    case NEW:{
      jj_consume_token(NEW);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case INT:{
        jj_consume_token(INT);
        jj_consume_token(OPEN_BRACKET);
        Expression();
        jj_consume_token(CLOSE_BRACKET);
        break;
        }
      case IDENTIFIER:{
        jj_consume_token(IDENTIFIER);
        jj_consume_token(OPEN_PARENTHESES);
        jj_consume_token(CLOSE_PARENTHESES);
        break;
        }
      default:
        jj_la1[12] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
      }
    case NOT:{
      jj_consume_token(NOT);
      Term();
      break;
      }
    case OPEN_PARENTHESES:{
      jj_consume_token(OPEN_PARENTHESES);
      Expression();
      jj_consume_token(CLOSE_PARENTHESES);
      break;
      }
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void Expression() throws ParseException {/*@bgen(jjtree) Expression */
  ASTExpression jjtn000 = new ASTExpression(JJTEXPRESSION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Expression1();
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case OPEN_BRACKET:
      case AND:
      case DOT:{
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case AND:{
          jj_consume_token(AND);
          Expression();
          break;
          }
        case OPEN_BRACKET:{
          jj_consume_token(OPEN_BRACKET);
          Expression();
          jj_consume_token(CLOSE_BRACKET);
          break;
          }
        case DOT:{
          jj_consume_token(DOT);
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case LENGTH:{
            jj_consume_token(LENGTH);
            break;
            }
          case IDENTIFIER:{
            jj_consume_token(IDENTIFIER);
            jj_consume_token(OPEN_PARENTHESES);
            switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
            case OPEN_PARENTHESES:
            case NEW:
            case THIS:
            case TRUE:
            case FALSE:
            case NOT:
            case INTEGERLITERAL:
            case IDENTIFIER:{
              Expression();
              label_7:
              while (true) {
                switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
                case VIRG:{
                  ;
                  break;
                  }
                default:
                  jj_la1[14] = jj_gen;
                  break label_7;
                }
                jj_consume_token(VIRG);
                Expression();
              }
              break;
              }
            default:
              jj_la1[15] = jj_gen;
              ;
            }
            jj_consume_token(CLOSE_PARENTHESES);
            break;
            }
          default:
            jj_la1[16] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
          break;
          }
        default:
          jj_la1[17] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
        }
      default:
        jj_la1[18] = jj_gen;
        ;
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void Expression1() throws ParseException {
    Expression2();
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case LESS:{
        ;
        break;
        }
      default:
        jj_la1[19] = jj_gen;
        break label_8;
      }
      jj_consume_token(LESS);
      Expression2();
    }
  }

  static final public void Expression2() throws ParseException {
    Expression3();
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case SUM:
      case SUB:{
        ;
        break;
        }
      default:
        jj_la1[20] = jj_gen;
        break label_9;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case SUM:{
        jj_consume_token(SUM);
        break;
        }
      case SUB:{
        jj_consume_token(SUB);
        break;
        }
      default:
        jj_la1[21] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      Expression3();
    }
  }

  static final public void Expression3() throws ParseException {
    Term();
    label_10:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case MULT:
      case DIV:{
        ;
        break;
        }
      default:
        jj_la1[22] = jj_gen;
        break label_10;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case MULT:{
        jj_consume_token(MULT);
        break;
        }
      case DIV:{
        jj_consume_token(DIV);
        break;
        }
      default:
        jj_la1[23] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      Term();
    }
  }

  static private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_3R_13()
 {
    if (jj_scan_token(INT)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_14()) jj_scanpos = xsp;
    return false;
  }

  static private boolean jj_3R_12()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(16)) {
    jj_scanpos = xsp;
    if (jj_3R_13()) {
    jj_scanpos = xsp;
    if (jj_scan_token(42)) return true;
    }
    }
    return false;
  }

  static private boolean jj_3R_11()
 {
    if (jj_3R_12()) return true;
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3_1()
 {
    if (jj_3R_11()) return true;
    return false;
  }

  static private boolean jj_3R_14()
 {
    if (jj_scan_token(OPEN_BRACKET)) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public FacTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[24];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x0,0x18000,0x100,0x18040,0x40000,0x18000,0xc5002800,0x10000000,0x18000,0xc5002800,0x820000,0xc5002800,0x8000,0xc4000000,0x40000,0xc4000000,0x0,0x10000000,0x10000000,0x0,0x180000,0x180000,0x600000,0x600000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x8,0x400,0x0,0x400,0x0,0x400,0x613,0x0,0x400,0x613,0x0,0x613,0x400,0x613,0x0,0x613,0x404,0x120,0x120,0x40,0x0,0x0,0x0,0x0,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[1];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Fac(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Fac(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new FacTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Fac(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new FacTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Fac(FacTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(FacTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error { }
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk_f() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[43];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 24; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 43; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

  static private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
