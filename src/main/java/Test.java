import org.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

public class Test {
    public static void main(String[] args){
        Test t = new Test();
        t.main();
    }

    public void main(){
        String str = "{\"user\":\"yanwii\"}";
        JSONObject json = new JSONObject(str);
        System.out.println(json.get("user"));

    }
}
