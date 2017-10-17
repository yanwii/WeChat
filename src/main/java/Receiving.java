import com.sun.org.apache.regexp.internal.RE;
import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Receiving {
    private Login login;
    private Request request;
    private Config config;
    Receiving(Login l){
        this.login = l;
        this.request = Request.getInstance();
        this.config = Config.getInstance();
    }
    public void start(){
        String syncStatus = null;
        try {
            while (true) {
                syncStatus = this.syncCheck();
                System.out.println(syncStatus);
                if (syncStatus.equals("2")){
                    this.getMsg();
                    break;
                }
                Thread.sleep(2);
            }
        } catch (Exception e){
            System.out.println(e.toString());
        }
        //this.getMsg();
    }


    private String syncCheck() {
        System.out.println("->SyncCheck");
        try {
            String result = null;
            JSONObject loginConfig = this.config.loginConfig;

            String url = loginConfig.get("syncUrl") + "/synccheck";
            String param =  "?r=" + System.currentTimeMillis() +
                            "&skey=" + loginConfig.get("skey") +
                            "&sid=" + loginConfig.get("wxsid") +
                            "&uin=" + loginConfig.get("wxuin") +
                            "&deviceid=" + loginConfig.get("deviceid") +
                            "&synckey=" + loginConfig.get("synckey") +
                            "&_=" + System.currentTimeMillis();
            url += param;
            Hashtable response = this.request.normalGet(url, false, false);
            String content = response.get("content").toString();
            Pattern pattern = Pattern.compile("window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}");
            Matcher matcher = pattern.matcher(content);
            System.out.println(content);
            if (matcher.find()) {
                if (!matcher.group(1).equals("0")) {
                    result = "None";
                } else {
                    result = matcher.group(2);
                }
            } else {
                result = "None";
            }
            return result;
        }catch (Exception e){
            return "None";
        }
    }

    private void getMsg(){
        System.out.println("->GetMsg");
        String url = this.config.loginConfig.get("url") + "/webwxsync";
        url += "?sid=" + this.config.loginConfig.get("wxsid") +
                "&skey=" + this.config.loginConfig.get("skey") +
                "&pass_ticket=" + this.config.loginConfig.get("pass_ticket");

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("SyncKey", this.config.loginConfig.get("synckey"));
        String rr = String.valueOf(System.currentTimeMillis());
        rr = "-" + rr;
        jsonParam.put("rr", rr);
        jsonParam.put("BaseRequest", this.config.baseRequest);
        Hashtable<String, String> response = this.request.normalPost(url, jsonParam.toString(), true);
        System.out.println(response);
    }
}
