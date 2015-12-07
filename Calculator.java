//Calculator.java
/*
This program creates a calculator with which the user can add, subtract, divide, multiply,
and take the square root of numbers via mouse interaction.  Order of operations is respected.
Taking the square root of a negative number yields "NaN" which is cleared on the next operation.

@author	Paul Sotherland
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Calculator
{
	public static void main(String[] args)
	{
		CalcFrame calc = new CalcFrame();
		calc.setVisible(true);
	}
}

/*
*CalcFrame works by storing numbers, decimal points, and binary operators in an ArrayList.
*Multiple decimals/points in a row are checked and merged to keep the ArrayList (ops) in
*an alternating pattern of numbers and binary operations.
*/
class CalcFrame extends JFrame
{
	ArrayList<String> ops = new ArrayList<String>();
	int counter;
	int maxw = 300;
	int maxh = 435;
	
	JButton[] num = { 	new JButton("0"),
						new JButton("1"),
						new JButton("2"),
						new JButton("3"),
						new JButton("4"),
						new JButton("5"),
						new JButton("6"),
						new JButton("7"),
						new JButton("8"),
						new JButton("9")};
	JButton plus = new JButton("+");
	JButton minus = new JButton("-");
	JButton times = new JButton("*");
	JButton div = new JButton("/");
	JButton equals = new JButton("=");
	JButton sqrt = new JButton("SQRT");
	JButton clear = new JButton("C");
	JButton point = new JButton(".");
	JTextField text = new JTextField("0");
	
	/*
	*Constructor.  Creates a new JFrame, sets the layout and button/text sizes.
	*Assigns an ActionListener (NumListener) to each button, and initializes the text field to "0".
	*/
	public CalcFrame()
	{
		this.set_sizes();
		this.build_frame();
		for (int i = 0; i < num.length; i ++)
		{
			this.num[i].addActionListener(new NumListener());
		}
		this.clear.addActionListener(new NumListener());
		this.plus.addActionListener(new NumListener());
		this.minus.addActionListener(new NumListener());
		this.times.addActionListener(new NumListener());
		this.div.addActionListener(new NumListener());
		this.sqrt.addActionListener(new NumListener());
		this.point.addActionListener(new NumListener());
		this.equals.addActionListener(new NumListener());
	}
	
	/*
	*Subclass.  Most operations are outsourced to the op_add function.  Number entries are added to the
	*ArrayList, which is subsequently processed and condensed by a call to the press method.
	*/
	class NumListener implements ActionListener
	{
		/**
		*This method overrides ActionListener's actionPerformed method.
		*
		*@param		e			the ActionEvent
		*/
		public void actionPerformed (ActionEvent e)
		{
			if (counter > 0) counter--;//the purpose of this counter is to allow a numeric entry after "=" to replace the previous answer
		
			Object o = new Object();
			o = e.getSource();
			if (text.getText().equals("NaN"))
			{
				ops.clear();
				text.setText("0");
				return;
			}
			
			//Handling of mathematical operators outsourced to op_add
			if (o == plus) op_add(ops, "+");
			else if (o == minus) op_add(ops, "-");
			else if (o == times) op_add(ops, "*");
			else if (o == div) op_add(ops, "/");
			else if (o == equals) op_add(ops, "=");
			else if (o == sqrt) op_add(ops, "S");
			//Decimal/number reconciliation is handled by the merge method
			else if (o == point) ops.add(".");
			else if (o == clear)
			{
				ops.clear();
				text.setText("0");
				return;
			}
			else
			{
				for (int i = 0; i < num.length; i++)
				{
					if (o == num[i])
					{
						if (counter == 1 && ops.size() == 1) ops.set(0, Integer.toString(i));
						else ops.add(Integer.toString(i));
					}
				}
			}
			
			press(ops); //checks numeric and decimal order for validity, and condenses sequential ArrayList values into one
			set_last(ops); //sets text in the textfield
		}
	}
	
	/**
	*This method ensures that sequential numeric or decimal point entries are condensed on-the-fly.
	*It also prevents multiple decimal points from being incorporated into a single number.
	*
	*@param		events		ArrayList<String> containing numeric and binary operator entries
	*/
	private void press(ArrayList<String> events)
	{
		if (events.size() < 1) return;
		String last = events.get(events.size() - 1);

		if (is_double(last) || contains_pt(last))
		{
			this.merge(events);
			if (events.size() > 1)
			{
				if (is_double(events.get(events.size() - 2))) events.remove(events.size() - 2);
			}
		}
	}
	
