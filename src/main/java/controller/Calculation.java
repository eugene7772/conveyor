package controller;

import DTO.CreditDTO;
import DTO.PaymentScheduleElement;
import DTO.ScoringDataDTO;
import enums.EmploymentStatus;
import enums.Gender;
import enums.MaritalStatus;
import enums.Position;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


@Controller
@Tag(name = "Калькулятор",description = "Данный контроллер рассчитывает кредит")
public class Calculation {

    private static Logger logger = Logger.getLogger(Calculation.class);

    @PostMapping("/conveyor/calculation")
    @Operation(
            summary = "Расчет",
            description = "Расчитывет кредит")
    public CreditDTO calculate(ScoringDataDTO scoringDataDTO) {

        logger.trace("calculate");

        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);
        LocalDate now = LocalDate.now();
        CreditDTO credit = new CreditDTO();
        List<PaymentScheduleElement> paymentSchedules = new ArrayList<>();

        if (scoringDataDTO.getEmploymentDTO().getEmploymentStatus().toString().equals(EmploymentStatus.Unemployed.toString()) ||
                scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmploymentDTO().getSalary().multiply(BigDecimal.valueOf(20))) >= 0 ||
                age < 20 ||
                age > 60 ||
                scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal() < 12 ||
                scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent() < 3) {
            return null;
        } else {
            logger.info("calculate");

            credit.setAmount(scoringDataDTO.getAmount());
            credit.setRate(calculateRate(scoringDataDTO));
            credit.setTerm(scoringDataDTO.getTerm());
            BigDecimal monthlyInterestRate = credit.getRate().divide(BigDecimal.valueOf(12).multiply(BigDecimal.valueOf(100)));


            final BigDecimal pow = (BigDecimal.valueOf(1).add(monthlyInterestRate)).pow(scoringDataDTO.getTerm());
            BigDecimal annuityRate = monthlyInterestRate.multiply(pow).divide(pow.subtract(BigDecimal.valueOf(1)), 6, RoundingMode.HALF_UP);
            credit.setMonthlyPayment(scoringDataDTO.getAmount().multiply(annuityRate).setScale(3));

            PaymentScheduleElement firstPaymentScheduleElement = new PaymentScheduleElement();

            BigDecimal firstInterestPayment = scoringDataDTO.getAmount().multiply(credit.getRate()).multiply(BigDecimal.valueOf(now.lengthOfMonth()))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
            BigDecimal firstDebtPayment = credit.getMonthlyPayment().subtract(firstInterestPayment);
            BigDecimal firstRemainingDebt = scoringDataDTO.getAmount().subtract(firstDebtPayment);

            firstPaymentScheduleElement.setNumber(1);
            firstPaymentScheduleElement.setDate(now.plusMonths(1));
            firstPaymentScheduleElement.setTotalPayment(credit.getMonthlyPayment());
            firstPaymentScheduleElement.setInterestPayment(firstInterestPayment);
            firstPaymentScheduleElement.setDebtPayment(firstDebtPayment);
            firstPaymentScheduleElement.setRemainingDebt(firstRemainingDebt);

            BigDecimal amountOfCreditPayments = firstDebtPayment;

            for (int i = 2; i <= scoringDataDTO.getTerm(); i++) {
                PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement();

                BigDecimal remains = scoringDataDTO.getAmount().subtract(amountOfCreditPayments);
                BigDecimal interestPayment = remains.multiply(credit.getRate()).multiply(BigDecimal.valueOf(now.lengthOfMonth()))
                        .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
                BigDecimal debtPayment = credit.getMonthlyPayment().subtract(interestPayment);
                amountOfCreditPayments = amountOfCreditPayments.add(debtPayment);
                BigDecimal lastRemains = remains.subtract(debtPayment);

                paymentScheduleElement.setNumber(i);
                paymentScheduleElement.setDate(now.plusMonths(i));
                paymentScheduleElement.setTotalPayment(credit.getMonthlyPayment());
                paymentScheduleElement.setInterestPayment(interestPayment);
                paymentScheduleElement.setDebtPayment(debtPayment);
                paymentScheduleElement.setRemainingDebt(lastRemains);

                paymentSchedules.add(paymentScheduleElement);
            }
            BigDecimal lastPayment = paymentSchedules.get(paymentSchedules.size() - 1).getRemainingDebt();

            PaymentScheduleElement lastPaymentScheduleElement = new PaymentScheduleElement();
            lastPaymentScheduleElement.setNumber(paymentSchedules.get(paymentSchedules.size() - 1).getNumber() + 1);
            lastPaymentScheduleElement.setDate(paymentSchedules.get(paymentSchedules.size() - 1).getDate());
            lastPaymentScheduleElement.setTotalPayment(lastPayment);
            lastPaymentScheduleElement.setDebtPayment(lastPayment);
            paymentSchedules.add(lastPaymentScheduleElement);

            BigDecimal psk = amountOfCreditPayments.divide(scoringDataDTO.getAmount(), 2, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1))
                    .divide(BigDecimal.valueOf(LocalDate.from(firstPaymentScheduleElement.getDate()).until(lastPaymentScheduleElement.getDate(), ChronoUnit.YEARS)))
                    .multiply(BigDecimal.valueOf(100));
            credit.setPaymentSchedule(paymentSchedules);
            credit.setPsk(psk);
            credit.setInsuranceEnabled(scoringDataDTO.getInsuranceEnabled());
            credit.setSalaryClient(scoringDataDTO.getSalaryClient());
        }
        logger.info("return credit");
        return credit;
    }

    @Operation(
            summary = "Расчет стваки",
            description = "Возвращает проецнтную ставку по заданным условия")
    public static BigDecimal calculateRate(ScoringDataDTO scoringDataDTO) {
        BigDecimal baseRate = new BigDecimal(11);
        final long age = LocalDate.from(scoringDataDTO.getBirthdate()).until(LocalDate.now(), ChronoUnit.YEARS);

        if (scoringDataDTO.getEmploymentDTO().getEmploymentStatus().toString().equals(EmploymentStatus.SelfEmployed.toString())) {
            baseRate = baseRate.add(BigDecimal.valueOf(1));
        }
        if (scoringDataDTO.getEmploymentDTO().getEmploymentStatus().toString().equals(EmploymentStatus.BusinessOwner.toString())) {
            baseRate = baseRate.add(BigDecimal.valueOf(3));
        }
        if (scoringDataDTO.getEmploymentDTO().getPosition().toString().equals(Position.MiddleManager.toString())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        }
        if (scoringDataDTO.getEmploymentDTO().getPosition().toString().equals(Position.TopManager.toString())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(4));
        }
        if (scoringDataDTO.getMaritalStatus().Married.toString().equals(MaritalStatus.Married.toString())) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(3));
        }
        if (scoringDataDTO.getMaritalStatus().toString().equals(MaritalStatus.Unmarried.toString())) {
            baseRate = baseRate.add(BigDecimal.valueOf(1));
        }
        if (scoringDataDTO.getGender().Female.toString().equals(Gender.Female.toString()) && (35 <= age && age <= 60) ||
                scoringDataDTO.getGender().Male.toString().equals(Gender.Male.toString()) && (30 <= age && age <= 55)) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(3));
        }
        if (scoringDataDTO.getGender().NotBinary.toString().equals(Gender.NotBinary.toString())) {
            baseRate = baseRate.add(BigDecimal.valueOf(3));
        }
        if (scoringDataDTO.getInsuranceEnabled()) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        }
        if (scoringDataDTO.getSalaryClient()) {
            baseRate = baseRate.subtract(BigDecimal.valueOf(1));
        }
        return baseRate;
    }

}
