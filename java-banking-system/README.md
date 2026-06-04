# 🏦 Banking System API — Spring Boot

A simulated banking backend with account management, deposits/withdrawals, fund transfers, interest calculations, and transaction history.

## 🚀 Features
- Create CHECKING and SAVINGS accounts
- Deposit, withdraw, and transfer funds
- Monthly interest calculation for savings accounts
- Full transaction history per account
- Account freeze/unfreeze for security
- `BigDecimal` for accurate money math (no floating point errors)

## 🛠️ Tech Stack
`Java 17` · `Spring Boot 3.2` · `Maven`

## ▶️ Run
```bash
./mvnw spring-boot:run
# API: http://localhost:8080
```

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/accounts` | All accounts |
| GET | `/api/accounts/summary` | System-wide stats |
| GET | `/api/accounts/{id}` | Account details |
| GET | `/api/accounts/{id}/transactions` | Transaction history |
| POST | `/api/accounts` | Open new account |
| POST | `/api/accounts/{id}/deposit` | Deposit money |
| POST | `/api/accounts/{id}/withdraw` | Withdraw money |
| POST | `/api/accounts/transfer` | Transfer between accounts |
| POST | `/api/accounts/{id}/interest` | Apply monthly interest |
| PATCH | `/api/accounts/{id}/freeze` | Freeze/unfreeze account |

## 🧪 Example: Transfer Money
```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "from": "TR...",
    "to": "TR...",
    "amount": "500.00",
    "description": "Rent payment"
  }'
```

## 💡 Key Concepts
- **Why BigDecimal?** `0.1 + 0.2 = 0.30000000000000004` in double — never use float/double for money
- **Immutable transactions:** Transaction records are never modified, only appended
- **Atomic transfers:** If withdrawal succeeds but deposit fails, rollback is needed (shown with comments)

## 📚 What I Learned
- Financial calculations with `BigDecimal` and `RoundingMode`
- Immutable audit trail (transaction log)
- Domain-driven design: business rules inside model classes
- REST API design for financial operations
