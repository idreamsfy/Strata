/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.future;

import static com.opengamma.strata.basics.currency.Currency.EUR;
import static com.opengamma.strata.basics.date.DayCounts.ACT_ACT_ISDA;
import static com.opengamma.strata.basics.index.IborIndices.EUR_EURIBOR_3M;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.time.LocalDate;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.FxMatrix;
import com.opengamma.strata.basics.interpolator.CurveInterpolator;
import com.opengamma.strata.basics.value.Rounding;
import com.opengamma.strata.collect.DoubleArrayMath;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.finance.rate.future.IborFuture;
import com.opengamma.strata.market.curve.CurveMetadata;
import com.opengamma.strata.market.curve.CurveName;
import com.opengamma.strata.market.curve.Curves;
import com.opengamma.strata.market.curve.InterpolatedNodalCurve;
import com.opengamma.strata.market.interpolator.CurveInterpolators;
import com.opengamma.strata.market.sensitivity.CurveCurrencyParameterSensitivities;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.pricer.impl.rate.model.HullWhiteOneFactorPiecewiseConstantParameters;
import com.opengamma.strata.pricer.rate.ImmutableRatesProvider;
import com.opengamma.strata.pricer.sensitivity.RatesFiniteDifferenceSensitivityCalculator;

/**
 * Test {@link HullWhiteIborFutureProductPricer}.
 */
@Test
public class HullWhiteIborFutureProductPricerTest {
  private static final LocalDate VALUATION = LocalDate.of(2011, 5, 12);
  private static final double MEAN_REVERSION = 0.01;
  private static final DoubleArray VOLATILITY = DoubleArray.of(0.01, 0.011, 0.012, 0.013, 0.014);
  private static final DoubleArray VOLATILITY_TIME = DoubleArray.of(0.5, 1.0, 2.0, 5.0);
  private static final HullWhiteOneFactorPiecewiseConstantParameters MODEL_PARAMETERS =
      HullWhiteOneFactorPiecewiseConstantParameters.of(MEAN_REVERSION, VOLATILITY, VOLATILITY_TIME);
  private static final HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider HW_PROVIDER =
      HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider.of(MODEL_PARAMETERS, ACT_ACT_ISDA, VALUATION);

  private static final CurveInterpolator INTERPOLATOR = CurveInterpolators.LINEAR;
  private static final DoubleArray DSC_TIME = DoubleArray.of(0.0, 0.5, 1.0, 2.0, 5.0, 10.0);
  private static final DoubleArray DSC_RATE = DoubleArray.of(0.0150, 0.0125, 0.0150, 0.0175, 0.0150, 0.0150);
  private static final CurveName DSC_NAME = CurveName.of("EUR Dsc");
  private static final CurveMetadata META_DSC = Curves.zeroRates(DSC_NAME, ACT_ACT_ISDA);
  private static final InterpolatedNodalCurve DSC_CURVE =
      InterpolatedNodalCurve.of(META_DSC, DSC_TIME, DSC_RATE, INTERPOLATOR);
  private static final DoubleArray FWD3_TIME = DoubleArray.of(0.0, 0.5, 1.0, 2.0, 3.0, 4.0, 5.0, 10.0);
  private static final DoubleArray FWD3_RATE =
      DoubleArray.of(0.0150, 0.0125, 0.0150, 0.0175, 0.0175, 0.0190, 0.0200, 0.0210);
  private static final CurveName FWD3_NAME = CurveName.of("EUR EURIBOR 3M");
  private static final CurveMetadata META_FWD3 = Curves.zeroRates(FWD3_NAME, ACT_ACT_ISDA);
  private static final InterpolatedNodalCurve FWD3_CURVE =
      InterpolatedNodalCurve.of(META_FWD3, FWD3_TIME, FWD3_RATE, INTERPOLATOR);
  private static final ImmutableRatesProvider RATE_PROVIDER = ImmutableRatesProvider.builder()
      .discountCurves(ImmutableMap.of(EUR, DSC_CURVE))
      .indexCurves(ImmutableMap.of(EUR_EURIBOR_3M, FWD3_CURVE))
      .fxMatrix(FxMatrix.empty())
      .valuationDate(VALUATION)
      .build();

