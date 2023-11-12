public class Device extends Thread{
    String name;
    String type;

    public Device(String name, String type) {
    }

    public void set_name(String name){
        this.name = name;
    }
    public void set_type(String type){
        this.type = type;
    }

    @Override
    public void run(){
        connect();
        perform();
        disconnect();
    }
    public void connect(){
        System.out.println(name + " login");
    }
    public void disconnect(){
        System.out.println(name + " logout");
    }
    public void perform(){
        System.out.println(name + " perform online activity");
    }

}