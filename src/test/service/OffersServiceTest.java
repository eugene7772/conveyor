package service;

import com.creditPipeline.conveyor.dto.LoanApplicationRequestDTO;
import com.creditPipeline.conveyor.dto.LoanOfferDTO;
import com.creditPipeline.conveyor.service.CalculateService;
import com.creditPipeline.conveyor.service.OffersService;
import static org.mockito.Mockito.when;

import com.creditPipeline.conveyor.service.ScoringService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OffersServiceTest {

    @InjectMocks
    private OffersService offersService;

    @Mock
    private ScoringService scoringService;

    @Test
    public void testGetOffers() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();

        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(500000));
        loanApplicationRequestDTO.setTerm(18);

        when(scoringService.calculateRateToOffer(false,false)).thenReturn(BigDecimal.valueOf(15.5));
        when(scoringService.calculateRateToOffer(false,true)).thenReturn(BigDecimal.valueOf(12.5));
        when(scoringService.calculateRateToOffer(true,false)).thenReturn(BigDecimal.valueOf(11.5));
        when(scoringService.calculateRateToOffer(true,true)).thenReturn(BigDecimal.valueOf(9.5));

        List<LoanOfferDTO> offers = offersService.getOffers(loanApplicationRequestDTO);
        Assertions.assertEquals(BigDecimal.valueOf(31310).setScale(3), offers.get(0).getMonthlyPayment());
        Assertions.assertEquals(BigDecimal.valueOf(30607.500).setScale(3), offers.get(1).getMonthlyPayment());
        Assertions.assertEquals(BigDecimal.valueOf(30375).setScale(3), offers.get(2).getMonthlyPayment());
        Assertions.assertEquals(BigDecimal.valueOf(29913.500).setScale(3), offers.get(3).getMonthlyPayment());
    }

    @Test
    public void testCreateOffer() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();

        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(500000));
        loanApplicationRequestDTO.setTerm(18);

        when(scoringService.calculateRateToOffer(false,false)).thenReturn(BigDecimal.valueOf(15.5));
        LoanOfferDTO offer = offersService.createOffer(0L, false, false, loanApplicationRequestDTO);
        Assertions.assertEquals(BigDecimal.valueOf(31310), offer.getMonthlyPayment().setScale(0));
    }

}
