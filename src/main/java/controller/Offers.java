package controller;

import DTO.LoanApplicationRequestDTO;
import DTO.LoanOfferDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Offers {

    @PostMapping("/conveyor/offers")
    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> offers = new ArrayList<>();
        return offers;
    }

}
