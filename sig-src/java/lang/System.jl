package java.lang;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.OutputStream;

public class System {
    public final static PrintStream out =
        new PrintStream(new OutputStream() {
            public void write(int b) { } 
            public void print(String s) { } 
            public void print(char c) { } 
            public void println(String s) { } }
        );

    public final static PrintStream err =
        new PrintStream(new OutputStream() {
            public void write(int b) { }
            public void print(String s) { } 
            public void print(char c) { } 
            public void println(String s) { } }
        );

    public final static InputStream in =
        new InputStream() { public int read() { return -1; }};

        public static long currentTimeMillis(){ return -1; }

        public static void exit(int i) { }

        public static void arraycopy(Object src,  int  srcPos,
                Object dest, int destPos, int length) {
            if (src instanceof Object[] && dest instanceof Object[]) {                
                ((Object[])dest)[destPos] = ((Object[])src)[srcPos]; 
                ((Object[])dest)[destPos+length] = ((Object[])src)[srcPos+length]; 
            }
            else  if (src instanceof int[] && dest instanceof int[]) {                
                ((int[])dest)[destPos] = ((int[])src)[srcPos]; 
                ((int[])dest)[destPos+length] = ((int[])src)[srcPos+length]; 
            }
        }

}
