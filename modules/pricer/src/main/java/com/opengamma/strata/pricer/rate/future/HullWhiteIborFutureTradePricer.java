/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.future;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.finance.rate.future.IborFuture;
import com.opengamma.strata.finance.rate.future.IborFutureTrade;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.pricer.rate.RatesProvider;

/**
 * Pricer for for Ibor future trades.
 * <p>
 * This function provides the ability to price a {@link IborFutureTrade} based on Hull-White one-factor model with 
 * piecewise constant volatility.  
 * <p> 
 * Reference: Henrard M., Eurodollar Futures and Options: Convexity Adjustment in HJM One-Factor Model. March 2005.
 * Available at <a href="http://ssrn.com/abstract=682343">http://ssrn.com/abstract=682343</a>
 */
public class HullWhiteIborFutureTradePricer
    extends AbstractIborFutureTradePricer {

  /**
   * Default implementation.
   */
  public static final HullWhiteIborFutureTradePricer DEFAULT =
      new HullWhiteIborFutureTradePricer(HullWhiteIborFutureProductPricer.DEFAULT);

  /**
   * Underlying pricer.
   */
  private final HullWhiteIborFutureProductPricer productPricer;

  /**
   * Creates an instance.
   * 
   * @param productPricer  the pricer for {@link IborFuture}
   */
  public HullWhiteIborFutureTradePricer(HullWhiteIborFutureProductPricer productPricer) {
    this.productPricer = ArgChecker.notNull(productPricer, "productPricer");
  }

  //-------------------------------------------------------------------------
  @Override
  protected HullWhiteIborFutureProductPricer getProductPricer() {
    return productPricer;
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the price of the Ibor future trade.
   * <p>
   * The price of the trade is the price on the valuation date.
   * 
   * @param trade  the trade to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the future convexity factor provider
   * @return the price of the trade, in decimal form
   */
  public double price(
      IborFutureTrade trade,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider hwProvider) {
    return productPricer.price(trade.getSecurity().getProduct(), ratesProvider, hwProvider);
  }

  /**
   * Calculates the present value of the Ibor future trade.
   * <p>
   * The present value of the product is the value on the valuation date.
   * 
   * @param trade  the trade to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the future convexity factor provider
   * @param lastMarginPrice  the last price used in margining. If the valuation is done on the trade date, the trade 
   * price will be used as a reference price; if not, the last margin price will be used.
   * @return the present value
   */
  public CurrencyAmount presentValue(
      IborFutureTrade trade,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider hwProvider,
      double lastMarginPrice) {
    double referencePrice = referencePrice(trade, ratesProvider.getValuationDate(), lastMarginPrice);
    double price = price(trade, ratesProvider, hwProvider);
    return presentValue(trade, price, referencePrice);
  }

  /**
   * Calculates the present value sensitivity of the Ibor future trade.
   * <p>
   * The present value sensitivity of the trade is the sensitivity of the present value to
   * the underlying curves.
   * 
   * @param trade  the trade to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the future convexity factor provider
   * @return the present value curve sensitivity of the trade
   */
  public PointSensitivities presentValueSensitivity(
      IborFutureTrade trade,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider hwProvider) {
    IborFuture product = trade.getSecurity().getProduct();
    PointSensitivities priceSensi = productPricer.priceSensitivity(product, ratesProvider, hwProvider);
    PointSensitivities marginIndexSensi = productPricer.marginIndexSensitivity(product, priceSensi);
    return marginIndexSensi.multipliedBy(trade.getQuantity());
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the par spread of the Ibor future trade.
   * <p>
   * The par spread is defined in the following way. When the reference price (or market quote)
   * is increased by the par spread, the present value of the trade is zero.
   * 
   * @param trade  the trade to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the future convexity factor provider
   * @param lastMarginPrice  the last price used in margining. If the valuation is done on the trade date, the trade 
   * price will be used as a reference price; if not, the last margin price will be used.
   * @return the par spread.
   */
  public double parSpread(
      IborFutureTrade trade,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider hwProvider,
      double lastMarginPrice) {
    double referencePrice = referencePrice(trade, ratesProvider.getValuationDate(), lastMarginPrice);
    return price(trade, ratesProvider, hwProvider) - referencePrice;
  }

  /**
   * Calculates the par spread sensitivity of the Ibor future trade.
   * <p>
   * The par spread sensitivity of the trade is the sensitivity of the par spread to
   * the underlying curves.
   * 
   * @param trade  the trade to price
   * @param ratesProvider  the rates provider
   * @param hwProvider  the future convexity factor provider
   * @return the par spread curve sensitivity of the trade
   */
  public PointSensitivities parSpreadSensitivity(
      IborFutureTrade trade,
      RatesProvider ratesProvider,
      HullWhiteOneFactorPiecewiseConstantConvexityFactorProvider hwProvider) {
    return productPricer.priceSensitivity(trade.getSecurity().getProduct(), ratesProvider, hwProvider);
  }

}
