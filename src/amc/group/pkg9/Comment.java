package amc.group.pkg9;

public class Comment {
    private String commentId,appointmentId,customerId,message;
    private int rating;

    public Comment(String commentId, String appointmentId, String customerId, String message,int rating){
        this.commentId = commentId;
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.message = message;
        this.rating=rating;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString(){
        return commentId+"|"+appointmentId+"|"+customerId+"|"+message+"|"+rating;
    }
}
