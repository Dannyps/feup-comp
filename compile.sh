if [[ $# -ne 1 ]] ; then
	echo "You must specify an input jmm file"
	exit 1
fi

JJT_FILE="teste.jjt"
JJ_FILE="teste.jj"
SYNTAX_ANALYZER_FILE="Fac"

cd ast

# Run JJTree
jjtree $JJT_FILE || exit 1

# Generate java code
javacc $JJ_FILE || read

# Go back to parent directory
cd ..

# Create folder for storing compiled java files (.class)
mkdir -p bin

# Compile generated java code
javac -d bin/ ast/*.java || read
javac -d bin/ symboltable/*.java || read

read -p "Press any key to continue..."
clear
java -cp bin/ ast.$SYNTAX_ANALYZER_FILE $1