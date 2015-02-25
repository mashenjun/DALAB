package assignment1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeInterface extends Remote{
	public void sendmessage(String name,Message msg) throws RemoteException;
	public void reveivemessage(Message msg) throws RemoteException;
	void register(NodeImpl c) throws RemoteException;
	void broadcast(Message msg) throws RemoteException;
	void connected() throws RemoteException;
	void adjestclock() throws RemoteException;
}
