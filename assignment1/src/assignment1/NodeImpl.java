package assignment1;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class NodeImpl extends UnicastRemoteObject implements NodeInterface,Runnable{
	int NodeID;	
	String NodeName;
	private LinkedList<Message> localbuff;
	private LinkedList<Integer> localclock;
	private Random random;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected NodeImpl() throws RemoteException {
		super();
		localbuff=new LinkedList<Message>();
		localclock= new LinkedList<Integer>();
		random = new Random(1000);
	}

	private void delivery (Message msg){

		this.mergeclock(msg.vectorclock);
		System.out.println("I receive message:"+msg.body);
//		System.out.println(this.localclock);
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
	
	private boolean clocklarger(LinkedList<Integer> vc, int sender){
		@SuppressWarnings("unchecked")
		LinkedList<Integer> temp=(LinkedList<Integer>) this.localclock.clone();
		temp.set(sender, temp.get(sender)+1);
//		System.out.println("theloaclclockis:"+this.localclock+"themsgclockis"+vc+"the temp clock is"+temp);
//		System.out.println(this.NodeID);
		boolean key=true;
		for (int i=0;i<this.localclock.size();i++){
			if (temp.get(i)<vc.get(i)){
				System.out.println("the temps is"+temp.get(i)+"the msg clock is "+vc.get(i)+"the sender is"+sender);
				key = false;
			}
		}
		System.out.println(key);
		
		return key;
	}
	
	private int checklocalbuffer(){
		//System.out.print("check....");
		int key=-1;
		
		for (int i=0;i<this.localbuff.size();i++){
			if(this.clocklarger(this.localbuff.get(i).vectorclock,this.localbuff.get(i).sender)){
				key=i;
			}
		}

		return key;
	}
	
	private void setmsgclock(Message msg){
		for(int i=0;i<msg.vectorclock.size();i++){
			msg.vectorclock.set(i, this.localclock.get(i));
		}
	}

	@Override
	public void sendmessage(String name,final Message msg ) throws RemoteException {
		Registry registry = LocateRegistry.getRegistry(2000);
		//System.out.println("I want to send a msg and the clock in msg is "+msg.vectorclock);
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
		System.out.println("local buffer has "+this.localbuff.size());
		if(this.clocklarger(msg.vectorclock,msg.sender)) {
			System.out.println(" deli");
			this.delivery(msg);
			while(this.checklocalbuffer()>-1){
				int i=this.checklocalbuffer();
				this.delivery(this.localbuff.get(i));
				this.localbuff.remove(i);
			}	
		}
		else {
			this.localbuff.add(msg);
			System.out.println("not deli");
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
	public void broadcast(final Message msg,int time) throws RemoteException {
		int time1;
		int time2;
//		System.out.println("the clock size is before :"+this.localclock);
//		System.out.println("the msg clock send is before"+ msg.vectorclock);
		Timer timer = new Timer();
		this.setlocalclock();
		this.setmsgclock(msg);
//		System.out.println("the clock size is :"+this.localclock);
//		System.out.println("the msg clock send is "+ msg.vectorclock);
//		System.out.println("in the broadcast, the param msg is"+msg.vectorclock);
		switch (time){
		case 1: 
			time1=2;
			time2=8;
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						Thread.sleep(2*1000);
						sendmessage("ChatServer1", msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}).start();
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						Thread.sleep(8*1000);
						sendmessage("ChatServer2", msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}).start();
			
//			timer.schedule(new TimerTask(){
//				@Override
//				public void run() {				
//						System.out.println("I want to know the msg.vectorclock "+msg.vectorclock);
//
//				}
//			},0,1000);
//			
			/*
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					try {
						System.out.println("this is time 2 and the msg is"+msg2.vectorclock);
						
						sendmessage("ChatServer2", msg2);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},time2*1000);
			
			timer.schedule(new TimerTask(){
				@Override
				public void run() {				
						System.out.println("I want to know the msg.vectorclock "+msg2.vectorclock);

				}
			},0,1000);

			System.out.println("in the broadcast, the param msg is"+msg2.vectorclock);
			
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					try {
						System.out.println("this is time 1 and the msg is "+msg2.vectorclock);
						sendmessage("ChatServer1", msg2);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},time1*1000);*/

			
			
			break;
		case 2:
			time1=1;
			time2=1;
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					try {
						sendmessage("ChatServer0", msg);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},time1*1000);
			
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					try {
						sendmessage("ChatServer2", msg);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},time2*1000);
			
			break;
		case 3:
			time1=1;
			time2=1;
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					try {
						sendmessage("ChatServer0", msg);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},time1*1000);
			
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					try {
						sendmessage("ChatServer1", msg);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},time2*1000);
			
		default:
			time1=0;
			time2=0;
			break;
		}

//		try{
//			//Registry registry = LocateRegistry.getRegistry(2000);
//			String[] reglist = Naming.list("rmi://localhost:2000/");
//			
//
//			for(int i=0; i<reglist.length;i++){
//				String name= reglist[i].split("\\/")[reglist[i].split("\\/").length-1];
//				if (name.equals(this.NodeName)==false) {
//					sendmessage(name, msg);
//				}
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
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
			System.out.println("input \"Start\" to the start broadcast");
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		msg=in.nextLine();
		in.close();
		//final Message M =new Message(this.NodeID, this.localclock,"Hello I'm ChatNode"+this.NodeID);
		if (msg.equals("start")){
			try {
				new Thread (new Runnable() {
					@Override
					public void run() {			
							try {
								Thread.sleep(NodeID*4000+5000);
								//System.out.println("the time is"+ (NodeID*4000+5000)+"the local clock is"+localclock);
								broadcast(new Message(NodeID, localclock,"Hello I'm ChatNode"+NodeID),NodeID+1);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					
				}).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		while(true) {
//			try{
//				msg = in.nextLine();	
//				
//				this.broadcast(new Message(this.NodeID, this.localclock,msg));
//				}
//				//System.out.println("the local vector clock is : "+this.vectorclock.size());
//			catch (Exception e) {
//				e.printStackTrace();
//				in.close();
//			}
//		}
		
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
