import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Router {
    private final CustomeSemaphore semaphore;
    private final List<Device> connections;
    private int maxCon;
    private int connectionNumber = 1; // initialize connection number
    private BufferedWriter bufferedWriter;

    public Router(int maxConnections) {
        semaphore = new CustomeSemaphore(maxConnections);
        connections = new ArrayList<>(maxConnections);
        maxCon = maxConnections;

        try {
            File file = new File("output.txt");
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(Device device) {
        try {
            semaphore.acquire();
            connections.add(device);
            writeToFile("(" + device.get_Name() + ")(" + device.getType() + ") arrived");
            occupyConnection(device);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(Device device, int conNum) {
        writeToFile("Connection " + conNum + ": " + device.get_Name() + " Logged out");
        releaseConnection(device);
        semaphore.release();
        handleWaitingDevices();
    }

    private void occupyConnection(Device device) {
        if (connectionNumber <= maxCon) {
            int occupiedConnectionNumber = connectionNumber++;
            writeToFile("Connection " + occupiedConnectionNumber + ": " + device.get_Name() + " Occupied");
            device.setConnectionNumber(occupiedConnectionNumber);
            device.performOnlineActivity();
        } else {
            writeToFile(device.get_Name() + "(" + device.getType() + ") arrived and waiting");
        }
    }

    private void handleWaitingDevices() {
        List<Device> waitingDevices = new ArrayList<>(connections);
        for (Device device : waitingDevices) {
            if (device.isWaiting()) {
                occupyConnection(device);
                break;
            }
        }
    }

    private void releaseConnection(Device device) {
        connections.remove(device);
    }

    public int getConnectionNum() {
        return connectionNumber;
    }

    public void closeFile() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Device extends Thread {
    private final Router router;
    private final String deviceName;
    private final String deviceType;
    private int connectionNumber;
    private boolean isWaiting = true;

    public String get_Name() {
        return deviceName;
    }

    public String getType() {
        return deviceType;
    }

    public Device(Router router, String deviceName, String deviceType) {
        this.router = router;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
    }

    @Override
    public void run() {
        router.connect(this);
        // Simulate online activity
        try {
            sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        router.disconnect(this, connectionNumber);
    }

    public void performOnlineActivity() {
        router.writeToFile("Connection " + connectionNumber + ": " + deviceName + " login");
        router.writeToFile("Connection " + connectionNumber + ": " + deviceName + " performs online activity");
        isWaiting = false;
    }

    public void setConnectionNumber(int connectionNumber) {
        this.connectionNumber = connectionNumber;
    }

    public boolean isWaiting() {
        return isWaiting;
    }
}

class CustomeSemaphore {
    private int permits;

    public CustomeSemaphore(int initialPermits) {
        this.permits = initialPermits;
    }

    public synchronized void acquire() throws InterruptedException {
        while (permits == 0) {
            wait();
        }
        permits--;
    }

    public synchronized void release() {
        permits++;
        notify();
    }
}

class Network {
    public static void main(String[] args) {
        // Get input values
        Scanner scanner = new Scanner(System.in);

        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();

        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();

        Router router = new Router(maxConnections);
        List<Device> allDevices = new ArrayList<>();

//         Loop to input devices' information
        for (int i = 0; i < totalDevices; i++) {
            System.out.println("Enter device name and type (e.g., C1 mobile):");
            String name = scanner.next();
            String type = scanner.next();
            Device device = new Device(router, name, type);
            allDevices.add(device);
        }

        for (Device device : allDevices){
            device.start();
        }

        // Wait for all devices to finish
        for (Device device : allDevices) {
            try {
                device.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Close the file after all devices finish
        router.closeFile();
    }
}
