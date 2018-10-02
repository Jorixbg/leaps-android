package club.leaps.presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xComputers on 09/07/2017.
 */

public class TagsHolder {

    private List<String> tags;

    private static final TagsHolder ourInstance = new TagsHolder();

    public static TagsHolder getInstance() {
        return ourInstance;
    }

    private TagsHolder() {
        tags = new ArrayList<>();
    }

    public void setTags(List<String> tags){
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }
}
