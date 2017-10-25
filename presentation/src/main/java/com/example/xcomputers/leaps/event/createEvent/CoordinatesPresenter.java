package com.example.xcomputers.leaps.event.createEvent;

import com.example.networking.test.AddressLocation;
import com.example.networking.test.Coordinates;
import com.example.networking.test.CoordinatesService;
import com.example.xcomputers.leaps.base.BasePresenter;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Ivan on 10/16/2017.
 */

public class CoordinatesPresenter extends BasePresenter {


    private CoordinatesService service;
    private Subject<Coordinates, Coordinates> coordinatesSubject;
    private Subject<Throwable, Throwable> errorCoordinatesSubject;
    private Subject<AddressLocation, AddressLocation> addressSubject;
    private Subject<Throwable, Throwable> errorAddressSubject;

    public CoordinatesPresenter(){
        service = new CoordinatesService();
        coordinatesSubject = PublishSubject.create();
        errorCoordinatesSubject = PublishSubject.create();
        addressSubject = PublishSubject.create();
        errorAddressSubject = PublishSubject.create();
    }

    public void getCoordinates(AddressLocation address){
        service.getCoordinates(address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(coordinates->{
                    coordinatesSubject.onNext(coordinates);
                }, throwable -> {
                    errorHandler().call(throwable);
                    errorCoordinatesSubject.onNext(null);
                });
    }

    public void getAddresss(double latitude, double longitude){
        service.getAddress(latitude,longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> {
                    addressSubject.onNext(address);
                }, throwable -> {
                    errorHandler().call(throwable);
                    errorAddressSubject.onNext(null);
                });


    }


    public Observable<Coordinates> getCoordinatesObservable(){
        return coordinatesSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorCoordinatesObservable(){
        return errorCoordinatesSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AddressLocation> getAddressSubject(){
        return addressSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorAddressSubject(){
        return errorAddressSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }


}
