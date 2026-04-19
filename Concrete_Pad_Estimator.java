import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Concrete_Pad_Estimator extends JFrame {
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
    private JTextField discountAmount = new JTextField(10);
    
    // Height fields for Thickness
    private JComboBox<String> thicknessType = new JComboBox<>(new String[]{"Uniform", "Sloped", "Thickened Edge"});
    private JTextField height = new JTextField(10);
    private JTextField secondaryHeight = new JTextField(10); 
    private JLabel secondaryLabel = new JLabel("End Height (ft) *:");
    private JTextField edgeWidth = new JTextField(10);
    private JLabel edgeWidthLabel = new JLabel("Edge Width (ft) *:");

    private JTextField laborHours = new JTextField(10);
    private JTextField numEmployees = new JTextField(10);
    private JTextField hourlyRate = new JTextField(10);
    private JTextField materialCost = new JTextField(10);
    
    private JTextArea summaryArea = new JTextArea();

    private final Color BG_COLOR = new Color(30, 30, 30);
    private final Color FG_COLOR = Color.WHITE;
    private final Color FIELD_BG = new Color(45, 45, 45);

    public Concrete_Pad_Estimator() {
        setTitle("Concrete Pad Estimator Calculator");
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
        mainPanel.add(createDiscountPanel(), "4");
        mainPanel.add(createSummaryPanel(), "5");

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
                    scanner.nextLine();
                }

                if (scanner.hasNextLine()) {
                    String dataLine = scanner.nextLine(); 
                    String[] d = dataLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    
                    if (d.length >= 15) {
                        projName.setText(d[0].replace("\"", ""));
                        location.setText(d[1].replace("\"", ""));
                        fName.setText(d[2].replace("\"", ""));
                        mName.setText(d[3].replace("\"", ""));
                        lName.setText(d[4].replace("\"", ""));

                        length.setText(d[5].trim());
                        width.setText(d[6].trim());

                        thicknessType.setSelectedItem(d[7].replace("\"", ""));
                        height.setText(d[8].trim());
                        secondaryHeight.setText(d[9].trim());
                        edgeWidth.setText(d[10].trim());

                        laborHours.setText(d[11].trim());
                        numEmployees.setText(d[12].trim());
                        hourlyRate.setText(d[13].trim());
                        materialCost.setText(d[14].trim());

                        if (d.length >=16) {
                            discountAmount.setText(d[15].trim());
                        } else {
                            discountAmount.setText("0");
                        }
                        
                        generateSummary(); 
                        cardLayout.show(mainPanel, "5");
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
            String type = (String)thicknessType.getSelectedItem();
            
            double vol = 0;
            double avgH = H1;
            double area = L * W;

            if (type.equals("Uniform")) {
                vol = area * H1;
                avgH = H1;
            } else if (type.equals("Sloped")) {
                double H2 = Double.parseDouble(secondaryHeight.getText());
                avgH = (H1 + H2) / 2.0;
                vol = area * avgH;
            } else if (type.equals("Thickened Edge")) {
                double H_Edge = Double.parseDouble(secondaryHeight.getText());
                double W_Edge = Double.parseDouble(edgeWidth.getText());

                double mainVol = area * H1;
                double perimeter = 2 * (L + W);
                double extraDepth = H_Edge - H1;
                double edgeVol = perimeter * W_Edge * extraDepth;

                vol = mainVol + edgeVol;
                avgH = vol / area;
            }

            double labTotal = Double.parseDouble(laborHours.getText()) * Double.parseDouble(numEmployees.getText()) * Double.parseDouble(hourlyRate.getText());
            double matTotal = vol * Double.parseDouble(materialCost.getText());

            double subtotal = labTotal + matTotal;
            
            double discountPercent = Double.parseDouble(discountAmount.getText());
            double discountDollars = subtotal * (discountPercent / 100.0);
            double finalTotal = subtotal - discountDollars;

            String out = String.format(
                " PROJECT: %s\n STYLE: %s\n LOCATION: %s\n CUSTOMER: %s %s %s\n" +
                " -------------------------------------\n" +
                " Dimensions:    %.2f' x %.2f'\n" +
                " Average H:     %.2f' height\n" +
                " Total Area:    %.2f sq.ft\n" +
                " Total Volume:  %.2f cu.ft\n" +
                " -------------------------------------\n" +
                " Labor Cost:     $%15.2f\n" +
                " Material Cost:  $%15.2f\n" +
                " Subtotal:       $%15.2f\n" +
                " Discount (%s%%):-$%15.2f\n" +
                " -------------------------------------\n" +
                " TOTAL ESTIMATE: $%15.2f",
                projName.getText(), type, location.getText(), 
                fName.getText(), mName.getText(), lName.getText(),
                L, W, avgH, area, vol, labTotal, matTotal, subtotal, discountAmount.getText(), discountDollars, finalTotal);
            
            summaryArea.setText(out);
        } catch (Exception e) {
            summaryArea.setText("CRITICAL ERROR: Could not generate estimate. \n" + "Please go back and ensure all measurements and costs are filled in correctly.");
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
                pw.println("Project_Name,Location,F_Name,M_Name,L_Name,Length,Width,Type,H1,H2,EdgeW,Hours,Staff,Rate,Mat_Cost,Discount");

                // Data Row
                String exportData = String.join(",", 
                    wrap(projName.getText()), 
                    wrap(location.getText()), 
                    wrap(fName.getText()), 
                    wrap(mName.getText()), 
                    wrap(lName.getText()), 
    
                    length.getText(), 
                    width.getText(),

                    wrap(thicknessType.getSelectedItem().toString()),
                    height.getText(), 
                    secondaryHeight.getText(), 
                    edgeWidth.getText(),
    
                    laborHours.getText(), 
                    numEmployees.getText(), 
                    hourlyRate.getText(), 
                    materialCost.getText(), 
                    
                    discountAmount.getText()
                );
                pw.println(exportData);
                pw.flush(); 
                
                JOptionPane.showMessageDialog(this, "Project Saved Successfully!");
                System.exit(0); 
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            }
        }
    }
        
    private String wrap(String text) {
        return "\"" + text.trim() + "\"";
    }

    // --- GUI Panels ---

    private JPanel createLandingPanel() {
        JPanel p = new JPanel(new GridBagLayout()); 
        p.setBackground(BG_COLOR);
        GridBagConstraints gbc = getGBC();
        
        JLabel title = new JLabel("Welcome to the Concrete Pad Estimator!");
        title.setFont(new Font("SansSerif", Font.BOLD, 18)); 
        title.setForeground(FG_COLOR);
        
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.insets = new Insets(0, 0, 40, 0);
        gbc.anchor = GridBagConstraints.CENTER; 
        p.add(title, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BG_COLOR);
        
        JButton newBtn = createButton("New Project");
        newBtn.addActionListener(e -> cardLayout.show(mainPanel, "1"));
        
        JButton importBtn = createButton("Import CSV");
        importBtn.addActionListener(e -> handleImport());

        Dimension btnSize = new Dimension(160, 40);
        newBtn.setPreferredSize(btnSize);
        importBtn.setPreferredSize(btnSize);

        buttonPanel.add(newBtn);
        buttonPanel.add(importBtn);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        p.add(buttonPanel, gbc);

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
        next.addActionListener(e -> { 
            if (validateStrings(projName, location, fName, lName)) {
                cardLayout.show(mainPanel, "2");
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all required (*) fields.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        p.add(next, gbc);
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
        
        // Sloped (Toggleable)
        secondaryLabel.setForeground(FG_COLOR);
        secondaryLabel.setVisible(false);
        secondaryHeight.setVisible(false);
        gbc.gridy = 4; gbc.gridx = 0; p.add(secondaryLabel, gbc);
        gbc.gridx = 1; secondaryHeight.setBackground(FIELD_BG); secondaryHeight.setForeground(FG_COLOR);
        secondaryHeight.setCaretColor(Color.WHITE);
        secondaryHeight.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        secondaryHeight.setPreferredSize(new Dimension(200, 25)); p.add(secondaryHeight, gbc);

        // Thickened Edge (Toggleable)
        edgeWidthLabel.setForeground(FG_COLOR);
        edgeWidthLabel.setVisible(false);
        edgeWidth.setVisible(false);
        edgeWidth.setBackground(FIELD_BG);
        edgeWidth.setForeground(FG_COLOR);
        edgeWidth.setCaretColor(Color.WHITE);
        edgeWidth.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        edgeWidth.setPreferredSize(new Dimension(200, 25));
        
        gbc.gridy = 5; gbc.gridx = 0; p.add(edgeWidthLabel, gbc);
        gbc.gridx = 1; p.add(edgeWidth, gbc);
        
        thicknessType.addActionListener(e -> {
            String sel = (String)thicknessType.getSelectedItem();
            boolean isSloped = sel.equals("Sloped");
            boolean isEdge = sel.equals("Thickened Edge");

            secondaryLabel.setText(isEdge ? "Total Edge Height (ft) *:" : "End Height (ft) *:");
            secondaryLabel.setVisible(isSloped || isEdge);
            secondaryHeight.setVisible(isSloped || isEdge);
            edgeWidthLabel.setVisible(isEdge);
            edgeWidth.setVisible(isEdge);

            p.revalidate();
            p.repaint();
        });

        JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        btnBox.setBackground(BG_COLOR);
        JButton back = createButton("Back"); 
        JButton next = createButton("Next Step");

        back.addActionListener(e -> cardLayout.show(mainPanel, "1"));
        next.addActionListener(e -> { 
            if (!validateNumerics(length, width, height)) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Length, Width, and Height.");
                return;
            }

            String sel = (String)thicknessType.getSelectedItem();

            if (sel.equals("Sloped")) {
                if (secondaryHeight.getText().trim().isEmpty() || !validateNumerics(secondaryHeight)) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid End Height for the sloped slab.");
                    return;
                }
            }

            else if (sel.equals("Thickened Edge")) {
                if (!validateNumerics(secondaryHeight, edgeWidth)) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers for Edge Height and Edge Width.");
                    return;
                }
            }

            cardLayout.show(mainPanel, "3");
        });

        btnBox.add(back); 
        btnBox.add(next);
        gbc.gridx = 1; gbc.gridy = 6; 
        p.add(btnBox, gbc);
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
        finish.setText("Next Step");
        finish.addActionListener(e -> { 
            if (validateNumerics(laborHours, numEmployees, hourlyRate, materialCost)) { 
                cardLayout.show(mainPanel, "4"); 
            } else {
                JOptionPane.showMessageDialog(this, "All fields are required. Please enter valid numbers for labor and material costs.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBox.add(back); btnBox.add(finish);
        gbc.gridx = 1; gbc.gridy = 4; p.add(btnBox, gbc);
        return p;
    }

    private JPanel createDiscountPanel() {
        JPanel p = createBasePanel("Step 4: Applied Discounts");
        GridBagConstraints gbc = getGBC();

        addLabelAndField(p, "Discount Amount (%) *:", discountAmount, gbc, 0);
        discountAmount.setText("0");

        JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBox.setBackground(BG_COLOR);
        JButton back = createButton("Back");
        JButton finish = createButton("Calculate Total");

        back.addActionListener(e -> cardLayout.show(mainPanel, "3"));
        finish.addActionListener(e -> {
            if (validateNumerics(discountAmount)) {
                generateSummary();
                cardLayout.show(mainPanel, "5");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the discount (use 0 if none).");
            }
        });

        btnBox.add(back); btnBox.add(finish);
        gbc.gridx = 1; gbc.gridy = 1; p.add(btnBox, gbc);
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
        JButton edit = createButton("Edit Info"); 
        JButton save = createButton("Save & Exit");
        edit.addActionListener(e -> cardLayout.show(mainPanel, "4"));
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
        new Concrete_Pad_Estimator();
    }
}
