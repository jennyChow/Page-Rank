//the class to store rational or real numbers
public class Number {
    private Rational rationalValue;
    private double doubleValue;
    boolean isRational = false;

    Number(double value) {
        doubleValue = value;
        isRational = false;
    }

    Number(Rational rational) {
        rationalValue = rational;
        isRational = true;
    }

    public Number getDoubleVersion() {
        if (isRational) {
            return new Number(this.rationalValue.toDouble());
        } else {
            return new Number(this.doubleValue);
        }
    }

    public void changeToDoubleVersion() {
        if (isRational) {
            isRational = false;
            doubleValue = rationalValue.toDouble();
        }
    }

    public double getDoubleValue() {
        Number doubleVersion = this.getDoubleVersion();
        return doubleVersion.doubleValue;
    }

    public boolean isPositive() {
        if (isRational) {
            return rationalValue.compareTo(new Rational(0, 1)) > 0;
        } else {
            return doubleValue > 0;
        }
    }

    public boolean isZero() {
        if (isRational) {
            return rationalValue.numerator() == 0;
        } else {
            return doubleValue == 0.0;
        }
    }

    public boolean isNegative() {
        return !isZero() && !isPositive();
    }

    @Override
    public String toString() {
        if (isRational) {
            return rationalValue.toString();
        } else {
            return Double.toString(doubleValue);
        }
    }

    //return a * b
    public Number times(Number b) {
        if (!this.isRational || !b.isRational) {
            Number a = this.getDoubleVersion();
            Number b_double = b.getDoubleVersion();
            return new Number(a.doubleValue * b_double.doubleValue);
        } else {
            Rational ret = this.rationalValue.times(b.rationalValue);
            return new Number(ret);
        }
    }

    //return a + b
    public Number plus(Number b) {
        if (!this.isRational || !b.isRational) {
            Number a = this.getDoubleVersion();
            Number b_double = b.getDoubleVersion();
            return new Number(a.doubleValue + b_double.doubleValue);
        } else {
            Rational ret = this.rationalValue.plus(b.rationalValue);
            return new Number(ret);
        }
    }
}
