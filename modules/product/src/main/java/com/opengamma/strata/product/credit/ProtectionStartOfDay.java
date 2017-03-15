/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.credit;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

import com.google.common.base.CaseFormat;
import com.opengamma.strata.collect.ArgChecker;

/**
 * The protection start of the day.
 * <p>
 * When the protection starts on the start date.
 */
public enum ProtectionStartOfDay {

  /**
   * Beginning of the start day. 
   * <p>
   * The protection starts at the beginning of the day. 
   */
  BEGINNING,
  /**
   * None.
   * <p>
   * The protection start is not specified. 
   * The CDS is priced based on the default date logic in respective model implementation.
   */
  NONE;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance from the specified unique name.
   * 
   * @param uniqueName  the unique name
   * @return the type
   * @throws IllegalArgumentException if the name is not known
   */
  @FromString
  public static ProtectionStartOfDay of(String uniqueName) {
    ArgChecker.notNull(uniqueName, "uniqueName");
    return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, uniqueName));
  }

  //-------------------------------------------------------------------------
  /**
   * Returns the formatted unique name of the type.
   * 
   * @return the formatted string representing the type
   */
  @ToString
  @Override
  public String toString() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
  }

  //-------------------------------------------------------------------------
  /**
   * Check if the type is 'Beginning'.
   * 
   * @return true if beginning, false otherwise
   */
  public boolean isBeginning() {
    return this == BEGINNING;
  }

}
