import java.util.Hashtable;

public class XiaoIce{
    private XiaoIce(){
    }
    public static void main(String[] args){
        Request request = new Request();
        XiaoIce xi = new XiaoIce();

        Login login = new Login(request);
        try {
            login.loginProgress();
        } catch (Exception e){
            System.out.println(String.valueOf(e));
        }
    }


}
