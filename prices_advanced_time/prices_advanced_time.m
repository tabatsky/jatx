xls = xlsread('data.xls');

time = 1:399;
real_time = 1979 + time/12; 

time_adjust = 1:200;
time_test = 201:399;

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
