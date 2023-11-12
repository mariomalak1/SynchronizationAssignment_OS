import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Network {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();

        List<Device> allDevices = new ArrayList<Device>(totalDevices);
        Router router = new Router(maxConnections);

//         Loop to input devices' information
        for (int i = 0; i < totalDevices; i++) {
            System.out.println("Enter device name and type (e.g., C1 mobile):");
            String name = scanner.next();
            String type = scanner.next();
            Device device = new Device(name, type);
            allDevices.add(device);
        }

//
//        Here's the logic of the network
//

    }
}