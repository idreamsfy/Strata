/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.impl.bond;

import static com.opengamma.strata.basics.currency.Currency.GBP;
import static com.opengamma.strata.basics.currency.Currency.USD;
import static com.opengamma.strata.basics.date.DayCounts.ACT_365F;
import static com.opengamma.strata.collect.TestHelper.date;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;

import org.testng.annotations.Test;

import com.opengamma.analytics.math.interpolation.Interpolator1DFactory;
import com.opengamma.strata.basics.interpolator.CurveInterpolator;
import com.opengamma.strata.finance.rate.bond.FixedCouponBondPaymentPeriod;
import com.opengamma.strata.market.curve.CurveMetadata;
import com.opengamma.strata.market.curve.CurveName;
import com.opengamma.strata.market.curve.Curves;
import com.opengamma.strata.market.curve.InterpolatedNodalCurve;
import com.opengamma.strata.market.explain.ExplainKey;
import com.opengamma.strata.market.explain.ExplainMap;
import com.opengamma.strata.market.explain.ExplainMapBuilder;
import com.opengamma.strata.market.sensitivity.IssuerCurveZeroRateSensitivity;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;
import com.opengamma.strata.market.value.DiscountFactors;
import com.opengamma.strata.market.value.IssuerCurveDiscountFactors;
import com.opengamma.strata.market.value.LegalEntityGroup;
import com.opengamma.strata.market.value.ZeroRateDiscountFactors;

/**
 * Test {@link DiscountingFixedCouponBondPaymentPeriodPricer}.
 */
@Test
public class DiscountingFixedCouponBondPaymentPeriodPricerTest {
  // issuer curves
  private static final LocalDate VALUATION = date(2015, 1, 28);
  private static final LocalDate VALUATION_AFTER = date(2015, 8, 28);
  private static final CurveInterpolator INTERPOLATOR = Interpolator1DFactory.LINEAR_INSTANCE;
  private static final CurveName NAME = CurveName.of("TestCurve");
  private static final CurveMetadata METADATA = Curves.zeroRates(NAME, ACT_365F);
  private static final InterpolatedNodalCurve CURVE =
      InterpolatedNodalCurve.of(METADATA, new double[] {0, 10 }, new double[] {0.1, 0.18 }, INTERPOLATOR);
  private static final DiscountFactors DSC_FACTORS = ZeroRateDiscountFactors.of(GBP, VALUATION, CURVE);
  private static final DiscountFactors DSC_FACTORS_AFTER = ZeroRateDiscountFactors.of(GBP, VALUATION_AFTER, CURVE);
  private static final LegalEntityGroup GROUP = LegalEntityGroup.of("ISSUER1");
  private static final IssuerCurveDiscountFactors ISSUER_CURVE = IssuerCurveDiscountFactors.of(DSC_FACTORS, GROUP);
  private static final IssuerCurveDiscountFactors ISSUER_CURVE_AFTER =
      IssuerCurveDiscountFactors.of(DSC_FACTORS_AFTER, GROUP);
  // coupon payment
  private static final LocalDate START = LocalDate.of(2015, 2, 2);
  private static final LocalDate END = LocalDate.of(2015, 8, 2);
  private static final LocalDate START_ADJUSTED = LocalDate.of(2015, 2, 2);
  private static final LocalDate END_ADJUSTED = LocalDate.of(2015, 8, 3);
  private static final double FIXED_RATE = 0.025;
  private static final double NOTIONAL = 1.0e7;
  private static final double YEAR_FRACTION = 0.51;
  private static final FixedCouponBondPaymentPeriod PAYMENT_PERIOD = FixedCouponBondPaymentPeriod.builder()
      .currency(USD)
      .startDate(START_ADJUSTED)
      .unadjustedStartDate(START)
      .endDate(END_ADJUSTED)
      .unadjustedEndDate(END)
      .notional(NOTIONAL)
      .fixedRate(FIXED_RATE)
      .yearFraction(YEAR_FRACTION)
      .build();
  /// z-spread
  private static final double Z_SPREAD = 0.02;
  private static final int PERIOD_PER_YEAR = 4;

  private static final DiscountingFixedCouponBondPaymentPeriodPricer PRICER =
      DiscountingFixedCouponBondPaymentPeriodPricer.DEFAULT;
  private static final double TOL = 1.0e-12;

  public void test_presentValue() {
    double computed = PRICER.presentValue(PAYMENT_PERIOD, ISSUER_CURVE);
    double expected = FIXED_RATE * NOTIONAL * YEAR_FRACTION * DSC_FACTORS.discountFactor(END_ADJUSTED);
    assertEquals(computed, expected);
  }

