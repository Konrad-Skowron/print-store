/**
 * @author Konrad Skorwon
 * gradlew build
 * gradlew jar
 * java -cp lab07_pop.jar pl.skowron.main.SellerApp <host>
 */
package pl.skowron.main;

import interfaces.IShop;
import pl.skowron.GUI.SellerGUI;
import pl.skowron.policy.MyPolicy;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Policy;

public class SellerApp implements Serializable {

    private static final long serialVersionUID = 1L;
    public static IShop shop;

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Policy.setPolicy(new MyPolicy());
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
        shop = (IShop) registry.lookup("Shop");

        SellerGUI.render();
    }
}
