/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.calibration;

import static com.opengamma.strata.basics.currency.Currency.EUR;
import static com.opengamma.strata.basics.currency.Currency.USD;
import static com.opengamma.strata.basics.date.DayCounts.ACT_365F;
import static com.opengamma.strata.basics.index.OvernightIndices.USD_FED_FUND;
import static com.opengamma.strata.finance.rate.swap.type.FixedOvernightSwapConventions.USD_FIXED_1Y_FED_FUND_OIS;
import static com.opengamma.strata.finance.rate.deposit.type.TermDepositConventions.USD_DEPOSIT_T;
import static com.opengamma.strata.finance.rate.deposit.type.TermDepositConventions.USD_DEPOSIT_T1;
import static com.opengamma.strata.finance.fx.type.FxSwapConventions.EUR_USD;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.BuySell;
import com.opengamma.strata.basics.Trade;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.FxMatrix;
import com.opengamma.strata.basics.currency.MultiCurrencyAmount;
import com.opengamma.strata.basics.date.DayCount;
import com.opengamma.strata.basics.date.Tenor;
import com.opengamma.strata.basics.index.Index;
import com.opengamma.strata.basics.interpolator.CurveExtrapolator;
import com.opengamma.strata.basics.interpolator.CurveInterpolator;
import com.opengamma.strata.basics.market.ImmutableObservableValues;
import com.opengamma.strata.basics.market.ObservableKey;
import com.opengamma.strata.collect.id.StandardId;
import com.opengamma.strata.collect.timeseries.LocalDateDoubleTimeSeries;
import com.opengamma.strata.finance.fx.FxSwapTrade;
import com.opengamma.strata.finance.fx.type.FxSwapTemplate;
import com.opengamma.strata.finance.rate.deposit.TermDepositTrade;
import com.opengamma.strata.finance.rate.deposit.type.TermDepositTemplate;
import com.opengamma.strata.finance.rate.swap.SwapTrade;
import com.opengamma.strata.finance.rate.swap.type.FixedOvernightSwapTemplate;
import com.opengamma.strata.market.curve.CurveGroupName;
import com.opengamma.strata.market.curve.CurveName;
import com.opengamma.strata.market.curve.definition.CurveGroupDefinition;
import com.opengamma.strata.market.curve.definition.CurveNode;
import com.opengamma.strata.market.curve.definition.FixedOvernightSwapCurveNode;
import com.opengamma.strata.market.curve.definition.FxSwapCurveNode;
import com.opengamma.strata.market.curve.definition.InterpolatedNodalCurveDefinition;
import com.opengamma.strata.market.curve.definition.TermDepositCurveNode;
import com.opengamma.strata.market.key.QuoteKey;
import com.opengamma.strata.market.sensitivity.CurveCurrencyParameterSensitivities;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.market.value.ValueType;
import com.opengamma.strata.math.impl.interpolation.FlatExtrapolator1D;
import com.opengamma.strata.math.impl.interpolation.LinearInterpolator1D;
import com.opengamma.strata.pricer.fx.DiscountingFxSwapProductPricer;
import com.opengamma.strata.pricer.rate.ImmutableRatesProvider;
import com.opengamma.strata.pricer.rate.deposit.DiscountingTermDepositProductPricer;
import com.opengamma.strata.pricer.rate.swap.DiscountingSwapProductPricer;
import com.opengamma.strata.pricer.sensitivity.MarketQuoteSensitivityCalculator;

/**
 * Test for curve calibration in USD and EUR.
 * The USD curve is obtained by OIS and the EUR one by FX Swaps from USD.
 */
@Test
public class CalibrationZeroRateUsdEur2OisFxTest {

  private static final LocalDate VALUATION_DATE = LocalDate.of(2015, 11, 2);

  private static final CurveInterpolator INTERPOLATOR_LINEAR = new LinearInterpolator1D();
  private static final CurveExtrapolator EXTRAPOLATOR_FLAT = new FlatExtrapolator1D();
  private static final DayCount CURVE_DC = ACT_365F;

  private static final String SCHEME = "CALIBRATION";

