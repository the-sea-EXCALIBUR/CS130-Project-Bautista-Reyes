
public class Token 
{
	public String lexeme;
	public String status;
	public String type;
	Token(String types, String lexemes, String statuz)
	{
		lexeme = lexemes;
		status = statuz;
		type = types;
	}

	public boolean match(String s)
	{
		return this.type.equals(s);		
	}
}
