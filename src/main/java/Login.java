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

        try {
            this.checkLogin(uuid);
        } catch (Exception e){
            System.out.println(e);
            throw new Exception("登录失败！:"+e);
        }

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

    private void getLoginConfig(String content){
        String redirectUrl;
        Pattern pattern = Pattern.compile("window.redirect_uri=\"(.*)\";");
        Matcher match = pattern.matcher(content);
        if (match.find()){
            redirectUrl = match.group(1);
            Hashtable response = this.request.get(redirectUrl, false);
            System.out.println(redirectUrl);
            System.out.println(response);
        }
    }
}
