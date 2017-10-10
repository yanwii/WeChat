import jdk.nashorn.internal.runtime.JSONFunctions;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Login{
    private Request request;
    public Hashtable<String, String> loginConfig = new Hashtable<String, String>();
    public Hashtable<String, String> baseRequest = new Hashtable<String, String>();
    Login(Request r){
        this.request = r;
    }

    public boolean loginProgress() throws Exception{
        // 获取uuid
        boolean isLogined = false;
        String uuid = "0";
        //this.testPost();

        try{
            uuid = this.getUuid();
            //this.webInit();
        } catch (Exception e){
            System.out.println(e.toString());
            throw new Exception("登录错误！获取uuid失败:"+e);
        }

        try{
            this.getQR(uuid);
        } catch (Exception e){
            System.out.println(e.toString());
            throw new Exception("登录错误！获取二维码失败:"+e);
        }

        try {
            this.checkLogin(uuid);
        } catch (Exception e){
            System.out.println(e.toString());
            throw new Exception("登录失败！:"+e);
        }

        try {
            this.webInit();
        } catch (Exception e){
            System.out.println(e.toString());
            throw new Exception("初始化失败!" + e);
        }
        //this.sendMsg();
        return isLogined;
    }

    private String getUuid() throws Exception{
        System.out.println("->获取uuid");
        String uuid = "0";
        String uri = this.request.baseUrl + "/jslogin?";
        uri += "appid=wx782c26e4c19acffb&" +
                "fun=new";
        Hashtable response = this.request.get(uri, true);
        //System.out.println(response);

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

        Hashtable response = this.request.saveImg(uri);

        if (response.get("code").equals("200")){
            //保存二维码
            //显示二维码
            System.out.println("二维码保存成功 请扫描");
        } else {
            throw new Exception(response.get("content").toString());
        }
    }

    private void checkLogin(String uuid) throws Exception{
        System.out.println("->检查登录状态");
        String url = this.request.baseUrl + "/cgi-bin/mmwebwx-bin/login?";
        long timestamp = System.currentTimeMillis();
        String params = "loginicon=true" +
                        "&uuid=" + uuid +
                        "&tip=0" +
                        "&r=" + String.valueOf(timestamp/1579) +
                        "&_=" + String.valueOf(timestamp);
        url += params;
        boolean isScan=false;
        boolean isLogin = false;
        Hashtable response;
        while (!isLogin){
            response = this.request.get(url, true);
            String statusCode = "400";
            boolean show = false;
            if (response.get("code").equals("200")){
                //检查扫描状态
                Pattern pattern = Pattern.compile("window.code=(\\d+)");
                Matcher match = pattern.matcher(response.get("content").toString());
                if (match.find()){
                    statusCode = match.group(1);
                    if(statusCode.equals("201") && !isScan){
                        System.out.println("已扫描，请在手机上确认！");
                        isScan = true;
                    }else if(statusCode.equals("200")){
                        System.out.println("已确认，登录成功");
                        this.getLoginConfig(response.get("content").toString());
                        isLogin = true;
                    }
                }
            } else {
                throw new Exception(response.get("content").toString());
            }
        }

    }

    private void getLoginConfig(String content) throws Exception{
        System.out.println("->获取登录信息");
        String redirectUrl;
        Pattern pattern = Pattern.compile("window.redirect_uri=\"(.*)\";");
        Matcher match = pattern.matcher(content);
        if (match.find()) {
            redirectUrl = match.group(1);
            String url = redirectUrl.substring(0, redirectUrl.lastIndexOf("/"));
            this.loginConfig.put("url", url);
            this.loginConfig.put("syncUrl","https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin");
            this.loginConfig.put("firlUrl","https://file.wx.qq.com/cgi-bin/mmwebwx-bin");
            String deviceid = String.valueOf(Math.random());
            deviceid = "e" + deviceid.substring(2, 17);
            this.loginConfig.put("deviceid", deviceid);

            //Hashtable response = this.request.get(redirectUrl, false);
            Hashtable response = this.request.normalGet(redirectUrl, false);

            if (response.get("code").equals("301")) {
                Document doc = Jsoup.parse(response.get("content").toString());
                String skey = doc.getElementsByTag("skey").text();
                String wxsid = doc.getElementsByTag("wxsid").text();
                String wxuin = doc.getElementsByTag("wxuin").text();
                String pass_ticket = doc.getElementsByTag("pass_ticket").text();
                this.loginConfig.put("skey", skey);
                this.loginConfig.put("wxsid", wxsid);
                this.loginConfig.put("wxuin", wxuin);
                this.loginConfig.put("pass_ticket", pass_ticket);

                this.baseRequest.put("Skey", skey);
                this.baseRequest.put("Sid", wxsid);
                this.baseRequest.put("Uin", wxuin);
                this.baseRequest.put("DeviceID", deviceid);
                System.out.println(this.loginConfig);
                System.out.println(this.baseRequest);
            } else {
                throw new Exception(response.get("content").toString());
            }
        }
    }

    private void webInit() throws Exception{
        System.out.println("->初始化");
        String url = this.loginConfig.get("url") + "/webwxinit?r=-9891412";
        url += "&pass_ticket=" + this.loginConfig.get("pass_ticket");
        url += "&lang=en_US";

        String param = "";
        param = "{\"BaseRequest\":" +
                    "{"+
                    "\"DeviceID\"" + ":" + "\"" + this.baseRequest.get("DeviceID")   +"\","+
                    "\"Sid\""      + ":" + "\"" + this.baseRequest.get("Sid")        +"\","+
                    "\"Skey\""     + ":" + "\"" + this.baseRequest.get("Skey")       +"\","+
                    "\"Uin\""      + ":" + "\"" + this.baseRequest.get("Uin")        +"\""+
                    "}" +
                "}";
        Hashtable response = this.request.normalPost(url, param);
        if (response.get("code").equals("200")) {
            JSONObject jsonResponse = new JSONObject(response.get("content").toString());
            System.out.println(jsonResponse);
            System.out.println("Welcome:" + jsonResponse.get("User"));
        }else {
            System.out.println(response.get("code"));
            System.out.println(response.get("content"));
        }
    }

    public void sendMsg(){
        String url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?lang=en_US&pass_ticket=" + this.loginConfig.get("pass_ticket");
        Hashtable<String, String> param = new Hashtable<String, String>();
        param.put("BaseRequest", this.baseRequest.toString());
        Hashtable<String, String> Msg = new Hashtable<String, String>();
        Msg.put("ClientMsgId", "15075546089280900");
        Msg.put("Content", "发誓");
        Msg.put("FromUserName", "@e1cad587104b77593feaed6778527f3c");
        Msg.put("LocalID", "15075546089280900");
        Msg.put("ToUserName","@11e383b9d307c0cf202a74f70808815a5795ebab0f2367cae9d400670acaff2e");
        Msg.put("Type", "1");
        param.put("Msg", Msg.toString());
        //Hashtable response = this.request.post(url, param);
        //System.out.println(response);
    }
    public void testPost(){
        String url = "http://yanwii.me/login";
    }

}
