
// Calculator program that accepts input like:  123+456=
// Supports addition subtraction and clear display

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class CalcStateTrans extends JFrame implements ActionListener
{
	private JTextField display;
	private String operand1 = "";
	private String operand2 = "";
	private String operation;
	
	public CalcStateTrans()
	{
		Container c = this.getContentPane();
		c.setLayout( new BorderLayout() );
		c.add( "North", display = new JTextField() );
		JPanel keys = new JPanel();
		keys.setLayout( new GridLayout( 5, 3) );
		JButton temp;
		for( int i = 1; i <=9; i++ )
		{
			keys.add( temp = new JButton(""+i) );
			temp.addActionListener( this );
		}
		keys.add( temp = new JButton( "0" ));
			temp.addActionListener( this );
		keys.add( temp = new JButton( "+" ));
			temp.addActionListener( this );
		keys.add( temp = new JButton( "-" ));
			temp.addActionListener( this );
		keys.add( temp = new JButton( "C" ));
			temp.addActionListener( this );
		keys.add( temp = new JButton( "" ));
			temp.addActionListener( this );
		keys.add( temp = new JButton( "=" ));
			temp.addActionListener( this );
		c.add( "Center", keys );

		// ends program when window close icon is pressed
        addWindowListener(new WindowAdapter()
        {
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
	}
	
	private int charClass( String text )
	{
		boolean isDigit = "1234567890".indexOf( text ) != -1; // if buttonpressed is a digit
		if ( isDigit )
		   return 0;
		else if ( "+-".indexOf( text ) != -1 )
		   return 1;
		else if ( text.equals("C" ))
		   return 2;
		else if ( text.equals("=" ))
		   return 3;
		else
		   return -1;  // never happens
	}

	private int currentState = 0;
	private int nextState[][] = {
		{ 1, 0, 0, 0 },
		{ 1, 2, 0, 5 },
		{ 3, 5, 0, 5 },
		{ 3, 0, 0, 4 }
	};
	
	public void actionPerformed( ActionEvent ae )
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
		
	}
	
	// driver program
	
	public static void main( String args[] )
	{
		CalcStateTrans c = new CalcStateTrans();
		c.setSize( 150, 300 );
		c.setVisible( true );
	}
}