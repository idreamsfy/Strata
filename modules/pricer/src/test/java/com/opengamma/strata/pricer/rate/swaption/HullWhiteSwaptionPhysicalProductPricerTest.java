/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.swaption;

import static com.opengamma.strata.basics.LongShort.LONG;
import static com.opengamma.strata.basics.LongShort.SHORT;
import static com.opengamma.strata.basics.PayReceive.PAY;
import static com.opengamma.strata.basics.PayReceive.RECEIVE;
import static com.opengamma.strata.basics.currency.Currency.EUR;
import static com.opengamma.strata.basics.date.BusinessDayConventions.MODIFIED_FOLLOWING;
import static com.opengamma.strata.basics.date.DayCounts.THIRTY_U_360;
import static com.opengamma.strata.basics.index.IborIndices.EUR_EURIBOR_6M;
import static com.opengamma.strata.basics.schedule.Frequency.P12M;
import static com.opengamma.strata.basics.schedule.Frequency.P6M;
import static com.opengamma.strata.collect.TestHelper.dateUtc;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.LongShort;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.date.AdjustableDate;
import com.opengamma.strata.basics.date.BusinessDayAdjustment;
import com.opengamma.strata.basics.date.DaysAdjustment;
import com.opengamma.strata.basics.date.HolidayCalendar;
import com.opengamma.strata.basics.date.HolidayCalendars;
import com.opengamma.strata.basics.schedule.PeriodicSchedule;
import com.opengamma.strata.basics.schedule.RollConventions;
import com.opengamma.strata.basics.schedule.StubConvention;
import com.opengamma.strata.basics.value.ValueSchedule;
import com.opengamma.strata.finance.rate.swap.FixedRateCalculation;
import com.opengamma.strata.finance.rate.swap.IborRateCalculation;
import com.opengamma.strata.finance.rate.swap.NotionalSchedule;
import com.opengamma.strata.finance.rate.swap.PaymentSchedule;
import com.opengamma.strata.finance.rate.swap.RateCalculationSwapLeg;
import com.opengamma.strata.finance.rate.swap.Swap;
import com.opengamma.strata.finance.rate.swap.SwapLeg;
import com.opengamma.strata.finance.rate.swaption.CashSettlement;
import com.opengamma.strata.finance.rate.swaption.CashSettlementMethod;
import com.opengamma.strata.finance.rate.swaption.PhysicalSettlement;
import com.opengamma.strata.finance.rate.swaption.Swaption;
import com.opengamma.strata.pricer.rate.ImmutableRatesProvider;
import com.opengamma.strata.pricer.rate.future.HullWhiteIborFutureDataSet;
import com.opengamma.strata.pricer.rate.future.HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider;

/**
 * Test {@link HullWhiteSwaptionPhysicalProductPricer}.
 */
@Test
public class HullWhiteSwaptionPhysicalProductPricerTest {

  private static final LocalDate VALUATION = LocalDate.of(2011, 7, 7);
  private static final HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider HW_PROVIDER =
      HullWhiteIborFutureDataSet.createHullWhiteProvider(VALUATION);
  private static final ImmutableRatesProvider RATE_PROVIDER = HullWhiteIborFutureDataSet.createRatesProvider(VALUATION);