	/**
	*This method is used to override previous binary operators if the next entry would be an operator
	*instead of a number.
	*
	*@param		events	ArrayList<String>: historical number and binary operator log
	*@param		op		String representing the most recent operator, not yet present in events
	*
	*@return	true/false if this has occurred
	*/
	private boolean replace_op(ArrayList<String> events, String op)
	{
		if (op.equals("=")) return false;
		String last = events.get(events.size() - 1);
		if (op.equals("S") && is_bop(last))
		{
			events.remove(events.size() - 1);
			return true;
		}
		if (events.size() > 1 && (is_bop(last) || last.equals(".")))
		{
			events.remove(events.size() - 1);
			events.add(op);
			return true;
		}
		return false;
	}
	
	
	/**
	*Verifies that the passed String represents a binary operator (+, -, *, /).
	*
	*@param		test	String being examined
	*@return	true/false
	*/
	private boolean is_bop(String test)
	{
		if (test.equals("+") || test.equals("-") || test.equals("*") || test.equals("/")) return true;
		return false;
	}
	
	/**
	*Executes the most recent addition/subtraction event, and condenses the event list accordingly.
	*
	*@param		events		ArrayList<String>: historical number and binary operator log
	*/
	private void add_last(ArrayList<String> events)
	{
		if (events.size() > 2)
		{
			String first = events.get(events.size() - 3);
			String op = events.get(events.size() - 2);
			String last = events.get(events.size() - 1);
			
			if (is_double(first) && is_double(last))
			{
				double sum;
				if (op.equals("+")) sum = to_double(first)+to_double(last);
				else if (op.equals("-")) sum = to_double(first)-to_double(last);
				else return;
				events.remove(events.size() - 1);
				events.remove(events.size() - 1);
				events.set(events.size() - 1, Double.toString(sum));
			}
		}
	}
	
	/**
	*Executes the most recent multiplication/division event, and condenses the event list accordingly.
	*
	*@param		events		ArrayList<String>: historical number and binary operator log
	*/
	private void mult_last(ArrayList<String> events)
	{
		if (events.size() > 2)
		{
			String first = events.get(events.size() - 3);
			String op = events.get(events.size() - 2);
			String last = events.get(events.size() - 1);
			
			if (is_double(first) && is_double(last))
			{
				double prod;
				if (op.equals("*")) prod = to_double(first)*to_double(last);
				else if (op.equals("/")) prod = to_double(first)/to_double(last);
				else return;
				events.remove(events.size() - 1);
				events.remove(events.size() - 1);
				events.set(events.size() - 1, Double.toString(prod));
			}
		}
	}
	
	/**
	*Dispositions how the most recent operator (non-numeric keypress) should be handled.
	*
	*@param		events		ArrayList<String>: historical number and binary operator log
	*@param		op			the most recent, non-numeric operator
	*/
	private void op_add(ArrayList<String> events, String op)
	{
		if (events.size() < 1) events.add("0");
		if (op.equals("+") || op.equals("-"))
		{
			mult_last(events);
			add_last(events);
		}
		else if (op.equals("*") || op.equals("/"))
		{
			mult_last(events);
		}
		if (!replace_op(events, op) && is_bop(op)) events.add(op); //override previous binary operation, or add new if one doesn't exist
		else if (op.equals("S"))
		{
			String last = events.get(events.size() - 1);
			if (is_double(last))
			{
				double num = to_double(last);
				if (num < 0)
				{
					events.clear();
					text.setText("NaN");
				}
				else
				{
					events.set(events.size() - 1, Double.toString(Math.sqrt(num)));
				}
			}
		}
		else if (op.equals("="))
		{
			mult_last(events);
			add_last(events);
			counter = 2;
		}
		else return;
	}
	

	
	/**
	*Displays the most current number in the CalcFrame textfield.
	*
	*@param		events		ArrayList<String>: historical number and binary operator log
	*/
	private void set_last(ArrayList<String> events)
	{
		String num = "0";
		int max;
		for (int i = events.size() - 1; i >= 0; i--)
		{
			if (is_double(events.get(i)))
			{
				num = events.get(i);
				if (num.length() > 22) max = 22;
				else max = num.length();
				num = num.substring(0, max);
				text.setText(num);
				return;
			}
		}
	}
	
