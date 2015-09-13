function [all_goods, all_names, all_coefs, all_coef_units, time, real_time] = load_fuel_and_metals()
    xls = xlsread('data_1960_2014_fuel_and_metals.xls');

    ii = 1:711;
    jj = 1:13:711;
    ii(jj) = [];

    data = xls(ii,2:13);

    time = 1:656;
    real_time = 1960 + time/12; 

    aluminum = data(:,1); % алюминий
    copper = data(:,2); % медь
    crude_oil = data(:,3); % нефть
    gold = data(:,4); % золото
    iron_ore = data(:,5); % железная руда
    lead = data(:,6); % свинец
    natural_gas = data(:,7); % газ
    nickel = data(:,8); % никель
    platinum = data(:,9); % платина
    silver = data(:,10); % серебро
    tin = data(:,11); % олово
    zinc = data(:,12); % цинк

    goods1 = [crude_oil natural_gas];
    names1 = {'Нефть', 'Природный газ'};
    coefs1 = [158.988 1000];
    coef_units1 = {'л','тыс. БТЕ'};
    goods2 = [gold silver platinum];
    names2 = {'Золото','Серебро','Платина'};
    coefs2 = [31.1034768*1000 31.1034768 31.1034768*1000];
    coef_units2 = {'мг','г','мг'};
    goods3 = [aluminum copper lead nickel tin zinc iron_ore];
    names3 = {'Алюминий','Медь','Свинец','Никель','Олово','Цинк','Железная руда'};
    coefs3 = [10^3 10^6 10^3 10^6 10^6 10^3 10^3];
    coef_units3 = {'кг','г','кг','г','г','кг','кг'};

    all_goods = [goods1 goods2 goods3];    
    all_names = [names1 names2 names3];
    all_coefs = [coefs1 coefs2 coefs3];
    all_coef_units = [coef_units1 coef_units2 coef_units3];
end