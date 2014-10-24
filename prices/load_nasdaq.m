function [all_goods, all_names, time, real_time] = load_nasdaq()
    xls = xlsread('nasdaq.xls');
    end_time = 2518;
    time = 1:end_time;
    real_time_start = 2004+296/366;
    real_time = real_time_start + ((time-1)*10)/(end_time-1);
    
    data = xls(time,1:7);
    
    apple = data(:,1);
    ford = data(:,2);
    GE = data(:,3);
    google = data(:,4);
    microsoft = data(:,5);
    intel = data(:,6);
    yahoo = data(:,7);
    
    all_goods = [apple, ford, GE, google, microsoft, intel, yahoo];
    all_names = {'Apple','Ford Motors','General Electric','Google','Microsoft','Intel','Yahoo'};
end