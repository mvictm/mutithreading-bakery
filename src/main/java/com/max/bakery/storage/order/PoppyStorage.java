package com.max.bakery.storage.order;

import com.max.bakery.product.Poppy;
import com.max.bakery.product.abstraction.Ingredient;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Storage of Poppy
 *
 * @author Max
 * @version 1.0
 */
public class PoppyStorage extends OrderStorage {
    private List<Ingredient> list = Collections.synchronizedList(new LinkedList<Ingredient>());

    private static PoppyStorage instance;

    public static PoppyStorage getInstance() {
        if (instance == null) {
            instance = new PoppyStorage();
        }
        return instance;
    }

    @Override
    protected Ingredient getIngredientInstance() {
        return new Poppy();
    }

    public synchronized List<Ingredient> getList() {
        return list;
    }
}
