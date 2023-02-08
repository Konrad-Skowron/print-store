package pl.skowron.GUI;

import model.ItemType;
import model.OrderLine;
import model.SubmittedOrder;
import pl.skowron.main.ShopApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ShopGUI extends JFrame {

    public static ShopGUI frame;
    public JPanel serverPlane;
    public boolean inOrders = false;

    public static void render() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        EventQueue.invokeLater(() -> {
            try {
                frame = new ShopGUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ShopGUI() {
        setResizable(false);
        setTitle("Shop Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(450, 300, 450, 450);
        orders();
    }

    public void items() {
        serverPlane = new JPanel();
        serverPlane.setBounds(0, 0, 434, 411);
        serverPlane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(serverPlane);
        serverPlane.setLayout(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, 434, 21);
        serverPlane.add(toolBar);

        JButton itemsBtn = new JButton("Items");
        itemsBtn.addActionListener(e -> items());
        toolBar.add(itemsBtn);

        JButton ordersBth = new JButton("Orders");
        ordersBth.addActionListener(e -> orders());
        toolBar.add(ordersBth);

        JLabel label = new JLabel("Items");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(10, 32, 414, 14);
        serverPlane.add(label);

        String[] columns = {"Name", "Category", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable scoreboard = new JTable(tableModel);
        scoreboard.setRowHeight(scoreboard.getRowHeight() + 20);
        JScrollPane scrollPane = new JScrollPane(scoreboard);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 57, 414, 343);
        serverPlane.add(scrollPane);

        for (ItemType item : ShopApp.items) {
            Object[] data = {item.getName(), ShopApp.getCategory(item.getCategory()), String.format("%.02f", item.getPrice()) + " zl"};
            tableModel.addRow(data);
        }
    }

    public void orders() {
        inOrders = true;
        serverPlane = new JPanel();
        serverPlane.setBounds(0, 0, 434, 411);
        serverPlane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(serverPlane);
        serverPlane.setLayout(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, 434, 21);
        serverPlane.add(toolBar);

        JButton itemsBtn = new JButton("Items");
        itemsBtn.addActionListener(e -> {
            inOrders = false;
            items();
        });
        toolBar.add(itemsBtn);

        JButton ordersBth = new JButton("Orders");
        ordersBth.addActionListener(e -> orders());
        toolBar.add(ordersBth);

        JLabel label = new JLabel("Orders");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(10, 32, 414, 14);
        serverPlane.add(label);

        String[] columns = {"ID", "Client ID", "Details", "Cost", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable scoreboard = new JTable(tableModel);
        scoreboard.setRowHeight(scoreboard.getRowHeight() + 55)
        ;
        JScrollPane scrollPane = new JScrollPane(scoreboard);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 57, 414, 343);
        serverPlane.add(scrollPane);

        for (SubmittedOrder order : ShopApp.orders) {
            String items = "<html><p style=\"font-size:8px\">";
            for (OrderLine item : order.getOrder().getOll()) {
                items += item.getIt().getName() + " " + item.getQuantity() + "szt. <i>" + item.getAdvert() + "</i><br>";
            }
            items += "</p></html>";

            Object[] data = {order.getId(), order.getOrder().getClientID(), items, String.format("%.02f", order.getOrder().getCost())  + " zl", order.getStatus()};
            tableModel.addRow(data);
        }
    }
}
