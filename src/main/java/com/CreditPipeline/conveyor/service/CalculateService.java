package com.creditPipeline.conveyor.service;

import com.creditPipeline.conveyor.dto.CreditDTO;
import com.creditPipeline.conveyor.dto.PaymentScheduleElement;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculateService {

    @Value("${baseRate}")
    private BigDecimal baseRate;

    private final ScoringService scoringService;
    private final static Logger logger = LogManager.getLogger(CalculateService.class);

    @Autowired
    public CalculateService(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    public CreditDTO getCredit(ScoringDataDTO scoringDataDTO) {

        CreditDTO credit = new CreditDTO();

        credit.setMonthlyPayment(calculateMonthlyPayment(scoringDataDTO));
        credit.setAmount(scoringDataDTO.getAmount());
        credit.setRate(scoringService.calculateRate(scoringDataDTO));
        credit.setTerm(scoringDataDTO.getTerm());
        credit.setPsk(calculatePsk(scoringDataDTO, scoringDataDTO.getTerm()));
        credit.setInsuranceEnabled(scoringDataDTO.getInsuranceEnabled());
        credit.setSalaryClient(scoringDataDTO.getSalaryClient());
        credit.setPaymentSchedule(getPaymentScheduleElement(scoringDataDTO));

        return credit;

    }

    private List<PaymentScheduleElement> getPaymentScheduleElement(ScoringDataDTO scoringDataDTO) {

        List<PaymentScheduleElement> paymentSchedules = new ArrayList<>();

        BigDecimal firstInterestPayment = scoringDataDTO.getAmount().multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal firstDebtPayment = calculateMonthlyPayment(scoringDataDTO).subtract(firstInterestPayment);
        BigDecimal amountOfCreditPayments = firstDebtPayment;

        PaymentScheduleElement firstPaymentScheduleElement = new PaymentScheduleElement();

        firstPaymentScheduleElement.setNumber(1);
        firstPaymentScheduleElement.setDate(LocalDate.now().plusMonths(1));
        firstPaymentScheduleElement.setTotalPayment(calculateMonthlyPayment(scoringDataDTO));
        firstPaymentScheduleElement.setInterestPayment(firstInterestPayment);
        firstPaymentScheduleElement.setDebtPayment(firstDebtPayment);
        firstPaymentScheduleElement.setRemainingDebt(scoringDataDTO.getAmount().subtract(amountOfCreditPayments));

        paymentSchedules.add(firstPaymentScheduleElement);

        for (int i = 2; i <= scoringDataDTO.getTerm(); i++) {
            PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement();

            BigDecimal remains = scoringDataDTO.getAmount().subtract(amountOfCreditPayments);
            BigDecimal interestPayment = remains.multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = calculateMonthlyPayment(scoringDataDTO).subtract(interestPayment);
            amountOfCreditPayments = amountOfCreditPayments.add(debtPayment);
            BigDecimal lastRemains = remains.subtract(debtPayment);

                paymentScheduleElement.setNumber(i);
                paymentScheduleElement.setDate(LocalDate.now().plusMonths(i));
                paymentScheduleElement.setTotalPayment(calculateMonthlyPayment(scoringDataDTO));
                paymentScheduleElement.setInterestPayment(interestPayment);
                paymentScheduleElement.setDebtPayment(debtPayment);
                paymentScheduleElement.setRemainingDebt(lastRemains);

            paymentSchedules.add(paymentScheduleElement);

        }

        logger.debug("paymentSchedules: " + paymentSchedules);
        return paymentSchedules;
    }

    private BigDecimal calculatePsk(ScoringDataDTO scoringDataDTO, Integer term) {

        BigDecimal psk = calculateAmountOfCreditPayments(scoringDataDTO).divide(scoringDataDTO.getAmount(), 2, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1))
                .divide(BigDecimal.valueOf(LocalDate.from(LocalDate.now()).until(LocalDate.now().plusMonths(term), ChronoUnit.YEARS)))
                .multiply(BigDecimal.valueOf(100));
        logger.debug("psk: " + psk);
        return psk;
    }

    private BigDecimal calculateAmountOfCreditPayments(ScoringDataDTO scoringDataDTO) {

        BigDecimal amountOfCreditPayments = calculateMonthlyPayment(scoringDataDTO);

        for (int i = 2; i <= scoringDataDTO.getTerm(); i++) {
            amountOfCreditPayments = amountOfCreditPayments.add(calculateMonthlyPayment(scoringDataDTO));
        }
        return amountOfCreditPayments;
    }

    private BigDecimal calculateFirstDebtPayment(ScoringDataDTO scoringDataDTO) {
        BigDecimal firstInterestPayment = scoringDataDTO.getAmount().multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal firstDebtPayment = calculateMonthlyPayment(scoringDataDTO).subtract(firstInterestPayment);
        return firstDebtPayment;
    }

    private BigDecimal calculateMonthlyPayment(ScoringDataDTO scoringDataDTO) {
        final BigDecimal countMonthsInYear = new BigDecimal(12);
        final BigDecimal toSkaryal = new BigDecimal(100);

        BigDecimal monthlyInterestRate = scoringService.calculateRate(scoringDataDTO).divide(countMonthsInYear).divide(toSkaryal);

        BigDecimal annuityRate = monthlyInterestRate.multiply((BigDecimal.valueOf(1).add(monthlyInterestRate)).pow(scoringDataDTO.getTerm()))
                .divide((BigDecimal.valueOf(1).add(monthlyInterestRate)).pow(scoringDataDTO.getTerm()).subtract(BigDecimal.valueOf(1)), 6, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = scoringDataDTO.getAmount().multiply(annuityRate).setScale(3);
        return monthlyPayment;
    }

}
