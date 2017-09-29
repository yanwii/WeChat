import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Hashtable;

public class Request {
    public Hashtable<String, String> headers = new Hashtable<String, String>();
    public String baseUrl = "https://login.weixin.qq.com";
    Request(){
        this.headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
    }

    public Hashtable<String,String> get(String url) {
        Hashtable<String, String> response = new Hashtable<String, String>();
        System.setProperty ("jsse.enableSNIExtension", "false");
        try {
            Connection.Response con = Jsoup.connect(url).headers(this.headers).ignoreContentType(true).execute();
            int code = con.statusCode();
            response.put("code", String.valueOf(code));
            response.put("content", con.body());
        } catch (Exception e){
            response.put("code", "410");
            response.put("content", e.toString());
        }
        return response;
    }


}
