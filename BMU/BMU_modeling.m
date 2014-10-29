subject_count = 20;
max_time = 10000;
test_count = 3;

product_av = 100;
%start_money = 100;
start_money = [50 100 150];
import_coeff = 0.15;
export_coeff = 0.15;

init_BMU_course = 1;

BMU_balance_all_tests = [];
BMU_course_all_tests = [];
money_total_all_tests = [];

export_mean = zeros(test_count, 1);
import_mean = zeros(test_count, 1);
BMU_course_mean = zeros(test_count, 1);
BMU_balance_mean = zeros(test_count, 1);

for i = 1:test_count
    
    BMU_course = ones(max_time, 1)*init_BMU_course;
    BMU_balance = zeros(max_time, 1);
    money = ones(max_time, subject_count)*start_money(i);

    export_all_time = zeros(max_time, subject_count);
    import_all_time = zeros(max_time, subject_count);
    
    for t = 2:max_time
        product = ones(1,subject_count)*product_av;
        export = product.*(rand(1,subject_count)*export_coeff)*BMU_course(t-1);
        export_all_time(t,:) = export;
        import = money(t-1,:).*(rand(1,subject_count)*import_coeff);
        import_all_time(t,:) = import;
        money_delta = export - import;
        money(t,:) = money(t-1,:) + money_delta;
        BMU_balance_delta = sum(money_delta)/BMU_course(t-1);
        BMU_balance(t) = BMU_balance(t-1) + BMU_balance_delta;
        
        BMU_course(t) = BMU_course(t-1)*BMU_feedback(BMU_balance(t-1),t);
        
        money_total = sum(money, 2);
    end
    
    BMU_balance_all_tests = [BMU_balance_all_tests BMU_balance];
    BMU_course_all_tests = [BMU_course_all_tests BMU_course];
    money_total_all_tests = [money_total_all_tests money_total];
    
    export_mean(i) = mean(mean(export_all_time(1000:2000,:)));
    import_mean(i) = mean(mean(import_all_time(1000:2000,:)));
    BMU_course_mean(i) = mean(BMU_course(1000:2000));
    BMU_balance_mean(i) = mean(BMU_balance(1000:2000));
end

time = [1:99 100:10:999 1000:100:max_time];
time1 = 1:200;
time2 = 200:50:max_time;

figure;
subplot(3,2,1);
plot(time1, BMU_balance_all_tests(time1,:));
title('Баланс страны в СДЕ, время 1:200');
subplot(3,2,3);
plot(time1, money_total_all_tests(time1,:));
title('Количество национальной валюты, время 1:200');
subplot(3,2,5);
plot(time1, BMU_course_all_tests(time1,:));
title('Курс СДЕ к национальной валюте, время 1:200');
subplot(3,2,2);
plot(time2, BMU_balance_all_tests(time2,:));
title('Баланс страны в СДЕ, время 200:50:10000');
subplot(3,2,4);
plot(time2, money_total_all_tests(time2,:));
title('Количество национальной валюты, время 200:50:10000');
subplot(3,2,6);
plot(time2, BMU_course_all_tests(time2,:));
title('Курс СДЕ к национальной валюте, время 200:50:10000');