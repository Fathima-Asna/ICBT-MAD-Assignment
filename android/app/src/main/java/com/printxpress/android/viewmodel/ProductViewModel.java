package com.printxpress.android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.data.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {

    private final ProductRepository productRepository;
    private final MutableLiveData<ApiResponse<List<Product>>> products = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<Product>> product = new MutableLiveData<>();

    public ProductViewModel() {
        this.productRepository = new ProductRepository();
    }

    public MutableLiveData<ApiResponse<List<Product>>> getProducts() {
        return products;
    }

    public MutableLiveData<ApiResponse<Product>> getProduct() {
        return product;
    }

    public void loadProducts(String category) {
        productRepository.getProducts(category, products);
    }

    public void loadProduct(String id) {
        productRepository.getProduct(id, product);
    }
}
