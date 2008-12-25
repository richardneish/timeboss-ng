package hu.schmidtsoft.timeboss.standalone;

public class TimerThread extends Thread {
	private boolean exit=false;
	public static final long maxSleepTime=60000;
	public static final long minSleepTime=1000;
	private long normalizeSleepTime(long sleepTime) {
		sleepTime=Math.max(minSleepTime, sleepTime);
		sleepTime=Math.min(maxSleepTime, sleepTime);
		return sleepTime;
	}
	MyRunnable toDo;
	public TimerThread(MyRunnable toDo) {
		super();
		this.toDo = toDo;
	}
	@Override
	public void run() {
		long sleepTime=maxSleepTime;
		while(!exit)
		{
			synchronized (this) {
				// empty sync block to let exit happen
				if(exit)
				{
					return;
				}
			}
			try
			{
				sleepTime=maxSleepTime;
				try
				{
					sleepTime=toDo.myRun();
					sleepTime=normalizeSleepTime(sleepTime);
				}catch(Throwable t)
				{
					t.printStackTrace();
				}
				try
				{
					Thread.sleep(sleepTime);
				}catch(InterruptedException e)
				{}
			}catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
	}
	void breakSleep()
	{
		interrupt();
	}
	public void exit()
	{
		synchronized (this) {
			exit=true;
		}
		breakSleep();
	}
}
