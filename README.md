# CardSystem

## Running
Required: Java8, Maven

To run type: mvn spring-boot:run

## Calls

### Create Card
curl -X POST localhost:8080/cards/card/{cardName}

### Credit Card
curl -X POST localhost:8080/cards/card/{cardName}/credit/{amount}

### Get Card
curl -X GET localhost:8080/cards/card/{cardName}

### Authorize
curl -X POST localhost:8080/cards/card/{cardName}/authorize/{amount}

returns transactionId

### Reverse
curl -X POST localhost:8080/cards/card/{cardName}/reverse/{transactionId}/{amount}

### Capture
curl -X POST localhost:8080/cards/card/{cardName}/capture/{transactionId}

### Refund
curl -X POST localhost:8080/cards/card/{cardName}/refund/{transactionId}/{amount}

### Get Transaction History
curl -X POST localhost:8080/cards/card/{cardName}/transactionHistory