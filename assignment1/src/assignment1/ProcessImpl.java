package assignment1;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class ProcessImpl extends UnicastRemoteObject implements ProcesserInterface,Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private NetworkInterface NS;
	private int ProcessID;
	
	public ArrayList<Integer> vectorclock;

	protected ProcessImpl(NetworkInterface ns) throws RemoteException {
		super();
		NS = ns;
		NS.connected();
		NS.register(this);
		/*int listsize= NS.getlsitlength();
		this.setProcessID(listsize);
		this.initvectorclock(listsize);*/
//		try {
//			StringBuffer temp = new StringBuffer();
//			temp.append("process");
//			temp.append(this.ProcessID);
//			LocateRegistry.getRegistry(2000);
//			Naming.bind("rmi://localhost:2000/", new ProcessImp;());
//			System.out.println("Client is ready....");
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
	
	}
	
	public void initvectorclock(int num){
		for (int i =0; i<num; i++) {
			vectorclock.add(0);
		}
	}

	@Override
	public void run() {
		
/*		try {
			StringBuffer temp = new StringBuffer();
			temp.append("process");
			temp.append(this.ProcessID);
			LocateRegistry.getRegistry(2000);
			Naming.bind("rmi://localhost:2000/", new NetworkImpl());
			System.out.println("Network is ready....");
		}
		catch (Exception e){
			e.printStackTrace();
		}*/
		
		Scanner in = new Scanner(System.in);
		String msg;
		
		while(true) {
			try{
				msg = in.nextLine();
				NS.broadcast(msg);
				//System.out.println("the local vector clock is : "+this.vectorclock.size());
				
			}
			catch (Exception e) {
				e.printStackTrace();
				in.close();
			}
		}
		
	}

	@Override
	public void sendmessage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reveivemessage(String s) {
		// TODO Auto-generated method stub
		System.out.println("Message: "+s);
		
	}
	
	public static void main (String[] args) {
		//String url = "rmi://localhost:2000/ChatServer";
		
		try {
			Registry registry = LocateRegistry.getRegistry(2000);
			NetworkInterface nsi = (NetworkInterface) registry.lookup("rmi://localhost:2000/ChatServer");
			
			new Thread(new ProcessImpl(nsi)).start();
			System.out.println("Client start");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
		

	}

/*	public int getProcessID() {
		return ProcessID;
	}

	public void setProcessID(int processID) {
		ProcessID = processID;
	}

	@Override
	public void setvectorclock( int value) throws RemoteException {
		// TODO Auto-generated method stub
		this.vectorclock.set(this.ProcessID-1, value);
		
	}

	@Override
	public void addvectorclock(int value) throws RemoteException {
		// TODO Auto-generated method stub
		//this.vectorclock.add(value);
	}*/

}
