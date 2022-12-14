package enterprise.college;

import domain.Application;
import domain.Roles;
import views.BaseFrame;

import javax.swing.*;

public class CollegeHomePage extends BaseFrame {
    private JPanel p;
    private JPanel mainPane;
    private JLabel heading;
    private JButton adminPortalButton;
    private JButton HRPortalButton;
    private JButton studentPortalButton;
    private JButton studentRegistrationButton;

    public CollegeHomePage() {
        super();
        setContentPane(p);
        try {
            setupRoles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setupActions();
    }

    private void setupRoles() {
        var person = Application.getCurrentlyLoggedInPerson();

        boolean isAdmin = person.hasRole(Roles.COLLEGE_ADMIN);
        boolean isHr = person.hasRole(Roles.COLLEGE_HR);
        boolean isStudent = person.hasRole(Roles.COLLEGE_STUDENT);

        if (!isAdmin) {
            adminPortalButton.setVisible(false);
        }

        if (!isHr) {
            HRPortalButton.setVisible(false);
        }

        if (!isStudent) {
            studentPortalButton.setVisible(false);
        }

        if (isAdmin || isHr || isStudent) {
            studentRegistrationButton.setVisible(false);
        }
    }

    private void setupActions() {
        adminPortalButton.addActionListener(e -> swapWindow(new CollegeAdminHomePage()));
        HRPortalButton.addActionListener(e -> swapWindow(new CollegeHRHomePage()));
        studentPortalButton.addActionListener(e -> swapWindow(new CollegeStudentHomePage()));
        studentRegistrationButton.addActionListener(e -> swapWindow(new StudentRegistrationPage()));
    }
}
