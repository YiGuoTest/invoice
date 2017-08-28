import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.fail;

/**
 * Created by yguo on 8/27/17.
 */
public class TestDB {
    private DBUtil db = new DBUtil();
    private Connection connection = db.connectToDB("jdbc:mysql://localhost:3306/sampledb", "root", "Intuit135");
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @AfterClass
    public void tearDown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Create a customer and an invoice for this customer. Invoice has three lines inside.
     */
    @Test
    public void testCreateCustomerAndInvoice() {
        Random randomGenerator = new Random();
        String randomName = "" + randomGenerator.nextInt(1000);
        Date dueDate = null;
        Customer responseCustomer;

        // Create a Customer and insert it to DB.
        String customerName = "Customer" + randomName;
        String customerEmail = randomName + "@abc.com";
        responseCustomer = createCustomerAndPostToDB(customerName, customerEmail);

        // Invoice
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        String dateInString = "27-Aug-2018";
        try {
            dueDate = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
        // Create lines
        List<Line> lines = new ArrayList<Line>();
        Line line1 = new Line("This is line 1", 10);
        Line line2 = new Line("This is line 2", 10.23);
        Line line3 = new Line("This is line 2", 200);
        lines.add(line1);
        lines.add(line2);
        lines.add(line3);

        // Create Invoice
        createInvoiceAndPostToDB(responseCustomer, dueDate, lines);
    }

    /*
    Create Customer
     */
    public Customer createCustomerAndPostToDB(String name, String email) {
        String responseCustomerS;

        Customer customer = new Customer(name, email);
        System.out.println("Request Customer=" + gson.toJson(customer));

        Customer responseCustomer = db.createCustomer(customer);

        if (responseCustomer != null) {
            responseCustomerS = gson.toJson(responseCustomer);
            System.out.println("Response Customer=" + responseCustomerS);
        } else {
            fail("Failed to create a customer.");
        }

        // Verify response Customer.
        Assert.assertEquals(responseCustomer.getName(), customer.getName());
        Assert.assertEquals(responseCustomer.getEmail(), customer.getEmail());
        Assert.assertNotNull(responseCustomer.getId());

        return responseCustomer;
    }

    /*
    Create Invoice
     */
    public Invoice createInvoiceAndPostToDB(Customer customer, Date dueDate, List<Line> lines) {
        String responseInvoiceS;
        double tempAmount = 0;

        Invoice invoice = new Invoice(customer.getId(), dueDate, lines);
        System.out.println("Request Invoice=" + gson.toJson(invoice));

        Invoice responseInvoice = db.createInvoice(invoice);

        if (responseInvoice != null) {
            responseInvoiceS = gson.toJson(responseInvoice);
            System.out.println("Response Invoice=" + responseInvoiceS);
        } else {
            fail("Failed to create an invoice.");
        }

        // Verify response.
        Assert.assertEquals(responseInvoice.getCustomerId(), customer.getId());
        Assert.assertEquals(responseInvoice.getDueDate(), invoice.getDueDate());

        for (int i = 0; i < lines.size(); i++) {
            Assert.assertEquals(responseInvoice.getLines().get(i).getDescription(), lines.get(i).getDescription());
            Assert.assertEquals(responseInvoice.getLines().get(i).getAmount(), lines.get(i).getAmount());
            tempAmount += lines.get(i).getAmount();
        }

        Assert.assertEquals(responseInvoice.getTotalAmt(), tempAmount);

        return responseInvoice;
    }
}
