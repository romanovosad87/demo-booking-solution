import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DemoApp {

    Map<String, Deque<Transaction>> countConstraintTransactions = new HashMap<>();
    Map<String, Deque<Transaction>> sumConstraintTransactions = new HashMap<>();

    public static void main(String[] args) {

        List<Transaction> transactions = getTransactions();

        List<Constraint> constraints = getConstraints();

        DemoApp demoApp = new DemoApp();
        transactions
                .stream()
                .map(transaction ->  demoApp.processTransaction(transaction, constraints))
                .forEach(System.out::println);
    }

    private static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(1, "411111111111", 0, 15));
        transactions.add(new Transaction(2, "411111111111", 1, 6));
        transactions.add(new Transaction(3, "422222222222", 4, 7));
        transactions.add(new Transaction(4, "411111111111", 40, 8));
        transactions.add(new Transaction(5, "411111111111", 43, 4));
        transactions.add(new Transaction(6, "422222222222", 45, 4.5));
        transactions.add(new Transaction(7, "411111111111", 50, 5));
        transactions.add(new Transaction(8, "411111111111", 80, 19));
        return transactions;
    }

    private static List<Constraint> getConstraints() {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(new Constraint(1, ConstraintType.COUNT, 60, 5));
        constraints.add(new Constraint(1, ConstraintType.SUM, 25, 20));
        return constraints;
    }

    private List<String> processTransaction(Transaction transaction, List<Constraint> constraints) {
        List<String> messages = constraints.stream()
                .map(constraint -> checkTransaction(transaction, constraint))
                .filter(Objects::nonNull)
                .toList();

        if (messages.isEmpty()) {
            messages = List.of(getAcceptanceMessage(transaction));
        }
        return messages;
    }


    private String checkTransaction(Transaction transaction, Constraint constraint) {
        Deque<Transaction> transactions = getTransactionDeque(transaction.cardNumber(), constraint.type());
        transactions.add(transaction);
        cleanOldTransactions(transaction, transactions, constraint.timespan());

        return switch (constraint.type()) {
            case COUNT -> checkCountConstraint(transaction, constraint, transactions);
            case SUM -> checkSumConstraint(transaction, constraint, transactions);
        };
    }


    private void cleanOldTransactions(Transaction transaction, Deque<Transaction> transactions, int timespan) {
        while (transaction.timestamp() - transactions.getFirst().timestamp() > timespan) {
            transactions.pollFirst();
        }
    }

    private Deque<Transaction> getTransactionDeque(String cardNumber, ConstraintType type) {
        return type == ConstraintType.COUNT ?
                countConstraintTransactions.computeIfAbsent(cardNumber, k -> new ArrayDeque<>()) :
                sumConstraintTransactions.computeIfAbsent(cardNumber, k -> new ArrayDeque<>());
    }

    private String checkCountConstraint(Transaction transaction, Constraint constraint, Deque<Transaction> transactions) {
        if (transactions.size() == constraint.transValue()) {
            return getRejectionMessage(transaction, constraint);
        }
        return null;
    }

    private String checkSumConstraint(Transaction transaction, Constraint constraint, Deque<Transaction> transactions) {
        double sumOfTransactions = transactions.stream()
                .mapToDouble(Transaction::transValue)
                .sum();
        if (sumOfTransactions > constraint.transValue()) {
            return getRejectionMessage(transaction, constraint);
        }
        return null;
    }

    private String getAcceptanceMessage(Transaction transaction) {
        return "Accepted transaction: %s".formatted(transaction.id());
    }

    private String getRejectionMessage(Transaction transaction, Constraint constraint) {
        return "Rejected transaction: %s - Violations: %s - %s".formatted(
                transaction.id(), constraint.id(), constraint.type().name());
    }
}

record Transaction(long id, String cardNumber, long timestamp, double transValue){}
enum ConstraintType {
    COUNT, SUM
}
record Constraint(long id, ConstraintType type, int timespan, double transValue){}
