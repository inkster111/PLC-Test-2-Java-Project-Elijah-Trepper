package com.elitrepper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    //initialize global starting position
    private static int filePosition;
    //initialize numbers, letters, and alphanumeric alphabet
    private static Character[] intArray = new Character[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    private static Character[] alphabetArray = new Character[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static Character[] alphanumericArray = new Character[]{
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_'};
    private static List<Character> intList = Arrays.asList(intArray);
    private static List<Character> alphabetList = Arrays.asList(alphabetArray);
    private static List<Character> alphanumericList = Arrays.asList(alphanumericArray);


    public static void main(String[] args) throws Exception {
        //start position at 0
        filePosition = 0;
        //convert text file to string
        String fileContent = readFile("./src/com/elitrepper/text.txt", StandardCharsets.US_ASCII);
        //print file turned into lexeme tokens
        System.out.println(separateLexemes(fileContent));
    }

    //convert text file to string
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    //separate string into lexeme tokens and error if invalid
    public static String separateLexemes(String initialString) throws Exception {
        String tokenString = "";
        //loop through string
        while (filePosition < initialString.length()) {
            //test for java string
            if (initialString.charAt(filePosition) == '"') {
                filePosition += 1;
                StringTest(initialString);
                tokenString += TokenType.JavaString + " ";
            //test for character
            } else if (initialString.charAt(filePosition) == '\'') {
                filePosition += 1;
                CharTest(initialString);
                tokenString += TokenType.CChar + " ";
            //test for identifiers
            } else if (initialString.charAt(filePosition) == '$'
                    || initialString.charAt(filePosition) == '@'
                    || initialString.charAt(filePosition) == '#') {
                TokenType identifierType = IdentifierTest(initialString);
                tokenString += identifierType + " ";
            //test for addition and increment
            } else if (initialString.charAt(filePosition) == '+') {
                filePosition += 1;
                //test for increment to decide whether its one +(add) or two ++(inc)
                if (initialString.charAt(filePosition) == '+') {
                    filePosition += 1;
                    tokenString += TokenType.Inc + " ";
                } else {
                    tokenString += TokenType.Add + " ";
                }
            //test for subtraction and decrement
            } else if (initialString.charAt(filePosition) == '-') {
                filePosition += 1;
                //test for increment to decide whether its one -(sub) or two --(dec)
                if (initialString.charAt(filePosition) == '-') {
                    filePosition += 1;
                    tokenString += TokenType.Dec + " ";
                } else {
                    tokenString += TokenType.Sub + " ";
                }
            //test for and and or, error if there is a ^ without finishing the ^&& or ^||
            } else if (initialString.charAt(filePosition) == '^') {
                filePosition += 1;
                //test for and
                if (initialString.charAt(filePosition) == '&') {
                    filePosition += 1;
                    if (initialString.charAt(filePosition) == '&') {
                        filePosition += 1;
                        tokenString += TokenType.And + " ";
                    } else {
                        throw new Exception("Lexical Error");
                    }
                //test for or
                } else if (initialString.charAt(filePosition) == '|') {
                    filePosition += 1;
                    if (initialString.charAt(filePosition) == '|') {
                        filePosition += 1;
                        tokenString += TokenType.Or + " ";
                    } else {
                        throw new Exception("Lexical Error");
                    }
                } else {
                    throw new Exception("Lexical Error");
                }
            //test for assignment
            } else if (initialString.charAt(filePosition) == '=') {
                filePosition += 1;
                tokenString += TokenType.Assign + " ";
            //test for division
            } else if (initialString.charAt(filePosition) == '/') {
                filePosition += 1;
                tokenString += TokenType.Div + " ";
            //test for multiplication
            } else if (initialString.charAt(filePosition) == '*') {
                filePosition += 1;
                tokenString += TokenType.Mult + " ";
            // test for modulus
            } else if (initialString.charAt(filePosition) == '%') {
                filePosition += 1;
                tokenString += TokenType.Mod + " ";
            //test for not
            } else if (initialString.charAt(filePosition) == '!') {
                filePosition += 1;
                tokenString += TokenType.Not + " ";
            //test for open code block
            } else if (initialString.charAt(filePosition) == '{') {
                filePosition += 1;
                tokenString += TokenType.OpenBlock + " ";
            //test for close code block
            } else if (initialString.charAt(filePosition) == '}') {
                filePosition += 1;
                tokenString += TokenType.CloseBlock + " ";
            //test for open and closed function parameter
            } else if (initialString.charAt(filePosition) == '(') {
                filePosition += 1;
                //confirm function parameter closes
                FuncTest(initialString);
                tokenString += TokenType.OpenFunc + " " + TokenType.CloseFunc + " ";
            //test for int and floating point
            } else if (intList.contains(initialString.charAt(filePosition))) {
                filePosition += 1;
                TokenType numberType = IntTest(initialString);
                tokenString += numberType + " ";
            //test for blank spaces
            } else if (initialString.charAt(filePosition) == ' '
                    || initialString.charAt(filePosition) == '\n'
                    || initialString.charAt(filePosition) == '\r') { //i found that '\r' would show up for new lines from text files hence why it is in the blank spaces category
                filePosition += 1;
            //error if all tests failed
            } else {
                throw new Exception("Lexical Error");
            }
        }
        //return the full string of tokens if the end of file is reached without no errors
        return tokenString;
    }

    //test that the string closes
    public static void StringTest(String initialString) throws Exception {
        //loop through till you get to an end quote or the file ends
        while (filePosition < initialString.length()) {
            if (initialString.charAt(filePosition) == '"') {
                break;
            }
            filePosition += 1;
        }
        //if you get to the end quote continue
        if (initialString.charAt(filePosition) == '"') {
            filePosition += 1;
            return;
        //if the file ends error
        } else {
            throw new Exception("Lexical Error");
        }
    }

    //test that the char only has 1 or 0 characters and closes
    public static void CharTest(String initialString) throws Exception {
        //if there is no characters between '' then continue
        if (initialString.charAt(filePosition) == '\'') {
            return;
        }
        filePosition += 1;
        //if there is 1 character between '' then continue
        if (initialString.charAt(filePosition) == '\'') {
            filePosition += 1;
            return;
        //all else error
        } else {
            throw new Exception("Lexical Error");
        }
    }

    //test which identifier is used and continue till the identifier ends
    public static TokenType IdentifierTest(String initialString) throws Exception {
        //test for Scalar identifier
        if (initialString.charAt(filePosition) == '$') {
            filePosition += 1;
            while (alphanumericList.contains(initialString.charAt(filePosition))) {
                filePosition += 1;
            }
            return TokenType.PerlScalar;
        }
        //test for array identifier
        if (initialString.charAt(filePosition) == '@') {
            filePosition += 1;
            while (alphanumericList.contains(initialString.charAt(filePosition))) {
                filePosition += 1;
            }
            return TokenType.PerlArray;
        }
        //test for hash identifier
        if (initialString.charAt(filePosition) == '#') {
            filePosition += 1;
            while (alphanumericList.contains(initialString.charAt(filePosition))) {
                filePosition += 1;
            }
            return TokenType.PerlHash;
        }
        //redundant throw error due to function needing to end if all if statements fail
        throw new Exception("Lexical Error");
    }

    //test that the function parameter closes
    public static void FuncTest(String initialString) throws Exception {
        //loop through until you get to the close symbol or the end of the file
        while (filePosition < initialString.length()) {
            if (initialString.charAt(filePosition) == ')') {
                break;
            }
            filePosition += 1;
        }
        //if you get to the end symbol continue
        if (initialString.charAt(filePosition) == ')') {
            filePosition += 1;
            return;
        //if you get to the end of the file error
        } else {
            throw new Exception("Lexical Error");
        }
    }

    //test for integer and floating point
    public static TokenType IntTest(String initialString) {
        //loop to where the initial integer ends
        while (intList.contains(initialString.charAt(filePosition))) {
            filePosition += 1;
        }
        //if the integer is followed by a . then continue and return as a floating point
        if (initialString.charAt(filePosition) == '.') {
            filePosition += 1;
            while (intList.contains(initialString.charAt(filePosition))) {
                filePosition += 1;
            }
            return TokenType.CFloat;
        //else return as an integer
        } else {
            return TokenType.CInt;
        }
    }

    //create token types
    private static enum TokenType {
        PerlScalar, PerlArray, PerlHash, JavaString, CInt, CChar, CFloat, Add, Assign, Sub, Div, Mult, Inc, Dec, Mod, And, Or, Not, OpenBlock, CloseBlock, OpenFunc, CloseFunc
    }

    /*
    LEXEME          TOKEN
    $               PerlScalar
    @               PerlArray
    #               PerlHash
    " "             JavaString
    int             CInt
    ' '             CChar
    int.int         CFloat
    +               Add
    =               Assign
    -               Sub
    /               Div
    *               Mult
    ++              Inc
    --              Dec
    %               Mod
    ^&&             And
    ^||             Or
    !               Not
    {               OpenBlock
    }               CloseBlock
    (               OpenFunc
    )               CloseFunc
     */

}
