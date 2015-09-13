function [result] = print_DP(xx, names, coefs, coef_units)
    goods_count = size(xx,1);
    result = cell(goods_count,3);
    
    for i = 1:goods_count
        result(i,1) = names(i);
        result(i,3) = coef_units(i);
        result(i,2) = num2cell(xx(i)*coefs(i));
    end
end