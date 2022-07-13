package service;

import com.creditPipeline.conveyor.dto.CreditDTO;
import com.creditPipeline.conveyor.dto.PaymentScheduleElement;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.service.CalculateService;
import com.creditPipeline.conveyor.service.ScoringService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.mockito.Mockito.when;

import org.mockito.quality.Strictness;

import java.math.BigDecimal;;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CalculateServiceTest {

    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private CalculateService calculateService;


    @Test
    public void testGetCredit() {

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAmount(BigDecimal.valueOf(500000));
        scoringDataDTO.setTerm(18);

        when(scoringService.calculateRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(6));
        CreditDTO creditDTO = calculateService.getCredit(scoringDataDTO);
        Assertions.assertEquals(BigDecimal.valueOf(29100), creditDTO.getMonthlyPayment().setScale(0));
    }

    @Test
    public void testGetPaymentScheduleElement() {

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAmount(BigDecimal.valueOf(500000));
        scoringDataDTO.setTerm(18);

        when(scoringService.calculateRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(6));
        List<PaymentScheduleElement> paymentSchedules = calculateService.getPaymentScheduleElement(scoringDataDTO);
    }

    @Test
    public void testCalculatePsk() {

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAmount(BigDecimal.valueOf(500000));
        scoringDataDTO.setTerm(18);

        when(scoringService.calculateRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(6));
        BigDecimal psk = calculateService.calculatePsk(scoringDataDTO, 18);
        Assertions.assertEquals(BigDecimal.valueOf(3), psk.setScale(0));
    }

    @Test
    public void testCalculateAmountOfCreditPayments() {

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAmount(BigDecimal.valueOf(500000));
        scoringDataDTO.setTerm(18);

        when(scoringService.calculateRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(6));
        BigDecimal amountOfCreditPayments = calculateService.calculateAmountOfCreditPayments(scoringDataDTO);
        Assertions.assertEquals(BigDecimal.valueOf(523800), amountOfCreditPayments.setScale(0));
    }

    @Test
    public void testCalculateMonthlyPayment() {

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        scoringDataDTO.setAmount(BigDecimal.valueOf(500000));
        scoringDataDTO.setTerm(18);

        when(scoringService.calculateRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(6));
        BigDecimal monthlyPayment = calculateService.calculateMonthlyPayment(scoringDataDTO);
        Assertions.assertEquals(BigDecimal.valueOf(29100), monthlyPayment.setScale(0));
    }

}
