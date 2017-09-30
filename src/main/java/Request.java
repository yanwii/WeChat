import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;

public class Request {
    public Hashtable<String, String> headers = new Hashtable<String, String>();
    public String baseUrl = "https://login.weixin.qq.com";
    private LinkedHashMap<String, String> cookies = new LinkedHashMap<String, String>();
    Request(){
        this.headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
    }

    public Hashtable<String,String> get(String url, boolean isFollowRedirects) {
        System.setProperty ("jsse.enableSNIExtension", "false");
        try {
            Connection.Response con = Jsoup.connect(url)
                    .headers(this.headers)
                    .ignoreContentType(true)
                    .followRedirects(isFollowRedirects)
                    .cookies(this.cookies)
                    .execute();
            this.cookies = (LinkedHashMap<String, String>) con.cookies();
            int code = con.statusCode();
            return this.mkResponse(String.valueOf(code), con.body());
        } catch (Exception e){
            return this.mkResponse("410", e.toString());
        }
    }

    public Hashtable<String, String> saveImg(String url){
        try{
            Connection.Response con = Jsoup.connect(url).headers(this.headers).ignoreContentType(true).execute();
            byte[] img = con.bodyAsBytes();
            File imgFile = new File("./tmp/QR.jpg");
            FileOutputStream outputStream = new FileOutputStream(imgFile);
            outputStream.write(img);
            outputStream.close();
            return this.mkResponse("200", "succeed!");
        } catch (Exception e){
            return this.mkResponse("411", e.toString());
        }

    }

    private Hashtable<String, String> mkResponse(String code, String content){
        Hashtable<String, String> response = new Hashtable<String, String>();
        response.put("code", code);
        response.put("content", content);
        return response;
    }

    public Hashtable<String, String> post(String url, Hashtable<String, String> params){
        try {
            Hashtable headers = this.headers;
            headers.put("ContentType", "application/json; charset=UTF-8");
            Connection.Response con = Jsoup.connect(url)
                    .headers(headers)
                    .ignoreContentType(true)
                    .cookies(this.cookies)
                    .data(params)
                    .execute();
            this.cookies = (LinkedHashMap<String, String>) con.cookies();
            System.out.println(this.cookies);
            int code = con.statusCode();
            return this.mkResponse(String.valueOf(code), con.body());
        } catch (Exception e){
            return this.mkResponse("412", e.toString());
        }
    }
}
