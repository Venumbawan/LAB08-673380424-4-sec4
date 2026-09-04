package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.repository.ProductRepository;
import com.example.demo.strategy.DiscountContext;
import com.example.demo.strategy.MemberDiscountStrategy;
import com.example.demo.strategy.NoDiscountStrategy;
import com.example.demo.strategy.SeasonalSaleStrategy;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            calculateDiscountedPrice(product);
        }

        return products;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {

        if (product.getDetail() == null) {
            product.setDetail(new ProductDetail());
        }

        product.getDetail().setProduct(product);

        if (product.getReviews() != null) {
            for (Review review : product.getReviews()) {
                review.setProduct(product);

                if (review.getReviewDate() == null
                        && review.getReviewer() != null
                        && !review.getReviewer().isBlank()) {

                    review.setReviewDate(LocalDate.now());
                }
            }
        }

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {

        Product existingProduct = productRepository.findById(id).orElse(null);

        if (existingProduct == null) {
            return null;
        }

        // Product
        existingProduct.setName(product.getName());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setStock(product.getStock());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscountType(product.getDiscountType());

  
        if (product.getDetail() != null) {

            if (existingProduct.getDetail() == null) {
                existingProduct.setDetail(new ProductDetail());
            }

            existingProduct.getDetail()
                    .setDescription(product.getDetail().getDescription());

            existingProduct.getDetail()
                    .setWarranty(product.getDetail().getWarranty());

            existingProduct.getDetail()
                    .setWeight(product.getDetail().getWeight());

            existingProduct.getDetail()
                    .setDimensions(product.getDetail().getDimensions());

            existingProduct.getDetail()
                    .setManufacturedCountry(
                            product.getDetail().getManufacturedCountry()
                    );

            existingProduct.getDetail()
                    .setProduct(existingProduct);
        }

        return productRepository.save(existingProduct);
    }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    private void calculateDiscountedPrice(Product product) {

        if (product.getPrice() == null) {
            return;
        }

        DiscountContext context;

        if ("MEMBER".equals(product.getDiscountType())) {

            context = new DiscountContext(
                    new MemberDiscountStrategy()
            );

        } else if ("SEASONAL".equals(product.getDiscountType())) {

            context = new DiscountContext(
                    new SeasonalSaleStrategy()
            );

        } else {

            context = new DiscountContext(
                    new NoDiscountStrategy()
            );
        }

        double discountedPrice =
                context.calculatePrice(product.getPrice());

    }
}