package club.leaps.networking.tags;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by xComputers on 09/07/2017.
 */

public interface TagsApi {

    @GET("event/tags")
    Observable<List<String>> getTags();
}
