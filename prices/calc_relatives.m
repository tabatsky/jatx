function [all_goods_rel, all_goods_norm, mean_, std_, percent_std_] = calc_relatives(all_goods, time)
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
end