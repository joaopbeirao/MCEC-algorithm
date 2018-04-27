function plotLL(dataArray,fileName)

figure()
plot(dataArray(:,1), dataArray(:,2), 'LineWidth', 2);
title(strcat('\textbf{Log-likelihood} [', fileName, ']'),'Interpreter','latex')
xlabel('$n$ - early classification time point [time]','Interpreter','latex');
ylabel('$-LL$ [bits]','Interpreter','latex');
% xlim([0 24]);
% xticks([0 3 6 12 18 24]);
% xticklabels({'0','3','6','12','18','24'});
grid on;
grid minor;

end

