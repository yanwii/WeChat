import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

class Request {
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

    public Hashtable<String, String> normalGet(String url, boolean isFollowRedirects) {
        try {
            System.setProperty ("jsse.enableSNIExtension", "false");

            String content = "";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(isFollowRedirects).build();
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = client.execute(httpGet);
            content = EntityUtils.toString(response.getEntity(),"utf-8");
            System.out.println("Content" + content);
            Header[] hs = response.getHeaders("Set-Cookie");
            String cookie = "";
            String cookies = "";
            for (int i=0; i<hs.length; i++){
                cookie = hs[i].toString();
                cookies += cookie.substring(cookie.indexOf(":")+2, cookie.indexOf(";"))+"; ";
            }
            cookies += "MM_WX_NOTIFY_STATE=1; MM_WX_SOUND_STATE=1;";
            this.headers.put("Cookie", cookies);
            System.out.println(this.headers);
            return this.mkResponse("301", content);
        } catch (Exception e){
            return this.mkResponse("413", e.toString());
        }

    }

    public Hashtable<String, String> normalPost(String url, String param) {
        try {
            System.setProperty ("jsse.enableSNIExtension", "false");
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpClient client = HttpClients.createDefault();

            ByteArrayEntity entity = null;
            entity = new ByteArrayEntity(param.getBytes("UTF-8"));
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            for (String key:this.headers.keySet()){
                System.out.println("Set \t"+ key + this.headers.get(key));
                httpPost.setHeader(key, this.headers.get(key));
            }
            RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(true).build();
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = client.execute(httpPost);
            String statusLine = response.getStatusLine().toString();
            String content = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(statusLine);
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
