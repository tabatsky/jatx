// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 10.10.2014 22:00:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package TaxseeDriver;

import java.io.*;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

// Referenced classes of package TaxseeDriver:
//            TaxseeDriver, a, h, i, 
//            k, o

public final class j
{

    public final void a(int i1)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.f)).append("&zone=").append(i1);
        a(stringbuffer.toString(), o.f);
        return;
        JVM INSTR dup ;
        i1;
        printStackTrace();
    }

    public final void a()
    {
        Object obj = a(o.e);
        a(((StringBuffer) (obj)).toString(), o.e);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void b()
    {
        Object obj = a(o.x);
        a(((StringBuffer) (obj)).toString(), o.x);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void a(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.k)).append("&ID=").append(s);
        a(stringbuffer.toString(), o.k);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void b(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.B)).append("&ID=").append(s);
        a(stringbuffer.toString(), o.B);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void c()
    {
        Object obj = a(o.d);
        a(((StringBuffer) (obj)).toString(), o.d);
        break MISSING_BLOCK_LABEL_29;
        JVM INSTR dup ;
        obj;
        printStackTrace();
        return;
        System.gc();
        return;
    }

    public final void d()
    {
        Object obj = a(o.b);
        a(((StringBuffer) (obj)).toString(), o.b);
          goto _L1
        JVM INSTR dup ;
        obj;
        printStackTrace();
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
_L1:
    }

    public final void c(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.P)).append("&ID=").append(s);
        a(stringbuffer.toString(), o.P);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void e()
    {
        Object obj = a(o.q);
        a(((StringBuffer) (obj)).toString(), o.q);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void f()
    {
        Object obj = a(o.H);
        a(((StringBuffer) (obj)).toString(), o.H);
          goto _L1
        JVM INSTR dup ;
        obj;
        printStackTrace();
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
_L1:
    }

    public final void g()
    {
        Object obj = a(o.I);
        a(((StringBuffer) (obj)).toString(), o.I);
          goto _L1
        JVM INSTR dup ;
        obj;
        printStackTrace();
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
_L1:
    }

    public final void b(int i1)
    {
        o o1;
        if(i1 == 1)
        {
            o1 = o.v;
            (i1 = a(o1)).append("&compact=1");
        } else
        {
            o1 = o.w;
            i1 = a(o1);
        }
        a(i1.toString(), o1);
        return;
        JVM INSTR dup ;
        i1;
        printStackTrace();
    }

    public final void a(String s, o o1)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o1)).append("&order=").append(s);
        a(stringbuffer.toString(), o1);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void d(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.D)).append("&time=").append(s);
        a(stringbuffer.toString(), o.D);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void a(String s, String s1)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.M)).append("&ID=").append(s);
        stringbuffer.append("&hardway=").append(TaxseeDriver.a.a(s1));
        a(stringbuffer.toString(), o.M);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void a(String s, String s1, String s2)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.Q)).append("&ID=").append(s);
        stringbuffer.append("&wait=").append(TaxseeDriver.a.a(s1));
        stringbuffer.append("&luggage=").append(TaxseeDriver.a.a(s2));
        a(stringbuffer.toString(), o.Q);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void b(String s, String s1)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.z)).append("&order=").append(s);
        stringbuffer.append("&newPrice=").append(s1);
        a(stringbuffer.toString(), o.z);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void e(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.c)).append("&zone=").append(s);
        a(stringbuffer.toString(), o.c);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void f(String s)
    {
        Object obj;
        ((StringBuffer) (obj = a(o.K))).append("&query=").append(TaxseeDriver.a.a(s));
        a(((StringBuffer) (obj)).toString(), o.K);
          goto _L1
        JVM INSTR dup ;
        s;
        printStackTrace();
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
_L1:
    }

    public final void g(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.L)).append("&ID=").append(s);
        a(stringbuffer.toString(), o.L);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void c(String s, String s1)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.g)).append("&zone=").append(s);
        stringbuffer.append("&time=").append(s1);
        a(stringbuffer.toString(), o.g);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void h(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.h)).append("&city=").append(s);
        a(stringbuffer.toString(), o.h);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void i(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.C)).append("&id=").append(s);
        a(stringbuffer.toString(), o.C);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void j(String s)
    {
        StringBuffer stringbuffer;
        o o1;
        if(s.equals(""))
            o1 = o.y;
        else
            o1 = o.i;
        stringbuffer = a(o1);
        if(!s.equals(""))
            stringbuffer.append("&zone=").append(s);
        a(stringbuffer.toString(), o1);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void d(String s, String s1)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.l)).append("&ID=").append(s);
        stringbuffer.append("&time=").append(s1);
        a(stringbuffer.toString(), o.l);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void h()
    {
        Object obj = a(o.n);
        a(((StringBuffer) (obj)).toString(), o.n);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void e(String s, String s1)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.A)).append("&to=").append(s);
        stringbuffer.append("&sum=").append(s1);
        a(stringbuffer.toString(), o.A);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void a(String s, String s1, String s2, String s3)
    {
        StringBuffer stringbuffer;
        s3 = s3.equals("R") ? ((String) (o.O)) : ((String) (o.N));
        stringbuffer = a(((o) (s3)));
        if(s != null)
            stringbuffer.append("&street=").append(s);
        if(s1 != null)
            stringbuffer.append("&address=").append(s1);
        if(s2 != null)
            stringbuffer.append("&house=").append(TaxseeDriver.a.a(s2));
        a(stringbuffer.toString(), ((o) (s3)));
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void i()
    {
        Object obj = a(o.o);
        a(((StringBuffer) (obj)).toString(), o.o);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void k(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.p)).append("&text=").append(TaxseeDriver.a.a(s));
        a(stringbuffer.toString(), o.p);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void l(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.G)).append("&ID=").append(s);
        a(stringbuffer.toString(), o.G);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void m(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.u)).append("&message=").append(TaxseeDriver.a.a(s));
        a(stringbuffer.toString(), o.u);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void n(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.E)).append("&ID=").append(s);
        a(stringbuffer.toString(), o.E);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void j()
    {
        Object obj = a(o.j);
        a(((StringBuffer) (obj)).toString(), o.j);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void k()
    {
        Object obj = a(o.r);
        a(((StringBuffer) (obj)).toString(), o.r);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void l()
    {
        Object obj = a(o.s);
        a(((StringBuffer) (obj)).toString(), o.s);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void m()
    {
        Object obj = a(o.t);
        a(((StringBuffer) (obj)).toString(), o.t);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    public final void o(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.S)).append("&callsign=").append(s);
        a(stringbuffer.toString(), o.S);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void p(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.J)).append("&ID=").append(s);
        a(stringbuffer.toString(), o.J);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void n()
    {
        Object obj = a(o.R);
        a(((StringBuffer) (obj)).toString(), o.R);
        return;
        JVM INSTR dup ;
        obj;
        printStackTrace();
    }

    private StringBuffer a(o o1)
    {
        StringBuffer stringbuffer;
        if(o1.equals(o.F))
            stringbuffer = new StringBuffer(d);
        else
            stringbuffer = new StringBuffer(b);
        stringbuffer.append(o1.toString());
        o1 = a_java_lang_String_fld != null ? ((o) (a_java_lang_String_fld)) : "<dummy>";
        stringbuffer.append("?t=" + o1);
        return stringbuffer;
    }

    public final void a(String s, String s1, String s2, String s3, String s4, String s5)
    {
        StringBuffer stringbuffer;
        (stringbuffer = new StringBuffer(b)).append(o.a.toString());
        stringbuffer.append("?callsign=" + s3 + s);
        stringbuffer.append("&password=" + s1);
        stringbuffer.append("&city=" + (s4.equals("0") ? "" : s4));
        stringbuffer.append("&version=" + s2);
        stringbuffer.append("&platform=j");
        if(s5 != null)
            stringbuffer.append("&j2meplatform=" + TaxseeDriver.a.a(s5));
        a(stringbuffer.toString(), o.a);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public j(TaxseeDriver taxseedriver, int i1)
    {
        a_int_fld = 1;
        b = "http://wds.taximaxim.ru/Services/Driver.svc/";
        new StringBuffer(512);
        c = "wds.taximaxim.ru:8294";
        a_java_lang_Object_fld = new Object();
        d = "http://wds.taximaxim.ru/Services/Driver.svc/";
        a_TaxseeDriver_TaxseeDriver_fld = taxseedriver;
        new h(a_TaxseeDriver_TaxseeDriver_fld);
        a_TaxseeDriver_i_fld = new i(a_TaxseeDriver_TaxseeDriver_fld, this);
        if(i1 == 1)
            a_int_fld = i1;
    }

    private String a(String s, o o1)
    {
        String s2;
        a_TaxseeDriver_TaxseeDriver_fld.a(false);
        System.out.println("GetHTTPData >>");
        if(a_int_fld != 1)
            break MISSING_BLOCK_LABEL_119;
        if(a_TaxseeDriver_k_fld == null)
        {
            a_TaxseeDriver_k_fld = new k(c, this, a_TaxseeDriver_i_fld);
            Thread thread;
            (thread = new Thread(a_TaxseeDriver_k_fld)).start();
        }
        String s1;
        s2 = (s1 = s.substring(7)).substring(s1.indexOf('/'));
        System.out.println(s);
        a_TaxseeDriver_k_fld.a(s2, o1);
        break MISSING_BLOCK_LABEL_109;
        s;
        throw s;
        System.out.println("GetHTTPData >>");
        return null;
        HttpConnection httpconnection;
        httpconnection = null;
        s2 = s;
        s = null;
        o1 = new StringBuffer();
        try
        {
            (httpconnection = (HttpConnection)Connector.open(s2, 1, true)).setRequestMethod("GET");
            int i1;
            if((i1 = httpconnection.getResponseCode()) == 401)
                a_TaxseeDriver_TaxseeDriver_fld.b();
            else
            if(i1 == 500)
                a_TaxseeDriver_TaxseeDriver_fld.a();
            if(i1 == 200)
            {
                s = new InputStreamReader(httpconnection.openDataInputStream(), "UTF-8");
                while((i1 = s.read()) != -1) 
                    o1.append((char)i1);
            } else
            {
                System.out.println("Error in opening HTTP Connection. Error#" + i1);
            }
        }
        catch(IOException ioexception)
        {
            a_TaxseeDriver_TaxseeDriver_fld.c();
            throw ioexception;
        }
        if(s != null)
            s.close();
        if(httpconnection != null)
            httpconnection.close();
        synchronized(a_java_lang_Object_fld) { }
        break MISSING_BLOCK_LABEL_356;
        o1;
        if(s != null)
            s.close();
        if(httpconnection != null)
            httpconnection.close();
        synchronized(a_java_lang_Object_fld) { }
        throw o1;
        return ioexception = o1.toString();
    }

    final void f(String s, String s1)
    {
        StringBuffer stringbuffer;
        (stringbuffer = a(o.F)).append("&callsign=").append(s);
        stringbuffer.append("&version=").append(s1);
        a(stringbuffer.toString(), o.F);
        return;
        JVM INSTR dup ;
        s;
        printStackTrace();
    }

    public final void g(String s, String s1)
    {
        b = s;
        c = s1;
    }

    final void q(String s)
    {
        a_java_lang_String_fld = s;
    }

    final void o()
    {
        if(a_TaxseeDriver_k_fld != null)
            a_TaxseeDriver_k_fld.a();
    }

    final void p()
    {
        if(a_TaxseeDriver_k_fld != null)
            a_TaxseeDriver_k_fld.b();
    }

    public final void q()
    {
        a_TaxseeDriver_TaxseeDriver_fld.b();
    }

    private int a_int_fld;
    private TaxseeDriver a_TaxseeDriver_TaxseeDriver_fld;
    private String a_java_lang_String_fld;
    private i a_TaxseeDriver_i_fld;
    private String b;
    private k a_TaxseeDriver_k_fld;
    private String c;
    private Object a_java_lang_Object_fld;
    private String d;
}
