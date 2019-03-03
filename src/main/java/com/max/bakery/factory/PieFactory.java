package com.max.bakery.factory;

import com.max.bakery.product.JamPie;
import com.max.bakery.product.PoppyPie;
import com.max.bakery.product.abstraction.Ingredient;
import com.max.bakery.product.abstraction.Pie;

/**
 * Pie Factory is realize {@link AbstractBakery}
 *
 * @author Max
 * @version 1.0
 */
public class PieFactory extends AbstractBakery {
    public Pie createWithJam(Ingredient ingredient) {
        return new JamPie(ingredient);
    }

    public Pie createWithPoppy(Ingredient ingredient) {
        return new PoppyPie(ingredient);
    }
}
