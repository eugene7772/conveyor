package controller;

import com.creditPipeline.conveyor.controller.Conveyor;
import com.creditPipeline.conveyor.dto.CreditDTO;
import com.creditPipeline.conveyor.dto.LoanApplicationRequestDTO;
import com.creditPipeline.conveyor.dto.LoanOfferDTO;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.exception.AgeException;
import com.creditPipeline.conveyor.exception.ScoringServiceException;
import com.creditPipeline.conveyor.service.ScoringService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConveyorTest {

    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private Conveyor conveyor;

    @Test
    public void testCalculate() {

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        ScoringServiceException scoringServiceException = Assertions.assertThrows(ScoringServiceException.class, () -> {
            when(conveyor.calculate(scoringDataDTO)).thenReturn(new CreditDTO());
            when(scoringService.isCreditAvailable(any())).thenReturn(Boolean.FALSE);
        });
        Assertions.assertEquals("Клиент не прошел скоринг", scoringServiceException.getMessage());
    }

    @Test
    public void testGetOffers() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        AgeException ageException = Assertions.assertThrows(AgeException.class, () -> {
            when(conveyor.getOffers(loanApplicationRequestDTO)).thenReturn(new ArrayList<LoanOfferDTO>());
            when(scoringService.calculateAge(loanApplicationRequestDTO.getBirthdate()));
        });
        Assertions.assertEquals("Клиенту меньше чем 18 лет", ageException.getMessage());

    }
}
