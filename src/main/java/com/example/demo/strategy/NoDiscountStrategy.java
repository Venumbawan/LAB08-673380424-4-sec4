package com.example.demo.strategy;

public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscountedPrice(double price) {
        return price;
    }
}