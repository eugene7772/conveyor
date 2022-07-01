package com.CreditPipeline.conveyor.controller;

import com.CreditPipeline.conveyor.DTO.LoanApplicationRequestDTO;
import com.CreditPipeline.conveyor.DTO.LoanOfferDTO;
import com.CreditPipeline.conveyor.service.OffersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@Tag(name = "Предложения", description = "Контроллер создает 4 предложения")
public class Offers {

    @Autowired
    private OffersService offersService;

    private static Logger logger = Logger.getLogger(Offers.class);

    @PostMapping("/conveyor/offers")
    @Operation(
            summary = "Создание предложений",
            description = "При прохождении прескоринга возвращает 4 предложения")
    public List<LoanOfferDTO> getOffers(@Valid LoanApplicationRequestDTO loanApplicationRequestDTO) throws IOException {

        logger.trace("getOffers");

        final long age = LocalDate.from(loanApplicationRequestDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        if (age < 18) {
            return null;
        }
        logger.info("create offers");
        //Создание 4 предложений
        List<LoanOfferDTO> offers = offersService.getOffers(loanApplicationRequestDTO);

        logger.info("return offers");
        //Возвращает 4 предложения
        return offers;
    }

}
