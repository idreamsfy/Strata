/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.rate.future;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;

import com.opengamma.strata.basics.date.BusinessDayAdjustment;
import com.opengamma.strata.basics.date.BusinessDayConventions;
import com.opengamma.strata.basics.index.IborIndex;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.id.StandardId;
import com.opengamma.strata.finance.Convention;
import com.opengamma.strata.finance.Security;
import com.opengamma.strata.finance.SecurityLink;
import com.opengamma.strata.finance.TradeInfo;
import com.opengamma.strata.finance.UnitSecurity;

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
 * A market convention for Ibor Future trades.
 */
@BeanDefinition
public class IborFutureConvention
    implements Convention, ImmutableBean, Serializable  {
  
  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The IBOR-like index.
   * <p>
   * The floating rate to be paid is based on this index
   * It will be a well known market index such as 'GBP-LIBOR-3M'.
   */
  @PropertyDefinition(validate = "notNull")
  private final IborIndex index;
  
  /**
   * The business day adjustment to apply to the reference date, optional with defaulting getter.
   * <p>
   * The reference date, which is often the third Wednesday of the month, will be adjusted as defined here.
   * <p>
   * This will default to 'Following' using the index fixing calendar if not specified.
   */
  @PropertyDefinition(validate = "notNull")
  private final BusinessDayAdjustment businessDayAdjustment;
  

  /**
   * The adjuster to find the reference date of the future.
   * <p>
   * The reference date of the future is the start date of the underlying synthetic deposit.
   */
  @PropertyDefinition(validate = "notNull")
  private final TemporalAdjuster referenceDateAdjuster;
  
  /**
   * Creates a convention based on the specified index and the adjuster for the reference date.
   * <p>
   * @param index   the index, the calendar for the adjustment is extracted from the index
   * @param referenceDateAdjuster the adjuster to find the reference date
   * @return the convention
   */
  public static IborFutureConvention of(IborIndex index, TemporalAdjuster referenceDateAdjuster) {
    return IborFutureConvention.builder()
        .index(index)
        .businessDayAdjustment(
            BusinessDayAdjustment.of(BusinessDayConventions.FOLLOWING, index.getEffectiveDateOffset().getCalendar()))
        .referenceDateAdjuster(referenceDateAdjuster)
        .build();
  }

  /**
   * Creates a trade based on this convention.
   * <p>
   * This returns a trade based on the specified dates.
   * 
   * @param tradeDate  the trade date
   * @param periodToStart  the period between the starting date and the start of the number of future iteration
   * @param number  the number of reference date adjuster after the initial period, should be positive
   * @param quantity  the quantity of contract traded
   * @param notional  the notional amount of one future contract
   * @param price  the trade price of the future
   * @return the trade
   */
  public IborFutureTrade toTrade(
      LocalDate tradeDate,
      Period periodToStart,
      int number,
      long quantity,
      double notional,
      double price) {
    LocalDate referenceDate = referenceDate(tradeDate, periodToStart, number);
    double accrualFactor = index.getTenor().get(ChronoUnit.MONTHS) / 12.0;
    LocalDate lastTradeDate = index.calculateFixingFromEffective(referenceDate);
    IborFuture underlying = IborFuture.builder()
        .index(index)
        .accrualFactor(accrualFactor)
        .lastTradeDate(lastTradeDate)
        .notional(notional).build();
    YearMonth m = YearMonth.from(lastTradeDate);
    Security<IborFuture> security = UnitSecurity.builder(underlying)
        .standardId(StandardId.of("OG-Ticker", "IborFuture-" + index.getName() + "-" + m.toString()))
        .build();
    SecurityLink<IborFuture> securityLink = SecurityLink.resolved(security);
    TradeInfo info = TradeInfo.builder().tradeDate(tradeDate).build();
    return IborFutureTrade.builder()
        .quantity(quantity).initialPrice(price).securityLink(securityLink).tradeInfo(info).build();
  }
  
  public LocalDate referenceDate(
      LocalDate tradeDate,
      Period periodToStart,
      int number) {
    ArgChecker.isTrue(number >= 0, "number should be >=0"); 
    LocalDate referenceDate = tradeDate.plus(periodToStart);
    for (int i = 0; i < number; i++) {
      referenceDate = referenceDate.with(referenceDateAdjuster);
      if (i < number - 1) {
        referenceDate = referenceDate.plusDays(1);
      }
    }
    return businessDayAdjustment.adjust(referenceDate);    
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code IborFutureConvention}.
   * @return the meta-bean, not null
   */
  public static IborFutureConvention.Meta meta() {
    return IborFutureConvention.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(IborFutureConvention.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static IborFutureConvention.Builder builder() {
    return new IborFutureConvention.Builder();
  }

  /**
   * Restricted constructor.
   * @param builder  the builder to copy from, not null
   */
  protected IborFutureConvention(IborFutureConvention.Builder builder) {
    JodaBeanUtils.notNull(builder.index, "index");
    JodaBeanUtils.notNull(builder.businessDayAdjustment, "businessDayAdjustment");
    JodaBeanUtils.notNull(builder.referenceDateAdjuster, "referenceDateAdjuster");
    this.index = builder.index;
    this.businessDayAdjustment = builder.businessDayAdjustment;
    this.referenceDateAdjuster = builder.referenceDateAdjuster;
  }

  @Override
  public IborFutureConvention.Meta metaBean() {
    return IborFutureConvention.Meta.INSTANCE;
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
   * Gets the IBOR-like index.
   * <p>
   * The floating rate to be paid is based on this index
   * It will be a well known market index such as 'GBP-LIBOR-3M'.
   * @return the value of the property, not null
   */
  public IborIndex getIndex() {
    return index;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the business day adjustment to apply to the reference date, optional with defaulting getter.
   * <p>
   * The reference date, which is often the third Wednesday of the month, will be adjusted as defined here.
   * <p>
   * This will default to 'Following' using the index fixing calendar if not specified.
   * @return the value of the property, not null
   */
  public BusinessDayAdjustment getBusinessDayAdjustment() {
    return businessDayAdjustment;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the adjuster to find the reference date of the future.
   * <p>
   * The reference date of the future is the start date of the underlying synthetic deposit.
   * @return the value of the property, not null
   */
  public TemporalAdjuster getReferenceDateAdjuster() {
    return referenceDateAdjuster;
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
      IborFutureConvention other = (IborFutureConvention) obj;
      return JodaBeanUtils.equal(getIndex(), other.getIndex()) &&
          JodaBeanUtils.equal(getBusinessDayAdjustment(), other.getBusinessDayAdjustment()) &&
          JodaBeanUtils.equal(getReferenceDateAdjuster(), other.getReferenceDateAdjuster());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getIndex());
    hash = hash * 31 + JodaBeanUtils.hashCode(getBusinessDayAdjustment());
    hash = hash * 31 + JodaBeanUtils.hashCode(getReferenceDateAdjuster());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("IborFutureConvention{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("index").append('=').append(JodaBeanUtils.toString(getIndex())).append(',').append(' ');
    buf.append("businessDayAdjustment").append('=').append(JodaBeanUtils.toString(getBusinessDayAdjustment())).append(',').append(' ');
    buf.append("referenceDateAdjuster").append('=').append(JodaBeanUtils.toString(getReferenceDateAdjuster())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code IborFutureConvention}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code index} property.
     */
    private final MetaProperty<IborIndex> index = DirectMetaProperty.ofImmutable(
        this, "index", IborFutureConvention.class, IborIndex.class);
    /**
     * The meta-property for the {@code businessDayAdjustment} property.
     */
    private final MetaProperty<BusinessDayAdjustment> businessDayAdjustment = DirectMetaProperty.ofImmutable(
        this, "businessDayAdjustment", IborFutureConvention.class, BusinessDayAdjustment.class);
    /**
     * The meta-property for the {@code referenceDateAdjuster} property.
     */
    private final MetaProperty<TemporalAdjuster> referenceDateAdjuster = DirectMetaProperty.ofImmutable(
        this, "referenceDateAdjuster", IborFutureConvention.class, TemporalAdjuster.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "index",
        "businessDayAdjustment",
        "referenceDateAdjuster");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return index;
        case -1065319863:  // businessDayAdjustment
          return businessDayAdjustment;
        case -484811467:  // referenceDateAdjuster
          return referenceDateAdjuster;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public IborFutureConvention.Builder builder() {
      return new IborFutureConvention.Builder();
    }

    @Override
    public Class<? extends IborFutureConvention> beanType() {
      return IborFutureConvention.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code index} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<IborIndex> index() {
      return index;
    }

    /**
     * The meta-property for the {@code businessDayAdjustment} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BusinessDayAdjustment> businessDayAdjustment() {
      return businessDayAdjustment;
    }

    /**
     * The meta-property for the {@code referenceDateAdjuster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<TemporalAdjuster> referenceDateAdjuster() {
      return referenceDateAdjuster;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return ((IborFutureConvention) bean).getIndex();
        case -1065319863:  // businessDayAdjustment
          return ((IborFutureConvention) bean).getBusinessDayAdjustment();
        case -484811467:  // referenceDateAdjuster
          return ((IborFutureConvention) bean).getReferenceDateAdjuster();
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
   * The bean-builder for {@code IborFutureConvention}.
   */
  public static class Builder extends DirectFieldsBeanBuilder<IborFutureConvention> {

    private IborIndex index;
    private BusinessDayAdjustment businessDayAdjustment;
    private TemporalAdjuster referenceDateAdjuster;

    /**
     * Restricted constructor.
     */
    protected Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    protected Builder(IborFutureConvention beanToCopy) {
      this.index = beanToCopy.getIndex();
      this.businessDayAdjustment = beanToCopy.getBusinessDayAdjustment();
      this.referenceDateAdjuster = beanToCopy.getReferenceDateAdjuster();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return index;
        case -1065319863:  // businessDayAdjustment
          return businessDayAdjustment;
        case -484811467:  // referenceDateAdjuster
          return referenceDateAdjuster;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          this.index = (IborIndex) newValue;
          break;
        case -1065319863:  // businessDayAdjustment
          this.businessDayAdjustment = (BusinessDayAdjustment) newValue;
          break;
        case -484811467:  // referenceDateAdjuster
          this.referenceDateAdjuster = (TemporalAdjuster) newValue;
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
    public IborFutureConvention build() {
      return new IborFutureConvention(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the IBOR-like index.
     * <p>
     * The floating rate to be paid is based on this index
     * It will be a well known market index such as 'GBP-LIBOR-3M'.
     * @param index  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder index(IborIndex index) {
      JodaBeanUtils.notNull(index, "index");
      this.index = index;
      return this;
    }

    /**
     * Sets the business day adjustment to apply to the reference date, optional with defaulting getter.
     * <p>
     * The reference date, which is often the third Wednesday of the month, will be adjusted as defined here.
     * <p>
     * This will default to 'Following' using the index fixing calendar if not specified.
     * @param businessDayAdjustment  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder businessDayAdjustment(BusinessDayAdjustment businessDayAdjustment) {
      JodaBeanUtils.notNull(businessDayAdjustment, "businessDayAdjustment");
      this.businessDayAdjustment = businessDayAdjustment;
      return this;
    }

    /**
     * Sets the adjuster to find the reference date of the future.
     * <p>
     * The reference date of the future is the start date of the underlying synthetic deposit.
     * @param referenceDateAdjuster  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder referenceDateAdjuster(TemporalAdjuster referenceDateAdjuster) {
      JodaBeanUtils.notNull(referenceDateAdjuster, "referenceDateAdjuster");
      this.referenceDateAdjuster = referenceDateAdjuster;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("IborFutureConvention.Builder{");
      int len = buf.length();
      toString(buf);
      if (buf.length() > len) {
        buf.setLength(buf.length() - 2);
      }
      buf.append('}');
      return buf.toString();
    }

    protected void toString(StringBuilder buf) {
      buf.append("index").append('=').append(JodaBeanUtils.toString(index)).append(',').append(' ');
      buf.append("businessDayAdjustment").append('=').append(JodaBeanUtils.toString(businessDayAdjustment)).append(',').append(' ');
      buf.append("referenceDateAdjuster").append('=').append(JodaBeanUtils.toString(referenceDateAdjuster)).append(',').append(' ');
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
