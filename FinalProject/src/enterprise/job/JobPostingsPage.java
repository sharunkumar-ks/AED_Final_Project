package enterprise.job;

import domain.Application;
import domain.Roles;
import domain.Validator;
import helpers.TableHelpers;
import models.JobPosting;
import models.tablemodels.BaseTableModel;
import models.tablemodels.CompanyPostingsTableModel;
import models.tablemodels.JobPostingsTableModel;
import utils.Dialog;
import views.BaseFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class JobPostingsPage extends BaseFrame {
    private final int APPLY_COLUMN_NUMBER = 4;
    private JPanel p;
    private JPanel mainPanel;
    private JLabel heading;
    private JButton addPersonButton;
    private JButton cancelEditButton;
    private JPanel addPersonPane;
    private JTextField firstName;
    private JTextField lastName;
    private JTextField dateOfBirth;
    private JComboBox role;
    private JTextField username;
    private JPasswordField password;
    private JTextField jobTitle;
    private JTextField description;
    private JTextField category;

    private JTable people;
    private JTextArea validationText;
    private JPanel jobPosterPanel;
    private JButton myApplicationsButton;
    private JScrollPane jobPostings;
    private JTextField email;
    private JTextField phone;

    private boolean editMode;
    private int currentlyEditingEmployee;
    private String existingPassword;
    private boolean isStudent;
    private boolean isHr;

    public JobPostingsPage() {
        super();
        setContentPane(p);
        setupRoles();

        displayJobPostings();

        setupActions();


        myApplicationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MyApplicationPage().setVisible(true);
            }
        });
        people.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // handle mouse double click
                if (e.getClickCount() == 2) {
                    var row = people.getSelectedRow();
                    int column = people.getSelectedColumn(); // selected column

                    if (column == APPLY_COLUMN_NUMBER) {
                        JobPosting jobPosting = ((BaseTableModel<JobPosting>) people.getModel()).getDataAt(row);

                        new JobApplicationPage(jobPosting, () -> {
                            displayJobPostings();
                        }).setVisible(true);
                    }

                }
            }
        });
    }

    private void setupRoles() {
        var person = Application.getCurrentlyLoggedInPerson();

        isStudent = person.hasAtLeastOneOfRole(new String[]{
                Roles.COLLEGE_STUDENT,
                Roles.JOB_PORTAL_USER,
        });

        isHr = person.hasAtLeastOneOfRole(new String[]{
                Roles.COMPANY_HR,
                Roles.JOB_PORTAL_ADMIN,
                Roles.ADMIN,
        });

        if (!isHr) {
            addPersonPane.setVisible(false);
            addPersonButton.setVisible(false);
            myApplicationsButton.setVisible(true);
        } else {
            jobPosterPanel.setVisible(true);
            addPersonButton.setVisible(true);
            myApplicationsButton.setVisible(false);
        }
    }


    private void addPerson() {
        if (!validateFields()) return;


        var person = new JobPosting(0, jobTitle.getText(), description.getText(), category.getText(), 1);

        if (!editMode) {

            try {
                Application.Database.JobPostings.add(person);
                Dialog.show(person.getTitle() + " added successfully.");
                displayJobPostings();


            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } else {
            try {


                Application.Database.JobPostings.update(person);
                Dialog.show(person.getTitle() + " updated successfully.");
                displayJobPostings();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }


    private boolean validateFields() {
        var validationMessages = new ArrayList<String>();
        if (!Validator.checkTextsBlank(new JTextField[]{jobTitle, description, category}))
            validationMessages.add("Enter all the mandatory fields");


        if (validationMessages.size() == 0) {
            validationText.setText("");
            return true;
        }

        validationText.setText(String.join("\n", validationMessages));

        return false;
    }

    private void setupActions() {
        addPersonButton.addActionListener(e -> addPerson());
    }

    private void displayJobPostings() {
        try {
            if (isHr) {
                people.setModel(new CompanyPostingsTableModel().loadData(Application.Database.JobPostings.getAll()));

            } else {

                var myJobApplications =
                        Application.Database.JobApplications.getAll().stream()
                                .filter(jobApplication -> jobApplication.getPersonId() == Application.getCurrentlyLoggedInPerson().getId())
                                .map(jobApplication -> jobApplication.getJobPostingId())
                                .collect(Collectors.toList());

                people.setModel(new JobPostingsTableModel().loadData(
                        Application.Database.JobPostings.getAll()
                                .stream().filter(jobPosting -> !myJobApplications.contains(jobPosting.getId()))
                                .collect(Collectors.toList())
                ));
            }
        } catch (SQLException e) {
            Dialog.error("Error getting people");
            return;
        }

        try {
            TableHelpers.centerColumn(people, 0);
            TableHelpers.centerColumn(people, 3);
            TableHelpers.centerColumn(people, 4);
        } catch (Exception e) {
            // ignore
        }
    }
}
