import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

class RestaurantManagementSystem {

    // To store ordered items quantities for each table
    static int[][] tableOrders = new int[7][12]; // 7 tables and 12 food items

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_management";
    private static final String USER = "root";
    private static final String PASSWORD = "saket_18";

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

        JPanel homePanel = createhomePanel();
        tabbedPane.addTab("Home", homePanel);

        //Table Reservation Tab
        JPanel reservationPanel = createReservationPanel();
        tabbedPane.addTab("Table Reservation", reservationPanel);

        //Order Food Tab
        JPanel orderPanel = createOrderPanel(tabbedPane);
        tabbedPane.addTab("Order Food", orderPanel);

        //Billing Management Tab
        JPanel billingPanel = createbillingPanel();
        tabbedPane.addTab("Bill Generation", billingPanel);


        //Add Tabbed Pane to Frame
        frame.add(tabbedPane);
        frame.setVisible(true);
    }
    private static JPanel createhomePanel() {
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout()); // Use BorderLayout for full-screen layout

        // Load the image (ensure the image path is correct)
        String imagePath = "src/images/home.jpeg"; // Change to the correct path of your image
        ImageIcon backgroundImage = new ImageIcon(imagePath);

        // Get the screen size or frame size dynamically
        //int frameWidth = frame.getWidth();
        //int frameHeight = frame.getHeight();

        // Scale the image to fit the entire frame
        Image scaledImage = backgroundImage.getImage().getScaledInstance(1100, 700, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Create a JLabel for the scaled image
        JLabel backgroundLabel = new JLabel(scaledIcon);
        backgroundLabel.setLayout(new GridBagLayout()); //to center text

        // Create a JLabel for the welcome text
        JLabel welcomeText = new JLabel("Welcome to My Restaurant");
        welcomeText.setForeground(new Color(245, 245, 220)); // Set text color to black
        welcomeText.setFont(new Font("Calisto MT", Font.BOLD, 80)); // Set the font and size of the text

        // Add the welcome text to the background label (centered)
        backgroundLabel.add(welcomeText, new GridBagConstraints());

        // Add the background label (with the text) to the panel
        homePanel.add(backgroundLabel, BorderLayout.CENTER);

        return homePanel;
    }

    //Table Reservation panel
    private static JPanel createReservationPanel() {
        JPanel reservationPanel = new JPanel();
        reservationPanel.setLayout(null);
        reservationPanel.setBackground(new Color(210, 210, 210));

        // Array to hold table buttons
        JButton[] tableButtons = new JButton[7];
        Color[] tableStates = {new Color(34, 139, 34), Color.RED, Color.YELLOW}; // States: Empty, Occupied, Reserved
        String[] statuses = {": Empty", ": Occupied", ": Reserved"};

        //7 table buttons
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

    // Order Food panel
    private static JPanel createOrderPanel(JTabbedPane tabbedPane) {
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBackground(new Color(210, 210, 210));

        // panel on the right for order summary
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
        int[] foodPrices = {200, 150, 180, 150, 80, 160, 250, 60, 160, 60, 30, 140};
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
            foodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
            int tableNumber = Integer.parseInt(selectedTable.split(" ")[1]) - 1; // to get table number
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
    private static JPanel createbillingPanel() {
        JPanel billingPanel = new JPanel();
        billingPanel.setBackground(new Color(210, 210, 210));
        billingPanel.setLayout(new BorderLayout());

        // Label at the top
        JLabel label = new JLabel("Billing Management", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        billingPanel.add(label, BorderLayout.NORTH);

        // Select table panel with label and radio buttons
        JPanel selectTablePanel = new JPanel();
        selectTablePanel.setLayout(new BoxLayout(selectTablePanel, BoxLayout.Y_AXIS)); // Vertical layout
        selectTablePanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment

        // Label for table selection
        JLabel tableSelectionLabel = new JLabel("  \s\s\s\s\s\s\s\s\s  Select the Table for which You want to generate  the bill:        \s\s\s\s\s\s\s");
        tableSelectionLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font for the label
        tableSelectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
        selectTablePanel.add(tableSelectionLabel);

        String[] tableNumbers = {"Table 1", "Table 2", "Table 3", "Table 4", "Table 5", "Table 6", "Table 7"};
        ButtonGroup tableButtonGroup = new ButtonGroup(); // Group to hold radio buttons

        // Spacer for vertical alignment
        selectTablePanel.add(Box.createRigidArea(new Dimension(0, 20))); // Adds some space before radio buttons

        // Creating larger radio buttons and centering them
        for (String tableNumber : tableNumbers) {
            JRadioButton tableRadioButton = new JRadioButton(tableNumber);
            tableRadioButton.setFont(new Font("Arial", Font.PLAIN, 28)); // Larger font size
            tableRadioButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center radio buttons
            tableButtonGroup.add(tableRadioButton);
            selectTablePanel.add(tableRadioButton);

            // Spacer between radio buttons
            selectTablePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adds space between each button
        }

        billingPanel.add(selectTablePanel, BorderLayout.WEST); // Add the panel with radio buttons to the left

        // Text area to display all bills
        JTextArea billDisplayArea = new JTextArea(10, 55); // 10 rows, 55 columns
        billDisplayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Monospaced font for alignment
        billDisplayArea.setEditable(false); // Read-only
        JScrollPane scrollPane = new JScrollPane(billDisplayArea);
        billingPanel.add(scrollPane, BorderLayout.EAST); // Add on the right side of the panel

        // Generate Bill Button
        JButton generateBillButton = new JButton("Generate Bill");
        generateBillButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font for the button
        generateBillButton.addActionListener(e -> {
            // Get selected table
            String selectedTable = null;
            for (Enumeration<AbstractButton> buttons = tableButtonGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    selectedTable = button.getText();
                    break;
                }
            }

            if (selectedTable != null) {
                int tableNumber = Integer.parseInt(selectedTable.split(" ")[1]) - 1; // 0-indexed
                String bill = generateBill(tableNumber);

                // Show the generated bill in JOptionPane
                JOptionPane.showMessageDialog(null, bill, "Bill for " + selectedTable, JOptionPane.INFORMATION_MESSAGE);

                // Append the bill to JTextArea for display on the panel
                billDisplayArea.append(bill + "\n\n----------------------------------------------\n\n");

                // Dummy print confirmation dialog
                int option = JOptionPane.showConfirmDialog(null, "Print Bill?", "Print", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Printing .....", "Print", JOptionPane.INFORMATION_MESSAGE);
                }
                saveBillToDatabase(tableNumber, bill);

                // Reset table orders after the bill is generated
                resetTableOrders(tableNumber);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a table.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        billingPanel.add(generateBillButton, BorderLayout.SOUTH);

        return billingPanel;
    }

    // Generate the bill for a specific table
    private static String generateBill(int tableIndex) {
        String[] foodNames = {"Pizza", "Pasta", "Spaghetti", "Salad", "Coffee", "Nachos", "Chicken Wings", "Tres Cake", "Ice Tea", "Chocolate Cake", "Water", "French Fries"};
        int[] foodPrices = {200, 150, 180, 150, 80, 160, 250, 60, 160, 60, 30, 140};

        StringBuilder bill = new StringBuilder("Bill for Table " + (tableIndex + 1) + ":\n\n");
        int totalAmount = 0;

        for (int i = 0; i < tableOrders[tableIndex].length; i++) {
            if (tableOrders[tableIndex][i] > 0) {
                int quantity = tableOrders[tableIndex][i];
                int price = foodPrices[i];
                bill.append(String.format("%-20s: %2d x ₹%-5d = ₹%-5d\n", foodNames[i], quantity, price, quantity * price));
                totalAmount += quantity * price;
            }
        }

        bill.append("\nTotal: ₹").append(totalAmount);
        return bill.toString();
    }

    // Reset table orders after billing
    private static void resetTableOrders(int tableIndex) {
        for (int i = 0; i < tableOrders[tableIndex].length; i++) {
            tableOrders[tableIndex][i] = 0; // Reset all orders for the given table
        }
    }




    // Save the bill to the database
    private static void saveBillToDatabase(int tableNumber, String bill) {
        int totalAmount = Integer.parseInt(bill.split("Total: ₹")[1].trim());
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO bills (table_number, amount, bill_time) VALUES (?, ?, ?)")) {

            pstmt.setInt(1, tableNumber + 1); // Table number (1-indexed)
            pstmt.setInt(2, totalAmount); // Total amount
            pstmt.setString(3, currentTime); // Current timestamp
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
