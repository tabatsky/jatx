package jatx.jexpressions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) {
        BufferedReader in = new BufferedReader(new
                InputStreamReader(System.in));		
	
        Parser parser = new Parser();
        
        while (true)
        {
        	String line = "";
            try
            {
                line = in.readLine();
            }
            catch (IOException e) {}
            line = line.replace(" ","");
            if (line.equals("quit")) break;
            if (line.equals("")) continue;
            String lines[] = line.split("=",2);
            String varName = "_";
            if (lines.length==2){
                varName = lines[0];
                line = lines[1];
            }
            
            try
            {
            	System.out.println(parser.prepareExpr(line));
            	Expr expr = parser.parseExpr(parser.prepareExpr(line));
                double result = expr.evaluate();
                System.out.println();
                System.out.println(varName+" = "+result);
                int num = parser.setAlias(varName);
                parser.setVar(num,result);
            } catch (BadVarException e) {
            	System.out.println("Var name is reserved");
            } catch (Exception e) {
                System.out.println("Wrong expression");
            }
        }
	}
}