  private static final double NOTIONAL = 1000000.0;
  private static final LocalDate LAST_TRADE_DATE = LocalDate.of(2012, 9, 17);
  private static final double FUTURE_FACTOR = 0.25;
  private static final IborFuture IBOR_FUTURE = IborFuture.builder()
      .currency(EUR)
      .notional(NOTIONAL)
      .lastTradeDate(LAST_TRADE_DATE)
      .index(EUR_EURIBOR_3M)
      .accrualFactor(FUTURE_FACTOR)
      .rounding(Rounding.none())
      .build();

  private static final double TOL = 1.0e-12;
  private static final double TOL_FD = 1.0e-6;
  private static final HullWhiteIborFutureProductPricer PRICER = HullWhiteIborFutureProductPricer.DEFAULT;
  private static final DiscountingIborFutureProductPricer PRICER_DSC = DiscountingIborFutureProductPricer.DEFAULT;
  private static final RatesFiniteDifferenceSensitivityCalculator FD_CAL =
      new RatesFiniteDifferenceSensitivityCalculator(TOL_FD);

  public void test_price() {
    double computed = PRICER.price(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    LocalDate start = EUR_EURIBOR_3M.calculateEffectiveFromFixing(IBOR_FUTURE.getFixingDate());
    LocalDate end = EUR_EURIBOR_3M.calculateMaturityFromEffective(start);
    double fixingYearFraction = EUR_EURIBOR_3M.getDayCount().yearFraction(start, end);
    double convexity = HW_PROVIDER.futuresConvexityFactor(IBOR_FUTURE.getLastTradeDate(), start, end);
    double forward = RATE_PROVIDER.iborIndexRates(EUR_EURIBOR_3M).rate(IBOR_FUTURE.getFixingDate());
    double expected = 1d - convexity * forward + (1d - convexity) / fixingYearFraction;
    assertEquals(computed, expected, TOL);
  }

  public void test_parRate() {
    double computed = PRICER.parRate(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    double price = PRICER.price(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    assertEquals(computed, 1d - price, TOL);
  }

  public void test_convexityAdjustment() {
    double computed = PRICER.convexityAdjustment(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    double priceHw = PRICER.price(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    double priceDsc = PRICER_DSC.price(IBOR_FUTURE, RATE_PROVIDER); // no convexity adjustment
    assertEquals(priceDsc + computed, priceHw, TOL);
  }

  public void test_priceSensitivity() {
    PointSensitivities point = PRICER.priceSensitivity(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    CurveCurrencyParameterSensitivities computed = RATE_PROVIDER.curveParameterSensitivity(point);
    CurveCurrencyParameterSensitivities expected =
        FD_CAL.sensitivity(RATE_PROVIDER, (p) -> CurrencyAmount.of(EUR, PRICER.price(IBOR_FUTURE, (p), HW_PROVIDER)));
    assertTrue(computed.equalWithTolerance(expected, TOL_FD));
  }

  //-------------------------------------------------------------------------
  public void regression_value() {
    double price = PRICER.price(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    assertEquals(price, 0.9802338355115904, TOL);
    double parRate = PRICER.parRate(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    assertEquals(parRate, 0.01976616448840962, TOL);
    double adjustment = PRICER.convexityAdjustment(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    assertEquals(adjustment, -1.3766374738599652E-4, TOL);
  }

  public void regression_sensitivity() {
    PointSensitivities point = PRICER.priceSensitivity(IBOR_FUTURE, RATE_PROVIDER, HW_PROVIDER);
    CurveCurrencyParameterSensitivities computed = RATE_PROVIDER.curveParameterSensitivity(point);
    double[] expected = new double[] {0.0, 0.0, 0.9514709785770106, -1.9399920741192112, 0.0, 0.0, 0.0, 0.0 };
    assertEquals(computed.size(), 1);
    assertTrue(DoubleArrayMath.fuzzyEquals(computed.getSensitivity(FWD3_NAME, EUR).getSensitivity().toArray(),
        expected, TOL));
  }
}
