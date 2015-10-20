/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.rate.future;

import static com.opengamma.strata.basics.index.IborIndices.USD_LIBOR_3M;
import static com.opengamma.strata.collect.TestHelper.assertSerialization;
import static com.opengamma.strata.collect.TestHelper.assertThrowsIllegalArg;
import static com.opengamma.strata.collect.TestHelper.coverBeanEquals;
import static com.opengamma.strata.collect.TestHelper.coverImmutableBean;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjuster;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.date.QuarterlyImmDateAdjuster;

/**
 * Tests {@link IborFutureTemplate}.
 */
@Test
public class IborFutureTemplateTest {

  private static final TemporalAdjuster QUARTERLY_IMM = QuarterlyImmDateAdjuster.DEFAULT;
  private static final IborFutureConvention CONVENTION = IborFutureConvention.of(USD_LIBOR_3M, QUARTERLY_IMM);
  private static final Period PERIOD_TO_START = Period.ofMonths(2);
  private static final int NUMBER = 2;

  public void test_of() {
    IborFutureTemplate test = IborFutureTemplate.of(PERIOD_TO_START, NUMBER, CONVENTION);
    assertEquals(test.getPeriodToStart(), PERIOD_TO_START);
    assertEquals(test.getNumber(), NUMBER);
    assertEquals(test.getConvention(), CONVENTION);
  }

  public void test_builder_insufficientInfo() {
    assertThrowsIllegalArg(() -> IborFutureTemplate.builder().convention(CONVENTION).build());
    assertThrowsIllegalArg(() -> IborFutureTemplate.builder().periodToStart(PERIOD_TO_START).build());
    assertThrowsIllegalArg(() -> IborFutureTemplate.builder().number(NUMBER).build());
    assertThrowsIllegalArg(() -> IborFutureTemplate.builder().periodToStart(PERIOD_TO_START).number(NUMBER).build());
    assertThrowsIllegalArg(() -> IborFutureTemplate.builder().number(NUMBER).convention(CONVENTION).build());
  }

  //-------------------------------------------------------------------------
  public void test_to_trade() {
    IborFutureTemplate base = IborFutureTemplate.of(PERIOD_TO_START, NUMBER, CONVENTION);
    LocalDate date = LocalDate.of(2015, 10, 20);
    long quantity = 3;
    double price = 0.99;
    double notional = 100.0;
    IborFutureTrade trade = base.toTrade(date, quantity, notional, price);
    IborFutureTrade expected = CONVENTION.toTrade(date, PERIOD_TO_START, NUMBER, quantity, notional, price);
    assertEquals(trade, expected);
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    IborFutureTemplate test = IborFutureTemplate.of(PERIOD_TO_START, NUMBER, CONVENTION);
    coverImmutableBean(test);
    IborFutureTemplate test2 =
        IborFutureTemplate.builder().periodToStart(PERIOD_TO_START).number(NUMBER).convention(CONVENTION).build();
    coverBeanEquals(test, test2);
  }

  public void test_serialization() {
    IborFutureTemplate test = IborFutureTemplate.of(PERIOD_TO_START, NUMBER, CONVENTION);
    assertSerialization(test);
  }

}
