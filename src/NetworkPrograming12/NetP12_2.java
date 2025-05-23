package NetworkPrograming12;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetP12_2 {
    public static void main(String[] args) throws Exception {
        URL url = new URL("https://docbox.etsi.org/stq/Open/Binary%20files/1MB.bin");
        HttpURLConnection u = (HttpURLConnection) url.openConnection();

        u.setRequestMethod("GET");
        u.connect();
        if (u.getResponseCode() == 200) {
            String contenttype = u.getContentType();
            System.out.println(contenttype);

            if (contenttype.contains("application/octet-stream")) {
                String filename = u.getHeaderField("Content-Disposition");
                String filepath = "/Users/nemm/Documents/NetP/download/";
                if (filename == null) {
                    filename = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
                    if (!filename.contains("bin"))
                        filename = filename + ".bin";
                }

                BufferedInputStream bin = new BufferedInputStream(u.getInputStream());
                FileOutputStream fout = new FileOutputStream(new File(filepath,filename));

                int size = 0;
                byte[] buffer = new byte[1024];
                while (( size = bin.read(buffer)) != -1) {
                    fout.write(buffer, 0, size);
                }

                System.out.println("파일이 다운로드 폴더에 저장되었습니다.");
                bin.close();
                fout.close();
            }else {
                System.out.println("바이너리 파일이 아닙니다.");
            }
        }else {

        }
    }
}
