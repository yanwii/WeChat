import org.omg.Messaging.SYNC_WITH_TRANSPORT;

public class Test {
    public static void main(String[] args){
        Test t = new Test();
        t.main();
    }

    public void main(){
        String time = String.valueOf(Math.random());
        System.out.println(time.substring(2, 17));
    }
}
