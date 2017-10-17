import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;

public class Contact {
    private Config config;
    private Request request;
    Contact(){
        this.config = Config.getInstance();
        this.request = Request.getInstance();
    }
    public void getContact(){
        JSONObject r = new JSONObject();
        JSONObject param = new JSONObject();
        param.put("Seq", 0);
        while (true) {
            r = getContactLoop(param);
            if (r.get("Seq").toString().equals("0")) {
                break;
            }else {
                param.put("Seq", r.get("Seq"));
            }
        }
        JSONArray memberList = r.getJSONArray("MemberList");
        Iterator iterator = memberList.iterator();
        JSONArray chatroomsList = new JSONArray();
        JSONArray friends = new JSONArray();
        while (iterator.hasNext()){
            JSONObject tmp = new JSONObject(iterator.next().toString());
            if (!tmp.get("Sex").toString().equals("0")){
                friends.put(tmp);
            }else if(tmp.get("UserName").toString().contains("@@")){
                chatroomsList.put(tmp);
            }else {
                friends.put(tmp);
            }
        }
        System.out.println("共有 "+ friends.length() + " 个好友");
        System.out.println("共有 "+ chatroomsList.length() + " 个聊天");

        updateLocalFriends(friends);
        updateLocalChatrooms(chatroomsList);
    }

    public void updateLocalFriends(JSONArray friends){

        Iterator iterator = friends.iterator();
        while (iterator.hasNext()){
            JSONObject tmp = new JSONObject(iterator.next().toString());
            this.config.friends.put(tmp.get("NickName").toString(), tmp);
        }
    }
    public void updateLocalChatrooms(JSONArray chatrooms){
        Iterator iterator = chatrooms.iterator();
        while (iterator.hasNext()){
            JSONObject tmp = new JSONObject(iterator.next());
            this.config.chatrooms.put("NickName", tmp);
        }
    }

    public JSONObject getContactLoop(JSONObject param){
        JSONObject r = new JSONObject();
        String url = this.config.loginConfig.getString("url") +
                "/webwxgetcontact?" +
                "r=" + String.valueOf(System.currentTimeMillis()) +
                "&seq=" + param.get("Seq").toString() +
                "&skey=" + this.config.loginConfig.getString("skey");
        Hashtable response = this.request.normalPost(url, "", false);
        JSONObject responseJson = new JSONObject(response.get("content").toString());
        r.put("Seq", responseJson.get("Seq"));
        r.put("MemberList", responseJson.get("MemberList"));
        return r;
    }
}
