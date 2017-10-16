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
//{"ClientMsgId":1508133752396,"FromUserName":"@cd55c7820affc2c8858e6388e8627d05","BaseRequest":{"DeviceID":"e236153385569642","Uin":"2335662880","Skey":"@crypt_d477b9cc_e33618f8b4fd9d23b7eaf3541a7ca798","Sid":"Crj6r2oUxo1rjJoy"},"ToUserName":"@cd55c7820affc2c8858e6388e8627d05","Code":3}
//{"ClientMsgId":1508133826362,"FromUserName":"@7cdd483d15dad774497cf6a4ca07f17c","BaseRequest":{"DeviceID":"e163826447307644","Uin":2335662880,"Sid":"BZXkkxVWxfo/OH1M","Skey":"@crypt_d477b9cc_57b71e6b619b5ddc34b04fb27bf1e853"},"Code":3,"ToUserName":"@7cdd483d15dad774497cf6a4ca07f17c"}