/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.calibration;

import static com.opengamma.strata.basics.currency.Currency.USD;
import static com.opengamma.strata.basics.date.DayCounts.ACT_365F;
import static com.opengamma.strata.basics.index.IborIndices.USD_LIBOR_3M;
import static com.opengamma.strata.product.rate.swap.type.FixedIborSwapConventions.USD_FIXED_6M_LIBOR_3M;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

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
import com.opengamma.strata.basics.market.MarketData;
import com.opengamma.strata.basics.market.ObservableKey;
import com.opengamma.strata.collect.id.StandardId;
import com.opengamma.strata.collect.timeseries.LocalDateDoubleTimeSeries;
import com.opengamma.strata.market.curve.CurveGroupName;
import com.opengamma.strata.market.curve.CurveMetadata;
import com.opengamma.strata.market.curve.CurveName;
import com.opengamma.strata.market.curve.DefaultCurveMetadata;
import com.opengamma.strata.market.curve.definition.CurveGroupDefinition;
import com.opengamma.strata.market.curve.definition.CurveNode;
import com.opengamma.strata.market.curve.definition.FixedIborSwapCurveNode;
import com.opengamma.strata.market.curve.definition.FraCurveNode;
import com.opengamma.strata.market.curve.definition.IborFixingDepositCurveNode;
import com.opengamma.strata.market.curve.definition.InterpolatedNodalCurveDefinition;
import com.opengamma.strata.market.key.QuoteKey;
import com.opengamma.strata.market.value.ValueType;
import com.opengamma.strata.math.impl.interpolation.FlatExtrapolator1D;
import com.opengamma.strata.math.impl.interpolation.LinearInterpolator1D;
import com.opengamma.strata.pricer.rate.ImmutableRatesProvider;
import com.opengamma.strata.pricer.rate.deposit.DiscountingIborFixingDepositProductPricer;
import com.opengamma.strata.pricer.rate.fra.DiscountingFraTradePricer;
import com.opengamma.strata.pricer.rate.swap.DiscountingSwapProductPricer;
import com.opengamma.strata.product.rate.deposit.IborFixingDepositTrade;
import com.opengamma.strata.product.rate.deposit.type.IborFixingDepositTemplate;
import com.opengamma.strata.product.rate.fra.FraTrade;
import com.opengamma.strata.product.rate.fra.type.FraTemplate;
import com.opengamma.strata.product.rate.swap.SwapTrade;
import com.opengamma.strata.product.rate.swap.type.FixedIborSwapTemplate;

/**
 * Test curve calibration
 */
@Test
public class CalibrationDiscountingSimple1Test {

  private static final LocalDate VALUATION_DATE = LocalDate.of(2015, 7, 21);

  private static final CurveInterpolator INTERPOLATOR_LINEAR = new LinearInterpolator1D();
  private static final CurveExtrapolator EXTRAPOLATOR_FLAT = new FlatExtrapolator1D();
  private static final DayCount CURVE_DC = ACT_365F;
  private static final LocalDateDoubleTimeSeries TS_EMTPY = LocalDateDoubleTimeSeries.empty();

  private static final String SCHEME = "CALIBRATION";

  /** Curve name */
  private static final String ALL_NAME = "USD-ALL-FRAIRS3M";
  private static final CurveName ALL_CURVE_NAME = CurveName.of(ALL_NAME);
  /** Curves associations to currencies and indices. */
  private static final Map<CurveName, Currency> DSC_NAMES = new HashMap<>();
  private static final Map<CurveName, Set<Index>> IDX_NAMES = new HashMap<>();
  private static final Set<Index> IBOR_INDICES = new HashSet<>();
  static {
    IBOR_INDICES.add(USD_LIBOR_3M);
  }
  private static final Map<Index, LocalDateDoubleTimeSeries> TS = new HashMap<>();
  static {
    DSC_NAMES.put(ALL_CURVE_NAME, USD);
    IDX_NAMES.put(ALL_CURVE_NAME, IBOR_INDICES);
    TS.put(USD_LIBOR_3M, TS_EMTPY);
  }

