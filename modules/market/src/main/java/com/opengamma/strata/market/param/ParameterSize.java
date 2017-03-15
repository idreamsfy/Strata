/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.param;

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
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.data.MarketDataName;

/**
 * The market data name and the associated number of parameters.
 * <p>
 * This holds the name and the number of parameters that define it.
 * For example, the curve name and the number of curve nodes.
 */
@BeanDefinition(builderScope = "private")
public final class ParameterSize
    implements ImmutableBean, Serializable {

  /**
   * The name of the market data.
   */
  @PropertyDefinition(validate = "notNull")
  private final MarketDataName<?> name;
  /**
   * The number of parameters.
   */
  @PropertyDefinition(validate = "ArgChecker.notNegativeOrZero")
  private final int parameterCount;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance, specifying the name and parameter count.
   * 
   * @param name  the curve name
   * @param parameterCount  the parameter count
   * @return the curve data
   */
  public static ParameterSize of(MarketDataName<?> name, int parameterCount) {
    return new ParameterSize(name, parameterCount);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ParameterSize}.
   * @return the meta-bean, not null
   */
  public static ParameterSize.Meta meta() {
    return ParameterSize.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ParameterSize.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private ParameterSize(
      MarketDataName<?> name,
      int parameterCount) {
    JodaBeanUtils.notNull(name, "name");
    ArgChecker.notNegativeOrZero(parameterCount, "parameterCount");
    this.name = name;
    this.parameterCount = parameterCount;
  }

  @Override
  public ParameterSize.Meta metaBean() {
    return ParameterSize.Meta.INSTANCE;
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
   * Gets the name of the market data.
   * @return the value of the property, not null
   */
  public MarketDataName<?> getName() {
    return name;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the number of parameters.
   * @return the value of the property
   */
  public int getParameterCount() {
    return parameterCount;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ParameterSize other = (ParameterSize) obj;
      return JodaBeanUtils.equal(name, other.name) &&
          (parameterCount == other.parameterCount);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(name);
    hash = hash * 31 + JodaBeanUtils.hashCode(parameterCount);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("ParameterSize{");
    buf.append("name").append('=').append(name).append(',').append(' ');
    buf.append("parameterCount").append('=').append(JodaBeanUtils.toString(parameterCount));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ParameterSize}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<MarketDataName<?>> name = DirectMetaProperty.ofImmutable(
        this, "name", ParameterSize.class, (Class) MarketDataName.class);
    /**
     * The meta-property for the {@code parameterCount} property.
     */
    private final MetaProperty<Integer> parameterCount = DirectMetaProperty.ofImmutable(
        this, "parameterCount", ParameterSize.class, Integer.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "name",
        "parameterCount");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case 1107332838:  // parameterCount
          return parameterCount;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ParameterSize> builder() {
      return new ParameterSize.Builder();
    }

    @Override
    public Class<? extends ParameterSize> beanType() {
      return ParameterSize.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public MetaProperty<MarketDataName<?>> name() {
      return name;
    }

    /**
     * The meta-property for the {@code parameterCount} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Integer> parameterCount() {
      return parameterCount;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((ParameterSize) bean).getName();
        case 1107332838:  // parameterCount
          return ((ParameterSize) bean).getParameterCount();
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
   * The bean-builder for {@code ParameterSize}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<ParameterSize> {

    private MarketDataName<?> name;
    private int parameterCount;

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(meta());
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case 1107332838:  // parameterCount
          return parameterCount;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          this.name = (MarketDataName<?>) newValue;
          break;
        case 1107332838:  // parameterCount
          this.parameterCount = (Integer) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public ParameterSize build() {
      return new ParameterSize(
          name,
          parameterCount);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("ParameterSize.Builder{");
      buf.append("name").append('=').append(JodaBeanUtils.toString(name)).append(',').append(' ');
      buf.append("parameterCount").append('=').append(JodaBeanUtils.toString(parameterCount));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
