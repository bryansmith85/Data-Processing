import java.util.HashMap;
import java.util.Map;

interface InMemoryDB {
    int get(String key);

    void put(String key, int val);

    void begin_transaction();

    void commit();

    void rollback();
}

public class inmemoryDB implements InMemoryDB {
    private final Map<String, Integer> database = new HashMap<>();
    private Map<String, Integer> transaction = null;

    @Override
    public int get(String key) {
        if (transaction != null && transaction.containsKey(key)) {
            Integer value = transaction.get(key);
            return (value != null) ? value : -1;
        }
        if (database.containsKey(key)) {
            Integer value = database.get(key);
            return (value != null) ? value : -1;
        }
        return -1;
    }

    @Override
    public void put(String key, int val) {
        if (transaction == null) {
            throw new IllegalStateException("Transaction not in progress");
        }
        transaction.put(key, val);
    }

    @Override
    public void begin_transaction() {
        if (transaction != null) {
            throw new IllegalStateException("Transaction already in progress");
        }
        transaction = new HashMap<>();
    }

    @Override
    public void commit() {
        if (transaction == null) {
            throw new IllegalStateException("No ongoing transaction");
        }
        for (Map.Entry<String, Integer> entry : transaction.entrySet()) {
            database.put(entry.getKey(), entry.getValue());
        }
        transaction = null;
    }

    @Override
    public void rollback() {
        if (transaction == null) {
            throw new IllegalStateException("No ongoing transaction");
        }
        transaction = null;
    }

    // Main method to test the implementation
    public static void main(String[] args) {
        inmemoryDB db = new inmemoryDB();

        System.out.println("Testing InMemoryDB functionality");

        // Sample 1: Get a non-existent key
        System.out.println("Get 'A' (should be null): " + db.get("A"));

        // Sample 2: Put without a transaction
        try {
            db.put("A", 5);
        } catch (IllegalStateException e) {
            System.out.println("Caught exception (expected): " + e.getMessage());
        }

        // Sample 3: Start a transaction and put a key
        db.begin_transaction();
        db.put("A", 5);

        // Sample 4: Get a key within a transaction
        System.out.println("Get 'A' in transaction (should be null): " + db.get("A"));

        // Sample 5: Update a key within a transaction
        db.put("A", 6);

        // Sample 6: Commit the transaction
        db.commit();

        // Sample 7: Get a key after commit
        System.out.println("Get 'A' after commit (should be 6): " + db.get("A"));

        // Sample 8: Commit without a transaction
        try {
            db.commit();
        } catch (IllegalStateException e) {
            System.out.println("Caught exception (expected): " + e.getMessage());
        }

        // Sample 9: Rollback without a transaction
        try {
            db.rollback();
        } catch (IllegalStateException e) {
            System.out.println("Caught exception (expected): " + e.getMessage());
        }

        // Sample 10: Start a transaction, add a key, and rollback
        db.begin_transaction();
        db.put("B", 10);
        db.rollback();

        // Sample 11: Get a key after rollback
        System.out.println("Get 'B' after rollback (should be null): " + db.get("B"));
    }
}
