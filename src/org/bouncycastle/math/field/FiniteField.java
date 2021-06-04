package org.bouncycastle.math.field;

import banki.util.BigInteger;

public interface FiniteField
{
    BigInteger getCharacteristic();

    int getDimension();
}
