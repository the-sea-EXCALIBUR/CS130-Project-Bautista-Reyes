
// Calculator program that accepts input like:  123+456=
// Supports addition subtraction and clear display

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class HTMLStateTrans extends JFrame implements ActionListener
{

	public HTMLStateTrans() throws Exception
	{
		FileReader file = null;
		try {
		file = new FileReader("text.txt");
		}
		catch(FileNotFoundException ae){
			System.out.println("File not Found");
		}
		
		file.close();
	}
	
	private int charClass( String text )
	{
		
	}

	private int currentState = 0;
	private int nextState[][] = {
		{ 1, 0, 0, 0 },
		{ 1, 2, 0, 5 },
		{ 3, 5, 0, 5 },
		{ 3, 0, 0, 4 }
	};

	public static void main( String args[] )
	{
		HTMLStateTrans test = new HTMLStateTrans();
		
	}
	
	/*public void actionPerformed( ActionEvent ae )
	{
		JButton b = (JButton) ae.getSource();
		String textOnButton = b.getText();
		int charIndex = charClass( textOnButton );
		int prevState = currentState;
		currentState = nextState[currentState][charIndex];
		// action is based on resulting state
		switch( currentState )
		{
			case 0:  
				operand1 = "";
				operand2 = "";
				display.setText("");
				break;
			case 1:
			    if (prevState == 0)
			    {
			    	operand1 = ""; display.setText("");
			    }
				operand1 += textOnButton;
				display.setText( display.getText() + textOnButton );
				break;
			case 2:
				operation = textOnButton;
				break;
			case 3:
			    if (prevState == 2)
			    {
			    	operand2 = ""; display.setText("");
			    }
				operand2 += textOnButton;
				display.setText( display.getText() + textOnButton );
				break;
			case 4:
				if ( operation.equals("+") )
				{
					int result = Integer.parseInt( operand1 ) + Integer.parseInt( operand2 );
					display.setText(""+result);
				}
				else if ( operation.equals("-") )
				{
					int result = Integer.parseInt( operand1 ) - Integer.parseInt( operand2 );
					display.setText(""+result);
				}
				currentState = 0; // restart because done
				break;
			case 5: // start over state
				display.setText("Start Over");
				currentState = 0; // restart due to error
				break;

		}
		
	}*/
	
	// driver program
	
}