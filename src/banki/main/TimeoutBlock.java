package banki.main;

public class TimeoutBlock {

	 private final long timeoutMilliSeconds;
	    private long timeoutInteval=100;

	    public TimeoutBlock(long timeoutMilliSeconds){
	        this.timeoutMilliSeconds=timeoutMilliSeconds;
	    }

	    public void addBlock(Runnable runnable) throws Throwable{
	        long collectIntervals=0;
	        Thread timeoutWorker=new Thread(runnable);
	        timeoutWorker.start();
	        do{ 
	            if(collectIntervals>=this.timeoutMilliSeconds){
	            	
	            	throw new Exception("<<<<<<<<<<****>>>>>>>>>>> Timeout Block Execution Time Exceeded In "+timeoutMilliSeconds+" Milli Seconds. Thread Block Terminated.");
	            }
	            collectIntervals+=timeoutInteval;           
	            Thread.sleep(timeoutInteval);

	        }while(timeoutWorker.isAlive());
	    }

	    public long getTimeoutInteval() {
	        return timeoutInteval;
	    }

	    public void setTimeoutInteval(long timeoutInteval) {
	        this.timeoutInteval = timeoutInteval;
	    }
	}
