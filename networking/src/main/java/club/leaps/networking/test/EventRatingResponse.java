package club.leaps.networking.test;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ivan on 9/1/2017.
 */

public class EventRatingResponse implements Serializable {


    @SerializedName("comment_id")
    private long comment_id;
    @SerializedName("user_id")
    private long userId;
    @SerializedName("rating")
    private float rating;
    @SerializedName("event_owner_name")
    private String ownerName;
    @SerializedName("event_owner_image")
    private String ownerImageUrl;
    @SerializedName("comment")
    private String comment;
    @SerializedName("comment_image")
    private String commentImageUrl;
    @SerializedName("date_created")
    private long date;



    public EventRatingResponse(long comment_id, float rating, String ownerName, String ownerImageUrl, String comment, String commentImageUrl, long date, long userId) {
        this.comment_id = comment_id;
        this.rating = rating;
        this.ownerName = ownerName;
        this.ownerImageUrl = ownerImageUrl;
        this.comment = comment;
        this.commentImageUrl = commentImageUrl;
        this.date = date;
        this.userId = userId;
    }


    public long getComment_id() {
        return comment_id;
    }

    public void setComment_id(long comment_id) {
        this.comment_id = comment_id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerImageUrl() {
        return ownerImageUrl;
    }

    public void setOwnerImageUrl(String ownerImageUrl) {
        this.ownerImageUrl = ownerImageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentImageUrl() {
        return commentImageUrl;
    }

    public void setCommentImageUrl(String commentImageUrl) {
        this.commentImageUrl = commentImageUrl;
    }

    public Date getDate() {
            return new Date(date);

    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
