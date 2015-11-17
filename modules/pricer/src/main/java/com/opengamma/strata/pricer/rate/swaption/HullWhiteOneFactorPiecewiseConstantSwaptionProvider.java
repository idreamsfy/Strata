/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.swaption;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.basics.date.DayCount;
import com.opengamma.strata.basics.value.ValueDerivatives;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.pricer.impl.rate.model.HullWhiteOneFactorPiecewiseConstantInterestRateModel;
import com.opengamma.strata.pricer.impl.rate.model.HullWhiteOneFactorPiecewiseConstantParameters;


/**
 * Provider for swaptions based on Hull-White one factor model with piecewise constant volatility.
 * <p>
 * Reference: Henrard, M. "The Irony in the derivatives discounting Part II: the crisis", Wilmott Journal, 2010, 2, 301-316
 */
@BeanDefinition(builderScope = "private")
public final class HullWhiteOneFactorPiecewiseConstantSwaptionProvider implements ImmutableBean, Serializable {

  /**
   * Hull-White one factor model with piecewise constant volatility.
   */
  private static final HullWhiteOneFactorPiecewiseConstantInterestRateModel MODEL =
      HullWhiteOneFactorPiecewiseConstantInterestRateModel.DEFAULT;
  /**
   * The Hull-White model parameters.
   */
  @PropertyDefinition(validate = "notNull")
  private final HullWhiteOneFactorPiecewiseConstantParameters parameters;
  /** 
   * The day count applicable to the model. 
   */
  @PropertyDefinition(validate = "notNull")
  private final DayCount dayCount;
  /** 
   * The valuation date-time. 
   * <p>
   * All data items in this environment are calibrated for this date-time. 
   */
  @PropertyDefinition(validate = "notNull")
  private final ZonedDateTime valuationDateTime;

  //-------------------------------------------------------------------------
  /**
   * Creates a provider from Hull-White model parameters and the date-time for which it is valid.
   * 
   * @param parameters  the Hull-White model parameters
   * @param dayCount  the day count applicable to the model
   * @param valuationDateTime  the valuation date-time
   * @return the provider
   */
  public static HullWhiteOneFactorPiecewiseConstantSwaptionProvider of(
      HullWhiteOneFactorPiecewiseConstantParameters parameters,
      DayCount dayCount,
      ZonedDateTime valuationDateTime) {

    return new HullWhiteOneFactorPiecewiseConstantSwaptionProvider(parameters, dayCount, valuationDateTime);
  }

  /**
   * Creates a provider from Hull-White model parameters and the date, time and zone for which it is valid.
   * 
   * @param parameters  the Hull-White model parameters
   * @param dayCount  the day count applicable to the model
   * @param valuationDate  the valuation date
   * @param valuationTime  the valuation time
   * @param valuationZone  the valuation time zone
   * @return the provider
   */
  public static HullWhiteOneFactorPiecewiseConstantSwaptionProvider of(
      HullWhiteOneFactorPiecewiseConstantParameters parameters,
      DayCount dayCount,
      LocalDate valuationDate,
      LocalTime valuationTime,
      ZoneId valuationZone) {

    return of(parameters, dayCount, valuationDate.atTime(valuationTime).atZone(valuationZone));
  }

  /**
   * Creates a provider from Hull-White model parameters, day count and valuation date. 
   * 
   * @param parameters  the Hull-White model parameters
   * @param dayCount  the day count
   * @param valuationDate  the valuation date
   * @return the provider 
   */
  public static HullWhiteOneFactorPiecewiseConstantSwaptionProvider of(
      HullWhiteOneFactorPiecewiseConstantParameters parameters,
      DayCount dayCount,
      LocalDate valuationDate) {
    return new HullWhiteOneFactorPiecewiseConstantSwaptionProvider(
        parameters, dayCount, valuationDate.atTime(LocalTime.NOON).atZone(ZoneOffset.UTC));
  }

  /**
   * Computes the alpha value for the specified period with respect to the maturity date.
   * <p>
   * The alpha is computed with a bond numeraire of {@code numeraireDate}.
   * 
   * @param startDate  the start date of the period
   * @param endDate  the end date of the period
   * @param numeraireDate  the numeraire date
   * @param maturityDate  the maturity date
   * @return  the alpha
   */
  public double alpha(LocalDate startDate, LocalDate endDate, LocalDate numeraireDate, LocalDate maturityDate) {
    double startTime = relativeTime(startDate);
    double endTime = relativeTime(endDate);
    double numeraireTime = relativeTime(numeraireDate);
    double maturityTime = relativeTime(maturityDate);
    return MODEL.alpha(parameters, startTime, endTime, numeraireTime, maturityTime);
  }

