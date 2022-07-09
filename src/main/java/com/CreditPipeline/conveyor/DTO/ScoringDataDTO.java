package com.creditPipeline.conveyor.dto;

import com.creditPipeline.conveyor.enums.Gender;
import com.creditPipeline.conveyor.enums.MaritalStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;


@Schema(description = "Сущность данных для формирования кредита")
@Data
public class ScoringDataDTO {

    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private Gender gender;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;
    private String passportSeries;
    private String passportNumber;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private EmploymentDTO employmentDTO;
    private String account;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public LocalDate getPassportIssueDate() {
        return passportIssueDate;
    }

    public void setPassportIssueDate(LocalDate passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public String getPassportIssueBranch() {
        return passportIssueBranch;
    }

    public void setPassportIssueBranch(String passportIssueBranch) {
        this.passportIssueBranch = passportIssueBranch;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getDependentAmount() {
        return dependentAmount;
    }

    public void setDependentAmount(Integer dependentAmount) {
        this.dependentAmount = dependentAmount;
    }

    public EmploymentDTO getEmploymentDTO() {
        return employmentDTO;
    }

    public void setEmploymentDTO(EmploymentDTO employmentDTO) {
        this.employmentDTO = employmentDTO;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Boolean getInsuranceEnabled() {
        return isInsuranceEnabled;
    }

    public void setInsuranceEnabled(Boolean insuranceEnabled) {
        isInsuranceEnabled = insuranceEnabled;
    }

    public Boolean getSalaryClient() {
        return isSalaryClient;
    }

    public void setSalaryClient(Boolean salaryClient) {
        isSalaryClient = salaryClient;
    }

    @Override
    public String toString() {
        return "ScoringDataDTO{" +
                "amount=" + amount +
                ", term=" + term +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", gender=" + gender +
                ", birthdate=" + birthdate +
                ", passportSeries='" + passportSeries + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", passportIssueDate=" + passportIssueDate +
                ", passportIssueBranch='" + passportIssueBranch + '\'' +
                ", maritalStatus=" + maritalStatus +
                ", dependentAmount=" + dependentAmount +
                ", employmentDTO=" + employmentDTO +
                ", account='" + account + '\'' +
                ", isInsuranceEnabled=" + isInsuranceEnabled +
                ", isSalaryClient=" + isSalaryClient +
                '}';
    }
}
