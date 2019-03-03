package com.max.bakery.system.equipment;

import com.max.bakery.controller.Manager;
import com.max.bakery.product.abstraction.BakedProduct;
import com.max.bakery.utils.ManagerUtil;
import com.max.bakery.utils.OvenUtil;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class imitates the working process of the electric oven.
 *
 * @author Max
 * @version 1.0
 */
public class ElectricOven implements Oven, Runnable {
    public ElectricOven(int number) {
        this.number = number;
    }

    private static final Logger LOGGER = LogManager.getLogger(ElectricOven.class.getName());

    @Getter
    private List<BakedProduct> ovenBakedProductList = new ArrayList<BakedProduct>();

    @Getter
    private int number;

    @Getter
    @Setter
    private String typeOfIngredient;

    @Getter
    @Setter
    private String typeOfProduct;

    @Getter
    @Setter
    private int quantityOfBaked;

    @Getter
    @Setter
    private volatile boolean busyOven = false;

    @Getter
    private static final int OVENCAPACITY = 10;

    @Getter
    private volatile Thread thread = null;

    /**
     * Method starts the baking process. {@link ElectricOven} use {@link OvenUtil} for determination of factory and storage.
     * Firstly, system checks sufficient quantity of raw materials for working. If it is not enough, system sets a flag
     * for buying necessary ingredients. Finally, baked product is added to list.
     */
    @Synchronized
    public void process() {
        try {
            Manager.getSEMAPHORE().acquire();
            while (true) {
                if (getQuantityOfBaked() * 2 > OvenUtil.determinationOfTheStorage(getTypeOfIngredient()).getList().size() || OvenUtil.determinationOfTheStorage(getTypeOfIngredient()).getList().size() < 2) {
                    System.out.println("You don't have ingredients enough!!!");
                    ManagerUtil.setNeedToBuyIngredients(true, getTypeOfIngredient());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.err.println("THREAD CANNOT SLEEP!: " + ElectricOven.class.getName());
                    }

                    StringBuilder infoString = new StringBuilder();
                    infoString.append("You don't have ingredients enough!!! ");
                    LOGGER.info(infoString);
                } else {
                    int canCreate = OvenUtil.howOvenCanCreateBakedProduct("Electric", getQuantityOfBaked());
                    for (int i = 0; i < canCreate; i++) {
                        ovenBakedProductList.add(OvenUtil.certainProduct(getTypeOfProduct(), getTypeOfIngredient()));
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            System.err.println("THREAD CANNOT SLEEP!: " + ElectricOven.class.getName());
                        }

                        StringBuilder infoString = new StringBuilder();
                        infoString.append("Oven make ").append(i + 1).append(" of ").append(getQuantityOfBaked()).append(" ").append(getTypeOfIngredient()).append(getTypeOfProduct());
                        LOGGER.info(infoString);
                    }
                    return;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            setBusyOven(false);
            Manager.getSEMAPHORE().release();

            StringBuilder infoString = new StringBuilder();
            infoString.append("Oven ").append(getNumber()).append(" finish");
            LOGGER.info(infoString);
        }
    }

    @Override
    public void run() {
        process();

        StringBuilder infoString = new StringBuilder();
        infoString.append("Oven number ").append(getNumber());
        LOGGER.info(infoString);
    }

    public void start() {
        setBusyOven(true);

        thread = new Thread(this);
        thread.start();
    }

    /**
     * Method waits for the completion of all the threads.
     */
    public void joinThread() {
        try {
            if (null != thread) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
