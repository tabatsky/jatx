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

goods1 = [oil gas liquid_gas coal gold platinum silver copper aluminum tin zinc iron lead];
goods2 = [logs woodpulp beef sheep_meat chicken maize barley rice soy bananas oranges coconut_oil groundnut_oil palm_oil];

goods1_count = size(goods1, 2);
goods2_count = size(goods2, 2);

geom_average_1 = ones(size(time))';
geom_average_2 = ones(size(time))';

for i = 1:goods1_count 
    geom_average_1 = geom_average_1 .* goods1(:,i);
end
geom_average_1 = geom_average_1 .^ (1/goods1_count);

for i = 1:goods2_count 
    geom_average_2 = geom_average_2 .* goods2(:,i);
end
geom_average_2 = geom_average_2 .^ (1/goods2_count);


goods_rel_1 = zeros(size(goods1));
goods_norm_1 = zeros(size(goods1));

mean_1 = zeros(1,goods1_count);
std_1 = zeros(1,goods1_count);
percent_std_1 = zeros(1,goods1_count);

for i = 1:goods1_count
    goods_rel_1(:,i) = goods1(:,i) ./ geom_average_1;
    mean_1(i) = mean(goods_rel_1(:,i));
    goods_norm_1(:,i) = goods_rel_1(:,i) / mean_1(i);
    std_1(i) = std(goods_rel_1(:,i));
    percent_std_1(i) = 100*std_1(i)/mean_1(i);
end

goods_rel_2 = zeros(size(goods2));
goods_norm_2 = zeros(size(goods2));

mean_2 = zeros(1,goods2_count);
std_2 = zeros(1,goods2_count);
percent_std_2 = zeros(1,goods2_count);

for i = 1:goods2_count
    goods_rel_2(:,i) = goods2(:,i) ./ geom_average_2;
    mean_2(i) = mean(goods_rel_2(:,i));
    goods_norm_2(:,i) = goods_rel_2(:,i) / mean_2(i);
    std_2(i) = std(goods_rel_2(:,i));
    percent_std_2(i) = 100*std_2(i)/mean_2(i);
end

%return;

figure;

subplot(2,1,1);
plot(real_time, goods_norm_1');
axis([real_time(1) real_time(end) 0.0 5.0]);
subplot(2,1,2);
plot(real_time, goods_norm_2');
axis([real_time(1) real_time(end) 0.0 5.0]);

%return;

A1 = cov(goods_rel_1);
cond1 = ones(1, goods1_count);
B1 = [2*A1 cond1'];
B1 = [B1; [cond1 0]];
b1 = [zeros(1, goods1_count) 1]';
x1 = (B1^-1)*b1;

A2 = cov(goods_rel_2);
cond2 = ones(1, goods2_count);
B2 = [2*A2 cond2'];
B2 = [B2; [cond2 0]];
b2 = [zeros(1, goods2_count) 1]';
x2 = (B2^-1)*b2;

%return;

DP1 = goods_rel_1*x1(1:goods1_count);
DP2 = goods_rel_2*x2(1:goods2_count);

USD_per_DP1 = goods1*x1(1:goods1_count);
USD_per_DP2 = goods2*x2(1:goods2_count);

%DP1_per_DP2 = DP2./DP1;
DP1_per_DP2 = USD_per_DP2./USD_per_DP1;


figure;

plot(real_time, DP1_per_DP2'/mean(DP1_per_DP2));
axis([real_time(1) real_time(end) 0.0 2.0]);

%return;

DP1_per_DP2_mean = mean(DP1_per_DP2)
DP1_per_DP2_std = std(DP1_per_DP2)
DP1_per_DP2_percent_std = 100*DP1_per_DP2_std/DP1_per_DP2_mean
DP1_per_DP2_min = min(DP1_per_DP2)/DP1_per_DP2_mean
DP1_per_DP2_max = max(DP1_per_DP2)/DP1_per_DP2_mean