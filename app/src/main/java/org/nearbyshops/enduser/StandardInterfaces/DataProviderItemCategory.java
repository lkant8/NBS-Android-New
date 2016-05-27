package org.nearbyshops.enduser.StandardInterfaces;


import org.nearbyshops.enduser.Model.ItemCategory;
import org.nearbyshops.enduser.StandardInterfacesGeneric.DataProvider;
import org.nearbyshops.enduser.StandardInterfacesGeneric.DataSubscriber;

/**
 * Created by sumeet on 19/5/16.
 */
public interface DataProviderItemCategory extends DataProvider<ItemCategory> {


    public void readMany(
            int parentID,
            int shopID,
            double latCenter,double lonCenter,
            double deliveryRangeMax,
            double deliveryRangeMin,
            double proximity,
            DataSubscriber<ItemCategory> Subscriber
    );

}
