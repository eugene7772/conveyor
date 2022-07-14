package controller;

import com.creditPipeline.conveyor.controller.Conveyor;
import com.creditPipeline.conveyor.dto.LoanApplicationRequestDTO;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.exception.AgeException;
import com.creditPipeline.conveyor.exception.ScoringServiceException;
import com.creditPipeline.conveyor.service.CalculateService;
import com.creditPipeline.conveyor.service.ScoringService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConveyorTest {

    @Mock
    private ScoringService scoringService;

    @Mock
    private CalculateService calculateService;

    @InjectMocks
    private Conveyor conveyor;

    @Test
    public void testCalculate() {

        when(scoringService.isCreditAvailable(any())).thenReturn(Boolean.FALSE);
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        ScoringServiceException scoringServiceException = Assertions.assertThrows(ScoringServiceException.class, () -> {
            conveyor.calculate(scoringDataDTO);
        });
        Assertions.assertEquals("Клиент не прошел скоринг", scoringServiceException.getMessage());
    }
    @Test
    public void testCalculateCredit() {

        when(scoringService.isCreditAvailable(any())).thenReturn(Boolean.TRUE);
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        conveyor.calculate(scoringDataDTO);
        Mockito.verify(calculateService).getCredit(scoringDataDTO);

    }

    @Test
    public void testGetOffers() {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        when(scoringService.calculateAge(loanApplicationRequestDTO.getBirthdate())).thenReturn(17l);
        AgeException ageException = Assertions.assertThrows(AgeException.class, () -> {
            conveyor.getOffers(loanApplicationRequestDTO);
        });
        Assertions.assertEquals("Клиенту меньше чем 18 лет", ageException.getMessage());

    }
}
