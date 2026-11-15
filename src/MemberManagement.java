import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MemberManagement extends JFrame {

    JTextField nameField, emailField, phoneField;

    public MemberManagement() {

        setTitle("LibTrack - Member Management");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        panel.add(new JLabel("Member Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        JButton addButton = new JButton("Add Member");
        JButton viewButton = new JButton("View Members");

        panel.add(addButton);
        panel.add(viewButton);

        add(panel);

        addButton.addActionListener(e -> addMember());
        viewButton.addActionListener(e -> viewMembers());
    }

    private void addMember() {

    String name = nameField.getText().trim();
    String email = emailField.getText().trim();
    String phone = phoneField.getText().trim();

    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter Member Name!");
        return;
    }

    if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
        JOptionPane.showMessageDialog(this, "Please enter a valid Email Address!");
        return;
    }

    if (!phone.matches("[0-9]{10}")) {
        JOptionPane.showMessageDialog(this, "Phone Number must contain exactly 10 digits!");
        return;
    }

    try (Connection con = DatabaseConfig.getConnection()) {

        String sql = "INSERT INTO Members (name, email, phone) VALUES (?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, phone);

        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Member Added Successfully!");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

    private void viewMembers() {

        try {
            Connection con = DatabaseConfig.getConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Members");

            StringBuilder result = new StringBuilder();

            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("member_id"))
                      .append(" | Name: ").append(rs.getString("name"))
                      .append(" | Email: ").append(rs.getString("email"))
                      .append(" | Phone: ").append(rs.getString("phone"))
                      .append("\n");
            }

            JOptionPane.showMessageDialog(this,
                result.length() == 0 ? "No members found." : result.toString());

            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemberManagement().setVisible(true));
    }
}