  /** Curve names */
  private static final String USD_DSCON_STR = "USD-DSCON-OIS";
  private static final CurveName USD_DSCON_CURVE_NAME = CurveName.of(USD_DSCON_STR);
  private static final String EUR_DSC_STR = "EUR-DSC-FX";
  private static final CurveName EUR_DSC_CURVE_NAME = CurveName.of(EUR_DSC_STR);
  /** Curves associations to currencies and indices. */
  private static final Map<CurveName, Currency> DSC_NAMES = new HashMap<>();
  private static final Map<CurveName, Set<Index>> IDX_NAMES = new HashMap<>();
  private static final Map<Index, LocalDateDoubleTimeSeries> TS = new HashMap<>();
  static {
    DSC_NAMES.put(USD_DSCON_CURVE_NAME, USD);
    Set<Index> usdFedFundSet = new HashSet<>();
    usdFedFundSet.add(USD_FED_FUND);
    IDX_NAMES.put(USD_DSCON_CURVE_NAME, usdFedFundSet);
  }

  /** Data FX **/
  private static final double FX_RATE_EUR_USD = 1.10;
  private static final FxMatrix FX_MATRIX = FxMatrix.of(EUR, USD, FX_RATE_EUR_USD);
  private static final String EUR_USD_ID_VALUE = "EUR-USD";
  /** Data for USD-DSCON curve */
  /* Market values */
  private static final double[] USD_DSC_MARKET_QUOTES = new double[] {
    0.0016, 0.0022,
    0.0013, 0.0016, 0.0020, 0.0026, 0.0033,
    0.0039, 0.0053, 0.0066, 0.0090, 0.0111};
  private static final int USD_DSC_NB_NODES = USD_DSC_MARKET_QUOTES.length;
  private static final String[] USD_DSC_ID_VALUE = new String[] {
    "USD-ON", "USD-TN",
    "USD-OIS-1M", "USD-OIS-2M", "USD-OIS-3M", "USD-OIS-6M", "USD-OIS-9M",
    "USD-OIS-1Y", "USD-OIS-18M", "USD-OIS-2Y", "USD-OIS-3Y", "USD-OIS-4Y"};
  /* Nodes */
  private static final CurveNode[] USD_DSC_NODES = new CurveNode[USD_DSC_NB_NODES];
  /* Tenors */
  private static final int[] USD_DSC_DEPO_OFFSET = new int[] {0, 1 };
  private static final int USD_DSC_NB_DEPO_NODES = USD_DSC_DEPO_OFFSET.length;
  private static final Period[] USD_DSC_OIS_TENORS = new Period[] {
    Period.ofMonths(1), Period.ofMonths(2), Period.ofMonths(3), Period.ofMonths(6), Period.ofMonths(9),
    Period.ofYears(1), Period.ofMonths(18), Period.ofYears(2), Period.ofYears(3), Period.ofYears(4) };
  private static final int USD_DSC_NB_OIS_NODES = USD_DSC_OIS_TENORS.length;
  static {
    USD_DSC_NODES[0] = TermDepositCurveNode.of(TermDepositTemplate.of(Period.ofDays(1), USD_DEPOSIT_T),
        QuoteKey.of(StandardId.of(SCHEME, USD_DSC_ID_VALUE[0])));
    USD_DSC_NODES[1] = TermDepositCurveNode.of(TermDepositTemplate.of(Period.ofDays(1), USD_DEPOSIT_T1),
        QuoteKey.of(StandardId.of(SCHEME, USD_DSC_ID_VALUE[1])));
    for (int i = 0; i < USD_DSC_NB_OIS_NODES; i++) {
      USD_DSC_NODES[USD_DSC_NB_DEPO_NODES + i] = FixedOvernightSwapCurveNode.of(
          FixedOvernightSwapTemplate.of(Period.ZERO, Tenor.of(USD_DSC_OIS_TENORS[i]), USD_FIXED_1Y_FED_FUND_OIS),
          QuoteKey.of(StandardId.of(SCHEME, USD_DSC_ID_VALUE[USD_DSC_NB_DEPO_NODES + i])));
    }
  }
  /** Data for EUR-DSC curve */
  /* Market values */
  private static final double[] EUR_DSC_MARKET_QUOTES = new double[] {
    0.0004, 0.0012, 0.0019, 0.0043, 0.0074,
    0.0109, 0.0193, 0.0294, 0.0519, 0.0757};
  private static final int EUR_DSC_NB_NODES = EUR_DSC_MARKET_QUOTES.length;
  private static final String[] EUR_DSC_ID_VALUE = new String[] {
    "EUR-USD-FX-1M", "EUR-USD-FX-2M", "EUR-USD-FX-3M", "EUR-USD-FX-6M", "EUR-USD-FX-9M",
    "EUR-USD-FX-1Y", "EUR-USD-FX-18M", "EUR-USD-FX-2Y", "EUR-USD-FX-3Y", "EUR-USD-FX-4Y"};
  /* Nodes */
  private static final CurveNode[] EUR_DSC_NODES = new CurveNode[EUR_DSC_NB_NODES];
  /* Tenors */
  private static final Period[] EUR_DSC_FX_TENORS = new Period[] {
      Period.ofMonths(1), Period.ofMonths(2), Period.ofMonths(3), Period.ofMonths(6), Period.ofMonths(9),
      Period.ofYears(1), Period.ofMonths(18), Period.ofYears(2), Period.ofYears(3), Period.ofYears(4)};
  private static final int EUR_DSC_NB_FX_NODES = EUR_DSC_FX_TENORS.length;
  static {
    for (int i = 0; i < EUR_DSC_NB_FX_NODES; i++) {
      EUR_DSC_NODES[i] = FxSwapCurveNode.of(FxSwapTemplate.of(EUR_DSC_FX_TENORS[i], EUR_USD), 
          QuoteKey.of(StandardId.of(SCHEME, EUR_USD_ID_VALUE)),
          QuoteKey.of(StandardId.of(SCHEME, EUR_DSC_ID_VALUE[i])));
    }
  }

