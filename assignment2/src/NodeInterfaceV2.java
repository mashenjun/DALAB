

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeInterfaceV2 extends Remote{
	public void sendmessage(String name,final Message msg) throws RemoteException;
	public void receivemessage(Message msg) throws RemoteException;
	void register(NodeImplV2 c) throws RemoteException;
	void adjestlinks(String name) throws RemoteException;
	void sendAck(String name) throws RemoteException;
	void receiveAck(String name) throws RemoteException;
	void close() throws RemoteException;
}
