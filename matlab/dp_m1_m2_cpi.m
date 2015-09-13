xls = xlsread('data.xls');

time = 1:399;
real_time = 1979 + time/12; 

data = xls(time,1:39);

oil = data(:,1); % нефть
gold = data(:,2); % золото 
iron = data(:,3); % железна€ руда
logs = data(:,4); % бревно
maize = data(:,5); % кукуруза
beef = data(:,6); % гов€дина
chicken = data(:,7); % курица
gas = data(:,8); % природный газ
liquid_gas = data(:,9); % сжиженный газ
tea = data(:,10); % чай
tobacco = data(:,11); % табак
wheat = data(:,12); % пшеница
sugar = data(:,13); % сахар
soy = data(:,14); % со€
silver = data(:,15); % серебро
rice = data(:,16); % рис
platinum = data(:,17); % платина
cotton = data(:,18); % хлопок
copper = data(:,19); % медь
coffee = data(:,20); % кофе
coal = data(:,21); % уголь
aluminum = data(:,22); % алюминий
bananas = data(:,23); % бананы
barley = data(:,24); % €чмень
cocoa = data(:,25); % какао
coconut_oil = data(:,26); % кокосовое масло
groundnut_oil = data(:,27); % арахисовое масло
lead = data(:,28); % свинец
sheep_meat = data(:,29); % баранина
nickel = data(:,30); % никель
oranges = data(:,31); % апельсины
palm_oil = data(:,32); % пальмовое масло
shrimp = data(:,33); % креветки
soybean_meal = data(:,34); % соевый шрот
soybean_oil = data(:,35); % соевое масло
steel_rebar = data(:,36); % стальна€ арматура
tin = data(:,37); % олово
woodpulp = data(:,38); % целлюлоза
zinc = data(:,39); % цинк

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
