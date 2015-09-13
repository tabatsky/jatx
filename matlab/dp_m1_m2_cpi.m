xls = xlsread('data.xls');

time = 1:399;
real_time = 1979 + time/12; 

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

goods1 = [oil gas liquid_gas coal];
goods2 = [gold platinum silver copper aluminum tin zinc iron lead];
goods3 = [beef sheep_meat chicken shrimp bananas oranges coconut_oil groundnut_oil palm_oil soybean_oil];
goods4 = [maize barley rice soy wheat];
goods5 =  [logs woodpulp cotton tea coffee sugar tobacco cocoa];
all_goods = [goods1 goods2 goods3 goods4 goods5];

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

A = cov(all_goods_rel);

cond = ones(1, goods_count);

B = [2*A cond'];
B = [B; [cond 0]];

b = [zeros(1, goods_count) 1]';
x = (B^-1)*b;

DP = all_goods_rel*x(1:goods_count);

DP_mean = mean(DP)
DP_std = std(DP)
DP_percent_std = 100*std(DP)/mean(DP)

USD_per_DP = all_goods*x(1:goods_count);

m1_m2_xls = xlsread('m1_m2.xls');
m1 = m1_m2_xls(:,3);
m2 = m1_m2_xls(:,4);
cpi = m1_m2_xls(:,5);

figure;
subplot(2,1,1);
plot(real_time, [USD_per_DP/mean(USD_per_DP) m1/mean(m1)]);
subplot(2,1,2);
plot(real_time, [USD_per_DP/mean(USD_per_DP) m2/mean(m2)]);

DP_m1_corr = corr2(USD_per_DP, m1)
DP_m2_corr = corr2(USD_per_DP, m2)

figure;
%plot(cpi);
plot(real_time, [USD_per_DP/mean(USD_per_DP) cpi/mean(cpi)]);

DP_cpi_corr = corr2(USD_per_DP, cpi)

DP_m1 = USD_per_DP./m1;
DP_m2 = USD_per_DP./m2;

figure;
subplot(2,1,1);
plot(real_time, DP_m1);
subplot(2,1,2);
plot(real_time, DP_m2);
