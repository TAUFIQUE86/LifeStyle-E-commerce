package com.ecom.service;

public class ProductAccessDeniedException extends RuntimeException {

    public ProductAccessDeniedException() {
        super("You do not own this product");
    }
}
