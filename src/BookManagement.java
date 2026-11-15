import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BookManagement extends JFrame {

    JTextField titleField, authorField, categoryField, quantityField;

    public BookManagement() {

        setTitle("LibTrack - Book Management");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        titleField = new JTextField();
        authorField = new JTextField();
        categoryField = new JTextField();
        quantityField = new JTextField();

        panel.add(new JLabel("Book Title:"));
        panel.add(titleField);

        panel.add(new JLabel("Author:"));
        panel.add(authorField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        JButton addButton = new JButton("Add Book");
        JButton viewButton = new JButton("View Books");

        panel.add(addButton);
        panel.add(viewButton);

        add(panel);

        addButton.addActionListener(e -> addBook());
        viewButton.addActionListener(e -> viewBooks());
    }

    private void addBook() {

    String title = titleField.getText().trim();
    String author = authorField.getText().trim();
    String category = categoryField.getText().trim();
    String quantityText = quantityField.getText().trim();

    if (title.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter Book Title!");
        return;
    }

    if (quantityText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter Quantity!");
        return;
    }

    try {
        int quantity = Integer.parseInt(quantityText);

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be greater than zero!");
            return;
        }

        try (Connection con = DatabaseConfig.getConnection()) {

            String sql = "INSERT INTO Books (title, author, category, quantity) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, category);
            ps.setInt(4, quantity);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Book Added Successfully!");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "Please enter a valid number for Quantity!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
    }
}

    private void viewBooks() {

        try {
            Connection con = DatabaseConfig.getConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Books");

            StringBuilder result = new StringBuilder();

            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("book_id"))
                      .append(" | Title: ").append(rs.getString("title"))
                      .append(" | Author: ").append(rs.getString("author"))
                      .append(" | Quantity: ").append(rs.getInt("quantity"))
                      .append("\n");
            }

            JOptionPane.showMessageDialog(this,
                result.length() == 0 ? "No books found." : result.toString());

            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookManagement().setVisible(true));
    }
}