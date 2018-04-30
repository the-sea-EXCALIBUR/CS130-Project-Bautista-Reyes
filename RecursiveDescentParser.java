import java.io.*;
import java.util.*;

public class RecursiveDescentParser 
{
	
	public static final Map<String, String> tokenMap = createTokenMap();
	public static ArrayList<String> tokenString = new ArrayList<String>();
	public static ArrayList<String> lexemeString = new ArrayList<String>();
	public static ArrayList<Token> tokens = new ArrayList<Token>();

	public static Map<String, String> createTokenMap()
    {
        Map<String,String> tokenMap = new HashMap<String,String>();
        tokenMap.put("PLUS", "OP");
        tokenMap.put("MINUS", "OP");
        tokenMap.put("MULT", "OP");
        tokenMap.put("DIVIDE", "OP");
        tokenMap.put("MODULO", "OP");
        tokenMap.put("EXP", "OP");
        tokenMap.put("LPAREN", "LPAREN");
        tokenMap.put("RPAREN", "RPAREN");
        tokenMap.put("EQUALS", "EQUALS");
		tokenMap.put("LTHAN", "LTHAN");
		tokenMap.put("GTHAN", "GTHAN");
        tokenMap.put("COLON", "COLON");
        tokenMap.put("COMMA", "COMMA");
        tokenMap.put("PERIOD", "PERIOD");
        tokenMap.put("QUOTE", "QUOTE");
        tokenMap.put("DQUOTE", "DQUOTE");
        tokenMap.put("LBRACK", "DQUOTE");
        tokenMap.put("RBRACK", "DQUOTE");
		tokenMap.put("SCOLON", "SCOLON");
        tokenMap.put("ENDTAGHEAD", "ENDTAGHEAD");
        tokenMap.put("IDENT", "IDENT");
        tokenMap.put("TAGIDENT", "TAGIDENT");
		tokenMap.put("NUMBER", "NUMBER");
		return tokenMap;
	}

	public static void getTokensLexemes(String all)
	{
		String currString = "";
	    for(int i = 0; i < all.length(); i++)
	    {
	    	char currentChar = all.charAt(i);
	    	if(Character.isWhitespace(currentChar))
	    	{
	    		if(tokenMap.get(currString) != null)
	    		{
	    			tokenString.add(currString);
	    			currString = "";
	    		}
	    		else
	    		{
	    			if(currString.trim().length() > 0)
	    			{
	    				lexemeString.add(currString);
	    				currString = "";
	    			}
	    		}
	    	}
	    	else
	    		currString += currentChar;
	    }
	    
	    if(currString.trim().length() > 0)
		{
			lexemeString.add(currString.trim());
			currString = "";
		}
	    
	    
	    for(int x = 0; x < tokenString.size(); x++)
	    {
	    	tokens.add(new Token(tokenString.get(x), lexemeString.get(x), "waiting"));
	    }
	    
	}

	public static void main(String args[]) throws Exception
	{
		File inFile = null;
		String all = "";
	            
	    BufferedReader br = null;
	    
	    try 
        {

            String sCurrentLine;

            //br = new BufferedReader(new FileReader(args[0]));
            br = new BufferedReader(new FileReader("src/output.txt"));
            
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

	    getTokensLexemes(all);
	    
	    PrintWriter writer = new PrintWriter("src/outputzz.csv", "UTF-8");
		//PrintWriter writer = new PrintWriter(args[1], "UTF-8");
    	Parser xd = new Parser(tokens, writer);
    	xd.table();

    	xd.writer.close();

	}
}
