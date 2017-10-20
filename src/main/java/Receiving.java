import com.sun.org.apache.regexp.internal.RE;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Receiving {
    private Login login;
    private Request request = Request.getInstance();
    private Config config = Config.getInstance();
    private Message message = Message.getInstance();
    Receiving(Login l){
        this.login = l;
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
                    //produceMsg
                    if (addMsgCount.iterator().hasNext()){
                        this.message.produceMsg(addMsgCount);
                    }
                    this.message.sendMsg("哈哈哈哈", "sssss");
                } else if (syncStatus.equals("1101")){
                    throw new Exception("同步失败");
                }
                Thread.sleep(3000);
            }
        } catch (Exception e){
            System.out.println(e.toString());
        }
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
                if(!matcher.group(1).equals("0")){
                    result = "1101";
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


}
