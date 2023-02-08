package pl.skowron.GUI;

import interfaces.IStatusListener;
import model.ItemType;
import model.Order;
import model.OrderLine;
import model.SubmittedOrder;
import pl.skowron.main.ClientApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

public class ClientGUI extends JFrame {

    public static ClientGUI frame;
    public JPanel shoppingPlane;
    public boolean inOrders = false;
    public boolean subscribed = false;


    public static void render() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        EventQueue.invokeLater(() -> {
            try {
                frame = new ClientGUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ClientGUI() {
        setResizable(false);
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(450, 300, 450, 450);
        JPanel loginPlane = new JPanel();
        loginPlane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(loginPlane);
        loginPlane.setLayout(null);

        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setBounds(10, 175, 414, 14);
        loginPlane.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(10, 200, 414, 20);
        nameField.setHorizontalAlignment(SwingConstants.CENTER);
        nameField.setColumns(10);
        loginPlane.add(nameField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            String name = nameField.getText();
            if (name.length() < 1) {
                JOptionPane.showMessageDialog(null, "Enter your name");
                return;
            }
            try {
                ClientApp.loginClient(name);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            setTitle(ClientApp.client.getName());
            shop();
        });
        loginBtn.setBounds(172, 231, 89, 23);
        loginPlane.add(loginBtn);
    }

    public void shop() {
        shoppingPlane = new JPanel();
        shoppingPlane.setBounds(0, 0, 434, 411);
        shoppingPlane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(shoppingPlane);
        shoppingPlane.setLayout(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, 434, 21);
        shoppingPlane.add(toolBar);

        JButton shopBtn = new JButton("Shop");
        shopBtn.addActionListener(e -> shop());
        toolBar.add(shopBtn);

        JButton cartBth = new JButton("Cart");
        cartBth.addActionListener(e -> cart());
        toolBar.add(cartBth);

        JButton ordersBtn = new JButton("Orders");
        ordersBtn.addActionListener(e -> myOrders());
        toolBar.add(ordersBtn);

        JLabel label = new JLabel("Shop");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(10, 32, 414, 14);
        shoppingPlane.add(label);

        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < ClientApp.items.size(); i++) {
            listModel.addElement(ClientApp.items.get(i).getName() + " - " + String.format("%.02f", ClientApp.items.get(i).getPrice()) + " zl");
        }

        JList<String> itemList = new JList(listModel);
        itemList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        itemList.setBorder(new LineBorder(new Color(0, 0, 0)));
        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 57, 414, 295);
        shoppingPlane.add(scrollPane);

        JPanel btnPlane = new JPanel();
        btnPlane.setBounds(10, 363, 414, 37);
        shoppingPlane.add(btnPlane);
        btnPlane.setLayout(null);

        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setForeground(new Color(255, 153, 0));
        addToCartBtn.setBounds(0, 0, 414, 37);
        addToCartBtn.addActionListener(e -> {
            String itemString = itemList.getSelectedValue();
            int index = itemString.indexOf(" - ");
            String itemName = itemString.substring(0, index);

            for (ItemType item : ClientApp.items) {
                if (item.getName().equals(itemName)) {
                    orderDetails(item);
                    break;
                }
            }
        });
        btnPlane.add(addToCartBtn);
    }

    public void cart() {
        shoppingPlane = new JPanel();
        shoppingPlane.setBounds(0, 0, 434, 411);
        shoppingPlane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(shoppingPlane);
        shoppingPlane.setLayout(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, 434, 21);
        shoppingPlane.add(toolBar);

        JButton shopBtn = new JButton("Shop");
        shopBtn.addActionListener(e -> shop());
        toolBar.add(shopBtn);

        JButton cartBth = new JButton("Cart");
        cartBth.addActionListener(e -> cart());
        toolBar.add(cartBth);

        JButton ordersBtn = new JButton("Orders");
        ordersBtn.addActionListener(e -> myOrders());
        toolBar.add(ordersBtn);

        JLabel label = new JLabel("My Cart");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(10, 32, 414, 14);
        shoppingPlane.add(label);

        DefaultListModel listModel = new DefaultListModel();
        for (OrderLine order : ClientApp.cart) {
            listModel.addElement(order.getIt().getName() + " - " + String.format("%.02f", order.getIt().getPrice() * order.getQuantity())
                    + " zl (" + order.getQuantity() + ")");
        }

        JList<String> itemList = new JList(listModel);
        itemList.setBorder(new LineBorder(new Color(0, 0, 0)));
        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 57, 414, 295);
        shoppingPlane.add(scrollPane);

        JPanel btnPlane = new JPanel();
        btnPlane.setBounds(10, 363, 414, 37);
        shoppingPlane.add(btnPlane);
        btnPlane.setLayout(null);

        JButton removeBtn = new JButton("Remove");
        removeBtn.setForeground(new Color(255, 0, 0));
        removeBtn.addActionListener(e -> {
            List<String> itemsString = itemList.getSelectedValuesList();
            for (String itemString : itemsString) {
                int index = itemString.indexOf(" - ");
                int index2 = itemString.indexOf("(");
                int index3 = itemString.indexOf(")");
                String itemName = itemString.substring(0, index);
                String itemQuantity = itemString.substring(index2 + 1, index3);
                Iterator<OrderLine> iterator = ClientApp.cart.iterator();
                while (iterator.hasNext()) {
                    OrderLine order = iterator.next();
                    if (order.getIt().getName().equals(itemName) && order.getQuantity() == Integer.parseInt(itemQuantity)) {
                        ClientApp.cart.remove(order);
                        break;
                    }
                }
            }
            cart();
        });
        removeBtn.setBounds(214, 0, 200, 37);
        btnPlane.add(removeBtn);

        JButton orderBtn = new JButton("Order");
        orderBtn.addActionListener(e -> {
            Order newOrder = new Order(ClientApp.myId);

            List<String> itemsString = itemList.getSelectedValuesList();
            if (itemsString.size() == 0) return;
            for (String itemString : itemsString) {
                int index = itemString.indexOf(" - ");
                int index2 = itemString.indexOf("(");
                int index3 = itemString.indexOf(")");
                String itemName = itemString.substring(0, index);
                String itemQuantity = itemString.substring(index2 + 1, index3);
                Iterator<OrderLine> iterator = ClientApp.cart.iterator();
                while (iterator.hasNext()) {
                    OrderLine order = iterator.next();
                    if (order.getIt().getName().equals(itemName) && order.getQuantity() == Integer.parseInt(itemQuantity)) {
                        newOrder.addOrderLine(order);
                        ClientApp.cart.remove(order);
                        break;
                    }
                }
            }
            try {
                ClientApp.shop.placeOrder(newOrder);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            cart();
        });
        orderBtn.setBounds(0, 0, 200, 37);
        btnPlane.add(orderBtn);
    }

    public void myOrders() {
        inOrders = true;
        shoppingPlane = new JPanel();
        shoppingPlane.setBounds(0, 0, 434, 411);
        shoppingPlane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(shoppingPlane);
        shoppingPlane.setLayout(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, 434, 21);
        shoppingPlane.add(toolBar);

        JButton shopBtn = new JButton("Shop");
        shopBtn.addActionListener(e -> {
            shop();
            inOrders = false;
        });
        toolBar.add(shopBtn);

        JButton cartBth = new JButton("Cart");
        cartBth.addActionListener(e -> {
            inOrders = false;
            cart();
        });
        toolBar.add(cartBth);

        JButton ordersBtn = new JButton("Orders");
        ordersBtn.addActionListener(e -> myOrders());
        toolBar.add(ordersBtn);

        JLabel itemsLabel = new JLabel("My Orders");
        itemsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        itemsLabel.setBounds(10, 32, 414, 14);
        shoppingPlane.add(itemsLabel);

        DefaultListModel listModel = new DefaultListModel();
        try {
            for (SubmittedOrder submittedOrder : ClientApp.shop.getSubmittedOrders()) {
                if (submittedOrder.getOrder().getClientID() != ClientApp.myId) continue;
                String fullOrder = "<html>";
                for (OrderLine order : submittedOrder.getOrder().getOll()) {
                    fullOrder += order.getIt().getName() + " - " + String.format("%.02f", order.getIt().getPrice())
                            + " zl (" + order.getQuantity() + ") <br>";
                }
                fullOrder += "Total: " + String.format("%.02f", submittedOrder.getOrder().getCost()) + " zl | Order nr: " + submittedOrder.getId()
                        + " | Status: " + submittedOrder.getStatus() + "</html>";
                listModel.addElement(fullOrder);
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        JList<String> itemList = new JList(listModel);
        itemList.setBorder(new LineBorder(new Color(0, 0, 0)));

        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 57, 414, 295);
        shoppingPlane.add(scrollPane);

        JPanel btnPlane = new JPanel();
        btnPlane.setBounds(10, 363, 414, 37);
        shoppingPlane.add(btnPlane);
        btnPlane.setLayout(null);

        JToggleButton subscriptionBtn = new JToggleButton("Subscribe");
        if (subscribed) {
            subscriptionBtn.setSelected(true);
            subscriptionBtn.setText("Subscribed");
        }
        subscriptionBtn.setBounds(214, 0, 200, 37);
        btnPlane.add(subscriptionBtn);

        ItemListener itemListener = itemEvent -> {
            int state = itemEvent.getStateChange();
            try {
                if (state == ItemEvent.SELECTED) {
                    subscribed = true;
                    subscriptionBtn.setText("Subscribed");
                    IStatusListener listener = new ClientApp();
                    ClientApp.shop.subscribe(listener, ClientApp.myId);
                } else {
                    subscribed = false;
                    subscriptionBtn.setText("Subscribe");
                    ClientApp.shop.unsubscribe(ClientApp.myId);
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        };
        subscriptionBtn.addItemListener(itemListener);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(0, 0, 200, 37);
        refreshBtn.addActionListener(e -> myOrders());
        btnPlane.add(refreshBtn);
    }

    public void orderDetails(ItemType item) {
        ClientGUI subFrame = new ClientGUI();
        subFrame.setVisible(true);
        subFrame.setTitle("My order");
        subFrame.setResizable(false);
        subFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        subFrame.setBounds(500, 400, 300, 150);

        JPanel orderPane = new JPanel();
        orderPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        subFrame.setContentPane(orderPane);
        orderPane.setLayout(null);

        JTextField advertField = new JTextField();
        advertField.setBounds(10, 36, 200, 20);
        orderPane.add(advertField);
        advertField.setColumns(10);

        JLabel advertLabel = new JLabel("Advert");
        advertLabel.setBounds(10, 11, 46, 14);
        orderPane.add(advertLabel);

        JLabel quantityLabel = new JLabel("Quantity");
        quantityLabel.setBounds(222, 11, 56, 14);
        orderPane.add(quantityLabel);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(203, 77, 73, 23);
        cancelBtn.addActionListener(e -> subFrame.dispose());
        orderPane.add(cancelBtn);

        JSpinner quantitySpinner = new JSpinner();
        quantitySpinner.setBounds(220, 36, 54, 20);
        orderPane.add(quantitySpinner);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> {
            int quantity = (int) quantitySpinner.getValue();
            if (quantity > 0) {
                String advert = advertField.getText();
                OrderLine orderLine = new OrderLine(item, quantity, advert);
                ClientApp.cart.add(orderLine);
                subFrame.dispose();
            }
        });
        okBtn.setBounds(127, 77, 66, 23);
        orderPane.add(okBtn);
    }
}
