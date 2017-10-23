import java.io.*;
import java.util.*;
import java.util.regex.*;






public class CASExecution
{
	public static void main(String[] args)
	{
		String befehl = "";
		//hashtable fuer Variablen
		Hashtable<String,String> var = new Hashtable<String,String>();
		String objects="double";
		//in der Variable objects wird festgehalten, ob mit reelen Zahlen (double) oder mit komplexen Zahlen gerechnet wird
		
		
	
		
		while(true)
		{
			System.out.print(">");
			BufferedReader eingabe = new BufferedReader( new InputStreamReader(System.in));
			try
			{			
				befehl = eingabe.readLine();
			} 
			catch(IOException e) {}
			befehl = befehl.replaceAll("\\s+", "");
			//ein Befehl wird eingelesen; alle Leerzeichen werden entfernt

			//pattern_ops prueft, ob ein Operator vorliegt
			Pattern pattern_ops = Pattern.compile("[\\+\\-\\*\\/\\^]");
			//pattern_var ueberprueft ob es sich um eine Variable handelt
			Pattern pattern_var = Pattern.compile("[a-zA-Z_]+[a-zA-Z_0-9]*");
			Matcher matcher_befehl_var =pattern_var.matcher(befehl);
			
			//pattern ueberprueft, ob einer Variable ein Wert zugewiesen wird 
			Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)=(.+)");
			Matcher matcher = pattern.matcher(befehl);
			Matcher befehl_ops =pattern_ops.matcher(befehl);
			while(matcher_befehl_var.find())
			{
				if(var.containsKey("imaginary unit"))
				{
					if(matcher_befehl_var.group().equals(var.get("imaginary unit")))
					{
						objects="complex";
						/*wenn eine imaginaere Einheit in einem Befehl gefunden wird, wird 
						die Variable objects auf complex gesetzt, da nun mit komplexen Zahlen gerechnet wird */
					}
					if(var.containsKey(matcher_befehl_var.group()))
					{
						if(var.get(matcher_befehl_var.group()).contains(var.get("imaginary unit")))
						{
							objects="complex";
							/*Wenn eine Variable in einem Befehl vorkommt, die eine imaginaere Einheit enthaelt,
							wird die Variable objects auf complex gesetzt, da nun mit komplexen Zahlen gerechnet wird */
						}
					}
				}
			}	
				
			if(befehl.equals("exit"))
				//Wenn der Befehl exit lautet, wird da Programm abgebrochen
				break;
			else if(befehl.equals("ShowVar"))
			{
				//Bei dem Befehl ShowVar werden alle Variablen mit Namen und Wert angezeigt
				Enumeration<String> names;
      				String str;
				names = var.keys();
      
	      			while(names.hasMoreElements()) 
				{
	         			str = names.nextElement();
					System.out.println(str + ": " + var.get(str));
		      		}    
			}
			else if(befehl.matches("ComplexField\\(([a-zA-Z_]+[a-zA-Z_0-9]*)\\)"))
			{
				//ist der Befehl Complex(var) fuer eine Variable var, dann wird eine imaginare Einheit var angelegt
				Pattern pattern_var_complex = Pattern.compile("ComplexField\\(([a-zA-Z_]+[a-zA-Z_0-9]*)\\)");
				Matcher ima_uni=pattern_var_complex.matcher(befehl);
				while(ima_uni.find())
				{
					var.put("imaginary unit",ima_uni.group(1));
				}
				
			}
			
			//ueberprueft, ob Befehl eine Variablenzuweisung ist
			else if(befehl.matches("([a-zA-Z0-9_]+)=(.+)"))
			{
				
   				
				
				if(matcher.find())
				{
					
					//wenn Ausdruck vor = eine Variable ist
					if(matcher.group(1).matches("[a-zA-Z_]+[a-zA-Z_0-9]*"))
					{	
						
   						Matcher group2_ops = pattern_ops.matcher(matcher.group(2));
						
						
					
						//wenn Ausdruck nach = eine Zahl ist
						if(matcher.group(2).matches("([0-9])+(\\.?([0-9])+)?"))
						{
							var.put(matcher.group(1),matcher.group(2));
						}
						//wenn Ausdruck nach = eine Variable ist
						else if(matcher.group(2).matches("[a-zA-Z_]+[a-zA-Z_0-9]*"))
						{
							if( var.containsKey(matcher.group(2)))
							{
							var.put(matcher.group(1),var.get(matcher.group(2)));
							}
							else
								System.out.println(">Error: " +  matcher.group(2) + " is not defined");
						}	
						
						else if(group2_ops.find())
						{
							/* wird ein Operator in dem Ausdruck nach = gefunden so liegt eine Gleichung vor,
							die Ausgewertet werden muss */
							/*je nachdem, ob nur reelle Zaheln oder mindestens eine komplexe Zahl vorliegt, wird
							eine Funktion zur Auswertung von reellen oder komplexen Zahlen aufgerufen */
							if(objects.equals("double"))
								var.put(matcher.group(1),Eval.evaluate(matcher.group(2),var));
							if(objects.equals("complex"))
							{
								/* Ueberpruefung, ob nach dem Gleichheitszeichen ein Term von komplexen Zahlen oder
								nur eine einzige komplexe Zahl mit nichttrivialem Real- und Imaginaerteil vorliegt */
								if(matcher.group(2).matches("([0-9])+(\\.?([0-9])+)?\\+([0-9])+(\\.?([0-9])+)?\\*"+var.get("imaginary unit")))								
								var.put(matcher.group(1),matcher.group(2));
								//Ueberpruefung, ob nach dem Gleichheitszeichen eine rein imaginaere komplexe Zahl steht
								else if(matcher.group(2).matches("(([0-9])+(\\.?([0-9])+)?)\\*"+var.get("imaginary unit")))
									var.put(matcher.group(1),matcher.group(2));
								else
								{
									//Funktion zur Auswertung von Termen mit komplexen Zahlen vorliegt
									var.put(matcher.group(1),Complex.evaluate(matcher.group(2),var));
								}
								
							}								
							
						}
						
						else
							System.out.println(">Error: second expression is not valid");				
					
					}	
					else
						System.out.println(">Error: First expression is not a variable");
					
				}
			}
			else if(befehl_ops.find())
			{
				/* Wenn ein Term vorliegt, dann wird je nachdem, ob nur reelle Zahlen oder mindestens eine komplexe Zahl
				vorkommt, eine Funktion zur Auswertung von Termen mit reellen oder komplexen Zahlen aufgerufen */
				if(objects.equals("double"))
					System.out.println(">" + Eval.evaluate(befehl,var));
				if(objects.equals("complex"))
				{
					System.out.println(">" + Complex.evaluate(befehl,var));
				}
			}
			//Ist befehl nur eine Variable, so wird ihr Wert ausgegeben
			else if(befehl.matches("[a-zA-Z_]+[a-zA-Z_0-9]*"))
			{
				if(var.containsKey(befehl))
				{
					System.out.println(">" + var.get(befehl));
				}
				else
					//ist die Variable nicht definiert, so erfolgt eine Fehlermeldung
					System.out.println("> Error: " +  befehl + " is not defined");			
			}
			
			
			
			objects="double";
			//fuer den naechsten Befehl wird objects wieder auf double gesetzt
		}
	}
}
