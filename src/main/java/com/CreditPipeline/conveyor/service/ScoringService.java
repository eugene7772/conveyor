package com.CreditPipeline.conveyor.service;

import com.CreditPipeline.conveyor.DTO.ScoringDataDTO;
import com.CreditPipeline.conveyor.enums.EmploymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ScoringService {

    public boolean scoringData(ScoringDataDTO scoringDataDTO) {

        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        if (scoringDataDTO.getEmploymentDTO().getEmploymentStatus().toString().equals(EmploymentStatus.Unemployed.toString()) ||
                scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmploymentDTO().getSalary().multiply(BigDecimal.valueOf(20))) >= 0 ||
                age < 20 ||
                age > 60 ||
                scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal() < 12 ||
                scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent() < 3) {
            return false;
        }
        return true;
    }
}
