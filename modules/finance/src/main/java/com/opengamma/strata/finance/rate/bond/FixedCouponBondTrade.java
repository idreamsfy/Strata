/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.rate.bond;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableDefaults;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.basics.currency.Payment;
import com.opengamma.strata.collect.id.LinkResolver;
import com.opengamma.strata.collect.id.StandardId;
import com.opengamma.strata.finance.SecurityLink;
import com.opengamma.strata.finance.SecurityTrade;
import com.opengamma.strata.finance.TradeInfo;

/**
 * A trade representing a fixed coupon bond.
 * <p>
 * A trade in an underlying {@link FixedCouponBond}.
 */
@BeanDefinition
public final class FixedCouponBondTrade
    implements SecurityTrade<FixedCouponBond>, ImmutableBean, Serializable {

  /**
   * The additional trade information, defaulted to an empty instance.
   * <p>
   * This allows additional information to be attached to the trade.
   */
  @PropertyDefinition(overrideGet = true)
  private final TradeInfo tradeInfo;
  /**
   * The link to the fixed coupon bond that was traded.
   * <p>
   * This property returns a link to the security via a {@link StandardId}.
   * See {@link #getSecurity()} and {@link SecurityLink} for more details.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final SecurityLink<FixedCouponBond> securityLink;
  /**
   * The quantity, indicating the number of bond contracts in the trade.
   * <p>
   * This will be positive if buying and negative if selling.
   */
  @PropertyDefinition
  private final long quantity;
  /**
   * The upfront fee payment of the bond trade.
   * <p>
   * The payment sign should be compatible with the product quantity, 
   * thus the payment is negative for positive quantity and positive for negative quantity.
   * <p>
   * Typically the date of this payment is the same as the settlement date in {@code tradeInfo}.
   */
  @PropertyDefinition(validate = "notNull")
  private final Payment payment;

  //-------------------------------------------------------------------------
  @SuppressWarnings({"rawtypes", "unchecked"})
  @ImmutableDefaults
  private static void applyDefaults(Builder builder) {
    builder.tradeInfo = TradeInfo.EMPTY;
  }

  //-------------------------------------------------------------------------
  @Override
  public SecurityTrade<FixedCouponBond> resolveLinks(LinkResolver resolver) {
    return resolver.resolveLinksIn(this, securityLink, resolved -> toBuilder().securityLink(resolved).build());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FixedCouponBondTrade}.
   * @return the meta-bean, not null
   */
  public static FixedCouponBondTrade.Meta meta() {
    return FixedCouponBondTrade.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FixedCouponBondTrade.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static FixedCouponBondTrade.Builder builder() {
    return new FixedCouponBondTrade.Builder();
  }

  private FixedCouponBondTrade(
      TradeInfo tradeInfo,
      SecurityLink<FixedCouponBond> securityLink,
      long quantity,
      Payment payment) {
    JodaBeanUtils.notNull(securityLink, "securityLink");
    JodaBeanUtils.notNull(payment, "payment");
    this.tradeInfo = tradeInfo;
    this.securityLink = securityLink;
    this.quantity = quantity;
    this.payment = payment;
  }

  @Override
  public FixedCouponBondTrade.Meta metaBean() {
    return FixedCouponBondTrade.Meta.INSTANCE;
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
   * Gets the additional trade information, defaulted to an empty instance.
   * <p>
   * This allows additional information to be attached to the trade.
   * @return the value of the property
   */
  @Override
  public TradeInfo getTradeInfo() {
    return tradeInfo;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the link to the fixed coupon bond that was traded.
   * <p>
   * This property returns a link to the security via a {@link StandardId}.
   * See {@link #getSecurity()} and {@link SecurityLink} for more details.
   * @return the value of the property, not null
   */
  @Override
  public SecurityLink<FixedCouponBond> getSecurityLink() {
    return securityLink;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the quantity, indicating the number of bond contracts in the trade.
   * <p>
   * This will be positive if buying and negative if selling.
   * @return the value of the property
   */
  public long getQuantity() {
    return quantity;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the upfront fee payment of the bond trade.
   * <p>
   * The payment sign should be compatible with the product quantity,
   * thus the payment is negative for positive quantity and positive for negative quantity.
   * <p>
   * Typically the date of this payment is the same as the settlement date in {@code tradeInfo}.
   * @return the value of the property, not null
   */
  public Payment getPayment() {
    return payment;
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
      FixedCouponBondTrade other = (FixedCouponBondTrade) obj;
      return JodaBeanUtils.equal(getTradeInfo(), other.getTradeInfo()) &&
          JodaBeanUtils.equal(getSecurityLink(), other.getSecurityLink()) &&
          (getQuantity() == other.getQuantity()) &&
          JodaBeanUtils.equal(getPayment(), other.getPayment());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getTradeInfo());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSecurityLink());
    hash = hash * 31 + JodaBeanUtils.hashCode(getQuantity());
    hash = hash * 31 + JodaBeanUtils.hashCode(getPayment());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("FixedCouponBondTrade{");
    buf.append("tradeInfo").append('=').append(getTradeInfo()).append(',').append(' ');
    buf.append("securityLink").append('=').append(getSecurityLink()).append(',').append(' ');
    buf.append("quantity").append('=').append(getQuantity()).append(',').append(' ');
    buf.append("payment").append('=').append(JodaBeanUtils.toString(getPayment()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FixedCouponBondTrade}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code tradeInfo} property.
     */
    private final MetaProperty<TradeInfo> tradeInfo = DirectMetaProperty.ofImmutable(
        this, "tradeInfo", FixedCouponBondTrade.class, TradeInfo.class);
    /**
     * The meta-property for the {@code securityLink} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<SecurityLink<FixedCouponBond>> securityLink = DirectMetaProperty.ofImmutable(
        this, "securityLink", FixedCouponBondTrade.class, (Class) SecurityLink.class);
    /**
     * The meta-property for the {@code quantity} property.
     */
    private final MetaProperty<Long> quantity = DirectMetaProperty.ofImmutable(
        this, "quantity", FixedCouponBondTrade.class, Long.TYPE);
    /**
     * The meta-property for the {@code payment} property.
     */
    private final MetaProperty<Payment> payment = DirectMetaProperty.ofImmutable(
        this, "payment", FixedCouponBondTrade.class, Payment.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "tradeInfo",
        "securityLink",
        "quantity",
        "payment");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 752580658:  // tradeInfo
          return tradeInfo;
        case 807992154:  // securityLink
          return securityLink;
        case -1285004149:  // quantity
          return quantity;
        case -786681338:  // payment
          return payment;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public FixedCouponBondTrade.Builder builder() {
      return new FixedCouponBondTrade.Builder();
    }

    @Override
    public Class<? extends FixedCouponBondTrade> beanType() {
      return FixedCouponBondTrade.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code tradeInfo} property.
     * @return the meta-property, not null
     */
    public MetaProperty<TradeInfo> tradeInfo() {
      return tradeInfo;
    }

    /**
     * The meta-property for the {@code securityLink} property.
     * @return the meta-property, not null
     */
    public MetaProperty<SecurityLink<FixedCouponBond>> securityLink() {
      return securityLink;
    }

    /**
     * The meta-property for the {@code quantity} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Long> quantity() {
      return quantity;
    }

    /**
     * The meta-property for the {@code payment} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Payment> payment() {
      return payment;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 752580658:  // tradeInfo
          return ((FixedCouponBondTrade) bean).getTradeInfo();
        case 807992154:  // securityLink
          return ((FixedCouponBondTrade) bean).getSecurityLink();
        case -1285004149:  // quantity
          return ((FixedCouponBondTrade) bean).getQuantity();
        case -786681338:  // payment
          return ((FixedCouponBondTrade) bean).getPayment();
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
   * The bean-builder for {@code FixedCouponBondTrade}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<FixedCouponBondTrade> {

    private TradeInfo tradeInfo;
    private SecurityLink<FixedCouponBond> securityLink;
    private long quantity;
    private Payment payment;

    /**
     * Restricted constructor.
     */
    private Builder() {
      applyDefaults(this);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(FixedCouponBondTrade beanToCopy) {
      this.tradeInfo = beanToCopy.getTradeInfo();
      this.securityLink = beanToCopy.getSecurityLink();
      this.quantity = beanToCopy.getQuantity();
      this.payment = beanToCopy.getPayment();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 752580658:  // tradeInfo
          return tradeInfo;
        case 807992154:  // securityLink
          return securityLink;
        case -1285004149:  // quantity
          return quantity;
        case -786681338:  // payment
          return payment;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 752580658:  // tradeInfo
          this.tradeInfo = (TradeInfo) newValue;
          break;
        case 807992154:  // securityLink
          this.securityLink = (SecurityLink<FixedCouponBond>) newValue;
          break;
        case -1285004149:  // quantity
          this.quantity = (Long) newValue;
          break;
        case -786681338:  // payment
          this.payment = (Payment) newValue;
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
    public FixedCouponBondTrade build() {
      return new FixedCouponBondTrade(
          tradeInfo,
          securityLink,
          quantity,
          payment);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the additional trade information, defaulted to an empty instance.
     * <p>
     * This allows additional information to be attached to the trade.
     * @param tradeInfo  the new value
     * @return this, for chaining, not null
     */
    public Builder tradeInfo(TradeInfo tradeInfo) {
      this.tradeInfo = tradeInfo;
      return this;
    }

    /**
     * Sets the link to the fixed coupon bond that was traded.
     * <p>
     * This property returns a link to the security via a {@link StandardId}.
     * See {@link #getSecurity()} and {@link SecurityLink} for more details.
     * @param securityLink  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder securityLink(SecurityLink<FixedCouponBond> securityLink) {
      JodaBeanUtils.notNull(securityLink, "securityLink");
      this.securityLink = securityLink;
      return this;
    }

    /**
     * Sets the quantity, indicating the number of bond contracts in the trade.
     * <p>
     * This will be positive if buying and negative if selling.
     * @param quantity  the new value
     * @return this, for chaining, not null
     */
    public Builder quantity(long quantity) {
      this.quantity = quantity;
      return this;
    }

    /**
     * Sets the upfront fee payment of the bond trade.
     * <p>
     * The payment sign should be compatible with the product quantity,
     * thus the payment is negative for positive quantity and positive for negative quantity.
     * <p>
     * Typically the date of this payment is the same as the settlement date in {@code tradeInfo}.
     * @param payment  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder payment(Payment payment) {
      JodaBeanUtils.notNull(payment, "payment");
      this.payment = payment;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(160);
      buf.append("FixedCouponBondTrade.Builder{");
      buf.append("tradeInfo").append('=').append(JodaBeanUtils.toString(tradeInfo)).append(',').append(' ');
      buf.append("securityLink").append('=').append(JodaBeanUtils.toString(securityLink)).append(',').append(' ');
      buf.append("quantity").append('=').append(JodaBeanUtils.toString(quantity)).append(',').append(' ');
      buf.append("payment").append('=').append(JodaBeanUtils.toString(payment));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
