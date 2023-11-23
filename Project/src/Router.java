import java.util.ArrayList;
import java.util.List;


class Router {
    private List<Device> connectedDevices;
    private CustomSemaphore semaphore;


    //    Constructor to initialize the router and semaphore with a maximum number of connections
    public Router(int maxConnections) {
        connectedDevices = new ArrayList<Device>(maxConnections);
        semaphore = new CustomSemaphore(maxConnections);
    }

    //    Adds a device to the list of connected devices
    public void addDeviceToList(Device device){

        connectedDevices.add(device);



    }
    public  int size(){
        return connectedDevices.size();
    }


    //    Acquires a connection for a device
    public void occupyConnection(Device device){

        semaphore.acquire(device); // Try to acquire a permit

        // If the thread reaches this point, it has successfully acquired a permit

    }

    //    Releases a connection for a device
    public void releaseConnection(Device device) {
        connectedDevices.remove(device);
        semaphore.release();


    }

    private void printStatus(String message) {
        System.out.println(message);
    }
    public List<Device> getConnectedDevices() {
        return connectedDevices;
    }
}