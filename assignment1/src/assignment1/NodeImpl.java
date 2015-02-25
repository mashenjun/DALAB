package assignment1;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Scanner;

public class NodeImpl extends UnicastRemoteObject implements NodeInterface,Runnable{
	int NodeID;	
	String NodeName;
	private LinkedList<Message> localbuff;
	private LinkedList<Integer> localclock;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected NodeImpl() throws RemoteException {
		super();
		localbuff=new LinkedList<Message>();
		localclock= new LinkedList<Integer>();
	}

	private void delivery (Message msg){

		this.mergeclock(msg.vectorclock);
		System.out.println("I receive message:"+msg.body);
		System.out.println(this.localclock);
	}
	
	private void setlocalclock(){
		int temp = this.localclock.get(NodeID);
		this.localclock.set(this.NodeID, temp+1);
	} 
	
	private void mergeclock(LinkedList<Integer> vc){
		for(int i=0; i<this.localclock.size();i++){
			if (this.localclock.get(i)<vc.get(i)){
				this.localclock.set(i, vc.get(i));
			}
		}
	}
	
	private boolean clocklarger(LinkedList<Integer> vc){
		
		System.out.println("theloaclclockis:"+this.localclock+"themsgclockis"+vc);
		System.out.println(this.NodeID);
		boolean key=true;
		for (int i=0;i<this.localclock.size();i++){
			if (this.localclock.get(i)+1<vc.get(i)){
				key = false;
			}
		}
		System.out.println(key);
		return key;
	}
	private int checklocalbuffer(){
		int key=-1;
		
		for (int i=0;i<this.localbuff.size();i++){
			if(this.clocklarger(this.localbuff.get(i).vectorclock)){
				key=i;
			}
		}

		return key;
	}

	@Override
	public void sendmessage(String name,Message msg ) throws RemoteException {
		Registry registry = LocateRegistry.getRegistry(2000);
		try {
			NodeInterface nsi = (NodeInterface) registry.lookup(name);
			nsi.reveivemessage(msg);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public synchronized void reveivemessage(Message msg) throws RemoteException {
		// TODO Auto-generated method stub
		//this.setlocalclock();
		if(this.clocklarger(msg.vectorclock)) {
			System.out.print(" deli");
			this.delivery(msg);
			while(this.checklocalbuffer()>-1){
				int i=this.checklocalbuffer();
				this.delivery(this.localbuff.get(i));
				this.localbuff.remove(i);
			}	
		}
		else {
			this.localbuff.add(msg);
			System.out.print("not deli");
		}
	}
	
	
	

	@Override
	public synchronized void register(NodeImpl c) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			String[] reglist = Naming.list("rmi://localhost:2000/");
			this.NodeID=reglist.length;
			Registry registry = LocateRegistry.getRegistry(2000);
			//correct the clock
			for(int i=0; i<reglist.length;i++){
				String name= reglist[i].split("\\/")[reglist[i].split("\\/").length-1];
				NodeInterface nsi = (NodeInterface) registry.lookup(name);
				nsi.adjestclock();
				}
			
			StringBuffer namegen = new StringBuffer("rmi://localhost:2000/");
			namegen.append("ChatServer");
			namegen.append(this.NodeID);
			this.NodeName="ChatServer"+this.NodeID;
			Naming.bind(namegen.toString(), c);
			System.out.println(this.NodeName+" is ready....");
			for (int i =0; i<=this.NodeID;i++){
				this.localclock.add(0);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void broadcast(Message msg) throws RemoteException {
		this.setlocalclock();
		System.out.println("the clock size is :"+this.localclock);
		try{
			//Registry registry = LocateRegistry.getRegistry(2000);
			String[] reglist = Naming.list("rmi://localhost:2000/");

			for(int i=0; i<reglist.length;i++){
				String name= reglist[i].split("\\/")[reglist[i].split("\\/").length-1];
				if (name.equals(this.NodeName)==false) {
					sendmessage(name, msg);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connected() throws RemoteException {

		
	}
	
	@Override
	public void adjestclock() throws RemoteException {
		this.localclock.add(0);
	}

	@Override
	public void run() {
		System.out.println("Please");
		Scanner in = new Scanner(System.in);
		String msg;
		
		try {
			this.register(this);
			System.out.println("connect to the network");
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true) {
			try{
				msg = in.nextLine();	
				
				this.broadcast(new Message(this.NodeID, this.localclock,msg));
				}
				//System.out.println("the local vector clock is : "+this.vectorclock.size());
			catch (Exception e) {
				e.printStackTrace();
				in.close();
			}
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new Thread(new NodeImpl()).start();	
		}
		catch (Exception e) {
			e.printStackTrace();
		}	

	}

	

}
