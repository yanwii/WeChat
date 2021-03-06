import com.sun.org.apache.regexp.internal.RE;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.Hashtable;
import java.util.LinkedHashMap;

class Request {
    public static Request instance=null;
    public static Request getInstance(){
        if (instance==null){
            instance = new Request();
        }
        return instance;
    }
    public ProcessLog plog = ProcessLog.getInstance();
    public Hashtable<String, String> headers = new Hashtable<String, String>();
    public String baseUrl = "http://login.weixin.qq.com";
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
                    .execute();
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

    public Hashtable<String, String> normalGet(String url, boolean isFollowRedirects, boolean ifResetCookies) {
        try {
            System.setProperty ("jsse.enableSNIExtension", "false");
            this.plog.requestLogger.info("Get: " + url);
            String content = "";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(isFollowRedirects).build();
            httpGet.setConfig(requestConfig);
            for (String key:this.headers.keySet()){
                //System.out.println("Set \t"+ key + this.headers.get(key));
                httpGet.setHeader(key, this.headers.get(key));
            }

            CloseableHttpResponse response = client.execute(httpGet);
            content = EntityUtils.toString(response.getEntity(),"utf-8");
            //reset cookies
            if (ifResetCookies) {
                Header[] hs = response.getHeaders("Set-Cookie");
                this.saveCookie(hs);
            }
            return this.mkResponse("301", content);
        } catch (Exception e){
            return this.mkResponse("413", e.toString());
        }

    }
    private void saveCookie(Header[] header){
        String key = null;
        String val = null;
        String cookie = null;
        for (int i = 0; i < header.length; i++) {
            cookie = header[i].toString();
            key = cookie.substring(cookie.indexOf(":")+2, cookie.indexOf("="));
            val = cookie.substring(cookie.indexOf("=")+1, cookie.indexOf(";"));
            this.cookies.put(key, val);
        }
        this.cookies.put("MM_WX_NOTIFY_STATE", "1");
        this.cookies.put("MM_WX_SOUND_STATE", "1");

        String cookies = "";
        for (String cookieKey: this.cookies.keySet()){
            cookies += cookieKey + "= " + this.cookies.get(cookieKey) + ";";
        }
        this.headers.put("Cookie", cookies);
    }


    public Hashtable<String, String> normalPost(String url, String param, boolean ifResetCookies) {
        try {
            //System.out.println("------------------------------------");
            //System.out.println("post->" + url);
            //System.out.println(param);
            //System.out.println("------------------------------------");
            this.plog.requestLogger.info("Post: " + url + " param: " + param);

            System.setProperty ("jsse.enableSNIExtension", "false");
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpClient client = HttpClients.createDefault();

            ByteArrayEntity entity = null;
            entity = new ByteArrayEntity(param.getBytes("UTF-8"));
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            for (String key:this.headers.keySet()){
                //System.out.println("Set \t"+ key + this.headers.get(key));
                httpPost.setHeader(key, this.headers.get(key));
            }
            RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(true).build();
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = client.execute(httpPost);
            String statusLine = response.getStatusLine().toString();
            if (ifResetCookies) {
                Header[] hs = response.getHeaders("Set-Cookie");
                this.saveCookie(hs);
            }
            String content = EntityUtils.toString(response.getEntity(), "utf-8");
            client.close();
            Header[] hd = response.getAllHeaders();
            return this.mkResponse("200", content);
        } catch (Exception e){
            return this.mkResponse("413", e.toString());
        }

    }

    public Hashtable<String, String> post(String url, String params){
        try {
            url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r=-25036998&lang=en_US&pass_ticket=rh3QGmalVa5GolUDstqOMeleid%252FJdg3AZ0acLtXqsP6nFgTB7CqhlbtFUEaM%252BBw1";
            System.setProperty ("jsse.enableSNIExtension", "false");
            Hashtable<String, String> localHeaders = this.headers;
            localHeaders.put("ContentType", "application/json; charset=utf-8");
            localHeaders.put("Accept","application/json, text/plain, */* ");
            localHeaders.put("Accept-Encoding","gzip, deflate, br");
            localHeaders.put("Accept-Language", "en-US,en;q=0.8");
            localHeaders.put("Connection", "keep-alive");
            localHeaders.put("Content-Type", "application/json;charset=UTF-8");
            localHeaders.put("Host", "wx.qq.com");
            localHeaders.put("Origin","https://wx.qq.com");
            localHeaders.put("Referer", "https://wx.qq.com/?&lang=en_US");
            localHeaders.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");


            Connection.Response con = Jsoup.connect(url)
                    .headers(localHeaders)
                    //.data("BaseRequest", nparam.toString())
                    .requestBody(params)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .followRedirects(true)
                    .execute();
            int code = con.statusCode();
            return this.mkResponse(String.valueOf(code), con.body());
        } catch (Exception e){
            return this.mkResponse("412", e.toString());
        }
    }

}
