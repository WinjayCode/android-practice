package com.winjay.practice.design_pattern.strategy;

/**
 * 策略模式
 * <p>
 * 定义一组算法, 并将每一个单独算法封装起来, 让它们可以相互替换.
 * </p>
 *
 * @author Winjay
 * @date 2019-12-26
 */
public class StrategyPattern {
    public interface ActivityStrategy {
        String getActivityPrice();
    }

    /**
     * 感恩节活动算法
     */
    public static class ThanksGivingDayStrategy implements ActivityStrategy {

        @Override
        public String getActivityPrice() {
            return "（感恩节）所有饮品一律5折";
        }
    }

    /**
     * 双十二算法
     */
    public static class DoubleTwelveDayStrategy implements ActivityStrategy {

        @Override
        public String getActivityPrice() {
            return "(双十二)满12立减2元";
        }
    }

    /**
     * 圣诞节算法
     */
    public static class ChristmasStrategy implements ActivityStrategy {

        @Override
        public String getActivityPrice() {
            return "(圣诞节)买热干面+饮品套餐, 送大苹果一个";
        }
    }

    /**
     * 默认算法
     */
    public static class DefaultActivityStrategy implements ActivityStrategy {
        @Override
        public String getActivityPrice() {
            return "没有活动";
        }
    }

    /**
     * 收银台
     */
    public static class Checkstand {
        private ActivityStrategy mActivityStrategy;

        public Checkstand() {
            mActivityStrategy = new DefaultActivityStrategy();
        }

        public Checkstand(ActivityStrategy mActivityStrategy) {
            this.mActivityStrategy = mActivityStrategy;
        }

        public void setActivityStrategy(ActivityStrategy mActivityStrategy) {
            this.mActivityStrategy = mActivityStrategy;
        }

        public void printBill() {
            System.out.println("本次账单活动：" + mActivityStrategy.getActivityPrice());
        }
    }

    public static void main(String[] args) {
        Checkstand checkstand = new Checkstand();
        checkstand.printBill();

        checkstand.setActivityStrategy(new ThanksGivingDayStrategy());
        checkstand.printBill();

        checkstand.setActivityStrategy(new DoubleTwelveDayStrategy());
        checkstand.printBill();

        checkstand.setActivityStrategy(new ChristmasStrategy());
        checkstand.printBill();
    }
}