  /** All quotes for the curve calibration */
  private static final ImmutableObservableValues ALL_QUOTES;
  static {
    Map<ObservableKey, Double> map = new HashMap<>();
    for (int i = 0; i < USD_DSC_NB_NODES; i++) {
      map.put(QuoteKey.of(StandardId.of(SCHEME, USD_DSC_ID_VALUE[i])), USD_DSC_MARKET_QUOTES[i]);
    }
    for (int i = 0; i < EUR_DSC_NB_NODES; i++) {
      map.put(QuoteKey.of(StandardId.of(SCHEME, EUR_DSC_ID_VALUE[i])), EUR_DSC_MARKET_QUOTES[i]);
    }
    map.put(QuoteKey.of(StandardId.of(SCHEME, EUR_USD_ID_VALUE)), FX_RATE_EUR_USD);
    ALL_QUOTES = ImmutableObservableValues.of(map);
  }

  private static final DiscountingSwapProductPricer SWAP_PRICER =
      DiscountingSwapProductPricer.DEFAULT;
  private static final DiscountingTermDepositProductPricer DEPO_PRICER =
      DiscountingTermDepositProductPricer.DEFAULT;
  private static final DiscountingFxSwapProductPricer FX_PRICER =
      DiscountingFxSwapProductPricer.DEFAULT;
  private static final MarketQuoteSensitivityCalculator MQC = MarketQuoteSensitivityCalculator.DEFAULT;
  
  private static final CalibrationMeasures CALIBRATION_MEASURES = CalibrationMeasures.DEFAULT;
  private static final CurveCalibrator CALIBRATOR = CurveCalibrator.of(1e-9, 1e-9, 100, CALIBRATION_MEASURES);

  // Constants
  private static final double TOLERANCE_PV = 1.0E-6;
  private static final double TOLERANCE_PV_DELTA = 1.0E+3;

