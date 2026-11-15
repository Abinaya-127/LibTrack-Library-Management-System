import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class IssueBook extends JFrame {

    JTextField bookIdField, memberIdField;

    public IssueBook() {

        setTitle("LibTrack - Issue Books");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        bookIdField = new JTextField();
        memberIdField = new JTextField();

        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);

        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);

        JButton issueButton = new JButton("Issue Book");
        JButton viewButton = new JButton("View Issued Books");

        panel.add(issueButton);
        panel.add(viewButton);

        add(panel);

        issueButton.addActionListener(e -> issueBook());
        viewButton.addActionListener(e -> viewIssuedBooks());
    }

    private void issueBook() {

    String bookText = bookIdField.getText().trim();
    String memberText = memberIdField.getText().trim();

    if (bookText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter Book ID!");
        return;
    }

    if (memberText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter Member ID!");
        return;
    }

    try {
        int bookId = Integer.parseInt(bookText);
        int memberId = Integer.parseInt(memberText);

        if (bookId <= 0 || memberId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Book ID and Member ID must be positive numbers!");
            return;
        }

        try (Connection con = DatabaseConfig.getConnection()) {

            con.setAutoCommit(false);

            try {
                String memberSql = "SELECT member_id FROM Members WHERE member_id = ?";
                PreparedStatement memberPs = con.prepareStatement(memberSql);
                memberPs.setInt(1, memberId);
                ResultSet memberRs = memberPs.executeQuery();

                if (!memberRs.next()) {
                    throw new Exception("Member ID not found!");
                }

                memberRs.close();
                memberPs.close();

                String checkSql = "SELECT quantity FROM Books WHERE book_id = ? FOR UPDATE";
                PreparedStatement checkPs = con.prepareStatement(checkSql);
                checkPs.setInt(1, bookId);

                ResultSet rs = checkPs.executeQuery();

                if (!rs.next()) {
                    throw new Exception("Book ID not found!");
                }

                int quantity = rs.getInt("quantity");
                rs.close();
                checkPs.close();

                if (quantity <= 0) {
                    throw new Exception("Book is not available!");
                }

                String updateSql = "UPDATE Books SET quantity = quantity - 1 WHERE book_id = ?";
                PreparedStatement updatePs = con.prepareStatement(updateSql);
                updatePs.setInt(1, bookId);
                updatePs.executeUpdate();
                updatePs.close();

                String sql = "INSERT INTO IssueRecords "
        + "(book_id, member_id, issue_date, due_date, status) "
        + "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'Issued')";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, bookId);
                ps.setInt(2, memberId);
                ps.executeUpdate();
                ps.close();

                con.commit();

                JOptionPane.showMessageDialog(this,
                        "Book Issued Successfully!");

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "Please enter valid numeric IDs!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
    }
}

    private void viewIssuedBooks() {

        try (Connection con = DatabaseConfig.getConnection()) {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT * FROM IssueRecords WHERE status = 'Issued'");

            StringBuilder result = new StringBuilder();

            while (rs.next()) {
                result.append("Issue ID: ").append(rs.getInt("issue_id"))
                      .append(" | Book ID: ").append(rs.getInt("book_id"))
                      .append(" | Member ID: ").append(rs.getInt("member_id"))
                      .append(" | Issue Date: ").append(rs.getDate("issue_date"))
                      .append("\n");
            }

            JOptionPane.showMessageDialog(this,
                    result.length() == 0 ? "No issued books found." : result.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IssueBook().setVisible(true));
    }
}