if [[ $# -ne 1 ]] ; then
	echo "You must specify an input jmm file"
	exit 1
fi

JJT_FILE="teste.jjt"
JJ_FILE="teste.jj"
SYNTAX_ANALYZER_FILE="Fac"

# Run JJTree
jjtree $JJT_FILE || exit 1

# Generate java code
cd ast
javacc $JJ_FILE || read 

# Compile generated java code
javac *.java || read


# Run syntax analyzer
read -p "Press any key to continue..."
clear
java $SYNTAX_ANALYZER_FILE ../$1 || read
