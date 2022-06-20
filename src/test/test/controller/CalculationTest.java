package controller;


import com.CreditPipeline.conveyor.DTO.CreditDTO;
import com.CreditPipeline.conveyor.DTO.ScoringDataDTO;
import com.CreditPipeline.conveyor.controller.Calculation;
import com.CreditPipeline.conveyor.enums.Gender;
import com.CreditPipeline.conveyor.enums.MaritalStatus;
import com.CreditPipeline.conveyor.service.ScoringService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;


public class CalculationTest {

    private CreditDTO creditDTO;

    private Calculation calculation = new Calculation();


    @Test
    public void testCalculate(){

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAmount(BigDecimal.valueOf(500000));
        scoringDataDTO.setTerm(12);
        scoringDataDTO.setGender(Gender.NotBinary);
        scoringDataDTO.setMaritalStatus(MaritalStatus.Married);
        scoringDataDTO.setBirthdate(LocalDate.of(2000,01,17));

        ScoringService scoringService =  Mockito.mock(ScoringService.class);

        Mockito.when(scoringService.scoringData(scoringDataDTO)).thenReturn(true);
        Mockito.when(scoringService.calculateRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(18));
        creditDTO = calculation.calculate(scoringDataDTO);
        Assertions.assertEquals(BigDecimal.valueOf(45840),creditDTO.getMonthlyPayment());
    }

}
