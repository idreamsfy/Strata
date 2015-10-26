/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.fx;

import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.collect.array.DoubleMatrix;

/**
 * Combines information about a volatility smile expressed in delta form and its sensitivities.
 * <p>
 * This contains a volatility smile expressed in delta form and the bucketed sensitivities
 * of the smile to the data points that were used to construct it.
 */
@BeanDefinition(style = "light")
final class SmileAndBucketedSensitivities
    implements ImmutableBean {
  // NOTE: This class is package scoped, as the Smile data provider API is effectively still in Beta

  /**
   * The smile.
   */
  @PropertyDefinition
  private final SmileDeltaParameters smile;
  /**
   * The sensitivities.
   */
  @PropertyDefinition(validate = "notNull")
  private final DoubleMatrix sensitivities;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param smile  the smile
   * @param sensitivities  the bucketed sensitivities
   * @return the volatility and sensitivities
   */
  public static SmileAndBucketedSensitivities of(SmileDeltaParameters smile, DoubleMatrix sensitivities) {
    return new SmileAndBucketedSensitivities(smile, sensitivities);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SmileAndBucketedSensitivities}.
   */
  private static MetaBean META_BEAN = LightMetaBean.of(SmileAndBucketedSensitivities.class);

  /**
   * The meta-bean for {@code SmileAndBucketedSensitivities}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return META_BEAN;
  }

  private SmileAndBucketedSensitivities(
      SmileDeltaParameters smile,
      DoubleMatrix sensitivities) {
    JodaBeanUtils.notNull(sensitivities, "sensitivities");
    this.smile = smile;
    this.sensitivities = sensitivities;
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
   * Gets the smile.
   * @return the value of the property
   */
  public SmileDeltaParameters getSmile() {
    return smile;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the sensitivities.
   * @return the value of the property, not null
   */
  public DoubleMatrix getSensitivities() {
    return sensitivities;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SmileAndBucketedSensitivities other = (SmileAndBucketedSensitivities) obj;
      return JodaBeanUtils.equal(getSmile(), other.getSmile()) &&
          JodaBeanUtils.equal(getSensitivities(), other.getSensitivities());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getSmile());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSensitivities());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("SmileAndBucketedSensitivities{");
    buf.append("smile").append('=').append(getSmile()).append(',').append(' ');
    buf.append("sensitivities").append('=').append(JodaBeanUtils.toString(getSensitivities()));
    buf.append('}');
    return buf.toString();
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
