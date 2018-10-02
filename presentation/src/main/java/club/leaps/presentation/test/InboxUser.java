package club.leaps.presentation.test;

/**
 * Created by IvanGachmov on 12/1/2017.
 */

public class InboxUser {

    private String name;
    private String image;
    private String id;
    private String device_token;

    public InboxUser() {
    }

    public InboxUser(String name, String image, String id, String device_token) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
