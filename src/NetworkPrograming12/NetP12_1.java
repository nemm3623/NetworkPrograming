package NetworkPrograming12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class NetP12_1 {
    public static void main(String[] args) throws IOException {
        URL url = new URL("https://www.youtube.com");
        HttpURLConnection u = (HttpURLConnection) url.openConnection();


        u.setRequestMethod("GET");
        u.connect();
        System.out.println("응답코드 : " + u.getResponseCode());
        System.out.println("응답 메시지 : " + u.getResponseMessage());
        for (String line : u.getHeaderFields().keySet()) {
            if (line == null) continue;
            System.out.println(line + ": " + u.getHeaderField(line));
        }

        Date date = new Date(u.getHeaderFieldDate("Last-Modified", -1));

        System.out.println();

        if(date.getTime() == -1){
            System.out.println("Last-Modified 헤더가 존재하지 않음");
        }
        // 현재 날짜 - 10일 > 수정 날짜
        else if((System.currentTimeMillis() - 864000000) < date.getTime()) {

            String s;
            BufferedReader br = new BufferedReader(new InputStreamReader(u.getInputStream()));
            while ((s = br.readLine()) != null){
                System.out.println(s);
            }
        } else {
            System.out.println("요청한 웹페이지가 10일 이내에 수정되지 않음");
        }
    }
}
