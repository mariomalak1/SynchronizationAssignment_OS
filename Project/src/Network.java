import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Network {
    private int N;
    private  int Tc;
    private List<Device> TcLines = new ArrayList<Device>(Tc);
    Router router= new Router(Tc);
    Network( int N,int Tc,List<Device> TcLines,Router router ){
        this.N = N;
        this.Tc =Tc;
        this.TcLines =TcLines;
        this.router =router;
    }
    public void link() throws InterruptedException {

            for (int i = 0; i <Tc; i++) {
                TcLines.get(i).start();
            }
        for (int i = 0; i <N; i++) {
            router.occupyConnection(TcLines.get(i));
            router.addDeviceToList(TcLines.get(i));
            router.releaseConnection(TcLines.get(i));


        }



    }






    }
