package com.CreditPipeline.conveyor.controller;

import com.CreditPipeline.conveyor.DTO.CreditDTO;
import com.CreditPipeline.conveyor.DTO.PaymentScheduleElement;
import com.CreditPipeline.conveyor.DTO.ScoringDataDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;
import com.CreditPipeline.conveyor.service.ScoringService;


@RestController
@Tag(name = "Калькулятор",description = "Данный контроллер рассчитывает кредит")
public class Calculation {


    @Autowired
    private ScoringService scoringService;

    private static Logger logger = Logger.getLogger(Calculation.class);

    @PostMapping("/conveyor/calculation")
    @Operation(
            summary = "Расчет",
            description = "Рассчитывает кредит")
    public CreditDTO calculate(ScoringDataDTO scoringDataDTO) {

        LocalDate now = LocalDate.now();
        CreditDTO credit = new CreditDTO();
        List<PaymentScheduleElement> paymentSchedules = new ArrayList<>();

        if ( !scoringService.scoringData(scoringDataDTO) ) {
            return null;
        } else {

            credit.setAmount(scoringDataDTO.getAmount());
            credit.setRate(scoringService.calculateRate(scoringDataDTO));
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
}
