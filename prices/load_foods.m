function [all_goods, all_names, all_coefs, all_coef_units, time, real_time] = load_foods()
    xls = xlsread('data_1960_2014_foods.xls');

    ii = 1:711;
    jj = 1:13:711;
    ii(jj) = [];

    data = xls(ii,2:22);

    time = 1:656;
    real_time = 1960 + time/12; 

    barley = data(:,1); % ячмень
    cocoa = data(:,2); % какао
    coffee_arabica = data(:,3); % кофе арабика
    coffee_robusta = data(:,4); % кофе робуста
    cotton = data(:,5); % хлопок
    logs = data(:,6); % бревна
    maize = data(:,7); % кукуруза
    beef = data(:,8); % говядина
    chicken = data(:,9); % курица
    rice = data(:,10); % рис
    sorghum = data(:,11); % сорго
    soybeans = data(:,12); % соя
    sugar_eu = data(:,13); % сахар
    sugar_us = data(:,14); % сахар
    sugar_world = data(:,15); % сахар
    tea_colombo = data(:,16); % чай
    tea_kokata = data(:,17); % чай
    tea_mombasa = data(:,18); % чай
    tea_average = data(:,19); % чай
    tobacco = data(:,20); % табак
    wheat = data(:,21); % пшеница

    goods1 = [barley maize rice sorghum soybeans wheat];
    names1 = {'Ячмень','Кукуруза','Рис','Сорго','Соя','Пшеница'};
    coefs1 = [10^6 10^6 10^6 10^6 10^6 10^6];
    coef_units1 = {'г','г','г','г','г','г'};
    goods2 = [cotton logs coffee_arabica cocoa sugar_world tea_average tobacco];
    names2 = {'Хлопок','Бревна','Кофе Арабика','Какао','Сахар','Чай','Табак'};
    coefs2 = [10^3 10^3 10^3 10^3 10^3 10^3 10^6];
    coef_units2 = {'г','куб. дм','г','г','г','г','г'};
    goods3 = [beef chicken];
    names3 = {'Говядина','Курица'};
    coefs3 = [10^3 10^3];
    coef_units3 = {'г','г'};

    all_goods = [goods1 goods2 goods3];    
    all_names = [names1 names2 names3];
    all_coefs = [coefs1 coefs2 coefs3];
    all_coef_units = [coef_units1 coef_units2 coef_units3];
end