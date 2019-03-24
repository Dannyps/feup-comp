JJT_FILE="teste.jjt"
JJ_FILE="teste.jj"
SYNTAX_ANALYZER_FILE="Fac"

# Run JJTree
jjtree $JJT_FILE || exit 1

# Generate java code
javacc $JJ_FILE || exit 2

# Compile generated java code
javac *.java || exit 3


# Run syntax analyzer
read -p "Press any key to continue..."
clear
java $SYNTAX_ANALYZER_FILE || exit 4
