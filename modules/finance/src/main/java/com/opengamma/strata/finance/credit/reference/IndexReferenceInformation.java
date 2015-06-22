/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.credit.reference;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
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

import com.opengamma.strata.collect.id.StandardId;
import com.opengamma.strata.finance.credit.RestructuringClause;

/**
 * contains all the terms relevant to defining the Credit DefaultSwap Index.
 */
@BeanDefinition
public final class IndexReferenceInformation
    implements ReferenceInformation, ImmutableBean, Serializable {

  /**
   * A CDS index identifier (e.g. RED pair code)
   */
  @PropertyDefinition(validate = "notNull")
  final StandardId indexId;

  /**
   * A CDS index series identifier, e.g. 1, 2, 3 etc
   */
  @PropertyDefinition(validate = "notNull")
  final int indexSeries;

  /**
   * A CDS index series version identifier, e.g. 1, 2, 3 etc
   */
  @PropertyDefinition(validate = "notNull")
  final int indexAnnexVersion;

  /**
   * Specifies the type of restructuring that is applicable.
   * This is used as part of the key in determining which curves will be used to price
   * the swap.
   */
  @PropertyDefinition(validate = "notNull")
  final RestructuringClause restructuringClause;

  /**
   * @return value that determines this is an index trade and not a single name
   */
  @Override
  public ReferenceInformationType getType() {
    return ReferenceInformationType.INDEX;
  }

  public static ReferenceInformation of(
      StandardId indexId,
      int indexSeries,
      int indexAnnexVersion,
      RestructuringClause restructuringClause) {

    return new IndexReferenceInformation(
        indexId,
        indexSeries,
        indexAnnexVersion,
        restructuringClause);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code IndexReferenceInformation}.
   * @return the meta-bean, not null
   */
  public static IndexReferenceInformation.Meta meta() {
    return IndexReferenceInformation.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(IndexReferenceInformation.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static IndexReferenceInformation.Builder builder() {
    return new IndexReferenceInformation.Builder();
  }

  private IndexReferenceInformation(
      StandardId indexId,
      int indexSeries,
      int indexAnnexVersion,
      RestructuringClause restructuringClause) {
    JodaBeanUtils.notNull(indexId, "indexId");
    JodaBeanUtils.notNull(indexSeries, "indexSeries");
    JodaBeanUtils.notNull(indexAnnexVersion, "indexAnnexVersion");
    JodaBeanUtils.notNull(restructuringClause, "restructuringClause");
    this.indexId = indexId;
    this.indexSeries = indexSeries;
    this.indexAnnexVersion = indexAnnexVersion;
    this.restructuringClause = restructuringClause;
  }

  @Override
  public IndexReferenceInformation.Meta metaBean() {
    return IndexReferenceInformation.Meta.INSTANCE;
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
   * Gets a CDS index identifier (e.g. RED pair code)
   * @return the value of the property, not null
   */
  public StandardId getIndexId() {
    return indexId;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets a CDS index series identifier, e.g. 1, 2, 3 etc
   * @return the value of the property, not null
   */
  public int getIndexSeries() {
    return indexSeries;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets a CDS index series version identifier, e.g. 1, 2, 3 etc
   * @return the value of the property, not null
   */
  public int getIndexAnnexVersion() {
    return indexAnnexVersion;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets specifies the type of restructuring that is applicable.
   * This is used as part of the key in determining which curves will be used to price
   * the swap.
   * @return the value of the property, not null
   */
  public RestructuringClause getRestructuringClause() {
    return restructuringClause;
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
      IndexReferenceInformation other = (IndexReferenceInformation) obj;
      return JodaBeanUtils.equal(getIndexId(), other.getIndexId()) &&
          (getIndexSeries() == other.getIndexSeries()) &&
          (getIndexAnnexVersion() == other.getIndexAnnexVersion()) &&
          JodaBeanUtils.equal(getRestructuringClause(), other.getRestructuringClause());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getIndexId());
    hash = hash * 31 + JodaBeanUtils.hashCode(getIndexSeries());
    hash = hash * 31 + JodaBeanUtils.hashCode(getIndexAnnexVersion());
    hash = hash * 31 + JodaBeanUtils.hashCode(getRestructuringClause());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("IndexReferenceInformation{");
    buf.append("indexId").append('=').append(getIndexId()).append(',').append(' ');
    buf.append("indexSeries").append('=').append(getIndexSeries()).append(',').append(' ');
    buf.append("indexAnnexVersion").append('=').append(getIndexAnnexVersion()).append(',').append(' ');
    buf.append("restructuringClause").append('=').append(JodaBeanUtils.toString(getRestructuringClause()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code IndexReferenceInformation}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code indexId} property.
     */
    private final MetaProperty<StandardId> indexId = DirectMetaProperty.ofImmutable(
        this, "indexId", IndexReferenceInformation.class, StandardId.class);
    /**
     * The meta-property for the {@code indexSeries} property.
     */
    private final MetaProperty<Integer> indexSeries = DirectMetaProperty.ofImmutable(
        this, "indexSeries", IndexReferenceInformation.class, Integer.TYPE);
    /**
     * The meta-property for the {@code indexAnnexVersion} property.
     */
    private final MetaProperty<Integer> indexAnnexVersion = DirectMetaProperty.ofImmutable(
        this, "indexAnnexVersion", IndexReferenceInformation.class, Integer.TYPE);
    /**
     * The meta-property for the {@code restructuringClause} property.
     */
    private final MetaProperty<RestructuringClause> restructuringClause = DirectMetaProperty.ofImmutable(
        this, "restructuringClause", IndexReferenceInformation.class, RestructuringClause.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "indexId",
        "indexSeries",
        "indexAnnexVersion",
        "restructuringClause");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1943291277:  // indexId
          return indexId;
        case 1329638889:  // indexSeries
          return indexSeries;
        case -1801228842:  // indexAnnexVersion
          return indexAnnexVersion;
        case -1774904020:  // restructuringClause
          return restructuringClause;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public IndexReferenceInformation.Builder builder() {
      return new IndexReferenceInformation.Builder();
    }

    @Override
    public Class<? extends IndexReferenceInformation> beanType() {
      return IndexReferenceInformation.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code indexId} property.
     * @return the meta-property, not null
     */
    public MetaProperty<StandardId> indexId() {
      return indexId;
    }

    /**
     * The meta-property for the {@code indexSeries} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Integer> indexSeries() {
      return indexSeries;
    }

    /**
     * The meta-property for the {@code indexAnnexVersion} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Integer> indexAnnexVersion() {
      return indexAnnexVersion;
    }

    /**
     * The meta-property for the {@code restructuringClause} property.
     * @return the meta-property, not null
     */
    public MetaProperty<RestructuringClause> restructuringClause() {
      return restructuringClause;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1943291277:  // indexId
          return ((IndexReferenceInformation) bean).getIndexId();
        case 1329638889:  // indexSeries
          return ((IndexReferenceInformation) bean).getIndexSeries();
        case -1801228842:  // indexAnnexVersion
          return ((IndexReferenceInformation) bean).getIndexAnnexVersion();
        case -1774904020:  // restructuringClause
          return ((IndexReferenceInformation) bean).getRestructuringClause();
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
   * The bean-builder for {@code IndexReferenceInformation}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<IndexReferenceInformation> {

    private StandardId indexId;
    private int indexSeries;
    private int indexAnnexVersion;
    private RestructuringClause restructuringClause;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(IndexReferenceInformation beanToCopy) {
      this.indexId = beanToCopy.getIndexId();
      this.indexSeries = beanToCopy.getIndexSeries();
      this.indexAnnexVersion = beanToCopy.getIndexAnnexVersion();
      this.restructuringClause = beanToCopy.getRestructuringClause();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1943291277:  // indexId
          return indexId;
        case 1329638889:  // indexSeries
          return indexSeries;
        case -1801228842:  // indexAnnexVersion
          return indexAnnexVersion;
        case -1774904020:  // restructuringClause
          return restructuringClause;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1943291277:  // indexId
          this.indexId = (StandardId) newValue;
          break;
        case 1329638889:  // indexSeries
          this.indexSeries = (Integer) newValue;
          break;
        case -1801228842:  // indexAnnexVersion
          this.indexAnnexVersion = (Integer) newValue;
          break;
        case -1774904020:  // restructuringClause
          this.restructuringClause = (RestructuringClause) newValue;
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
    public IndexReferenceInformation build() {
      return new IndexReferenceInformation(
          indexId,
          indexSeries,
          indexAnnexVersion,
          restructuringClause);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code indexId} property in the builder.
     * @param indexId  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder indexId(StandardId indexId) {
      JodaBeanUtils.notNull(indexId, "indexId");
      this.indexId = indexId;
      return this;
    }

    /**
     * Sets the {@code indexSeries} property in the builder.
     * @param indexSeries  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder indexSeries(int indexSeries) {
      JodaBeanUtils.notNull(indexSeries, "indexSeries");
      this.indexSeries = indexSeries;
      return this;
    }

    /**
     * Sets the {@code indexAnnexVersion} property in the builder.
     * @param indexAnnexVersion  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder indexAnnexVersion(int indexAnnexVersion) {
      JodaBeanUtils.notNull(indexAnnexVersion, "indexAnnexVersion");
      this.indexAnnexVersion = indexAnnexVersion;
      return this;
    }

    /**
     * Sets the {@code restructuringClause} property in the builder.
     * @param restructuringClause  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder restructuringClause(RestructuringClause restructuringClause) {
      JodaBeanUtils.notNull(restructuringClause, "restructuringClause");
      this.restructuringClause = restructuringClause;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(160);
      buf.append("IndexReferenceInformation.Builder{");
      buf.append("indexId").append('=').append(JodaBeanUtils.toString(indexId)).append(',').append(' ');
      buf.append("indexSeries").append('=').append(JodaBeanUtils.toString(indexSeries)).append(',').append(' ');
      buf.append("indexAnnexVersion").append('=').append(JodaBeanUtils.toString(indexAnnexVersion)).append(',').append(' ');
      buf.append("restructuringClause").append('=').append(JodaBeanUtils.toString(restructuringClause));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
