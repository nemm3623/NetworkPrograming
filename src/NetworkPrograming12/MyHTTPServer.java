package NetworkPrograming12;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;


public class MyHTTPServer {

    public static final int SUCCESS_CODE = 200;
    public static final int NOT_EXISTS_CODE = 404;
    public static final int NULL_CODE = 400;
    public static final int NO_AUTH_CODE = 403;
    static Map<String, String> sessionMap = new HashMap<>();

    public static void main(String[] args) throws Exception{
        System.out.println("MyHTTPServer starting...");
        HttpServer server = HttpServer.create(new InetSocketAddress(5020), 0);
        server.createContext("/", new DetailHandler());
        server.createContext("/download", new DownloadHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
    }

    public static String getResponse(){
        return "<html><h1>Http Server Home Page...</h1><br>" +
                "<b>Welcome to the new and improved web server!</b><br>" +
                "</html>";
    }

    static class DetailHandler implements HttpHandler {

        Map<String, String> userData = new HashMap<>();

        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("\nRequest Headers");

            for (Map.Entry<String, List<String>> entry : t.getRequestHeaders().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            String requestMethod = t.getRequestMethod();

            if (requestMethod.equalsIgnoreCase("POST")) {
                System.out.println("\nRequest Body");
                InputStream in = t.getRequestBody();
                String id = null, pwd = null;

                if (in != null) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

                        String line;
                        while ((line = br.readLine()) != null) {
                            System.out.println(line);
                            String[] parts = line.split(":", 2);
                            if (parts.length == 2) {
                                String key = parts[0].trim();
                                String value = parts[1].trim();
                                if (key.equalsIgnoreCase("ID")) id = value;
                                if (key.equalsIgnoreCase("PWD")) pwd = value;
                            }
                        }
                    }
                } else {
                    System.out.println("Request Body is null");
                }

                if (id != null && pwd != null) {

                    // 로그인 성공 시 인증을 위한 쿠키 발급
                    String sessionId = UUID.randomUUID().toString();
                    sessionMap.put(sessionId, id);

                    String response = "<h1>로그인 성공!</h1>";
                    Headers headers = t.getResponseHeaders();
                    headers.set("Content-Type", "text/html");
                    headers.set("Set-Cookie", sessionId);
                    t.sendResponseHeaders(SUCCESS_CODE, response.getBytes().length);
                    t.getResponseBody().write(response.getBytes());

                    Headers responseHeaders = t.getResponseHeaders();
                    String responseMessage = getResponse();

                    System.out.println("Response Headers");
                    Set<String> keys = responseHeaders.keySet();
                    keys.stream().map((k) -> {
                        List<String> values = responseHeaders.get(k);
                        return k + " : " + values.toString() + "\n";
                    }).forEach(System.out::println);

                    try (OutputStream responseBody = t.getResponseBody()) {
                        responseBody.write(responseMessage.getBytes());
                    }
                }else {
                    System.out.println("Request Body is null");
                }
            }
        }
    }

    static class DownloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            if (t.getRequestMethod().equalsIgnoreCase("POST")) {

                Headers requestHeaders = t.getRequestHeaders();
                String cookie = requestHeaders.getFirst("Cookie");

                System.out.println("\nRequest Header");
                for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }

                if (cookie == null || !sessionMap.containsKey(cookie)) {
                    String response = "인증되지 않은 요청입니다.";
                    t.sendResponseHeaders(NO_AUTH_CODE, response.getBytes().length);
                    t.getResponseBody().write(response.getBytes());
                    t.getResponseBody().close();
                    return;
                }

                InputStream in = t.getRequestBody();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String fileName = null;
                String line;
                System.out.println("\nRequest Body");
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    if (line.startsWith("FILE-NAME")) {
                        fileName = line.split(":", 2)[1].trim();
                        break;
                    }
                }

                if (fileName != null) {
                    File file = new File(fileName);
                    if (!file.exists()) {
                        String response = "파일이 존재하지 않습니다.";
                        t.sendResponseHeaders(NOT_EXISTS_CODE, response.getBytes().length);
                        t.getResponseBody().write(response.getBytes());
                    } else {
                        Headers headers = t.getResponseHeaders();
                        headers.set("Content-Type", "application/octet-stream");
                        t.sendResponseHeaders(SUCCESS_CODE, file.length());

                        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                             OutputStream os = t.getResponseBody()) {
                            byte[] buffer = new byte[1024];
                            int count;
                            while ((count = bis.read(buffer)) != -1) {
                                os.write(buffer, 0, count);
                            }
                        }
                    }
                }else {
                    String response = "파일명을 입력해주세요.";
                    t.sendResponseHeaders(NULL_CODE, response.getBytes().length);
                    t.getResponseBody().write(response.getBytes());
                }
            }
        }
    }
}
