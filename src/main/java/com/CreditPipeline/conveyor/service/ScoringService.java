package com.creditPipeline.conveyor.service;

import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.enums.EmploymentStatus;
import com.creditPipeline.conveyor.enums.Gender;
import com.creditPipeline.conveyor.enums.MaritalStatus;
import com.creditPipeline.conveyor.enums.Position;
import com.creditPipeline.conveyor.exception.ScoringServiceException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ScoringService {

    @Value("${baseRate}")
    private BigDecimal baseRate;

    private final static Logger logger = LogManager.getLogger(ScoringService.class);

    public BigDecimal calculateRate(ScoringDataDTO scoringDataDTO) {

        baseRate = BigDecimal.valueOf(12);

        if (EmploymentStatus.SELF_EMPLOYED.equals(scoringDataDTO.getEmploymentDTO().getEmploymentStatus())) {
            baseRate = baseRate.add(BigDecimal.valueOf(1));
        }
        if (EmploymentStatus.BUSINESS_OWNER.equals(scoringDataDTO.getEmploymentDTO().getEmploymentStatus())) {
            baseRate = baseRate.add(BigDecimal.valueOf(3));
        }
        if (Position.MIDDLE_MANAGER.equals(scoringDataDTO.getEmploymentDTO().getPosition())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        }
        if (Position.TOP_MANAGER.equals(scoringDataDTO.getEmploymentDTO().getPosition())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(4));
        }
        if (MaritalStatus.MARRIED.equals(scoringDataDTO.getMaritalStatus())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(3));
        }
        if (MaritalStatus.UNMARRIED.equals(scoringDataDTO.getMaritalStatus())) {
            baseRate = baseRate.add(BigDecimal.valueOf(1));
        }
        if (Gender.FEMALE.equals(scoringDataDTO.getGender()) && (35 <= calculateAge(scoringDataDTO.getBirthdate()) && calculateAge(scoringDataDTO.getBirthdate()) <= 60) ||
                Gender.MALE.equals(scoringDataDTO.getGender()) && (30 <= calculateAge(scoringDataDTO.getBirthdate()) && calculateAge(scoringDataDTO.getBirthdate()) <= 55)) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(3));
        }
        if (Gender.NOT_BINARY.equals(scoringDataDTO.getGender())) {
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

    public boolean isCreditAvailable(ScoringDataDTO scoringDataDTO) throws ScoringServiceException {

        Integer workExperienceTotal = scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal();
        Integer workExperienceCurrent = scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent();

        if (EmploymentStatus.UNEMPLOYED.equals(scoringDataDTO.getEmploymentDTO().getEmploymentStatus())) {
            throw new ScoringServiceException("Клиент безработный");
        }
        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmploymentDTO().getSalary().multiply(BigDecimal.valueOf(20))) >= 0) {
            throw new ScoringServiceException("Сумма займа больше, чем 20 зарплат");
        }
        if (calculateAge(scoringDataDTO.getBirthdate()) < 20 || calculateAge(scoringDataDTO.getBirthdate()) > 60) {
            throw new ScoringServiceException("Возраст клиента не подходит");
        }
        if (workExperienceTotal < 12 || workExperienceCurrent < 3) {
            throw new ScoringServiceException("Не соответствует стаж работы");
        }
        logger.debug("isCreditAvailable = " + true);
        return true;
    }

    public BigDecimal calculateRateToOffer(Boolean insuranceEnabled, Boolean salaryClient) {

        baseRate = BigDecimal.valueOf(12);

        if (insuranceEnabled) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        } else {
            baseRate = baseRate.add(BigDecimal.valueOf(2));
        }
        if (salaryClient) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(1.5));
        } else {
            baseRate = baseRate.add(BigDecimal.valueOf(1.5));
        }
        return baseRate;
    }

    public Long calculateAge(LocalDate birthDate) {
        return LocalDate.from(birthDate).until(LocalDate.now(), ChronoUnit.YEARS);
    }

}
