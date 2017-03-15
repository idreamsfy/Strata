/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.deposit;

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

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.product.ProductTrade;
import com.opengamma.strata.product.ResolvableTrade;
import com.opengamma.strata.product.TradeInfo;

/**
 * A trade in a term deposit.
 * <p>
 * An Over-The-Counter (OTC) trade in a {@link TermDeposit}.
 * <p>
 * A term deposit is a financial instrument that provides a fixed rate of interest on
 * an amount for a specific term.
 * For example, investing GBP 1,000 for 3 months at a 1% interest rate.
 */
@BeanDefinition
public final class TermDepositTrade
    implements ProductTrade, ResolvableTrade<ResolvedTermDepositTrade>, ImmutableBean, Serializable {

  /**
   * The additional trade information, defaulted to an empty instance.
   * <p>
   * This allows additional information to be attached to the trade.
   */
  @PropertyDefinition(overrideGet = true)
  private final TradeInfo info;
  /**
   * The term deposit product that was agreed when the trade occurred.
   * <p>
   * The product captures the contracted financial details of the trade.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final TermDeposit product;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance of a Term Deposit trade.
   * 
   * @param info  the trade info
   * @param product  the product
   * @return the trade
   */
  public static TermDepositTrade of(TradeInfo info, TermDeposit product) {
    return new TermDepositTrade(info, product);
  }

  @ImmutableDefaults
  private static void applyDefaults(Builder builder) {
    builder.info = TradeInfo.empty();
  }

  //-------------------------------------------------------------------------
  @Override
  public ResolvedTermDepositTrade resolve(ReferenceData refData) {
    return ResolvedTermDepositTrade.builder()
        .info(info)
        .product(product.resolve(refData))
        .build();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code TermDepositTrade}.
   * @return the meta-bean, not null
   */
  public static TermDepositTrade.Meta meta() {
    return TermDepositTrade.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(TermDepositTrade.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static TermDepositTrade.Builder builder() {
    return new TermDepositTrade.Builder();
  }

  private TermDepositTrade(
      TradeInfo info,
      TermDeposit product) {
    JodaBeanUtils.notNull(product, "product");
    this.info = info;
    this.product = product;
  }

  @Override
  public TermDepositTrade.Meta metaBean() {
    return TermDepositTrade.Meta.INSTANCE;
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
  public TradeInfo getInfo() {
    return info;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the term deposit product that was agreed when the trade occurred.
   * <p>
   * The product captures the contracted financial details of the trade.
   * @return the value of the property, not null
   */
  @Override
  public TermDeposit getProduct() {
    return product;
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
      TermDepositTrade other = (TermDepositTrade) obj;
      return JodaBeanUtils.equal(info, other.info) &&
          JodaBeanUtils.equal(product, other.product);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(info);
    hash = hash * 31 + JodaBeanUtils.hashCode(product);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("TermDepositTrade{");
    buf.append("info").append('=').append(info).append(',').append(' ');
    buf.append("product").append('=').append(JodaBeanUtils.toString(product));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code TermDepositTrade}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code info} property.
     */
    private final MetaProperty<TradeInfo> info = DirectMetaProperty.ofImmutable(
        this, "info", TermDepositTrade.class, TradeInfo.class);
    /**
     * The meta-property for the {@code product} property.
     */
    private final MetaProperty<TermDeposit> product = DirectMetaProperty.ofImmutable(
        this, "product", TermDepositTrade.class, TermDeposit.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "info",
        "product");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3237038:  // info
          return info;
        case -309474065:  // product
          return product;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public TermDepositTrade.Builder builder() {
      return new TermDepositTrade.Builder();
    }

    @Override
    public Class<? extends TermDepositTrade> beanType() {
      return TermDepositTrade.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code info} property.
     * @return the meta-property, not null
     */
    public MetaProperty<TradeInfo> info() {
      return info;
    }

    /**
     * The meta-property for the {@code product} property.
     * @return the meta-property, not null
     */
    public MetaProperty<TermDeposit> product() {
      return product;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3237038:  // info
          return ((TermDepositTrade) bean).getInfo();
        case -309474065:  // product
          return ((TermDepositTrade) bean).getProduct();
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
   * The bean-builder for {@code TermDepositTrade}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<TermDepositTrade> {

    private TradeInfo info;
    private TermDeposit product;

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
    private Builder(TermDepositTrade beanToCopy) {
      this.info = beanToCopy.getInfo();
      this.product = beanToCopy.getProduct();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3237038:  // info
          return info;
        case -309474065:  // product
          return product;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3237038:  // info
          this.info = (TradeInfo) newValue;
          break;
        case -309474065:  // product
          this.product = (TermDeposit) newValue;
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
    public TermDepositTrade build() {
      return new TermDepositTrade(
          info,
          product);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the additional trade information, defaulted to an empty instance.
     * <p>
     * This allows additional information to be attached to the trade.
     * @param info  the new value
     * @return this, for chaining, not null
     */
    public Builder info(TradeInfo info) {
      this.info = info;
      return this;
    }

    /**
     * Sets the term deposit product that was agreed when the trade occurred.
     * <p>
     * The product captures the contracted financial details of the trade.
     * @param product  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder product(TermDeposit product) {
      JodaBeanUtils.notNull(product, "product");
      this.product = product;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("TermDepositTrade.Builder{");
      buf.append("info").append('=').append(JodaBeanUtils.toString(info)).append(',').append(' ');
      buf.append("product").append('=').append(JodaBeanUtils.toString(product));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
