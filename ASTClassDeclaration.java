/* Generated By:JJTree: Do not edit this line. ASTClassDeclaration.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTClassDeclaration extends SimpleNode {
  /**
   * @brief The class identifier
   */
  protected String className = "";
  /**
   * @brief The name of the extended class. If no class is extended, value is null
   */
  protected String extendsClassName = null;
  
  public ASTClassDeclaration(int id) {
    super(id);
  }

  public ASTClassDeclaration(Fac p, int id) {
    super(p, id);
  }
  
  @Override
  public String toString() {
    String str = "Class declaration : " + this.className;
    if(this.extendsClassName != null)
      str += " (extends " + this.extendsClassName + ")";

    return str;
  }
}
/* JavaCC - OriginalChecksum=063e85e045dff69edf08fad4ecc0e845 (do not edit this line) */