  /** Market values for the Fwd 3M USD curve */
  private static final double[] FWD3_MARKET_QUOTES = new double[] {
      0.0420, 0.0420, 0.0420, 0.0420, 0.0430,
      0.0470, 0.0540, 0.0570, 0.0600};
  private static final int FWD3_NB_NODES = FWD3_MARKET_QUOTES.length;
  private static final String[] FWD3_ID_VALUE = new String[] {
      "Fixing", "FRA3Mx6M", "FRA6Mx9M", "IRS1Y", "IRS2Y",
      "IRS3Y", "IRS5Y", "IRS7Y", "IRS10Y"};
  /** Nodes for the Fwd 3M USD curve */
  private static final CurveNode[] ALL_NODES = new CurveNode[FWD3_NB_NODES];
  /** Tenors for the Fwd 3M USD swaps */
  private static final Period[] FWD3_FRA_TENORS = new Period[] { // Period to start
      Period.ofMonths(3), Period.ofMonths(6)};
  private static final int FWD3_NB_FRA_NODES = FWD3_FRA_TENORS.length;
  private static final Period[] FWD3_IRS_TENORS = new Period[] {
      Period.ofYears(1), Period.ofYears(2), Period.ofYears(3), Period.ofYears(5), Period.ofYears(7), Period.ofYears(10)};
  private static final int FWD3_NB_IRS_NODES = FWD3_IRS_TENORS.length;
  static {
    ALL_NODES[0] = IborFixingDepositCurveNode.of(IborFixingDepositTemplate.of(USD_LIBOR_3M),
        QuoteKey.of(StandardId.of(SCHEME, FWD3_ID_VALUE[0])));
    for (int i = 0; i < FWD3_NB_FRA_NODES; i++) {
      ALL_NODES[i + 1] = FraCurveNode.of(FraTemplate.of(FWD3_FRA_TENORS[i], USD_LIBOR_3M),
          QuoteKey.of(StandardId.of(SCHEME, FWD3_ID_VALUE[1])));
    }
    for (int i = 0; i < FWD3_NB_IRS_NODES; i++) {
      ALL_NODES[i + 1 + FWD3_NB_FRA_NODES] = FixedIborSwapCurveNode.of(
          FixedIborSwapTemplate.of(Period.ZERO, Tenor.of(FWD3_IRS_TENORS[i]), USD_FIXED_6M_LIBOR_3M),
          QuoteKey.of(StandardId.of(SCHEME, FWD3_ID_VALUE[i])));
    }
  }

  /** All quotes for the curve calibration */
  private static final MarketData ALL_QUOTES;
  static {
    Map<ObservableKey, Double> map = new HashMap<>();
    for (int i = 0; i < FWD3_NB_NODES; i++) {
      map.put(QuoteKey.of(StandardId.of(SCHEME, FWD3_ID_VALUE[i])), FWD3_MARKET_QUOTES[i]);
    }
    ALL_QUOTES = MarketData.of(map);
  }

  /** All nodes by groups. */
  private static final List<List<CurveNode[]>> CURVES_NODES = new ArrayList<>();
  static {
    List<CurveNode[]> groupNodes = new ArrayList<>();
    groupNodes.add(ALL_NODES);
    CURVES_NODES.add(groupNodes);
  }

  /** All metadata by groups */
  private static final List<List<CurveMetadata>> CURVES_METADATA = new ArrayList<>();
  static {
    List<CurveMetadata> groupMetadata = new ArrayList<>();
    groupMetadata.add(DefaultCurveMetadata.builder().curveName(ALL_CURVE_NAME).xValueType(ValueType.YEAR_FRACTION)
        .yValueType(ValueType.ZERO_RATE).dayCount(CURVE_DC).build());
    CURVES_METADATA.add(groupMetadata);
  }

  private static final DiscountingIborFixingDepositProductPricer FIXING_PRICER =
      DiscountingIborFixingDepositProductPricer.DEFAULT;
  private static final DiscountingFraTradePricer FRA_PRICER =
      DiscountingFraTradePricer.DEFAULT;
  private static final DiscountingSwapProductPricer SWAP_PRICER =
      DiscountingSwapProductPricer.DEFAULT;

