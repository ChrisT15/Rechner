import java.util.*;
import java.util.Stack;
import java.util.regex.*;



public class Complex
{
	//eine komplexe Zahl besteht aus einem Realteil und einem Imaginaerteil
	private double re;
	private double im;
		
	
		
	public Complex(double real, double ima)
	{
		re=real;
		im=ima;	
	}
		
	public String toString(String im_un)
	{
		//eine komplexe Zahl wird in der Form a+ib geschrieben, wobei i eine imaginaere Einheit ist
		String re_str=Double.toString(re);
		String im_str=Double.toString(im);
		if(re != 0 && im != 0)
			return (re_str + "+" + im_str + "*" + im_un);	
		else
		{
			if(re==0)
			{
				return (im_str + "*" + im_un);
			}
			else
			{
				return re_str;
			}
		}
		
	}
		
	public Complex plus(Complex b)
	{
		return new Complex(re+b.re,im+b.im);
	}
	
	public Complex minus(Complex b)
	{
		return new Complex(re-b.re,im-b.im);
	}
	
	public Complex times(Complex b)
	{
		return new Complex(re*b.re-im*b.im,re*b.im+im*b.re);
	}
	
	public Complex divide(Complex b)
	{
			Complex c = new Complex(b.re/(b.re*b.re+b.im*b.im),-b.im/(b.re*b.re+b.im*b.im));
			return (this.times(c));
		
	}
		
	public int toInt()
	{
		/*Hat eine komplexe Zahl keinen Imaginaerteil und ist ihr Realteil eine ganze Zahl,so
		kann diese komplexe Zahl in eine ganze Zahl umgewandelt werden */
		/* diese Funktion wird fuer die Potenzrechnung benoetigt, da diese momentan nur fuer ganzzahlige
		Exponenten funktioniert */
		if(im==0)
			return ((int) re);
		else
			return 0;	
	}
	
