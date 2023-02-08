package pl.skowron.GUI;

import model.OrderLine;
import model.Status;
import model.SubmittedOrder;
import pl.skowron.main.SellerApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.rmi.RemoteException;

public class SellerGUI extends JFrame {

    private static SellerGUI frame;
    public JPanel serverPlane;
    private int id;

    public static void render() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        EventQueue.invokeLater(() -> {
            try {
                frame = new SellerGUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public SellerGUI() throws RemoteException {
        setResizable(false);
        setTitle("Seller Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(450, 300, 450, 450);
        orders();
    }

    public void orders() throws RemoteException {
        serverPlane = new JPanel();
        serverPlane.setBounds(0, 0, 434, 411);
        serverPlane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(serverPlane);
        serverPlane.setLayout(null);

        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, 434, 21);
        serverPlane.add(toolBar);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            try {
                orders();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        toolBar.add(refreshBtn);

        JLabel label = new JLabel("Pending Orders");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(10, 32, 414, 14);
        serverPlane.add(label);

        String[] columns = {"ID", "Client ID", "Details", "Income", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable scoreboard = new JTable(tableModel);
        scoreboard.setRowHeight(scoreboard.getRowHeight() + 55);
        setUpStatusColumn(scoreboard.getColumnModel().getColumn(4));

        scoreboard.getSelectionModel().addListSelectionListener(event -> {
            if (scoreboard.getSelectedRow() > -1) {
                id = (int) scoreboard.getValueAt(scoreboard.getSelectedRow(), 0);
            }
        });

        JScrollPane scrollPane = new JScrollPane(scoreboard);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 57, 414, 343);
        serverPlane.add(scrollPane);

        for (SubmittedOrder order : SellerApp.shop.getSubmittedOrders()) {
            String items = "<html><p style=\"font-size: 8px\">";
            for (OrderLine item : order.getOrder().getOll()) {
                items += item.getIt().getName() + " " + item.getQuantity() + "szt. <i>" + item.getAdvert() + "</i><br>";
            }
            items += "</p></html>";

            Object[] data = {order.getId(), order.getOrder().getClientID(), items, String.format("%.02f", order.getOrder().getCost()) + " zl",
                    "<html><b>" + order.getStatus() + "</b></html>"};
            tableModel.addRow(data);
        }
    }

    public void setUpStatusColumn(TableColumn statusColumn) {
        JComboBox comboBox = new JComboBox();
        for (Status status : Status.values()) {
            comboBox.addItem("<html><b>" + status + "</b></html>");
        }
        statusColumn.setCellEditor(new DefaultCellEditor(comboBox));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click to see options");
        statusColumn.setCellRenderer(renderer);

        comboBox.addActionListener(e -> {
            String s = (String) comboBox.getSelectedItem();
            int index1 = s.indexOf("<b>");
            int index2 = s.indexOf("</b>");
            String status = s.substring(index1 + 3, index2);
            try {
                SellerApp.shop.setStatus(id, Status.valueOf(status));
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
