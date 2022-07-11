package service;

import com.creditPipeline.conveyor.dto.EmploymentDTO;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.enums.EmploymentStatus;
import com.creditPipeline.conveyor.enums.Gender;
import com.creditPipeline.conveyor.enums.MaritalStatus;
import com.creditPipeline.conveyor.enums.Position;
import com.creditPipeline.conveyor.service.ScoringService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ScoringServiceTest {

    @InjectMocks
    private ScoringService scoringService;

    @Test
    public void testCalculateRate(){

        EmploymentDTO employmentDTO = new EmploymentDTO();

        employmentDTO.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        employmentDTO.setPosition(Position.MIDDLE_MANAGER);
        employmentDTO.setWorkExperienceCurrent(15);
        employmentDTO.setWorkExperienceTotal(18);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        scoringDataDTO.setGender(Gender.MALE);
        scoringDataDTO.setBirthdate(LocalDate.of(2000,01,01));
        scoringDataDTO.setMaritalStatus(MaritalStatus.MARRIED);
        scoringDataDTO.setEmploymentDTO(employmentDTO);
        scoringDataDTO.setInsuranceEnabled(true);
        scoringDataDTO.setSalaryClient(true);
        BigDecimal baseRate = scoringService.calculateRate(scoringDataDTO);
        Assertions.assertEquals(BigDecimal.valueOf(5),baseRate);
    }

    @Test
    public void testCalculateRateToOffer(){
        BigDecimal baseRate = scoringService.calculateRateToOffer(false,false);
        Assertions.assertEquals(BigDecimal.valueOf(15.5),baseRate);
    }

    @Test
    public void testCalculateAge(){
        LocalDate birthDate = LocalDate.of(2000,01,01);
        Assertions.assertEquals(22L,scoringService.calculateAge(birthDate));
    }

}
