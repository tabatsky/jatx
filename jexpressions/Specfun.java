package jatx.jexpressions;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




/**
 *
 * @author jatx
 */
public class Specfun {
    private static final double pi = 3.141592653589793238;
    private static final double euler = 0.577215664901532860;
    private static final double zeta[] = {
        1.644934066848226436, //2
        1.202056903159594285, //3
        1.082323233711138191, //4
        1.036927755143369926, //5
        1.017343061984449139, //6
        1.008349277381922826, //7
        1.004077356197944339, //8
        1.002008392826082214, //9
        1.000994575127818085, //10
        1.000494188604119464, //11
        1.000246086553308048, //12
        1.000122713347578489, //13
        1.000061248135058704, //14
        1.000030588236307020, //15
        1.000015282259408651, //16
        1.000007637197637899, //17
        1.000003817293264999, //18
        1.000001908212716553, //19
        1.000000953962033872, //20
        1.000000476932986787, //21
        1.000000238450502727, //22
        1.000000119219925965, //23
        1.000000059608189051, //24
        1.000000029803503514, //25
        1.000000014901554828, //26
        1.000000007450711789, //27
        1.000000003725334024, //28
        1.000000001862659723, //29
        1.000000000931327432, //30
    };

    public static double lngamma(double x){
        double t=x-1;
        double sum=-Math.log(x)+t*(1-euler);
        double pow=t*t;
        int n;
        for(n=2; n<=30; n++){
            sum+=(zeta[n-2]-1)*pow/n;
            pow*=(-t);
        }  
        return sum;
    }

    public static double eulergamma(double x){
        if((x<=0.0)&&(Math.round(x)==x)){
            System.out.print("\nargument of gamma cannot be zero or negative integer\n");
            return Double.NaN;
        }
        
        //if(x>1.5) return (x-1)*eulergamma(x-1);
        //if(x<0.5) return eulergamma(x+1)/x;
        
        if (x>180) return Double.POSITIVE_INFINITY;
        if (x<-180) {
        	int xIntAbs = (int)(-x);
        	int sign = 2*(xIntAbs%2)-1; 
        	return 0.0*sign;
        }
        
        double mult = 1.0;
        while (x>1.5) {
        	mult *= x-1;
        	x -= 1;
        }
        while (x<0.5) {
        	mult /= x;
        	x += 1;
        }
  
        return mult*Math.exp(lngamma(x));
    }

    public static double eulerbeta(double x,double y){
        return eulergamma(x)*eulergamma(y)/eulergamma(x+y);
    }

    public static double besselJ(double nu,double x){
        double next,sum,next1,next2,sum1,sum2,int1,int2,yp,y,yn,h;
        long m,N;
        if (x<0) return Double.NaN;
        if((nu<0)&&(Math.round(nu)==nu))
            if(((int)Math.round(nu))%2==1) return -besselJ(-nu,x);
            else return besselJ(-nu,x);
        if((nu==0.0)&&(x==0.0)) return 1.0;
        if(x==0.0) return 0.0;
        // power series
        if((x<=15)||(x*x<15*nu)){
            next=Math.pow(x/2,nu)/eulergamma(nu+1);
            sum=next;
            //int max = (int)(200>5*nu?200*x:5*nu*x);
            int max = 200;
            for(m=1; m<max; m++){
                next*=(-1)*(x*x/4)/(m*(m+nu));
                sum+=next;
            }
            return sum;
        } else if (8*x>(nu*nu)) {
        // asymptotic expansion
        //if((x>30)&&(x>(3*(nu*nu-0.25)))){
            next1=1;
            sum1=next1;
            next2=(4*nu*nu-1)/(8*x);
            sum2=next2;
            int max = 10>nu?10:(int)nu;
            for(m=1; m<=max; m++){
                next1*=-(4*nu*nu-(4*m-3)*(4*m-3))*(4*nu*nu-(4*m-1)*(4*m-1))/((2*m-1)*2*m*8*x*8*x);
                sum1+=next1;
                next2*=-(4*nu*nu-(4*m-1)*(4*m-1))*(4*nu*nu-(4*m+1)*(4*m+1))/(2*m*(2*m+1)*8*x*8*x);
                sum2+=next2;
            }
            return Math.sqrt(2/(pi*x))*(Math.cos(x-pi*nu/2-pi/4)*sum1-Math.sin(x-pi*nu/2-pi/4)*sum2);
        } 
        //integral
        N=(long)40*Math.round(x);
        h=pi/N;
        yp=1;
        y=Math.cos(nu*h-x*Math.sin(h));
        yn=Math.cos(nu*2*h-x*Math.sin(2*h));
        int1=h/3*(yp+4*y+yn);
        for(m=3; m<N; m+=2){
            yp=yn;
            y=Math.cos(nu*m*h-x*Math.sin(m*h));
            yn=Math.cos(nu*(m+1)*h-x*Math.sin((m+1)*h));
            int1+=h/3*(yp+4*y+yn);
        }
        if(nu==Math.round(nu)) return int1/pi;
        h=0.0004;
        yp=1;
        y=Math.exp(-x*Math.sinh(h)-nu*h);
        yn=Math.exp(-x*Math.sinh(2*h)-nu*h);
        int2=h/3*(yp+4*y+yn);
        for(m=3; m<5000; m+=2){
            yp=yn;
            y=Math.exp(-x*Math.sinh(m*h)-nu*m*h);
            yn=Math.exp(-x*Math.sinh((m+1)*h)-nu*(m+1)*h);
            int2+=h/3*(yp+4*y+yn);
        }
        return int1/pi-Math.sin(nu*pi)*int2/pi;
    }

