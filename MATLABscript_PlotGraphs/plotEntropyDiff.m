function plotEntropyDiff(dataArray,fileName)

figure()
plot(dataArray(:,1), dataArray(:,2), 'LineWidth', 2);
title(strcat('\textbf{Difference in entropy} [', fileName, ']'),'Interpreter','latex')
xlabel('$n$ - early classification time point [time]','Interpreter','latex');
ylabel('$H(C|A_n) - H(C|A_nB_n)$ [bits]','Interpreter','latex');
% xlim([0 24]);
% xticks([0 3 6 12 18 24])
% xticklabels({'0','3','6','12','18','24'})
grid on;
grid minor;

end

