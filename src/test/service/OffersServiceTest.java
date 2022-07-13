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
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OffersServiceTest {

    @InjectMocks
    private OffersService offersService;

    @Mock
    private CalculateService calculateService;

    @Mock
    private ScoringService scoringService;

    @Test
    public void testGetOffers() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();

        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(500000));
        loanApplicationRequestDTO.setTerm(18);

        List<LoanOfferDTO> offers = offersService.getOffers(loanApplicationRequestDTO);
        Assertions.assertEquals(BigDecimal.valueOf(27777.78), offers.get(0).getMonthlyPayment());
    }

    @Test
    public void testCreateOffer() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();

        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(500000));
        loanApplicationRequestDTO.setTerm(18);

        when(scoringService.calculateRateToOffer(false,false)).thenReturn(BigDecimal.valueOf(15.5));
        when(calculateService.calculateMonthlyPaymentForOffer(false,false, loanApplicationRequestDTO)).thenReturn(BigDecimal.valueOf(27777.78));
        LoanOfferDTO offer = offersService.createOffer(0L, false, false, loanApplicationRequestDTO);
        Assertions.assertEquals(BigDecimal.valueOf(27777.78), offer.getMonthlyPayment());
    }

}
