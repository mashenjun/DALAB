

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Timestamp;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;



public class NodeImplV2 extends UnicastRemoteObject implements NodeInterfaceV2,Runnable{
	private static final long serialVersionUID = 1L;
	
	int NodeID;	
	String NodeName;
	//this value is used to decided captured or not. 
	private int idvalue;

	private LinkedList<String> links;
	private LinkedList<String> candidatelinks;
	private LinkedList<Message> storage; 
	private int candidateLevel = -1;
	private int ordinaryLevel = -1;
	private boolean isElected = false;
	private int sendcount = 0;
	private int ackcount = 0;
	boolean isCandidate=false;
	public Timestamp d;
	private int[] pair = new int[2];
	private Random random;

	private Timer timer=new Timer();

	
	public static void main(String[] args) {
		try {
			new Thread(new NodeImplV2()).start();	
		}
		catch (Exception e) {
			e.printStackTrace();
		}	

	}

	protected NodeImplV2() throws RemoteException {
		super();
		this.links = new LinkedList<String>(); 
		this.candidatelinks = new LinkedList<String>();
		this.storage = new LinkedList<Message>();
		random = new Random();
		int i = random.nextInt(1000);
		this.setIdvalue(i);
		if(i>=500){
			isCandidate = true;
		}	
		
	}
	
	public int getIdvalue() {
		return idvalue;
	}

	public void setIdvalue(int idvalue) {
		this.idvalue = idvalue;
	}
	
