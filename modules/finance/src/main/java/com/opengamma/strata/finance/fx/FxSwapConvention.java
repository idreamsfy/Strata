/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.fx;

import static com.opengamma.strata.basics.date.BusinessDayConventions.MODIFIED_FOLLOWING;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;

import com.opengamma.strata.basics.BuySell;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.CurrencyPair;
import com.opengamma.strata.basics.date.BusinessDayAdjustment;
import com.opengamma.strata.basics.date.DaysAdjustment;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.finance.Convention;
import com.opengamma.strata.finance.TradeInfo;

import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * A market convention for FX swap trades
 * <p>
 * This defines the market convention for a FX swap based on a particular currency pair.
 * <p>
 * The convention is defined by four dates.
 * <ul>
 * <li>Trade date, the date that the trade is agreed
 * <li>Spot date, the base for date calculations, typically 2 business days after the trade date
 * <li>Near date, the date on which the near leg of the swap is exchanged, typically equal to the spot date
 * <li>Far date, the date on which the far leg of the swap is exchanged, typically a number of months or years after the spot date
 * </ul>
 * The period between the spot date and the start/end date is specified by {@link FxSwapTemplate}, not by this convention.
 */
@BeanDefinition
public final class FxSwapConvention
    implements Convention, ImmutableBean, Serializable {
  
  /**
   * The currency pair associated to the convention.
   */
  @PropertyDefinition(validate = "notNull")
  private final CurrencyPair currencyPair;  
  /**
   * The offset of the spot value date from the trade date, optional with defaulting getter.
   * <p>
   * The offset is applied to the trade date and is typically plus 2 business days in the joint calendar of the two currencies.
   * The start and end date of the FX swap are relative to the spot date.
   */
  @PropertyDefinition(validate = "notNull")
  private final DaysAdjustment spotDateOffset;
  /**
   * The business day adjustment to apply to the start and end date.
   * <p>
   * The start and end date are typically defined as valid business days and thus
   * do not need to be adjusted. If this optional property is present, then the
   * start and end date will be adjusted as defined here.
   * <p>
   * This will default to 'ModifiedFollowing' using the spot date offset calendar if not specified.
   */
  @PropertyDefinition(get = "field")
  private final BusinessDayAdjustment businessDayAdjustment;

  //-------------------------------------------------------------------------
  /**
   * Creates a convention based on the specified currency pair, offset and adjustment.=
   * 
   * @param currencyPair  the currency pair associated to the convention
   * @param spotDateOffset  the spot date offset 
   * @return the convention
   */
  public static FxSwapConvention of(
      CurrencyPair currencyPair, 
      DaysAdjustment spotDateOffset) {
    return FxSwapConvention.builder()
        .currencyPair(currencyPair)
        .spotDateOffset(spotDateOffset)
        .build();
  }
  
  /**
   * Creates a convention based on the specified currency pair, offset and adjustment.=
   * 
   * @param currencyPair  the currency pair associated to the convention
   * @param spotDateOffset  the spot date offset 
   * @return the convention
   */
  public static FxSwapConvention of(
      CurrencyPair currencyPair, 
      DaysAdjustment spotDateOffset, 
      BusinessDayAdjustment businessDayAdjustment) {
    return FxSwapConvention.builder()
        .currencyPair(currencyPair)
        .spotDateOffset(spotDateOffset)
        .businessDayAdjustment(businessDayAdjustment)
        .build();
  }

  /**
   * Gets the business day adjustment to apply to the start and end date,
   * providing a default result if no override specified.
   * <p>
   * The start and end date are typically defined as valid business days and thus
   * do not need to be adjusted. If this optional property is present, then the
   * start and end date will be adjusted as defined here.
   * <p>
   * This will default to 'ModifiedFollowing' using the spot date offset calendar if not specified.
   * 
   * @return the business day adjustment, not null
   */
  public BusinessDayAdjustment getBusinessDayAdjustment() {
    return businessDayAdjustment != null ?
        businessDayAdjustment : BusinessDayAdjustment.of(MODIFIED_FOLLOWING, spotDateOffset.getCalendar());
  }
  


  //-------------------------------------------------------------------------
  /**
   * Creates a trade based on this convention.
   * <p>
   * This returns a trade based on the specified periods.
   * For example, a '3M x 6M' FX swap has a period from spot to the start date of 3 months and
   * a period from spot to the end date of 6 months
   * <p>
   * The notional is unsigned, with buy/sell determining the direction of the trade.
   * If buying the FX Swap, the amount in the first currency of the pair is received in the near leg and paid in the 
   * far leg, while the second currency is paid in the near leg and received in the far leg.
   * 
   * @param tradeDate  the date of the trade
   * @param periodToNear  the period between the spot date and the near date
   * @param periodToFar  the period between the spot date and the far date
   * @param buySell  the buy/sell flag
   * @param notional  the notional amount, in the first currency of the currency pair
   * @param nearFxRate  the FX rate for the near leg
   * @param forwardPoints  the FX points to be added to the FX rate at the far leg
   * @return the trade
   */
  public FxSwapTrade toTrade(
      LocalDate tradeDate,
      Period periodToNear,
      Period periodToFar,
      BuySell buySell,
      double notional,
      double nearFxRate,
      double forwardPoints) {

    BusinessDayAdjustment bda = getBusinessDayAdjustment();
    LocalDate spotValue = getSpotDateOffset().adjust(tradeDate);
    LocalDate startDate = bda.adjust(spotValue.plus(periodToNear));
    LocalDate endDate = bda.adjust(spotValue.plus(periodToFar));
    return toTrade(tradeDate, startDate, endDate, buySell, notional, nearFxRate, forwardPoints);
  }

  /**
   * Creates a trade based on this convention.
   * <p>
   * This returns a trade based on the specified dates.
   * The notional is unsigned, with buy/sell determining the direction of the trade.
   * If buying the FX Swap, the amount in the first currency of the pair is received in the near leg and paid in the 
   * far leg, while the second currency is paid in the near leg and received in the far leg.
   * 
   * @param tradeDate  the date of the trade
   * @param startDate  the start date
   * @param endDate  the end date
   * @param buySell  the buy/sell flag
   * @param notional  the notional amount, in the payment currency of the template
   * @param nearFxRate  the FX rate for the near leg
   * @param forwardPoints  the FX points to be added to the FX rate at the far leg
   * @return the trade
   */
  public FxSwapTrade toTrade(
      LocalDate tradeDate,
      LocalDate startDate,
      LocalDate endDate,
      BuySell buySell,
      double notional,
      double nearFxRate,
      double forwardPoints) {

    ArgChecker.inOrderOrEqual(tradeDate, startDate, "tradeDate", "startDate");
    double amount1 = notional * (buySell.equals(BuySell.BUY) ? 1.0 : -1.0);
    return FxSwapTrade.builder()
        .tradeInfo(TradeInfo.builder()
            .tradeDate(tradeDate).build())
        .product(FxSwap.ofForwardPoints(CurrencyAmount.of(currencyPair.getBase(), amount1), 
                    currencyPair.getCounter(), nearFxRate, forwardPoints, startDate, endDate) ).build();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FxSwapConvention}.
   * @return the meta-bean, not null
   */
  public static FxSwapConvention.Meta meta() {
    return FxSwapConvention.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FxSwapConvention.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static FxSwapConvention.Builder builder() {
    return new FxSwapConvention.Builder();
  }

  private FxSwapConvention(
      CurrencyPair currencyPair,
      DaysAdjustment spotDateOffset,
      BusinessDayAdjustment businessDayAdjustment) {
    JodaBeanUtils.notNull(currencyPair, "currencyPair");
    JodaBeanUtils.notNull(spotDateOffset, "spotDateOffset");
    this.currencyPair = currencyPair;
    this.spotDateOffset = spotDateOffset;
    this.businessDayAdjustment = businessDayAdjustment;
  }

  @Override
  public FxSwapConvention.Meta metaBean() {
    return FxSwapConvention.Meta.INSTANCE;
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
   * Gets the currency pair associated to the convention.
   * @return the value of the property, not null
   */
  public CurrencyPair getCurrencyPair() {
    return currencyPair;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the offset of the spot value date from the trade date, optional with defaulting getter.
   * <p>
   * The offset is applied to the trade date and is typically plus 2 business days in the joint calendar of the two currencies.
   * The start and end date of the FX swap are relative to the spot date.
   * @return the value of the property, not null
   */
  public DaysAdjustment getSpotDateOffset() {
    return spotDateOffset;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FxSwapConvention other = (FxSwapConvention) obj;
      return JodaBeanUtils.equal(getCurrencyPair(), other.getCurrencyPair()) &&
          JodaBeanUtils.equal(getSpotDateOffset(), other.getSpotDateOffset()) &&
          JodaBeanUtils.equal(businessDayAdjustment, other.businessDayAdjustment);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getCurrencyPair());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSpotDateOffset());
    hash = hash * 31 + JodaBeanUtils.hashCode(businessDayAdjustment);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("FxSwapConvention{");
    buf.append("currencyPair").append('=').append(getCurrencyPair()).append(',').append(' ');
    buf.append("spotDateOffset").append('=').append(getSpotDateOffset()).append(',').append(' ');
    buf.append("businessDayAdjustment").append('=').append(JodaBeanUtils.toString(businessDayAdjustment));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FxSwapConvention}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code currencyPair} property.
     */
    private final MetaProperty<CurrencyPair> currencyPair = DirectMetaProperty.ofImmutable(
        this, "currencyPair", FxSwapConvention.class, CurrencyPair.class);
    /**
     * The meta-property for the {@code spotDateOffset} property.
     */
    private final MetaProperty<DaysAdjustment> spotDateOffset = DirectMetaProperty.ofImmutable(
        this, "spotDateOffset", FxSwapConvention.class, DaysAdjustment.class);
    /**
     * The meta-property for the {@code businessDayAdjustment} property.
     */
    private final MetaProperty<BusinessDayAdjustment> businessDayAdjustment = DirectMetaProperty.ofImmutable(
        this, "businessDayAdjustment", FxSwapConvention.class, BusinessDayAdjustment.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "currencyPair",
        "spotDateOffset",
        "businessDayAdjustment");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1005147787:  // currencyPair
          return currencyPair;
        case 746995843:  // spotDateOffset
          return spotDateOffset;
        case -1065319863:  // businessDayAdjustment
          return businessDayAdjustment;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public FxSwapConvention.Builder builder() {
      return new FxSwapConvention.Builder();
    }

    @Override
    public Class<? extends FxSwapConvention> beanType() {
      return FxSwapConvention.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code currencyPair} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurrencyPair> currencyPair() {
      return currencyPair;
    }

    /**
     * The meta-property for the {@code spotDateOffset} property.
     * @return the meta-property, not null
     */
    public MetaProperty<DaysAdjustment> spotDateOffset() {
      return spotDateOffset;
    }

    /**
     * The meta-property for the {@code businessDayAdjustment} property.
     * @return the meta-property, not null
     */
    public MetaProperty<BusinessDayAdjustment> businessDayAdjustment() {
      return businessDayAdjustment;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1005147787:  // currencyPair
          return ((FxSwapConvention) bean).getCurrencyPair();
        case 746995843:  // spotDateOffset
          return ((FxSwapConvention) bean).getSpotDateOffset();
        case -1065319863:  // businessDayAdjustment
          return ((FxSwapConvention) bean).businessDayAdjustment;
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
   * The bean-builder for {@code FxSwapConvention}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<FxSwapConvention> {

    private CurrencyPair currencyPair;
    private DaysAdjustment spotDateOffset;
    private BusinessDayAdjustment businessDayAdjustment;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(FxSwapConvention beanToCopy) {
      this.currencyPair = beanToCopy.getCurrencyPair();
      this.spotDateOffset = beanToCopy.getSpotDateOffset();
      this.businessDayAdjustment = beanToCopy.businessDayAdjustment;
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1005147787:  // currencyPair
          return currencyPair;
        case 746995843:  // spotDateOffset
          return spotDateOffset;
        case -1065319863:  // businessDayAdjustment
          return businessDayAdjustment;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1005147787:  // currencyPair
          this.currencyPair = (CurrencyPair) newValue;
          break;
        case 746995843:  // spotDateOffset
          this.spotDateOffset = (DaysAdjustment) newValue;
          break;
        case -1065319863:  // businessDayAdjustment
          this.businessDayAdjustment = (BusinessDayAdjustment) newValue;
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
    public FxSwapConvention build() {
      return new FxSwapConvention(
          currencyPair,
          spotDateOffset,
          businessDayAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the currency pair associated to the convention.
     * @param currencyPair  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder currencyPair(CurrencyPair currencyPair) {
      JodaBeanUtils.notNull(currencyPair, "currencyPair");
      this.currencyPair = currencyPair;
      return this;
    }

    /**
     * Sets the offset of the spot value date from the trade date, optional with defaulting getter.
     * <p>
     * The offset is applied to the trade date and is typically plus 2 business days in the joint calendar of the two currencies.
     * The start and end date of the FX swap are relative to the spot date.
     * @param spotDateOffset  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder spotDateOffset(DaysAdjustment spotDateOffset) {
      JodaBeanUtils.notNull(spotDateOffset, "spotDateOffset");
      this.spotDateOffset = spotDateOffset;
      return this;
    }

    /**
     * Sets the business day adjustment to apply to the start and end date.
     * <p>
     * The start and end date are typically defined as valid business days and thus
     * do not need to be adjusted. If this optional property is present, then the
     * start and end date will be adjusted as defined here.
     * <p>
     * This will default to 'ModifiedFollowing' using the spot date offset calendar if not specified.
     * @param businessDayAdjustment  the new value
     * @return this, for chaining, not null
     */
    public Builder businessDayAdjustment(BusinessDayAdjustment businessDayAdjustment) {
      this.businessDayAdjustment = businessDayAdjustment;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("FxSwapConvention.Builder{");
      buf.append("currencyPair").append('=').append(JodaBeanUtils.toString(currencyPair)).append(',').append(' ');
      buf.append("spotDateOffset").append('=').append(JodaBeanUtils.toString(spotDateOffset)).append(',').append(' ');
      buf.append("businessDayAdjustment").append('=').append(JodaBeanUtils.toString(businessDayAdjustment));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
