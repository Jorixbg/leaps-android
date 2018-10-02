package club.leaps.networking.test;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Ivan on 10/16/2017.
 */

public interface CoordinatesApi {

    @POST("/utils/coordinates")
    Observable<Coordinates> getCoordinates(@Body AddressLocation address);

    @POST("/utils/address")
    Observable<AddressLocation> getAddress(@Body Coordinates coordinates);

}