	public static String evaluate(String expression,Hashtable<String,String> var)
	{
		//Funktion zur Auswertung von Termen mit komplexen Zahlen
		
		
		
		//expression wird in ein Array aus Characters umgewandelt
		char[] tokens = expression.toCharArray();
		
		
		//Stack fuer die Werte, welche komplexe Zahlen sind
		Stack<Complex> values = new Stack<Complex>();
		//Stack fuer die Operatoren
		Stack<Character> ops = new Stack<Character>();
		
		//Wenn erstes Zeichen ein Minuszeichen ist, so ist dies ein Vorzeichen
		boolean add_minus = false;
		if(tokens[0] == '-')
		{
			add_minus = true;
		}	

		for(int i=0;i<tokens.length;i++)
		{
			boolean neg_number1 = tokens[0]=='-';
			boolean neg_number2 = i>0 && tokens[i] == '-' && Character.toString(tokens[i-1]).matches("[\\+\\-\\/\\*\\(\\)]");
			/*falls das erste Zeichen in tokens ein Minuszeichen ist oder vor einem Minuszeichen ein Operator steht, 
			ist dieses Minuszeichen ein Vorzeichen */
			
			if(Character.toString(tokens[i]).matches("[^\\+\\-\\/\\*\\\\(\\)\\^]") || neg_number2)
			{
				
				/*Wenn das aktuelle Zeichen kein Operator ist, wird ein StringBuffer sbuf angelegt,
				in dem eine Variable oder eine Zahl gespeichert wird */	
				StringBuffer sbuf = new StringBuffer();

				if(add_minus)
				{
					//ist das erste Zeichen ein Minuszeichen, so ist es ein Vorzeichen und wird zu sbuf hinzugefuegt
					sbuf.append(tokens[0]);
					add_minus= false;
				}	
	
				

				while (i < tokens.length &&  (Character.toString(tokens[i]).matches("[a-zA-Z0-9._]") || neg_number2))
				{
					/*Solange expression noch nicht zu Ende ist und das aktuelle Zeichen ein Buchstabe, eine
					Ziffer,ein Punkt, ein negatives Vorzeichen oder ein Unterstrich ist, wird das aktuelle Zeichen zu sbuf hinzugefuegt */
					neg_number2 = i>0 && tokens[i] == '-' && Character.toString(tokens[i-1]).matches("[\\+\\-\\/\\*\\(\\)]");
					sbuf.append(tokens[i++]);
				}
				i--;
				//Ruecksprung um ein Zeichen, nachdem das erste Operator gelesen wurde
				String sbuf_str=sbuf.toString();
				if(sbuf_str.matches("(\\-?[0-9])+(\\.?([0-9])+)?"))
				{
					//Wenn sbuf eine Zahl ist, wird sie auf den Stack values gelegt
					Complex new_com = new Complex(Double.parseDouble(sbuf_str),0);
					values.push(new_com);
				}
				else if(sbuf_str.matches("[a-zA-Z_]+[a-zA-Z_0-9]*"))
				{
					if(sbuf_str.equals(var.get("imaginary unit")))
					{
						//ist sbuf eine imaginaere Einheit, so wird diese auf den Stack values gelegt
						Complex new_com = new Complex(0,1);
						values.push(new_com);
					}
					else if(var.containsKey(sbuf_str))
					{
						if(var.get(sbuf_str).matches("(\\-?[0-9])+(\\.?([0-9])+)?"))
						{
							/*ist sbuf ein Variablenname und der Wert der Variable eine rein reelle Zahl, so wird
							dieser Wert auf den Stack values gelegt */ 
							Complex new_com = new Complex(Double.parseDouble(sbuf_str),0);
							values.push(new_com);
						}
						else if(var.get(sbuf_str).matches("((\\-?[0-9])+(\\.?([0-9])+)?)\\+((\\-?[0-9])+(\\.?([0-9])+)?)\\*"+var.get("imaginary unit")))
						{
							/*Ist sbuf eine Variable und deren Werte eine komplexe Zahl mit nichttrivialem 
							Realteil und nichttrivialem Imaginaerteil,
							so werden Realteil und Imaginaerteil darauf ueberprueft, ob sie reelle Zahlen sind und
							der Wert der Variablen auf dem Stack values gespeichert  */
							String com_val= var.get(sbuf_str);
							Pattern pattern_z_c = Pattern.compile("(\\-?[0-9])+(\\.?([0-9])+)?");
							Matcher matcher_z_c = pattern_z_c.matcher(com_val);
							List<String> com_args= new ArrayList<String>();
							//Liste fuer den Real-und Imaginearteil
							
							while(matcher_z_c.find())
							{
								com_args.add(matcher_z_c.group());
							}
							double arg1 = Double.parseDouble(com_args.get(0));
							double arg2 = Double.parseDouble(com_args.get(1));
							Complex new_c = new Complex(arg1,arg2);
							values.push(new_c);

						}
						else if(var.get(sbuf_str).matches("((\\-?[0-9])+(\\.?([0-9])+)?)\\*"+var.get("imaginary unit")))
						{
							/*Wenn sbuf eine Variable ist und deren Wert eine rein imaginaere komplexe Zahl ist,
							so wird der Wert auf den Stack values gelegt */
							Pattern pattern_z_c2 = Pattern.compile("(\\-?[0-9])+(\\.?([0-9])+)?");
							Matcher matcher_z_c2 = pattern_z_c2.matcher(var.get(sbuf_str));	
							double arg2=0;
							if(matcher_z_c2.find())						
								arg2= Double.parseDouble(matcher_z_c2.group());
							Complex new_c = new Complex(0,arg2);
							values.push(new_c);
						}
							
						
					}
					else	
						return "Error: variable " + sbuf_str + " is not defined";
						//ist sbuf eine undefinierte Variable, so erscheint eine Fehlermeldung
				}
				
				//System.out.println("double: " + Double.parseDouble(sbuf.toString()));
				//values.push(Double.parseDouble(sbuf.toString()));
			}

			else if (tokens[i] == '(')
                		ops.push(tokens[i]);
				//eine oeffnende Klammer wird auf den Stack ops gelegt
			
			else if (tokens[i] == ')')
		            {
		                while (ops.peek() != '(')
				/* Ist das aktuelle Zeichen eine schliessende Klammer, so wird der Term solange ausgewertet bis
				das oberste Element auf dem Stack ops eine oeffnende Klammer ist */
		                  values.push(applyOp(ops.pop(), values.pop(), values.pop()));
		                ops.pop();
				//oeffnende Klammer wird vom Stack ops geworfen
		            }	
			
			else if (tokens[i]=='+' || tokens[i] == '-' || tokens[i]=='*' || tokens[i]=='/' || tokens[i] == '^')
            		{
				
				/* Hat das oberste Element auf dem Stack ops eine gleiche oder groessere Prioritaet wie
				das aktuelle Zeichen, welches ein Operator ist, wird der Operator, der oben auf ops liegt,
				auf die zwei obersten Elemente des Stacks values mit Hilfe der Funktion applyOp angewendet */
                		
				//Wenn das erste Zeichen ein Minuszeichen ist, so ist es ein Vorzeichen und kein Operator
				if(add_minus == false)
                		{
					while (!ops.empty() && hasPrecedence(tokens[i], ops.peek()))
					{
						Complex zahl= applyOp(ops.pop(), values.pop(), values.pop());
                  				values.push(zahl);
					}
 
                			// das akutelle Zeichen, welches ein Operator ist, wird auf den Stack ops gelegt
                			ops.push(tokens[i]);
				}
            		}		
 
		}
		
		//Solange Operatoren in dem Stack ops enthalten sind, wird der Term ausgewerten
		while (!ops.empty())
		{
			Complex zahl2=applyOp(ops.pop(), values.pop(), values.pop());
            		values.push(zahl2);
		}
 
        		//Sind alle Operatoren und Werte verarbeitet worden, so ist der einzige Wert im Stack values das Ergebnis
			String result = values.pop().toString(var.get("imaginary unit"));
        		return result;
	}
	
	public static boolean hasPrecedence(char op1, char op2)
    	{
		//diese Funktion entscheidet welcher Operator groessere Prioritaet besitzt
        	if (op2 == '(' || op2 == ')')
		//ist der zweite Operator eine Klammer, so hat der erste Operator groessere Prioritaet
        	    return false;
		//Punkt- vor Strichrechnung
        	if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
        	    return false;
		//Potenzrechnung hat Vorrang
		if (op1 == '^' && (op2== '*' || op2=='/' || op2=='+' || op2=='-'))
			return false;
		//^ ist rechts assoziativ
		if (op1 =='^' && op2 =='^')
			return false;
        	else
        	    return true;
    	}
		
	public static Complex pow(Complex a, int b)
	{
		//Funktion fuer die Potenzrechnung
		Complex result= new Complex(1,0);
		for(int i=1;i<=b;i++)
		{
			result=result.times(a);
		}
		return result;
				
	}
		
	public static Complex applyOp(char op, Complex b, Complex a)
    	{
		//Funktion, welche einen Operator op auf zwei komplexe Zahlen a und b anwendet
        	switch (op)
        	{
       			 case '+':
            			return a.plus(b);
        		case '-':
            			return a.minus(b);
        		case '*':
            			return a.times(b);
        		case '/':
            			if (b.re == 0 && b.im ==0)
                		throw new
                		UnsupportedOperationException("Cannot divide by zero");
            			return a.divide(b);
			case '^':
				int b_int = b.toInt();
				return pow(a,b_int);
        	}
        	return new Complex(0,0);
    }
}


