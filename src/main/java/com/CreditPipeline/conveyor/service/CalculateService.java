package com.CreditPipeline.conveyor.service;

import com.CreditPipeline.conveyor.DTO.CreditDTO;
import com.CreditPipeline.conveyor.DTO.PaymentScheduleElement;
import com.CreditPipeline.conveyor.DTO.ScoringDataDTO;
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

    @Autowired
    private ScoringService scoringService;

    private BigDecimal amountOfCreditPayments;
    private PaymentScheduleElement firstPaymentScheduleElement = new PaymentScheduleElement();
    private PaymentScheduleElement lastPaymentScheduleElement = new PaymentScheduleElement();

    public CreditDTO getCredit(ScoringDataDTO scoringDataDTO){

        CreditDTO credit = new CreditDTO();

        BigDecimal monthlyInterestRate = scoringService.calculateRate(scoringDataDTO).divide(BigDecimal.valueOf(12).multiply(BigDecimal.valueOf(100)));

        BigDecimal annuityRate = monthlyInterestRate.multiply((BigDecimal.valueOf(1).add(monthlyInterestRate)).pow(scoringDataDTO.getTerm()))
                .divide((BigDecimal.valueOf(1).add(monthlyInterestRate)).pow(scoringDataDTO.getTerm()).subtract(BigDecimal.valueOf(1)), 6, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = scoringDataDTO.getAmount().multiply(annuityRate).setScale(3);

        credit.setPaymentSchedule(getPaymentScheduleElement(scoringDataDTO,monthlyPayment));
        credit.setMonthlyPayment(monthlyPayment);
        credit.setAmount(scoringDataDTO.getAmount());
        credit.setRate(scoringService.calculateRate(scoringDataDTO));
        credit.setTerm(scoringDataDTO.getTerm());
        credit.setPsk(calculatePsk(scoringDataDTO));
        credit.setInsuranceEnabled(scoringDataDTO.getInsuranceEnabled());
        credit.setSalaryClient(scoringDataDTO.getSalaryClient());

        return credit;

    }

    private  List<PaymentScheduleElement> getPaymentScheduleElement (ScoringDataDTO scoringDataDTO, BigDecimal monthlyPayment){

        List<PaymentScheduleElement> paymentSchedules = new ArrayList<>();

        BigDecimal firstInterestPayment = scoringDataDTO.getAmount().multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        BigDecimal firstDebtPayment = monthlyPayment.subtract(firstInterestPayment);
        BigDecimal firstRemainingDebt = scoringDataDTO.getAmount().subtract(firstDebtPayment);

        firstPaymentScheduleElement.setNumber(1);
        firstPaymentScheduleElement.setDate(LocalDate.now().plusMonths(1));
        firstPaymentScheduleElement.setTotalPayment(monthlyPayment);
        firstPaymentScheduleElement.setInterestPayment(firstInterestPayment);
        firstPaymentScheduleElement.setDebtPayment(firstDebtPayment);
        firstPaymentScheduleElement.setRemainingDebt(firstRemainingDebt);

        amountOfCreditPayments = firstDebtPayment;

        for (int i = 2; i <= scoringDataDTO.getTerm(); i++) {
            PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement();

            BigDecimal remains = scoringDataDTO.getAmount().subtract(amountOfCreditPayments);
            BigDecimal interestPayment = remains.multiply(scoringService.calculateRate(scoringDataDTO)).multiply(BigDecimal.valueOf(LocalDate.now().lengthOfMonth()))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            amountOfCreditPayments = amountOfCreditPayments.add(debtPayment);
            BigDecimal lastRemains = remains.subtract(debtPayment);

            paymentScheduleElement.setNumber(i);
            paymentScheduleElement.setDate(LocalDate.now().plusMonths(i));
            paymentScheduleElement.setTotalPayment(monthlyPayment);
            paymentScheduleElement.setInterestPayment(interestPayment);
            paymentScheduleElement.setDebtPayment(debtPayment);
            paymentScheduleElement.setRemainingDebt(lastRemains);

            paymentSchedules.add(paymentScheduleElement);
        }
        BigDecimal lastPayment = paymentSchedules.get(paymentSchedules.size() - 1).getRemainingDebt();

        lastPaymentScheduleElement.setNumber(paymentSchedules.get(paymentSchedules.size() - 1).getNumber() + 1);
        lastPaymentScheduleElement.setDate(paymentSchedules.get(paymentSchedules.size() - 1).getDate());
        lastPaymentScheduleElement.setTotalPayment(lastPayment);
        lastPaymentScheduleElement.setDebtPayment(lastPayment);
        paymentSchedules.add(lastPaymentScheduleElement);

        return paymentSchedules;
    }
    private BigDecimal calculatePsk(ScoringDataDTO scoringDataDTO){
        BigDecimal psk = amountOfCreditPayments.divide(scoringDataDTO.getAmount(), 2, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1))
                .divide(BigDecimal.valueOf(LocalDate.from(firstPaymentScheduleElement.getDate()).until(lastPaymentScheduleElement.getDate(), ChronoUnit.YEARS)))
                .multiply(BigDecimal.valueOf(100));
        return psk;
    }

}
