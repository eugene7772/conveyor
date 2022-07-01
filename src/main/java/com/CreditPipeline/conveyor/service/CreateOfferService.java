package com.CreditPipeline.conveyor.service;

import com.CreditPipeline.conveyor.DTO.LoanApplicationRequestDTO;
import com.CreditPipeline.conveyor.DTO.LoanOfferDTO;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

@Service
public class CreateOfferService {

 public LoanOfferDTO createOffer(Long id, Boolean insuranceEnabled, Boolean salaryClient,  LoanApplicationRequestDTO loanApplicationRequestDTO) throws IOException {

     LoanOfferDTO offer = new LoanOfferDTO();

     offer.setApplicationId(id);
     offer.setInsuranceEnabled(insuranceEnabled);
     offer.setSalaryClient(salaryClient);
     offer.setRate(calculateRate(insuranceEnabled,salaryClient));
     offer.setRequestedAmount(loanApplicationRequestDTO.getAmount());
     offer.setTerm(loanApplicationRequestDTO.getTerm());
     offer.setMonthlyPayment(loanApplicationRequestDTO.getAmount().divide(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())));
     offer.setTotalAmount(loanApplicationRequestDTO.getAmount());

     return offer;

 }
 private BigDecimal calculateRate( Boolean insuranceEnabled, Boolean salaryClient) throws IOException {

     Properties prop = new Properties();
     String path = "C:\\Users\\79518\\Desktop\\УЧЕБА\\JavaAll\\CreditPipeline\\conveyor\\src\\main\\resources\\application.properties";
     FileInputStream in =  new FileInputStream(path);
     prop.load(in);
     BigDecimal rate =  new BigDecimal(prop.getProperty("baseRate"));

     if (insuranceEnabled) {
         rate = rate.subtract(BigDecimal.valueOf(2));
     } else {
         rate = rate.add(BigDecimal.valueOf(2));
     }
     if (salaryClient) {
         rate = rate.subtract(BigDecimal.valueOf(1.5));
     } else {
         rate = rate.add(BigDecimal.valueOf(1.5));
     }
     return rate;
 }

}
