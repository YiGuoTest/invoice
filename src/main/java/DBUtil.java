import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yguo on 8/27/17.
 */
public class DBUtil {
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /*
    Connect to db.
     */
    public Connection connectToDB(String url, String userName, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            return connection;
        }
    }

    /*
    Create Customer in DB
     */
    public Customer createCustomer(Customer customer) {
        Customer response = null;
        int customerId = -1;

        try {
            preparedStatement = connection.prepareStatement("insert into Customer values (default, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());

            // Save to DB
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            while(keys.next()) {
                customerId = keys.getInt(1);
                break;
            }

            if (customerId == -1) {
                throw new SQLException("Insertion failure");
            }
            preparedStatement.close();

            // Read data back to return.
            preparedStatement = connection.prepareStatement("select * from Customer where customer_id = ? ; ");
            preparedStatement.setInt(1, customerId);
            resultSet = preparedStatement.executeQuery();

            // Get Customer from resultSet.
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                int id = resultSet.getInt("customer_id");
                response = new Customer(name, email);
                response.setId(id);

                break;
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSetAndPreparedStatement();
        }
        return response;
    }

    /*
    Create line in DB.
     */
    public Line createLine(Line line) {
        Line response = null;
        int lineId = -1;

        try {

            preparedStatement = connection.prepareStatement("insert into TxnLine values (default, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, line.getDescription());
            preparedStatement.setDouble(2, line.getAmount());

            // Save to DB
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            while(keys.next()) {
                lineId = keys.getInt(1);
                break;
            }

            if (lineId == -1) {
                throw new SQLException("Insertion failure");
            }
            preparedStatement.close();

            // Get
            response = getLine(lineId);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSetAndPreparedStatement();
        }
        return response;
    }

    /*
    Get txn line.
     */
    private Line getLine(int lineId) {
        Line response = null;

        try {
            preparedStatement = connection.prepareStatement("select * from TxnLine where line_id = ? ; ");
            preparedStatement.setInt(1, lineId);
            resultSet = preparedStatement.executeQuery();

            // Get line from resultSet.
            while (resultSet.next()) {
                String description = resultSet.getString("description");
                double amount = resultSet.getDouble("amount");
                int id = resultSet.getInt("line_id");
                response = new Line(description, amount);
                response.setId(id);

                break;
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }

    /*
    Create Invoice.
     */
    public Invoice createInvoice(Invoice invoice) {
        Invoice response = null;
        int invoiceId = -1;
        StringBuilder sb = new StringBuilder();

        /* Note: Based on https://docs.oracle.com/javase/tutorial/jdbc/basics/array.html: MySQL and Java DB currently
        do not support the ARRAY SQL data type. Consequently, no JDBC tutorial example is available to demonstrate
        the Array JDBC data type.
        Because of this, we are using a text to store a list of comma separated line ids.
         */
        for (Line line : invoice.getLines()) {
            Line createdLine = createLine(line);
            sb.append(createdLine.getId());
            sb.append(',');
        }
        sb.deleteCharAt(sb.lastIndexOf(","));

        try {
            preparedStatement = connection.prepareStatement("insert into Txn values (default, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, invoice.getCustomerId());
            preparedStatement.setDate(2, new java.sql.Date(invoice.getDueDate().getTime()));
            preparedStatement.setDouble(3, invoice.getTotalAmt());
            preparedStatement.setString(4, sb.toString());

            // Save to DB
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            while(keys.next()) {
                invoiceId = keys.getInt(1);
                break;
            }

            if (invoiceId == -1) {
                throw new SQLException("Insertion failure");
            }
            preparedStatement.close();

            // Read data back to return.
            preparedStatement = connection.prepareStatement("select * from Txn where txn_id = ? ; ");
            preparedStatement.setInt(1, invoiceId);
            resultSet = preparedStatement.executeQuery();

            // Get Invoice from resultSet.
            while (resultSet.next()) {
                int txnId = resultSet.getInt("txn_id");
                int customer_id = resultSet.getInt("customer_id");
                Date dueDate = resultSet.getDate("due_date");
                double totalAmt = resultSet.getDouble("total_amount");

                // Retrieve all lines for this invoice.
                String ids = resultSet.getString("line_ids");
                String[] idsArray = ids.split(",");
                List<Line> dbLines = new ArrayList<Line>();
                for (String id : idsArray) {
                    dbLines.add(getLine(Integer.valueOf(id)));
                }

                response = new Invoice(customer_id, dueDate, dbLines);
                response.setId(txnId);
                response.setTotalAmt(totalAmt);

                break;
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSetAndPreparedStatement();
        }
        return response;
    }

    /*
    Close resultSet and preparedstatement.
     */
    private void closeResultSetAndPreparedStatement() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (Exception e) {

        }
    }
}
