package com.quizapp.ui.admin;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel for user management (viewing, editing, and deleting users).
 */
public class UserManagementPanel extends JPanel {
    
    private User currentUser;
    private UserDAO userDAO;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton editUserButton;
    private JButton deleteUserButton;
    private JButton refreshButton;
    
    public UserManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        
        setLayout(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        setBackground(ThemeManager.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        
        // Initialize components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add action listeners
        addEventListeners();
        
        // Load users
        loadUsers();
    }
    
    private void initializeComponents() {
        // Create table model with column names
        String[] columnNames = {"ID", "Username", "Email", "Admin"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class; // For the admin column
                }
                return super.getColumnClass(columnIndex);
            }
        };
        
        // Create table with the model
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(ThemeManager.SPACING_LARGE);
        userTable.getTableHeader().setFont(ThemeManager.BODY_FONT.deriveFont(Font.BOLD));
        userTable.setFont(ThemeManager.BODY_FONT);
        
        // Create buttons
        editUserButton = ThemeManager.createStyledButton("Edit User");
        deleteUserButton = ThemeManager.createStyledButton("Delete User");
        refreshButton = ThemeManager.createStyledButton("Refresh");
        
        // Set button colors
        deleteUserButton.setBackground(ThemeManager.ERROR_COLOR);
    }
    
    private void setupLayout() {
        // Create title label
        JLabel titleLabel = ThemeManager.createStyledLabel("User Management");
        titleLabel.setFont(ThemeManager.HEADING_FONT);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeManager.SPACING_SMALL, 0));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(editUserButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(Box.createHorizontalStrut(ThemeManager.SPACING_LARGE));
        buttonPanel.add(refreshButton);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addEventListeners() {
        editUserButton.addActionListener((ActionEvent e) -> {
            editUser();
        });
        
        deleteUserButton.addActionListener((ActionEvent e) -> {
            deleteUser();
        });
        
        refreshButton.addActionListener((ActionEvent e) -> {
            loadUsers();
        });
    }
    
    private void loadUsers() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Get all users
            List<User> users = userDAO.getAllUsers();
            
            // Add users to table
            for (User user : users) {
                tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.isAdmin()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) userTable.getValueAt(selectedRow, 0);
        
        // Prevent editing your own user through this panel
        if (userId == currentUser.getId()) {
            JOptionPane.showMessageDialog(this,
                    "You cannot edit your own user through this panel. Use the Profile page instead.",
                    "Cannot Edit",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            User user = userDAO.getUserById(userId);
            
            if (user != null) {
                UserEditorDialog editorDialog = new UserEditorDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this), user);
                editorDialog.setVisible(true);
                
                // Refresh table after dialog is closed
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this,
                        "User not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading user: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) userTable.getValueAt(selectedRow, 0);
        String username = (String) userTable.getValueAt(selectedRow, 1);
        
        // Prevent deleting your own user
        if (userId == currentUser.getId()) {
            JOptionPane.showMessageDialog(this,
                    "You cannot delete your own user account.",
                    "Cannot Delete",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the user '" + username + "'? This cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = userDAO.deleteUser(userId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "User deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete user.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting user: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}