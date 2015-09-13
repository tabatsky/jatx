function [all_goods, all_names, all_coefs, all_coef_units, time, real_time] = load_foods()
    xls = xlsread('data_1960_2014_foods.xls');

    ii = 1:711;
    jj = 1:13:711;
    ii(jj) = [];

    data = xls(ii,2:22);

    time = 1:656;
    real_time = 1960 + time/12; 

    barley = data(:,1); % ������
    cocoa = data(:,2); % �����
    coffee_arabica = data(:,3); % ���� �������
    coffee_robusta = data(:,4); % ���� �������
    cotton = data(:,5); % ������
    logs = data(:,6); % ������
    maize = data(:,7); % ��������
    beef = data(:,8); % ��������
    chicken = data(:,9); % ������
    rice = data(:,10); % ���
    sorghum = data(:,11); % �����
    soybeans = data(:,12); % ���
    sugar_eu = data(:,13); % �����
    sugar_us = data(:,14); % �����
    sugar_world = data(:,15); % �����
    tea_colombo = data(:,16); % ���
    tea_kokata = data(:,17); % ���
    tea_mombasa = data(:,18); % ���
    tea_average = data(:,19); % ���
    tobacco = data(:,20); % �����
    wheat = data(:,21); % �������

    goods1 = [barley maize rice sorghum soybeans wheat];
    names1 = {'������','��������','���','�����','���','�������'};
    coefs1 = [10^6 10^6 10^6 10^6 10^6 10^6];
    coef_units1 = {'�','�','�','�','�','�'};
    goods2 = [cotton logs coffee_arabica cocoa sugar_world tea_average tobacco];
    names2 = {'������','������','���� �������','�����','�����','���','�����'};
    coefs2 = [10^3 10^3 10^3 10^3 10^3 10^3 10^6];
    coef_units2 = {'�','���. ��','�','�','�','�','�'};
    goods3 = [beef chicken];
    names3 = {'��������','������'};
    coefs3 = [10^3 10^3];
    coef_units3 = {'�','�'};

    all_goods = [goods1 goods2 goods3];    
    all_names = [names1 names2 names3];
    all_coefs = [coefs1 coefs2 coefs3];
    all_coef_units = [coef_units1 coef_units2 coef_units3];
end