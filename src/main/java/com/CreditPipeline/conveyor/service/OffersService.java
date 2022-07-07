package com.creditPipeline.conveyor.service;

import com.creditPipeline.conveyor.dto.LoanApplicationRequestDTO;
import com.creditPipeline.conveyor.dto.LoanOfferDTO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OffersService {

    private final ScoringService scoringService;
    private final static Logger logger = LogManager.getLogger(OffersService.class);

    @Autowired
    public OffersService(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> offers;

        offers = List.of(createOffer(0L, false, false, loanApplicationRequestDTO),
                createOffer(1L, false, true, loanApplicationRequestDTO),
                createOffer(2L, true, false, loanApplicationRequestDTO),
                createOffer(3L, true, true, loanApplicationRequestDTO));

        logger.debug("Предложения по кредиту: " + offers);

        return offers;
    }

    public LoanOfferDTO createOffer(Long id, Boolean insuranceEnabled, Boolean salaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO) {

        LoanOfferDTO offer = new LoanOfferDTO();

        offer.setApplicationId(id);
        offer.setInsuranceEnabled(insuranceEnabled);
        offer.setSalaryClient(salaryClient);
        offer.setRate(scoringService.calculateRateToOffer(insuranceEnabled, salaryClient));
        offer.setRequestedAmount(loanApplicationRequestDTO.getAmount());
        offer.setTerm(loanApplicationRequestDTO.getTerm());
        offer.setMonthlyPayment(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm()),2, RoundingMode.HALF_UP));
        offer.setTotalAmount(loanApplicationRequestDTO.getAmount());

        return offer;
    }
}
