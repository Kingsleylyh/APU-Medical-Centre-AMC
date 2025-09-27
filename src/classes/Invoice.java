package classes;

public class Invoice {
	private String invoiceId;
	private String appointmentId;
	private String issuedBy;
	private String issuedTo;
	private double totalAmount;
	private String invoiceDate;
	private String receiptNo;

	public Invoice(String invoiceId, String appointmentId, String issuedBy, String issuedTo, double totalAmount, String invoiceDate, String receiptNo) {
		this.invoiceId = invoiceId;
		this.appointmentId = appointmentId;
		this.issuedBy = issuedBy;
		this.issuedTo = issuedTo;
		this.totalAmount = totalAmount;
		this.invoiceDate = invoiceDate;
		this.receiptNo = receiptNo;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	public String getIssuedTo() {
		return issuedTo;
	}

	public void setIssuedTo(String issuedTo) {
		this.issuedTo = issuedTo;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
}
