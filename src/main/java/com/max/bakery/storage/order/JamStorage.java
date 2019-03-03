package com.max.bakery.storage.order;

import com.max.bakery.product.Jam;
import com.max.bakery.product.abstraction.Ingredient;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Storage of Jam
 *
 * @author Max
 * @version 1.0
 */
public class JamStorage extends OrderStorage {
    private List<Ingredient> list = Collections.synchronizedList(new LinkedList<Ingredient>());

    private static JamStorage instance;

    public static JamStorage getInstance() {
        if (instance == null) {
            instance = new JamStorage();
        }
        return instance;
    }

    public synchronized List<Ingredient> getList() {
        return list;
    }

    @Override
    protected Ingredient getIngredientInstance() {
        return new Jam();
    }
}
