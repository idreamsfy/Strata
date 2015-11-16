/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.swaption;

import java.time.LocalDate;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.MultiCurrencyAmount;
import com.opengamma.strata.basics.value.ValueDerivatives;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.DoubleArrayMath;
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

/**
 * Pricer for swaption with physical settlement in Hull-White one factor model with piecewise constant volatility.
 * <p>
 * Reference: Henrard, M. "The Irony in the derivatives discounting Part II: the crisis", Wilmott Journal, 2010, 2, 301-316
 */
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

  /**
   * Calculates the present value of the swaption product.
   * <p>
   * The result is expressed using the currency of the swapion.
   * 
   * @param swaption  the product to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the Hull-White model parameter provider
   * @return the present value of the swaption product
   */
  public CurrencyAmount presentValue(
      SwaptionProduct swaption,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider) {

    ExpandedSwaption expanded = swaption.expand();
    validate(expanded, ratesProvider, hwProvider);
    ExpandedSwap swap = expanded.getUnderlying();
    ExpandedSwapLeg cashFlowEquiv = CashFlowEquivalentCalculator.cashFlowEquivalent(swap);
    LocalDate expiryDate = expanded.getExpiryDate();
    if (expiryDate.isBefore(ratesProvider.getValuationDate())) { // Option has expired already
      return CurrencyAmount.of(cashFlowEquiv.getCurrency(), 0d);
    }
    int nPayments = cashFlowEquiv.getPaymentPeriods().size();
    double[] alpha = new double[nPayments];
    double[] discountedCashFlow = new double[nPayments];
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      PaymentPeriod payment = cashFlowEquiv.getPaymentPeriods().get(loopcf);
      LocalDate paymentDate = cashFlowEquivalentPaymnetDate(payment);
      alpha[loopcf] = hwProvider.alpha(ratesProvider.getValuationDate(), expiryDate, expiryDate, paymentDate);
      discountedCashFlow[loopcf] = paymentPeriodPricer.presentValueCashFlowEquivalent(payment, ratesProvider);
    }
    double omega = (swap.getLegs(SwapLegType.FIXED).get(0).getPayReceive().isPay() ? -1d : 1d);
    double kappa = computeKappa(hwProvider, discountedCashFlow, alpha, omega);
    double pv = 0.0;
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      pv += discountedCashFlow[loopcf] * NORMAL.getCDF(omega * (kappa + alpha[loopcf]));
    }
    return CurrencyAmount.of(cashFlowEquiv.getCurrency(), pv * (expanded.getLongShort().isLong() ? 1d : -1d));
  }

  /**
   * Computes the currency exposure of the swaption product.
   * 
   * @param swaption  the product to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the Hull-White model parameter provider
   * @return the present value of the swaption product
   */
  public MultiCurrencyAmount currencyExposure(
      SwaptionProduct swaption,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider) {

    return MultiCurrencyAmount.of(presentValue(swaption, ratesProvider, hwProvider));
  }

  /**
   * Calculates the present value sensitivity of the swaption product.
   * <p>
   * The present value sensitivity of the product is the sensitivity of the present value to
   * the underlying curves.
   * 
   * @param swaption  the product to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the Hull-White model parameter provider
   * @return the present value of the swaption product
   */
  public PointSensitivityBuilder presentValueSensitivity(
      SwaptionProduct swaption,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider) {

    ExpandedSwaption expanded = swaption.expand();
    validate(expanded, ratesProvider, hwProvider);
    ExpandedSwap swap = expanded.getUnderlying();
    ExpandedSwapLeg cashFlowEquiv = CashFlowEquivalentCalculator.cashFlowEquivalent(swap);
    LocalDate expiryDate = expanded.getExpiryDate();
    if (expiryDate.isBefore(ratesProvider.getValuationDate())) { // Option has expired already
      return PointSensitivityBuilder.none();
    }
    int nPayments = cashFlowEquiv.getPaymentPeriods().size();
    double[] alpha = new double[nPayments];
    double[] discountedCashFlow = new double[nPayments];
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      PaymentPeriod payment = cashFlowEquiv.getPaymentPeriods().get(loopcf);
      LocalDate paymentDate = cashFlowEquivalentPaymnetDate(payment);
      alpha[loopcf] = hwProvider.alpha(ratesProvider.getValuationDate(), expiryDate, expiryDate, paymentDate);
      discountedCashFlow[loopcf] = paymentPeriodPricer.presentValueCashFlowEquivalent(payment, ratesProvider);
    }
    double omega = (swap.getLegs(SwapLegType.FIXED).get(0).getPayReceive().isPay() ? -1d : 1d);
    double kappa = computeKappa(hwProvider, discountedCashFlow, alpha, omega);
    PointSensitivityBuilder point = PointSensitivityBuilder.none();
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      PaymentPeriod payment = cashFlowEquiv.getPaymentPeriods().get(loopcf);
      point = point.combinedWith(paymentPeriodPricer.presentValueSensitivityCashFlowEquivalent(payment, ratesProvider)
          .multipliedBy(NORMAL.getCDF(omega * (kappa + alpha[loopcf]))));
    }
    return expanded.getLongShort().isLong() ? point : point.multipliedBy(-1d);
  }

  /**
   * Calculates the present value sensitivity to piecewise constant volatility parameters of the Hull-White model.
   * 
   * @param swaption  the product to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the Hull-White model parameter provider
   * @return the present value of the swaption product
   */
  public DoubleArray presentValueSensitivityHullWhiteParameter(
      SwaptionProduct swaption,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider) {

    ExpandedSwaption expanded = swaption.expand();
    validate(expanded, ratesProvider, hwProvider);
    ExpandedSwap swap = expanded.getUnderlying();
    ExpandedSwapLeg cashFlowEquiv = CashFlowEquivalentCalculator.cashFlowEquivalent(swap);
    LocalDate expiryDate = expanded.getExpiryDate();
    if (expiryDate.isBefore(ratesProvider.getValuationDate())) { // Option has expired already
      return DoubleArray.EMPTY;
    }
    int nPayments = cashFlowEquiv.getPaymentPeriods().size();
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
    double omega = (swap.getLegs(SwapLegType.FIXED).get(0).getPayReceive().isPay() ? -1d : 1d);
    double kappa = computeKappa(hwProvider, discountedCashFlow, alpha, omega);
    int nParams = alphaAdjoint[0].length;
    double[] pvSensi = new double[nParams];
    double sign = (expanded.getLongShort().isLong() ? 1d : -1d);
    for (int i = 0; i < nParams; ++i) {
      for (int loopcf = 0; loopcf < nPayments; loopcf++) {
        pvSensi[i] += sign * discountedCashFlow[loopcf] * NORMAL.getPDF(omega * (kappa + alpha[loopcf])) * omega *
            alphaAdjoint[loopcf][i];
      }
    }
    return DoubleArray.copyOf(pvSensi);
  }

  //-------------------------------------------------------------------------
  // validate that the rates and volatilities providers are coherent
  private void validate(ExpandedSwaption swaption, RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider) {
    ArgChecker.isTrue(hwProvider.getValuationDateTime().toLocalDate().equals(ratesProvider.getValuationDate()),
        "Hull-White model data and rate data should be for the same date");
    ArgChecker.isFalse(swaption.getUnderlying().isCrossCurrency(), "underlying swap should be single currency");
    ArgChecker.isTrue(swaption.getSwaptionSettlement().getSettlementType().equals(SettlementType.PHYSICAL),
        "swaption should be physical settlement");
  }

  // handling short time to expiry
  private double computeKappa(HullWhiteOneFactorPiecewiseConstantSwaptionProvider hwProvider,
      double[] discountedCashFlow, double[] alpha, double omega) {
    double kappa = 0d;
    if (DoubleArrayMath.fuzzyEqualsZero(alpha, 1.0e-9)) { // threshold coherent to rootfinder in kappa computation
      double totalPv = DoubleArrayMath.sum(discountedCashFlow);
      kappa = totalPv * omega > 0d ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    } else {
      kappa = hwProvider.getModel().kappa(DoubleArray.copyOf(discountedCashFlow), DoubleArray.copyOf(alpha));
    }
    return kappa;
  }

  // returns "correct" payment date
  private LocalDate cashFlowEquivalentPaymnetDate(PaymentPeriod payment) {
    return payment instanceof KnownAmountPaymentPeriod ? payment.getPaymentDate()
        : ((RatePaymentPeriod) payment).getAccrualPeriods().get(0).getStartDate();
  }
}
