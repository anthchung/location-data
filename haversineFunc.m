function distance = haversineFunc(latitude1, latitude2, longitude1, longitude2)
    r = 6371e3;
    latitude1 = latitude1*pi/180;
    latitude2 = latitude2*pi/180;
    longitude1 = longitude1*pi/180;
    longitude2 = longitude2*pi/180;
    distance = 2*r*asin(sqrt(sin(abs(latitude1-latitude2)/2)^2 + cos(latitude1)*cos(latitude2)*sin(abs(longitude1-longitude2)/2)^2));
end

