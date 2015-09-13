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
goods2 = [gold silver platinum];
goods3 = [aluminum copper lead nickel tin zinc iron_ore];

all_goods = [goods1 goods2 goods3];

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
    %all_goods_norm(:,i) = all_goods_rel(:,i) / mean_(i);
    all_goods_rel(:,i) = all_goods_rel(:,i) / mean_(i);
    std_(i) = std(all_goods_rel(:,i));
    percent_std_(i) = 100*std_(i)/mean_(i);
end

A = cov(all_goods_rel);

[A_eig_vec A_eig_val] = eig(A);
[A_eig_val,I] = sort(diag(A_eig_val));
A_eig_vec = A_eig_vec(:,I);

all_goods_rel_proj = all_goods_rel*A_eig_vec;

%all_goods_restore = zeros(size(all_goods));

%all_goods_restore = all_goods_rel_proj(:,13)*A_eig_vec(10,13);
%figure;
%plot(real_time, all_goods_restore);

%return;

index = 1:goods_count;

process_all = [];
xcorr_all = [];
fft_all = [];
dct_all = [];

for i = index
   process_ = all_goods_rel_proj(:,i); 
   
   size_ = size(process_,1);
   T = 12;
   
   for t = 1:size_
        start_ = max(1,t-T);
        end_ = min(size_,t+T);
        count_ = end_ - start_ + 1;
        val_ = sum(process_(start_:end_))/count_;
        process_(t) = val_;
   end
   
   %xcorr_ = autocorr(process_);
   

   %process_ = process_/mean(process_);
   xcorr_ = xcorr(process_-mean(process_),'coeff');
   
   fft_ = fftshift(xcorr_);
   dct_ = dct(xcorr_(time(end):end));
   
   
   process_all = [process_all process_];
   xcorr_all = [xcorr_all xcorr_];
   fft_all = [fft_all fft_];
   dct_all = [dct_all dct_];
end


%plot_index = 1:13;
plot_index = 1:12;
i0 = plot_index(1);

plots_count = 6;

corr_time = -655:655;

for i = plot_index
   process_ = process_all(:,i); 
   xcorr_ = xcorr_all(:,i);
   fft_ = fft_all(:,i);
   dct_ = dct_all(:,i);
   
   if rem(2*i-2*i0,plots_count) == 0
      figure; 
   end
   
   subplot(3,2,rem(2*i-2*i0,plots_count)+1);
   plot(real_time,process_);
   xlim([real_time(1) real_time(end)]);
   mean_ = mean(process_);
   std_ = std(process_);
   ylim([mean_-5*std_ mean_+5*std_]);
   subplot(3,2,rem(2*i-2*i0,plots_count)+2);
   plot(corr_time/12,xcorr_);
   xlim([corr_time(1)/12 corr_time(end)/12]);
   ylim([-1 1])
   %plot(fft_);
   %plot(dct_(1:40));
end

ind = 11;

max_ = find(imregionalmax(xcorr_all(time,ind)))/12;
period_max = max_(2:end) - max_(1:end-1);
min_ = find(imregionalmax(-xcorr_all(time,ind)))/12;
period_min = min_(2:end) - min_(1:end-1);


return;





