package NetworkPrograming34;

import java.io.*;

public class NetP4_11 {
    public static void main(String[] args) {
        String buf;
        FileInputStream fin = null;
        FileOutputStream fout = null;

        if (args.length != 2) {
            System.out.println("소스파일 및 대상파일을 지정하십시오.");
            System.exit(1);
        }

        try{
            fin = new FileInputStream(args[0]);
            fout = new FileOutputStream(args[1]);
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
            }catch (IOException e){
                System.err.println(e.getMessage());
                break;
            }

            buf = num++ + " : " + buf; // [ 번호 : 프로그램 내용 ] 형식
            ps.println(buf);

        }

        try {
            fin.close();
            fout.close();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