  private static final CalibrationMeasures CALIBRATION_MEASURES = CalibrationMeasures.DEFAULT;
  private static final CurveCalibrator CALIBRATOR = CurveCalibrator.of(1e-9, 1e-9, 100, CALIBRATION_MEASURES);

  // Constants
  private static final double TOLERANCE_PV = 1.0E-6;

  /** Test with CurveGroupDefinition */
  private static final String CURVE_GROUP_NAME_STR = "USD-SINGLE-CURVE";
  private static final CurveGroupName CURVE_GROUP_NAME = CurveGroupName.of(CURVE_GROUP_NAME_STR);
  private static final InterpolatedNodalCurveDefinition CURVE_DEFN =
      InterpolatedNodalCurveDefinition.builder()
          .name(ALL_CURVE_NAME)
          .xValueType(ValueType.YEAR_FRACTION)
          .yValueType(ValueType.ZERO_RATE)
          .dayCount(CURVE_DC)
          .interpolator(INTERPOLATOR_LINEAR)
          .extrapolatorLeft(EXTRAPOLATOR_FLAT)
          .extrapolatorRight(EXTRAPOLATOR_FLAT)
          .nodes(ALL_NODES).build();
  private static final CurveGroupDefinition CURVE_GROUP_DEFN =
      CurveGroupDefinition.builder()
          .name(CURVE_GROUP_NAME)
          .addCurve(CURVE_DEFN, USD, USD_LIBOR_3M).build();

  //-------------------------------------------------------------------------
  public void calibration_present_value() {
    ImmutableRatesProvider result2 =
        CALIBRATOR.calibrate(CURVE_GROUP_DEFN, VALUATION_DATE, ALL_QUOTES, TS, FxMatrix.empty());
    // Test PV
    CurveNode[] fwd3Nodes = CURVES_NODES.get(0).get(0);
    List<Trade> fwd3Trades = new ArrayList<>();
    for (int i = 0; i < fwd3Nodes.length; i++) {
      fwd3Trades.add(fwd3Nodes[i].trade(VALUATION_DATE, ALL_QUOTES));
    }
    // Fixing 
    CurrencyAmount pvFixing2 =
        FIXING_PRICER.presentValue(((IborFixingDepositTrade) fwd3Trades.get(0)).getProduct(), result2);
    assertEquals(pvFixing2.getAmount(), 0.0, TOLERANCE_PV);
    // FRA
    for (int i = 0; i < FWD3_NB_FRA_NODES; i++) {
      CurrencyAmount pvFra2 =
          FRA_PRICER.presentValue(((FraTrade) fwd3Trades.get(i + 1)), result2);
      assertEquals(pvFra2.getAmount(), 0.0, TOLERANCE_PV);
    }
    // IRS
    for (int i = 0; i < FWD3_NB_IRS_NODES; i++) {
      MultiCurrencyAmount pvIrs2 = SWAP_PRICER
          .presentValue(((SwapTrade) fwd3Trades.get(i + 1 + FWD3_NB_FRA_NODES)).getProduct(), result2);
      assertEquals(pvIrs2.getAmount(USD).getAmount(), 0.0, TOLERANCE_PV);
    }
  }

  //-------------------------------------------------------------------------
  @Test(enabled = false)
  void performance() {
    long startTime, endTime;
    int nbTests = 100;
    int nbRep = 5;
    int count = 0;

    for (int i = 0; i < nbRep; i++) {
      startTime = System.currentTimeMillis();
      for (int looprep = 0; looprep < nbTests; looprep++) {
        ImmutableRatesProvider result =
            CALIBRATOR.calibrate(CURVE_GROUP_DEFN, VALUATION_DATE, ALL_QUOTES, TS, FxMatrix.empty());
        count += result.getDiscountCurves().size() + result.getIndexCurves().size();
      }
      endTime = System.currentTimeMillis();
      System.out.println("Performance: " + nbTests + " calibrations for 1 curve with 9 nodes in "
          + (endTime - startTime) + " ms.");
    }
    System.out.println("Avoiding hotspot: " + count);
    // Previous run: 290 ms for 100 calibrations (1 curve - 9 nodes)
  }

}
