package com.example.networking.test;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;

import rx.Observable;

/**
 * Created by Ivan on 10/16/2017.
 */

@RetrofitInterface(retrofitApi = CoordinatesApi.class)
public class CoordinatesService  extends BaseService<CoordinatesApi> {

    public Observable<Coordinates> getCoordinates (AddressLocation address) {
        return serviceApi.getCoordinates(address);
    }

    public Observable<AddressLocation> getAddress (double latitude, double longitude) {
        return serviceApi.getAddress(new Coordinates(latitude,longitude));
    }



}
