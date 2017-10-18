import com.sun.org.apache.regexp.internal.RE;
import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
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
        JSONObject response = new JSONObject();
        try {
            while (true) {
                syncStatus = this.syncCheck();
                if (syncStatus.equals("2")){
                    response = this.getMsg();
                    if (!response.getJSONObject("BaseResponse").get("Ret").toString().equals("0")) {
                        throw new Exception("同步失败!");
                    }
                    JSONArray modContactList = response.getJSONArray("ModContactList");
                    JSONArray addMsgCount = response.getJSONArray("AddMsgList");
                    if (addMsgCount.iterator().hasNext()){
                        produceMsg(addMsgCount);
                    }
                }
                Thread.sleep(3000);
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
            if (matcher.find()) {
                if (matcher.group(1).equals("1101")) {
                    throw new Exception(content);
                } else if(!matcher.group(1).equals("0")){
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

    private JSONObject getMsg(){
        System.out.println("->GetMsg");
        String url = this.config.loginConfig.get("url") + "/webwxsync";
        url += "?sid=" + this.config.loginConfig.get("wxsid") +
                "&skey=" + this.config.loginConfig.get("skey") +
                "&pass_ticket=" + this.config.loginConfig.get("pass_ticket");

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("SyncKey", this.config.loginConfig.get("Synckey"));
        String rr = String.valueOf(System.currentTimeMillis());
        rr = "-" + rr;
        jsonParam.put("rr", -694178901);
        jsonParam.put("BaseRequest", this.config.baseRequest);
        Hashtable<String, String> response = this.request.normalPost(url, jsonParam.toString(), true);
        JSONObject responseJson = new JSONObject(response.get("content"));
        JSONArray syncKeyJsonArray = responseJson.getJSONObject("SyncKey").getJSONArray("List");
        Iterator iter1 = syncKeyJsonArray.iterator();
        String syncKey = "";
        while (iter1.hasNext()){
            JSONObject json = new JSONObject(iter1.next().toString());
            syncKey += json.get("Key").toString() + "_" + json.get("Val").toString();
            if (iter1.hasNext()){
                syncKey += "%7C";
            }
        }
        this.config.loginConfig.put("synckey", syncKey);
        this.config.loginConfig.put("Synckey", responseJson.get("SyncKey"));
        return responseJson;
    }

    public void  produceMsg(JSONArray addMsgList){
        Iterator iterator = addMsgList.iterator();
        JSONArray msgList = new JSONArray();
        while (iterator.hasNext()){
            JSONObject tmp = new JSONObject(iterator.next().toString());
            int msgType = Integer.parseInt(tmp.get("MsgType").toString());
            switch (msgType){
                case 1:
                    // words
                    msgList.put(getText(tmp));
                case 51:
                    //phone init
            }
        }
        System.out.println(msgList.toString());
    }
    public JSONObject getText(JSONObject json){
        JSONObject r = new JSONObject();
        if (json.get("Url").equals("")){
            System.out.println(json);
            r.put("Type", "Text");
            r.put("Text", json.get("Content"));
            r.put("FromUserName", json.get("FromUserName"));
            r.put("ToUserName", json.get("ToUserName"));
        }
        return r;
    }
}
