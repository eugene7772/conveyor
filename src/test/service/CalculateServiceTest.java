package service;

import com.creditPipeline.conveyor.dto.CreditDTO;
import com.creditPipeline.conveyor.dto.EmploymentDTO;
import com.creditPipeline.conveyor.dto.PaymentScheduleElement;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import com.creditPipeline.conveyor.enums.EmploymentStatus;
import com.creditPipeline.conveyor.enums.Gender;
import com.creditPipeline.conveyor.enums.MaritalStatus;
import com.creditPipeline.conveyor.enums.Position;
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
import java.time.LocalDate;
import java.util.ArrayList;
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

        EmploymentDTO employmentDTO = new EmploymentDTO();

        employmentDTO.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        employmentDTO.setEmployerINN("2134124124");
        employmentDTO.setPosition(Position.MIDDLE_MANAGER);
        employmentDTO.setSalary(BigDecimal.valueOf(2131231));
        employmentDTO.setWorkExperienceCurrent(15);
        employmentDTO.setWorkExperienceTotal(18);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        scoringDataDTO.setAmount(BigDecimal.valueOf(10000));
        scoringDataDTO.setTerm(6);
        scoringDataDTO.setFirstName("Ivan");
        scoringDataDTO.setMiddleName("Ivanovich");
        scoringDataDTO.setLastName("Ivanov");
        scoringDataDTO.setGender(Gender.MALE);
        scoringDataDTO.setBirthdate(LocalDate.of(2000,01,01));
        scoringDataDTO.setPassportIssueDate(LocalDate.of(2014,01,01));
        scoringDataDTO.setPassportSeries("23124");
        scoringDataDTO.setPassportNumber("2w321");
        scoringDataDTO.setMaritalStatus(MaritalStatus.MARRIED);
        scoringDataDTO.setDependentAmount(123123);
        scoringDataDTO.setEmploymentDTO(employmentDTO);
        scoringDataDTO.setAccount("213123");
        scoringDataDTO.setInsuranceEnabled(true);
        scoringDataDTO.setSalaryClient(true);

        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();

        PaymentScheduleElement paymentScheduleElement1 = new PaymentScheduleElement();
        paymentScheduleElement1.setNumber(1);
        paymentScheduleElement1.setDate(LocalDate.of(2022,8,14));
        paymentScheduleElement1.setTotalPayment(BigDecimal.valueOf(1696).setScale(3));
        paymentScheduleElement1.setInterestPayment(BigDecimal.valueOf(50.9589));
        paymentScheduleElement1.setDebtPayment(BigDecimal.valueOf(1645.0411));
        paymentScheduleElement1.setRemainingDebt(BigDecimal.valueOf(8354.9589));
        paymentScheduleElements.add(paymentScheduleElement1);

        PaymentScheduleElement paymentScheduleElement2 = new PaymentScheduleElement();
        paymentScheduleElement2.setNumber(2);
        paymentScheduleElement2.setDate(LocalDate.of(2022,9,14));
        paymentScheduleElement2.setTotalPayment(BigDecimal.valueOf(1696).setScale(3));
        paymentScheduleElement2.setInterestPayment(BigDecimal.valueOf(41.20254));
        paymentScheduleElement2.setDebtPayment(BigDecimal.valueOf(1654.79746));
        paymentScheduleElement2.setRemainingDebt(BigDecimal.valueOf(6700.16144));
        paymentScheduleElements.add(paymentScheduleElement2);

        PaymentScheduleElement paymentScheduleElement3 = new PaymentScheduleElement();
        paymentScheduleElement3.setNumber(3);
        paymentScheduleElement3.setDate(LocalDate.of(2022,10,14));
        paymentScheduleElement3.setTotalPayment(BigDecimal.valueOf(1696).setScale(3));
        paymentScheduleElement3.setInterestPayment(BigDecimal.valueOf(34.14329));
        paymentScheduleElement3.setDebtPayment(BigDecimal.valueOf(1661.85671));
        paymentScheduleElement3.setRemainingDebt(BigDecimal.valueOf(5038.30473));
        paymentScheduleElements.add(paymentScheduleElement3);

        PaymentScheduleElement paymentScheduleElement4 = new PaymentScheduleElement();
        paymentScheduleElement4.setNumber(4);
        paymentScheduleElement4.setDate(LocalDate.of(2022,11,14));
        paymentScheduleElement4.setTotalPayment(BigDecimal.valueOf(1696).setScale(3));
        paymentScheduleElement4.setInterestPayment(BigDecimal.valueOf(24.84643));
        paymentScheduleElement4.setDebtPayment(BigDecimal.valueOf(1671.15357));
        paymentScheduleElement4.setRemainingDebt(BigDecimal.valueOf(3367.15116));
        paymentScheduleElements.add(paymentScheduleElement4);

        PaymentScheduleElement paymentScheduleElement5 = new PaymentScheduleElement();
        paymentScheduleElement5.setNumber(5);
        paymentScheduleElement5.setDate(LocalDate.of(2022,12,14));
        paymentScheduleElement5.setTotalPayment(BigDecimal.valueOf(1696).setScale(3));
        paymentScheduleElement5.setInterestPayment(BigDecimal.valueOf(17.15863));
        paymentScheduleElement5.setDebtPayment(BigDecimal.valueOf(1678.84137));
        paymentScheduleElement5.setRemainingDebt(BigDecimal.valueOf(1688.30979));
        paymentScheduleElements.add(paymentScheduleElement5);

        PaymentScheduleElement paymentScheduleElement6 = new PaymentScheduleElement();
        paymentScheduleElement6.setNumber(6);
        paymentScheduleElement6.setDate(LocalDate.of(2023,1,14));
        paymentScheduleElement6.setTotalPayment(BigDecimal.valueOf(1696).setScale(3));
        paymentScheduleElement6.setInterestPayment(BigDecimal.valueOf(8.60344));
        paymentScheduleElement6.setDebtPayment(BigDecimal.valueOf(1687.39656));
        paymentScheduleElement6.setRemainingDebt(BigDecimal.valueOf(0.91323));
        paymentScheduleElements.add(paymentScheduleElement6);

        when(scoringService.calculateRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(6));
        CreditDTO creditDTO = calculateService.getCredit(scoringDataDTO);
        Assertions.assertEquals(BigDecimal.valueOf(1696), creditDTO.getMonthlyPayment().setScale(0));
        Assertions.assertEquals(BigDecimal.valueOf(4),creditDTO.getPsk().setScale(0));
        Assertions.assertEquals(paymentScheduleElements,creditDTO.getPaymentSchedule());
    }

}
