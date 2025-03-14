# Notification Service

## Overview

The Notification Service is responsible for handling synchronous notifications related to payment transactions and upcoming events. It ensures users receive real-time notifications via email. This service is built using Java, Spring Boot, and MySQL.

## Tech Stack

- Java 17  
- Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Security)  
- MySQL (AWS RDS for production)  
- Docker (for containerization)  
- Postman (for API Documentation)  
- Lombok (for boilerplate code reduction)  
- JUnit & Mockito (for unit testing)  

## Features

- Synchronous email notifications for payment transactions  
- Notification on successful payment and booking confirmation  
- Upcoming event notifications for users  
- Subscribe/Unsubscribe feature for event notifications  
- Retrieve all upcoming events via API  
- Logging of sent notifications for tracking  
- Error handling for failed notifications  
- API documentation using Postman  

## API Endpoints

| Method | Endpoint                                  | Description                                   |
| ------ | ---------------------------------------- | --------------------------------------------- |
| POST   | /api/notifications/send-payment-notification | Send a notification after payment or booking confirmation |
| POST   | /api/notifications/subscribe            | Subscribe to upcoming event notifications     |
| POST   | /api/notifications/unsubscribe          | Unsubscribe from upcoming event notifications |
| GET    | /api/notifications/upcoming-events      | Retrieve all upcoming events                  |


## Architecture

- Controller Layer: Handles API requests for sending, subscribing, unsubscribing, and retrieving upcoming events.  
- Service Layer: Implements the business logic for processing notifications synchronously.  
- Repository Layer: Manages database operations for storing notification and subscription records.  

## Database Schema

The service uses MySQL with tables like:  

- users (User data, including email information)  
- notifications (Stores sent notifications and their status)  
- subscriptions (Stores subscription details for event notifications)  
- events (Stores upcoming event details)  

## Running Locally

### Prerequisites

- Install Java 17 and Maven  
- Set up MySQL database  
- Configure environment variables  

### Steps

1. Clone the repository:  
   bash
   git clone https://github.com/samruddhithakor/BookOnTheGo_Notification_Microservice.git 
   cd BookOnTheGo_Notification_Service  
   
2. Build and run the application:  
   bash
   mvn clean install  
   mvn spring-boot:run  

## Testing  

Run unit and integration tests:  
bash
mvn test  

## Contributors  

- Anupam Chopra - [GitHub Profile](https://github.com/AnupamC16/BookOnTheGo_Notification_Service)  

## License  

This project is licensed under the MIT License - see the LICENSE file for details.