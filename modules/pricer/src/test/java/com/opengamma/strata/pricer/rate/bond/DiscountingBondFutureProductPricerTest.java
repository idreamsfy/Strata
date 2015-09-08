/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.bond;

import static com.opengamma.strata.basics.currency.Currency.USD;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.time.LocalDate;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.finance.SecurityLink;
import com.opengamma.strata.finance.TradeInfo;
import com.opengamma.strata.finance.rate.bond.BondFuture;
import com.opengamma.strata.finance.rate.bond.FixedCouponBond;
import com.opengamma.strata.finance.rate.bond.FixedCouponBondTrade;
import com.opengamma.strata.market.curve.CurveMetadata;
import com.opengamma.strata.market.sensitivity.CurveCurrencyParameterSensitivities;
import com.opengamma.strata.market.sensitivity.CurveCurrencyParameterSensitivity;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.pricer.datasets.LegalEntityDiscountingProviderDataSets;
import com.opengamma.strata.pricer.rate.LegalEntityDiscountingProvider;
import com.opengamma.strata.pricer.sensitivity.RatesFiniteDifferenceSensitivityCalculator;

/**
 * Test {@link DiscountingBondFutureProductPricer}.
 */
@Test
public class DiscountingBondFutureProductPricerTest {
  // product 
  private static final BondFuture FUTURE_PRODUCT = BondDataSets.FUTURE_PRODUCT;
  private static final LocalDate TRADE_DATE = BondDataSets.TRADE_DATE;
  private static final SecurityLink<FixedCouponBond>[] BOND_SECURITY_LINK = BondDataSets.BOND_SECURITY_LINK.clone();
  private static final Double[] CONVERSION_FACTOR = BondDataSets.CONVERSION_FACTOR.clone();
  // curves
  private static final LegalEntityDiscountingProvider PROVIDER = LegalEntityDiscountingProviderDataSets.ISSUER_REPO_ZERO;
  private static final CurveMetadata METADATA_ISSUER = LegalEntityDiscountingProviderDataSets.META_ZERO_ISSUER;
  private static final CurveMetadata METADATA_REPO = LegalEntityDiscountingProviderDataSets.META_ZERO_REPO;
  // parameters
  private static final double Z_SPREAD = 0.0075;
  private static final int PERIOD_PER_YEAR = 4;
  private static final double TOL = 1.0e-12;
  private static final double EPS = 1.0e-6;
  // pricer
  private static final DiscountingBondFutureProductPricer FUTURE_PRICER = DiscountingBondFutureProductPricer.DEFAULT;
  private static final DiscountingFixedCouponBondTradePricer BOND_PRICER = DiscountingFixedCouponBondTradePricer.DEFAULT;
  private static final RatesFiniteDifferenceSensitivityCalculator FD_CAL = new RatesFiniteDifferenceSensitivityCalculator(EPS);

  public void test_price() {
    double computed = FUTURE_PRICER.price(FUTURE_PRODUCT, PROVIDER);
    TradeInfo tradeInfo = TradeInfo.builder()
        .tradeDate(TRADE_DATE)
        .settlementDate(FUTURE_PRODUCT.getLastDeliveryDate())
        .build();
    FixedCouponBondTrade bondTrade = FixedCouponBondTrade.builder()
        .quantity(1)
        .securityLink(BOND_SECURITY_LINK[0])
        .tradeInfo(tradeInfo)
        .build();
    double dirtyPrice = BOND_PRICER.dirtyPriceFromCurves(bondTrade, PROVIDER);
    double expected = BOND_PRICER.cleanPriceFromDirtyPrice(bondTrade, dirtyPrice) / CONVERSION_FACTOR[0];
    assertEquals(computed, expected, TOL);
  }

  public void test_priceWithZSpread_continuous() {
    double computed = FUTURE_PRICER.priceWithZSpread(FUTURE_PRODUCT, PROVIDER, Z_SPREAD, false, 0);
    TradeInfo tradeInfo = TradeInfo.builder()
        .tradeDate(TRADE_DATE)
        .settlementDate(FUTURE_PRODUCT.getLastDeliveryDate())
        .build();
    FixedCouponBondTrade bondTrade = FixedCouponBondTrade.builder()
        .quantity(1)
        .securityLink(BOND_SECURITY_LINK[0])
        .tradeInfo(tradeInfo)
        .build();
    double dirtyPrice = BOND_PRICER.dirtyPriceFromCurvesWithZSpread(bondTrade, PROVIDER, Z_SPREAD, false, 0);
    double expected = BOND_PRICER.cleanPriceFromDirtyPrice(bondTrade, dirtyPrice) / CONVERSION_FACTOR[0];
    assertEquals(computed, expected, TOL);
  }

  public void test_priceWithZSpread_periodic() {
    double computed = FUTURE_PRICER.priceWithZSpread(FUTURE_PRODUCT, PROVIDER, Z_SPREAD, true, PERIOD_PER_YEAR);
    TradeInfo tradeInfo = TradeInfo.builder()
        .tradeDate(TRADE_DATE)
        .settlementDate(FUTURE_PRODUCT.getLastDeliveryDate())
        .build();
    FixedCouponBondTrade bondTrade = FixedCouponBondTrade.builder()
        .quantity(1)
        .securityLink(BOND_SECURITY_LINK[0])
        .tradeInfo(tradeInfo)
        .build();
    double dirtyPrice = BOND_PRICER.dirtyPriceFromCurvesWithZSpread(
        bondTrade, PROVIDER, Z_SPREAD, true, PERIOD_PER_YEAR);
    double expected = BOND_PRICER.cleanPriceFromDirtyPrice(bondTrade, dirtyPrice) / CONVERSION_FACTOR[0];
    assertEquals(computed, expected, TOL);
  }