    public static double besselY(double nu,double x){
        double next1,next2,sum1,sum2,int1,int2,yp,y,yn,h;
        long m,N,sign;
        if (x<=0) return Double.NaN;
        if((nu<0)&&(Math.round(nu)==nu))
            if(((int)Math.round(nu))%2==1) return -besselY(-nu,x);
            else return besselY(-nu,x);
        if(Math.round(nu)!=nu)
            return (besselJ(nu,x)*Math.cos(nu*pi)
                    -besselJ(-nu,x))/Math.sin(nu*pi);
        return (besselY(nu+1e-3,x)+besselY(nu-1e-3,x))/2;
        
    }

    public static double besselI(double nu,double x){
        double next,sum,next1,next2,sum1,sum2,int1,int2,yp,y,yn,h;
        long m,N;
        if (x<0) return Double.NaN;
        if((nu<0)&&(Math.round(nu)==nu))
            if(((int)Math.round(nu))%2==1) return -besselI(-nu,x);
            else return besselI(-nu,x);
        if((nu==0.0)&&(x==0.0)) return 1.0;
        if(x==0.0) return 0.0;
        // power series
        if((x<=15)||(x*x<15*nu)){
            next=Math.pow(x/2,nu)/eulergamma(nu+1);
            sum=next;
            int max = (int)(40*x);
            for(m=1; m<200; m++){
                next*=(x*x/4)/(m*(m+nu));
                sum+=next;
            }
            return sum;
        } else if (8*x>(nu*nu)) {
        // asymptotic expansion
        //if((x>30)&&(x>(3*(nu*nu-0.25)))){
            next1=1;
            sum1=next1;
            //next2=(4*nu*nu-1)/(8*x);
            //sum2=next2;
            int max = 10>nu?10:(int)nu;
            for(m=1; m<=max; m++){
                next1*=-(4*nu*nu-(2*m-1)*(2*m-1))/(m*8*x);
                sum1+=next1;
                //next2*=-(4*nu*nu-(4*m-1)*(4*m-1))*(4*nu*nu-(4*m+1)*(4*m+1))/(2*m*(2*m+1)*8*x*8*x);
                //sum2+=next2;
            }
            return Math.exp(x)/Math.sqrt(2*pi*x)*sum1;
        }
        //integral
        N=(long)40*Math.round(x);
        h=pi/N;
        yp=Math.exp(x);
        y=Math.exp(x*Math.cos(h))*Math.cos(nu*h);
        yn=Math.exp(x*Math.cos(2*h))*Math.cos(nu*2*h);
        int1=h/3*(yp+4*y+yn);
        for(m=3; m<N; m+=2){
            yp=yn;
            y=Math.exp(x*Math.cos(m*h))*Math.cos(nu*m*h);
            yn=Math.exp(x*Math.cos((m+1)*h))*Math.cos(nu*(m+1)*h);
            int1+=h/3*(yp+4*y+yn);
        }
        if(nu==Math.round(nu)) return int1/pi;
        h=0.0004;
        yp=1;
        y=Math.exp(-x*Math.cosh(h)-nu*h);
        yn=Math.exp(-x*Math.cosh(2*h)-nu*h);
        int2=h/3*(yp+4*y+yn);
        for(m=3; m<5000; m+=2){
            yp=yn;
            y=Math.exp(-x*Math.cosh(m*h)-nu*m*h);
            yn=Math.exp(-x*Math.cosh((m+1)*h)-nu*(m+1)*h);
            int2+=h/3*(yp+4*y+yn);
        }
        return int1/pi-Math.sin(nu*pi)*int2/pi;
    }

    public static double besselK(double nu,double x){
    	double next1,next2,sum1,sum2,int1,int2,yp,y,yn,h;
        long m,N,sign;
        if(Math.round(nu)!=nu)
            return pi*(besselI(-nu,x)-besselI(nu,x))/(2*Math.sin(nu*pi));
        return (besselK(nu+1e-3,x)+besselK(nu-1e-3,x))/2;
    } 

    public static double airyAi(double x){
        if(x==0) return 0.35502805388781719;
        if(x>0){
            return 1/pi*Math.sqrt(x/3)
                    *besselK(1.0/3,2.0/3*x*Math.sqrt(x));
        }else{
            return 1.0/3*Math.sqrt(-x)
                    *(besselJ(1.0/3,2.0/3*(-x)*Math.sqrt(-x))
                    +besselJ(-1.0/3,2.0/3*(-x)*Math.sqrt(-x)));
        }
    }

    public static double airyBi(double x){
        if(x==0) return 0.61492662744600065;
        if(x>0){
            return Math.sqrt(x/3)*
                    (besselI(1.0/3,2.0/3*x*Math.sqrt(x))
                    +besselI(-1.0/3,2.0/3*x*Math.sqrt(x)));
        }else{
            return Math.sqrt(-x/3)
                    *(besselJ(-1.0/3,2.0/3*(-x)*Math.sqrt(-x))
                    -besselJ(1.0/3,2.0/3*(-x)*Math.sqrt(-x)));
        }
    }
}
