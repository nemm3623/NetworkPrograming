package NetworkPrograming45;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class NetP45_2 {
    public static void main(String[] args)  {
        boolean same = true;


        try{
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            BufferedReader br1 = new BufferedReader(new FileReader(args[1]));

            while(br.ready() && br1.ready()){
                if(!Objects.equals(br.readLine(), br1.readLine())){
                    same = false;
                    break;
                }
            }

        }catch (FileNotFoundException e){
            System.err.println("파일을 찾을 수 없습니다.");
        }catch (IOException e){
            System.err.println(e.getMessage());
        }

        if(same){
            File f = new File(args[0]);
            File f1 = new File(args[1]);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 EEEE HH시 mm분 ss초");

            System.out.println("파일 1 :" + formatter.format(f.lastModified()) + "파일 2 : " + formatter.format(f1.lastModified()));
        }
        else {
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("newfile.txt"))) {
                BufferedReader br = new BufferedReader(new FileReader(args[0]));
                BufferedReader br1 = new BufferedReader(new FileReader(args[1]));

                while(br.ready()) {
                    bw.write(br.readLine() + "\n");
                }
                while(br1.ready()) {
                    bw.write(br1.readLine() + "\n");
                }
                bw.flush();
            }catch (IOException e){
                System.err.println(e.getMessage());
            }
        }
    }
}
