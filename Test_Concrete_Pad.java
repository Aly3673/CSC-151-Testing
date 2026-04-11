import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class Test_Concrete_Pad extends JFrame {
    private JPanel cards;
    private CardLayout cl;

    // Project Info Fields
    private JTextField txtProjName, txtLoc, txtDate, txtFname, txtMname, txtLname;

    // Calculation Fields
    private JTextField txtLength, txtWidth, txtDepth;
    private JLabel lblAreaResult, lblVolumeResult;

    public Test_Concrete_Pad() {
        setTitle("Concrete Pad Construction");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cl = new CardLayout();
        cards = new JPanel(cl);

        cards.add(createProjectDetailsPanel(), "Details");
        cards.add(createCalculatorPanel(), "Calc");

        add(cards);
    }

    private JPanel createProjectDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel(" Project Name (Required):"));
        txtProjName = new JTextField();
        panel.add(txtProjName);

        panel.add(new JLabel(" Location (Required):"));
        txtLoc = new JTextField();
        panel.add(txtLoc);

        panel.add(new JLabel(" Due Date:"));
        txtDate = new JTextField();
        panel.add(txtDate);

        panel.add(new JLabel(" Customer First Name (Required):"));
        txtFname = new JTextField();
        panel.add(txtFname);

        panel.add(new JLabel(" Customer Middle Name:"));
        txtMname = new JTextField();
        panel.add(txtMname);

        panel.add(new JLabel(" Customer Last Name (Required):"));
        txtLname = new JTextField();
        panel.add(txtLname);

        panel.add(new JLabel("")); // Spacer
        JButton btnNext = new JButton("Next: Dimensions ->");
        
        // Validation Logic Added Here
        btnNext.addActionListener(e -> {
            if (validateDetails()) {
                cl.show(cards, "Calc");
            }
        });
        panel.add(btnNext);

        return panel;
    }

    // New validation method
    private boolean validateDetails() {
        if (txtProjName.getText().trim().isEmpty() || 
            txtLoc.getText().trim().isEmpty() || 
            txtFname.getText().trim().isEmpty() || 
            txtLname.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Error: Please provide a Project Name, Location, and First/Last Name.", 
                "Missing Information", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private JPanel createCalculatorPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtLength = new JTextField();
        txtWidth = new JTextField();
        txtDepth = new JTextField();
        lblAreaResult = new JLabel("0.00 sq ft");
        lblVolumeResult = new JLabel("0.00 cubic yards");

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculate(); }
            public void removeUpdate(DocumentEvent e) { calculate(); }
            public void changedUpdate(DocumentEvent e) { calculate(); }
        };

        txtLength.getDocument().addDocumentListener(dl);
        txtWidth.getDocument().addDocumentListener(dl);
        txtDepth.getDocument().addDocumentListener(dl);

        panel.add(new JLabel(" Length (feet):"));
        panel.add(txtLength);
        panel.add(new JLabel(" Width (feet):"));
        panel.add(txtWidth);
        panel.add(new JLabel(" Depth/Height (feet):"));
        panel.add(txtDepth);
        
        panel.add(new JLabel(" Total Area:"));
        panel.add(lblAreaResult);
        
        panel.add(new JLabel(" Concrete Volume:"));
        panel.add(lblVolumeResult);

        JButton btnBack = new JButton("<- Back");
        btnBack.addActionListener(e -> cl.show(cards, "Details"));
        panel.add(btnBack);

        JButton btnFinish = new JButton("Complete Project");
        btnFinish.addActionListener(e -> showFinalSummary());
        panel.add(btnFinish);

        return panel;
    }

    private void calculate() {
        try {
            double l = parseInput(txtLength.getText());
            double w = parseInput(txtWidth.getText());
            double d = parseInput(txtDepth.getText());
            double area = l * w;
            double cubicYards = (l * w * d) / 27.0;
            lblAreaResult.setText(String.format("%.2f sq ft", area));
            lblVolumeResult.setText(String.format("%.2f cubic yards", cubicYards));
        } catch (Exception e) {}
    }

    private double parseInput(String val) {
        if (val == null || val.trim().isEmpty() || val.equals(".")) return 0;
        return Double.parseDouble(val);
    }

    private void showFinalSummary() {
        String customer = txtFname.getText() + " " + (txtMname.getText().isEmpty() ? "" : txtMname.getText() + " ") + txtLname.getText();
        String summary = String.format(
            "PROJECT SUMMARY\n----------------------\n" +
            "Project: %s\nLocation: %s\nCustomer: %s\nDue Date: %s\n\n" +
            "DIMENSIONS\nArea: %s\nTotal Concrete: %s",
            txtProjName.getText(), txtLoc.getText(), customer, txtDate.getText(), 
            lblAreaResult.getText(), lblVolumeResult.getText()
        );
        JOptionPane.showMessageDialog(this, summary, "Project Finalized", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new Test_Concrete_Pad().setVisible(true));
    }
}
