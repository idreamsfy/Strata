/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.common;

import static com.opengamma.strata.collect.TestHelper.assertSerialization;
import static com.opengamma.strata.collect.TestHelper.coverPrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.testng.annotations.Test;

/**
 * Test {@link ExchangeId}.
 */
@Test
public class ExchangeIdTest {

  public void test_of() {
    ExchangeId test = ExchangeId.of("GB");
    assertThat(test.getName()).isEqualTo("GB");
    assertThat(test).hasToString("GB");
    assertThatIllegalArgumentException().isThrownBy(() -> CcpId.of(""));
  }

  //-------------------------------------------------------------------------
  public void test_equalsHashCode() {
    ExchangeId a = ExchangeId.of("ECAG");
    ExchangeId a2 = ExchangeIds.ECAG;
    ExchangeId b = ExchangeId.of("XLON");
    assertThat(a)
        .isEqualTo(a)
        .isEqualTo(a2)
        .isNotEqualTo(b)
        .isNotEqualTo("")
        .isNotEqualTo(null)
        .hasSameHashCodeAs(a2);
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    coverPrivateConstructor(ExchangeIds.class);
  }

  public void test_serialization() {
    ExchangeId test = ExchangeId.of("ECAG");
    assertSerialization(test);
  }

}
