# E-Commerce Demo

This software was created as a proof of concept for a potential e-commerce application.
It is intended to demonstrate the use of Spring and React to build a full-stack web application.

### _**It is currently a (very early) work in progress, and is not intended to be used in a production environment.**_

## Table of Contents

- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Features](#features)
    - [Spring Backend](#spring-backend)
    - [React Frontend](#react-frontend)
- [Roadmap](#roadmap-wip)
- [Contributing](#contributing)
- [License](#license)

## Project Overview

The project is a simple e-commerce application that allows users to browse and purchase products, in addition to a
fully-fledged admin portal for managing products, orders, and users. The Backend API is designed to run on a single host,
but can easily be scaled to run on multiple hosts with a load balancer. The storage volume can also be modified to a
cloud-based storage bucket such as AWS S3 or Google Cloud Storage. If you want to test it locally, you can specify the
photos volume in the [Docker Compose File](api/docker-compose.yml) (when it is implemented)

## Technologies Used

The project utilises the following technologies:

### Backend API
- [Kotlin](https://kotlinlang.org/): A statically typed programming language that runs on the JVM.
- [Spring](https://spring.io/): A powerful and flexible Java framework for building backend applications.
- [MongoDB](https://www.mongodb.com/): A NoSQL database that stores data in JSON-like documents.
- [Redis](https://redis.io/): An in-memory data structure store that can be used as a database, cache, or message broker.
- [Docker](https://www.docker.com/): A containerization platform that allows applications to be run in isolated
  environments.

### Frontend
- [TypeScript](https://www.typescriptlang.org/): A typed superset of JavaScript that compiles to plain JavaScript.
- [React](https://reactjs.org/): A JavaScript library for building user interfaces.
- [React-Admin](https://marmelab.com/react-admin/): A frontend framework for building admin applications.

## Getting Started

To get started with the project, follow the instructions below:

1. Clone the repository: `git clone https://github.com/james-samios/ecommerce-demo.git`
2. Setup environment variables as per .env.example for both the API and Frontend, ensuring that you have live MongoDB
and Redis servers running.
3. The Spring Backend can be run locally by starting the main function in ECommerceAPI.kt. Alternatively, the backend
   can be run in a Docker container by running `docker-compose up` in the api directory.
4. The React Frontend can be run locally by running `npm run start` in the frontend directory.

## Features

The project is divided into two main components: the Spring backend and the React frontend.

### Spring Backend

The Spring backend provides the following features:

- [x] Middle-man between frontend and databases (MongoDB and Redis).
- [x] REST API for the frontend to manage products, orders, and users.
- [x] Authentication for the admin portal via tokens.
- [x] Photo storage volume implementation for products.
- [ ] Payment processing via Stripe.
- [ ] Email integration with AWS SES.
- [ ] WebSocket integration for real-time updates such as order status.

### React Frontend

The React frontend provides the following features:

- [ ] Storefront for browsing and purchasing products.
- [ ] Customer portal for managing orders and account details.
- [ ] Admin portal for managing products, orders, and users.

## Roadmap (WIP)

The following features are planned for each end of the project:

### Spring Backend

- [x] Database Implementation
    - Description: Implement MongoDB and Redis for storing data.
- [x] Admin Authentication Implementation
    - Description: Implement authentication for the admin portal via tokens.
- [ ] Products REST Implementation
    - Description: Operations for products (including photos implementation) and categories.
- [ ] Orders REST Implementation
  - Description: Operations for orders including payment processing and email integration.
- [ ] Customers REST Implementation
    - Description: Operations for customers including registration, authentication, order history, and account details.
- [ ] WebSocket Implementation
    - Description: Real-time updates used in the admin portal for new orders and updating components when they are updated
    for other admin users such as products, categories, orders, banners, etc.

### React Frontend

- [ ] Storefront Implementation
    - Description: Create a storefront for browsing and purchasing products.
- [ ] Search Implementation
    - Description: Implement a search bar for searching products.
- [ ] Cart Implementation
    - Description: Implement a cart for adding and removing products.
- [ ] Customer Registration and Login Implementation
    - Description: Implement a customer registration and login system including guests for checkout.
- [ ] Customer Portal Implementation
    - Description: Implement a customer portal for managing orders and account details.
- [ ] Admin Login Implementation
    - Description: Implement an admin login system for the admin portal.
- [ ] Admin Portal Implementation
    - Description: Implement an admin portal for managing products, orders, and users.
- [ ] WebSocket Implementation
    - Description: Front-end implementation of the WebSocket functionality for real-time updates.

## Contributing

As this project is a proof of concept, it is not currently open to contributions. However, this may change in the future.

## License

The project is licensed under the [MIT License](LICENSE.md). Feel free to use, modify, and distribute the code as needed.
