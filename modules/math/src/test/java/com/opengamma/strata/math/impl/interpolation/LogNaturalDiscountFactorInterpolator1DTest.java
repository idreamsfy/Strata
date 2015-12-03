package com.opengamma.strata.math.impl.interpolation;

import org.testng.annotations.Test;

import com.opengamma.strata.math.impl.interpolation.data.Interpolator1DDataBundle;

/**
 * Test {@link LogNaturalDiscountFactorInterpolator1D}.
 */
@Test
public class LogNaturalDiscountFactorInterpolator1DTest {

  public void test() {
    final double[] xValues = new double[] {1., 3., 5., 7., 10. };
    final double[] yValues = new double[] {0.99, 0.95, 0.92, 0.85, 0.82 };

    LogNaturalDiscountFactorInterpolator1D interp = new LogNaturalDiscountFactorInterpolator1D();
    Interpolator1DDataBundle bundle = interp.getDataBundleFromSortedArrays(xValues, yValues);
    InterpolatorExtrapolator extrap = new InterpolatorExtrapolator();
    ;

    //    for (int i = 0; i < 100; ++i) {
    //      double time = 0.01 * i;
    //      System.out.println(time + "\t" + interp.interpolate(bundle, time) + "\t" +
    //          extrap.extrapolate(bundle, time, interp));
    //    }

    double[] iSensi = interp.getNodeSensitivitiesForValue(bundle, 0.5);
    double[] eSensi = extrap.getNodeSensitivitiesForValue(bundle, 0.5, interp);
  }

}
