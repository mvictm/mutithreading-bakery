package com.max.bakery.controller;

import com.max.bakery.storage.finish.ProductStorage;
import com.max.bakery.system.delivery.DeliverySystem;
import com.max.bakery.system.equipment.ElectricOven;
import com.max.bakery.system.equipment.Oven;
import com.max.bakery.utils.ManagerUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.Synchronized;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a class, which is responsible for loading the bakery. Singleton. Factory starts in this class.
 *
 * @author Maxim
 * @version 1.0
 */
public class Manager {
    /**
     * Local variable of Logger, which responsible to write a message to file and console.
     */
    private static final Logger LOGGER = LogManager.getLogger(Manager.class.getName());

    private static final Integer ovenCount = 5;

    /**
     * Local variable of Semaphore, which restricts access to threads. factory can use five threads in one
     * moment. Namely our service, such as
     *
     * @see DeliverySystem
     * @see ElectricOven
     * @see ManagerUtil
     * <p>
     * A semaphore initialized to one, and which is used such that it
     * only has at most one permit available, can serve as a mutual
     * exclusion lock. Generally, semaphores used to control resource access should be
     * initialized as fair, to ensure that no thread is starved out from
     * accessing a resource.
     * Semaphore has a getter for access in another classes.
     * @see lombok.Lombok
     */
    @Getter
    @Singular
    private static final Semaphore SEMAPHORE = new Semaphore(ovenCount + 1);

    /**
     * List of {@link Oven}s. When Manager is initialized, he create 5 {@link ElectricOven}.
     */
    @Getter
    private List<Oven> ovenList = new ArrayList<Oven>(5);

    /**
     * Boolean flag, which notifies about state of order. Initial value equally false.
     */
    @Getter
    @Setter
    private static volatile boolean orderIsReady = false;

    private static Manager instance;

