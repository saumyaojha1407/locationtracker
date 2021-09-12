package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;

public class RoutePoints {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        String startPosition = sc.nextLine();
        String endPosition = sc.nextLine();
        System.out.println(startPosition);
        System.out.println(endPosition);
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();
        Integer fixedInterval = 50;
        Double startLat = new Double(startPosition.split(",")[0]);
        Double startLong = new Double(startPosition.split(",")[1]);
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAEQvKUVouPDENLkQlCF6AAap1Ze-6zMos")
                .build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, startPosition, endPosition);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
//                                    if (startLat.equals(0.0)){
                                        startLat = step.startLocation.lat;
                                        startLong = step.startLocation.lng;
//                                    }
                                    Double endLat = step.endLocation.lat;
                                    Double endLong = step.endLocation.lng;
                                    Double inMetres = (double)step.distance.inMeters;
                                    Integer totalIntervals = (int)step.distance.inMeters/fixedInterval;
                                    for (int m=0;m<=totalIntervals;m++){
                                        Double initialX = (double)fixedInterval*m;
                                        Double latValue = startLat + initialX*((endLat-startLat)/inMetres);
                                        Double longValue = startLong + initialX*((endLong-startLong)/inMetres);
                                        path.add(new LatLng(latValue, longValue));
//                                        startLat = latValue;
//                                        startLong = longValue;
                                    }
//                                    for (int m=0; m<stepDistance.inMeters/fixedInterval;m++)

//                                    public double interpolate(double startValue,
//                                    double endValue,
//                                    double fraction)
//                                    EncodedPolyline points = step.polyline;
//                                    if (points != null) {
//                                        //Decode polyline and add points to list of route coordinates
//                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                        for (com.google.maps.model.LatLng coord : coords) {
//                                            path.add(new LatLng(coord.lat, coord.lng));
//                                        }
//                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            System.out.println(ex);
//            Log.e(TAG, ex.getLocalizedMessage());
        }

        System.out.println(path);
    }
}