  public void test_presentValue_withSpread() {
    double computed = PRICER.presentValue(PAYMENT_PERIOD, ISSUER_CURVE, Z_SPREAD, true, PERIOD_PER_YEAR);
    double expected = FIXED_RATE * NOTIONAL * YEAR_FRACTION *
        DSC_FACTORS.discountFactorWithSpread(END_ADJUSTED, Z_SPREAD, true, PERIOD_PER_YEAR);
    assertEquals(computed, expected);
  }

  public void test_futureValue() {
    double computed = PRICER.futureValue(PAYMENT_PERIOD, ISSUER_CURVE);
    double expected = FIXED_RATE * NOTIONAL * YEAR_FRACTION;
    assertEquals(computed, expected);
  }

  //-------------------------------------------------------------------------
  public void test_presentValueSensitivity() {
    PointSensitivityBuilder computed = PRICER.presentValueSensitivity(PAYMENT_PERIOD, ISSUER_CURVE);
    PointSensitivityBuilder expected = IssuerCurveZeroRateSensitivity.of(
        DSC_FACTORS.zeroRatePointSensitivity(END_ADJUSTED).multipliedBy(FIXED_RATE * NOTIONAL * YEAR_FRACTION), GROUP);
    assertEquals(computed, expected);
  }

  public void test_presentValueSensitivity_withSpread() {
    PointSensitivityBuilder computed = PRICER.presentValueSensitivity(
        PAYMENT_PERIOD, ISSUER_CURVE, Z_SPREAD, true, PERIOD_PER_YEAR);
    PointSensitivityBuilder expected = IssuerCurveZeroRateSensitivity.of(
        DSC_FACTORS.zeroRatePointSensitivityWithSpread(END_ADJUSTED, Z_SPREAD, true, PERIOD_PER_YEAR)
            .multipliedBy(FIXED_RATE * NOTIONAL * YEAR_FRACTION), GROUP);
    assertEquals(computed, expected);
  }

  public void test_futureValueSensitivity() {
    PointSensitivityBuilder computed = PRICER.futureValueSensitivity(PAYMENT_PERIOD, ISSUER_CURVE);
    assertEquals(computed, PointSensitivityBuilder.none());
  }

  //-------------------------------------------------------------------------
  public void test_explainPresentValue() {
    ExplainMapBuilder builder = ExplainMap.builder();
    PRICER.explainPresentValue(PAYMENT_PERIOD, ISSUER_CURVE, builder);
    ExplainMap explain = builder.build();
    assertEquals(explain.get(ExplainKey.ENTRY_TYPE).get(), "FixedCouponBondPaymentPeriod");
    assertEquals(explain.get(ExplainKey.PAYMENT_DATE).get(), PAYMENT_PERIOD.getPaymentDate());
    assertEquals(explain.get(ExplainKey.PAYMENT_CURRENCY).get(), PAYMENT_PERIOD.getCurrency());
    assertEquals(explain.get(ExplainKey.START_DATE).get(), START_ADJUSTED);
    assertEquals(explain.get(ExplainKey.UNADJUSTED_START_DATE).get(), START);
    assertEquals(explain.get(ExplainKey.END_DATE).get(), END_ADJUSTED);
    assertEquals(explain.get(ExplainKey.UNADJUSTED_END_DATE).get(), END);
    assertEquals(explain.get(ExplainKey.ACCRUAL_DAYS).get().intValue(),
        (int) DAYS.between(START_ADJUSTED, END_ADJUSTED));
    assertEquals(explain.get(ExplainKey.DISCOUNT_FACTOR).get(), DSC_FACTORS.discountFactor(END_ADJUSTED));
    assertEquals(explain.get(ExplainKey.FUTURE_VALUE).get().getAmount(),
        FIXED_RATE * NOTIONAL * YEAR_FRACTION, NOTIONAL * TOL);
    assertEquals(explain.get(ExplainKey.PRESENT_VALUE).get().getAmount(),
        FIXED_RATE * NOTIONAL * YEAR_FRACTION * DSC_FACTORS.discountFactor(END_ADJUSTED), NOTIONAL * TOL);

  }

