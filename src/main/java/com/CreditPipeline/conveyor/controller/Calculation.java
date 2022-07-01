package com.CreditPipeline.conveyor.controller;

import com.CreditPipeline.conveyor.DTO.CreditDTO;
import com.CreditPipeline.conveyor.DTO.PaymentScheduleElement;
import com.CreditPipeline.conveyor.DTO.ScoringDataDTO;
import com.CreditPipeline.conveyor.service.CalculateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;
import com.CreditPipeline.conveyor.service.ScoringService;


@RestController
@Tag(name = "Калькулятор",description = "Данный контроллер рассчитывает кредит")
public class Calculation {


    @Autowired
    private ScoringService scoringService;

    @Autowired
    private CalculateService calculateService;

    private static Logger logger = Logger.getLogger(Calculation.class);

    @PostMapping("/conveyor/calculation")
    @Operation(
            summary = "Расчет",
            description = "Рассчитывает кредит")
    public CreditDTO calculate(ScoringDataDTO scoringDataDTO) {

        CreditDTO credit;

        if ( !scoringService.scoringData(scoringDataDTO) ) {
            return null;
        } else {
            credit = calculateService.getCredit(scoringDataDTO);
        }
        logger.info("return credit");
        return credit;
    }
}
