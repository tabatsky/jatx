function [feedback] = BMU_feedback(BMU_balance, t)
    q1 = 0.0001;
    q2 = 0.003;

    %feedback = 1;
    %feedback = 1-q1*BMU_balance;
    %feedback = exp(-q1*BMU_balance);
    %feedback = exp(-q1*BMU_balance)^(1/t);
    feedback = exp(-q1*BMU_balance)^exp(-q2*t);
end