  private static final ZonedDateTime MATURITY = dateUtc(2016, 7, 7);
  private static final HolidayCalendar CALENDAR = HolidayCalendars.SAT_SUN;
  private static final BusinessDayAdjustment BDA_MF = BusinessDayAdjustment.of(MODIFIED_FOLLOWING, CALENDAR);
  private static final LocalDate SETTLE = BDA_MF.adjust(CALENDAR.shift(MATURITY.toLocalDate(), 2));
  private static final double NOTIONAL = 100000000; //100m
  private static final int TENOR_YEAR = 5;
  private static final LocalDate END = SETTLE.plusYears(TENOR_YEAR);
  private static final double RATE = 0.0175;
  private static final PeriodicSchedule PERIOD_FIXED = PeriodicSchedule.builder()
      .startDate(SETTLE)
      .endDate(END)
      .frequency(P12M)
      .businessDayAdjustment(BDA_MF)
      .stubConvention(StubConvention.SHORT_FINAL)
      .rollConvention(RollConventions.EOM)
      .build();
  private static final PaymentSchedule PAYMENT_FIXED = PaymentSchedule.builder()
      .paymentFrequency(P12M)
      .paymentDateOffset(DaysAdjustment.NONE)
      .build();
  private static final FixedRateCalculation RATE_FIXED = FixedRateCalculation.builder()
      .dayCount(THIRTY_U_360)
      .rate(ValueSchedule.of(RATE))
      .build();
  private static final PeriodicSchedule PERIOD_IBOR = PeriodicSchedule.builder()
      .startDate(SETTLE)
      .endDate(END)
      .frequency(P6M)
      .businessDayAdjustment(BDA_MF)
      .stubConvention(StubConvention.SHORT_FINAL)
      .rollConvention(RollConventions.EOM)
      .build();
  private static final PaymentSchedule PAYMENT_IBOR = PaymentSchedule.builder()
      .paymentFrequency(P6M)
      .paymentDateOffset(DaysAdjustment.NONE)
      .build();
  private static final IborRateCalculation RATE_IBOR = IborRateCalculation.builder()
      .index(EUR_EURIBOR_6M)
      .fixingDateOffset(DaysAdjustment.ofBusinessDays(-2, CALENDAR, BDA_MF))
      .build();
  private static final SwapLeg FIXED_LEG_REC = RateCalculationSwapLeg.builder()
      .payReceive(RECEIVE)
      .accrualSchedule(PERIOD_FIXED)
      .paymentSchedule(PAYMENT_FIXED)
      .notionalSchedule(NotionalSchedule.of(EUR, NOTIONAL))
      .calculation(RATE_FIXED)
      .build();
  private static final SwapLeg FIXED_LEG_PAY = RateCalculationSwapLeg.builder()
      .payReceive(PAY)
      .accrualSchedule(PERIOD_FIXED)
      .paymentSchedule(PAYMENT_FIXED)
      .notionalSchedule(NotionalSchedule.of(EUR, NOTIONAL))
      .calculation(RATE_FIXED)
      .build();
  private static final SwapLeg IBOR_LEG_REC = RateCalculationSwapLeg.builder()
      .payReceive(RECEIVE)
      .accrualSchedule(PERIOD_IBOR)
      .paymentSchedule(PAYMENT_IBOR)
      .notionalSchedule(NotionalSchedule.of(EUR, NOTIONAL))
      .calculation(RATE_IBOR)
      .build();
  private static final SwapLeg IBOR_LEG_PAY = RateCalculationSwapLeg.builder()
      .payReceive(PAY)
      .accrualSchedule(PERIOD_IBOR)
      .paymentSchedule(PAYMENT_IBOR)
      .notionalSchedule(NotionalSchedule.of(EUR, NOTIONAL))
      .calculation(RATE_IBOR)
      .build();
  private static final Swap SWAP_REC = Swap.of(FIXED_LEG_REC, IBOR_LEG_PAY);
  private static final Swap SWAP_PAY = Swap.of(FIXED_LEG_PAY, IBOR_LEG_REC);
  private static final CashSettlement PAR_YIELD = CashSettlement.builder()
      .cashSettlementMethod(CashSettlementMethod.PAR_YIELD)
      .settlementDate(SETTLE)
      .build();
  private static final Swaption SWAPTION_REC_LONG = Swaption
      .builder()
      .expiryDate(AdjustableDate.of(MATURITY.toLocalDate(), BDA_MF))
      .expiryTime(MATURITY.toLocalTime())
      .expiryZone(MATURITY.getZone())
      .swaptionSettlement(PAR_YIELD)
      .longShort(LONG)
      .underlying(SWAP_REC)
      .build();
  private static final Swaption SWAPTION_REC_SHORT = Swaption
      .builder()
      .expiryDate(AdjustableDate.of(MATURITY.toLocalDate(), BDA_MF))
      .expiryTime(MATURITY.toLocalTime())
      .expiryZone(MATURITY.getZone())
      .swaptionSettlement(PAR_YIELD)
      .longShort(SHORT)
      .underlying(SWAP_REC)
      .build();
  private static final Swaption SWAPTION_PAY_LONG = Swaption
      .builder()
      .expiryDate(AdjustableDate.of(MATURITY.toLocalDate(), BDA_MF))
      .expiryTime(MATURITY.toLocalTime())
      .expiryZone(MATURITY.getZone())
      .swaptionSettlement(PAR_YIELD)
      .longShort(LONG)
      .underlying(SWAP_PAY)
      .build();
  private static final Swaption SWAPTION_PAY_SHORT = Swaption
      .builder()
      .expiryDate(AdjustableDate.of(MATURITY.toLocalDate(), BDA_MF))
      .expiryTime(MATURITY.toLocalTime())
      .expiryZone(MATURITY.getZone())
      .swaptionSettlement(PAR_YIELD)
      .longShort(SHORT)
      .underlying(SWAP_PAY)
      .build();
  private static final Swaption SWAPTION_PHYS = Swaption.builder()
      .expiryDate(AdjustableDate.of(MATURITY.toLocalDate()))
      .expiryTime(MATURITY.toLocalTime())
      .expiryZone(MATURITY.getZone())
      .longShort(LongShort.LONG)
      .swaptionSettlement(PhysicalSettlement.DEFAULT)
      .underlying(SWAP_REC)
      .build();

  private static final HullWhiteSwaptionPhysicalProductPricer PRICER = HullWhiteSwaptionPhysicalProductPricer.DEFAULT;

  public void test() {
    CurrencyAmount pv = PRICER.presentValue(SWAPTION_PAY_LONG, RATE_PROVIDER, HW_PROVIDER);
    System.out.println(pv);
    // [EUR 4213670.335092038]
    // 
  }
}
