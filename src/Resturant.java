import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class RestaurantManagementSystem {

    // To store ordered items quantities for each table
    static int[][] tableOrders = new int[7][12]; // 7 tables and 12 food items

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_management"; // Updated DB name
    private static final String USER = "root";
    private static final String PASSWORD = "saket_18"; // Update as necessary

    public static void main(String[] args) {
        // Create the main frame (window)
        JFrame frame = new JFrame("Restaurant Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.getContentPane().setBackground(new Color(200, 200, 200));

        // Create a JTabbedPane to hold the three tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(180, 180, 180));
        tabbedPane.setForeground(Color.BLACK);
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 16));

        // --- Table Reservation Tab ---
        JPanel reservationPanel = createReservationPanel();
        tabbedPane.addTab("Table Reservation", reservationPanel);

        // --- Order Food Tab ---
        JPanel orderPanel = createOrderPanel(tabbedPane);
        tabbedPane.addTab("Order Food", orderPanel);

        // --- Inventory Management Tab ---
        JPanel inventoryPanel = createInventoryPanel();
        tabbedPane.addTab("Inventory Management", inventoryPanel);

        // --- Add Tabbed Pane to Frame ---
        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    // Method to create Table Reservation panel
    private static JPanel createReservationPanel() {
        JPanel reservationPanel = new JPanel();
        reservationPanel.setLayout(null);
        reservationPanel.setBackground(new Color(210, 210, 210));

        // Array to hold table buttons
        JButton[] tableButtons = new JButton[7];
        Color[] tableStates = {new Color(34, 139, 34), Color.RED, Color.YELLOW}; // States: Empty, Occupied, Reserved
        String[] statuses = {": Empty", ": Occupied", ": Reserved"};

        // Create 7 table buttons with custom positions
        for (int i = 0; i < 7; i++) {
            JButton tableButton = new JButton("Table " + (i + 1) + statuses[0]);
            tableButton.setBackground(tableStates[0]);
            tableButton.setOpaque(true);
            tableButton.setBorderPainted(false);
            tableButton.setForeground(Color.BLACK);
            tableButton.setFont(new Font("Arial", Font.BOLD, 16));

            // Position each table button based on its index
            switch (i) {
                case 0 -> tableButton.setBounds(50, 50, 300, 150);
                case 1 -> tableButton.setBounds(400, 50, 300, 150);
                case 2 -> tableButton.setBounds(750, 50, 300, 150);
                case 3 -> tableButton.setBounds(100, 250, 200, 350);
                case 4 -> tableButton.setBounds(380, 250, 320, 150);
                case 5 -> tableButton.setBounds(380, 450, 320, 150);
                case 6 -> tableButton.setBounds(780, 250, 200, 350);
            }

            int finalI = i;
            tableButton.addActionListener(new ActionListener() {
                int currentState = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    currentState = (currentState + 1) % 3;
                    tableButton.setBackground(tableStates[currentState]);
                    tableButton.setText("Table " + (finalI + 1) + statuses[currentState]);
                }
            });

            tableButtons[i] = tableButton;
            reservationPanel.add(tableButton);
        }

        return reservationPanel;
    }

    // Method to create Order Food panel
    private static JPanel createOrderPanel(JTabbedPane tabbedPane) {
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBackground(new Color(210, 210, 210));

        // Create a panel on the right for order summary
        JPanel orderSummaryPanel = new JPanel();
        orderSummaryPanel.setLayout(new BorderLayout());
        orderSummaryPanel.setPreferredSize(new Dimension(200, 700));
        orderSummaryPanel.setBackground(new Color(230, 230, 230));

        JLabel summaryLabel = new JLabel("Order Summary:");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        orderSummaryPanel.add(summaryLabel, BorderLayout.NORTH);

        // Text area to show order summary
        JTextArea orderSummaryArea = new JTextArea();
        orderSummaryArea.setEditable(false);
        orderSummaryPanel.add(new JScrollPane(orderSummaryArea), BorderLayout.CENTER);

        // Center panel to display food items
        JPanel foodItemsPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        foodItemsPanel.setBackground(new Color(210, 210, 210));

        // Create a combo box to select table number
        JPanel selectTablePanel = new JPanel();
        String[] tableNumbers = {"Table 1", "Table 2", "Table 3", "Table 4", "Table 5", "Table 6", "Table 7"};
        JComboBox<String> tableComboBox = new JComboBox<>(tableNumbers);
        selectTablePanel.add(new JLabel("Select Table: "));
        selectTablePanel.add(tableComboBox);

        orderPanel.add(selectTablePanel, BorderLayout.NORTH);

        // Food items data (name, price, images)
        String[] foodNames = {"Pizza", "Pasta", "Spaghetti", "Salad", "Coffee", "Nachos", "Chicken Wings", "Tres Cake", "Ice Tea", "Chocolate Cake", "Water", "French Fries"};
        int[] foodPrices = {200, 150, 180, 100, 120, 80, 90, 300, 250, 220, 180, 50};
        String[] foodImages = {"pizza.jpeg", "pasta.jpeg", "spaghetti.jpeg", "salad.jpeg", "coffee.jpeg", "nachos.jpeg", "chickenwings.jpeg", "trescake.jpeg", "icetea.jpeg", "chocolatecake.jpeg", "water.jpeg", "frenchfries.jpeg"};

        // Create individual food items with images, labels, and quantity control
        for (int i = 0; i < foodNames.length; i++) {
            JPanel foodItemPanel = new JPanel();
            foodItemPanel.setLayout(new BoxLayout(foodItemPanel, BoxLayout.Y_AXIS));
            foodItemPanel.setBackground(new Color(230, 230, 230));

            // Add image placeholder
            String imagePath = "src/images/" + foodImages[i]; // Adjust path as necessary
            JLabel foodImage = new JLabel(new ImageIcon(imagePath));
            foodImage.setPreferredSize(new Dimension(200, 200));
            foodItemPanel.add(foodImage);

            // Add food name and price
            JLabel foodLabel = new JLabel(foodNames[i] + " - ₹" + foodPrices[i]);
            foodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            foodLabel.setFont(new Font("Arial", Font.BOLD, 14));
            foodItemPanel.add(foodLabel);

            // Quantity controls
            JPanel quantityPanel = new JPanel(new FlowLayout());
            JButton reduceButton = new JButton("-");
            JButton addButton = new JButton("+");
            JLabel quantityLabel = new JLabel("0");
            quantityLabel.setFont(new Font("Arial", Font.BOLD, 14));

            quantityPanel.add(reduceButton);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(addButton);
            foodItemPanel.add(quantityPanel);

            // Use final variable for food index
            final int foodIndex = i;
            addButton.addActionListener(e -> {
                int tableIndex = tableComboBox.getSelectedIndex();
                tableOrders[tableIndex][foodIndex]++;
                quantityLabel.setText(String.valueOf(tableOrders[tableIndex][foodIndex]));
                updateOrder(tableComboBox, foodNames, foodPrices, orderSummaryArea);
            });

            reduceButton.addActionListener(e -> {
                int tableIndex = tableComboBox.getSelectedIndex();
                if (tableOrders[tableIndex][foodIndex] > 0) {
                    tableOrders[tableIndex][foodIndex]--;
                    quantityLabel.setText(String.valueOf(tableOrders[tableIndex][foodIndex]));
                    updateOrder(tableComboBox, foodNames, foodPrices, orderSummaryArea);
                }
            });

            foodItemsPanel.add(foodItemPanel);
        }

        // Add the food items panel to the center of the order panel
        orderPanel.add(new JScrollPane(foodItemsPanel), BorderLayout.CENTER);

        // Add order summary panel to the right of the order panel
        orderPanel.add(orderSummaryPanel, BorderLayout.EAST);

        // Add action to place order
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(e -> {
            String selectedTable = (String) tableComboBox.getSelectedItem();
            int tableNumber = Integer.parseInt(selectedTable.split(" ")[1]) - 1; // Extract table number (0-indexed)
            placeOrderToDatabase(tableNumber); // Save the order to the database
            JOptionPane.showMessageDialog(null, "Order placed successfully for " + selectedTable + "!");
        });

        orderPanel.add(placeOrderButton, BorderLayout.SOUTH);
        return orderPanel;
    }

    // Method to update the order summary
    private static void updateOrder(JComboBox<String> tableComboBox, String[] foodNames, int[] foodPrices, JTextArea orderSummaryArea) {
        int tableIndex = tableComboBox.getSelectedIndex();
        StringBuilder summary = new StringBuilder("Order Summary for " + tableComboBox.getSelectedItem() + ":\n");

        int total = 0;
        for (int i = 0; i < foodNames.length; i++) {
            if (tableOrders[tableIndex][i] > 0) {
                summary.append(foodNames[i]).append(": ").append(tableOrders[tableIndex][i]).append(" x ₹").append(foodPrices[i]).append("\n");
                total += tableOrders[tableIndex][i] * foodPrices[i];
            }
        }
        summary.append("Total: ₹").append(total);
        orderSummaryArea.setText(summary.toString());
    }

    // Method to place the order in the database
    private static void placeOrderToDatabase(int tableNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO orders (table_number, food_item, quantity) VALUES (?, ?, ?)")) {
            // Loop through the table orders and save each non-zero order
            for (int i = 0; i < tableOrders[tableNumber].length; i++) {
                if (tableOrders[tableNumber][i] > 0) {
                    pstmt.setInt(1, tableNumber + 1); // Table number (1-indexed)
                    pstmt.setString(2, getFoodNameById(i)); // Food item
                    pstmt.setInt(3, tableOrders[tableNumber][i]); // Quantity
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch(); // Execute the batch update
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get food name by its index
    private static String getFoodNameById(int index) {
        String[] foodNames = {"Pizza", "Pasta", "Spaghetti", "Salad", "Coffee", "Nachos", "Chicken Wings", "Tres Cake", "Ice Tea", "Chocolate Cake", "Water", "French Fries"};
        return foodNames[index];
    }

    // Dummy method to create Inventory Management tab (to be implemented later)
    private static JPanel createInventoryPanel() {
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setBackground(new Color(210, 210, 210));
        JLabel label = new JLabel("Inventory Management will be implemented later.");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        inventoryPanel.add(label);
        return inventoryPanel;
    }
}
