package NetworkPrograming12;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class HttpURLConnectionExample {
    private final String USER_AGENT = "Mozilla/5.0";
    private String cookie;

    public static void main(String[] args) {
        HttpURLConnectionExample http = new HttpURLConnectionExample();
        System.out.println("Sent Http POST request");
        http.sendGet();
    }

    private void sendGet() {
        try {
            URL url = new URL("http://localhost:5020");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            System.out.print("아이디를 입력하세요 : ");
            bw.write("ID : " + input.readLine() + "\r\n");
            System.out.print("패스워드를 입력하세요 : ");
            bw.write("PWD : " + input.readLine() + "\r\n");
            bw.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nResponse Code : " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = getResponse(conn);
                System.out.println("response: " + response);

                // 새 연결
                url = new URL("http://localhost:5020/download");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", USER_AGENT);
                conn.setRequestProperty("Cookie", cookie);
                conn.setDoOutput(true);

                // 사용자 입력
                System.out.print("다운로드할 파일 명을 입력하세요 : ");
                String fileName = input.readLine();

                // 파일명 전송
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write("FILE-NAME : " + fileName + "\r\n");
                writer.flush();
                writer.close();

                // 응답 처리
                responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream("new_" + fileName);
                    byte[] buffer = new byte[1024];
                    int size;
                    while ((size = is.read(buffer)) != -1) {
                        System.out.write(buffer, 0, size);
                        fos.write(buffer, 0, size);
                    }
                    fos.close();
                    is.close();
                    System.out.println("\n파일 다운로드 완료");
                } else {
                    System.out.println("파일 다운로드 실패: HTTP " + responseCode);
                }
            }else {
                System.out.println("Bad response Code : " +responseCode);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String getResponse(HttpURLConnection conn) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            System.out.println("\nResponse Headers");

            for (Map.Entry<String,List<String>> headers : conn.getHeaderFields().entrySet()) {
                System.out.println(headers.getKey() + " : " + headers.getValue() + "\n");
            }
            if (conn.getHeaderField("Set-Cookie") != null)
                cookie = conn.getHeaderField("Set-Cookie");
            System.out.println();

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            return response.toString();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
}
