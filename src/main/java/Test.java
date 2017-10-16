import org.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.print.DocFlavor;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

public class Test {
    public static void main(String[] args){
        Test t = new Test();
        t.main();
    }

    public void main(){
        Hashtable<String, String> param = new Hashtable<String, String>();
        param.put("user", "yanwii");
        JSONObject json = new JSONObject(param);
        System.out.println(json);
    }
}
//https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck?r=1507789674230&skey=@crypt_d477b9cc_1e3bd8733bf346364fd96e517d7e720f&sid=fhzcE8homT2aPQZZ&uin=2335662880&deviceid=e321411006570309&synckey=1_658405698|2_658405729|3_658405721|1000_1507768921&_=1507789674230
//https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck?r=1507789844241&skey=@crypt_d477b9cc_05b70b78a82340c7a843491d58500bcc&sid=%2BGGBRRc9LXhaF46u&uin=2335662880&deviceid=e502414210347888&synckey=1_658405698%7C2_658405739%7C3_658405721%7C11_658404548%7C13_658320135%7C201_1507789705%7C1000_1507768921%7C1001_1507768933&_=1507789701388