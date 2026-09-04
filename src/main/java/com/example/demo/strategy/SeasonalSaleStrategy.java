package com.example.demo.strategy;

public class SeasonalSaleStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscountedPrice(double price) {
        return price * 0.80;
    }
}