package assignment1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcesserInterface extends Remote{
	public void sendmessage() throws RemoteException;
	
	public void reveivemessage(String s) throws RemoteException;
	

}
