class CustomSemaphore {
    //     variable to represent the semaphore value
    protected int value = 0;

    //     Default constructor
    protected CustomSemaphore() {
        value = 0;
    }

    //     Parameterized constructor to initialize the semaphore with an initial value
    protected CustomSemaphore(int initial) {
        value = initial;
    }

    //     Method to acquire a permit from the semaphore
    public synchronized void acquire(Device device) {
        value--;
        if (value < 0) {
            try {
                System.out.println("Connection: " + device.getName() + " Occupied and wait");
                wait();
                System.out.println("Connection:  wait");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }

    //     Method to release a permit to the semaphore
    public synchronized void release() {

        value++;

        if (value <= 0) {
            notify();
        }
    }
}