	
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
	
public class RMIServer2 extends UnicastRemoteObject implements Hello {
	
    
    public RMIServer2() throws java.rmi.RemoteException{
    	super();
    }
    public String sayHello() {
	int l=10/0;
	return "Hello, world!";
    }
	
    public static void main(String args[]) {
	
	try {
	    RMIServer2 obj = new RMIServer2();
	    //Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);
	    //System.out.println(stub);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry();
	    registry.bind("Hello", obj);
	    //Naming.rebind("rmi://localhost/Hello",obj);
	    System.err.println("Server ready");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
    }
}