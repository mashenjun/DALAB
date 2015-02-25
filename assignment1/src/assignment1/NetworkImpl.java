package assignment1;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class NetworkImpl extends UnicastRemoteObject implements NetworkInterface {
	
	private static final long serialVersionUID = 1L;
	private LinkedList<ProcesserInterface> ProcessList;

	protected NetworkImpl() throws RemoteException {
		super();
		ProcessList = new LinkedList<ProcesserInterface>();
	}

	@Override
	public synchronized void register(ProcesserInterface c) throws RemoteException {	
		ProcessList.add(c);
		System.out.println(this.ProcessList.size());
		/*for (ProcesserInterface element : this.ProcessList) {
			element.addvectorclock(0);
		}*/
		
	}

	@Override
	public synchronized void broadcast(String s) throws RemoteException {
		for (ProcesserInterface element : this.ProcessList){
			element.reveivemessage(s);
		}
	}
	
	public static void main(String[] args){
		
		try {
			//LocateRegistry.getRegistry(2000);

			Naming.bind("rmi://localhost:2000/ChatServer", new NetworkImpl());
			Naming.bind("rmi://localhost:2000/ChatServer12", new NetworkImpl());
			String[] reglist = Naming.list("rmi://localhost:2000/");
			System.out.println(reglist.length);
			//String[] reglist = Naming.list("rmi://localhost:2000/");
			
			System.out.println(reglist[1].split("\\/")[reglist[1].split("\\/").length-1]);
			System.out.println("Network is ready....");

		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void connected() throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("connect to Server");
		
	}
/*
	@Override
	public int getlsitlength() throws RemoteException {

		return this.ProcessList.size();
	}*/
}
