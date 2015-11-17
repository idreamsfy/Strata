/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.rate.future;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableValidator;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.product.Product;
import com.opengamma.strata.product.rate.swap.ExpandedSwapLeg;
import com.opengamma.strata.product.rate.swap.NotionalExchange;
import com.opengamma.strata.product.rate.swap.NotionalPaymentPeriod;
import com.opengamma.strata.product.rate.swap.PaymentEvent;
import com.opengamma.strata.product.rate.swap.PaymentPeriod;
import com.opengamma.strata.product.rate.swap.Swap;
import com.opengamma.strata.product.rate.swap.SwapLeg;
import com.opengamma.strata.product.rate.swap.SwapLegType;

/**
 * A deliverable swap futures contract.
 * <p>
 * A deliverable swap future is a financial instrument that physically settles an interest rate swap on a future date. 
 * The delivered swap is cleared by a central counterparty. 
 * The last future price before delivery is quoted in term of the underlying swap present value. 
 * The futures product is margined on a daily basis.
 */
@BeanDefinition
public final class DeliverableSwapFuture
    implements Product, ImmutableBean, Serializable {

  /**
   * The notional of the futures. 
   * <p>
   * This is also called face value or contract value.
   */
  @PropertyDefinition(validate = "ArgChecker.notNegative")
  private final double notional;
  /**
   * The delivery date. 
   * <p>
   * The underlying swap is delivered on this date.
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate deliveryDate;
  /**
   * The last date of trading.
   * <p>
   * This date must be before the delivery date of the underlying swap. 
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate lastTradeDate;
  /**
   * The underlying swap.
   * <p>
   * The delivery date of the future is typically the first accrual date of the underlying swap. 
   * The swap should be a receiver swap of notional 1. 
   */
  @PropertyDefinition(validate = "notNull")
  private final Swap underlyingSwap;

  //-------------------------------------------------------------------------
  @ImmutableValidator
  private void validate() {
    ArgChecker.inOrderOrEqual(deliveryDate, underlyingSwap.getStartDate(), "deliveryDate", "startDate");
    ArgChecker.isFalse(underlyingSwap.isCrossCurrency(), "underlying swap must not be cross currency");
    for (SwapLeg swapLeg : underlyingSwap.getLegs()) {
      if (swapLeg.getType().equals(SwapLegType.FIXED)) {
        ArgChecker.isTrue(swapLeg.getPayReceive().isReceive(), "underlying must be receiver swap");
      }
      ExpandedSwapLeg expandedSwapLeg = swapLeg.expand();
      for (PaymentEvent event : expandedSwapLeg.getPaymentEvents()) {
        ArgChecker.isTrue(event instanceof NotionalExchange, "PaymentEvent must be NotionalExchange");
        NotionalExchange notioanlEvent = (NotionalExchange) event;
        ArgChecker.isTrue(Math.abs(notioanlEvent.getPaymentAmount().getAmount()) == 1d,
            "notional of underlying swap must be unity");
      }
      for (PaymentPeriod period : expandedSwapLeg.getPaymentPeriods()) {
        ArgChecker.isTrue(period instanceof NotionalPaymentPeriod, "PaymentPeriod must be NotionalPaymentPeriod");
        NotionalPaymentPeriod notioanlPeriod = (NotionalPaymentPeriod) period;
        ArgChecker.isTrue(Math.abs(notioanlPeriod.getNotionalAmount().getAmount()) == 1d,
            "notional of underlying swap must be unity");
      }
    }
    ArgChecker.inOrderOrEqual(lastTradeDate, deliveryDate, "lastTradeDate", "deliveryDate");
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the currency of the underlying swap.
   * <p>
   * The underlying swap must have a single currency.
   * 
   * @return the currency of the swap
   */
  public Currency getCurrency() {
    return underlyingSwap.getReceiveLeg().get().getCurrency();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DeliverableSwapFuture}.
   * @return the meta-bean, not null
   */
  public static DeliverableSwapFuture.Meta meta() {
    return DeliverableSwapFuture.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(DeliverableSwapFuture.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static DeliverableSwapFuture.Builder builder() {
    return new DeliverableSwapFuture.Builder();
  }

  private DeliverableSwapFuture(
      double notional,
      LocalDate deliveryDate,
      LocalDate lastTradeDate,
      Swap underlyingSwap) {
    ArgChecker.notNegative(notional, "notional");
    JodaBeanUtils.notNull(deliveryDate, "deliveryDate");
    JodaBeanUtils.notNull(lastTradeDate, "lastTradeDate");
    JodaBeanUtils.notNull(underlyingSwap, "underlyingSwap");
    this.notional = notional;
    this.deliveryDate = deliveryDate;
    this.lastTradeDate = lastTradeDate;
    this.underlyingSwap = underlyingSwap;
    validate();
  }

  @Override
  public DeliverableSwapFuture.Meta metaBean() {
    return DeliverableSwapFuture.Meta.INSTANCE;
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
   * Gets the notional of the futures.
   * <p>
   * This is also called face value or contract value.
   * @return the value of the property
   */
  public double getNotional() {
    return notional;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the delivery date.
   * <p>
   * The underlying swap is delivered on this date.
   * @return the value of the property, not null
   */
  public LocalDate getDeliveryDate() {
    return deliveryDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the last date of trading.
   * <p>
   * This date must be before the delivery date of the underlying swap.
   * @return the value of the property, not null
   */
  public LocalDate getLastTradeDate() {
    return lastTradeDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlying swap.
   * <p>
   * The delivery date of the future is typically the first accrual date of the underlying swap.
   * The swap should be a receiver swap of notional 1.
   * @return the value of the property, not null
   */
  public Swap getUnderlyingSwap() {
    return underlyingSwap;
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
      DeliverableSwapFuture other = (DeliverableSwapFuture) obj;
      return JodaBeanUtils.equal(notional, other.notional) &&
          JodaBeanUtils.equal(deliveryDate, other.deliveryDate) &&
          JodaBeanUtils.equal(lastTradeDate, other.lastTradeDate) &&
          JodaBeanUtils.equal(underlyingSwap, other.underlyingSwap);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(notional);
    hash = hash * 31 + JodaBeanUtils.hashCode(deliveryDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(lastTradeDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(underlyingSwap);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("DeliverableSwapFuture{");
    buf.append("notional").append('=').append(notional).append(',').append(' ');
    buf.append("deliveryDate").append('=').append(deliveryDate).append(',').append(' ');
    buf.append("lastTradeDate").append('=').append(lastTradeDate).append(',').append(' ');
    buf.append("underlyingSwap").append('=').append(JodaBeanUtils.toString(underlyingSwap));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code DeliverableSwapFuture}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code notional} property.
     */
    private final MetaProperty<Double> notional = DirectMetaProperty.ofImmutable(
        this, "notional", DeliverableSwapFuture.class, Double.TYPE);
    /**
     * The meta-property for the {@code deliveryDate} property.
     */
    private final MetaProperty<LocalDate> deliveryDate = DirectMetaProperty.ofImmutable(
        this, "deliveryDate", DeliverableSwapFuture.class, LocalDate.class);
    /**
     * The meta-property for the {@code lastTradeDate} property.
     */
    private final MetaProperty<LocalDate> lastTradeDate = DirectMetaProperty.ofImmutable(
        this, "lastTradeDate", DeliverableSwapFuture.class, LocalDate.class);
    /**
     * The meta-property for the {@code underlyingSwap} property.
     */
    private final MetaProperty<Swap> underlyingSwap = DirectMetaProperty.ofImmutable(
        this, "underlyingSwap", DeliverableSwapFuture.class, Swap.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "notional",
        "deliveryDate",
        "lastTradeDate",
        "underlyingSwap");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1585636160:  // notional
          return notional;
        case 681469378:  // deliveryDate
          return deliveryDate;
        case -1041950404:  // lastTradeDate
          return lastTradeDate;
        case 1497421456:  // underlyingSwap
          return underlyingSwap;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public DeliverableSwapFuture.Builder builder() {
      return new DeliverableSwapFuture.Builder();
    }

    @Override
    public Class<? extends DeliverableSwapFuture> beanType() {
      return DeliverableSwapFuture.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code notional} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> notional() {
      return notional;
    }

    /**
     * The meta-property for the {@code deliveryDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> deliveryDate() {
      return deliveryDate;
    }

    /**
     * The meta-property for the {@code lastTradeDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> lastTradeDate() {
      return lastTradeDate;
    }

    /**
     * The meta-property for the {@code underlyingSwap} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Swap> underlyingSwap() {
      return underlyingSwap;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1585636160:  // notional
          return ((DeliverableSwapFuture) bean).getNotional();
        case 681469378:  // deliveryDate
          return ((DeliverableSwapFuture) bean).getDeliveryDate();
        case -1041950404:  // lastTradeDate
          return ((DeliverableSwapFuture) bean).getLastTradeDate();
        case 1497421456:  // underlyingSwap
          return ((DeliverableSwapFuture) bean).getUnderlyingSwap();
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
   * The bean-builder for {@code DeliverableSwapFuture}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<DeliverableSwapFuture> {

    private double notional;
    private LocalDate deliveryDate;
    private LocalDate lastTradeDate;
    private Swap underlyingSwap;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(DeliverableSwapFuture beanToCopy) {
      this.notional = beanToCopy.getNotional();
      this.deliveryDate = beanToCopy.getDeliveryDate();
      this.lastTradeDate = beanToCopy.getLastTradeDate();
      this.underlyingSwap = beanToCopy.getUnderlyingSwap();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1585636160:  // notional
          return notional;
        case 681469378:  // deliveryDate
          return deliveryDate;
        case -1041950404:  // lastTradeDate
          return lastTradeDate;
        case 1497421456:  // underlyingSwap
          return underlyingSwap;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1585636160:  // notional
          this.notional = (Double) newValue;
          break;
        case 681469378:  // deliveryDate
          this.deliveryDate = (LocalDate) newValue;
          break;
        case -1041950404:  // lastTradeDate
          this.lastTradeDate = (LocalDate) newValue;
          break;
        case 1497421456:  // underlyingSwap
          this.underlyingSwap = (Swap) newValue;
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
    public DeliverableSwapFuture build() {
      return new DeliverableSwapFuture(
          notional,
          deliveryDate,
          lastTradeDate,
          underlyingSwap);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the notional of the futures.
     * <p>
     * This is also called face value or contract value.
     * @param notional  the new value
     * @return this, for chaining, not null
     */
    public Builder notional(double notional) {
      ArgChecker.notNegative(notional, "notional");
      this.notional = notional;
      return this;
    }

    /**
     * Sets the delivery date.
     * <p>
     * The underlying swap is delivered on this date.
     * @param deliveryDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder deliveryDate(LocalDate deliveryDate) {
      JodaBeanUtils.notNull(deliveryDate, "deliveryDate");
      this.deliveryDate = deliveryDate;
      return this;
    }

    /**
     * Sets the last date of trading.
     * <p>
     * This date must be before the delivery date of the underlying swap.
     * @param lastTradeDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder lastTradeDate(LocalDate lastTradeDate) {
      JodaBeanUtils.notNull(lastTradeDate, "lastTradeDate");
      this.lastTradeDate = lastTradeDate;
      return this;
    }

    /**
     * Sets the underlying swap.
     * <p>
     * The delivery date of the future is typically the first accrual date of the underlying swap.
     * The swap should be a receiver swap of notional 1.
     * @param underlyingSwap  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder underlyingSwap(Swap underlyingSwap) {
      JodaBeanUtils.notNull(underlyingSwap, "underlyingSwap");
      this.underlyingSwap = underlyingSwap;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(160);
      buf.append("DeliverableSwapFuture.Builder{");
      buf.append("notional").append('=').append(JodaBeanUtils.toString(notional)).append(',').append(' ');
      buf.append("deliveryDate").append('=').append(JodaBeanUtils.toString(deliveryDate)).append(',').append(' ');
      buf.append("lastTradeDate").append('=').append(JodaBeanUtils.toString(lastTradeDate)).append(',').append(' ');
      buf.append("underlyingSwap").append('=').append(JodaBeanUtils.toString(underlyingSwap));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
