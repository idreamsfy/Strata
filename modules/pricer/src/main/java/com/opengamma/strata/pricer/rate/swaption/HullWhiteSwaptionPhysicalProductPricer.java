package com.opengamma.strata.pricer.rate.swaption;

import java.time.LocalDate;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.value.ValueDerivatives;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;
import com.opengamma.strata.math.impl.statistics.distribution.NormalDistribution;
import com.opengamma.strata.math.impl.statistics.distribution.ProbabilityDistribution;
import com.opengamma.strata.pricer.impl.rate.swap.CashFlowEquivalentCalculator;
import com.opengamma.strata.pricer.rate.RatesProvider;
import com.opengamma.strata.pricer.rate.swap.PaymentPeriodPricer;
import com.opengamma.strata.product.rate.swap.ExpandedSwap;
import com.opengamma.strata.product.rate.swap.ExpandedSwapLeg;
import com.opengamma.strata.product.rate.swap.KnownAmountPaymentPeriod;
import com.opengamma.strata.product.rate.swap.PaymentPeriod;
import com.opengamma.strata.product.rate.swap.RatePaymentPeriod;
import com.opengamma.strata.product.rate.swap.SwapLegType;
import com.opengamma.strata.product.rate.swaption.ExpandedSwaption;
import com.opengamma.strata.product.rate.swaption.SettlementType;
import com.opengamma.strata.product.rate.swaption.SwaptionProduct;

public class HullWhiteSwaptionPhysicalProductPricer {

  private static final ProbabilityDistribution<Double> NORMAL = new NormalDistribution(0, 1);

  public static final HullWhiteSwaptionPhysicalProductPricer DEFAULT =
      new HullWhiteSwaptionPhysicalProductPricer(PaymentPeriodPricer.instance());

  /**
   * Pricer for {@link PaymentPeriod}.
   */
  private final PaymentPeriodPricer<PaymentPeriod> paymentPeriodPricer;

  /**
   * Creates an instance.
   * 
   * @param paymentPeriodPricer  the pricer for {@link PaymentPeriod}
   */
  public HullWhiteSwaptionPhysicalProductPricer(PaymentPeriodPricer<PaymentPeriod> paymentPeriodPricer) {
    this.paymentPeriodPricer = ArgChecker.notNull(paymentPeriodPricer, "paymentPeriodPricer");
  }

