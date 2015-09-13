package jatx.jexpressions;

public class Expr
{
    public enum Type {PLUS,MINUS,MULT,DIV,POW,NUM,FUNC,PAIR,VAR}

    private Type type;

    Expr parentExpr;
    Expr leftChild;
    Expr rightChild;

    double value = 0;
    String var = "x";
    String func = "sin";

    Parser parser;
    
    public Expr(Parser pars, Expr par)
    {
       parser = pars;
       parentExpr = par;
       leftChild = null;
       rightChild = null;
       type = Type.PLUS;
    }

    public Expr(Parser pars, Expr par, double value)
    {
    	parser = pars;
        parentExpr = par;
        leftChild = null;
        rightChild = null;
        type = Type.NUM;
        this.value = value;
    }

    public Expr(Parser pars, Expr par, String str)
    {
    	parser = pars;
        parentExpr = par;
        leftChild = null;
        rightChild = null;
        if (str.charAt(0)=='$') {
        	type = Type.VAR;
        	this.var = str.replace("$","");
        } else {
        	type = Type.FUNC;
        	this.func = str;
        }
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public void setLeftChild(Expr left)
    {
        leftChild = left;
    }

    public void setRightChild(Expr right)
    {
        rightChild = right;
    }

    public Expr getParent()
    {
        return parentExpr;
    }

    public double evalFunc(String func, Expr value)
    {
    	if (func.equals("var")) return parser.getVar((int)(value.evaluate()+0.1));  
    	if (func.equals("E"))	return Math.E;
    	if (func.equals("PI"))	return Math.PI;
        if (func.equals("sin")) return Math.sin(value.evaluate());
        if (func.equals("cos")) return Math.cos(value.evaluate());
        if (func.equals("tan")) return Math.tan(value.evaluate());
        if (func.equals("exp")) return Math.exp(value.evaluate());
        if (func.equals("log")) return Math.log(value.evaluate());
        if (func.equals("sinh")) return Math.sinh(value.evaluate());
        if (func.equals("cosh")) return Math.cosh(value.evaluate());
        if (func.equals("tanh")) return Math.tanh(value.evaluate());
        if (func.equals("abs")) return Math.abs(value.evaluate());
        if (func.equals("sqrt")) return Math.sqrt(value.evaluate());
        if (func.equals("acos")) return Math.acos(value.evaluate());
        if (func.equals("asin")) return Math.asin(value.evaluate());
        if (func.equals("atan")) return Math.atan(value.evaluate());
        if (func.equals("signum")) return Math.signum(value.evaluate());
        if (func.equals("gamma")) return Specfun.eulergamma(value.evaluate());
        if (func.equals("airyAi")) return Specfun.airyAi(value.evaluate());
        if (func.equals("airyBi")) return Specfun.airyBi(value.evaluate());
        while (value.rightChild == null) value = value.leftChild;
        if (func.equals("beta"))
        return Specfun.eulerbeta(value.leftChild.evaluate(),
                value.rightChild.evaluate());
        if (func.equals("besselJ"))
        return Specfun.besselJ(value.leftChild.evaluate(),
                value.rightChild.evaluate());
        if (func.equals("besselY"))
        return Specfun.besselY(value.leftChild.evaluate(),
                value.rightChild.evaluate());
        if (func.equals("besselI"))
        return Specfun.besselI(value.leftChild.evaluate(),
                value.rightChild.evaluate());
        if (func.equals("besselK"))
        return Specfun.besselK(value.leftChild.evaluate(),
                value.rightChild.evaluate());
        System.out.println("Wrong function");
        return 0;
    }

    public double evaluate()
    {
        if (type!=Type.NUM && rightChild == null) return leftChild.evaluate();
        if (type == Type.NUM) 
        {
            //System.out.print(value+" ");
            return value;
        }
        if (type == Type.PLUS)
        {
            //System.out.print("+ ");
            return leftChild.evaluate()+rightChild.evaluate();
        }
        if (type == Type.MINUS)
        {
            //System.out.print("- ");
            return leftChild.evaluate()-rightChild.evaluate();
        }
        if (type == Type.MULT)
        {
            //System.out.print("* ");
            return leftChild.evaluate()*rightChild.evaluate();
        }
        if (type == Type.DIV)
        {
            //System.out.print("/ ");
            return leftChild.evaluate()/rightChild.evaluate();
        }
        if (type == Type.POW)
        {
            //System.out.print("^ ");
            return Math.pow(leftChild.evaluate(),rightChild.evaluate());
        }
        if (type == Type.FUNC)
        {
            //System.out.print("# "+leftChild.func+" ");
            return evalFunc(leftChild.func,rightChild);
        }
       /* if (type == Type.VAR)
        {
        	System.out.print("$"+var);
        	return Parser.getVar(var);
        } */
        if (type == Type.PAIR)
        {
            //System.out.print("NaN ");
            return Double.NaN;
        }
        return 0;
    }
}

