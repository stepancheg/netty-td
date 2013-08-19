package com.github.stepancheg.nomutex.common;

import java.math.BigInteger;

/**
 * @author Stepan Koltsov
 */
public class Computation {
    private BigInteger sum = BigInteger.ZERO;

    public BigInteger getSum() {
        return sum;
    }

    public void update(BigInteger param) {
        sum = sum.add(param);
    }
}