	private void performCandidateRound(){
		System.out.println("candidateround start, candidatelevel is "+this.candidateLevel);
		int k ;
		this.candidateLevel++;
		if (this.candidateLevel % 2 ==0){
			if (this.links.size()==0) {
				this.isElected = true;
				System.out.println(this.NodeName+" is elected");
				this.closeall();				
			}
			else {
				 k = (int) Math.min(Math.pow(2D, (double) candidateLevel / 2D), this.links.size());
				 this.updatepair(this.candidateLevel, this.idvalue);
				 //System.out.println("the current link's size is "+ this.links.size());
				for (int i = 0; i< k;i++ ) {
					try {
						this.sendmessage(this.links.get(i), new Message (this.NodeID,this.NodeName,this.pair));
						System.out.println(this.NodeName+" send to "+this.links.get(i)+" with pair "+pair[0]+", "+pair[1]+" to "+this.links.get(i));
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for (int i = 0; i< k;i++){
					this.links.remove();
				}
				this.sendcount=k;
			}
		}
		else {
			System.out.println("the ackcount is "+this.ackcount+" the sendcount is "+this.sendcount);
			if(this.ackcount<this.sendcount) {
				this.isCandidate=false;
			System.out.println("no more a candidate");
			}
			this.sendcount=0;
			this.ackcount=0;
		}
		System.out.println("candidateround over, candidatelevel is "+this.candidateLevel);
	}
	
	private void performOrdinaryRound(){
		System.out.println("OrdinaryRound start, Ordinarylevel is "+this.ordinaryLevel);
		this.ordinaryLevel++;
		//System.out.println("the storage size is"+ this.storage.size());
		for (int i = 0; i<this.storage.size();i++) {
			//System.out.println("the local pair is <"+this.ordinaryLevel+","+this.idvalue+">"+" the msg pair is <"+this.storage.get(i).pair[0]+","+this.storage.get(i).pair[1]+">");
			if (NodeImplV2.pairIsLarger(this.ordinaryLevel, this.idvalue, this.storage.get(i).pair[0], this.storage.get(i).pair[1])){
				this.ordinaryLevel = this.storage.get(i).pair[0];
				this.idvalue = this.storage.get(i).pair[1];
				System.out.println("the current pair change to <"+this.ordinaryLevel+","+this.idvalue+">");
				if(this.candidatelinks.size()>0){
					this.candidatelinks.remove();
				}
				this.candidatelinks.add(this.storage.get(i).sendername);
				this.isCandidate=false;
			}

		}
		//System.out.println("the candidatelinks size is "+this.candidatelinks.size());
		for(int i=0;i<this.candidatelinks.size();i++){
			try {
				System.out.println("send ack to "+this.candidatelinks.get(i));
				sendAck(this.candidatelinks.get(i));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		this.storage.clear();
		this.candidatelinks.clear();
		System.out.println("OrdinaryRound over, Ordinarylevel is "+this.ordinaryLevel);
		
	}
	
	private void finalizeRound(){
		System.out.println("start the finalizeRound");

	}
	
	public static boolean pairIsLarger(int referenceLevel, int referenceId,
			int otherLevel, int otherId) {
		return otherLevel > referenceLevel
				|| (otherLevel == referenceLevel && otherId > referenceId);
	}



	@Override
	public void sendmessage(String name,final Message msg ) throws RemoteException {
		Registry registry = LocateRegistry.getRegistry(2000);
		//System.out.println("I want to send a msg and the clock in msg is "+msg.vectorclock);
		try {
			NodeInterfaceV2 nsi = (NodeInterfaceV2) registry.lookup(name);
			nsi.receivemessage(msg);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public synchronized void receivemessage(Message msg) throws RemoteException {
		// TODO Auto-generated method stub
		//this.setlocalclock();
		System.out.println(this.NodeName+" receive message form "+msg.sendername+", local pair is "+this.ordinaryLevel+","+this.idvalue
				 +", the msg's pair is "+msg.pair[0]+","+msg.pair[1]);
		this.storage.add(msg);
	}
	
	
	

	@Override
	public synchronized void register(NodeImplV2 c) throws RemoteException {
		try {
			String[] reglist = Naming.list("rmi://localhost:2000/");
			this.NodeID=reglist.length;
			Registry registry = LocateRegistry.getRegistry(2000);
			//correct the clock
			for(int i=0; i<reglist.length;i++){
				String name= reglist[i].split("\\/")[reglist[i].split("\\/").length-1];
				NodeInterfaceV2 nsi = (NodeInterfaceV2) registry.lookup(name);
				nsi.adjestlinks("ChatServer"+this.NodeID);
				this.links.add(name);
				}
			
			
			StringBuffer namegen = new StringBuffer("rmi://localhost:2000/");
			namegen.append("ChatServer");
			namegen.append(this.NodeID);
			this.NodeName="ChatServer"+this.NodeID;
			Naming.bind(namegen.toString(), c);
			//System.out.println("in the process, the length is "+Naming.list("rmi://localhost:2000/").length);
			System.out.println(this.NodeName+" is ready....");
			System.out.println(this.NodeName+" the candidate value is "+this.isCandidate+" and the idValue is "+this.idvalue);

		}
		catch (Exception e){
			e.printStackTrace();
		}

	}
	
	
	public void adjestlinks(String name) throws RemoteException {
		this.links.add(name);
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
			e1.printStackTrace();
		}
		msg=in.nextLine();
		in.close();
		if (msg.equals("start")){
			pair[0]=candidateLevel;
			pair[1]=NodeID;
			try {
				new Thread (new Runnable() {
					@Override
					public void run() {			
							try {
								int timesetting = String.valueOf(links.size()).length();
								int roundtime = 10+2*(timesetting-1);
								int halfroundtime = (int) (0.5*roundtime);
								Thread.sleep(random.nextInt(5)*100+halfroundtime*1000);
								
								timer.schedule(new TimerTask(){   
									public void run(){
										if(isCandidate==true){
											performCandidateRound();
										}
									}},500,roundtime*1000);
								
								
								timer.schedule(new TimerTask(){   
									public void run(){   
										performOrdinaryRound();   
									}},halfroundtime*1000,roundtime*1000);
								
								timer.schedule(new TimerTask(){   
									public void run(){   
										finalizeRound();   
									}},roundtime*1000,roundtime*1000);
								
								
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					
				}).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * receiveAck will only increase the   ackcount value
	 */
	@Override
	public void receiveAck(String name) throws RemoteException {
		System.out.println("receive the ACK from "+name);
		this.ackcount++;
	}
	
	public void updatepair(int level,int idvalue){
		this.pair[0] = level;
		this.pair[1] = idvalue;
	}

	/**
	 * sendAck will notify the sender  
	 */
	@Override
	public void sendAck(String name) throws RemoteException {
		Registry registry = LocateRegistry.getRegistry(2000);
		try {
			NodeInterfaceV2 nsi = (NodeInterfaceV2) registry.lookup(name);
			nsi.receiveAck(this.NodeName);
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	public void closeall() {
		try {
			Thread.currentThread().sleep(1000);
			String[] reglist = Naming.list("rmi://localhost:2000/");
			Registry registry = LocateRegistry.getRegistry(2000);
			//correct the clock
			for(int i=0; i<reglist.length;i++){
				String name= reglist[i].split("\\/")[reglist[i].split("\\/").length-1];
				if(!name.equals(this.NodeName)) {
					NodeInterfaceV2 nsi = (NodeInterfaceV2) registry.lookup(name);
					nsi.close();
				}
			}
		} catch (InterruptedException | MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
			this.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws RemoteException {
		System.out.println("Election is completed......closing.....");
		try {			
			Registry registry = LocateRegistry.getRegistry(2000);
			registry.unbind(this.NodeName);
			UnicastRemoteObject.unexportObject(this, false);
			
		} catch (Exception e){
			e.printStackTrace();
		}
		  new Thread() {
			    @Override
			    public void run() {
			      try {
			        sleep(2000);
			      } catch (InterruptedException e) {
			    	  e.printStackTrace();
			      }
			      System.out.println("done");
			      System.exit(0);
			    }
			  }.start();
	}
}
