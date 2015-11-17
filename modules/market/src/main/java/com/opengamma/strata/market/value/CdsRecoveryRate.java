/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.value;

import java.io.Serializable;
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

/**
 * The expected recovery rate for a CDS product based upon the underlying issue or index.
 */
@BeanDefinition(builderScope = "private")
public final class CdsRecoveryRate
    implements ImmutableBean, Serializable {

  /**
   * The recovery rate.
   */
  @PropertyDefinition(validate = "notNull")
  private final double recoveryRate;

  //-------------------------------------------------------------------------
  /**
   * Creates an instance of the recovery rate.
   *
   * @param recoveryRate  the recovery rate
   * @return the recovery rate
   */
  public static CdsRecoveryRate of(double recoveryRate) {
    return new CdsRecoveryRate(recoveryRate);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CdsRecoveryRate}.
   * @return the meta-bean, not null
   */
  public static CdsRecoveryRate.Meta meta() {
    return CdsRecoveryRate.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CdsRecoveryRate.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private CdsRecoveryRate(
      double recoveryRate) {
    JodaBeanUtils.notNull(recoveryRate, "recoveryRate");
    this.recoveryRate = recoveryRate;
  }

  @Override
  public CdsRecoveryRate.Meta metaBean() {
    return CdsRecoveryRate.Meta.INSTANCE;
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
   * Gets the recovery rate.
   * @return the value of the property, not null
   */
  public double getRecoveryRate() {
    return recoveryRate;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CdsRecoveryRate other = (CdsRecoveryRate) obj;
      return JodaBeanUtils.equal(recoveryRate, other.recoveryRate);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(recoveryRate);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("CdsRecoveryRate{");
    buf.append("recoveryRate").append('=').append(JodaBeanUtils.toString(recoveryRate));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CdsRecoveryRate}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code recoveryRate} property.
     */
    private final MetaProperty<Double> recoveryRate = DirectMetaProperty.ofImmutable(
        this, "recoveryRate", CdsRecoveryRate.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "recoveryRate");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2002873877:  // recoveryRate
          return recoveryRate;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CdsRecoveryRate> builder() {
      return new CdsRecoveryRate.Builder();
    }

    @Override
    public Class<? extends CdsRecoveryRate> beanType() {
      return CdsRecoveryRate.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code recoveryRate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> recoveryRate() {
      return recoveryRate;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 2002873877:  // recoveryRate
          return ((CdsRecoveryRate) bean).getRecoveryRate();
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
   * The bean-builder for {@code CdsRecoveryRate}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<CdsRecoveryRate> {

    private double recoveryRate;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2002873877:  // recoveryRate
          return recoveryRate;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 2002873877:  // recoveryRate
          this.recoveryRate = (Double) newValue;
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
    public CdsRecoveryRate build() {
      return new CdsRecoveryRate(
          recoveryRate);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("CdsRecoveryRate.Builder{");
      buf.append("recoveryRate").append('=').append(JodaBeanUtils.toString(recoveryRate));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
