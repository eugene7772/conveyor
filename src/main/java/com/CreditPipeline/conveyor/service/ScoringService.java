package com.CreditPipeline.conveyor.service;

import com.CreditPipeline.conveyor.DTO.ScoringDataDTO;
import com.CreditPipeline.conveyor.enums.EmploymentStatus;
import com.CreditPipeline.conveyor.enums.Gender;
import com.CreditPipeline.conveyor.enums.MaritalStatus;
import com.CreditPipeline.conveyor.enums.Position;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ScoringService {

    public boolean scoringData(ScoringDataDTO scoringDataDTO) {

        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);
        String empStatus = scoringDataDTO.getEmploymentDTO().getEmploymentStatus().toString();
        Integer workExperienceTotal = scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal();
        Integer workExperienceCurrent = scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent();

        if(empStatus.equals(EmploymentStatus.Unemployed.toString())){
            return false;
        }
        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmploymentDTO().getSalary().multiply(BigDecimal.valueOf(20))) >= 0){
            return false;
        }
        if(age < 20 || age > 60){
            return false;
        }
        if(workExperienceTotal < 12 || workExperienceCurrent < 3) {
            return false;
        }
        return true;
    }

    public BigDecimal calculateRate(ScoringDataDTO scoringDataDTO){
        BigDecimal baseRate = new BigDecimal(11);
        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        if (scoringDataDTO.getEmploymentDTO().getEmploymentStatus().toString().equals(EmploymentStatus.SelfEmployed.toString())) {
            baseRate = baseRate.add(BigDecimal.valueOf(1));
        }
        if (scoringDataDTO.getEmploymentDTO().getEmploymentStatus().toString().equals(EmploymentStatus.BusinessOwner.toString())) {
            baseRate = baseRate.add(BigDecimal.valueOf(3));
        }
        if (scoringDataDTO.getEmploymentDTO().getPosition().toString().equals(Position.MiddleManager.toString())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        }
        if (scoringDataDTO.getEmploymentDTO().getPosition().toString().equals(Position.TopManager.toString())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(4));
        }
        if (scoringDataDTO.getMaritalStatus().Married.toString().equals(MaritalStatus.Married.toString())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(3));
        }
        if (scoringDataDTO.getMaritalStatus().toString().equals(MaritalStatus.Unmarried.toString())) {
            baseRate = baseRate.add(BigDecimal.valueOf(1));
        }
        if (scoringDataDTO.getGender().Female.toString().equals(Gender.Female.toString()) && (35 <= age && age <= 60) ||
                scoringDataDTO.getGender().Male.toString().equals(Gender.Male.toString()) && (30 <= age && age <= 55)) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(3));
        }
        if (scoringDataDTO.getGender().NotBinary.toString().equals(Gender.NotBinary.toString())) {
            baseRate = baseRate.add(BigDecimal.valueOf(3));
        }
        if (scoringDataDTO.getInsuranceEnabled()) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        }
        if (scoringDataDTO.getSalaryClient()) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(1));
        }
        return baseRate;
    }
}
