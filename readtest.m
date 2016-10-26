data = csvread('test3.csv', 1);
networkLong = data(:,2);
networkLat = data(:,3);
gpsLong = data(:,4);
gpsLat = data(:,5);
fusedLong = data(:,6);
fusedLat = data(:,7);



B431Lat = -33.91815312;
B431Long = 151.2311330;



networkDistm = zeros(1,60);
for i = 1:60
    networkDist = haversineFunc(networkLat(i), B431Lat, networkLong(i), B431Long);
    networkDistm(i) = networkDist;
end
meanNetwork = mean(networkDistm);
stdNetwork = std(networkDistm);

gpsDistm = zeros(1,60);
for i = 1:60
    gpsDist = haversineFunc(gpsLat(i), B431Lat, gpsLong(i), B431Long);
    gpsDistm(i) = gpsDist;
end
meanGPS = mean(gpsDistm);
stdGPS = std(gpsDistm);

fusedDistm = zeros(1,60);
for i = 1:60
    fusedDist = haversineFunc(fusedLat(i), B431Lat, fusedLong(i), B431Long);
    fusedDistm(i) = fusedDist;
end
meanFused = mean(fusedDistm);
stdFused = std(fusedDistm);

networkLat(61) = B431Lat;
networkLong(61) = B431Long;
%c = zeros(61);
%c(61) = 1;
figure
scatter(networkLat, networkLong)
title('Plot of the network coordinates');
xlabel('Latitude (°)');
ylabel('Longitude (°)');
%gpsLat(61) = B431Lat;
%gpsLong(61) = B431Long;
%scatter(gpsLat, gpsLong)

%networkLongMean = mean(data(:,2)); %(:,2) means all of the rows, only the 2nd column
%networkLatMean = mean(data(:,3));
%networkLongStd = std(data(:,2));
%networkLatStd = std(data(:,3));

%gpsLongMean = mean(data(:,4));
%gpsLatMean = mean(data(:,5));
%gpsLongStd = std(data(:,4));
%gpsLatStd = std(data(:,5));

%fusedLongMean = mean(data(:,6));
%fusedLatMean = mean(data(:,7));
%fusedLongStd = std(data(:,6));
%fusedLatStd = std(data(:,7));

%networkLatDiff = abs(networkLatMean - B431Lat);
%networkLongDiff = abs(networkLongMean - B431Long);
%gpsLatDiff = abs(gpsLatMean - B431Lat);
%gpsLongDiff = abs(gpsLongMean - B431Long);
%fusedLatDiff = abs(fusedLatMean - B431Lat);
%fusedLongDiff = abs(fusedLongMean - B431Long);

%convert the differences from degrees to radians
%RadnetworkLatDiff = networkLatDiff * pi/180;
%RadnetworkLongDiff = networkLongDiff * pi/180;
%RadgpsLatDiff = gpsLatDiff * pi/180;
%RadgpsLongDiff = gpsLongDiff * pi/180;
%RadfusedLatDiff = fusedLatDiff * pi/180;
%RadfusedLongDiff = fusedLongDiff * pi/180;

%convert latitude to radians
%RadB431Lat = B431Lat * pi/180;
%RadnetworkLatMean = networkLatMean * pi/180;
%RadgpsLatMean = gpsLatMean * pi/180;
%RadfusedLatMean = fusedLatMean * pi/180;

%r = 6371e3; %radius of the earth
%networkDist = 2*r*asin(sqrt(sin(RadnetworkLatDiff/2)^2 + cos(RadB431Lat)*cos(RadnetworkLatMean)*sin(RadnetworkLongDiff/2)^2))
%gpsDist = 2*r*asin(sqrt(sin(RadgpsLatDiff/2)^2 + cos(RadB431Lat)*cos(RadgpsLatMean)*sin(RadgpsLongDiff/2)^2))
%fusedDist = 2*r*asin(sqrt(sin(RadfusedLatDiff/2)^2 + cos(RadB431Lat)*cos(RadfusedLatMean)*sin(RadfusedLongDiff/2)^2))

%networkDist2 = haversineFunc(networkLatMean, B431Lat, networkLongMean, B431Long)
