package jatx.jexpressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser 

{	
	public static final String[] constants = {"E","PI"};
    public static final String[] functions =
    	{"sin","cos","tan","exp","log","sinh","cosh","tanh",
    	"abs","sqrt","acos","asin","atan","signum",
    	"gamma","airyAi","airyBi","beta","besselJ","besselY","besselI","besselK"};
    public static final String[] before =
    	{"{","(","+","-","*","/","^","#",":"};
    public static final String[] after = 
    	{"}",")","+","-","*","/","^",":"};
	
	
    private Map<Integer,Double> vars;
    private List<String> alias;
    private static final String varSep = "!";
    
    public Parser() {
    	vars = new HashMap<Integer,Double>();
    	alias = new ArrayList<String>();
    }
    
    public int setAlias(String name) throws BadVarException {
    	for (String str: constants) {
    		if (name.equals(str)) throw new BadVarException();
    	}
    	for (String str: functions) {
    		if (name.equals(str)) throw new BadVarException();
    	}
    	
    	if (alias.indexOf(name) == -1) alias.add(name);
    	return alias.indexOf(name);
    }
    
    public String[] getAlias() {
    	return alias.toArray(new String[alias.size()]);
    }

    public double getVar(Integer num)
    {
        Double val = vars.get(num);
        if (val==null) return Double.NaN;
        return val.doubleValue();
    }

    public void setVar(Integer num, double value)
    {
        vars.put(num, value);
    }

    public static boolean isDigit(char c)
    {
        if (c=='0') return true;
        if (c=='1') return true;
        if (c=='2') return true;
        if (c=='3') return true;
        if (c=='4') return true;
        if (c=='5') return true;
        if (c=='6') return true;
        if (c=='7') return true;
        if (c=='8') return true;
        if (c=='9') return true;
        if (c=='.') return true;
        return false;
    }

    public static boolean isPlus(char c)
    {
        if (c=='+') return true;
        return false;
    }

    public static boolean isMinus(char c)
    {
        if (c=='-') return true;
        return false;
    }

    public static boolean isMult(char c)
    {
        if (c=='*') return true;
        return false;
    }

    public static boolean isDiv(char c)
    {
        if (c=='/') return true;
        return false;
    }

    public static boolean isPow(char c)
    {
        if (c=='^') return true;
        return false;
    }

    public static boolean isFunc(char c)
    {
        if (c=='#') return true;
        return false;
    }

    public static boolean isPair(char c)
    {
        if (c==':') return true;
        return false;
    }

    public static boolean isOper(char c)
    {
        if (isPlus(c)||isMinus(c)||isMult(c)||isDiv(c)||
                isPow(c)||isFunc(c)||isPair(c)) return true;
        return false;
    }

    public static boolean isLeftPar(char c)
    {
        if (c=='(') return true;
        return false;
    }

    public static boolean isRightPar(char c)
    {
        if (c==')') return true;
        return false;
    }

    public static boolean isLetter(char c)
    {
        if((c>='a')&&(c<='z')) return true;
        if((c>='A')&&(c<='Z')) return true;
        if((c>='À')&&(c<='ß')) return true;
        if((c>='à')&&(c<='ÿ')) return true;
        if(c=='_') return true;
        return false;
    }

    public static boolean isVar(char c)
    {
        if (c=='$') return true;
        return false;
    }

    public static boolean isSpace(char c)
    {
        return (!(isDigit(c)||isOper(c)||isLetter(c)
                ||isLeftPar(c)||isRightPar(c)||isVar(c)));
    }

    public String prepareExpr(String s) throws Exception
    {
    	String line = s;
    	line = "{"+line+"}";
    	
    	int len = line.length();
    	while (true) {
    		len = line.length();
    		line = line.replace("--", "+");
    		line = line.replace("++", "+");
    		line = line.replace("-+", "-");
    		line = line.replace("+-", "-");
    		if (len == line.length()) break;
    	}
    	
    	for (String str: constants) {
        	for (String start: before) {
        		for (String end: after) {
        			line = line.replace(start+str+end, start+"&"+str+"#0"+end);
        		}
        	}
        }        
        for (String str: functions) {
        	line = line.replace(str+"(", "&"+str+"#(");
        }
        line = line.replace(",", ":");
        
        String[] alias = getAlias();
        for (String var: alias) {
        	for (String start: before) {
        		for (String end: after) {
        			line = line.replace(start+var+end, 
        					start+"$"+varSep+var+varSep+end);
        		}
        	}
        }
    	
    	for (int i=0; i<alias.length; i++) {
    		line = line.replace(varSep+alias[i]+varSep, new Integer(i).toString());
    		//System.out.println(line);
    	}
    	
    	line = line.replace("$", "var#");
    	line = line.replace("}","");
    	line = line.replace("{","");
    	line = line.replace("&","");
    	
    	//System.out.println(line);
    	
        StringBuilder expr = new StringBuilder(line);
        int i=0;
        int pars = 0;
        for (i=0; i<expr.length(); i++)
        {
            if (isLeftPar(expr.charAt(i))) pars++;
            if (isRightPar(expr.charAt(i))) pars--;
        }
        if (pars!=0) throw new Exception();
        i=0;
        while (i<expr.length())
        {
            if (isSpace(expr.charAt(i)))
            {
                expr.delete(i, i + 1);
            }
            else
            {
              i++;
            }
        }

        if (isMinus(expr.charAt(0))||isPlus(expr.charAt(0))) expr.insert(0, "0");

        i=expr.length()-1;
    outer0:
        while (i>=0)
        {
            if (isFunc(expr.charAt(i))/*(||isPair(expr.charAt(i))*/){
                int par=0;
                int j = i-1;
             inner1:
                while (j>=0)
                {
                    if (isRightPar(expr.charAt(j))) par++;
                    if (isLeftPar(expr.charAt(j))) par--;
                    if (j==0 && par<=0)
                    {
                        expr.insert(j, "(");
                        i++;
                        break inner1;
                    }

                    if (par<=0 && isOper(expr.charAt(j)))
                    {
                        expr.insert(j+1, "(");
                        i++;
                        break inner1;
                    }
           /*         if (par==-1){
                        i++;
                        continue outer0;
                    } */
                    j--;
                }

                j = i+1;
                par = 0;
            inner2:
                while (j<expr.length())
                {
                    if (isRightPar(expr.charAt(j))) par--;
                    if (isLeftPar(expr.charAt(j))) par++;
                    if (j>=expr.length()-1 && par<=0)
                    {
                        expr.insert(j+1, ")");
                        break inner2;
                    }

                    if (par<=0 && isOper(expr.charAt(j)))
                    {
                    	if (j==i+1 && (isMinus(expr.charAt(j))||isPlus(expr.charAt(j)))) {
                    		j++;
                    		continue inner2;
                    	}
                        expr.insert(j, ")");
                        break inner2;
                    }
            /*        if (par==-1){
                        i++;
                        continue outer0;
                    } */
                    j++;
                }

            }
            i--;
        }

        i=1;
    outer1:
        while (i<expr.length())
        {
            if (isPow(expr.charAt(i))){
                int par=0;
                int j = i-1;
             inner1:
                while (j>=0)
                {
                    if (isRightPar(expr.charAt(j))) par++;
                    if (isLeftPar(expr.charAt(j))) par--;
                    if (j==0 && par<=0)
                    {
                        expr.insert(j, "(");
                        i++;
                        break inner1;
                    }
                    
                    if (par<=0 && isOper(expr.charAt(j)))
                    {
                        expr.insert(j+1, "(");
                        i++;
                        break inner1;
                    }
        /*            if (par==-1){
                        i++;
                        continue outer1;
                    } */
                    j--;
                }
           
                j = i+1;
                par = 0;
            inner2:
                while (j<expr.length())
                {
                    if (isRightPar(expr.charAt(j))) par--;
                    if (isLeftPar(expr.charAt(j))) par++;
                    if (j>=expr.length()-1 && par<=0)
                    {
                        expr.insert(j+1, ")");
                        break inner2;
                    }
                    
                    if (par<=0 && isOper(expr.charAt(j)))
                    {
                    	if (j==i+1 && (isMinus(expr.charAt(j))||isPlus(expr.charAt(j)))) {
                    		j++;
                    		continue inner2;
                    	}
                        expr.insert(j, ")");
                        break inner2;
                    }
           /*         if (par==-1){
                        i++;
                        continue outer1;
                    } */
                    j++;
                }
            
            }
            i++;
        }

        i=1;
    outer2:
        while (i<expr.length())
        {
            if (isMult(expr.charAt(i))||isDiv(expr.charAt(i))){
                int par=0;
                int j = i-1;
             inner1:
                while (j>=0)
                {
                    if (isRightPar(expr.charAt(j))) par++;
                    if (isLeftPar(expr.charAt(j))) par--;
                    if (j==0 && par<=0)
                    {
                        expr.insert(j, "(");
                        i++;
                        break inner1;
                    }
                    
                    if (par<=0 && isOper(expr.charAt(j)))
                    {
                        expr.insert(j+1, "(");
                        i++;
                        break inner1;
                    }
            /*        if (par==-1){
                        i++;
                        continue outer2;
                    } */
                    j--;
                }

                j = i+1;
                par = 0;
            inner2:
                while (j<expr.length())
                {
                    if (isRightPar(expr.charAt(j))) par--;
                    if (isLeftPar(expr.charAt(j))) par++;
                    if (j>=expr.length()-1 && par<=0)
                    {
                        expr.insert(j+1, ")");
                        break inner2;
                    }
                    
                    if (par<=0 && isOper(expr.charAt(j)))
                    {
                    	if (j==i+1 && (isMinus(expr.charAt(j))||isPlus(expr.charAt(j)))) {
                    		j++;
                    		continue inner2;
                    	}
                        expr.insert(j, ")");
                        break inner2;
                    }
               /*     if (par==-1){
                        i++;
                        continue outer2;
                    } */
                    j++;
                }

            }
            i++;
        }

        i=1;
    outer3:
        while (i<expr.length())
        {
            if (isPlus(expr.charAt(i))||isMinus(expr.charAt(i))){
                int par=0;
                int j = i-1;
             inner1:
                while (j>=0)
                {
                    if (isRightPar(expr.charAt(j))) par++;
                    if (isLeftPar(expr.charAt(j))) par--;
                    if (j==0 && par<=0)
                    {
                        expr.insert(j, "(");
                        i++;
                        break inner1;
                    }
                    
                    if (par<=0 && isOper(expr.charAt(j)))
                    {
                        expr.insert(j+1, "(");
                        i++;
                        break inner1;
                    }
                    if (par==-1 && j==i-1){
                        expr.insert(j+1, "0");
                        i+=2;
                        continue outer3;
                    }
                  /*  if (par==-1){
                        i++;
                        continue outer3;
                    } */
                    j--;
                }

                j = i+1;
                par = 0;
            inner2:
                while (j<expr.length())
                {
                    if (isRightPar(expr.charAt(j))) par--;
                    if (isLeftPar(expr.charAt(j))) par++;
                    if (j>=expr.length()-1 && par<=0)
                    {
                        expr.insert(j+1, ")");
                        break inner2;
                    }
                    
                    if (par<=0 && isOper(expr.charAt(j)))
                    {
                        expr.insert(j, ")");
                        break inner2;
                    }
                /*    if (par==-1){
                        i++;
                        continue outer3;
                    } */
                    j++;
                }
                
            }
            i++;
        }

        String strExpr = expr.toString();
        strExpr = strExpr.replace("(-", "(0-");
        strExpr = strExpr.replace("(+", "(0+");
        
        boolean bad = 
        		strExpr.contains("*)") || strExpr.contains("(*")
        		|| strExpr.contains("/)") || strExpr.contains("(/")
        		|| strExpr.contains("^)") || strExpr.contains("(^")
        		|| strExpr.contains("#)") || strExpr.contains("(#")
        		|| strExpr.contains(":)") || strExpr.contains("(:")
        		|| strExpr.contains("-)") || strExpr.contains("+)");
        
        if (bad) throw new Exception();
        
        return strExpr;
    }

    public Expr parseExpr(String s)
    {
        StringBuilder expr = new StringBuilder(s);

        if (isDigit(expr.charAt(0)))
            return new Expr(this, null,Double.parseDouble(s));
        if (isVar(expr.charAt(0)))
        {
            //expr.deleteCharAt(0);
            //return new Expr(null,getVar(expr.toString()));
            return new Expr(this, null, expr.toString());
        }
        Expr root = new Expr(this, null);
        Expr current = root;
    out:
        for (int i=1; i<expr.length(); )
        {
            /*if (isVar(expr.charAt(i)))
            {
                i++;
                char c = expr.charAt(i);
                StringBuilder name = new StringBuilder();
                while (isLetter(c))
                {
                    name.append(c);
                    i++;
                    //if (i>=expr.length()) break out;
                    c = expr.charAt(i);
                }
                //System.out.println(number.toString());
                Expr newExpr = new Expr(current,
                        Parser.getVar(name.toString()));
                current.setLeftChild(newExpr);
                if (isRightPar(expr.charAt(i)))
                {
                    current.setType(Expr.Type.PLUS);
                    current.setRightChild(new
                            Expr(current,0));
                }
                continue;
            }*/
            if (isLetter(expr.charAt(i)))
            {
                char c = expr.charAt(i);
                StringBuilder name = new StringBuilder();
                while (isLetter(c))
                {
                    name.append(c);
                    i++;
                    //if (i>=expr.length()) break out;
                    c = expr.charAt(i);
                }
                //System.out.println(number.toString());
                Expr newExpr = new Expr(this, current,
                        name.toString());
                current.setLeftChild(newExpr);
                continue;
            }
            if (isDigit(expr.charAt(i)))
            {
                char c = expr.charAt(i);
                StringBuilder number = new StringBuilder();
                while (isDigit(c))
                {
                    number.append(c);
                    i++;
                    //if (i>=expr.length()) break out;
                    c = expr.charAt(i);
                }
                //System.out.println(number.toString());
                Expr newExpr = new Expr(this, current,
                        Double.parseDouble(number.toString()));
                current.setLeftChild(newExpr);
                if (isRightPar(expr.charAt(i)))
                {
                    current.setType(Expr.Type.PLUS);
                    current.setRightChild(new
                            Expr(this, current, 0));
                }
                continue;
            }
            if (isLeftPar(expr.charAt(i)))
            {
                Expr newExpr = new Expr(this, current);
                current.setLeftChild(newExpr);
                current = newExpr;
                i++;
                //if (i>=expr.length()) break out;
                continue;
            }
            if (isOper(expr.charAt(i)))
            {
                if (isPlus(expr.charAt(i)))
                    current.setType(Expr.Type.PLUS);
                if (isMinus(expr.charAt(i)))
                    current.setType(Expr.Type.MINUS);
                if (isMult(expr.charAt(i)))
                    current.setType(Expr.Type.MULT);
                if (isDiv(expr.charAt(i)))
                    current.setType(Expr.Type.DIV);
                if (isPow(expr.charAt(i)))
                    current.setType(Expr.Type.POW);
                if (isFunc(expr.charAt(i)))
                    current.setType(Expr.Type.FUNC);
                if (isPair(expr.charAt(i)))
                    current.setType(Expr.Type.PAIR);
                //current = current.getParent();
                i++;
                //if (i>=expr.length()) break out;
                /*if (isVar(expr.charAt(i)))
                {
                    i++;
                    //if (i>=expr.length()) break out;
                    char c = expr.charAt(i);
                    StringBuilder name = new StringBuilder();
                    while (isLetter(c))
                    {
                        name.append(c);
                        i++;
                        //if (i>=expr.length()) break out;
                        c = expr.charAt(i);
                    }
                    Expr newExpr = new Expr(current,
                            Parser.getVar(name.toString()));
                    current.setRightChild(newExpr);
                }*/
                if (isLetter(expr.charAt(i)))
                {
                    char c = expr.charAt(i);
                    StringBuilder name = new StringBuilder();
                    while (isLetter(c))
                    {
                        name.append(c);
                        i++;
                        //if (i>=expr.length()) break out;
                        c = expr.charAt(i);
                    }
                    Expr newExpr = new Expr(this, current,
                            name.toString());
                    current.setRightChild(newExpr);
                }
                if (isDigit(expr.charAt(i)))
                {
                    char c = expr.charAt(i);
                    StringBuilder number = new StringBuilder();
                    while (isDigit(c))
                    {
                        number.append(c);
                        i++;
                        //if (i>=expr.length()) break out;
                        c = expr.charAt(i);
                    }
                    Expr newExpr = new Expr(this, current,
                            Double.parseDouble(number.toString()));
                    current.setRightChild(newExpr);
                }
                if (isLeftPar(expr.charAt(i)))
                {
                    Expr newExpr = new Expr(this, current);
                    current.setRightChild(newExpr);
                    current = newExpr;
                    i++;
                    //if (i>=expr.length()) break out;
                }
                continue;
            }
            if (isRightPar(expr.charAt(i)))
            {
                current = current.getParent();
                i++;
                //if (i>=expr.length()) break out;
            }
        }
        return root;
    }
}
