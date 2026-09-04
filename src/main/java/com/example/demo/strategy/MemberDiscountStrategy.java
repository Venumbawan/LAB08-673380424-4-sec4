package com.example.demo.strategy;

public class MemberDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscountedPrice(double price) {
        return price * 0.90;
    }
}