  //-------------------------------------------------------------------------
  public void test_priceSensitivity() {
    PointSensitivities point = FUTURE_PRICER.priceSensitivity(FUTURE_PRODUCT, PROVIDER);
    CurveCurrencyParameterSensitivities computed = PROVIDER.curveParameterSensitivity(point);
    CurveCurrencyParameterSensitivities expected = FD_CAL.sensitivity(PROVIDER,
        (p) -> CurrencyAmount.of(USD, FUTURE_PRICER.price(FUTURE_PRODUCT, (p))));
    assertTrue(computed.equalWithTolerance(expected, EPS * 10.0));
  }

  public void test_priceSensitivityWithZSpread_continuous() {
    PointSensitivities point = FUTURE_PRICER.priceSensitivityWithZSpread(
        FUTURE_PRODUCT, PROVIDER, Z_SPREAD, false, 0);
    CurveCurrencyParameterSensitivities computed = PROVIDER.curveParameterSensitivity(point);
    CurveCurrencyParameterSensitivities expected = FD_CAL.sensitivity(PROVIDER,
        (p) -> CurrencyAmount.of(USD, FUTURE_PRICER.priceWithZSpread(FUTURE_PRODUCT, (p), Z_SPREAD, false, 0)));
    assertTrue(computed.equalWithTolerance(expected, EPS * 10.0));
  }

  public void test_priceSensitivityWithZSpread_periodic() {
    PointSensitivities point = FUTURE_PRICER.priceSensitivityWithZSpread(
        FUTURE_PRODUCT, PROVIDER, Z_SPREAD, true, PERIOD_PER_YEAR);
    CurveCurrencyParameterSensitivities computed = PROVIDER.curveParameterSensitivity(point);
    CurveCurrencyParameterSensitivities expected = FD_CAL.sensitivity(PROVIDER, (p) -> CurrencyAmount.of(
        USD, FUTURE_PRICER.priceWithZSpread(FUTURE_PRODUCT, (p), Z_SPREAD, true, PERIOD_PER_YEAR)));
    assertTrue(computed.equalWithTolerance(expected, EPS * 10.0));
  }

  //-------------------------------------------------------------------------
  // regression to 2.x
  public void regression() {
    double price = FUTURE_PRICER.price(FUTURE_PRODUCT, PROVIDER);
    assertEquals(price, 1.2106928633440506, TOL);
    PointSensitivities point = FUTURE_PRICER.priceSensitivity(FUTURE_PRODUCT, PROVIDER);
    CurveCurrencyParameterSensitivities sensiNew = PROVIDER.curveParameterSensitivity(point);
    double[] sensiIssuer = new double[] {-3.940585873921608E-4, -0.004161527192990392, -0.014331606019672717,
      -1.0229665443857998, -4.220553063715371, 0.0 };
    double[] sensiRepo = new double[] {0.14752541809405412, 0.20907575809356016, 0.0, 0.0, 0.0, 0.0 };
    CurveCurrencyParameterSensitivities sensiOld = CurveCurrencyParameterSensitivities
        .of(CurveCurrencyParameterSensitivity.of(METADATA_ISSUER, USD, sensiIssuer));
    sensiOld = sensiOld.combinedWith(CurveCurrencyParameterSensitivity.of(METADATA_REPO, USD, sensiRepo));
    assertTrue(sensiNew.equalWithTolerance(sensiOld, TOL));
  }

  public void regression_withZSpread_continuous() {
    double price = FUTURE_PRICER.priceWithZSpread(FUTURE_PRODUCT, PROVIDER, Z_SPREAD, false, 0);
    assertEquals(price, 1.1718691843665354, TOL);
   // curve parameter sensitivity is not supported for continuous z-spread in 2.x. 
  }

  public void regression_withZSpread_periodic() {
    double price = FUTURE_PRICER.priceWithZSpread(FUTURE_PRODUCT, PROVIDER, Z_SPREAD, true, PERIOD_PER_YEAR);
    assertEquals(price, 1.1720190529653407, TOL);
    PointSensitivities point =
        FUTURE_PRICER.priceSensitivityWithZSpread(FUTURE_PRODUCT, PROVIDER, Z_SPREAD, true, PERIOD_PER_YEAR);
    CurveCurrencyParameterSensitivities sensiNew = PROVIDER.curveParameterSensitivity(point);
    double[] sensiIssuer = new double[] {-3.9201229100932256E-4, -0.0041367134351306374, -0.014173323438217467,
      -0.9886444827927878, -4.07533109609094, 0.0 };
    double[] sensiRepo = new double[] {0.1428352116441475, 0.20242871054203687, 0.0, 0.0, 0.0, 0.0 };
    CurveCurrencyParameterSensitivities sensiOld = CurveCurrencyParameterSensitivities
        .of(CurveCurrencyParameterSensitivity.of(METADATA_ISSUER, USD, sensiIssuer));
    sensiOld = sensiOld.combinedWith(CurveCurrencyParameterSensitivity.of(METADATA_REPO, USD, sensiRepo));
    assertTrue(sensiNew.equalWithTolerance(sensiOld, TOL));
  }

}
