import java.io.*;
import java.util.*;

public class HTMLexicalAnalyzer {
        
        public static int currentState = 0;
        /*
        STATE TRANSITION TABLE
        letters use element 0 in the arrays to access next state,
        numbers use element 1, 
        white spaces use element 2,
        < 3,
        ! 4,
        - 5,
        / 6, 
        > 7, 
        * 8, 
        . 9, 
        e 10, 
        special 11, 
        invalid 12
        */
        public static int nextState[][] = 
        {
            { 11, 12, 0, 1, 15, 13, 13, 13, 21, 13, 11, 13, 15}, //q0
            { 10, 12, 0, 1, 2, 13, 8, 13, 21, 13, 10, 13, 15}, //q1
            { 11, 12, 0, 1, 15, 3, 13, 13, 21, 13, 11, 13, 15}, //q2
            { 11, 12, 0, 1, 15, 4, 13, 13, 21, 13, 11, 13, 15}, //q3
            { 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4}, //q4
            { 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4}, //q5
            { 4, 4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4}, //q6
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15}, //q7
            { 11, 12, 0, 1, 15, 13, 13, 13, 21, 13, 11, 13, 15}, //q8
            { 11, 12, 0, 1, 15, 13, 13, 13, 13, 13, 11, 13, 15}, //q9 unused state
            { 10, 12, 0, 1, 15, 13, 13, 13, 21, 13, 10, 13, 15}, //q10
            { 11, 18, 0, 1, 15, 13, 13, 13, 21, 13, 11, 13, 15}, //q11
            { 11, 12, 0, 13, 15, 13, 13, 13, 21, 14, 23, 13, 16}, //q12 6
            { 11, 12, 0, 1, 15, 13, 13, 13, 21, 13, 11, 13, 15}, //q13
            { 16, 20, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16}, //q14 6.
            { 11, 12, 0, 13, 15, 13, 13, 13, 21, 13, 11, 13, 13}, //q15
            { 16, 12, 0, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16}, //q16
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //q17
            { 11, 18, 0, 16, 16, 16, 16, 16, 16, 19, 23, 16, 16 }, //q18 w6 number before string
            { 11, 19, 16, 16, 16, 16, 16, 16, 16, 16, 11, 16, 16 }, //q19 w6.
            { 11, 20, 0, 13, 15, 13, 13, 13, 21, 13, 26, 13, 15}, //q20 6.5 no string before number
            { 11, 12, 0, 1, 15, 13, 13, 13, 22, 13, 11, 13, 15}, //q21 mult
            { 11, 12, 0, 1, 15, 13, 13, 13, 21, 13, 11, 13, 15}, //q22 exp
            { 16, 24, 0, 1, 15, 25, 13, 13, 21, 13, 16, 13, 15}, //q23 1e
            { 11, 24, 0, 1, 15, 13, 13, 13, 21, 13, 11, 13, 15}, //q24 1e5
            { 11, 24, 0, 1, 15, 13, 13, 13, 21, 13, 11, 13, 15}, //q25 1e-5
            { 27, 24, 0, 27, 27, 25, 27, 27, 27, 27, 27, 27, 27}, //q26 1.5e no string before
            { 27, 12, 0, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27}, //q27 1.5ex invalid

        };
        
        public static final Map<String, Integer> stateMap = createStateMap();
        public static final Map<String, String> charMap = createCharMap();
        public static final Map<String, String> validMap = createValidMap();
        
        public static Map<String, String> createCharMap()
        {
            Map<String,String> charMap = new HashMap<String,String>();
            charMap.put("+", "PLUS");
            charMap.put("-", "MINUS");
            charMap.put("*", "MULT");
            charMap.put("/", "DIVIDE");
            charMap.put("%", "MODULO");
            charMap.put("**", "EXP");
            charMap.put("(", "LPAREN");
            charMap.put(")", "RPAREN");
            charMap.put("=", "EQUALS");
            charMap.put("<", "LTHAN");
            charMap.put(">", "GTHAN");
            charMap.put(":", "COLON");
            charMap.put(",", "COMMA");
            charMap.put(".", "PERIOD");
            charMap.put("'", "QUOTE");
            charMap.put("\"", "DQUOTE");
            charMap.put(";", "SCOLON");
            charMap.put("</", "ENDTAGHEAD");
            charMap.put("string", "IDENT");
            charMap.put("number", "NUMBER");
            return charMap;
        }
        
        //VALID MAP checks if the current character is valid
        public static Map<String, String> createValidMap()
        {
            Map<String,String> validMap = new HashMap<String,String>();
            validMap.put("+", "special");
            validMap.put("-", "-");
            validMap.put("*", "*");
            validMap.put("/", "/");
            validMap.put("%", "special");
            validMap.put("**", "special");
            validMap.put("(", "special");
            validMap.put(")", "special");
            validMap.put("=", "special");
            validMap.put("<", "<");
            validMap.put(">", ">");
            validMap.put("!", "!");
            validMap.put(":", "special");
            validMap.put(",", "special");
            validMap.put(".", ".");
            validMap.put("'", "special");
            validMap.put("\"", "special");
            validMap.put(";", "special");
            validMap.put("e", "e");
            return validMap;
        }
        
        public static void printTokens(ArrayList<String> tokens, PrintWriter output)
        {
            for(int i = 0; i < tokens.size(); i++)
            {
                output.println(tokens.get(i));
                System.out.println(tokens.get(i));
            }
            tokens.clear();
        }
        
        //STATE MAP checks the current character to determine what kind of character it is
        public static Map<String, Integer> createStateMap()
        {
            Map<String,Integer> stateMap = new HashMap<String,Integer>();
            stateMap.put("letter", 0);
            stateMap.put("number", 1);
            stateMap.put("white", 2);
            stateMap.put("<", 3);
            stateMap.put("!", 4);
            stateMap.put("-", 5);
            stateMap.put("/", 6);
            stateMap.put(">", 7);
            stateMap.put("*", 8);
            stateMap.put(".", 9);
            stateMap.put("e", 10);
            stateMap.put("special", 11);
            stateMap.put("invalid", 12);
            return stateMap;
        }

        public static void LexicalAnalyzer(Character chara, ArrayList<String> tokens, PrintWriter output)
        {
            int prevState = currentState;
            String currentChar = Character.toString(chara);
            String charState = "";
            if(validMap.get(currentChar) != null)
                charState = validMap.get(currentChar);
            else if(Character.isDigit(chara))
                charState = "number";
            else if(Character.isLetter(chara))
                charState = "letter";
            else if(Character.isWhitespace(chara))
                charState = "white";
            else 
                charState = "invalid";
            
            currentState = nextState[currentState][stateMap.get(charState)];
            //System.out.println(currentChar +" "+ charState + " " + currentState);

            switch(currentState)
            {
                case 0:
                    //parsing state
                    if(prevState != 0) //time to parse
                    {
                       printTokens(tokens, output);
                    }
                    break;
                case 1:
                    printTokens(tokens, output);
                    tokens.add(charMap.get(currentChar) + "      " + currentChar);
                    break;
                case 2:
                    tokens.add("***lexical error: illegal character ("+ currentChar + ")");
                    break;
                case 3:
                    tokens.add(charMap.get(currentChar) + "      " + currentChar);
                    break;
                case 4:
                    tokens.clear();
                    break;
                case 5:
                    tokens.add(charMap.get(currentChar) + "      " + currentChar);
                    break;
                case 6:
                    tokens.add(charMap.get(currentChar) + "      " + currentChar);
                    tokens.clear();
                    break;
                case 17:
                    tokens.clear();
                    break;
                case 8:
                    tokens.remove(tokens.size() - 1);
                    tokens.add("ENDTAGHEAD </");
                    break;
                case 10:
                    if(prevState != 10)
                    {
                        tokens.remove(tokens.size() - 1);
                        tokens.add("TAGIDENT   <" + currentChar);
                    }
                    else
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    break;
                case 11:
                    if(prevState == 11)
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    else //parsing state
                    {
                        printTokens(tokens, output);
                        tokens.add("IDENT      " + currentChar);
                    }
                    break;
                case 12:
                    if(prevState == 12)
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    else
                    {
                        printTokens(tokens, output);
                        //System.out.println("Hello world!");
                        tokens.add("NUMBER     " + currentChar);
                    }
                    break;
                case 14:
                    if(prevState == 12)
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                case 20:
                    if(prevState == 14 || prevState == 20)
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    break;
                case 15:
                    printTokens(tokens, output);
                    tokens.add("***lexical error: illegal character ("+ currentChar + ")");
                    break;

                case 13:
                    printTokens(tokens, output);
                    tokens.add(charMap.get(currentChar) + "      " + currentChar);
                    break;
                case 16:
                    if(prevState != 16)
                    {
                        String error = "***lexical error: badly formed number";
                        tokens.add(error);
                        //System.out.println("yo " + tokens.get(tokens.size() - 1));
                        tokens.set(tokens.size() - 1, tokens.get(tokens.size() - 2));
                        tokens.set(tokens.size() - 2, error);
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);

                    }
                    else 
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    break;
                case 18:
                    if(prevState == 18)
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    else
                    {
                        printTokens(tokens, output);
                        //System.out.println("Hello world!");
                        tokens.add("NUMBER     " + currentChar);
                    }
                    break;
                case 19:
                    if(prevState == 18 || prevState == 19)
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    break;
                case 21:
                    printTokens(tokens,output);
                    tokens.add("MULT       " + currentChar);
                    break;
                case 22:
                    tokens.remove(tokens.size() - 1);
                    tokens.add("EXP        **");
                    break;
                case 23:
                    tokens.add("IDENT      " + currentChar);
                    break;
                case 24:
                    if(prevState == 25)
                    {
                        tokens.remove(tokens.size() - 1);
                        tokens.remove(tokens.size() - 1);
                        String newToken = tokens.get(tokens.size() - 1) + "e-" + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    else if(prevState == 24)
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    else
                    {
                        
                        tokens.remove(tokens.size() - 1);
                        String newToken = tokens.get(tokens.size() - 1) + "e" + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    break;
                case 25:
                    tokens.add(charMap.get(currentChar) + "      " + currentChar);
                    break;
                case 26:
                    tokens.add("IDENT      " + currentChar);
                    break;
                case 27:
                    if(prevState != 27)
                    {
                        tokens.remove(tokens.size() - 1);
                        tokens.set(tokens.size() - 1, tokens.get(tokens.size() - 1) + "e");
                        String error = "***lexical error: badly formed number";
                        tokens.add(error);
                        //System.out.println("yo " + tokens.get(tokens.size() - 1));
                        tokens.set(tokens.size() - 1, tokens.get(tokens.size() - 2));
                        tokens.set(tokens.size() - 2, error);
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);

                    }
                    else 
                    {
                        String newToken = tokens.get(tokens.size() - 1) + currentChar;
                        tokens.set(tokens.size() - 1, newToken);
                    }
                    break;
                    
            }
        }
    
        public static void main(String[] args) throws Exception
        {
            File inFile = null;
            //all is where every character is placed after reading the file
            String all = "";
            
            /*if (0 < args.length) 
            {
               inFile = new File(args[0]);
            } 
            else 
            {
               System.err.println("No text file found: " + args.length);
               System.exit(0);
            }*/
            BufferedReader br = null;
            
            try 
            {

                String sCurrentLine;

                br = new BufferedReader(new FileReader(args[0]));
                //br = new BufferedReader(new FileReader("src/manycases.txt"));
                
                while ((sCurrentLine = br.readLine()) != null) {
                    //all is concatenated with the elements of splt
                    all+=" ";
                    all+=sCurrentLine;
                }

            } 

            catch (IOException e) 
            {
                e.printStackTrace();
            } 

            finally
            {
                try 
                {
                    if (br != null)br.close();
                } 
                catch (IOException ex) 
                {
                    ex.printStackTrace();
                }
            }

            ArrayList<String> tokens = new ArrayList<>();
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            for(int i = 0; i < all.length(); i++)
            {
                LexicalAnalyzer(all.charAt(i), tokens, writer);
                //for(int x = 0; x < tokens.size(); x++)
                //    System.out.println(tokens.get(x));
            }
            if(!tokens.isEmpty())
            {
                printTokens(tokens, writer);
            }
            System.out.println(currentState);
            if(currentState == 4)
            {
            	System.out.println("***lexical error: un-expected end of file");
            	writer.println("***lexical error: un-expected end of file");
            }
            	
            writer.close();
            
            //System.out.println(all);
        }
}
