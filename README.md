## Capstone 3 — E-Commerce API (Spring Boot)

This project is a backend Spring Boot REST API for an e-commerce website. The front-end web store is already provided and functional; my work focuses on completing and improving the backend API, using a MySQL database for persistence.

In this version, I implemented the Phase 3 Shopping Cart feature so authenticated users can add items, view their cart, update quantities, and clear the cart. The cart persists in the database between logins.

# Tech Stack

Java + Spring Boot

MySQL

JDBC / DAO pattern

JWT Authentication

Postman (API testing)

## Setup Instructions
 1) Create the Database

Open MySQL Workbench

Run the provided SQL script: create_database.sql

Confirm the database is created and populated with sample data

Sample users included:

user / password

admin / password

george / password

2) Configure the API

Update application.properties (or equivalent config) with your database connection:

spring.datasource.url=jdbc:mysql://localhost:3306/easyshop
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

3) Run the API

Run the Spring Boot application, then verify the API is live:

http://localhost:8080

 Authentication (JWT)
Register

POST /register

Example body:

{
  "username": "newuser",
  "password": "password",
  "confirmPassword": "password",
  "role": "USER"
}

Login

POST /login

Example body:

{
  "username": "user",
  "password": "password"
}


The login response returns a JWT token. Use it for protected endpoints:

Postman → Authorization → Bearer Token
or send header:
Authorization: Bearer <token>

## Phase 3 — Shopping Cart Feature
Requirements Met

Shopping cart is available only to logged in users

Cart items are stored in the database (shopping_cart table)

Cart persists across logouts/logins

Endpoints implemented:

GET /cart

POST /cart/products/{productId}

PUT /cart/products/{productId}

DELETE /cart

Shopping Cart Endpoints
1) Get Cart

GET /cart (Authenticated)

Returns the current user’s shopping cart including items and totals.

Example response:

{
  "items": {
    "15": {
      "product": {
        "productId": 15,
        "name": "External Hard Drive",
        "price": 129.99,
        "categoryId": 1,
        "description": "Expand your storage...",
        "subCategory": "Gray",
        "stock": 25,
        "imageUrl": "external-hard-drive.jpg",
        "featured": true
      },
      "quantity": 1,
      "discountPercent": 0,
      "lineTotal": 129.99
    }
  },
  "total": 129.99
}

2) Add Product to Cart

POST /cart/products/{productId} (Authenticated)

Adds a product to cart:

If not in cart: inserts a new row with quantity = 1

If already in cart: increments quantity by 1

Important: This endpoint returns 200 OK with the updated cart JSON to support the provided website UI (some UIs fail when POST returns 204/no JSON).

3) Update Product Quantity (Bonus PUT)

PUT /cart/products/{productId} (Authenticated)

Request body:

{
  "quantity": 3
}


Updates quantity only if the item already exists in cart.

4) Clear Cart

DELETE /cart (Authenticated)

Deletes all cart rows for the current user.

Important: This endpoint returns 200 OK with the empty cart JSON so the provided website UI can re-render cleanly.

CORS Support for Provided Website

The provided UI runs from a different origin (example: http://localhost:63342). Browsers enforce CORS, so API requests can be blocked unless the backend allows it.

To support the provided site, CORS was enabled for local development for:

http://localhost:63342

Testing (Postman)

Recommended flow:

POST /login → copy JWT token

POST /cart/products/12 → should return 200 OK + updated cart JSON

GET /cart → verify item shows up

PUT /cart/products/12 with { "quantity": 3 } → verify update

DELETE /cart → verify cart clears

GET /cart → verify cart is empty

Interesting Code (Example)

A key design choice is representing the cart using a map keyed by productId, which makes it easy to merge items and calculate totals:

ShoppingCart stores Map<Integer, ShoppingCartItem>

ShoppingCartItem computes lineTotal

ShoppingCart computes the cart total

This structure matches the required JSON format and simplifies cart math.

Project Management / Workflow

Work tracked using a GitHub project board (user stories / tasks)

Features implemented in small, meaningful commits

Manual testing and Postman collections used during development

Future Improvements

Potential upgrades for later versions:

Checkout endpoint that converts cart → order + order line items

Remove an individual product from cart

Stock validation before adding to cart

Discounts / promotions logic

Order history endpoints
