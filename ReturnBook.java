import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReturnBook extends JFrame {

    JTextField issueIdField;

    public ReturnBook() {

        setTitle("LibTrack - Return Books");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        issueIdField = new JTextField();

        panel.add(new JLabel("Issue ID:"));
        panel.add(issueIdField);

        JButton returnButton = new JButton("Return Book");
        JButton viewButton = new JButton("View Returns");

        panel.add(returnButton);
        panel.add(viewButton);

        add(panel);

        returnButton.addActionListener(e -> returnBook());
        viewButton.addActionListener(e -> viewReturns());
    }

    private void returnBook() {

    String issueText = issueIdField.getText().trim();

    if (issueText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter Issue ID!");
        return;
    }

    try {
        int issueId = Integer.parseInt(issueText);

        if (issueId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Issue ID must be a positive number!");
            return;
        }

        try (Connection con = DatabaseConfig.getConnection()) {

            con.setAutoCommit(false);

            try {
                String checkSql =
                "SELECT book_id, status, due_date FROM IssueRecords WHERE issue_id = ? FOR UPDATE";

                PreparedStatement check = con.prepareStatement(checkSql);
                check.setInt(1, issueId);

                ResultSet rs = check.executeQuery();

                if (!rs.next()) {
                    throw new Exception("Invalid Issue ID!");
                }

                int bookId = rs.getInt("book_id");
                String status = rs.getString("status");
                java.sql.Date dueDate = rs.getDate("due_date");

                rs.close();
                check.close();

                if ("Returned".equalsIgnoreCase(status)) {
                    throw new Exception("Book already returned!");
                }

                String updateSql =
                        "UPDATE IssueRecords SET status = 'Returned', return_date = CURDATE() WHERE issue_id = ?";

                PreparedStatement update = con.prepareStatement(updateSql);
                update.setInt(1, issueId);
                update.executeUpdate();
                update.close();

                String quantitySql =
                        "UPDATE Books SET quantity = quantity + 1 WHERE book_id = ?";

                PreparedStatement quantityUpdate = con.prepareStatement(quantitySql);
                quantityUpdate.setInt(1, bookId);
                quantityUpdate.executeUpdate();
                quantityUpdate.close();

                int fine = 0;

if (dueDate != null && java.time.LocalDate.now().isAfter(dueDate.toLocalDate())) {
    fine = (int) (java.time.temporal.ChronoUnit.DAYS.between(
            dueDate.toLocalDate(),
            java.time.LocalDate.now()) * 5);
}

String insertSql =
        "INSERT INTO Returns (issue_id, actual_return_date, fine_amount) VALUES (?, CURDATE(), ?)";

                PreparedStatement insert = con.prepareStatement(insertSql);
                insert.setInt(1, issueId);
                insert.setInt(2, fine);
                insert.executeUpdate();
                insert.close();

                con.commit();

                JOptionPane.showMessageDialog(this,
                        "Book Returned Successfully!");

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "Please enter a valid numeric Issue ID!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
    }
}

    private void viewReturns() {

        try (Connection con = DatabaseConfig.getConnection()) {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT * FROM Returns");

            StringBuilder result = new StringBuilder();

            while (rs.next()) {
                result.append("Return ID: ").append(rs.getInt("return_id"))
                      .append(" | Issue ID: ").append(rs.getInt("issue_id"))
                      .append(" | Return Date: ").append(rs.getDate("actual_return_date"))
                      .append(" | Fine: ").append(rs.getBigDecimal("fine_amount"))
                      .append("\n");
            }

            JOptionPane.showMessageDialog(this,
                    result.length() == 0 ? "No returns found." : result.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new ReturnBook().setVisible(true));
    }
}