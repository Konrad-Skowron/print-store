/**
 * @author Konrad Skorwon
 * gradlew build
 * gradlew jar
 * java -cp lab07_pop.jar pl.skowron.main.ClientApp <host>
 */
package pl.skowron.main;

import interfaces.IShop;
import interfaces.IStatusListener;
import model.Client;
import model.ItemType;
import model.OrderLine;
import model.Status;
import pl.skowron.GUI.ClientGUI;
import pl.skowron.policy.MyPolicy;

import javax.swing.*;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

public class ClientApp extends UnicastRemoteObject implements Serializable, IStatusListener {

    private static final long serialVersionUID = 1L;
    public static IShop shop;
    public static Client client;
    public static int myId;
    public static List<ItemType> items = new ArrayList<>();
    public static List<OrderLine> cart = new ArrayList<>();

    public ClientApp() throws RemoteException { }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Policy.setPolicy(new MyPolicy());
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
        shop = (IShop) registry.lookup("Shop");

        items = shop.getItemList();

        ClientGUI.render();
    }

    public static void loginClient(String name) throws RemoteException {
        client = new Client();
        client.setName(name);
        myId = shop.register(client);
    }

    @Override
    public void statusChanged(int id, Status status) throws RemoteException {
        if (ClientGUI.frame.inOrders) ClientGUI.frame.myOrders();
        else JOptionPane.showMessageDialog(null, "Order " + id + " status changed to " + status);
    }
}
