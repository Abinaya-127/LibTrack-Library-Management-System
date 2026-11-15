import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class OverdueBooks extends JFrame {

    public OverdueBooks() {

        setTitle("LibTrack - Overdue Books");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        JButton viewButton = new JButton("View Overdue Books");

        add(new JScrollPane(area), BorderLayout.CENTER);
        add(viewButton, BorderLayout.SOUTH);

        viewButton.addActionListener(e -> {

            area.setText("");

            try (Connection con = DatabaseConfig.getConnection()) {

                String sql =
                "SELECT *, DATEDIFF(CURDATE(), due_date) AS overdue_days " +
                "FROM IssueRecords " +
                "WHERE status = 'Issued' " +
                "AND due_date < CURDATE()";

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);

                boolean found = false;

                while (rs.next()) {
                found = true;

                int overdueDays = rs.getInt("overdue_days");
                int fine = overdueDays * 5;

                area.append("Issue ID: " + rs.getInt("issue_id") + "\n");
                area.append("Book ID: " + rs.getInt("book_id") + "\n");
                area.append("Member ID: " + rs.getInt("member_id") + "\n");
                area.append("Issue Date: " + rs.getDate("issue_date") + "\n");
                area.append("Due Date: " + rs.getDate("due_date") + "\n");
                area.append("Overdue Days: " + overdueDays + "\n");
                area.append("Fine: Rs. " + fine + "\n");
                area.append("------------------------------\n");
                }

                if (!found) {
                    area.setText("No overdue books found.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new OverdueBooks().setVisible(true));
    }
}