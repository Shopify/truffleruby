package org.truffleruby.core.numeric;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.object.DynamicObject;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.truffleruby.Layouts.BIGNUM;
import static org.truffleruby.Layouts.BIG_DECIMAL;
import static org.truffleruby.language.RubyGuards.isRubyBigDecimal;
import static org.truffleruby.language.RubyGuards.isRubyBignum;

/** Wrapper for methods of {@link BigDecimal} decorated with a {@link TruffleBoundary} annotation, as these methods are
 * blacklisted by SVM. */
public final class BigDecimalOps {

    @TruffleBoundary
    public static BigDecimal fromBigInteger(BigInteger value) {
        return new BigDecimal(value);
    }

    public static BigDecimal fromBigInteger(DynamicObject value) {
        assert isRubyBignum(value);
        return fromBigInteger(BIGNUM.getValue(value));
    }

    @TruffleBoundary
    public static int compare(BigDecimal a, BigDecimal b) {
        return a.compareTo(b);
    }

    public static int compare(DynamicObject a, BigDecimal b) {
        assert isRubyBigDecimal(a);
        return compare(BIG_DECIMAL.getValue(a), b);
    }

    @TruffleBoundary
    public static BigDecimal negate(BigDecimal value) {
        return value.negate();
    }

    @TruffleBoundary
    public static int signum(BigDecimal value) {
        return value.signum();
    }

    public static int signum(DynamicObject value) {
        assert isRubyBigDecimal(value);
        return signum(BIG_DECIMAL.getValue(value));
    }

    @TruffleBoundary
    public static BigInteger toBigInteger(BigDecimal value) {
        return value.toBigInteger();
    }

    @TruffleBoundary
    public static BigDecimal valueOf(long value) {
        return BigDecimal.valueOf(value);
    }

    @TruffleBoundary
    public static BigDecimal valueOf(double value) {
        return BigDecimal.valueOf(value);
    }
}
