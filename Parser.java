import java.io.*;
import java.util.*;

public class Parser
{


	Parser(ArrayList<Token>tokenz, PrintWriter writerz)
	{
		tokens = tokenz;
		token = tokens.get(0);
		writer = writerz;
	}




	/** CFG USED:


		BLOCK -> {tagident, gthan, BLOCK, ENDTAG} | LINE
		ENDTAG -> endtaghead, ident, gthan
		LINE -> equals, EXPRESSION | {(ident | number | symbol)}
		EXPRESSION -> TERM {(plus | minus) TERM} 
		TERM -> FACTOR {(multiply | divide) FACTOR} //
		FACTOR -> number | lparen, EXPRESSION, rparen 

		Legend:
		 ,  -> concatenate: i.e. "a", "b" = "ab"
		 |  -> separator
		{ } -> 0 or more times: i.e. {x} = x*
		( ) -> pick one: i.e (a | b | c) = a
		symbol -> any lexeme that's not a IDENT, NUMBER, EQUALS, "[", "]"

		Each function, (block(), endTag(), etc) is a terminal.

	**/

	public static final Map<String, String> tokenMap = createTokenMap();
	public static final Map<String, String> validMap = createValidMap();

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
        validMap.put("[", "special");
        validMap.put("]", "special");
        validMap.put("e", "e");
        return validMap;
    }

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
		tokenMap.put("SCOLON", "SCOLON");
        tokenMap.put("ENDTAGHEAD", "ENDTAGHEAD");
        tokenMap.put("IDENT", "IDENT");
		tokenMap.put("NUMBER", "NUMBER");
		return tokenMap;
	}

	public static ArrayList<Token> tokens;
	public static int pointer = 0;
	public static Token token;
	public static String currentLine = "";
	public static PrintWriter writer;

	public static boolean match(String s)
	{
		if(token.type.equals(s))
		{
			//writer.println("Current token: " + (pointer + 1) + ", " + token.type + ", " + token.lexeme);
			pointer++;
			try
			{
				token = tokens.get(pointer);	
			}
			catch(Exception e)
			{
				currentLine += ("\nEND OF FILE.");
			}
			
			return true;
		}
		else
			return false; 
	}

	public static void parseString(String s)
	{
		for(int i = currentLine.length() - 1; i > 0; i--)
		{
			int completed = 0;
			for(int x = 0; x < s.length() - 1; x++)
			{
				if(currentLine.charAt(i + x) == "s".charAt(x))
				{
					completed++;
				}
			}
			if(completed == s.length())
			{
				String tempLine = currentLine.substring(i+s.length(), currentLine.length() - 1);
				currentLine = currentLine.substring(0, i - 1) + tempLine;
			}
		}
	}

	public static boolean expect(String s, int expectation)
	{
		return tokens.get(pointer+ expectation).type.equals(s);
	}

	public static void table()
	{
		if(token.type.equals("TAGIDENT") && token.lexeme.equals("<table"))
		{
			match("TAGIDENT");
			if(match("GTHAN"))
			{
				block();
				if(token.type.equals("ENDTAGHEAD") && expect("IDENT", 1))
				{
					currentLine += "\nEND OF FILE.";
				}
				else
				{
					writer.println("Error, invalid table!");
					System.out.println("Error, invalid table!");
					System.exit(0);
				}	
			}
			else
			{
				writer.println("Error, unclosed tag!");
				System.out.println("Error, unclosed tag!");
				System.exit(0);
			}
		}
	}

	public static void block()
	{
		if(token.type.equals("TAGIDENT"))
		{
			while(token.type.equals("TAGIDENT"))
			{
				Token tempToken = token; //<th <tr
				boolean matched = match("TAGIDENT"); 
				if(match("GTHAN"))
				{
					block();
					endTag(tempToken);
				}
				else
				{
					System.out.println("Error, unclosed tag!");
					writer.println("Error, unclosed tag!");
					System.exit(0);
				}
			}
		}
		else
			line();	
	}

	public static void endTag(Token s)
	{
		Token tempToken = tokens.get(pointer + 1);
		if(token.type.equals("ENDTAGHEAD")) 
		{
			match("ENDTAGHEAD");
			if(("<" + token.lexeme).equals(s.lexeme))
			{
				String tempLexeme = s.lexeme;
				if(match("IDENT"))
				{
					if(match("GTHAN"))
					{
						if(tempLexeme.equals("<tr"))
						{
							writer.println(currentLine);
							System.out.println(currentLine);
							currentLine="";
						}
					}
				}
			}
		}
		else
		{
			writer.println("Error, unclosed endtag!");
			System.exit(0);
		}
	}

	public static void line()
	{
		
		if(match("EQUALS"))
		{
			if(currentLine.length() > 0)
				currentLine += "," + expression();
			else
				currentLine += expression();
		}

		if(match("LBRACK"))
		{
			currentLine += "[";
			while(token.type.equals("PLUS") || token.type.equals("MINUS") || token.type.equals("MULT") || token.type.equals("DIVIDE") || token.type.equals("EXP") || token.type.equals("NUMBER"))
			{
				currentLine += token.lexeme;
				if(match("PLUS"))
				{}
				else if(match("MINUS"))	
				{}
				else if(match("MULT"))
				{}
				else if(match("DIVIDE"))
				{}
				else if(match("EXP"))
				{}
				else if(match("NUMBER"))
				{}
			}
			if(match("RBRACK"))
			{
				currentLine += "]";
			}
		}

		while((token.type.equals("IDENT")) || (token.type.equals("NUMBER")) || validMap.get(token.lexeme) != null)
		{
			if(currentLine.length() > 0)
				currentLine += "," + token.lexeme;
			else
				currentLine += token.lexeme;

			if(match("IDENT"))
			{}
			else if(match("NUMBER"))
			{}
			else if(match(token.type))
			{}
			else if(token.type.equals("EQUALS"))
				{line();}
			else if(token.type.equals("LBRACK"))
				{line();}
		}
		
	}

	public static String checkExp(String s)
	{
		boolean exponent = false;
		for(int i = 0; i < s.length() - 1; i++)
		{
			if(s.charAt(i) == "e".charAt(0))
				exponent = true;
		}
		String firstNum = "";
		String secondNum = "";
		String ss = s;
		boolean first = true;
		if(exponent)
		{
			try
			{
				s.replace("e", "-");	
			}
			catch(Exception e)
			{}
			try
			{
				s.replace("-", "-");	
			}
			catch(Exception e)
			{}

			for(int i = 0; i< s.length() - 1; i++)
			{
				if(s.charAt(i) != "-".charAt(0) && first)
				{
					firstNum += Character.toString(s.charAt(i));
				}
				else if(s.charAt(i) != "-".charAt(0) && !first)
					secondNum += Character.toString(s.charAt(i));
			}

			double num = Math.pow(Double.parseDouble(firstNum), Double.parseDouble(secondNum));
			ss = Double.toString(num);
		}
			return ss;
	}

	// = 10 + 10 + 5 * 10 * 10 + 3

	public static String expression()
	{
		String s = "";
		if(expect("PLUS", 1) || expect("MINUS", 1))
		{
			Token tempToken = token; // 520 + 3
			if(match("NUMBER"))
			{
				if(match("PLUS"))
				{
					s = Double.toString(Double.parseDouble(tempToken.lexeme) + Double.parseDouble(expression()));
				}
				else if(match("MINUS"))
				{
					s = Double.toString(Double.parseDouble(tempToken.lexeme) - Double.parseDouble(expression()));
				}
			}	
		}
		else if(token.type.equals("NUMBER"))
		{
			s = term();

			if(match("PLUS"))
			{
				s = Double.toString(Double.parseDouble(s) + Double.parseDouble(expression()));
			}
			else if(match("MINUS"))
			{
				s = Double.toString(Double.parseDouble(s) - Double.parseDouble(expression()));
			}
		}
		else
		{
			writer.println("Error, invalid expression!");
			System.exit(0);
		}

		return s;
		
	}

	public static String term()
	{

		String s = "";
		if(token.type.equals("NUMBER"))
		{
			Token tempToken = token;
			if(expect("MULT", 1))
			{
				if(match("NUMBER"))
				{
					if(match("MULT"))
					{
						s = Double.toString(Double.parseDouble(tempToken.lexeme) * Double.parseDouble(term()));
					}
				}
			}
			else if(expect("DIVIDE", 1))
			{
				if(match("NUMBER"))
				{
					if(match("DIVIDE"))
					{
						s = Double.toString(Double.parseDouble(tempToken.lexeme) / Double.parseDouble(term()));
					}
				}
			}
			else if(expect("EXP", 1))
			{
				if(match("NUMBER"))
				{
					if(match("EXP"))
					{
						s = Double.toString(Math.pow(Double.parseDouble(tempToken.lexeme), Double.parseDouble(factor())));
					}
				}
			}
			else
			{
				s = factor();
			}
		}
		return s;
	}

	public static String factor()
	{
		//10 + 3
		Token tempToken = token;
		match("NUMBER");
		return checkExp(tempToken.lexeme);
	}

}