[f_m, f_m_names, f_m_coefs, f_m_coef_units, time, real_time] = load_fuel_and_metals;
[foods, foods_names, foods_coefs, foods_coef_units] = load_foods;

[f_m_rel, f_m_norm, f_m_mean, f_m_std, f_m_percent_std] = calc_relatives(f_m, time);
[foods_rel, foods_norm, foods_mean, foods_std, foods_percent_std] = calc_relatives(foods, time);

f_m_xx = calc_DP(f_m_rel);
foods_xx = calc_DP(foods_rel);

f_m_res = print_DP(f_m_xx, f_m_names, f_m_coefs, f_m_coef_units);
foods_res = print_DP(foods_xx, foods_names, foods_coefs, foods_coef_units);

f_m_DP = f_m_rel*f_m_xx;
foods_DP = foods_rel*foods_xx;

%figure;
%subplot(2,1,1);
%plot(real_time, f_m_DP/mean(f_m_DP));
%subplot(2,1,2);
%plot(real_time, foods_DP/mean(foods_DP));


USD_per_f_m_DP = f_m*f_m_xx;
USD_per_foods_DP = foods*foods_xx;

USD_per_f_m_DP = USD_per_f_m_DP/mean(USD_per_f_m_DP);
USD_per_foods_DP = USD_per_foods_DP/mean(USD_per_foods_DP);

f_m_per_foods = USD_per_foods_DP./USD_per_f_m_DP;

figure;
subplot(2,1,1);
plot(real_time, [USD_per_f_m_DP USD_per_foods_DP]);
title('南1, 南2');
subplot(2,1,2);
plot(real_time, f_m_per_foods);
title('南2/南1');

realty = xlsread('realty.xls');
r_time = 123:3:651;
f_m_per_realty = (realty/mean(realty))./USD_per_f_m_DP(r_time);

figure;
subplot(2,1,1);
plot(real_time(r_time),[USD_per_f_m_DP(r_time) realty/mean(realty)]);
title('南1, 湾溻桄桁铖螯');
subplot(2,1,2);
plot(real_time(r_time), f_m_per_realty);
title('湾溻桄桁铖螯/南1');

[nasdaq nasdaq_names nasdaq_time nasdaq_real_time] = load_nasdaq;
[nasdaq_rel, nasdaq_norm, nasdaq_mean, nasdaq_std, nasdaq_percent_std] = calc_relatives(nasdaq, nasdaq_time);
nasdaq_xx = calc_DP(nasdaq_rel);
nasdaq_DP = nasdaq_rel*nasdaq_xx;
USD_per_nasdaq_DP = nasdaq*nasdaq_xx;
USD_per_nasdaq_DP = USD_per_nasdaq_DP/mean(USD_per_nasdaq_DP);

%figure;
%subplot(2,1,1);
%plot(nasdaq_real_time, nasdaq_DP/mean(nasdaq_DP));
%subplot(2,1,2);
%plot(nasdaq_real_time, USD_per_nasdaq_DP);

f_m_interp = interp1(real_time, USD_per_f_m_DP, nasdaq_real_time, 'cubic')';

figure;
subplot(2,1,1);
plot(nasdaq_real_time, [f_m_interp/mean(f_m_interp) USD_per_nasdaq_DP]);
title('南1, 南-HiTech');
subplot(2,1,2);
plot(nasdaq_real_time, USD_per_nasdaq_DP./f_m_interp);
title('南-HiTech/南1');

m1_m2_xls = xlsread('m1_m2.xls');
m1 = m1_m2_xls(:,3);
m2 = m1_m2_xls(:,4);
cpi = m1_m2_xls(:,5);

figure;
subplot(2,1,1);
plot(real_time, [USD_per_f_m_DP/mean(USD_per_f_m_DP) m1/mean(m1)]);
title('南1,M1');
subplot(2,1,2);
plot(real_time, [USD_per_f_m_DP/mean(USD_per_f_m_DP) m2/mean(m2)]);
title('南1,M2');

DP_m1_corr = corr2(USD_per_f_m_DP, m1)
DP_m2_corr = corr2(USD_per_f_m_DP, m2)