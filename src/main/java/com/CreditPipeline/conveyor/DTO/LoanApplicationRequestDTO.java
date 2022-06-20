package com.CreditPipeline.conveyor.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

@Data
@Schema(description = "Сущность заявки на получение кредита")
public class LoanApplicationRequestDTO {

    @Min(value = 10000)
    private BigDecimal amount;

    @Min(value = 6)
    private Integer term;

    @NotEmpty
    @Size(min = 2, max = 30)
    private String firstName;

    @NotEmpty
    @Size(min = 2, max = 30)
    private String lastName;

    @NotNull
    @Size(min = 2, max = 30)
    private String middleName;

    @Email(regexp = "\" [\\\\w\\\\.]{2,50}@[\\\\w\\\\.]{2,20}\"")
    private String email;

    private LocalDate birthdate;

    private String passportSeries;

    private String passportNumber;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "LoanApplicationRequestDTO{" +
                "amount=" + amount +
                ", term=" + term +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", passportSeries='" + passportSeries + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                '}';
    }
}
