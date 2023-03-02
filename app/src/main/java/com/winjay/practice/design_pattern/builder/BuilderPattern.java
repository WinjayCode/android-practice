package com.winjay.practice.design_pattern.builder;

/**
 * 建造者模式
 *
 * 将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。
 *
 * Android中很多地方运用了Builder模式，比如常见的对话框创建/Okhttp等
 *
 * BuilderPattern builderPattern = new BuilderPattern.Builder()
 *                 .setName("1")
 *                 .setAge(1)
 *                 .setHeight(1)
 *                 .setWeight(1)
 *                 .build();
 *         builderPattern.setAge(1);
 *         builderPattern.getAge();
 *
 * @author Winjay
 * @date 2023-03-02
 */
public class BuilderPattern {
    private String name;
    private int age;
    private double height;
    private double weight;

    private BuilderPattern(Builder builder) {
        name = builder.name;
        age = builder.age;
        height = builder.height;
        weight = builder.weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public static class Builder {
        private String name;
        private int age;
        private double height;
        private double weight;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public Builder setHeight(double height) {
            this.height = height;
            return this;
        }

        public Builder setWeight(double weight) {
            this.weight = weight;
            return this;
        }

        public BuilderPattern build() {
            return new BuilderPattern(this);
        }
    }
}
