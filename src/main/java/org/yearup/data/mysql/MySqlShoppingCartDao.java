package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();

        String sql =
                "SELECT p.*, sc.quantity " +
                        "FROM shopping_cart sc " +
                        "JOIN products p ON p.product_id = sc.product_id " +
                        "WHERE sc.user_id = ? " +
                        "ORDER BY p.product_id;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, userId);

            try (ResultSet row = statement.executeQuery())
            {
                while (row.next())
                {

                    Product product = MySqlProductDao.mapRow(row);

                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(row.getInt("quantity"));
                    item.setDiscountPercent(BigDecimal.ZERO); // spec default

                    cart.add(item); // puts into Map keyed by productId
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error getting cart for userId: " + userId, e);
        }

        return cart;
    }

    @Override
    public void addProduct(int userId, int productId)
    {
        // If exists -> quantity + 1; else insert quantity = 1
        String selectSql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?;";
        String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1);";
        String updateSql = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?;";

        try (Connection connection = getConnection())
        {
            boolean exists;

            try (PreparedStatement select = connection.prepareStatement(selectSql))
            {
                select.setInt(1, userId);
                select.setInt(2, productId);

                try (ResultSet rs = select.executeQuery())
                {
                    exists = rs.next();
                }
            }

            if (!exists)
            {
                try (PreparedStatement insert = connection.prepareStatement(insertSql))
                {
                    insert.setInt(1, userId);
                    insert.setInt(2, productId);
                    insert.executeUpdate();
                }
            }
            else
            {
                try (PreparedStatement update = connection.prepareStatement(updateSql))
                {
                    update.setInt(1, userId);
                    update.setInt(2, productId);
                    update.executeUpdate();
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error adding productId " + productId + " to cart for userId " + userId, e);
        }
    }

    @Override
    public void updateProductQuantity(int userId, int productId, int quantity)
    {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating quantity for userId " + userId + ", productId " + productId, e);
        }
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error clearing cart for userId: " + userId, e);
        }
    }
}