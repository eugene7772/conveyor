package controller;


import com.creditPipeline.conveyor.dto.CreditDTO;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.enums.Gender;
import com.creditPipeline.conveyor.enums.MaritalStatus;
import com.creditPipeline.conveyor.service.ScoringService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;


public class CalculationTest {

    private CreditDTO creditDTO;



    @Test
    public void testCalculate(){

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAmount(BigDecimal.valueOf(500000));
        scoringDataDTO.setTerm(12);
        scoringDataDTO.setGender(Gender.NOT_BINARY);
        scoringDataDTO.setMaritalStatus(MaritalStatus.MARRIED);
        scoringDataDTO.setBirthdate(LocalDate.of(2000,01,17));

        ScoringService scoringService =  Mockito.mock(ScoringService.class);

        Mockito.when(scoringService.scoringData(scoringDataDTO)).thenReturn(true);
        Mockito.when(scoringService.calculateRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(18));
        //creditDTO = calculation.calculate(scoringDataDTO);
       // Assertions.assertEquals(BigDecimal.valueOf(45840),creditDTO.getMonthlyPayment());
    }

}
