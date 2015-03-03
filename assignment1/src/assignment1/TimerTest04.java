package assignment1;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTest04 {
    private Timer timer;
    public long start;   
    private int key;
    
    public TimerTest04(){
        this.timer = new Timer();
        start = System.currentTimeMillis();
        key=0;
    }
    
    public void timerOne(final int a){
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("timerOne invoked ,the time:" + (System.currentTimeMillis() - start)+"print :"+a);
               
  
            }
        }, 3000);
    }
    
    public void timerTwo(final int a){
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("timerTwo invoked ,the time:" + (System.currentTimeMillis() - start)+"print :"+a);
            }
        }, 10000);
    }
    
    public void timerThree(){
    	
    }
    
    public void callthetime1(int a){
        TimerTest04 test = new TimerTest04();
        test.timerOne(a);
        test.timerTwo(a);
    }
    public void callthetime2(final int a){
    	final int b =a;
    	System.out.println("callthetime2 is called");
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("timerOne invoked ,the time:" + (System.currentTimeMillis() - start)+"print :"+b);
  
            }
        }, 5000);
        
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("timerTwo invoked ,the time:" + (System.currentTimeMillis() - start)+"print :"+b);
  
            }
        }, 1000);
        
        timer.schedule(new TimerTask() {
            public void run() {
            }
        }, 3000);
        
    }
    
    
    public static void main(String[] args) throws Exception {
    	TimerTest04 time4 = new TimerTest04();
    	time4.callthetime2(time4.key);
    	time4.key=100;
    }
}