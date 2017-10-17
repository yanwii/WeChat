import org.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.print.DocFlavor;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

public class Test {
    public static void main(String[] args) {
        Test t = new Test();
        t.main();
    }

    public void main() {
        String a = "@@ssdfsfd";

        if (a.indexOf("@@")!=-1){
            System.out.println(true);
        }
    }
}

//{"BaseRequest":{"Uin":2335662880,"Sid":"QA2fBUJzCRQkd9tP","Skey":"@crypt_d477b9cc_e606ecb0e87e84745e235a9569eababf","DeviceID":"e655098565555246"},"SyncKey":{"Count":4,"List":[{"Key":1,"Val":658406674},{"Key":2,"Val":658406790},{"Key":3,"Val":658406717},{"Key":1000,"Val":1508200322}]},"rr":-694178901}
//{"rr":"-1508229176112","BaseRequest":{"DeviceID":"e344431451388271","Skey":"@crypt_d477b9cc_98ed870a44f743d100d122491897b4d8","Uin":"2335662880","Sid":"cMtkX2tU7DlCM0zt"},"SyncKey":[{"Val":658406674,"Key":1},{"Val":658406800,"Key":2},{"Val":658406717,"Key":3},{"Val":1508200322,"Key":1000}]}
