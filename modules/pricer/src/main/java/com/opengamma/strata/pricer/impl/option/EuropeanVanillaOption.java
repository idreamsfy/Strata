/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.impl.option;

import java.io.Serializable;
import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.basics.PutCall;
import com.opengamma.strata.collect.ArgChecker;

/**
 * Simple representation of a European-style vanilla option.
 */
@BeanDefinition(style = "light")
public final class EuropeanVanillaOption
    implements ImmutableBean, Serializable {

  /**
   * The strike.
   */
  @PropertyDefinition
  private final double strike;
  /**
   * The time to expiry, year fraction.
   */
  @PropertyDefinition(validate = "ArgChecker.notNegative")
  private final double timeToExpiry;
  /**
   * Whether the option is call or put.
   */
  @PropertyDefinition(validate = "notNull")
  private final PutCall putCall;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param strike  the strike
   * @param timeToExpiration  the time to expiration, year fraction
   * @param putCall  whether the option is put or call.
   * @return the option definition
   */
  public static EuropeanVanillaOption of(double strike, double timeToExpiration, PutCall putCall) {
    return new EuropeanVanillaOption(strike, timeToExpiration, putCall);
  }

  //-------------------------------------------------------------------------
  /**
   * Checks if the option is call.
   * 
   * @return true if call, false if put
   */
  public boolean isCall() {
    return putCall == PutCall.CALL;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code EuropeanVanillaOption}.
   */
  private static MetaBean META_BEAN = LightMetaBean.of(EuropeanVanillaOption.class);

  /**
   * The meta-bean for {@code EuropeanVanillaOption}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return META_BEAN;
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private EuropeanVanillaOption(
      double strike,
      double timeToExpiry,
      PutCall putCall) {
    ArgChecker.notNegative(timeToExpiry, "timeToExpiry");
    JodaBeanUtils.notNull(putCall, "putCall");
    this.strike = strike;
    this.timeToExpiry = timeToExpiry;
    this.putCall = putCall;
  }

  @Override
  public MetaBean metaBean() {
    return META_BEAN;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the strike.
   * @return the value of the property
   */
  public double getStrike() {
    return strike;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the time to expiry, year fraction.
   * @return the value of the property
   */
  public double getTimeToExpiry() {
    return timeToExpiry;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether the option is call or put.
   * @return the value of the property, not null
   */
  public PutCall getPutCall() {
    return putCall;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      EuropeanVanillaOption other = (EuropeanVanillaOption) obj;
      return JodaBeanUtils.equal(getStrike(), other.getStrike()) &&
          JodaBeanUtils.equal(getTimeToExpiry(), other.getTimeToExpiry()) &&
          JodaBeanUtils.equal(getPutCall(), other.getPutCall());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getStrike());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTimeToExpiry());
    hash = hash * 31 + JodaBeanUtils.hashCode(getPutCall());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("EuropeanVanillaOption{");
    buf.append("strike").append('=').append(getStrike()).append(',').append(' ');
    buf.append("timeToExpiry").append('=').append(getTimeToExpiry()).append(',').append(' ');
    buf.append("putCall").append('=').append(JodaBeanUtils.toString(getPutCall()));
    buf.append('}');
    return buf.toString();
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
