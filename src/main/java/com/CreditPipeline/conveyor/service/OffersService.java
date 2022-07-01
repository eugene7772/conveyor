package com.CreditPipeline.conveyor.service;

import com.CreditPipeline.conveyor.DTO.LoanApplicationRequestDTO;
import com.CreditPipeline.conveyor.DTO.LoanOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OffersService {

    @Autowired
    private CreateOfferService createOfferService;

    public List<LoanOfferDTO> getOffers (LoanApplicationRequestDTO loanApplicationRequestDTO) throws IOException {
        List<LoanOfferDTO> offers = new ArrayList<>();

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
        return offers;
    }

}
