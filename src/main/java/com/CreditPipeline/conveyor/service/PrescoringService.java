package com.CreditPipeline.conveyor.service;

import com.CreditPipeline.conveyor.DTO.LoanApplicationRequestDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PrescoringService {

    public boolean prescoringData(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        final long age = LocalDate.from(loanApplicationRequestDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        if (loanApplicationRequestDTO.getFirstName().length() >= 2 & loanApplicationRequestDTO.getFirstName().length() <= 30 &
                loanApplicationRequestDTO.getLastName().length() >= 2 & loanApplicationRequestDTO.getLastName().length() <= 30 &
                loanApplicationRequestDTO.getAmount().compareTo(BigDecimal.valueOf(10000)) >= 0 &
                loanApplicationRequestDTO.getTerm() >= 6 &
                age >= 18 &
                NumberUtils.isParsable(loanApplicationRequestDTO.getPassportSeries()) &
                loanApplicationRequestDTO.getPassportSeries().length() == 4 &
                NumberUtils.isParsable(loanApplicationRequestDTO.getPassportNumber()) &
                loanApplicationRequestDTO.getPassportNumber().length() == 6
        ) {
            if (loanApplicationRequestDTO.getMiddleName() != null) {
                if (!(loanApplicationRequestDTO.getMiddleName().length() >= 2) & !(loanApplicationRequestDTO.getMiddleName().length() <= 30)) {
                    //Возращается отказ (0 предложений)
                    return false;
                }
            }
        }
        return true;
    }
}
