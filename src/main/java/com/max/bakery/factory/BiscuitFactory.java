package com.max.bakery.factory;

import com.max.bakery.product.JamBiscuit;
import com.max.bakery.product.PoppyBiscuit;
import com.max.bakery.product.abstraction.Biscuit;
import com.max.bakery.product.abstraction.Ingredient;

/**
 * Biscuit Factory is realize {@link AbstractBakery}
 *
 * @author Max
 * @version 1.0
 */
public class BiscuitFactory extends AbstractBakery {
    public Biscuit createWithJam(Ingredient ingredient) {
        return new JamBiscuit(ingredient);
    }

    public Biscuit createWithPoppy(Ingredient ingredient) {
        return new PoppyBiscuit(ingredient);
    }
}
