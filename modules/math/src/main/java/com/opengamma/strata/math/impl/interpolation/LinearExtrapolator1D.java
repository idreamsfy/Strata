/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
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
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.basics.interpolator.CurveExtrapolator;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.math.impl.interpolation.data.Interpolator1DDataBundle;

/**
 * 
 */
@BeanDefinition(style = "light", constructorScope = "public")
public final class LinearExtrapolator1D
    implements CurveExtrapolator, Extrapolator1D, ImmutableBean, Serializable {

  /** The extrapolator name. */
  public static final String NAME = "Linear";

  @PropertyDefinition
  private final double eps;

  /**
   * Creates an instance.
   */
  public LinearExtrapolator1D() {
    this(1e-8);
  }

  //-------------------------------------------------------------------------
  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Double extrapolate(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    ArgChecker.notNull(data, "data");
    ArgChecker.notNull(value, "value");

    if (value < data.firstKey()) {
      return leftExtrapolate(data, value, interpolator);
    } else if (value > data.lastKey()) {
      return rightExtrapolate(data, value, interpolator);
    }
    throw new IllegalArgumentException("Value " + value + " was within data range");
  }

  @Override
  public double firstDerivative(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    ArgChecker.notNull(data, "data");
    ArgChecker.notNull(value, "value");

    if (value < data.firstKey()) {
      return leftExtrapolateDerivative(data, value, interpolator);
    } else if (value > data.lastKey()) {
      return rightExtrapolateDerivative(data, value, interpolator);
    }
    throw new IllegalArgumentException("Value " + value + " was within data range");
  }

  @Override
  public double[] getNodeSensitivitiesForValue(
      Interpolator1DDataBundle data,
      Double value,
      Interpolator1D interpolator) {

    ArgChecker.notNull(data, "data");

    if (value < data.firstKey()) {
      return getLeftSensitivities(data, value, interpolator);
    } else if (value > data.lastKey()) {
      return getRightSensitivities(data, value, interpolator);
    }
    throw new IllegalArgumentException("Value " + value + " was within data range");
  }

  private Double leftExtrapolate(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    ArgChecker.notNull(data, "data");
    ArgChecker.notNull(value, "value");

    double x = data.firstKey();
    double y = data.firstValue();
    double eps = this.eps * (data.lastKey() - x);
    double m = (interpolator.interpolate(data, x + eps) - y) / eps;
    return y + (value - x) * m;
  }

  private Double rightExtrapolate(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    ArgChecker.notNull(data, "data");
    ArgChecker.notNull(value, "value");
    double x = data.lastKey();
    double y = data.lastValue();
    double eps = this.eps * (x - data.firstKey());
    double m = (y - interpolator.interpolate(data, x - eps)) / eps;
    return y + (value - x) * m;
  }

  private Double leftExtrapolateDerivative(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    ArgChecker.notNull(data, "data");
    ArgChecker.notNull(value, "value");
    double x = data.firstKey();
    double y = data.firstValue();
    double eps = this.eps * (data.lastKey() - x);
    double m = (interpolator.interpolate(data, x + eps) - y) / eps;
    return m;
  }

  private Double rightExtrapolateDerivative(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    ArgChecker.notNull(data, "data");
    ArgChecker.notNull(value, "value");
    double x = data.lastKey();
    double y = data.lastValue();
    double eps = this.eps * (x - data.firstKey());
    double m = (y - interpolator.interpolate(data, x - eps)) / eps;
    return m;
  }

  private double[] getLeftSensitivities(Interpolator1DDataBundle data, double value, Interpolator1D interpolator) {
    double eps = this.eps * (data.lastKey() - data.firstKey());
    double x = data.firstKey();
    double[] result = interpolator.getNodeSensitivitiesForValue(data, x + eps);
    int n = result.length;
    for (int i = 1; i < n; i++) {
      result[i] = result[i] * (value - x) / eps;
    }
    result[0] = 1 + (result[0] - 1) * (value - x) / eps;
    return result;
  }

  private double[] getRightSensitivities(Interpolator1DDataBundle data, Double value, Interpolator1D interpolator) {
    double eps = this.eps * (data.lastKey() - data.firstKey());
    double x = data.lastKey();
    double[] result = interpolator.getNodeSensitivitiesForValue(data, x - eps);
    int n = result.length;
    for (int i = 0; i < n - 1; i++) {
      result[i] = -result[i] * (value - x) / eps;
    }
    result[n - 1] = 1 + (1 - result[n - 1]) * (value - x) / eps;
    return result;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code LinearExtrapolator1D}.
   */
  private static MetaBean META_BEAN = LightMetaBean.of(LinearExtrapolator1D.class);

  /**
   * The meta-bean for {@code LinearExtrapolator1D}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return META_BEAN;
  }

  static {
    JodaBeanUtils.registerMetaBean(META_BEAN);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance.
   * @param eps  the value of the property
   */
  public LinearExtrapolator1D(
      double eps) {
    this.eps = eps;
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
   * Gets the eps.
   * @return the value of the property
   */
  public double getEps() {
    return eps;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      LinearExtrapolator1D other = (LinearExtrapolator1D) obj;
      return JodaBeanUtils.equal(eps, other.eps);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(eps);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("LinearExtrapolator1D{");
    buf.append("eps").append('=').append(JodaBeanUtils.toString(eps));
    buf.append('}');
    return buf.toString();
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
