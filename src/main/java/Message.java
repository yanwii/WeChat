import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;

public class Message {
    public static Message instance = null;
    public static Message getInstance(){
        if (instance==null){
            instance = new Message();
        }
        return instance;
    }
    private Request request = Request.getInstance();
    private Config config = Config.getInstance();
    private ProcessLog plog = ProcessLog.getInstance();
    Message(){

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
            r.put("Type", "Text");
            r.put("Text", json.get("Content"));
            String fromUserName = json.getString("FromUserName");
            String toUserName = json.getString("ToUserName");
            r.put("FromUserName", this.config.usernameToNickname.getString(fromUserName));
            r.put("ToUserName", this.config.usernameToNickname.getString(toUserName));
        }
        return r;
    }

    public void sendMsg(String content, String toUserName){
        String url = this.config.loginConfig.get("url") + "/webwxsendmsg";
        JSONObject param = new JSONObject();
        JSONObject msg = new JSONObject();
        msg.put("Type", "1");
        msg.put("Content", content);
        msg.put("FromUserName", this.config.loginConfig.getString("UserName"));
        msg.put("ToUserName", toUserName);
        msg.put("LocalID", System.currentTimeMillis()*10000);
        msg.put("ClientMsgId", System.currentTimeMillis()*10000);
        param.put("BaseRequest", this.config.baseRequest);
        param.put("Msg", msg);
        param.put("scene", "0");
        Hashtable response = this.request.normalPost(url, param.toString(), false);
        JSONObject jsonResponse = new JSONObject(response.get("content").toString());
        if (!jsonResponse.getJSONObject("BaseResponse").get("Ret").equals("0")){
            System.out.println("消息发送失败");
            this.plog.logger.warning("消息发送失败");
            //log.logger.warning("消息发送失败: To:" + toUserName + " Content:" + content+
            //" Response:" + jsonResponse.toString());
        }
    }


}
