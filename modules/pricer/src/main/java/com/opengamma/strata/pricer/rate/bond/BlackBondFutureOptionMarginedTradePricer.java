/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.bond;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.finance.rate.bond.BondFuture;
import com.opengamma.strata.finance.rate.bond.BondFutureOption;
import com.opengamma.strata.finance.rate.bond.BondFutureOptionTrade;
import com.opengamma.strata.market.sensitivity.BondFutureOptionSensitivity;
import com.opengamma.strata.pricer.rate.LegalEntityDiscountingProvider;

/**
 * Pricer implementation for bond future option.
 * <p>
 * The bond future option is priced based on Black model.
 */
public final class BlackBondFutureOptionMarginedTradePricer extends BondFutureOptionMarginedTradePricer {

  /**
   * Default implementation.
   */
  public static final BlackBondFutureOptionMarginedTradePricer DEFAULT =
      new BlackBondFutureOptionMarginedTradePricer(BlackBondFutureOptionMarginedProductPricer.DEFAULT);

  /**
   * Underlying option pricer.
   */
  private final BlackBondFutureOptionMarginedProductPricer futureOptionPricer;

  /**
   * Creates an instance.
   * 
   * @param futureOptionPricer  the pricer for {@link BondFutureOption}
   */
  public BlackBondFutureOptionMarginedTradePricer(
      BlackBondFutureOptionMarginedProductPricer futureOptionPricer) {
    this.futureOptionPricer = ArgChecker.notNull(futureOptionPricer, "futureOptionPricer");
  }

  //-------------------------------------------------------------------------
  @Override
  public BlackBondFutureOptionMarginedProductPricer getProductPricer() {
    return futureOptionPricer;
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value of the bond future option trade from the underlying future price.
   * <p>
   * The present value of the product is the value on the valuation date.
   * 
   * @param trade  the trade to price
   * @param ratesProvider  the rates provider
   * @param volatilityProvider  the provider of Black volatility
   * @param futurePrice  the price of the underlying future
   * @param lastClosingPrice  the last closing price
   * @return the present value
   */
  public CurrencyAmount presentValue(
      BondFutureOptionTrade trade,
      LegalEntityDiscountingProvider ratesProvider,
      BlackVolatilityBondFutureProvider volatilityProvider,
      double futurePrice,
      double lastClosingPrice) {
    double optionPrice = getProductPricer().price(trade.getProduct(), ratesProvider, volatilityProvider, futurePrice);
    return presentValue(trade, ratesProvider.getValuationDate(), optionPrice, lastClosingPrice);
  }

  //-------------------------------------------------------------------------
  /**
   * Computes the present value sensitivity to the Black volatility used in the pricing.
   * <p>
   * The result is a single sensitivity to the volatility used.
   * The volatility is associated with the expiration/delay/strike/future price key combination.
   * <p>
   * This calculates the underlying future price using the future pricer.
   * 
   * @param futureOptionTrade  the trade to price
   * @param ratesProvider  the rates provider
   * @param volatilityProvider  the provider of Black volatility
   * @return the price sensitivity
   */
  public BondFutureOptionSensitivity presentValueSensitivityBlackVolatility(
      BondFutureOptionTrade futureOptionTrade,
      LegalEntityDiscountingProvider ratesProvider,
      BlackVolatilityBondFutureProvider volatilityProvider) {
    BondFuture future = futureOptionTrade.getProduct().getUnderlying();
    double futurePrice = futureOptionPricer.getFuturePricer().price(future, ratesProvider);
    return presentValueSensitivityBlackVolatility(futureOptionTrade, ratesProvider, volatilityProvider, futurePrice);
  }

  /**
   * Computes the present value sensitivity to the Black volatility used in the pricing
   * based on the price of the underlying future.
   * <p>
   * The result is a single sensitivity to the volatility used.
   * The volatility is associated with the expiration/delay/strike/future price key combination.
   * 
   * @param futureOptionTrade  the trade to price
   * @param ratesProvider  the rates provider
   * @param volatilityProvider  the provider of Black volatility
   * @param futurePrice  the price of the underlying future
   * @return the price sensitivity
   */
  public BondFutureOptionSensitivity presentValueSensitivityBlackVolatility(
      BondFutureOptionTrade futureOptionTrade,
      LegalEntityDiscountingProvider ratesProvider,
      BlackVolatilityBondFutureProvider volatilityProvider,
      double futurePrice) {
    BondFutureOption product = futureOptionTrade.getProduct();
    BondFutureOptionSensitivity priceSensitivity =
        futureOptionPricer.priceSensitivityBlackVolatility(product, ratesProvider, volatilityProvider, futurePrice);
    double factor = futureOptionPricer.marginIndex(product, 1) * futureOptionTrade.getQuantity();
    return priceSensitivity.withSensitivity(priceSensitivity.getSensitivity() * factor);
  }

}