  /**
   * Computes the alpha and its derivative values for the specified period with respect to the maturity date.
   * <p>
   * The alpha is computed with a bond numeraire of {@code numeraireDate}.
   * 
   * @param startDate  the start date of the period
   * @param endDate  the end date of the period
   * @param numeraireDate  the numeraire date
   * @param maturityDate  the maturity date
   * @return  the alpha adjoint
   */
  public ValueDerivatives alphaAdjoint(
      LocalDate startDate,
      LocalDate endDate,
      LocalDate numeraireDate,
      LocalDate maturityDate) {
    double startTime = relativeTime(startDate);
    double endTime = relativeTime(endDate);
    double numeraireTime = relativeTime(numeraireDate);
    double maturityTime = relativeTime(maturityDate);
    return MODEL.alphaAdjoint(parameters, startTime, endTime, numeraireTime, maturityTime);
  }

  /**
   * Returns a Hull-White one-factor model.
   * 
   * @return the model
   */
  public HullWhiteOneFactorPiecewiseConstantInterestRateModel getModel() {
    return MODEL;
  }

  /**
   * Converts a date to a relative year fraction. 
   * <p>
   * When the date is after the valuation date, the returned number is negative.
   * 
   * @param date  the date to find the relative year fraction of
   * @return the relative year fraction
   */
  public double relativeTime(LocalDate date) {
    ArgChecker.notNull(date, "date");
    LocalDate valuationDate = valuationDateTime.toLocalDate();
    boolean timeIsNegative = valuationDate.isAfter(date);
    if (timeIsNegative) {
      return -dayCount.yearFraction(date, valuationDate);
    }
    return dayCount.yearFraction(valuationDate, date);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code HullWhiteOneFactorPiecewiseConstantSwaptionProvider}.
   * @return the meta-bean, not null
   */
  public static HullWhiteOneFactorPiecewiseConstantSwaptionProvider.Meta meta() {
    return HullWhiteOneFactorPiecewiseConstantSwaptionProvider.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(HullWhiteOneFactorPiecewiseConstantSwaptionProvider.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private HullWhiteOneFactorPiecewiseConstantSwaptionProvider(
      HullWhiteOneFactorPiecewiseConstantParameters parameters,
      DayCount dayCount,
      ZonedDateTime valuationDateTime) {
    JodaBeanUtils.notNull(parameters, "parameters");
    JodaBeanUtils.notNull(dayCount, "dayCount");
    JodaBeanUtils.notNull(valuationDateTime, "valuationDateTime");
    this.parameters = parameters;
    this.dayCount = dayCount;
    this.valuationDateTime = valuationDateTime;
  }

  @Override
  public HullWhiteOneFactorPiecewiseConstantSwaptionProvider.Meta metaBean() {
    return HullWhiteOneFactorPiecewiseConstantSwaptionProvider.Meta.INSTANCE;
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
   * Gets the Hull-White model parameters.
   * @return the value of the property, not null
   */
  public HullWhiteOneFactorPiecewiseConstantParameters getParameters() {
    return parameters;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the day count applicable to the model.
   * @return the value of the property, not null
   */
  public DayCount getDayCount() {
    return dayCount;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valuation date-time.
   * <p>
   * All data items in this environment are calibrated for this date-time.
   * @return the value of the property, not null
   */
  public ZonedDateTime getValuationDateTime() {
    return valuationDateTime;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      HullWhiteOneFactorPiecewiseConstantSwaptionProvider other = (HullWhiteOneFactorPiecewiseConstantSwaptionProvider) obj;
      return JodaBeanUtils.equal(parameters, other.parameters) &&
          JodaBeanUtils.equal(dayCount, other.dayCount) &&
          JodaBeanUtils.equal(valuationDateTime, other.valuationDateTime);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(parameters);
    hash = hash * 31 + JodaBeanUtils.hashCode(dayCount);
    hash = hash * 31 + JodaBeanUtils.hashCode(valuationDateTime);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("HullWhiteOneFactorPiecewiseConstantSwaptionProvider{");
    buf.append("parameters").append('=').append(parameters).append(',').append(' ');
    buf.append("dayCount").append('=').append(dayCount).append(',').append(' ');
    buf.append("valuationDateTime").append('=').append(JodaBeanUtils.toString(valuationDateTime));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code HullWhiteOneFactorPiecewiseConstantSwaptionProvider}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code parameters} property.
     */
    private final MetaProperty<HullWhiteOneFactorPiecewiseConstantParameters> parameters = DirectMetaProperty.ofImmutable(
        this, "parameters", HullWhiteOneFactorPiecewiseConstantSwaptionProvider.class, HullWhiteOneFactorPiecewiseConstantParameters.class);
    /**
     * The meta-property for the {@code dayCount} property.
     */
    private final MetaProperty<DayCount> dayCount = DirectMetaProperty.ofImmutable(
        this, "dayCount", HullWhiteOneFactorPiecewiseConstantSwaptionProvider.class, DayCount.class);
    /**
     * The meta-property for the {@code valuationDateTime} property.
     */
    private final MetaProperty<ZonedDateTime> valuationDateTime = DirectMetaProperty.ofImmutable(
        this, "valuationDateTime", HullWhiteOneFactorPiecewiseConstantSwaptionProvider.class, ZonedDateTime.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "parameters",
        "dayCount",
        "valuationDateTime");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 458736106:  // parameters
          return parameters;
        case 1905311443:  // dayCount
          return dayCount;
        case -949589828:  // valuationDateTime
          return valuationDateTime;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends HullWhiteOneFactorPiecewiseConstantSwaptionProvider> builder() {
      return new HullWhiteOneFactorPiecewiseConstantSwaptionProvider.Builder();
    }

    @Override
    public Class<? extends HullWhiteOneFactorPiecewiseConstantSwaptionProvider> beanType() {
      return HullWhiteOneFactorPiecewiseConstantSwaptionProvider.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code parameters} property.
     * @return the meta-property, not null
     */
    public MetaProperty<HullWhiteOneFactorPiecewiseConstantParameters> parameters() {
      return parameters;
    }

    /**
     * The meta-property for the {@code dayCount} property.
     * @return the meta-property, not null
     */
    public MetaProperty<DayCount> dayCount() {
      return dayCount;
    }

    /**
     * The meta-property for the {@code valuationDateTime} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ZonedDateTime> valuationDateTime() {
      return valuationDateTime;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 458736106:  // parameters
          return ((HullWhiteOneFactorPiecewiseConstantSwaptionProvider) bean).getParameters();
        case 1905311443:  // dayCount
          return ((HullWhiteOneFactorPiecewiseConstantSwaptionProvider) bean).getDayCount();
        case -949589828:  // valuationDateTime
          return ((HullWhiteOneFactorPiecewiseConstantSwaptionProvider) bean).getValuationDateTime();
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
   * The bean-builder for {@code HullWhiteOneFactorPiecewiseConstantSwaptionProvider}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<HullWhiteOneFactorPiecewiseConstantSwaptionProvider> {

    private HullWhiteOneFactorPiecewiseConstantParameters parameters;
    private DayCount dayCount;
    private ZonedDateTime valuationDateTime;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 458736106:  // parameters
          return parameters;
        case 1905311443:  // dayCount
          return dayCount;
        case -949589828:  // valuationDateTime
          return valuationDateTime;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 458736106:  // parameters
          this.parameters = (HullWhiteOneFactorPiecewiseConstantParameters) newValue;
          break;
        case 1905311443:  // dayCount
          this.dayCount = (DayCount) newValue;
          break;
        case -949589828:  // valuationDateTime
          this.valuationDateTime = (ZonedDateTime) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public HullWhiteOneFactorPiecewiseConstantSwaptionProvider build() {
      return new HullWhiteOneFactorPiecewiseConstantSwaptionProvider(
          parameters,
          dayCount,
          valuationDateTime);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("HullWhiteOneFactorPiecewiseConstantSwaptionProvider.Builder{");
      buf.append("parameters").append('=').append(JodaBeanUtils.toString(parameters)).append(',').append(' ');
      buf.append("dayCount").append('=').append(JodaBeanUtils.toString(dayCount)).append(',').append(' ');
      buf.append("valuationDateTime").append('=').append(JodaBeanUtils.toString(valuationDateTime));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
