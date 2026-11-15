import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {

        setTitle("LibTrack - Library Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color background = new Color(15, 23, 42);
        Color buttonColor = new Color(30, 41, 59);
        Color accent = new Color(56, 189, 248);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 30));
        mainPanel.setBackground(background);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel heading = new JLabel("LIBTRACK");
        heading.setFont(new Font("Arial", Font.BOLD, 38));
        heading.setForeground(accent);
        heading.setHorizontalAlignment(JLabel.CENTER);

        JLabel subtitle = new JLabel(
                "LIBRARY MANAGEMENT SYSTEM");
        subtitle.setFont(new Font("Arial", Font.BOLD, 18));
        subtitle.setForeground(Color.WHITE);
        subtitle.setHorizontalAlignment(JLabel.CENTER);

        JLabel welcome = new JLabel(
                "Welcome! Manage your library efficiently.");
        welcome.setFont(new Font("Arial", Font.PLAIN, 15));
        welcome.setForeground(Color.LIGHT_GRAY);
        welcome.setHorizontalAlignment(JLabel.CENTER);

        JPanel header = new JPanel(new GridLayout(3, 1, 5, 5));
        header.setBackground(background);
        header.add(heading);
        header.add(subtitle);
        header.add(welcome);

        JPanel panel = new JPanel(new GridLayout(3, 2, 20, 20));
        panel.setBackground(background);

        JButton books = createButton("Book Management", buttonColor);
        JButton members = createButton("Member Management", buttonColor);
        JButton issue = createButton("Issue Books", buttonColor);
        JButton returns = createButton("Return Books", buttonColor);
        JButton overdue = createButton("Overdue Books", buttonColor);
        JButton exit = createButton("Exit", new Color(153, 27, 27));

        panel.add(books);
        panel.add(members);
        panel.add(issue);
        panel.add(returns);
        panel.add(overdue);
        panel.add(exit);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);

        add(mainPanel);

        exit.addActionListener(e -> System.exit(0));

        books.addActionListener(e ->
                new BookManagement().setVisible(true));

        members.addActionListener(e ->
                new MemberManagement().setVisible(true));

        issue.addActionListener(e ->
                new IssueBook().setVisible(true));

        returns.addActionListener(e ->
                new ReturnBook().setVisible(true));

        overdue.addActionListener(e ->
                new OverdueBooks().setVisible(true));
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 17));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createLineBorder(
                        new Color(71, 85, 105), 1));

        return button;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}