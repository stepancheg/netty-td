package com.github.stepancheg.nomutex.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Stepan Koltsov
 */
public class Parameters {

    public static final int EMIT_BY_THREAD = 50 * 1000 * 1000;

    public static final List<BigInteger> numbers;

    static {
        List<BigInteger> numbersTemp = new ArrayList<BigInteger>();
        for (int i = 0; i < 1000000; ++i) {
            numbersTemp.add(BigInteger.valueOf(i));
        }
        numbers = Collections.unmodifiableList(numbersTemp);
    }

    public static final boolean USE_PREALLOCATED = true;

    public static String describe() {
        return USE_PREALLOCATED ? "preallocated" : "new";
    }

    public static BigInteger getNumber(int i) {
        // when new objects are allocated are used, difference between version
        // is not big because of caching

        if (USE_PREALLOCATED) {
            return numbers.get(i % numbers.size());
        } else {
            return BigInteger.valueOf(i);
        }
    }

}
