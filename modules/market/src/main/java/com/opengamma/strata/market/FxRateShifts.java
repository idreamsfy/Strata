/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.CurrencyPair;
import com.opengamma.strata.basics.currency.FxRate;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.data.scenario.MarketDataBox;
import com.opengamma.strata.data.scenario.ScenarioPerturbation;

/**
 * A perturbation that applies different shifts to an FX rate.
 * <p>
 * This class contains shifts, each of which is associated with a scenario and applied to an FX rate based on the shift type.
 */
@BeanDefinition(builderScope = "private", constructorScope = "package")
public final class FxRateShifts
    implements ScenarioPerturbation<FxRate>, ImmutableBean, Serializable {

  /** 
   * Logger. 
   */
  private static final Logger log = LoggerFactory.getLogger(FxRateShifts.class);

  /**
   * The type of shift applied to the FX rate.
   */
  @PropertyDefinition(validate = "notNull")
  private final ShiftType shiftType;

  /**
   * The shifts to apply to {@code FxRate}.
   * <p>
   * Each element in the array corresponds to each scenario.
   */
  @PropertyDefinition(validate = "notNull")
  private final DoubleArray shiftAmount;

  /**
   * The currency pair for which the shifts are applied.
   * <p>
   * This also defines the direction of the FX rate to be shifted.
   */
  @PropertyDefinition(validate = "notNull")
  private final CurrencyPair currencyPair;

  //-------------------------------------------------------------------------
  /**
   * Creates an instance.
   * 
   * @param shiftType  the shift type
   * @param shiftAmount  the shift amount
   * @param currencyPair  the currency pair
   * @return the instance
   */
  public static FxRateShifts of(ShiftType shiftType, DoubleArray shiftAmount, CurrencyPair currencyPair) {
    return new FxRateShifts(shiftType, shiftAmount, currencyPair);
  }

  //-------------------------------------------------------------------------
  @Override
  public MarketDataBox<FxRate> applyTo(MarketDataBox<FxRate> marketData, ReferenceData refData) {
    log.debug("Applying {} shift to FX rate '{}'", shiftType,
        marketData.getValue(0).getPair().toString());
    return marketData.mapWithIndex(
        getScenarioCount(),
        (fxRate, scenarioIndex) -> FxRate.of(
            currencyPair,
            shiftType.applyShift(fxRate.fxRate(currencyPair), shiftAmount.get(scenarioIndex))));
  }

  @Override
  public int getScenarioCount() {
    return shiftAmount.size();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FxRateShifts}.
   * @return the meta-bean, not null
   */
  public static FxRateShifts.Meta meta() {
    return FxRateShifts.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FxRateShifts.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance.
   * @param shiftType  the value of the property, not null
   * @param shiftAmount  the value of the property, not null
   * @param currencyPair  the value of the property, not null
   */
  FxRateShifts(
      ShiftType shiftType,
      DoubleArray shiftAmount,
      CurrencyPair currencyPair) {
    JodaBeanUtils.notNull(shiftType, "shiftType");
    JodaBeanUtils.notNull(shiftAmount, "shiftAmount");
    JodaBeanUtils.notNull(currencyPair, "currencyPair");
    this.shiftType = shiftType;
    this.shiftAmount = shiftAmount;
    this.currencyPair = currencyPair;
  }

  @Override
  public FxRateShifts.Meta metaBean() {
    return FxRateShifts.Meta.INSTANCE;
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
   * Gets the type of shift applied to the FX rate.
   * @return the value of the property, not null
   */
  public ShiftType getShiftType() {
    return shiftType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the shifts to apply to {@code FxRate}.
   * <p>
   * Each element in the array corresponds to each scenario.
   * @return the value of the property, not null
   */
  public DoubleArray getShiftAmount() {
    return shiftAmount;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency pair for which the shifts are applied.
   * <p>
   * This also defines the direction of the FX rate to be shifted.
   * @return the value of the property, not null
   */
  public CurrencyPair getCurrencyPair() {
    return currencyPair;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FxRateShifts other = (FxRateShifts) obj;
      return JodaBeanUtils.equal(shiftType, other.shiftType) &&
          JodaBeanUtils.equal(shiftAmount, other.shiftAmount) &&
          JodaBeanUtils.equal(currencyPair, other.currencyPair);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(shiftType);
    hash = hash * 31 + JodaBeanUtils.hashCode(shiftAmount);
    hash = hash * 31 + JodaBeanUtils.hashCode(currencyPair);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("FxRateShifts{");
    buf.append("shiftType").append('=').append(shiftType).append(',').append(' ');
    buf.append("shiftAmount").append('=').append(shiftAmount).append(',').append(' ');
    buf.append("currencyPair").append('=').append(JodaBeanUtils.toString(currencyPair));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FxRateShifts}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code shiftType} property.
     */
    private final MetaProperty<ShiftType> shiftType = DirectMetaProperty.ofImmutable(
        this, "shiftType", FxRateShifts.class, ShiftType.class);
    /**
     * The meta-property for the {@code shiftAmount} property.
     */
    private final MetaProperty<DoubleArray> shiftAmount = DirectMetaProperty.ofImmutable(
        this, "shiftAmount", FxRateShifts.class, DoubleArray.class);
    /**
     * The meta-property for the {@code currencyPair} property.
     */
    private final MetaProperty<CurrencyPair> currencyPair = DirectMetaProperty.ofImmutable(
        this, "currencyPair", FxRateShifts.class, CurrencyPair.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "shiftType",
        "shiftAmount",
        "currencyPair");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 893345500:  // shiftType
          return shiftType;
        case -1043480710:  // shiftAmount
          return shiftAmount;
        case 1005147787:  // currencyPair
          return currencyPair;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FxRateShifts> builder() {
      return new FxRateShifts.Builder();
    }

    @Override
    public Class<? extends FxRateShifts> beanType() {
      return FxRateShifts.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code shiftType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ShiftType> shiftType() {
      return shiftType;
    }

    /**
     * The meta-property for the {@code shiftAmount} property.
     * @return the meta-property, not null
     */
    public MetaProperty<DoubleArray> shiftAmount() {
      return shiftAmount;
    }

    /**
     * The meta-property for the {@code currencyPair} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurrencyPair> currencyPair() {
      return currencyPair;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 893345500:  // shiftType
          return ((FxRateShifts) bean).getShiftType();
        case -1043480710:  // shiftAmount
          return ((FxRateShifts) bean).getShiftAmount();
        case 1005147787:  // currencyPair
          return ((FxRateShifts) bean).getCurrencyPair();
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
   * The bean-builder for {@code FxRateShifts}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<FxRateShifts> {

    private ShiftType shiftType;
    private DoubleArray shiftAmount;
    private CurrencyPair currencyPair;

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
        case 893345500:  // shiftType
          return shiftType;
        case -1043480710:  // shiftAmount
          return shiftAmount;
        case 1005147787:  // currencyPair
          return currencyPair;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 893345500:  // shiftType
          this.shiftType = (ShiftType) newValue;
          break;
        case -1043480710:  // shiftAmount
          this.shiftAmount = (DoubleArray) newValue;
          break;
        case 1005147787:  // currencyPair
          this.currencyPair = (CurrencyPair) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public FxRateShifts build() {
      return new FxRateShifts(
          shiftType,
          shiftAmount,
          currencyPair);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("FxRateShifts.Builder{");
      buf.append("shiftType").append('=').append(JodaBeanUtils.toString(shiftType)).append(',').append(' ');
      buf.append("shiftAmount").append('=').append(JodaBeanUtils.toString(shiftAmount)).append(',').append(' ');
      buf.append("currencyPair").append('=').append(JodaBeanUtils.toString(currencyPair));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
