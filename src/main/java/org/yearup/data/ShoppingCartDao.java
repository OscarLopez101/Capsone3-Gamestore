package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao {
    ShoppingCart getByUserId(int userId);

    void addProduct(int userId, int productId);          // POST behavior (insert or +1)
    void updateQuantity(int userId, int productId, int quantity); // PUT behavior
    void clearCart(int userId);                          // DELETE behavior
}