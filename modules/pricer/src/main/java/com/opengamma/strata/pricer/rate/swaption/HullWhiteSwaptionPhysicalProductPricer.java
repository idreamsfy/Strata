package com.opengamma.strata.pricer.rate.swaption;

import java.time.LocalDate;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.math.impl.statistics.distribution.NormalDistribution;
import com.opengamma.strata.math.impl.statistics.distribution.ProbabilityDistribution;
import com.opengamma.strata.pricer.impl.rate.swap.CashFlowEquivalentCalculator;
import com.opengamma.strata.pricer.rate.RatesProvider;
import com.opengamma.strata.pricer.rate.future.HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider;
import com.opengamma.strata.pricer.rate.swap.PaymentEventPricer;
import com.opengamma.strata.pricer.rate.swap.PaymentPeriodPricer;
import com.opengamma.strata.product.rate.swap.ExpandedSwap;
import com.opengamma.strata.product.rate.swap.ExpandedSwapLeg;
import com.opengamma.strata.product.rate.swap.KnownAmountPaymentPeriod;
import com.opengamma.strata.product.rate.swap.PaymentEvent;
import com.opengamma.strata.product.rate.swap.PaymentPeriod;
import com.opengamma.strata.product.rate.swap.RatePaymentPeriod;
import com.opengamma.strata.product.rate.swap.SwapLegType;
import com.opengamma.strata.product.rate.swaption.ExpandedSwaption;
import com.opengamma.strata.product.rate.swaption.SwaptionProduct;

public class HullWhiteSwaptionPhysicalProductPricer {

  private static final ProbabilityDistribution<Double> NORMAL = new NormalDistribution(0, 1);

  private static final CashFlowEquivalentCalculator CFE_CALC = CashFlowEquivalentCalculator.DEFAULT;

  public static final HullWhiteSwaptionPhysicalProductPricer DEFAULT =
      new HullWhiteSwaptionPhysicalProductPricer(
          PaymentPeriodPricer.instance(),
          PaymentEventPricer.instance());

  /**
   * Pricer for {@link PaymentPeriod}.
   */
  private final PaymentPeriodPricer<PaymentPeriod> paymentPeriodPricer;
  /**
   * Pricer for {@link PaymentEvent}.
   */
  private final PaymentEventPricer<PaymentEvent> paymentEventPricer;

  /**
   * Creates an instance.
   * 
   * @param paymentPeriodPricer  the pricer for {@link PaymentPeriod}
   * @param paymentEventPricer  the pricer for {@link PaymentEvent}
   */
  public HullWhiteSwaptionPhysicalProductPricer(
      PaymentPeriodPricer<PaymentPeriod> paymentPeriodPricer,
      PaymentEventPricer<PaymentEvent> paymentEventPricer) {
    this.paymentPeriodPricer = ArgChecker.notNull(paymentPeriodPricer, "paymentPeriodPricer");
    this.paymentEventPricer = ArgChecker.notNull(paymentEventPricer, "paymentEventPricer");
  }

  public CurrencyAmount presentValue(
      SwaptionProduct swaption,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider hwProvider) {

    ExpandedSwaption expanded = swaption.expand();
    ExpandedSwap swap = expanded.getUnderlying();
    ExpandedSwapLeg cashFlowEquiv = CFE_CALC.cashFlowEquivalent(swap);
    int nPayments = cashFlowEquiv.getPaymentPeriods().size();
    LocalDate expiryDate = expanded.getExpiryDate();
    double[] alpha = new double[nPayments];
    double[] discountedCashFlow = new double[nPayments];
    for (int loopcf = 0; loopcf < nPayments; loopcf++) {
      PaymentPeriod payment = cashFlowEquiv.getPaymentPeriods().get(loopcf);
      LocalDate paymentDate = cashFlowEquivalentPaymnetDate(payment);
      //      System.out.println(payment.toString());
      alpha[loopcf] = hwProvider.alpha(hwProvider.getValuationDate(), expiryDate, expiryDate, paymentDate);
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

  private LocalDate cashFlowEquivalentPaymnetDate(PaymentPeriod payment) {
    return payment instanceof KnownAmountPaymentPeriod ? payment.getPaymentDate()
        : ((RatePaymentPeriod) payment).getAccrualPeriods().get(0).getStartDate();
  }
}
