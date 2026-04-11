import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Concrete_Pad_Estimator extends JFrame {
    private JTextField txtProjectName, txtLocation, txtDueDate;
    private JTextField txtFirstName, txtMiddleName, txtLastName;
    private JButton btnSubmit;

    public Concrete_Pad_Estimator() {
        setTitle("Concrete Pad Construction - Project Entry");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 10));

        add(new JLabel(" Project Name:"));
        txtProjectName = new JTextField();
        add(txtProjectName);

        add(new JLabel(" Location:"));
        txtLocation = new JTextField();
        add(txtLocation);

        add(new JLabel(" Due Date (MM/DD/YYYY):"));
        txtDueDate = new JTextField();
        add(txtDueDate);

        add(new JLabel(" Customer First Name:"));
        txtFirstName = new JTextField();
        add(txtFirstName);

        add(new JLabel(" Customer Middle Name (Optional):"));
        txtMiddleName = new JTextField();
        add(txtMiddleName);

        add(new JLabel(" Customer Last Name:"));
        txtLastName = new JTextField();
        add(txtLastName);

        btnSubmit = new JButton("Save Project");
        add(new JLabel(""));
        add(btnSubmit);

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });
    }

    public void handleSubmit() {
        String project = txtProjectName.getText();
        String location = txtLocation.getText();
        String date = txtDueDate.getText();
        String customer = txtFirstName.getText() + " " + (txtMiddleName.getText().isEmpty() ? "" : txtMiddleName.getText() + " ") + txtLastName.getText();

        if (project.isEmpty() || location.isEmpty() || txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty()) { // For first prompt, manually added the location.isEmpty()
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String summary = String.format("Project: %s\nLocation: %s\nDue: %s\nCustomer: %s", project, location, date, customer);
            JOptionPane.showMessageDialog(this, "Project Saved Successfully!\n\n" + summary);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Concrete_Pad_Estimator().setVisible(true);
        });
    }
}
