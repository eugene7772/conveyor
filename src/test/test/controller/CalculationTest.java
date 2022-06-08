package controller;

import DTO.CreditDTO;
import DTO.PaymentScheduleElement;
import DTO.ScoringDataDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CalculationTest {

    @Autowired
    private Calculation calculation;
    @MockBean
    private CreditDTO creditDTO;
    @MockBean
    private ScoringDataDTO scoringDataDTO;
    @MockBean
    private PaymentScheduleElement paymentScheduleElement;

    @Test
    public void contextLoads() throws Exception {
        assertThat(calculation);
    }
    @Test
    public void testCalculate() throws Exception {
        assertThat(creditDTO).isNotNull();
    }

}