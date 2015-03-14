

import java.io.Serializable;
import java.util.LinkedList;


public class Message implements Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int sender;
	public String sendername;
	public int [] pair = new int[2];
	
	public Message(int sender,String sendername , int[] pair) {
		this.sender = sender;
		this.sendername = sendername;
		//this.vectorclock=new LinkedList<Integer>();
		//for(int i=0;i<vectorclock.size();i++){
		//	this.vectorclock.add(vectorclock.get(i));
		//}
		this.pair = pair;
	}
	
	
	
}
