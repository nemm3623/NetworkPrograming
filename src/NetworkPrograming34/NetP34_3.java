package NetworkPrograming34;

import java.io.*;

public class NetP34_3 {

    public static void main(String[] args) {
        String f = "test.txt";

        // 키보드 입력을 받아 파일에 저장
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
             BufferedWriter bw = new BufferedWriter(writer);
             InputStreamReader reader = new InputStreamReader(System.in, "UTF-8");
             BufferedReader br = new BufferedReader(reader)) {

            System.out.println("텍스트를 입력하세요. 종료를 원할 경우 stop을 입력하세요. ");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("stop"))
                    break;
                bw.write(line);
                bw.newLine();

            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // 저장된 파일에서 한 줄씩 읽어 출력
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(f), "UTF-8");
             BufferedReader br = new BufferedReader(reader)) {

            System.out.println("\n파일 내용:");
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

