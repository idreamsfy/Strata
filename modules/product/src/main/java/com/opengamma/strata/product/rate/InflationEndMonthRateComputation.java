/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.rate;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.index.Index;
import com.opengamma.strata.basics.index.PriceIndex;
import com.opengamma.strata.basics.index.PriceIndexObservation;
import com.opengamma.strata.collect.ArgChecker;

/**
 * Defines the computation of inflation figures from a price index
 * where the start index value is known.
 * <p>
 * A typical application of this rate computation is payments of a capital indexed bond, 
 * where the reference start month is start month of the bond rather than start month of the payment period.
 * <p>
 * A price index is typically published monthly and has a delay before publication.
 * The rate observed by this instance will be based on the start index value
 * and the observation relative to the end month.
 */
@BeanDefinition(builderScope = "private")
public final class InflationEndMonthRateComputation
    implements RateComputation, ImmutableBean, Serializable {

  /**
   * The start index value.
   * <p>
   * The published index value of the start month.
   */
  @PropertyDefinition(validate = "ArgChecker.notNegativeOrZero")
  private final double startIndexValue;
  /**
   * The observation at the end.
   * <p>
   * The inflation rate is the ratio between the start index value and end observation.
   * The end month is typically three months before the end of the period.
   */
  @PropertyDefinition(validate = "notNull")
  private final PriceIndexObservation endObservation;

  //-------------------------------------------------------------------------
  /**
   * Creates an instance from an index, start index value and reference end month.
   * 
   * @param index  the index
   * @param startIndexValue  the start index value
   * @param referenceEndMonth  the reference end month
   * @return the inflation rate computation
   */
  public static InflationEndMonthRateComputation of(
      PriceIndex index,
      double startIndexValue,
      YearMonth referenceEndMonth) {

    return new InflationEndMonthRateComputation(startIndexValue, PriceIndexObservation.of(index, referenceEndMonth));
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the Price index.
   * 
   * @return the Price index
   */
  public PriceIndex getIndex() {
    return endObservation.getIndex();
  }

  //-------------------------------------------------------------------------
  @Override
  public void collectIndices(ImmutableSet.Builder<Index> builder) {
    builder.add(getIndex());
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code InflationEndMonthRateComputation}.
   * @return the meta-bean, not null
   */
  public static InflationEndMonthRateComputation.Meta meta() {
    return InflationEndMonthRateComputation.Meta.INSTANCE;
  }

  static {
    MetaBean.register(InflationEndMonthRateComputation.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private InflationEndMonthRateComputation(
      double startIndexValue,
      PriceIndexObservation endObservation) {
    ArgChecker.notNegativeOrZero(startIndexValue, "startIndexValue");
    JodaBeanUtils.notNull(endObservation, "endObservation");
    this.startIndexValue = startIndexValue;
    this.endObservation = endObservation;
  }

  @Override
  public InflationEndMonthRateComputation.Meta metaBean() {
    return InflationEndMonthRateComputation.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the start index value.
   * <p>
   * The published index value of the start month.
   * @return the value of the property
   */
  public double getStartIndexValue() {
    return startIndexValue;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the observation at the end.
   * <p>
   * The inflation rate is the ratio between the start index value and end observation.
   * The end month is typically three months before the end of the period.
   * @return the value of the property, not null
   */
  public PriceIndexObservation getEndObservation() {
    return endObservation;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      InflationEndMonthRateComputation other = (InflationEndMonthRateComputation) obj;
      return JodaBeanUtils.equal(startIndexValue, other.startIndexValue) &&
          JodaBeanUtils.equal(endObservation, other.endObservation);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(startIndexValue);
    hash = hash * 31 + JodaBeanUtils.hashCode(endObservation);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("InflationEndMonthRateComputation{");
    buf.append("startIndexValue").append('=').append(startIndexValue).append(',').append(' ');
    buf.append("endObservation").append('=').append(JodaBeanUtils.toString(endObservation));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code InflationEndMonthRateComputation}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code startIndexValue} property.
     */
    private final MetaProperty<Double> startIndexValue = DirectMetaProperty.ofImmutable(
        this, "startIndexValue", InflationEndMonthRateComputation.class, Double.TYPE);
    /**
     * The meta-property for the {@code endObservation} property.
     */
    private final MetaProperty<PriceIndexObservation> endObservation = DirectMetaProperty.ofImmutable(
        this, "endObservation", InflationEndMonthRateComputation.class, PriceIndexObservation.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "startIndexValue",
        "endObservation");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1656407615:  // startIndexValue
          return startIndexValue;
        case 82210897:  // endObservation
          return endObservation;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends InflationEndMonthRateComputation> builder() {
      return new InflationEndMonthRateComputation.Builder();
    }

    @Override
    public Class<? extends InflationEndMonthRateComputation> beanType() {
      return InflationEndMonthRateComputation.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code startIndexValue} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> startIndexValue() {
      return startIndexValue;
    }

    /**
     * The meta-property for the {@code endObservation} property.
     * @return the meta-property, not null
     */
    public MetaProperty<PriceIndexObservation> endObservation() {
      return endObservation;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1656407615:  // startIndexValue
          return ((InflationEndMonthRateComputation) bean).getStartIndexValue();
        case 82210897:  // endObservation
          return ((InflationEndMonthRateComputation) bean).getEndObservation();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code InflationEndMonthRateComputation}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<InflationEndMonthRateComputation> {

    private double startIndexValue;
    private PriceIndexObservation endObservation;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1656407615:  // startIndexValue
          return startIndexValue;
        case 82210897:  // endObservation
          return endObservation;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1656407615:  // startIndexValue
          this.startIndexValue = (Double) newValue;
          break;
        case 82210897:  // endObservation
          this.endObservation = (PriceIndexObservation) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public InflationEndMonthRateComputation build() {
      return new InflationEndMonthRateComputation(
          startIndexValue,
          endObservation);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("InflationEndMonthRateComputation.Builder{");
      buf.append("startIndexValue").append('=').append(JodaBeanUtils.toString(startIndexValue)).append(',').append(' ');
      buf.append("endObservation").append('=').append(JodaBeanUtils.toString(endObservation));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
