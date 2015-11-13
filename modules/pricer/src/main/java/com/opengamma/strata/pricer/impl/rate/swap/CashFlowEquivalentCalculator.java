/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.impl.rate.swap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.opengamma.strata.basics.PayReceive;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.Payment;
import com.opengamma.strata.basics.index.IborIndex;
import com.opengamma.strata.basics.schedule.SchedulePeriod;
import com.opengamma.strata.finance.rate.FixedRateObservation;
import com.opengamma.strata.finance.rate.IborRateObservation;
import com.opengamma.strata.finance.rate.swap.ExpandedSwap;
import com.opengamma.strata.finance.rate.swap.ExpandedSwapLeg;
import com.opengamma.strata.finance.rate.swap.KnownAmountPaymentPeriod;
import com.opengamma.strata.finance.rate.swap.PaymentPeriod;
import com.opengamma.strata.finance.rate.swap.RateAccrualPeriod;
import com.opengamma.strata.finance.rate.swap.RatePaymentPeriod;
import com.opengamma.strata.finance.rate.swap.SwapLegType;
import com.opengamma.strata.finance.rate.swap.SwapProduct;

public class CashFlowEquivalentCalculator {

  public static final CashFlowEquivalentCalculator DEFAULT = new CashFlowEquivalentCalculator();

  public ExpandedSwapLeg cashFlowEquivalent(SwapProduct swap) {
    // TODO exclude x-currency
    ExpandedSwap expanded = swap.expand();
    ExpandedSwapLeg fixedLeg = expanded.getLegs(SwapLegType.FIXED).get(0);
    ExpandedSwapLeg iborLeg = expanded.getLegs(SwapLegType.IBOR).get(0);
    List<PaymentPeriod> paymentPeriods = new ArrayList<PaymentPeriod>();
    TreeMap<LocalDate, KnownAmountPaymentPeriod> flow = new TreeMap<>();
    for (PaymentPeriod paymentPeriod : fixedLeg.getPaymentPeriods()) {
      RatePaymentPeriod ratePaymentPeriod = (RatePaymentPeriod) paymentPeriod; // TODO check
      RateAccrualPeriod rateAccrualPeriod = ratePaymentPeriod.getAccrualPeriods().get(0); // TODO check
      double factor = rateAccrualPeriod.getYearFraction() *
          ((FixedRateObservation) rateAccrualPeriod.getRateObservation()).getRate();
      CurrencyAmount notional = ratePaymentPeriod.getNotionalAmount().multipliedBy(factor);
      LocalDate startDate = rateAccrualPeriod.getStartDate();
      LocalDate endDate = rateAccrualPeriod.getEndDate();
      LocalDate paymentDate = ratePaymentPeriod.getPaymentDate();
      KnownAmountPaymentPeriod pay = KnownAmountPaymentPeriod.of(
          Payment.of(notional, paymentDate), SchedulePeriod.of(startDate, endDate));
      paymentPeriods.add(pay);
    }

    for (PaymentPeriod paymentPeriod : iborLeg.getPaymentPeriods()) {
      RatePaymentPeriod ratePaymentPeriod = (RatePaymentPeriod) paymentPeriod; // TODO check
      RateAccrualPeriod rateAccrualPeriod = ratePaymentPeriod.getAccrualPeriods().get(0); // TODO check
      CurrencyAmount notional = ratePaymentPeriod.getNotionalAmount();
      LocalDate startDate = rateAccrualPeriod.getStartDate();
      LocalDate endDate = rateAccrualPeriod.getEndDate();
      LocalDate paymentDate = ratePaymentPeriod.getPaymentDate();

      IborRateObservation obs = ((IborRateObservation) rateAccrualPeriod.getRateObservation()); // TODO
      IborIndex index = obs.getIndex();
      LocalDate fixingStartDate = index.calculateEffectiveFromFixing(obs.getFixingDate());
      LocalDate fixingEndDate = index.calculateMaturityFromEffective(fixingStartDate);
      double fixingYearFraction = index.getDayCount().yearFraction(fixingStartDate, fixingEndDate);

      //      RatePaymentPeriod payStart = ratePaymentPeriod;
      RatePaymentPeriod payStart = RatePaymentPeriod.builder()
          .accrualPeriods(ratePaymentPeriod.getAccrualPeriods()) // TODO no compounding
          .currency(ratePaymentPeriod.getCurrency())
          .dayCount(ratePaymentPeriod.getDayCount())
          .notional(ratePaymentPeriod.getNotional() * rateAccrualPeriod.getYearFraction() / fixingYearFraction)
          .paymentDate(paymentDate).build();

      KnownAmountPaymentPeriod payEnd = KnownAmountPaymentPeriod.of(
          Payment.of(notional.multipliedBy(-rateAccrualPeriod.getYearFraction() / fixingYearFraction), paymentDate),
          SchedulePeriod.of(startDate, endDate));
      paymentPeriods.add(payStart);
      paymentPeriods.add(payEnd);
    }
    paymentPeriods.addAll(flow.values());
    
    ExpandedSwapLeg leg = ExpandedSwapLeg.builder()
        .paymentPeriods(paymentPeriods)
        .payReceive(PayReceive.RECEIVE)
        .type(SwapLegType.OTHER)
        .build();
    return leg;
  }

}
