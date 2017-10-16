import java.util.Hashtable;

public class XiaoIce{
    private XiaoIce(){
    }
    public static void main(String[] args){
        Request request = new Request();
        XiaoIce xi = new XiaoIce();

        //login
        Login login = new Login(request);
        try {
            login.loginProgress();
        } catch (Exception e){
            System.out.println(e.toString());
        }

        //start monitoring
        Receiving recerving = new Receiving(login, request);
        try {
            recerving.start();
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }


}