  private static final CurveGroupName CURVE_GROUP_NAME = CurveGroupName.of("USD-DSCON-EUR-DSC");
  private static final InterpolatedNodalCurveDefinition USD_DSC_CURVE_DEFN =
      InterpolatedNodalCurveDefinition.builder()
          .name(USD_DSCON_CURVE_NAME)
          .xValueType(ValueType.YEAR_FRACTION)
          .yValueType(ValueType.ZERO_RATE)
          .dayCount(CURVE_DC)
          .interpolator(INTERPOLATOR_LINEAR)
          .extrapolatorLeft(EXTRAPOLATOR_FLAT)
          .extrapolatorRight(EXTRAPOLATOR_FLAT)
          .nodes(USD_DSC_NODES).build();
  private static final InterpolatedNodalCurveDefinition EUR_DSC_CURVE_DEFN =
      InterpolatedNodalCurveDefinition.builder()
          .name(EUR_DSC_CURVE_NAME)
          .xValueType(ValueType.YEAR_FRACTION)
          .yValueType(ValueType.ZERO_RATE)
          .dayCount(CURVE_DC)
          .interpolator(INTERPOLATOR_LINEAR)
          .extrapolatorLeft(EXTRAPOLATOR_FLAT)
          .extrapolatorRight(EXTRAPOLATOR_FLAT)
          .nodes(EUR_DSC_NODES).build();
  private static final CurveGroupDefinition CURVE_GROUP_CONFIG =
      CurveGroupDefinition.builder()
          .name(CURVE_GROUP_NAME)
          .addCurve(USD_DSC_CURVE_DEFN, USD, USD_FED_FUND)
          .addDiscountCurve(EUR_DSC_CURVE_DEFN, EUR).build();
  
  //-------------------------------------------------------------------------
  public void calibration_present_value_oneGroup() {
    ImmutableRatesProvider result =
        CALIBRATOR.calibrate(CURVE_GROUP_CONFIG, VALUATION_DATE, ALL_QUOTES, TS, FX_MATRIX);
    assertPresentValue(result);
  }
  
  private void assertPresentValue(ImmutableRatesProvider result) {
    // Test PV USD;
    List<Trade> usdTrades = new ArrayList<>();
    for (int i = 0; i < USD_DSC_NODES.length; i++) {
      usdTrades.add(USD_DSC_NODES[i].trade(VALUATION_DATE, ALL_QUOTES));
    }
    // Depo
    for (int i = 0; i < USD_DSC_NB_DEPO_NODES; i++) {
      CurrencyAmount pvDep = DEPO_PRICER
          .presentValue(((TermDepositTrade) usdTrades.get(i)).getProduct(), result);
      assertEquals(pvDep.getAmount(), 0.0, TOLERANCE_PV);
    }
    // OIS
    for (int i = 0; i < USD_DSC_NB_OIS_NODES; i++) {
      MultiCurrencyAmount pvOis = SWAP_PRICER
          .presentValue(((SwapTrade) usdTrades.get(USD_DSC_NB_DEPO_NODES + i)).getProduct(), result);
      assertEquals(pvOis.getAmount(USD).getAmount(), 0.0, TOLERANCE_PV);
    }
    // Test PV EUR;
    List<Trade> eurTrades = new ArrayList<>();
    for (int i = 0; i < EUR_DSC_NODES.length; i++) {
      eurTrades.add(EUR_DSC_NODES[i].trade(VALUATION_DATE, ALL_QUOTES));
    }
    // Depo
    for (int i = 0; i < EUR_DSC_NB_FX_NODES; i++) {
      MultiCurrencyAmount pvFx = FX_PRICER
          .presentValue(((FxSwapTrade) eurTrades.get(i)).getProduct(), result);
      assertEquals(pvFx.convertedTo(USD, result).getAmount(), 0.0, TOLERANCE_PV);
    }
  }
  
  public void calibration_market_quote_sensitivity_one_group() {
    double shift = 1.0E-6;
    Function<ImmutableObservableValues, ImmutableRatesProvider> f =
        (ov) -> CALIBRATOR.calibrate(CURVE_GROUP_CONFIG, VALUATION_DATE, ov, TS, FX_MATRIX);
    calibration_market_quote_sensitivity_check(f, shift);
  }

