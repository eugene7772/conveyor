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
        if (isScoring(scoringDataDTO)){
            return true;
        }else {
            return false;
        }
    }

    public BigDecimal calculateRate(ScoringDataDTO scoringDataDTO){
        BigDecimal baseRate = new BigDecimal(11);
        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        if (EmploymentStatus.SelfEmployed.equals(scoringDataDTO.getEmploymentDTO().getEmploymentStatus())) {
            baseRate = baseRate.add(BigDecimal.valueOf(1));
        }
        if (EmploymentStatus.BusinessOwner.equals(scoringDataDTO.getEmploymentDTO().getEmploymentStatus())) {
            baseRate = baseRate.add(BigDecimal.valueOf(3));
        }
        if (Position.MiddleManager.equals(scoringDataDTO.getEmploymentDTO().getPosition())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        }
        if (Position.TopManager.equals( scoringDataDTO.getEmploymentDTO().getPosition())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(4));
        }
        if (MaritalStatus.Married.equals(scoringDataDTO.getMaritalStatus())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(3));
        }
        if (MaritalStatus.Unmarried.equals(scoringDataDTO.getMaritalStatus())) {
            baseRate = baseRate.add(BigDecimal.valueOf(1));
        }
        if (Gender.Female.equals( scoringDataDTO.getGender()) && (35 <= age && age <= 60) ||
                Gender.Male.equals( scoringDataDTO.getGender()) && (30 <= age && age <= 55)) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(3));
        }
        if (Gender.NotBinary.equals( scoringDataDTO.getGender())) {
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

    private boolean isScoring(ScoringDataDTO scoringDataDTO){

        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);
        Integer workExperienceTotal = scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal();
        Integer workExperienceCurrent = scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent();

        if (EmploymentStatus.Unemployed.equals(scoringDataDTO.getEmploymentDTO().getEmploymentStatus())) {
            return false;
        }
        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmploymentDTO().getSalary().multiply(BigDecimal.valueOf(20))) >= 0) {
            return false;
        }
        if (age < 20 || age > 60) {
            return false;
        }
        if (workExperienceTotal < 12 || workExperienceCurrent < 3) {
            return false;
        }
        return true;
    }
}
