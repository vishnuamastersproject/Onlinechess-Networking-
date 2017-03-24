
public class Clock extends Thread{
	private Server server;
	public Clock(Server s) {
		this.server = s;
	}
	
	public void run(){
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.timeIsOver();
	}
}