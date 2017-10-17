import java.util.Hashtable;

public class XiaoIce{
    private XiaoIce(){
    }
    public static void main(String[] args){
        Request request = new Request();
        XiaoIce xi = new XiaoIce();
        boolean islogined = false;
        //login
        Login login = new Login(request);
        try {
            login.loginProgress();
        } catch (Exception e){
            System.out.println(e.toString());
        }
        islogined = true;
        if (islogined) {
            //start monitoring
            Receiving recerving = new Receiving(login);
            try {
                recerving.start();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }


}
