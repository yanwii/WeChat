import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login{
    private Request request;
    Login(Request r){
        this.request = r;
    }

    public boolean loginProgress() throws Exception{
        // 获取uuid
        boolean isLogined = false;
        String uuid = "0";
        try{
            uuid = this.getUuid();
        } catch (Exception e){
            System.out.println(e);
            throw new Exception("登录错误！获取uuid失败:"+e);
        }

        try{
            this.getQR(uuid);
        } catch (Exception e){
            System.out.println(e);
            throw new Exception("登录错误！获取二维码失败:"+e);
        }

        return isLogined;
    }

    private String getUuid() throws Exception{
        System.out.println("->获取uuid");
        String uuid = "0";
        String uri = this.request.baseUrl + "/jslogin?";
        uri += "appid=wx782c26e4c19acffb&" +
                "fun=new";
        Hashtable response = this.request.get(uri);
        System.out.println(response);

        String content = response.get("content").toString();
        Pattern pattern = Pattern.compile("window.QRLogin.code = (200); window.QRLogin.uuid = \"(.*)\"");
        Matcher match = pattern.matcher(content);

        if (match.find()){
            uuid = match.group(2);
            System.out.println("uuid:"+uuid);
        }else {
            throw new Exception(response.get("content").toString());
        }
        return uuid;

    }

    private void getQR(String uuid) throws Exception{
        System.out.println("->获取二维码");
        String uri = "https://login.weixin.qq.com/qrcode/" + uuid;

        Hashtable response = this.request.get(uri);

        if (response.get("code").equals("200")){
            //保存二维码
            //显示二维码
            String content = response.get("content").toString();
            Document doc = Jsoup.parse(content);
            System.out.println(doc.text());
        } else {
            throw new Exception(response.get("content").toString());
        }


    }


}
