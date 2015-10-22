/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.math.impl.interpolation;

import java.io.Serializable;
import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.basics.interpolator.CurveExtrapolator;
import com.opengamma.strata.math.impl.interpolation.data.Interpolator1DDataBundle;

/**
 * Extrapolator that does no extrapolation itself and delegates to the interpolator for all operations.
 * <p>
 * This reproduces the old behaviour in {@link CombinedInterpolatorExtrapolator} when the extrapolators were
 * null. This extrapolator is used in place of a null extrapolator which allows the extrapolators to be non-null
 * and makes for simpler and cleaner code where the extrapolators are used.
 */
@BeanDefinition(style = "light", constructorScope = "public")
public final class InterpolatorExtrapolator
    implements CurveExtrapolator, Extrapolator1D, ImmutableBean, Serializable {

  /** The interpolator name. */
  public static final String NAME = "Interpolator";

  //-------------------------------------------------------------------------
  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Double extrapolate(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    JodaBeanUtils.notNull(data, "data");
    return interpolator.interpolate(data, value);
  }

  @Override
  public double firstDerivative(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    JodaBeanUtils.notNull(data, "data");
    return interpolator.firstDerivative(data, value);
  }

  @Override
  public double[] getNodeSensitivitiesForValue(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    JodaBeanUtils.notNull(data, "data");
    return interpolator.getNodeSensitivitiesForValue(data, value);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code InterpolatorExtrapolator}.
   */
  private static MetaBean META_BEAN = LightMetaBean.of(InterpolatorExtrapolator.class);

  /**
   * The meta-bean for {@code InterpolatorExtrapolator}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return META_BEAN;
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance.
   */
  public InterpolatorExtrapolator() {
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
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(32);
    buf.append("InterpolatorExtrapolator{");
    buf.append('}');
    return buf.toString();
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