  private void calibration_market_quote_sensitivity_check(
      Function<ImmutableObservableValues, ImmutableRatesProvider> calibrator,
      double shift) {
    double notional = 100_000_000.0;
    double fx = 1.1111;
    double fxPts = 0.0012;
    FxSwapTrade trade = EUR_USD
        .toTrade(VALUATION_DATE, Period.ofWeeks(6), Period.ofMonths(5), BuySell.BUY, notional, fx, fxPts);
    ImmutableRatesProvider result =
        CALIBRATOR.calibrate(CURVE_GROUP_CONFIG, VALUATION_DATE, ALL_QUOTES, TS, FX_MATRIX);
    PointSensitivities pts = FX_PRICER.presentValueSensitivity(trade.getProduct(), result);
    CurveCurrencyParameterSensitivities ps = result.curveParameterSensitivity(pts);
    CurveCurrencyParameterSensitivities mqs = MQC.sensitivity(ps, result);
    double pvUsd = FX_PRICER.presentValue(trade.getProduct(), result).getAmount(USD).getAmount();
    double pvEur = FX_PRICER.presentValue(trade.getProduct(), result).getAmount(EUR).getAmount();
    double[] mqsUsd1Computed = mqs.getSensitivity(USD_DSCON_CURVE_NAME, USD).getSensitivity().toArray();
    for (int i = 0; i < USD_DSC_NB_NODES; i++) {
      Map<ObservableKey, Double> map = new HashMap<>(ALL_QUOTES.getValues());
      map.put(QuoteKey.of(StandardId.of(SCHEME, USD_DSC_ID_VALUE[i])), USD_DSC_MARKET_QUOTES[i] + shift);
      ImmutableObservableValues ov = ImmutableObservableValues.of(map);
      ImmutableRatesProvider rpShifted = calibrator.apply(ov);
      double pvS = FX_PRICER.presentValue(trade.getProduct(), rpShifted).getAmount(USD).getAmount();
      assertEquals(mqsUsd1Computed[i], (pvS - pvUsd) / shift, TOLERANCE_PV_DELTA);
    }
    double[] mqsUsd2Computed = mqs.getSensitivity(USD_DSCON_CURVE_NAME, EUR).getSensitivity().toArray();
    for (int i = 0; i < USD_DSC_NB_NODES; i++) {
      Map<ObservableKey, Double> map = new HashMap<>(ALL_QUOTES.getValues());
      map.put(QuoteKey.of(StandardId.of(SCHEME, USD_DSC_ID_VALUE[i])), USD_DSC_MARKET_QUOTES[i] + shift);
      ImmutableObservableValues ov = ImmutableObservableValues.of(map);
      ImmutableRatesProvider rpShifted = calibrator.apply(ov);
      double pvS = FX_PRICER.presentValue(trade.getProduct(), rpShifted).getAmount(EUR).getAmount();
      assertEquals(mqsUsd2Computed[i], (pvS - pvEur) / shift, TOLERANCE_PV_DELTA);
    }
    double[] mqsEur1Computed = mqs.getSensitivity(EUR_DSC_CURVE_NAME, USD).getSensitivity().toArray();
    for (int i = 0; i < EUR_DSC_NB_NODES; i++) {
      assertEquals(mqsEur1Computed[i], 0.0 , TOLERANCE_PV_DELTA);      
    }
    double[] mqsEur2Computed = mqs.getSensitivity(EUR_DSC_CURVE_NAME, EUR).getSensitivity().toArray();
    for (int i = 0; i < EUR_DSC_NB_NODES; i++) {
      Map<ObservableKey, Double> map = new HashMap<>(ALL_QUOTES.getValues());
      map.put(QuoteKey.of(StandardId.of(SCHEME, EUR_DSC_ID_VALUE[i])), EUR_DSC_MARKET_QUOTES[i] + shift);
      ImmutableObservableValues ov = ImmutableObservableValues.of(map);
      ImmutableRatesProvider rpShifted = calibrator.apply(ov);
      double pvS = FX_PRICER.presentValue(trade.getProduct(), rpShifted).getAmount(EUR).getAmount();
      assertEquals(mqsEur2Computed[i], (pvS - pvEur) / shift, TOLERANCE_PV_DELTA, "Node " + i);
    }
  }

}
