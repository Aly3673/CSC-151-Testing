import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Test_Concrete_Pad extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // --- Components ---
    private JTextField projName = new JTextField(20);
    private JTextField location = new JTextField(20);
    private JTextField fName = new JTextField(20);
    private JTextField mName = new JTextField(20);
    private JTextField lName = new JTextField(20);
    private JTextField length = new JTextField(10);
    private JTextField width = new JTextField(10);
    
    // Height fields for Variable Thickness
    private JComboBox<String> thicknessType = new JComboBox<>(new String[]{"Uniform", "Sloped"});
    private JTextField height = new JTextField(10);
    private JTextField secondaryHeight = new JTextField(10); 
    private JLabel secondaryLabel = new JLabel("End Height (ft) *:");

    private JTextField laborHours = new JTextField(10);
    private JTextField numEmployees = new JTextField(10);
    private JTextField hourlyRate = new JTextField(10);
    private JTextField materialCost = new JTextField(10);
    
    private JTextArea summaryArea = new JTextArea();

    private final Color BG_COLOR = new Color(30, 30, 30);
    private final Color FG_COLOR = Color.WHITE;
    private final Color FIELD_BG = new Color(45, 45, 45);

    public Test_Concrete_Pad() {
        setTitle("Concrete Estimator Pro");
        setSize(550, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);

        mainPanel.add(createLandingPanel(), "0");
        mainPanel.add(createProjectPanel(), "1");
        mainPanel.add(createDimensionsPanel(), "2");
        mainPanel.add(createLaborCostsPanel(), "3");
        mainPanel.add(createSummaryPanel(), "4");

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Core Logic ---

    private void handleImport() {
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Scanner scanner = new Scanner(fileChooser.getSelectedFile())) {
                
                if (scanner.hasNextLine()) {
                    scanner.nextLine(); // Skips the header row
                }

                if (scanner.hasNextLine()) {
                    String dataLine = scanner.nextLine(); 
                    String[] d = dataLine.split(",");
                    
                    if (d.length >= 14) {
                        projName.setText(d[0].trim());
                        location.setText(d[1].trim());
                        fName.setText(d[2].trim());
                        mName.setText(d[3].trim());
                        lName.setText(d[4].trim());
                        length.setText(d[5].trim());
                        width.setText(d[6].trim());
                        height.setText(d[7].trim());
                        secondaryHeight.setText(d[8].trim());
                        thicknessType.setSelectedItem(d[9].trim());
                        laborHours.setText(d[10].trim());
                        numEmployees.setText(d[11].trim());
                        hourlyRate.setText(d[12].trim());
                        materialCost.setText(d[13].trim());
                        
                        generateSummary(); 
                        cardLayout.show(mainPanel, "4");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "The file contains headers but no project data.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Import Error: " + e.getMessage());
            }
        }
    }

    private void generateSummary() {
        try {
            double L = Double.parseDouble(length.getText());
            double W = Double.parseDouble(width.getText());
            double H1 = Double.parseDouble(height.getText());
            
            // Logic for Variable Thickness (Average Height)
            double H2 = thicknessType.getSelectedItem().equals("Sloped") ? Double.parseDouble(secondaryHeight.getText()) : H1;
            double avgH = (H1 + H2) / 2.0; 

            double area = L * W;
            double vol = area * avgH;
            
            double labTotal = Double.parseDouble(laborHours.getText()) * Double.parseDouble(numEmployees.getText()) * Double.parseDouble(hourlyRate.getText());
            double matTotal = vol * Double.parseDouble(materialCost.getText());

            String out = String.format(
                " PROJECT: %s\n STYLE: %s\n LOCATION: %s\n CUSTOMER: %s %s %s\n" +
                " -------------------------------------\n" +
                " Dimensions:    %.2f' x %.2f'\n" +
                " Average H:     %.2f' height\n" +
                " Total Area:    %.2f sq.ft\n" +
                " Total Volume:  %.2f cu.ft\n" +
                " -------------------------------------\n" +
                " Labor Cost:    $%12.2f\n" +
                " Material Cost: $%12.2f\n" +
                " -------------------------------------\n" +
                " TOTAL ESTIMATE: $%10.2f",
                projName.getText(), thicknessType.getSelectedItem(), location.getText(), 
                fName.getText(), mName.getText(), lName.getText(),
                L, W, avgH, area, vol, labTotal, matTotal, (labTotal + matTotal));
            
            summaryArea.setText(out);
        } catch (NumberFormatException e) {
            summaryArea.setText("Error: Numbers could not be calculated.");
        }
    }

    private void saveAndExit() {
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setDialogTitle("Save Project Estimate");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileToSave, false)))) {
                // Header Row
                pw.println("Project_Name,Location,F_Name,M_Name,L_Name,Length,Width,H1,H2,Type,Hours,Staff,Rate,Mat_Cost");

                // Data Row
                String exportData = String.join(",", 
                    projName.getText().trim(), location.getText().trim(), fName.getText().trim(), 
                    mName.getText().trim(), lName.getText().trim(), length.getText().trim(), 
                    width.getText().trim(), height.getText().trim(), 
                    thicknessType.getSelectedItem().equals("Sloped") ? secondaryHeight.getText().trim() : height.getText().trim(),
                    thicknessType.getSelectedItem().toString(),
                    laborHours.getText().trim(), numEmployees.getText().trim(), 
                    hourlyRate.getText().trim(), materialCost.getText().trim());
                
                pw.println(exportData);
                pw.flush(); 
                
                JOptionPane.showMessageDialog(this, "Project Saved Successfully!");
                System.exit(0); 
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            }
        }
    }

    // --- GUI Panels ---

    private JPanel createLandingPanel() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(BG_COLOR);
        GridBagConstraints gbc = getGBC();
        JLabel title = new JLabel("Welcome to Concrete Estimator");
        title.setFont(new Font("SansSerif", Font.BOLD, 18)); title.setForeground(FG_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; p.add(title, gbc);
        JButton newBtn = createButton("New Project");
        newBtn.addActionListener(e -> cardLayout.show(mainPanel, "1"));
        JButton importBtn = createButton("Import CSV");
        importBtn.addActionListener(e -> handleImport());
        gbc.gridy = 1; gbc.gridwidth = 1; p.add(newBtn, gbc);
        gbc.gridx = 1; p.add(importBtn, gbc);
        return p;
    }

    private JPanel createProjectPanel() {
        JPanel p = createBasePanel("Step 1: Project Details");
        GridBagConstraints gbc = getGBC();
        addLabelAndField(p, "Project Name *:", projName, gbc, 0);
        addLabelAndField(p, "Location *:", location, gbc, 1);
        addLabelAndField(p, "First Name *:", fName, gbc, 2);
        addLabelAndField(p, "Middle Name:", mName, gbc, 3);
        addLabelAndField(p, "Last Name *:", lName, gbc, 4);
        JButton next = createButton("Next Step");
        next.addActionListener(e -> { if (validateStrings(projName, location, fName, lName)) cardLayout.show(mainPanel, "2"); });
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST; p.add(next, gbc);
        return p;
    }

    private JPanel createDimensionsPanel() {
        JPanel p = createBasePanel("Step 2: Measurements");
        GridBagConstraints gbc = getGBC();
        addLabelAndField(p, "Length (ft) *:", length, gbc, 0);
        addLabelAndField(p, "Width (ft) *:", width, gbc, 1);
        
        // Slab Style Dropdown
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel typeL = new JLabel("Slab Style:"); typeL.setForeground(FG_COLOR); p.add(typeL, gbc);
        gbc.gridx = 1; p.add(thicknessType, gbc);

        addLabelAndField(p, "Height (ft) *:", height, gbc, 3);
        
        // Sloped Height (Toggleable)
        secondaryLabel.setForeground(FG_COLOR);
        secondaryLabel.setVisible(false);
        secondaryHeight.setVisible(false);
        gbc.gridy = 4; gbc.gridx = 0; p.add(secondaryLabel, gbc);
        gbc.gridx = 1; secondaryHeight.setBackground(FIELD_BG); secondaryHeight.setForeground(FG_COLOR);
        secondaryHeight.setPreferredSize(new Dimension(200, 25)); p.add(secondaryHeight, gbc);

        thicknessType.addActionListener(e -> {
            boolean isSloped = thicknessType.getSelectedItem().equals("Sloped");
            secondaryLabel.setVisible(isSloped);
            secondaryHeight.setVisible(isSloped);
            p.revalidate(); p.repaint();
        });

        JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btnBox.setBackground(BG_COLOR);
        JButton back = createButton("Back"); JButton next = createButton("Next Step");
        back.addActionListener(e -> cardLayout.show(mainPanel, "1"));
        next.addActionListener(e -> { 
            if (validateNumerics(length, width, height)) {
                if (thicknessType.getSelectedItem().equals("Sloped") && !validateNumerics(secondaryHeight)) return;
                cardLayout.show(mainPanel, "3"); 
            }
        });
        btnBox.add(back); btnBox.add(next);
        gbc.gridx = 1; gbc.gridy = 5; p.add(btnBox, gbc);
        return p;
    }

    private JPanel createLaborCostsPanel() {
        JPanel p = createBasePanel("Step 3: Labor & Costs");
        GridBagConstraints gbc = getGBC();
        addLabelAndField(p, "Labor Hours *:", laborHours, gbc, 0);
        addLabelAndField(p, "No. Employees *:", numEmployees, gbc, 1);
        addLabelAndField(p, "Rate ($/hr) *:", hourlyRate, gbc, 2);
        addLabelAndField(p, "Concrete ($/cu.ft) *:", materialCost, gbc, 3);
        JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btnBox.setBackground(BG_COLOR);
        JButton back = createButton("Back"); JButton finish = createButton("Calculate");
        back.addActionListener(e -> cardLayout.show(mainPanel, "2"));
        finish.addActionListener(e -> { if (validateNumerics(laborHours, numEmployees, hourlyRate, materialCost)) { generateSummary(); cardLayout.show(mainPanel, "4"); } });
        btnBox.add(back); btnBox.add(finish);
        gbc.gridx = 1; gbc.gridy = 4; p.add(btnBox, gbc);
        return p;
    }

    private JPanel createSummaryPanel() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBackground(BG_COLOR); p.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        summaryArea.setBackground(FIELD_BG); summaryArea.setForeground(FG_COLOR);
        summaryArea.setEditable(false); summaryArea.setFont(new Font("Monospaced", Font.BOLD, 13));
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); btnPanel.setBackground(BG_COLOR);
        JButton edit = createButton("Edit Info"); JButton save = createButton("Save & Exit");
        edit.addActionListener(e -> cardLayout.show(mainPanel, "3"));
        save.addActionListener(e -> saveAndExit());
        btnPanel.add(edit); btnPanel.add(save);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    // --- Helpers ---

    private JPanel createBasePanel(String title) {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(BG_COLOR);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title, 0, 0, null, FG_COLOR));
        return p;
    }

    private GridBagConstraints getGBC() {
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 8, 8, 8);
        return gbc;
    }

    private void addLabelAndField(JPanel p, String text, JTextField f, GridBagConstraints gbc, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel l = new JLabel(text); l.setForeground(FG_COLOR); p.add(l, gbc);
        gbc.gridx = 1; f.setBackground(FIELD_BG); f.setForeground(FG_COLOR); f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(Color.GRAY)); f.setPreferredSize(new Dimension(200, 25));
        p.add(f, gbc);
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text); b.setPreferredSize(new Dimension(140, 35));
        return b;
    }

    private boolean validateStrings(JTextField... fields) {
        for (JTextField f : fields) if (f.getText().trim().isEmpty()) return false;
        return true;
    }

    private boolean validateNumerics(JTextField... fields) {
        try { for (JTextField f : fields) Double.parseDouble(f.getText()); return true; } catch (Exception e) { return false; }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        new Test_Concrete_Pad();
    }
}