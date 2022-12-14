package enterprise.training;

import domain.Application;
import domain.Roles;
import domain.Validator;
import helpers.TableHelpers;
import models.TrainingModule;
import models.tablemodels.TrainingModuleAdminTableModel;
import models.tablemodels.TrainingModuleTableModel;
import utils.Dialog;
import views.BaseFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class TrainingListPage extends BaseFrame {

    private final int VIEW_COLUMN_NUMBER = 3;
    private final int EDIT_COLUMN_NUMBER = 4;
    private final int DELETE_COLUMN_NUMBER = 5;
    private boolean editMode;
    private int currentlyEditingEmployee;

    private JPanel p;
    private JPanel mainPanel;
    private JLabel heading;
    private JButton addModuleButton;
    private JTable modules;
    private JTextArea validationText;
    private JButton startTrainingButton;
    private JPanel addPersonPane;
    private JTextField description;
    private JTextField name;
    private JButton cancelButton;
    private boolean isTrainer;
    private boolean isTrainee;

    public TrainingListPage() {
        setupActions();
        setupRoles();
        displayModules();
        setContentPane(p);
        setEditMode(false);
    }

    private void setupRoles() {
        var person = Application.getCurrentlyLoggedInPerson();

        isTrainer = person.hasRole(Roles.TRAINING_SITE_ADMIN);
        isTrainee = person.hasRole(Roles.TRAINEE);

        // TODO Roles Restriction

        if (!isTrainer) {
            addPersonPane.setVisible(false);
            addModuleButton.setVisible(false);
            validationText.setVisible(false);
        } else {
            addPersonPane.setVisible(true);
            addModuleButton.setVisible(true);
        }
    }

    private void displayModules() {
        try {

            if (isTrainer) {
                modules.setModel(new TrainingModuleAdminTableModel().loadData(Application.Database.TrainingModules.getAll()));
            } else {
                modules.setModel(new TrainingModuleTableModel().loadData(Application.Database.TrainingModules.getAll()));
            }

            for (int i = 0; i < modules.getColumnCount(); i++) {
                TableHelpers.centerColumn(modules, i);
            }
        } catch (SQLException e) {
            Dialog.error("Error loading training modules");
        }
    }

    private void addPerson() {
        if (!validateFields()) return;


        var person = new TrainingModule(currentlyEditingEmployee, name.getText(), description.getText());

        if (!editMode) {

            try {
                Application.Database.TrainingModules.add(person);
                Dialog.show(person.getName() + " added successfully.");
                displayModules();


            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } else {
            try {


                Application.Database.TrainingModules.update(person);
                Dialog.show(person.getName() + " updated successfully.");
                displayModules();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        setEditMode(false);
    }

    private void setEditMode(boolean mode) {
        editMode = mode;
        cancelButton.setVisible(mode);

        if (mode) {
            addModuleButton.setText("Update Module");
            addPersonPane.setBorder(BorderFactory.createTitledBorder("Edit Module"));
        } else {
            addModuleButton.setText("Add Module");
            addPersonPane.setBorder(BorderFactory.createTitledBorder("Add Module"));

            for (var text : new JTextField[]{name, description}) {
                text.setText("");
                text.setBorder(BorderFactory.createLineBorder(Color.black));
            }
        }
    }

    private boolean validateFields() {
        var validationMessages = new ArrayList<String>();
        if (!Validator.checkTextsBlank(new JTextField[]{name, description}))
            validationMessages.add("Enter all the mandatory fields");


        if (validationMessages.size() == 0) {
            validationText.setText("");
            return true;
        }

        validationText.setText(String.join("\n", validationMessages));

        return false;
    }

    private void setupActions() {
        addModuleButton.addActionListener(e -> addPerson());
        cancelButton.addActionListener(e -> setEditMode(false));

        modules.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                JTable target = (JTable) me.getSource();
                int row = target.getSelectedRow(); // selected row
                int column = target.getSelectedColumn(); // selected column

                if (me.getClickCount() == 2) {
                    System.out.println("double click");
                    int personId = Integer.parseInt(target.getModel().getValueAt(row, 0) + "");

                    String personName = target.getModel().getValueAt(row, 1) + "";

                    if (column == DELETE_COLUMN_NUMBER) {
                        System.out.println("Delete Clicked");


                        int result = Dialog.confirm("Are you sure you want to delete " + personName + "?", "Delete Person", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        System.out.println(result);

                        if (result == JOptionPane.YES_OPTION) {
                            try {
                                Application.Database.TrainingModules.delete(personId);
                                displayModules();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (column == EDIT_COLUMN_NUMBER) {


                        setEditMode(true);
                        currentlyEditingEmployee = personId;
                        try {
                            TrainingModule trainingModule = Application.Database.TrainingModules.getById(personId);
                            name.setText(trainingModule.getName());
                            description.setText(trainingModule.getDescription());

                        } catch (SQLException e) {
                            Dialog.error("Error getting person");
                        }

                    } else if (column == VIEW_COLUMN_NUMBER) {
                        System.out.println("View Clicked");
                        try {
                            new TrainingModulePage(Application.Database.TrainingModules.getById(personId)).setVisible(true);
                        } catch (SQLException e) {
                            Dialog.error("Error getting person");
                        }
                    }

                }
            }
        });

    }
}
