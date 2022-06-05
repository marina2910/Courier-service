/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.testing;

import java.math.BigDecimal;
import rs.etf.sab.tests.Pair;

public class Util
{
    static double euclidean(final int x1, final int y1, final int x2, final int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
    
    static BigDecimal getPackagePrice(final int type, final BigDecimal weight, final double distance) {
        switch (type) {
            case 0: {
                return new BigDecimal(115.0 * distance);
            }
            case 1: {
                return new BigDecimal((175.0 + weight.doubleValue() * 100.0) * distance);
            }
            case 2: {
                return new BigDecimal((250.0 + weight.doubleValue() * 100.0) * distance);
            }
            case 3: {
                return new BigDecimal((350.0 + weight.doubleValue() * 500.0) * distance);
            }
            default: {
                return null;
            }
        }
    }
    
    static double getDistance(final Pair<Integer, Integer>... addresses) {
        double distance = 0.0;
        for (int i = 1; i < addresses.length; ++i) {
            distance += euclidean((int)addresses[i - 1].getKey(), (int)addresses[i - 1].getValue(), (int)addresses[i].getKey(), (int)addresses[i].getValue());
        //    System.out.println("U testu: " + distance);
        }
        return distance;
    }
}
