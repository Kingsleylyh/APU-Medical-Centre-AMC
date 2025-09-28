package classes;

public class Comment {
	public enum Recipient { STAFF, DOCTOR }

	private String customerId;
	private String text;
	private Recipient to;
	private String author;

	public Comment(String id, String text, Recipient to, String author) {
		this.customerId = id;
		this.text = text;
		this.to = to;
		this.author = author;
	}

	public String getId() {
		return customerId;
	}

	public void setId(String id) {
		this.customerId = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Recipient getTo() {
		return to;
	}

	public void setTo(Recipient to) {
		this.to = to;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		String safe = text == null ? "" : text.replace(",", "‚");
		return customerId + "|" + author + "|" + to.name() + "|" + safe;
	}

	public static Comment fromString(String line) {
		String[] p = line.split("\\|", -1);
		if (p.length < 4) return null;
		String restored = p[3].replace("‚", ",");
		return new Comment(p[0], restored, Recipient.valueOf(p[2]), p[1]);
	}
}
