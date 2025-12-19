package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);

    void addProduct(int userId, int productId);                 // POST /cart/products/{id}
    void updateProductQuantity(int userId, int productId, int quantity); // PUT /cart/products/{id}
    void clearCart(int userId);                                 // DELETE /cart
}