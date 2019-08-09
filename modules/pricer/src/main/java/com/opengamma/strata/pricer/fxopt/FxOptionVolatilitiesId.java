/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.fxopt;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.TypedMetaBean;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.data.MarketDataName;
import com.opengamma.strata.data.NamedMarketDataId;

/**
 * An identifier used to access FX option volatilities by name.
 * <p>
 * This is used when there is a need to obtain an instance of {@link FxOptionVolatilities}.
 */
@BeanDefinition(style = "light", cacheHashCode = true)
public final class FxOptionVolatilitiesId
    implements NamedMarketDataId<FxOptionVolatilities>, ImmutableBean, Serializable {

  /**
   * The name of the volatilities.
   */
  @PropertyDefinition(validate = "notNull")
  private final FxOptionVolatilitiesName name;

  //-------------------------------------------------------------------------
  /**
   * Obtains an identifier used to find FX option volatilities.
   *
   * @param name  the name
   * @return an identifier for the volatilities
   */
  public static FxOptionVolatilitiesId of(String name) {
    return new FxOptionVolatilitiesId(FxOptionVolatilitiesName.of(name));
  }

  /**
   * Obtains an identifier used to find FX option volatilities.
   *
   * @param name  the name
   * @return an identifier for the volatilities
   */
  public static FxOptionVolatilitiesId of(FxOptionVolatilitiesName name) {
    return new FxOptionVolatilitiesId(name);
  }

  //-------------------------------------------------------------------------
  @Override
  public Class<FxOptionVolatilities> getMarketDataType() {
    return FxOptionVolatilities.class;
  }

  @Override
  public MarketDataName<FxOptionVolatilities> getMarketDataName() {
    return name;
  }

  @Override
  public String toString() {
    return "FxOptionVolatilitiesId:" + name;
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code FxOptionVolatilitiesId}.
   */
  private static final TypedMetaBean<FxOptionVolatilitiesId> META_BEAN =
      LightMetaBean.of(
          FxOptionVolatilitiesId.class,
          MethodHandles.lookup(),
          new String[] {
              "name"},
          new Object[0]);

  /**
   * The meta-bean for {@code FxOptionVolatilitiesId}.
   * @return the meta-bean, not null
   */
  public static TypedMetaBean<FxOptionVolatilitiesId> meta() {
    return META_BEAN;
  }

  static {
    MetaBean.register(META_BEAN);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The cached hash code, using the racy single-check idiom.
   */
  private transient int cacheHashCode;

  private FxOptionVolatilitiesId(
      FxOptionVolatilitiesName name) {
    JodaBeanUtils.notNull(name, "name");
    this.name = name;
  }

  @Override
  public TypedMetaBean<FxOptionVolatilitiesId> metaBean() {
    return META_BEAN;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the name of the volatilities.
   * @return the value of the property, not null
   */
  public FxOptionVolatilitiesName getName() {
    return name;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FxOptionVolatilitiesId other = (FxOptionVolatilitiesId) obj;
      return JodaBeanUtils.equal(name, other.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = cacheHashCode;
    if (hash == 0) {
      hash = getClass().hashCode();
      hash = hash * 31 + JodaBeanUtils.hashCode(name);
      cacheHashCode = hash;
    }
    return hash;
  }

  //-------------------------- AUTOGENERATED END --------------------------
}
