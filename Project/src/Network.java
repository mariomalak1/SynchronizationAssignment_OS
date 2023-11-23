import java.util.List;

public class Network {
    private final int MaxConnections;
    private final int Tc;
    private final List<Device> TcLines;
    Router router;
    Network( int N,int Tc,List<Device> TcLines,Router router ){
        this.MaxConnections = N;
        this.Tc =Tc;
        this.TcLines =TcLines;
        this.router =router;
    }
    public void link() {
        for (int i = 0; i < Tc; i++) {
            TcLines.get(i).start();
        }
        for (int i = 0; i < Tc; i++) {
            router.occupyConnection(TcLines.get(i));
            router.addDeviceToList(TcLines.get(i));
            router.releaseConnection(TcLines.get(i));
        }
    }
}
