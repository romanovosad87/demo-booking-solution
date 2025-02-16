# Demo Booking Solution

This repository contains the solution to an interview task.

## Task Description

Fraud velocity checks are a fraud prevention mechanism used to flag potential fraudulent credit cards based on constraints that check the rate at which specific cards are observed during transactions.

A transaction contains an id, credit card number, timestamp and a transaction value. A constraint is defined by an id, type, timewindow and a violation value.

You are given a list of transactions and a list of constraints. Work out a solution that checks the given set of constraints against the transactions and flag the transactions that triggered a constraint violation. Each constraint is evaluated independently.

### Constraint types:

1. **COUNT**: Checks if a specific card has been used a certain number of times within a given time interval.
2. **SUM**: Checks if the total sum of transactions for a specific card within a time interval does not exceed the constraint value.

For simplicity, assume all transactions and constraints are in the same currency. The solution should print constraint violations if they occur.

### Example Input

```json
{
  "transactions": [
    {"id": 1, "card": 41111111, "timestamp": 0, "value": 15},
    {"id": 2, "card": 41111111, "timestamp": 1, "value": 6},
    {"id": 3, "card": 42222222, "timestamp": 4, "value": 7},
    {"id": 4, "card": 41111111, "timestamp": 40, "value": 8},
    {"id": 5, "card": 41111111, "timestamp": 43, "value": 4},
    {"id": 6, "card": 42222222, "timestamp": 45, "value": 4.5},
    {"id": 7, "card": 41111111, "timestamp": 50, "value": 5},
    {"id": 8, "card": 41111111, "timestamp": 80, "value": 19}
  ],
  "constraints": [
    {"id": 1, "type": "COUNT", "timespan": 60, "value": 5},
    {"id": 2, "type": "SUM", "timespan": 25, "value": 20}
  ]
}
```

### Example output:
```
Accepted transaction: 1 
Rejected transaction: 2 - Violations: 2 - SUM 
Accepted transaction: 3 
Accepted transaction: 4 
Accepted transaction: 5 
Accepted transaction: 6 
Rejected transaction: 7 - Violations: 1 - COUNT 
Accepted transaction: 8
````


