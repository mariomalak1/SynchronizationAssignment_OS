import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

class Router {
    public CustomerSemaphore semaphore;
    public boolean[] connections; // Array to track the status of each connection
    private int maxCon; // Maximum number of connections allowed
    private BufferedWriter bufferedWriter; // BufferedWriter to write to the output file

    // Constructor for the Router class
    public Router(int maxConnections) {
        this.semaphore = new CustomerSemaphore(maxConnections);
        this.connections = new boolean[maxConnections]; // Initialize connection status array
        this.maxCon = maxConnections;

        try {
            File file = new File("output.txt");
            FileWriter fileWriter = new FileWriter(file);
            this.bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to connect a device to the router
    public synchronized int connect(Device device) throws InterruptedException {
        for (int i = 0; i < maxCon; i++) {
            if (!connections[i]) { // Check if the connection is available
                device.setConnectionNumber(i + 1); // Assign the connection number to the device
                connections[i] = true; // Mark the connection as occupied
                sleep(100);
                break;
            }
        }
        return device.getConnectionNumber(); // Return the connection number assigned to the device
    }

    // Method to disconnect a device from the router
    public synchronized void disconnect(Device device) {
        writeToFile("Connection " + device.getConnectionNumber() + ": " + device.get_Name() + " Logged out");
        System.out.println("Connection " + device.getConnectionNumber() + ": " + device.get_Name() + " Logged out");
        connections[device.getConnectionNumber() - 1] = false; // Mark the connection as available
        notify(); // Notify waiting threads that a connection is available
    }

    // Method to write messages to the output file
    public void writeToFile(String message) {
        try {
            this.bufferedWriter.write(message);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to close the output file
    public void closeFile() {
        try {
            this.bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Device extends Thread {
    public final Router router;
    private final String deviceName;
    private final String deviceType;
    private int connectionNumber;

    // Constructor for the Device class
    public Device(Router router, String deviceName, String deviceType) {
        this.router = router;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        connectionNumber = 1;
    }

    // Run method for the Device thread
    @Override
    public void run() {
        try {
            router.semaphore.acquire(this, router); // Acquire a permit from the semaphore
            connectionNumber = router.connect(this); // Connect to the router and get the connection number
            router.writeToFile("Connection " + connectionNumber + ": " + deviceName + " Occupied");
            System.out.println("Connection " + connectionNumber + ": " + deviceName + " Occupied");
            performOnlineActivity();
            router.disconnect(this); // Disconnect from the router
            router.semaphore.release(); // Release the permit to the semaphore
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method representing online activity performed by the device
    public void performOnlineActivity() throws InterruptedException {
        router.writeToFile("Connection " + connectionNumber + ": " + deviceType + " login");
        router.writeToFile("Connection " + connectionNumber + ": " + deviceType + " Performs online activity");
        System.out.println("Connection " + connectionNumber + ": " + deviceType + " login");
        System.out.println("Connection " + connectionNumber + ": " + deviceType + " Performs online activity");
        sleep(2000);
    }

    // Getter method to retrieve the device name
    public String get_Name() {
        return deviceName;
    }

    // Getter method to retrieve the device type
    public String getType() {
        return deviceType;
    }

    // Setter method to set the connection number
    public void setConnectionNumber(int connectionNumber) {
        this.connectionNumber = connectionNumber;
    }

    // Getter method to retrieve the connection number
    public int getConnectionNumber() {
        return connectionNumber;
    }
}

class CustomerSemaphore {
    private int permits;

    // Constructor for the CustomerSemaphore class
    public CustomerSemaphore(int initialPermits) {
        this.permits = initialPermits;
    }

    // Method to acquire permits from the semaphore
    public synchronized void acquire(Device device, Router router) throws InterruptedException {
        permits--;
        if (permits < 0) {
            router.writeToFile(device.get_Name() + " (" + device.getType() + ")" + " arrived and waiting");
            System.out.println(device.get_Name() + " (" + device.getType() + ")" + " arrived and waiting");
            wait(); // Wait until a permit is available
        } else {
            router.writeToFile(device.get_Name() + " (" + device.getType() + ")" + " arrived");
            System.out.println(device.get_Name() + " (" + device.getType() + ")" + " arrived");
        }

        device.router.connect(device); // Connect to the router
    }

    // Method to release permits to the semaphore
    public synchronized void release() {
        permits++;
        if (permits <= 0) {
            notify(); // Notify waiting threads that a permit is available
        }
    }
}

class Network {
    public static void main(String[] args) throws InterruptedException {
        // Get input values
        Scanner scanner = new Scanner(System.in);

        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();

        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();

        Router router = new Router(maxConnections);
        List<Device> allDevices = new ArrayList<>();

        // Loop to input devices' information
        for (int i = 0; i < totalDevices; i++) {
            System.out.println("Enter device name and type (e.g., C1 mobile):");
            String name = scanner.next();
            String type = scanner.next();
            Device device = new Device(router, name, type);
            allDevices.add(device);
        }

        // Let all devices start connecting to the router
        for (Device device : allDevices) {
            sleep(100);
            device.start();
        }

        // Wait for all devices to finish before closing the file
        for (Device device : allDevices) {
            device.join();
        }

        // Close the file after all devices finish
        router.closeFile();
    }
}
