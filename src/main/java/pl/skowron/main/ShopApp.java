/**
 * @author Konrad Skorwon
 * gradlew build
 * gradlew jar
 * java -cp lab07_pop.jar pl.skowron.main.ShopApp <port>
 */
package pl.skowron.main;

import interfaces.IShop;
import interfaces.IStatusListener;
import model.*;
import pl.skowron.GUI.ShopGUI;
import pl.skowron.policy.MyPolicy;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopApp extends UnicastRemoteObject implements Serializable, IShop {

    private static final long serialVersionUID = 1L;
    public static int uniqueId = 1;
    public static HashMap<Integer, String> idClient = new HashMap<>();
    public static List<ItemType> items = new ArrayList<>();
    public static List<SubmittedOrder> orders = new ArrayList<>();
    public static Map<Integer, IStatusListener> subscribers = new HashMap<>();

    public ShopApp() throws RemoteException { }

    public static void main(String[] args) throws RemoteException {
        Policy.setPolicy(new MyPolicy());
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        ShopApp shop = new ShopApp();
        shop.addItem("Biala koszulka", 49.99F, 1);
        shop.addItem("Czarna koszulka", 49.99F, 1);
        shop.addItem("Koszulka ze wzorem", 54.99F, 1);
        shop.addItem("Kubek z uszkiem", 19.99F, 2);
        shop.addItem("Kubek do kawy", 22.99F, 2);
        shop.addItem("Kubek termiczny", 29.99F, 2);
        shop.addItem("Duzy kubek", 24.99F, 2);
        shop.addItem("Naklejka", 1.99F, 3);
        shop.addItem("Naklejka blyszczaca", 2.99F, 3);
        shop.addItem("Naklejka z brokatem", 3.99F, 3);
        shop.addItem("Torba na zakupy", 4.99F, 4);

        Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
        registry.rebind("Shop", shop);

        ShopGUI.render();
    }

    public static String getCategory(int category) {
        switch (category) {
            case 1:
                return "T-Shirts";
            case 2:
                return "Mugs";
            case 3:
                return "Stickers";
            default:
                return "Others";
        }
    }

    public void addItem(String name, float price, int category) {
        ItemType item = new ItemType();
        item.setName(name);
        item.setPrice(price);
        item.setCategory(category);
        items.add(item);
    }

    @Override
    public int register(Client c) throws RemoteException {
        idClient.put(uniqueId, c.getName());
        return uniqueId++;
    }

    @Override
    public List<ItemType> getItemList() throws RemoteException {
        return items;
    }

    @Override
    public int placeOrder(Order o) throws RemoteException {
        SubmittedOrder submittedOrder = new SubmittedOrder(o);
        orders.add(submittedOrder);
        if (ShopGUI.frame.inOrders) ShopGUI.frame.orders();
        return submittedOrder.getId();
    }

    @Override
    public List<SubmittedOrder> getSubmittedOrders() throws RemoteException {
        return orders;
    }

    @Override
    public boolean setStatus(int id, Status s) throws RemoteException {
        for (SubmittedOrder order : orders) {
            if (order.getId() == id) {
                if (order.getStatus() == s) return false;

                order.setStatus(s);

                if (ShopGUI.frame.inOrders) ShopGUI.frame.orders();

                int clientId = order.getOrder().getClientID();
                if (subscribers.containsKey(clientId)) {
                    subscribers.get(clientId).statusChanged(id, s);
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public Status getStatus(int id) throws RemoteException {
        for (SubmittedOrder order : orders) {
            if (order.getId() == id) {
                return order.getStatus();
            }
        }
        return null;
    }

    @Override
    public boolean subscribe(IStatusListener ic, int clientId) throws RemoteException {
        subscribers.put(clientId, ic);
        return true;
    }

    @Override
    public boolean unsubscribe(int clientId) throws RemoteException {
        subscribers.remove(clientId);
        return true;
    }
}
