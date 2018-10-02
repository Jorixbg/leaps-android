package club.leaps.networking.feed.event;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;

import java.util.List;

import rx.Observable;

/**
 * Created by xComputers on 14/06/2017.
 */
@RetrofitInterface(retrofitApi = FeedEventsApi.class)
public class FeedEventsService extends BaseService<FeedEventsApi>{

    public Observable<FeedEventsResponse> getEvents(String latitude, String longitude){
        return serviceApi.getEvents(latitude, longitude);
    }

    public Observable<FeedFilterEventResponse> getEventsFilter(FeedFilterRequest request){
        return serviceApi.getEventsFilter(request);
    }

    public Observable<List<RealEvent>> getEvents(String type, int page, String latitude, String longtitude){
        return serviceApi.getEvents(page, type, latitude, longtitude);
    }

    public Observable<List<RealEvent>> getPopular(int page){
        return serviceApi.getPopular(page);
    }

    public Observable<List<RealEvent>> getSuited(int page){return serviceApi.getSuited(page);}

    public Observable<List<RealEvent>> getFollowFutureEvent(){return serviceApi.getFollowingFutureEvent();}

    public Observable<List<RealEvent>> getFollowFuturePastEvent(){return serviceApi.getFollowingPastEvent();}

}
