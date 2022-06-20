package com.CreditPipeline.conveyor.service;

import com.CreditPipeline.conveyor.DTO.LoanApplicationRequestDTO;
import com.CreditPipeline.conveyor.DTO.LoanOfferDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreateOfferService {

 public LoanOfferDTO createOffer(Long id, Boolean insuranceEnabled, Boolean salaryClient,  LoanApplicationRequestDTO loanApplicationRequestDTO){

     final BigDecimal baseRate = new BigDecimal(11);

     BigDecimal rate = baseRate;

     LoanOfferDTO offer = new LoanOfferDTO();

     offer.setApplicationId(id);
     offer.setInsuranceEnabled(insuranceEnabled);
     offer.setSalaryClient(salaryClient);

     if(insuranceEnabled){
         rate = rate.subtract(BigDecimal.valueOf(2));
     }else{
         rate = rate.add(BigDecimal.valueOf(2));
     }
     if(salaryClient){
         rate = rate.subtract(BigDecimal.valueOf(1.5));
     }else {
         rate = rate.add(BigDecimal.valueOf(1.5));
     }

     offer.setRate(rate);
     offer.setRequestedAmount(loanApplicationRequestDTO.getAmount());
     offer.setTerm(loanApplicationRequestDTO.getTerm());
     offer.setMonthlyPayment(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())));
     offer.setTotalAmount(loanApplicationRequestDTO.getAmount());

     return offer;

 }

}
