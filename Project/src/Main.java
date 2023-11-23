import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();

        List<Device> allDevices = new ArrayList<>(totalDevices);
        Router router = new Router(maxConnections);

//         Loop to input devices' information
        for (int i = 0; i < totalDevices; i++) {
            System.out.println("Enter device name and type (e.g., C1 mobile):");
            String name = scanner.next();
            String type = scanner.next();
            Device device = new Device(name, type);
            System.out.println("from first : " + name + " " + type);
            allDevices.add(device);
        }
        Network n = new Network(maxConnections, totalDevices, allDevices, router);
        n.link();
    }

}