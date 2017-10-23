import java.util.Stack;
import java.util.regex.*;
import java.util.*;

class Eval
{
	public static String evaluate(String expression,Hashtable<String,String> var)
	{
		
		
		//die Zeichenkette expression wird in ein Array von characters umgewandelt
		char[] tokens = expression.toCharArray();
			
		
		Stack<Double> values = new Stack<Double>();
		//Stack fuer alle Werte in expression
		Stack<Character> ops = new Stack<Character>();
		//Stack fuer alle Operanden in expression
		
		//Wenn erstes Zeichen ein Minuszeichen ist, so ist dies ein Vorzeichen
		boolean add_minus = false;
		if(tokens[0] == '-')
		{
			add_minus = true;
		}	
		

		for(int i=0;i<tokens.length;i++)
		{
			
			boolean neg_number2 = i>0 && tokens[i] == '-' && Character.toString(tokens[i-1]).matches("[\\+\\-\\/\\*\\(\\)]");
			/*falls vor einem Minuszeichen ein Operator steht, ist dieses Minuszeichen ein Vorzeichen */
			
			
			
			if(Character.toString(tokens[i]).matches("[^\\+\\-\\/\\*\\(\\)\\^]") ||  neg_number2)
			{
				//wenn das aktuelle Zeichen kein Operand ist, wird eine Variable oder eine Zahl eingelesen
				
				
				StringBuffer sbuf = new StringBuffer();
				
				if(add_minus)
				{
					//ist das erste Zeichen ein Minuszeichen, so ist es ein Vorzeichen und wird zu sbuf hinzugefuegt
					sbuf.append(tokens[0]);
					add_minus = false;
				}				
				 
				//da eine Zahl oder eine Variable sich ueber mehrere Zeichen erstrecken kann, wird ein StringBuffer angelegt
				while (i < tokens.length &&  (Character.toString(tokens[i]).matches("[a-zA-Z0-9._]") || neg_number2))
				{
					/*Solange das Ende von expression nicht erreicht ist und das aktuelle Zeichen ein Buchstabe, eine Ziffer,
					ein Punkt, ein negatives Vorzeichen oder ein Unterstrich ist, werden die Zeichen zu sbuf hinzugefuegt */ 
						
						neg_number2 = i>0 && tokens[i] == '-' && Character.toString(tokens[i-1]).matches("[\\+\\-\\/\\*\\(\\)]");
						sbuf.append(tokens[i++]);
					
				}
				/*Ruecksprung um ein Zeichen, nachdem das erste Zeichen gelesen wurde, das nicht mehr zu einer Variablen oder
				zu einer Zahl gehört */
				i--;
				//sbuf wird in einen String verwandelt
				String sbuf_str=sbuf.toString();
				
				if(sbuf_str.matches("\\-?([0-9])+(\\.?([0-9])+)?"))
					//Wenn sbuf eine Zahl ist, wird sie zu dem Stack fuer Werte hinzugefuegt
					values.push(Double.parseDouble(sbuf_str));
				if(sbuf_str.matches("[a-zA-Z_]+[a-zA-Z_0-9]*"))
				{
					/*Wenn sbuf der Name einer Variable ist, so wird der Werte der Variable zu dem Stack fuer Werte
					hinzugefuegt, wenn die Variable definiert ist */
					if(var.containsKey(sbuf_str))
					{
						if(var.get(sbuf_str).matches("(\\-?[0-9])+(\\.?([0-9])+)?"))
							values.push(Double.parseDouble(var.get(sbuf_str)));
						
					}
					else	
						return "Error: variable " + sbuf_str + " is not defined";
				}
				
				
			}

			else if (tokens[i] == '(')
				//Ist das Zeichen eine öffnende Klammer, so wird die Klammer zu dem Stack fuer Operanden hinzugefuegt
                		ops.push(tokens[i]);
			
			else if (tokens[i] == ')')
		            {
				/*Ist das Zeichen eine schliessende Klammer, so wird der Term solange ausgewertet wird das oberste Element d
				des Stacks fuer Operatoren eine oeffnende Klammer ist */
		                while (ops.peek() != '(')
		                  values.push(applyOp(ops.pop(), values.pop(), values.pop()));
		                ops.pop();
				//oeffnende Klammer wird vom Stack fuer Operatoren entfernt
		            }	
			
			else if (tokens[i]=='+' || tokens[i] == '-' || tokens[i]=='*' || tokens[i]=='/' || tokens[i] == '^')
            		{
				//das Zeichen ist ein Operator

				/*Wenn das oberste Element von dem Stack ops dieselbe oder groessere Prioritaet hat wie 
				das aktuelle Zeichen, welches ein Operator ist, wird der Operator, der das oberste 
				Element auf dem Stack ops ist, auf die zwei obersten Elemente des Stacks values mit Hilfe der Funktion
				applyOp angewendet */
                		
				//Wenn das erste Zeichen im Term ein Minus ist, so ist dies ein Vorzeichen und kein Operator
				if(add_minus == false)
				{
                			while (!ops.empty() && hasPrecedence(tokens[i], ops.peek()))
					{
						// die Funktion hasPrecendence ueberprueft, welcher Operator groessere Prioritaet besitzt
						double zahl= applyOp(ops.pop(), values.pop(), values.pop());
                  				values.push(zahl);
					}
					ops.push(tokens[i]);
					//das aktuelle Zeichen wird auf den Stack ops gelegt
				}	
 
                		
            		}		
 
		}
		
		while (!ops.empty())
		{
			/*Solange der Stack ops nicht leer ist, werden die Operatoren auf die Werte in dem Stack values
			mit Hilfe der Funktion applyOp angewendet */
			double zahl2=applyOp(ops.pop(), values.pop(), values.pop());
            		values.push(zahl2);
		}
 
        		// Top of 'values' contains result, return it
			String result = String.valueOf(values.pop());
        		return result;
			//Sind alle Operatoren und Werte verarbeitet worden, so ist das einzige Element vom Stack values das Ergebnis
	}
	
	public static boolean hasPrecedence(char op1, char op2)
    	{
		//Diese Funktion stellt fest, welcher Operator groessere Prioritaet besitzt
		//Dabei gilt Punkt vor Strich Rechnung und Potenzrechnung vor den anderen Operatoren
        	if (op2 == '(' || op2 == ')')
		//die der zweiter Operator eine Klammer, so besitzt der erste Operator die groessere Prioritaet
        	    return false;
        	if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
        	    return false;
		if (op1 == '^' && (op2== '*' || op2=='/' || op2=='+' || op2=='-'))
			return false;
		//^ ist rechts assoziativ
		if (op1 =='^' && op2 =='^')
			return false;
        	else
        	    return true;
    	}
		
	public static double pow(double a, double b)
	{
		//Funktion fuer die Potenzrechnung
		double result=1;
		for(int i=1;i<=b;i++)
		{
			result*=a;
		}
		return result;
				
	}
		
	public static double applyOp(char op, double b, double a)
    	{
		//Funkion, welche einen Operator op auf zwei reelle Zahlen a und b anwendet
        	switch (op)
        	{
       			 case '+':
            			return a + b;
        		case '-':
            			return a - b;
        		case '*':
            			return a * b;
        		case '/':
            			if (b == 0)
                		throw new
                		UnsupportedOperationException("Cannot divide by zero");
				//durch Null teilen ist nicht definiert
            			return a / b;
			case '^':
				return pow(a,b);
        	}
        	return 0;
    }
}
