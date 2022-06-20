package com.CreditPipeline.conveyor.controller;

import com.CreditPipeline.conveyor.DTO.LoanApplicationRequestDTO;
import com.CreditPipeline.conveyor.DTO.LoanOfferDTO;
import com.CreditPipeline.conveyor.service.CreateOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Предложения",description = "Контроллер создает 4 предложения")
public class Offers {

    @Autowired
    private CreateOfferService createOfferService;

    private static Logger logger = Logger.getLogger(Offers.class);

    @PostMapping("/conveyor/offers")
    @Operation(
            summary = "Создание предложений",
            description = "При прохождении прескоринга возвращает 4 предложения")
    public List<LoanOfferDTO> getOffers(@Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {

        logger.trace("getOffers");

        List<LoanOfferDTO> offers = new ArrayList<>();

        final long age = LocalDate.from(loanApplicationRequestDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        //Прескоринг (отсеивание неправильных заявок), создание 4 предложений и добавление их в список
        if (age<18) {
            return null;
        }
            logger.info("create offers");
            //Создание 4 предложений

            //Ставка 14.5
            LoanOfferDTO offer1 = createOfferService.createOffer(0L,false,false, loanApplicationRequestDTO);
            offers.add(offer1);

            //12.5
            LoanOfferDTO offer2 = createOfferService.createOffer(1L,false,true, loanApplicationRequestDTO);
            offers.add(offer2);

            //Ставка 9.5
            LoanOfferDTO offer3 = createOfferService.createOffer(2L,true,false, loanApplicationRequestDTO);
            offers.add(offer3);

            //7.5
            LoanOfferDTO offer4 = createOfferService.createOffer(3L,true,true, loanApplicationRequestDTO);
            offers.add(offer4);

        logger.info("return offers");
        //Возвращает 4 предложения
        return offers;
    }

}
