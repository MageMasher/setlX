@ECHO off

set javaParameters=

REM uncomment the next line by removing 'REM' to execute with increased stack size
REM set javaParameters=%javaParameters% -Xss10m

IF NOT EXIST setlX.jar (
    IF EXIST src (
        echo "Building the setlX interpreter ..."
        mkdir bin
        javac -d bin -sourcepath src src\comparableSet\*.java
        javac -d bin -sourcepath src src\interpreter\exceptions\*.java
        javac -d bin -sourcepath src src\interpreter\types\*.java
        javac -d bin -sourcepath src src\interpreter\utilities\*.java
        javac -d bin -sourcepath src src\interpreter\functions\*.java
        javac -d bin -sourcepath src src\interpreter\boolExpressions\*.java
        javac -d bin -sourcepath src src\interpreter\expressions\*.java
        javac -d bin -sourcepath src src\interpreter\statements\*.java
        java  -cp antlr\antlr-*.jar org.antlr.Tool -fo src\grammar src\grammar\SetlXgrammar.g
        javac -cp antlr\antlr-*.jar -d bin -sourcepath src src\grammar\SetlXgrammar*.java
        javac -cp antlr\antlr-*.jar -d bin -sourcepath src src\SetlX.java
        cd bin
        jar xf ..\antlr\antlr-*.jar org/antlr/runtime
        cd ..
        jar cfe setlX.jar SetlX -C bin\ .
        rmdir /S /Q bin
        del /F /Q src\grammar\*.java
        del /F /Q src\grammar\*.tokens
        echo "The setlX interpreter was (hopefully correctly) build."
    ) ELSE (
        echo "No source available -- the setlX interpreter can not be build."
    )
)
IF EXIST setlX.jar (
    java %javaParameters% -jar setlX.jar %*
)
pause
@ECHO on