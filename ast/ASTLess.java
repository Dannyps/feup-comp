/* Generated By:JJTree: Do not edit this line. ASTLess.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

package ast;

public class ASTLess extends SimpleNode {
  public ASTLess(int id) {
    super(id);
    this.op = MyConstants.LESS;
  }

  public ASTLess(Fac p, int id) {
    super(p, id);
  }
  @Override
  public String toString() {
  	return "<";
  }
}
/* JavaCC - OriginalChecksum=1598e0e05e3e4b33a3987d58d9db5d5d (do not edit this line) */
