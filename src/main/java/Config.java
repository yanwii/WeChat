import org.json.JSONObject;

class Config {
    private static Config instance = null;
    public static Config getInstance(){
        if (instance==null){
            instance = new Config();
        }
        return instance;
    }
    public JSONObject chatrooms = new JSONObject();
    public JSONObject friends = new JSONObject();
    public JSONObject loginConfig = new JSONObject();
    public JSONObject baseRequest = new JSONObject();

}