  public void test_explainPresentValue_past() {
    ExplainMapBuilder builder = ExplainMap.builder();
    PRICER.explainPresentValue(PAYMENT_PERIOD, ISSUER_CURVE_AFTER, builder);
    ExplainMap explain = builder.build();
    assertEquals(explain.get(ExplainKey.ENTRY_TYPE).get(), "FixedCouponBondPaymentPeriod");
    assertEquals(explain.get(ExplainKey.PAYMENT_DATE).get(), PAYMENT_PERIOD.getPaymentDate());
    assertEquals(explain.get(ExplainKey.PAYMENT_CURRENCY).get(), PAYMENT_PERIOD.getCurrency());
    assertEquals(explain.get(ExplainKey.START_DATE).get(), START_ADJUSTED);
    assertEquals(explain.get(ExplainKey.UNADJUSTED_START_DATE).get(), START);
    assertEquals(explain.get(ExplainKey.END_DATE).get(), END_ADJUSTED);
    assertEquals(explain.get(ExplainKey.UNADJUSTED_END_DATE).get(), END);
    assertEquals(explain.get(ExplainKey.ACCRUAL_DAYS).get().intValue(),
        (int) DAYS.between(START_ADJUSTED, END_ADJUSTED));
    assertEquals(explain.get(ExplainKey.FUTURE_VALUE).get().getAmount(), 0d, NOTIONAL * TOL);
    assertEquals(explain.get(ExplainKey.PRESENT_VALUE).get().getAmount(), 0d, NOTIONAL * TOL);
  }

  public void test_explainPresentValue_withSpread() {
    ExplainMapBuilder builder = ExplainMap.builder();
    PRICER.explainPresentValue(PAYMENT_PERIOD, ISSUER_CURVE, builder, Z_SPREAD, true, PERIOD_PER_YEAR);
    ExplainMap explain = builder.build();
    assertEquals(explain.get(ExplainKey.ENTRY_TYPE).get(), "FixedCouponBondPaymentPeriod");
    assertEquals(explain.get(ExplainKey.PAYMENT_DATE).get(), PAYMENT_PERIOD.getPaymentDate());
    assertEquals(explain.get(ExplainKey.PAYMENT_CURRENCY).get(), PAYMENT_PERIOD.getCurrency());
    assertEquals(explain.get(ExplainKey.START_DATE).get(), START_ADJUSTED);
    assertEquals(explain.get(ExplainKey.UNADJUSTED_START_DATE).get(), START);
    assertEquals(explain.get(ExplainKey.END_DATE).get(), END_ADJUSTED);
    assertEquals(explain.get(ExplainKey.UNADJUSTED_END_DATE).get(), END);
    assertEquals(explain.get(ExplainKey.ACCRUAL_DAYS).get().intValue(),
        (int) DAYS.between(START_ADJUSTED, END_ADJUSTED));
    assertEquals(explain.get(ExplainKey.DISCOUNT_FACTOR).get(),
        DSC_FACTORS.discountFactorWithSpread(END_ADJUSTED, Z_SPREAD, true, PERIOD_PER_YEAR));
    assertEquals(explain.get(ExplainKey.FUTURE_VALUE).get().getAmount(),
        FIXED_RATE * NOTIONAL * YEAR_FRACTION, NOTIONAL * TOL);
    assertEquals(explain.get(ExplainKey.PRESENT_VALUE).get().getAmount(), FIXED_RATE * NOTIONAL * YEAR_FRACTION *
        DSC_FACTORS.discountFactorWithSpread(END_ADJUSTED, Z_SPREAD, true, PERIOD_PER_YEAR), NOTIONAL * TOL);
  }

  public void test_explainPresentValue_withSpread_past() {
    ExplainMapBuilder builder = ExplainMap.builder();
    PRICER.explainPresentValue(PAYMENT_PERIOD, ISSUER_CURVE_AFTER, builder, Z_SPREAD, true, PERIOD_PER_YEAR);
    ExplainMap explain = builder.build();
    assertEquals(explain.get(ExplainKey.ENTRY_TYPE).get(), "FixedCouponBondPaymentPeriod");
    assertEquals(explain.get(ExplainKey.PAYMENT_DATE).get(), PAYMENT_PERIOD.getPaymentDate());
    assertEquals(explain.get(ExplainKey.PAYMENT_CURRENCY).get(), PAYMENT_PERIOD.getCurrency());
    assertEquals(explain.get(ExplainKey.START_DATE).get(), START_ADJUSTED);
    assertEquals(explain.get(ExplainKey.UNADJUSTED_START_DATE).get(), START);
    assertEquals(explain.get(ExplainKey.END_DATE).get(), END_ADJUSTED);
    assertEquals(explain.get(ExplainKey.UNADJUSTED_END_DATE).get(), END);
    assertEquals(explain.get(ExplainKey.ACCRUAL_DAYS).get().intValue(),
        (int) DAYS.between(START_ADJUSTED, END_ADJUSTED));
    assertEquals(explain.get(ExplainKey.FUTURE_VALUE).get().getAmount(), 0d, NOTIONAL * TOL);
    assertEquals(explain.get(ExplainKey.PRESENT_VALUE).get().getAmount(), 0d, NOTIONAL * TOL);
  }
}
