package assignment1;

import java.io.Serializable;
import java.util.LinkedList;


public class Message implements Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LinkedList<Integer> vectorclock;
	public int sender;

	public String body;

	
	public Message(int sender, LinkedList<Integer> vectorclock, String body) {
		//System.out.println("msg creating, I got a "+vectorclock);
		this.sender = sender;
		this.vectorclock=new LinkedList<Integer>();
		for(int i=0;i<vectorclock.size();i++){
			this.vectorclock.add(vectorclock.get(i));
		}
		//this.vectorclock = (LinkedList<Integer>) vectorclock.clone();
		this.body = body;
	}
	
	
	
}
