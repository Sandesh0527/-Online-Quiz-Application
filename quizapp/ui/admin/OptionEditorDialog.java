package com.quizapp.ui.admin;

import com.quizapp.model.Question.Option;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog for creating and editing question options.
 */
public class OptionEditorDialog extends JDialog {
    
    private Option option;
    private boolean isNewOption;
    private boolean optionSaved;
    
    private JTextField optionTextField;
    private JCheckBox correctCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    
    /**
     * Constructor for creating or editing an option.
     *
     * @param parent the parent dialog
     * @param option the option to edit, or null for a new option
     * @param isNewOption true if creating a new option, false if editing
     */
    public OptionEditorDialog(JDialog parent, Option option, boolean isNewOption) {
        super(parent, "Option Editor", true);
        this.isNewOption = isNewOption;
        this.optionSaved = false;
        
        // Create new option or use existing
        if (option == null) {
            this.option = new Option(0, "", false);
            setTitle("Add New Option");
        } else {
            this.option = option;
            setTitle("Edit Option");
        }
        
        // Set up the dialog
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Initialize components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add action listeners
        addEventListeners();
        
        // Load option data
        loadOptionData();
    }
    
    private void initializeComponents() {
        optionTextField = new JTextField(30);
        correctCheckBox = new JCheckBox("This is the correct answer");
        correctCheckBox.setFont(ThemeManager.BODY_FONT);
        correctCheckBox.setForeground(ThemeManager.TEXT_PRIMARY);
        
        saveButton = ThemeManager.createStyledButton("Save");
        cancelButton = ThemeManager.createStyledButton("Cancel");
        
        // Set button colors
        cancelButton.setBackground(ThemeManager.SECONDARY_COLOR);
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        // Option details panel
        JPanel optionDetailsPanel = new JPanel(new GridBagLayout());
        optionDetailsPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Option text
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        optionDetailsPanel.add(ThemeManager.createStyledLabel("Option Text:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        optionDetailsPanel.add(optionTextField, gbc);
        
        // Correct checkbox
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        optionDetailsPanel.add(correctCheckBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeManager.SPACING_MEDIUM, 0));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add panels to main panel
        mainPanel.add(optionDetailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
    }
    
    private void addEventListeners() {
        saveButton.addActionListener((ActionEvent e) -> {
            saveOption();
        });
        
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
        });
    }
    
    private void loadOptionData() {
        optionTextField.setText(option.getText());
        correctCheckBox.setSelected(option.isCorrect());
    }
    
    private void saveOption() {
        // Validate input
        String optionText = optionTextField.getText().trim();
        boolean isCorrect = correctCheckBox.isSelected();
        
        if (optionText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter option text.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update option object
        option.setText(optionText);
        option.setCorrect(isCorrect);
        
        optionSaved = true;
        dispose();
    }
    
    /**
     * Checks if the option was successfully saved.
     *
     * @return true if the option was saved, false otherwise
     */
    public boolean isOptionSaved() {
        return optionSaved;
    }
    
    /**
     * Gets the option object.
     *
     * @return the option
     */
    public Option getOption() {
        return option;
    }
}