package NetworkPrograming45;

import java.io.*;

public class NetP45_1 {
    public static void main(String[] args) {
        String buf;
        FileInputStream fin = null;
        FileOutputStream fout = null;

        if (args.length != 1) {
            System.out.println("파일 지정하십시오.");
            System.exit(1);
        }

        try{
            fin = new FileInputStream(args[0]);
            fout = new FileOutputStream("numbered_" + args[0]);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.exit(1);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fin));
        PrintStream ps = new PrintStream(fout);
        int num = 1;
        while(true){
            try {
                buf = br.readLine();

                if (buf == null) break;

                buf = num++ + " : " + buf; // [ 번호 : 프로그램 내용 ] 형식
                ps.println(buf);
                System.out.write(buf.getBytes());
                System.out.println();
            }catch (IOException e){
                System.err.println(e.getMessage());
                break;
            }
        }

        try {
            fin.close();
            fout.close();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
