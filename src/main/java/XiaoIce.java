import java.util.Hashtable;

public class XiaoIce{
    private XiaoIce(){
    }
    public static void main(String[] args){
        Request request = new Request();
        boolean islogined = true;

        //login
        Login login = new Login(request);
        try {
            login.loginProgress();
        } catch (Exception e){
            islogined = false;
            System.out.println(e.toString());
        }
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
