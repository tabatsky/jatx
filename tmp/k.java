// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 10.10.2014 22:01:03
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package TaxseeDriver;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

// Referenced classes of package TaxseeDriver:
//            i, j, q, t, 
//            o

public final class k
    implements Runnable
{

    public final void a()
    {
        a_boolean_fld = false;
    }

    public final synchronized void run()
    {
_L3:
        if(!a_boolean_fld) goto _L2; else goto _L1
_L1:
        if(b_boolean_fld)
        {
            b_boolean_fld = false;
            d();
            c();
        } else
        if(!a_java_util_Vector_fld.isEmpty())
        {
            t t1;
            synchronized(a_java_lang_Object_fld)
            {
                t1 = (t)a_java_util_Vector_fld.elementAt(0);
            }
            q q1 = a(t1);
            synchronized(a_java_lang_Object_fld)
            {
                a_java_util_Vector_fld.removeElement(t1);
                if(q1 != null)
                {
                    System.out.println(t1.a_java_lang_String_fld + " ---- " + t1.a_TaxseeDriver_o_fld);
                    if(q.a(q1) == 401)
                        a_TaxseeDriver_j_fld.q();
                    else
                        a_TaxseeDriver_i_fld.a(q1.a(), t1.a_TaxseeDriver_o_fld);
                } else
                {
                    System.out.println("Request failed after 3 retries");
                    d();
                    c();
                }
            }
        }
        synchronized(a_java_lang_Object_fld)
        {
            a_java_lang_Object_fld.wait(50L);
        }
          goto _L3
        JVM INSTR dup ;
        InterruptedException interruptedexception;
        interruptedexception;
        printStackTrace();
          goto _L3
_L2:
    }

    final void b()
    {
        b_boolean_fld = true;
    }

    public k(String s, j j1, i l)
    {
        a_java_lang_String_fld = "";
        a_java_util_Vector_fld = new Vector();
        b_java_lang_String_fld = "";
        a_java_lang_StringBuffer_fld = new StringBuffer(256);
        a_TaxseeDriver_j_fld = null;
        a_boolean_fld = true;
        b_boolean_fld = false;
        c = false;
        a_java_lang_String_fld = "socket://" + s;
        b_java_lang_String_fld = s;
        System.out.println("host:" + s);
        a_TaxseeDriver_j_fld = j1;
        a_TaxseeDriver_i_fld = l;
        c();
    }

    private synchronized void c()
    {
        System.out.println("openConn >>");
        c = false;
        a_javax_microedition_io_SocketConnection_fld = (SocketConnection)Connector.open(a_java_lang_String_fld, 3, true);
        a_java_io_OutputStream_fld = a_javax_microedition_io_SocketConnection_fld.openOutputStream();
        a_java_io_DataOutputStream_fld = new DataOutputStream(a_java_io_OutputStream_fld);
        a_java_io_InputStream_fld = a_javax_microedition_io_SocketConnection_fld.openInputStream();
        a_java_io_DataInputStream_fld = new DataInputStream(a_java_io_InputStream_fld);
        c = true;
        System.out.println("openConn: done");
        break MISSING_BLOCK_LABEL_120;
        JVM INSTR dup ;
        IOException ioexception;
        ioexception;
        printStackTrace();
        c = false;
        d();
        b_boolean_fld = true;
        System.out.println("Disconnected");
        return;
    }

    private synchronized void d()
    {
        c = false;
        System.out.println(">> resetConn");
        try
        {
            if(a_java_io_DataInputStream_fld != null)
                a_java_io_DataInputStream_fld.close();
        }
        catch(Exception _ex) { }
        try
        {
            if(a_java_io_DataOutputStream_fld != null)
                a_java_io_DataOutputStream_fld.close();
        }
        catch(Exception _ex) { }
        try
        {
            if(a_java_io_OutputStream_fld != null)
                a_java_io_OutputStream_fld.close();
        }
        catch(Exception _ex) { }
        try
        {
            if(a_java_io_InputStream_fld != null)
                a_java_io_InputStream_fld.close();
        }
        catch(Exception _ex) { }
        try
        {
            if(a_javax_microedition_io_SocketConnection_fld != null)
                a_javax_microedition_io_SocketConnection_fld.close();
            return;
        }
        catch(Exception _ex)
        {
            return;
        }
    }

    public final void a(String s, o o1)
    {
        s = new t(this, s, o1);
        synchronized(a_java_lang_Object_fld)
        {
            boolean flag = false;
            if(!a_java_util_Vector_fld.isEmpty())
            {
                Enumeration enumeration = a_java_util_Vector_fld.elements();
                do
                {
                    if(!enumeration.hasMoreElements())
                        break;
                    t t1;
                    if((t1 = (t)enumeration.nextElement()).a_TaxseeDriver_o_fld.equals(o1))
                        flag = true;
                } while(true);
            }
            if(!flag)
                a_java_util_Vector_fld.addElement(s);
        }
        synchronized(a_java_lang_Object_fld)
        {
            a_java_lang_Object_fld.notify();
        }
    }

    private synchronized q a(t t1)
    {
        Object obj;
        q q1;
        int l;
        if(!c)
            return null;
        obj = 0;
        q1 = new q(this);
        l = 0;
_L7:
        StringBuffer stringbuffer;
        if(obj != 0)
            break; /* Loop/switch isn't completed */
        if(l > 3)
            return null;
        l++;
        stringbuffer = new StringBuffer();
        q1 = new q(this);
        try
        {
            if(a_java_lang_StringBuffer_fld.length() > 0)
                a_java_lang_StringBuffer_fld.delete(0, a_java_lang_StringBuffer_fld.length() - 1);
            a_java_lang_StringBuffer_fld.append("GET http://");
            a_java_lang_StringBuffer_fld.append(b_java_lang_String_fld);
            a_java_lang_StringBuffer_fld.append(t1.a_java_lang_String_fld);
            a_java_lang_StringBuffer_fld.append(" HTTP/1.1 \r\nHost: ");
            a_java_lang_StringBuffer_fld.append(b_java_lang_String_fld);
            a_java_lang_StringBuffer_fld.append("\r\nConnection: keep-alive\r\nUser-Agent: TaxseeDriver\r\n\r\n\r\n\r\n");
            obj = a_java_lang_StringBuffer_fld.toString().getBytes("ISO-8859-1");
            a_java_io_DataOutputStream_fld.write(((byte []) (obj)), 0, obj.length);
            a_java_io_DataOutputStream_fld.flush();
            int j1 = 0;
            int k1 = 0;
            for(int i1 = 0; i1 != -1 && (k1 == 0 || k1 != 0 && j1 <= k1); j1++)
            {
                i1 = a_java_io_DataInputStream_fld.read();
                stringbuffer.append((char)i1);
                if(!stringbuffer.toString().endsWith("\r\n\r\n"))
                    continue;
                obj = stringbuffer.toString();
                stringbuffer.delete(0, stringbuffer.length() - 1);
                k1 = Integer.parseInt(j1 = (j1 = (j1 = (j1 = ((int) (obj))).substring(j1.indexOf("Content-Length: "))).substring(16)).substring(0, j1.indexOf("\r")));
                q1.a((j1 = ((int) (obj))).startsWith("HTTP") ? Integer.parseInt(j1 = j1.substring(9, 12)) : 401);
                j1 = 0;
                if(k1 == 0)
                    break;
            }

            obj = 1;
            break MISSING_BLOCK_LABEL_470;
        }
        catch(IOException ioexception)
        {
            System.err.println("Exception caught:" + ioexception);
            obj = 0;
        }
        if(q1.a() != 500) goto _L2; else goto _L1
_L1:
        Thread.sleep(5000L);
          goto _L3
        JVM INSTR dup ;
        Object obj1;
        obj1;
        printStackTrace();
          goto _L3
_L2:
        if(q1.a() != 401) goto _L5; else goto _L4
_L4:
        obj1 = q1;
        return ((q) (obj1));
_L5:
        if(q1.a() != 200)
            return null;
_L3:
        b_boolean_fld = true;
        stringbuffer.delete(0, 0x10000);
        break MISSING_BLOCK_LABEL_470;
        t1;
        throw t1;
        try
        {
            byte abyte0[] = stringbuffer.toString().getBytes("ISO-8859-1");
            String s = (new String(abyte0, "UTF-8")).trim();
            q1.a(s);
        }
        catch(Exception _ex)
        {
            q1.a(stringbuffer.toString());
        }
        if(true) goto _L7; else goto _L6
        t1;
        throw t1;
_L6:
        return q1;
    }

    private SocketConnection a_javax_microedition_io_SocketConnection_fld;
    private OutputStream a_java_io_OutputStream_fld;
    private DataOutputStream a_java_io_DataOutputStream_fld;
    private InputStream a_java_io_InputStream_fld;
    private DataInputStream a_java_io_DataInputStream_fld;
    private String a_java_lang_String_fld;
    private Vector a_java_util_Vector_fld;
    private String b_java_lang_String_fld;
    private StringBuffer a_java_lang_StringBuffer_fld;
    private j a_TaxseeDriver_j_fld;
    private final Object a_java_lang_Object_fld = new Object();
    private i a_TaxseeDriver_i_fld;
    private boolean a_boolean_fld;
    private boolean b_boolean_fld;
    private boolean c;
}
