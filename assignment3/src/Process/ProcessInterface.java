package Process;

import Message.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by mashenjun on 29-4-15.
 */
public interface ProcessInterface extends Remote {
    void adjestlinks(String name) throws RemoteException;
    void setF(int number) throws RemoteException;
    void receive(Message msg) throws RemoteException;
}
