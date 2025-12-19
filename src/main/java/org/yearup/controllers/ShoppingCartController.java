package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/cart")

public class ShoppingCartController
{
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    // GET http://localhost:8080/cart
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            int userId = getUserId(principal);
            return shoppingCartDao.getByUserId(userId);
        }
        catch (ResponseStatusException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // POST http://localhost:8080/cart/products/15
    @PostMapping("/products/{productId}")
    public ShoppingCart addProductToCart(@PathVariable int productId, Principal principal)
    {
        int userId = getUserId(principal);
        shoppingCartDao.addProduct(userId, productId);
        return shoppingCartDao.getByUserId(userId);
    }


    @PutMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateQuantity(@PathVariable int productId,
                               @RequestBody ShoppingCartItem item,
                               Principal principal)
    {
        try
        {
            int userId = getUserId(principal);


            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
            if (!cart.contains(productId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart.");

            int qty = item.getQuantity();
            if (qty <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1.");

            shoppingCartDao.updateProductQuantity(userId, productId, qty);
        }
        catch (ResponseStatusException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // DELETE http://localhost:8080/cart  (clear cart)
    @DeleteMapping
    public ShoppingCart clearCart(Principal principal)
    {
        try
        {
            int userId = getUserId(principal);
            shoppingCartDao.clearCart(userId);

            // return empty cart JSON so the website can re-render without crashing
            return shoppingCartDao.getByUserId(userId);
        }
        catch (ResponseStatusException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    private int getUserId(Principal principal)
    {
        if (principal == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in.");

        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user.");

        return user.getId();
    }
}
