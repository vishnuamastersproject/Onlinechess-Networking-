import java.util.Enumeration;
import java.util.Hashtable;

public class Locker {
	private Hashtable<String, Server> waitingUsers;

	public Locker(Hashtable<String, Server> w){
		this.waitingUsers = w;
	}
	
	public synchronized boolean addUser(String userName, Server server){
		
		if ( waitingUsers.containsKey(userName)){
			return false;
		}else{
			waitingUsers.put(userName, server);
			return true;
		}
	}
	
	public synchronized Server removeUser(String userName){
		Server server = waitingUsers.get(userName);
		waitingUsers.remove(userName);
		return server;
	}
	
	public Server getUser(String userName){
		return waitingUsers.get(userName);
	}
	
	public synchronized boolean contains(String userName) {
		return waitingUsers.containsKey(userName);
	}
	
	public int size(){
		return waitingUsers.size();
	}
	
	public Enumeration<String> keys(){
		return waitingUsers.keys();
	}
}