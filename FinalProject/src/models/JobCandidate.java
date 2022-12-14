package models;

import domain.Application;

import java.sql.SQLException;
import java.util.Objects;

public class JobCandidate {
    private int id;
    private int personId;
    private int jobApplicationId;
    private int interviewRound;
    private String result;
    private boolean isRejected;
    private boolean isAccepted;

    public JobCandidate(int id, int personId, int jobApplicationId, int interviewRound, String result,
                        boolean isRejected, boolean isAccepted) {
        this.id = id;
        this.personId = personId;
        this.jobApplicationId = jobApplicationId;
        this.interviewRound = interviewRound;
        this.result = result;
        this.isRejected = isRejected;
        this.isAccepted = isAccepted;
    }

    public JobApplication getJobApplication() throws SQLException {
        return Application.Database.JobApplications.getById(jobApplicationId);
    }

    public Person getPerson() throws SQLException {
        return Application.Database.Persons.getById(personId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(int jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public int getInterviewRound() {
        return interviewRound;
    }

    public void setInterviewRound(int interviewRound) {
        this.interviewRound = interviewRound;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobCandidate that = (JobCandidate) o;
        return id == that.id && personId == that.personId && jobApplicationId == that.jobApplicationId && interviewRound == that.interviewRound && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, personId, jobApplicationId, interviewRound, result);
    }

    public boolean getIsRejected() {
        return isRejected;
    }

    public void setIsRejected(boolean isRejected) {
        this.isRejected = isRejected;
    }

    public boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}
