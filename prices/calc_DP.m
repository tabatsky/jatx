function [xx] = calc_DP(all_goods_rel)
    goods_count = size(all_goods_rel, 2);

    A = cov(all_goods_rel);

    cond = ones(1, goods_count);

    B = [2*A cond'];
    B = [B; [cond 0]];

    b = [zeros(1, goods_count) 1]';
    x = (B^-1)*b;

    bad_index = [];

    for i=1:20
        bad_index = [bad_index; find(x(1:end-1)<0)];
        index = [find(x(1:end-1)>=0); size(x,1)];
        B = B(index,index);
        b = b(index);
        x = (B^-1)*b;
    end

    correct_index = [];

    for i = 1:goods_count
        if isempty(find(bad_index==i))
            correct_index = [correct_index i];
        end
    end

    xx = zeros(goods_count,1);
    xx(correct_index) = x(1:end-1)';
end