    /**
     * The constructor initializes factory {@link Oven} numbers each oven.
     */
    private Manager() {
        for (int i = 0; i < ovenCount; i++) {
            ElectricOven electricOven = new ElectricOven(i + 1);
            ovenList.add(electricOven);
        }

        StringBuilder infoString = new StringBuilder();
        infoString.append("Initialized ").append(ovenList.size()).append(" oven");
        LOGGER.info(infoString);
    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    /**
     * <p>Method imitates loading a factory system. We transfer necessary parameters and factory starts working.
     * Firstly, system checks {@link com.max.bakery.storage.order.OrderStorage}. If it is empty, system starts to buy necessary
     * ingredients.</p>
     * <p>Secondly, system <i>calculates</i> necessary number of {@link Oven} for work.</p>
     * <p>For example: customer wants 43 pieces of
     * product. As an oven capacity equal to 10, factory needs to use 5 ovens.</p>
     * <p>Finally, system checks each oven in use. If it is filled with cooked products, {@link DeliverySystem} it delivers products
     * to {@link ProductStorage}. When all ovens are empty and stop working, system finishes its work.</p>
     *
     * @param typeOfOven         name of {@link Oven}
     * @param typeOfIngredient   type of ingredient
     * @param typeOfProduct      name of product
     * @param bulkOfBakedProduct bulk of cooked product
     */
    @Synchronized
    public void processAdministrator(String typeOfOven, String typeOfIngredient, String typeOfProduct, int bulkOfBakedProduct) {
        DeliverySystem.setEndThread(false);
        DeliverySystem deliverySystem = DeliverySystem.getInstance();
        Thread thread = new Thread(deliverySystem);
        thread.start();
        setOrderIsReady(false);
        while (!isOrderIsReady()) {
            startBuyIngredient(typeOfIngredient);
            calculationAndStart(typeOfOven, typeOfIngredient, typeOfProduct, bulkOfBakedProduct);
        }
        while (true) {
            if (allOvenEmpty() && allOvenFree()) {
                ManagerUtil.setNeedToBuyIngredients(false, typeOfIngredient);
                ManagerUtil.setEndOfThread(true);

                StringBuilder infoString = new StringBuilder();
                infoString.append("All ovens are free and empty! We create ").append(bulkOfBakedProduct).append(" ").append(typeOfIngredient).append(typeOfProduct).append(" in ").append(typeOfOven).append(" ovens");
                LOGGER.info(infoString);
                return;
            } else {
                StringBuilder infoString = new StringBuilder();
                infoString.append("Ovens are busy or not empty! Wait.");
                LOGGER.info(infoString);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>Method creates a thread, which buys necessary ingredients for making products. system sets a flag in
     * {@link ManagerUtil}</p>
     *
     * @param typeOfIngredient - type of ingredient
     */
    @Synchronized
    private void startBuyIngredient(String typeOfIngredient) {
        ManagerUtil.setEndOfThread(false);
        ManagerUtil.setNeedToBuyIngredients(true, typeOfIngredient);
        ManagerUtil managerUtil = ManagerUtil.getInstance();
        Thread thread = new Thread(managerUtil);
        thread.start();

        StringBuilder infoString = new StringBuilder();
        infoString.append("Buy necessary ingredients for making products");
        LOGGER.info(infoString);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.err.println("THREAD CANNOT SLEEP! " + Manager.class.getName());
        }
    }

    /**
     * <p>Method <em>calculates</em> necessary number of ovens for working and amount of raw material. Use {@link AtomicInteger} for
     * safe estimation.</p>
     * <p>For example: if a customer needs 45 pieces of cooked products, system uses 5 ovens. The first four ovens make 10
     * products and the last oven makes 5 products.</p>
     *
     * @param typeOfOven         type of {@link Oven}
     * @param typeOfIngredient   type of product ingredient
     * @param typeOfProduct      type of product
     * @param bulkOfBakedProduct bulk of cooked product
     */
    @Synchronized
    private void calculationAndStart(String typeOfOven, String typeOfIngredient, String typeOfProduct, int bulkOfBakedProduct) {
        AtomicInteger howOvenUse = new AtomicInteger(0);
        AtomicInteger howPut = new AtomicInteger(0);
        AtomicInteger needOven = new AtomicInteger(ManagerUtil.deduceRequiredNumberOfOvens(typeOfOven, bulkOfBakedProduct));
        AtomicInteger howMuchCanOneOvenBake = new AtomicInteger(ManagerUtil.howMuchCanOneOvenBake(typeOfOven, bulkOfBakedProduct, needOven.intValue()));

        for (int i = 0; i < needOven.intValue(); i++) {
            howPut.getAndSet(howPut.get() + howMuchCanOneOvenBake.get());
            if (howPut.intValue() > bulkOfBakedProduct) {
                howMuchCanOneOvenBake.getAndSet(howMuchCanOneOvenBake.get() - (howPut.get() - bulkOfBakedProduct));
                howPut.getAndSet(howPut.get() - (howPut.get() - bulkOfBakedProduct));

            }

            start(typeOfIngredient, typeOfProduct, howMuchCanOneOvenBake.get());
            howOvenUse.incrementAndGet();

            if (howPut.get() == bulkOfBakedProduct) {
                ovenList.forEach(Oven::joinThread);
                setOrderIsReady(true);
                StringBuilder infoString = new StringBuilder();
                infoString.append("order is ready!");
                LOGGER.info(infoString);
                return;
            }
        }
    }

    /**
     * <p>Method starts working of the ovens. Firstly, system tries to get a {@link Semaphore} and find free and empty
     * ovens for work. Further ovens start working in threads. After loading, system returns {@link Semaphore}.</p>
     *
     * @param typeOfIngredient      type of ingredient
     * @param typeOfProduct         type of product
     * @param howMuchCanOneOvenBake amount of cooked product which can one oven create
     */
    @Synchronized
    private void start(String typeOfIngredient, String typeOfProduct, int howMuchCanOneOvenBake) {
        boolean filled = false;
        while (!filled) {
            try {
                SEMAPHORE.acquire();
                if (ovenList.stream().anyMatch(s -> !s.isBusyOven() && s.getOvenBakedProductList().isEmpty())) {
                    ElectricOven electricOven = (ElectricOven) ovenList.stream().filter(s -> !s.isBusyOven() && s.getOvenBakedProductList().isEmpty()).findFirst().get();
                    electricOven.setTypeOfIngredient(typeOfIngredient);
                    electricOven.setTypeOfProduct(typeOfProduct);
                    electricOven.setQuantityOfBaked(howMuchCanOneOvenBake);

                    electricOven.start();

                    filled = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                SEMAPHORE.release();
            }

            if (!filled) {
                try {
                    System.out.println("Wait for a free oven");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return true, if <b>all</b> ovens are free, false if not.
     */
    private boolean allOvenFree() {
        return ovenList.stream().noneMatch(Oven::isBusyOven);
    }

    /**
     * @return true, if <b>all</b> ovens are empty, false if not.
     */
    private boolean allOvenEmpty() {
        return ovenList.stream().allMatch(s -> s.getOvenBakedProductList().isEmpty());
    }
}



