package com.creditPipeline.conveyor.service;

import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.enums.EmploymentStatus;
import com.creditPipeline.conveyor.enums.Gender;
import com.creditPipeline.conveyor.enums.MaritalStatus;
import com.creditPipeline.conveyor.enums.Position;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@PropertySource("classpath:application.properties")
public class ScoringService {

    @Value("${baseRate}")
    private BigDecimal baseRate;

    @Value("${baseRate}")
    private BigDecimal rateForOffer;

    private static Logger logger = LogManager.getLogger(ScoringService.class);

    public boolean scoringData(ScoringDataDTO scoringDataDTO) {
        if (isCreditUnavailable(scoringDataDTO)) {
            return true;
        } else {
            return false;
        }
    }

    public BigDecimal calculateRate(ScoringDataDTO scoringDataDTO) {

        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

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
        if (Gender.FEMALE.equals(scoringDataDTO.getGender()) && (35 <= age && age <= 60) ||
                Gender.MALE.equals(scoringDataDTO.getGender()) && (30 <= age && age <= 55)) {
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
        logger.debug("baseRate: " + baseRate);
        return baseRate;
    }

    private boolean isCreditUnavailable(ScoringDataDTO scoringDataDTO) {

        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);
        Integer workExperienceTotal = scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal();
        Integer workExperienceCurrent = scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent();

        if (EmploymentStatus.UNEMPLOYED.equals(scoringDataDTO.getEmploymentDTO().getEmploymentStatus())) {
            try {
                throw new Exception("Клиент безработный");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmploymentDTO().getSalary().multiply(BigDecimal.valueOf(20))) >= 0) {
            try {
                throw new Exception("Сумма займа больше, чем 20 зарплат");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        if (age < 20 || age > 60) {
            try {
                throw new Exception("Возраст клиента не подходит");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        if (workExperienceTotal < 12 || workExperienceCurrent < 3) {
            try {
                throw new Exception("Не соответствует стаж работы");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public BigDecimal calculateRateToOffer(Boolean insuranceEnabled, Boolean salaryClient) throws IOException {

        if (insuranceEnabled) {
            rateForOffer = rateForOffer.subtract(BigDecimal.valueOf(2));
        } else {
            rateForOffer = rateForOffer.add(BigDecimal.valueOf(2));
        }
        if (salaryClient) {
            rateForOffer = rateForOffer.subtract(BigDecimal.valueOf(1.5));
        } else {
            rateForOffer = rateForOffer.add(BigDecimal.valueOf(1.5));
        }
        logger.debug("rateForOffer: " + rateForOffer);
        return rateForOffer;
    }

}
