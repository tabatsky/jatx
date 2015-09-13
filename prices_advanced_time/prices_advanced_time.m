xls = xlsread('data.xls');

time = 1:399;
real_time = 1979 + time/12; 

time_adjust = 1:200;
time_test = 201:399;

data = xls(time,1:39);

oil = data(:,1); % �����
gold = data(:,2); % ������ 
iron = data(:,3); % �������� ����
logs = data(:,4); % ������
maize = data(:,5); % ��������
beef = data(:,6); % ��������
chicken = data(:,7); % ������
gas = data(:,8); % ��������� ���
liquid_gas = data(:,9); % ��������� ���
tea = data(:,10); % ���
tobacco = data(:,11); % �����
wheat = data(:,12); % �������
sugar = data(:,13); % �����
soy = data(:,14); % ���
silver = data(:,15); % �������
rice = data(:,16); % ���
platinum = data(:,17); % �������
cotton = data(:,18); % ������
copper = data(:,19); % ����
coffee = data(:,20); % ����
coal = data(:,21); % �����
aluminum = data(:,22); % ��������
bananas = data(:,23); % ������
barley = data(:,24); % ������
cocoa = data(:,25); % �����
coconut_oil = data(:,26); % ��������� �����
groundnut_oil = data(:,27); % ���������� �����
lead = data(:,28); % ������
sheep_meat = data(:,29); % ��������
nickel = data(:,30); % ������
oranges = data(:,31); % ���������
palm_oil = data(:,32); % ��������� �����
shrimp = data(:,33); % ��������
soybean_meal = data(:,34); % ������ ����
soybean_oil = data(:,35); % ������ �����
steel_rebar = data(:,36); % �������� ��������
tin = data(:,37); % �����
woodpulp = data(:,38); % ���������
zinc = data(:,39); % ����

all_goods = [oil gold logs maize beef chicken gas tea tobacco wheat sugar soy rice cotton copper coffee coal];

goods_count = size(all_goods, 2);

geom_average = ones(size(time))';

for i = 1:goods_count 
    geom_average = geom_average .* all_goods(:,i);
end

geom_average = geom_average .^ (1/goods_count);

all_goods_rel = zeros(size(all_goods));
all_goods_norm = zeros(size(all_goods));

mean_ = zeros(1,goods_count);
std_ = zeros(1,goods_count);
percent_std_ = zeros(1,goods_count);

for i = 1:goods_count
    all_goods_rel(:,i) = all_goods(:,i) ./ geom_average;
    mean_(i) = mean(all_goods_rel(:,i));
    all_goods_norm(:,i) = all_goods_rel(:,i) / mean_(i);
    std_(i) = std(all_goods_rel(:,i));
    percent_std_(i) = 100*std_(i)/mean_(i);
end

A = cov(all_goods_rel(time_adjust,:));

cond = ones(1, goods_count);

B = [2*A cond'];
B = [B; [cond 0]];

b = [zeros(1, goods_count) 1]';
x = (B^-1)*b;

DP = all_goods_rel*x(1:goods_count);

figure;

plot(real_time(time_test), DP(time_test)'/mean(DP(time_test)));
axis([real_time(time_test(1)) real_time(time_test(end)) 0.8 1.2]);

DP_mean = mean(DP(time_test))
DP_std = std(DP(time_test))
DP_percent_std = 100*DP_std/DP_mean
DP_min = min(DP(time_test))/DP_mean
DP_max = max(DP(time_test))/DP_mean
