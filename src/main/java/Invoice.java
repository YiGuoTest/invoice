import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yguo on 8/27/17.
 */
public class Invoice {
    private int id;
    private Date dueDate;
    private double totalAmt;
    private int customer_id;
    private List<Line> lines = new ArrayList<Line>();

    public Invoice(int customerId, Date dueDate, List<Line> lines) {
        double tempAmt = 0;

        this.customer_id = customerId;
        this.dueDate = dueDate;
        this.lines = lines;

        // Calculate the total amount based on line amounts.
        for (Line line : lines) {
            tempAmt += line.getAmount();
        }

        this.totalAmt = tempAmt;
    }

    public int getCustomerId() {
        return customer_id;
    }

    public void setCustomerId(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getId() {
        return id;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public double getTotalAmt() {
        return totalAmt;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setTotalAmt(double totalAmt) {
        this.totalAmt = totalAmt;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }
}
