package com.max.bakery.factory;

import com.max.bakery.product.abstraction.BakedProduct;
import com.max.bakery.product.abstraction.Ingredient;

/**
 * Abstract Bakery is realize Abstract Factory Pattern
 *
 * @author Max
 * @version 1.0
 */
public abstract class AbstractBakery {
    public abstract BakedProduct createWithJam(Ingredient ingredient);

    public abstract BakedProduct createWithPoppy(Ingredient ingredient);
}
