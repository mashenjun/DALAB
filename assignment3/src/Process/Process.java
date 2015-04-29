package Process;

import Message.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mashenjun on 22-4-15.
 */
public class Process extends UnicastRemoteObject implements ProcessInterface, Runnable{

    private int round;
    private int value;
    private int ProcessID;
    private boolean decided;
    private int f;
    private String name;
    private LinkedList<String> links=new LinkedList<String>();
    private Address address;
    private Message msg;
    private AtomicInteger MeaageReceived;

    public static void main(String[] args) {
        try {
            new Thread(new Process()).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Process() throws RemoteException, UnknownHostException {
        round=1;
        decided=false;
        this.address=new Address(Inet4Address.getLocalHost().getHostAddress(),2000);
        this.MeaageReceived=new AtomicInteger();

    }

    public synchronized void register(Process c) throws RemoteException {
        try {
//            String[] reglist = Naming.list("rmi://localhost:2000");
            String[] reglist = Naming.list(this.address.toString());
            this.ProcessID=reglist.length;
            Registry registry = LocateRegistry.getRegistry(2000);

            //correct the clock
            for(int i=0; i<reglist.length;i++){
                name= reglist[i].split("\\/")[reglist[i].split("\\/").length-1];
                System.out.println("name in the list is "+name);
                ProcessInterface nsi = (ProcessInterface) registry.lookup(name);
                nsi.adjestlinks("Process"+this.ProcessID);
                this.links.add(name);

            }


            StringBuffer namegen = new StringBuffer("rmi://localhost:2000/");
            namegen.append("Process");
            namegen.append(this.ProcessID);
            this.name="Process"+this.ProcessID;
            System.out.println("the namegen is "+ address.toString()+"/"+this.name.toString());
            Naming.bind(address.toString()+"/"+this.name.toString(),c);
            //System.out.println("in the process, the length is "+Naming.list("rmi://localhost:2000/").length);
            System.out.println(this.name+" is ready...."+"and the link length is "+this.links.size());

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
        try {
            this.register(this);
            Message msg =new Message(MessageType.notification,this.round,this.value);
            while(!decided){
                this.notifacation();
                this.proposal();
                this.decision();
            }

            Object lockObject = new Object();
            synchronized (lockObject) {
                lockObject.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        System.out.println("Process stop");

    }

    @Override
    public synchronized void adjestlinks(String name) throws RemoteException {
        this.links.add(name);
    }

    @Override
    public synchronized void setF(int number) throws RemoteException {
        this.f=number;
    }

    @Override
    public synchronized void receive(Message msg) throws RemoteException {
        this.MeaageReceived.incrementAndGet();
    }



    public void broadcast(Message msg) throws RemoteException, NotBoundException {
        for ( String target :this.links){
            send(msg,target);
        }
    }

    public void send(Message msg, String target) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(2000);
        ProcessInterface nsi = (ProcessInterface) registry.lookup(target);
        nsi.receive(msg);
    }

    public void notifacation () throws RemoteException, NotBoundException {
        this.broadcast(msg);
    }

    public void proposal () {

    }

    public void decision() {

    }
}
