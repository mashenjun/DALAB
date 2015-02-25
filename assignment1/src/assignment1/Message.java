package assignment1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;


public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LinkedList<Integer> vectorclock;
	public int sender;

	public String body;

	
	public Message(int sender, LinkedList<Integer> vectorclock, String body) {
		this.sender = sender;
		this.vectorclock = vectorclock;
		this.body = body;
	}
	
	
}
