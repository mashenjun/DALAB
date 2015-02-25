package assignment1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NetworkInterface extends Remote{
	void register(ProcesserInterface c) throws RemoteException;
	void broadcast(String s) throws RemoteException;
	void connected() throws RemoteException;
	
}
