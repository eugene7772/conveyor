package controller;

import DTO.CreditDTO;
import DTO.ScoringDataDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Calculation {

    @PostMapping("/conveyor/calculation")
    public CreditDTO calculate (ScoringDataDTO scoringDataDTO){
        return new CreditDTO();
    }

}
