package com.creditPipeline.conveyor.service;

import com.creditPipeline.conveyor.dto.CreditDTO;
import com.creditPipeline.conveyor.dto.PaymentScheduleElement;
import com.creditPipeline.conveyor.dto.ScoringDataDTO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculateService {

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
        credit.setPsk(calculatePsk(scoringDataDTO, credit.getPaymentSchedule().get(0).getDate(), credit.getPaymentSchedule().get(credit.getPaymentSchedule().size() - 1).getDate()));
        credit.setInsuranceEnabled(scoringDataDTO.getInsuranceEnabled());
        credit.setSalaryClient(scoringDataDTO.getSalaryClient());
        credit.setPaymentSchedule(getPaymentScheduleElement(scoringDataDTO));

        return credit;

    }

    private List<PaymentScheduleElement> getPaymentScheduleElement(ScoringDataDTO scoringDataDTO) {

        List<PaymentScheduleElement> paymentSchedules = new ArrayList<>();

        BigDecimal firstInterestPayment = scoringDataDTO.getAmount().multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        BigDecimal firstDebtPayment = calculateMonthlyPayment(scoringDataDTO).subtract(firstInterestPayment);
        BigDecimal firstRemainingDebt = scoringDataDTO.getAmount().subtract(firstDebtPayment);

        PaymentScheduleElement firstPaymentScheduleElement = new PaymentScheduleElement();
        firstPaymentScheduleElement.setNumber(1);
        firstPaymentScheduleElement.setDate(LocalDate.now().plusMonths(1));
        firstPaymentScheduleElement.setTotalPayment(calculateMonthlyPayment(scoringDataDTO));
        firstPaymentScheduleElement.setInterestPayment(firstInterestPayment);
        firstPaymentScheduleElement.setDebtPayment(firstDebtPayment);
        firstPaymentScheduleElement.setRemainingDebt(firstRemainingDebt);
        BigDecimal amountOfCreditPayments = firstDebtPayment;

        for (int i = 2; i <= scoringDataDTO.getTerm(); i++) {
            PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement();

            BigDecimal remains = scoringDataDTO.getAmount().subtract(amountOfCreditPayments);
            BigDecimal interestPayment = remains.multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
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
        BigDecimal lastPayment = paymentSchedules.get(paymentSchedules.size() - 1).getRemainingDebt();

        PaymentScheduleElement lastPaymentScheduleElement = new PaymentScheduleElement();
        lastPaymentScheduleElement.setNumber(paymentSchedules.get(paymentSchedules.size() - 1).getNumber() + 1);
        lastPaymentScheduleElement.setDate(paymentSchedules.get(paymentSchedules.size() - 1).getDate());
        lastPaymentScheduleElement.setTotalPayment(lastPayment);
        lastPaymentScheduleElement.setDebtPayment(lastPayment);
        paymentSchedules.add(lastPaymentScheduleElement);

        logger.debug("paymentSchedules: " + paymentSchedules);
        return paymentSchedules;
    }

    private BigDecimal calculatePsk(ScoringDataDTO scoringDataDTO, LocalDate firstPaymentScheduleElement, LocalDate lastPaymentScheduleElement) {

        BigDecimal psk = calculateAmountOfCreditPayments(scoringDataDTO).divide(scoringDataDTO.getAmount(), 2, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1))
                .divide(BigDecimal.valueOf(LocalDate.from(firstPaymentScheduleElement).until(lastPaymentScheduleElement, ChronoUnit.YEARS)))
                .multiply(BigDecimal.valueOf(100));
        logger.debug("psk: " + psk);
        return psk;
    }

    private BigDecimal calculateAmountOfCreditPayments(ScoringDataDTO scoringDataDTO) {

        BigDecimal amountOfCreditPayments = calculateFirstDebtPayment(scoringDataDTO);

        for (int i = 2; i <= scoringDataDTO.getTerm(); i++) {
            BigDecimal remains = scoringDataDTO.getAmount().subtract(amountOfCreditPayments);
            BigDecimal interestPayment = remains.multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = calculateMonthlyPayment(scoringDataDTO).subtract(interestPayment);
            amountOfCreditPayments = amountOfCreditPayments.add(debtPayment);
        }
        return amountOfCreditPayments;
    }

    private BigDecimal calculateFirstDebtPayment(ScoringDataDTO scoringDataDTO) {
        BigDecimal firstInterestPayment = scoringDataDTO.getAmount().multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        BigDecimal firstDebtPayment = calculateMonthlyPayment(scoringDataDTO).subtract(firstInterestPayment);
        return firstDebtPayment;
    }

    private BigDecimal calculateMonthlyPayment(ScoringDataDTO scoringDataDTO) {
        final BigDecimal countMonthsInYear = new BigDecimal(12);
        final BigDecimal toSkaryal = new BigDecimal(100);

        BigDecimal monthlyInterestRate = scoringService.calculateRate(scoringDataDTO).divide(countMonthsInYear.divide(toSkaryal));

        BigDecimal annuityRate = monthlyInterestRate.multiply((BigDecimal.valueOf(1).add(monthlyInterestRate)).pow(scoringDataDTO.getTerm()))
                .divide((BigDecimal.valueOf(1).add(monthlyInterestRate)).pow(scoringDataDTO.getTerm()).subtract(BigDecimal.valueOf(1)), 6, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = scoringDataDTO.getAmount().multiply(annuityRate).setScale(3);
        return monthlyPayment;
    }

}
