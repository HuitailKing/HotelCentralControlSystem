import com.bupt.Server.*;
import com.bupt.Client.*;
import com.bupt.Test.*;
public class Main {

    public static void main(String[] args) {
        try{
            String Room="Room";


            /**
             * test node init
             */
            TestNode testNode = new TestNode();
            Thread t = new Thread(testNode);
            t.start();
            Thread.sleep(2000);
            /**
             * server init
             */
            SocketServer test_server=new SocketServer();
            Thread s=new Thread(test_server);
            s.start();
            /**
             * waiting for server thread initialization finished
             */
            Thread.sleep(2000);
            /**
             * client init
             */
            for(int i=1;i<=4;i++){
                SocketClient test_client=new SocketClient(i+"");
                Thread c=new Thread(test_client);
                c.start();
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
