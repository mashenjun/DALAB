import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class start {
	public static void main (String[] args) throws IOException{	
		 final ArrayList<Process> list = new ArrayList<Process>();
		 
		for (int i=0; i<Integer.parseInt(args[0]);i++){

			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ProcessBuilder pb = new ProcessBuilder("java","NodeImplV2");
			Process p= pb.start();
			synchronized(list){
				list.add(p);
				}
			final BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			new Thread(new Runnable(){

				@Override
				public void run() {

					 // BufferedInputStream in = new BufferedInputStream(list.get(i).getInputStream());   
					  BufferedReader br = new BufferedReader(new InputStreamReader(in));   
					  String s;    
					  try {
						while ((s = br.readLine()) != null)    
							  System.out.println(s);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
				}
				
			}).start();

		}
		
		
		System.out.println("input start to begin");
		Scanner userin = new Scanner(System.in);
		String msg;
		msg=userin.nextLine();
		userin.close();
		
		if (msg.equals("start")) {
			for (int i = 0; i<list.size();i++ ) {
				OutputStream fos = list.get(i).getOutputStream(); 
				 //final BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
				
				
				PrintStream ps = new PrintStream(fos);
				ps.print("start\n");
				ps.flush();
				ps.close();

			}
		}
		


	}
}