  public CurrencyAmount presentValue(
      SwaptionProduct swaption,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider) {

    ExpandedSwaption expanded = swaption.expand();
    validate(expanded, ratesProvider, hwProvider);
    ExpandedSwap swap = expanded.getUnderlying();
    ExpandedSwapLeg cashFlowEquiv = CashFlowEquivalentCalculator.cashFlowEquivalent(swap);
    int nPayments = cashFlowEquiv.getPaymentPeriods().size();
    LocalDate expiryDate = expanded.getExpiryDate();
    double[] alpha = new double[nPayments];
    double[] discountedCashFlow = new double[nPayments];
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      PaymentPeriod payment = cashFlowEquiv.getPaymentPeriods().get(loopcf);
      LocalDate paymentDate = cashFlowEquivalentPaymnetDate(payment);
      alpha[loopcf] = hwProvider.alpha(ratesProvider.getValuationDate(), expiryDate, expiryDate, paymentDate);
      discountedCashFlow[loopcf] = paymentPeriodPricer.presentValueCashFlowEquivalent(payment, ratesProvider);
    }
    double kappa = hwProvider.getModel().kappa(DoubleArray.copyOf(discountedCashFlow), DoubleArray.copyOf(alpha));
    double omega = (swap.getLegs(SwapLegType.FIXED).get(0).getPayReceive().isPay() ? -1d : 1d);
    double pv = 0.0;
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      pv += discountedCashFlow[loopcf] * NORMAL.getCDF(omega * (kappa + alpha[loopcf]));
    }
    return CurrencyAmount.of(cashFlowEquiv.getCurrency(), pv * (expanded.getLongShort().isLong() ? 1d : -1d));
  }

  public PointSensitivityBuilder presentValueSensitivity(
      SwaptionProduct swaption,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider) {

    ExpandedSwaption expanded = swaption.expand();
    validate(expanded, ratesProvider, hwProvider);
    ExpandedSwap swap = expanded.getUnderlying();
    ExpandedSwapLeg cashFlowEquiv = CashFlowEquivalentCalculator.cashFlowEquivalent(swap);
    int nPayments = cashFlowEquiv.getPaymentPeriods().size();
    LocalDate expiryDate = expanded.getExpiryDate();
    double[] alpha = new double[nPayments];
    double[] discountedCashFlow = new double[nPayments];
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      PaymentPeriod payment = cashFlowEquiv.getPaymentPeriods().get(loopcf);
      LocalDate paymentDate = cashFlowEquivalentPaymnetDate(payment);
      alpha[loopcf] = hwProvider.alpha(ratesProvider.getValuationDate(), expiryDate, expiryDate, paymentDate);
      discountedCashFlow[loopcf] = paymentPeriodPricer.presentValueCashFlowEquivalent(payment, ratesProvider);
    }
    double kappa = hwProvider.getModel().kappa(DoubleArray.copyOf(discountedCashFlow), DoubleArray.copyOf(alpha));
    double omega = (swap.getLegs(SwapLegType.FIXED).get(0).getPayReceive().isPay() ? -1d : 1d);

    PointSensitivityBuilder point = PointSensitivityBuilder.none();
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      PaymentPeriod payment = cashFlowEquiv.getPaymentPeriods().get(loopcf);
      point = point.combinedWith(paymentPeriodPricer.presentValueSensitivityCashFlowEquivalent(payment, ratesProvider)
          .multipliedBy(NORMAL.getCDF(omega * (kappa + alpha[loopcf]))));
    }
    return expanded.getLongShort().isLong() ? point : point.multipliedBy(-1d);
  }

  public DoubleArray presentValueHullWhiteSensitivity(
      SwaptionProduct swaption,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider) {

    ExpandedSwaption expanded = swaption.expand();
    validate(expanded, ratesProvider, hwProvider);
    ExpandedSwap swap = expanded.getUnderlying();
    ExpandedSwapLeg cashFlowEquiv = CashFlowEquivalentCalculator.cashFlowEquivalent(swap);
    int nPayments = cashFlowEquiv.getPaymentPeriods().size();
    LocalDate expiryDate = expanded.getExpiryDate();
    double[] alpha = new double[nPayments];
    double[][] alphaAdjoint = new double[nPayments][];
    double[] discountedCashFlow = new double[nPayments];
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      PaymentPeriod payment = cashFlowEquiv.getPaymentPeriods().get(loopcf);
      LocalDate paymentDate = cashFlowEquivalentPaymnetDate(payment);
      ValueDerivatives valueDeriv = hwProvider.alphaAdjoint(
          ratesProvider.getValuationDate(), expiryDate, expiryDate, paymentDate);
      alpha[loopcf] = valueDeriv.getValue();
      alphaAdjoint[loopcf] = valueDeriv.getDerivatives();
      discountedCashFlow[loopcf] = paymentPeriodPricer.presentValueCashFlowEquivalent(payment, ratesProvider);
    }
    double kappa = hwProvider.getModel().kappa(DoubleArray.copyOf(discountedCashFlow), DoubleArray.copyOf(alpha));
    double omega = (swap.getLegs(SwapLegType.FIXED).get(0).getPayReceive().isPay() ? -1d : 1d);
    double pv = 0.0;

    int nParams = alphaAdjoint[0].length;
    double[] pvSensi = new double[nParams];
    double sign = (expanded.getLongShort().isLong() ? 1d : -1d);
    for (int i = 0; i < nParams; ++i) {
      for (int loopcf = 0; loopcf < nPayments; loopcf++) {
        pvSensi[i] += sign * discountedCashFlow[loopcf] * NORMAL.getPDF(omega * (kappa + alpha[loopcf])) * omega *
            alphaAdjoint[loopcf][i];
        pv += discountedCashFlow[loopcf] * NORMAL.getCDF(omega * (kappa + alpha[loopcf]));
      }
    }
    CurrencyAmount.of(cashFlowEquiv.getCurrency(), pv * (expanded.getLongShort().isLong() ? 1d : -1d));
    return DoubleArray.copyOf(pvSensi);
  }

  //-------------------------------------------------------------------------
  // validate that the rates and volatilities providers are coherent
  private void validate(ExpandedSwaption swaption, RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider volatilityProvider) {
    ArgChecker.isTrue(volatilityProvider.getValuationDateTime().toLocalDate().equals(ratesProvider.getValuationDate()),
        "Hull-White model data and rate data should be for the same date");
    ArgChecker.isFalse(swaption.getUnderlying().isCrossCurrency(), "underlying swap should be single currency");
    ArgChecker.isTrue(swaption.getSwaptionSettlement().getSettlementType().equals(SettlementType.PHYSICAL),
        "swaption should be physical settlement");
  }

  // returns "correct" payment date
  private LocalDate cashFlowEquivalentPaymnetDate(PaymentPeriod payment) {
    return payment instanceof KnownAmountPaymentPeriod ? payment.getPaymentDate()
        : ((RatePaymentPeriod) payment).getAccrualPeriods().get(0).getStartDate();
  }
}