	/**
	*Condenses multiple, sequential numeric or decimal entries into a single valid number or decimal.
	*
	*@param		events		ArrayList<String>: historical number and binary operator log
	*/
	private void merge(ArrayList<String> events)
	{
		if (events.size() > 1)
		{
			boolean merge = false;
			String last = events.get(events.size() - 1);
			String next = events.get(events.size() - 2);
			if (next.equals("0") && !contains_pt(last))
			{
				events.remove(events.size() - 2);
				return;
			}
			if (contains_pt(next) && contains_pt(last))
			{
				events.remove(events.size() - 1);
				return;
			}
			if (is_double(next) || contains_pt(next))
			{
				if (contains_pt(last))
				{
					if (!contains_pt(next)) merge = true;
				}
				else
				{
					if (is_double(last))
					{
						merge = true;
					}
				}
			}
			if (merge)
			{
				events.remove(events.size() - 1);
				events.remove(events.size() - 1);
				events.add(next + last);
			}
		}
	}
	
	
	/**
	*Attempts to convert a String into a double value.  Has a do-nothing catch for NumberFormatExceptions.
	*The is_double method should be employed before using this function.
	*
	*@param		test		String being converted
	*@return	double (zero is default).
	*/
	private double to_double(String test)
	{
		double num = 0;
		try
		{
			num = Double.parseDouble(test);
		}
		catch (NumberFormatException e)
		{
			//do nothing
		}
		return num;
	
	}
	
	/**
	*Tests whether the passed String represents a double-precision number.
	*
	*@param		test		String being tested
	*@return	true/false, if the String can be converted to a double
	*/
	private boolean is_double(String test)
	{
		double num = 0;
		try
		{
			num = Double.parseDouble(test);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}
	
	/**
	*Tests whether the passed String contains a decimal point character.
	*
	*@param		test		String being tested
	*@return	true/false
	*/
	private boolean contains_pt(String test)
	{
		for (int i = 0; i < test.length(); i++)
		{
			if (test.charAt(i) == '.') return true;
		}
		return false;
	}
	
	/**
	*Layout function.  Sets window/control sizes and fonts.
	*/
	private void set_sizes()
	{
		int minw = 60;
		int minh = 60;
		Dimension min = new Dimension(minw, minh);
		Dimension larger = new Dimension(2*minw + 5, minh);
		Dimension wide = new Dimension(maxw, minh);
		for (int i = 0; i < num.length; i++)
		{
			num[i].setPreferredSize(min);
		}
		plus.setPreferredSize(min);
		minus.setPreferredSize(min);
		times.setPreferredSize(min);
		div.setPreferredSize(min);
		equals.setPreferredSize(larger);
		sqrt.setPreferredSize(larger);
		clear.setPreferredSize(min);
		point.setPreferredSize(min);
		text.setPreferredSize(wide);
		text.setHorizontalAlignment(SwingConstants.RIGHT);
		Font f = new Font("Serif", Font.BOLD, 26);
		text.setFont(f);
	}
	
	/**
	*Layout function.  Arranges the controls in the CalcFrame.
	*/
	private void build_frame()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());

		text.setEditable(false);
		
		buttons.add(clear);
		buttons.add(sqrt);
		buttons.add(div);
		buttons.add(num[7]);
		buttons.add(num[8]);
		buttons.add(num[9]);
		buttons.add(times);
		buttons.add(num[4]);
		buttons.add(num[5]);
		buttons.add(num[6]);
		buttons.add(minus);
		buttons.add(num[1]);
		buttons.add(num[2]);
		buttons.add(num[3]);
		buttons.add(plus);
		buttons.add(point);
		buttons.add(num[0]);
		buttons.add(equals);
		
		this.setPreferredSize(new Dimension(maxw, maxh));
		this.add(text, BorderLayout.NORTH);
		this.add(buttons, BorderLayout.CENTER);
		
		this.pack();
		this.setResizable(false);
		this.setTitle("Calculator");
	}
}










