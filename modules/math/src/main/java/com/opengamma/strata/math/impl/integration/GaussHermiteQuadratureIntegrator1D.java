/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.math.impl.integration;

import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.math.impl.function.Function1D;

/**
 * Gauss-Hermite quadrature approximates the value of integrals of the form
 * $$
 * \begin{align*}
 * \int_{-\infty}^{\infty} e^{-x^2} g(x) dx
 * \end{align*}
 * $$
 * The weights and abscissas are generated by {@link GaussHermiteWeightAndAbscissaFunction}. 
 * <p>
 * At present, this integrator can only be used for the limits $\pm\infty$. The
 * function to integrate is scaled in such a way as to allow any values for the
 * limits of integration.
 */
public class GaussHermiteQuadratureIntegrator1D extends GaussianQuadratureIntegrator1D {

  private static final Double[] LIMITS = new Double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
  private static final GaussHermiteWeightAndAbscissaFunction GENERATOR = new GaussHermiteWeightAndAbscissaFunction();

  /**
   * @param n The number of sample points to use in the integration
   */
  public GaussHermiteQuadratureIntegrator1D(int n) {
    super(n, GENERATOR);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double[] getLimits() {
    return LIMITS;
  }

  /**
   * {@inheritDoc}
   * The function $f(x)$ that is to be integrated is transformed into a form
   * suitable for this quadrature method using:
   * $$
   * \begin{align*}
   * \int_{-\infty}^{\infty} f(x) dx
   * &= \int_{-\infty}^{\infty} f(x) e^{x^2} e^{-x^2} dx\\
   * &= \int_{-\infty}^{\infty} g(x) e^{-x^2} dx
   * \end{align*} 
   * $$
   * @throws UnsupportedOperationException If the lower limit is not $-\infty$ or the upper limit is not $\infty$
   */
  @Override
  public Function1D<Double, Double> getIntegralFunction(Function1D<Double, Double> function, Double lower, Double upper) {
    ArgChecker.notNull(function, "function");
    ArgChecker.notNull(lower, "lower");
    ArgChecker.notNull(upper, "upper");
    if (lower.equals(LIMITS[0]) && upper.equals(LIMITS[1])) {
      return new Function1D<Double, Double>() {
        @Override
        public Double evaluate(Double x) {
          return Math.exp(x * x) * function.evaluate(x);
        }
      };
    }
    throw new UnsupportedOperationException("Limits for this integration method are +/-infinity");
  }

}
