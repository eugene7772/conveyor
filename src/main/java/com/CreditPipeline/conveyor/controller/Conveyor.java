package com.creditPipeline.conveyor.controller;

import com.creditPipeline.conveyor.dto.CreditDTO;
import com.creditPipeline.conveyor.dto.LoanApplicationRequestDTO;
import com.creditPipeline.conveyor.dto.LoanOfferDTO;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.exception.AgeException;
import com.creditPipeline.conveyor.exception.ScoringServiceException;
import com.creditPipeline.conveyor.service.CalculateService;
import com.creditPipeline.conveyor.service.OffersService;
import com.creditPipeline.conveyor.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
@Tag(name = "Конвеер")
public class Conveyor {

    private final ScoringService scoringService;
    private final CalculateService calculateService;
    private final OffersService offersService;
    private final static Logger logger = LogManager.getLogger(Conveyor.class);

    @Autowired
    public Conveyor(ScoringService scoringService, CalculateService calculateService, OffersService offersService) {
        this.scoringService = scoringService;
        this.calculateService = calculateService;
        this.offersService = offersService;
    }

    @PostMapping(value = "/calculation")
    @Operation(
            summary = "Расчет",
            description = "Рассчитывает кредит")
    public CreditDTO calculate(@RequestBody ScoringDataDTO scoringDataDTO) throws ScoringServiceException {

        CreditDTO credit = new CreditDTO();
        logger.debug("Credit" + credit);

        if (scoringService.isCreditAvailable(scoringDataDTO)) {
            credit = calculateService.getCredit(scoringDataDTO);
        } else {
            throw new ScoringServiceException("Клиент не прошел скоринг");
        }
        logger.debug("Для расчета кредита приходят входные данные: " + "имя: " + scoringDataDTO.getFirstName() +
                ", фамилия: " + scoringDataDTO.getLastName() + ", отчество: " + scoringDataDTO.getMiddleName() +
                ", дата рождения: " + scoringDataDTO.getBirthdate() + ", пол: " + scoringDataDTO.getGender() +
                ", данные о работе: " + scoringDataDTO.getEmploymentDTO());
        return credit;
    }

    @PostMapping(value = "/offers")
    @Operation(
            summary = "Создание предложений",
            description = "При прохождении прескоринга возвращает 4 предложения")
    public List<LoanOfferDTO> getOffers(@RequestBody @Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {

        logger.debug("Получение ответа на первоначальную заявку с данными: " + " сумма кредита - " + loanApplicationRequestDTO.getAmount() +
                ", срок - " + loanApplicationRequestDTO.getTerm() + ", ФИО - " + loanApplicationRequestDTO.getFirstName() + " " + loanApplicationRequestDTO.getMiddleName() + " " + loanApplicationRequestDTO.getLastName() +
                ", email - " + loanApplicationRequestDTO.getEmail() + ", дата рождения - " + loanApplicationRequestDTO.getBirthdate());

        final long age = LocalDate.from(loanApplicationRequestDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        if (age < 18) {
           throw new AgeException("Клиенту меньше чем 18 лет");
        }
        List<LoanOfferDTO> offers = offersService.getOffers(loanApplicationRequestDTO);

        logger.debug("Offers" + offers);
        return offers;
    }

}
