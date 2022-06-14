package com.CreditPipeline.conveyor.controller;

import com.CreditPipeline.conveyor.DTO.LoanApplicationRequestDTO;
import com.CreditPipeline.conveyor.DTO.LoanOfferDTO;
import com.CreditPipeline.conveyor.service.PrescoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Предложения",description = "Контроллер создает 4 предложения")
public class Offers {

    @Autowired
    PrescoringService prescoringService;

    private static Logger logger = Logger.getLogger(Offers.class);

    @PostMapping("/conveyor/offers")
    @Operation(
            summary = "Создание предложений",
            description = "При прохождении прескоринга возвращает 4 предложения")
    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        logger.trace("getOffers");

        final BigDecimal baseRate = new BigDecimal(11);

        List<LoanOfferDTO> offers = new ArrayList<>();

        //Прескоринг (отсеивание неправильных заявок), создание 4 предложений и добавление их в список
        if (!prescoringService.prescoringData(loanApplicationRequestDTO)) {
            return null;
        }
            logger.info("create offers");
            //Создание 4 предложений

            //Ставка 12.5
            LoanOfferDTO offer1 = new LoanOfferDTO();
            offer1.setApplicationId(0L);
            offer1.setInsuranceEnabled(false);
            offer1.setSalaryClient(false);
            offer1.setRequestedAmount(loanApplicationRequestDTO.getAmount());
            offer1.setTerm(loanApplicationRequestDTO.getTerm());
            offer1.setRate(baseRate.add(BigDecimal.valueOf(3.5)));
            offer1.setMonthlyPayment(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())));
            offer1.setTotalAmount(loanApplicationRequestDTO.getAmount());
            offers.add(offer1);

            //10.5
            LoanOfferDTO offer2 = new LoanOfferDTO();
            offer2.setApplicationId(1L);
            offer2.setInsuranceEnabled(false);
            offer2.setSalaryClient(true);
            offer2.setRequestedAmount(loanApplicationRequestDTO.getAmount());
            offer2.setTerm(loanApplicationRequestDTO.getTerm());
            offer2.setRate(baseRate.add(BigDecimal.valueOf(1.5)));
            offer2.setMonthlyPayment(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())));
            offer2.setTotalAmount(loanApplicationRequestDTO.getAmount());
            offers.add(offer2);

            //Ставка 7.5
            LoanOfferDTO offer3 = new LoanOfferDTO();
            offer3.setApplicationId(2L);
            offer3.setInsuranceEnabled(true);
            offer3.setSalaryClient(false);
            offer3.setRequestedAmount(loanApplicationRequestDTO.getAmount());
            offer3.setTerm(loanApplicationRequestDTO.getTerm());
            offer3.setRate(baseRate.subtract(BigDecimal.valueOf(1.5)));
            offer3.setMonthlyPayment(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())));
            offer3.setTotalAmount(loanApplicationRequestDTO.getAmount());
            offers.add(offer3);

            //5.5
            LoanOfferDTO offer4 = new LoanOfferDTO();
            offer4.setApplicationId(3L);
            offer4.setInsuranceEnabled(true);
            offer4.setSalaryClient(true);
            offer4.setRequestedAmount(loanApplicationRequestDTO.getAmount());
            offer4.setTerm(loanApplicationRequestDTO.getTerm());
            offer4.setRate(baseRate.subtract(BigDecimal.valueOf(3.5)));
            offer4.setMonthlyPayment(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())));
            offer4.setTotalAmount(loanApplicationRequestDTO.getAmount());
            offers.add(offer4);

        logger.info("return offers");
        //Возвращает 4 предложения
        return offers;
    }

}