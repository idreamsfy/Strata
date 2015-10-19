/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.rate.future;

import org.joda.beans.PropertyDefinition;

import com.opengamma.strata.basics.date.BusinessDayAdjustment;
import com.opengamma.strata.basics.index.IborIndex;

/**
 * A market convention for forward rate agreement (FRA) trades.
 */
public class IborFutureConvention {
  
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
  @PropertyDefinition(get = "field")
  private final BusinessDayAdjustment businessDayAdjustment;

}
