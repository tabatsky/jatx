xls = xlsread('data.xls');

time = 1:399;
real_time = 1979 + time/12; 

data = xls(time,1:22);

oil = data(:,1);
gold = data(:,2);
iron = data(:,3);
logs = data(:,4);
maize = data(:,5);
beef = data(:,6);
chicken = data(:,7);
gas = data(:,8);
liquid_gas = data(:,9);
tea = data(:,10);
tobacco = data(:,11);
wheat = data(:,12);
sugar = data(:,13);
soy = data(:,14);
silver = data(:,15);
rice = data(:,16);
platinum = data(:,17);
cotton = data(:,18);
copper = data(:,19);
coffee = data(:,20);
coal = data(:,21);
aluminum = data(:,22);

%all_goods = [oil gold iron logs maize beef chicken gas liquid_gas tea tobacco wheat sugar soy silver rice platinum cotton copper coffee coal aluminum];
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

figure;

plot(real_time, all_goods_norm');
axis([real_time(1) real_time(end) 0.0 5.0]);

A = cov(all_goods_rel);

cond = ones(1, goods_count);

B = [2*A cond'];
B = [B; [cond 0]];

b = [zeros(1, goods_count) 1]';
x = (B^-1)*b;

DP = all_goods_rel*x(1:goods_count);

figure;

subplot(2,1,1);
plot(real_time, DP'/mean(DP));
axis([real_time(1) real_time(end) 0.8 1.2]);

DP_mean = mean(DP)
DP_std = std(DP)
DP_percent_std = 100*std(DP)/mean(DP)

USD_per_DP = all_goods*x(1:goods_count);

subplot(2,1,2);
plot(real_time, USD_per_DP);

debt_xls = xlsread('usa_debt.xls');

debt_time = debt_xls(:,1);
end_index = size(debt_time,1);
start_index = (1:end_index)*(debt_time == 1978);
debt_time = debt_time(start_index:end_index);

debt_usd = debt_xls(start_index:end_index,2);
debt_percent = debt_xls(start_index:end_index,3);

debt_time = debt_time + 1; % данные о долге даны на конец года, что равносильно началу следующего

figure;
subplot(2,1,1);
plot(debt_time, debt_usd);
subplot(2,1,2);
plot(debt_time, debt_percent);

a = 1;
b = ones(1,24)/24;

debt_interp = interp1(debt_time, debt_percent, real_time, 'cubic')';
USD_per_DP_mov_av = filter(b, a, USD_per_DP);

debt_and_DP = [USD_per_DP_mov_av/mean(USD_per_DP_mov_av) debt_interp/mean(debt_interp)];

debt_DP_corr = corr2(debt_and_DP(:,1),debt_and_DP(:,2))

figure;
plot(real_time, debt_and_DP);