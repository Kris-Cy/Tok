package com.sleepy.tok.data_models;

public class Messages {
    private String message;
    private String message_type;
    private boolean seen;
    private String time_sent;
    private String from;


    public Messages (String message, String message_type, boolean seen, String time_sent, String from){
        this.message = message;
        this.message_type = message_type;
        this.seen = seen;
        this.time_sent = time_sent;
        this.from=from;
    }

    public Messages(){}

    public String getFrom() { return from; }

    public void setFrom(String from) { this.from = from; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getMessage_type() { return message_type; }

    public void setMessage_type(String message_type) { this.message_type = message_type; }

    public boolean getSeen() { return seen; }

    public void setSeen(boolean seen) { this.seen = seen; }

    public String getTime_sent() { return time_sent; }

    public void setTime_sent(String time_sent) { this.time_sent = time_sent; }
}
