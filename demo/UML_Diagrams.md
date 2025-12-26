# Bank Management System - UML Diagrams

This document contains the UML diagrams for the Bank Management System project.

## 1. Use Case Diagram

```mermaid
usecaseDiagram
    actor "Customer" as User
    actor "Administrator" as Admin

    package "Banking System" {
        usecase "Login" as UC1
        usecase "Register" as UC2
        usecase "Logout" as UC3
        
        usecase "View Dashboard" as UC4
        usecase "Update Profile" as UC5
        usecase "Send Money" as UC6
        usecase "View Transaction History" as UC7
        
        usecase "Admin Dashboard" as UC8
        usecase "View All Customers" as UC9
        usecase "Manage Transactions" as UC10
        usecase "Edit Customer" as UC11
        usecase "Delete Customer" as UC12
        usecase "Add Customer" as UC13
        usecase "View All Transactions" as UC14
    }

    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7

    Admin --> UC1
    Admin --> UC3
    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
    Admin --> UC12
    Admin --> UC13
    Admin --> UC14
```

## 2. Class Diagram

```mermaid
classDiagram
    class DatabaseConnection {
        +getConnection() Connection
    }

    class HttpServlet {
        <<Abstract>>
    }

    namespace Servlets {
        class LoginServlet
        class RegisterServlet
        class LogoutServlet
        class UserDashboardServlet
        class AdminDashboardServlet
        class SendMoneyServlet
        class UpdateProfileServlet
        class ViewCustomersServlet
        class ManageTransactionServlet
        class EditCustomerServlet
        class DeleteCustomerServlet
        class AddCustomerServlet
        class ViewTransactionsServlet
        class AdminViewTransactionsServlet
    }

    namespace Entities {
        class User {
            +int id
            +String email
            +String password
            +String role
        }

        class Customer {
            +int id
            +int userId
            +String name
            +String address
            +String phone
            +String accountNumber
            +double balance
        }

        class Transaction {
            +int id
            +String senderAccount
            +String receiverAccount
            +double amount
            +String type
            +TimeStamp timestamp
        }
    }

    LoginServlet --|> HttpServlet
    RegisterServlet --|> HttpServlet
    UserDashboardServlet --|> HttpServlet
    AdminDashboardServlet --|> HttpServlet
    SendMoneyServlet --|> HttpServlet
    
    LoginServlet ..> DatabaseConnection : uses
    RegisterServlet ..> DatabaseConnection : uses
    UserDashboardServlet ..> DatabaseConnection : uses
    
    User "1" -- "1" Customer : has
    Customer "1" -- "*" Transaction : initiates
```

## 3. Sequence Diagram (Login Scenario)

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant LoginServlet
    participant Database

    User->>Browser: Enter Email & Password
    Browser->>LoginServlet: POST /LoginServlet
    
    activate LoginServlet
    LoginServlet->>Database: getConnection()
    activate Database
    Database-->>LoginServlet: Connection Object
    deactivate Database
    
    LoginServlet->>Database: SELECT * FROM users WHERE email=?
    activate Database
    Database-->>LoginServlet: ResultSet (User Row)
    deactivate Database
    
    alt User Found
        LoginServlet->>LoginServlet: Create Session
        LoginServlet-->>Browser: Redirect to UserDashboard
    else User Not Found
        LoginServlet-->>Browser: Redirect to Login?error=true
    end
    deactivate LoginServlet
```

## 4. Collaboration Diagram (Send Money Scenario)

```mermaid
sequenceDiagram
    participant User
    participant SendMoneyServlet
    participant Database

    note right of User: This represents the interaction flow\nfor sending money between accounts.
    
    User->>SendMoneyServlet: 1. Request Send Money (POST)
    
    activate SendMoneyServlet
    SendMoneyServlet->>Database: 2. Check Balance (SELECT)
    activate Database
    Database-->>SendMoneyServlet: 3. Return Balance
    deactivate Database
    
    SendMoneyServlet->>Database: 4. Deduct Sender (UPDATE)
    activate Database
    Database-->>SendMoneyServlet: 5. Confirm Update
    deactivate Database
    
    SendMoneyServlet->>Database: 6. Add Receiver (UPDATE)
    activate Database
    Database-->>SendMoneyServlet: 7. Confirm Update
    deactivate Database
    
    SendMoneyServlet->>Database: 8. Log Transaction (INSERT)
    activate Database
    Database-->>SendMoneyServlet: 9. Confirm Insert
    deactivate Database
    
    SendMoneyServlet-->>User: 10. Redirect Success
    deactivate SendMoneyServlet
```

## 5. Activity Diagram (Transaction Process)

```mermaid
flowchart TD
    start((Start)) --> input[User Inputs Receiver & Amount]
    input --> check{Check Balance}
    
    check -- Sufficient --> deduct[Deduct Amount from Sender]
    check -- Insufficient --> error[Show Error Message] --> stop((End))
    
    deduct --> add[Add Amount to Receiver]
    add --> log[Log Transaction in DB]
    log --> success[Show Success Message]
    success --> stop
```

## 6. State Chart Diagram (User Session State)

```mermaid
stateDiagram-v2
    [*] --> LoggedOut
    
    LoggedOut --> LoggedIn : Login Success
    LoggedOut --> LoggedOut : Login Failure
    
    state LoggedIn {
        [*] --> Dashboard
        Dashboard --> PerformingAction : User clicks Action
        PerformingAction --> Dashboard : Action Complete
    }
    
    LoggedIn --> LoggedOut : Logout Clicked
    LoggedIn --> SessionExpired : Inactivity Timeout
    SessionExpired --> LoggedOut : Relogin Required
```

## 7. Component Diagram

```mermaid
componentDiagram
    component "Web Browser" as Client
    
    package "Bank Management System" {
        component "Authentication Module" as Auth
        component "User Services" as UserMod
        component "Admin Services" as AdminMod
        component "Database Utility" as DBUtil
    }
    
    component "MySQL Database" as DB
    
    Client --> Auth : Login/Register
    Client --> UserMod : Transaction/Profile
    Client --> AdminMod : Manage Users
    
    Auth ..> DBUtil : uses
    UserMod ..> DBUtil : uses
    AdminMod ..> DBUtil : uses
    
    DBUtil --> DB : JDBC Connection
```

## 8. Deployment Diagram

```mermaid
graph TD
    node1["Client Workstation\n(OS: Windows/Mac/Linux)"] {
        subgraph BrowserContainer [Web Browser]
            UI[HTML/JSP Pages]
        end
    }
    
    node2["Web Server\n(Apache Tomcat 9.0)"] {
        subgraph WebContainer [Web Container]
            Servlet[Java Servlets]
        end
    }
    
    node3["Database Server\n(MySQL 8.0)"] {
        DB[(Bank DB)]
    }
    
    UI -- "HTTP/HTTPS" --> Servlet
    Servlet -- "JDBC (TCP/IP)" --> DB
```
