package com.creditPipeline.conveyor.service;

import com.creditPipeline.conveyor.dto.LoanApplicationRequestDTO;
import com.creditPipeline.conveyor.dto.LoanOfferDTO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OffersService {

    private final ScoringService scoringService;
    private final CalculateService calculateService;
    private final static Logger logger = LogManager.getLogger(OffersService.class);

    @Autowired
    public OffersService(ScoringService scoringService, CalculateService calculateService) {
        this.scoringService = scoringService;
        this.calculateService = calculateService;
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
        offer.setMonthlyPayment(calculateService.calculateMonthlyPaymentForOffer(insuranceEnabled,salaryClient,loanApplicationRequestDTO));
        offer.setTotalAmount(offer.getMonthlyPayment().multiply(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())));

        return offer;
    }
}
