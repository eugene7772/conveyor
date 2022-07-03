package com.creditPipeline.conveyor.controller;

import com.creditPipeline.conveyor.dto.CreditDTO;
import com.creditPipeline.conveyor.dto.LoanApplicationRequestDTO;
import com.creditPipeline.conveyor.dto.LoanOfferDTO;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.service.CalculateService;
import com.creditPipeline.conveyor.service.OffersService;
import com.creditPipeline.conveyor.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@Tag(name = "Конвеер")
public class Conveyor {

    private final ScoringService scoringService;
    private final CalculateService calculateService;
    private final OffersService offersService;

    @Autowired
    public Conveyor(ScoringService scoringService, CalculateService calculateService, OffersService offersService) {
        this.scoringService = scoringService;
        this.calculateService = calculateService;
        this.offersService = offersService;
    }

    private static Logger logger = LogManager.getLogger(Conveyor.class);

    @PostMapping("/conveyor/calculation")
    @Operation(
            summary = "Расчет",
            description = "Рассчитывает кредит")
    public CreditDTO calculate(ScoringDataDTO scoringDataDTO) {

        CreditDTO credit = new CreditDTO();
        logger.debug("Credit" + credit);

        if (scoringService.scoringData(scoringDataDTO)) {
            try {
                throw new Exception("Клиент не прошел скоринг");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            credit = calculateService.getCredit(scoringDataDTO);
        }
        logger.info("return credit");
        logger.debug("Credit" + credit);
        return credit;
    }

    @PostMapping("/conveyor/offers")
    @Operation(
            summary = "Создание предложений",
            description = "При прохождении прескоринга возвращает 4 предложения")
    public List<LoanOfferDTO> getOffers(@Valid LoanApplicationRequestDTO loanApplicationRequestDTO) throws IOException {

        logger.trace("getOffers");

        final long age = LocalDate.from(loanApplicationRequestDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        if (age < 18) {
            try {
                throw new Exception("Возраст не позволяет взять кредит: " + age + "< 18");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        logger.info("create offers");
        List<LoanOfferDTO> offers = offersService.getOffers(loanApplicationRequestDTO);

        logger.info("return offers");
        logger.debug("Offers" + offers);
        return offers;
    }

}
