/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.rate.future;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

import org.testng.annotations.Test;

/**
 * Tests {@link IborFutureConvention}.
 */
public class IborFutureConventionTest {
  
  /* Third Wednesday. */
  private static final TemporalAdjuster THIRD_WEDNESDAY = TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.WEDNESDAY);
  
  // QuarterlyImmRollDateAdjuster --> com.opengamma.strata.basics.date?
  // MonthlyImmRollDateAdjuster
  
  @Test
  public void f() {
  }
  
}
