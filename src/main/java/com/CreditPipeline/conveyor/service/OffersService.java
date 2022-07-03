package com.creditPipeline.conveyor.service;

import com.creditPipeline.conveyor.dto.LoanApplicationRequestDTO;
import com.creditPipeline.conveyor.dto.LoanOfferDTO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OffersService {

    private final ScoringService scoringService;

    @Autowired
    public OffersService(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    private static Logger logger = LogManager.getLogger(OffersService.class);

    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) throws IOException {
        List<LoanOfferDTO> offers;

        LoanOfferDTO offer1 = createOffer(0L, false, false, loanApplicationRequestDTO);

        LoanOfferDTO offer2 = createOffer(1L, false, true, loanApplicationRequestDTO);

        LoanOfferDTO offer3 = createOffer(2L, true, false, loanApplicationRequestDTO);

        LoanOfferDTO offer4 = createOffer(3L, true, true, loanApplicationRequestDTO);

        offers = List.of(offer1, offer2, offer3, offer4);
        logger.debug("offers: " + offers);
        return offers;
    }

    public LoanOfferDTO createOffer(Long id, Boolean insuranceEnabled, Boolean salaryClient, LoanApplicationRequestDTO loanApplicationRequestDTO) throws IOException {

        LoanOfferDTO offer = new LoanOfferDTO();

        offer.setApplicationId(id);
        offer.setInsuranceEnabled(insuranceEnabled);
        offer.setSalaryClient(salaryClient);
        offer.setRate(scoringService.calculateRateToOffer(insuranceEnabled, salaryClient));
        offer.setRequestedAmount(loanApplicationRequestDTO.getAmount());
        offer.setTerm(loanApplicationRequestDTO.getTerm());
        offer.setMonthlyPayment(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())));
        offer.setTotalAmount(loanApplicationRequestDTO.getAmount());

        logger.debug("offer: " + offer);
        return offer;
    }
}
