/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.swaption;

import static com.opengamma.strata.basics.date.DayCounts.ACT_360;
import static com.opengamma.strata.basics.date.DayCounts.ACT_ACT_ISDA;
import static com.opengamma.strata.collect.TestHelper.assertSerialization;
import static com.opengamma.strata.collect.TestHelper.coverBeanEquals;
import static com.opengamma.strata.collect.TestHelper.coverImmutableBean;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.value.ValueDerivatives;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.pricer.impl.rate.model.HullWhiteOneFactorPiecewiseConstantInterestRateModel;
import com.opengamma.strata.pricer.impl.rate.model.HullWhiteOneFactorPiecewiseConstantParameters;

/**
 * Test {@link HullWhiteOneFactorPiecewiseConstantSwaptionProvider}.
 */
@Test
public class HullWhiteOneFactorPiecewiseConstantSwaptionProviderTest {

  private static final double MEAN_REVERSION = 0.01;
  private static final DoubleArray VOLATILITY = DoubleArray.of(0.01, 0.011, 0.012, 0.013, 0.014);
  private static final DoubleArray VOLATILITY_TIME = DoubleArray.of(0.5, 1.0, 2.0, 5.0);
  private static final HullWhiteOneFactorPiecewiseConstantParameters PARAMETERS =
      HullWhiteOneFactorPiecewiseConstantParameters.of(MEAN_REVERSION, VOLATILITY, VOLATILITY_TIME);
  private static final LocalDate VALUATION = LocalDate.of(2015, 2, 14);
  private static final LocalTime TIME = LocalTime.of(14, 00);
  private static final ZoneId ZONE = ZoneId.of("GMT+05");

  public void test_of_LocalDate() {
    HullWhiteOneFactorPiecewiseConstantSwaptionProvider test =
        HullWhiteOneFactorPiecewiseConstantSwaptionProvider.of(PARAMETERS, ACT_360, VALUATION);
    assertEquals(test.getDayCount(), ACT_360);
    assertEquals(test.getParameters(), PARAMETERS);
    assertEquals(test.getValuationDateTime(), VALUATION.atTime(LocalTime.NOON).atZone(ZoneOffset.UTC));
    assertEquals(test.getModel(), HullWhiteOneFactorPiecewiseConstantInterestRateModel.DEFAULT);
  }

  public void test_of_ZonedDateTime() {
    ZonedDateTime dataTime = VALUATION.atTime(TIME).atZone(ZONE);
    HullWhiteOneFactorPiecewiseConstantSwaptionProvider test =
        HullWhiteOneFactorPiecewiseConstantSwaptionProvider.of(PARAMETERS, ACT_360, dataTime);
    assertEquals(test.getDayCount(), ACT_360);
    assertEquals(test.getParameters(), PARAMETERS);
    assertEquals(test.getValuationDateTime(), dataTime);
    assertEquals(test.getModel(), HullWhiteOneFactorPiecewiseConstantInterestRateModel.DEFAULT);
  }

  public void test_of_LocalDateAndTime() {
    HullWhiteOneFactorPiecewiseConstantSwaptionProvider test =
        HullWhiteOneFactorPiecewiseConstantSwaptionProvider.of(PARAMETERS, ACT_360, VALUATION, TIME, ZONE);
    assertEquals(test.getDayCount(), ACT_360);
    assertEquals(test.getParameters(), PARAMETERS);
    assertEquals(test.getValuationDateTime(), VALUATION.atTime(TIME).atZone(ZONE));
    assertEquals(test.getModel(), HullWhiteOneFactorPiecewiseConstantInterestRateModel.DEFAULT);
  }

  public void test_alpha() {
    HullWhiteOneFactorPiecewiseConstantSwaptionProvider provider =
        HullWhiteOneFactorPiecewiseConstantSwaptionProvider.of(PARAMETERS, ACT_360, VALUATION);
    LocalDate data1 = LocalDate.of(2015, 5, 20);
    LocalDate data2 = LocalDate.of(2017, 5, 20);
    LocalDate data3 = LocalDate.of(2017, 3, 20);
    LocalDate data4 = LocalDate.of(2017, 12, 20);
    double computed = provider.alpha(data1, data2, data3, data4);
    double expected = HullWhiteOneFactorPiecewiseConstantInterestRateModel.DEFAULT.alpha(PARAMETERS,
        ACT_360.relativeYearFraction(VALUATION, data1), ACT_360.relativeYearFraction(VALUATION, data2),
        ACT_360.relativeYearFraction(VALUATION, data3), ACT_360.relativeYearFraction(VALUATION, data4));
    assertEquals(computed, expected);
  }

  public void test_alphaAdjoint() {
    HullWhiteOneFactorPiecewiseConstantSwaptionProvider provider =
        HullWhiteOneFactorPiecewiseConstantSwaptionProvider.of(PARAMETERS, ACT_360, VALUATION);
    LocalDate data1 = LocalDate.of(2015, 5, 20);
    LocalDate data2 = LocalDate.of(2017, 5, 20);
    LocalDate data3 = LocalDate.of(2017, 3, 20);
    LocalDate data4 = LocalDate.of(2017, 12, 20);
    ValueDerivatives computed = provider.alphaAdjoint(data1, data2, data3, data4);
    ValueDerivatives expected = HullWhiteOneFactorPiecewiseConstantInterestRateModel.DEFAULT.alphaAdjoint(PARAMETERS,
        ACT_360.relativeYearFraction(VALUATION, data1), ACT_360.relativeYearFraction(VALUATION, data2),
        ACT_360.relativeYearFraction(VALUATION, data3), ACT_360.relativeYearFraction(VALUATION, data4));
    assertEquals(computed, expected);
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    HullWhiteOneFactorPiecewiseConstantSwaptionProvider test1 =
        HullWhiteOneFactorPiecewiseConstantSwaptionProvider.of(PARAMETERS, ACT_360, VALUATION);
    coverImmutableBean(test1);
    HullWhiteOneFactorPiecewiseConstantParameters params = HullWhiteOneFactorPiecewiseConstantParameters.of(
        0.02, DoubleArray.of(0.01, 0.011, 0.014), DoubleArray.of(0.5, 5.0));
    HullWhiteOneFactorPiecewiseConstantSwaptionProvider test2 =
        HullWhiteOneFactorPiecewiseConstantSwaptionProvider.of(params, ACT_ACT_ISDA, LocalDate.of(2015, 3, 14));
    coverBeanEquals(test1, test2);
  }

  public void test_serialization() {
    HullWhiteOneFactorPiecewiseConstantSwaptionProvider test =
        HullWhiteOneFactorPiecewiseConstantSwaptionProvider.of(PARAMETERS, ACT_360, VALUATION);
    assertSerialization(test);
  }

}
