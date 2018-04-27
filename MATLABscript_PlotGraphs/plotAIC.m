function plotAIC(dataArray, fileName)

figure()
x = dataArray(:,1);
y = dataArray(:,2);
% indexmin = find(min(y) == y); 
% xmin = x(indexmin);
%plot(dataArray(:,1), dataArray(:,2), 'LineWidth', 2, 'Marker', 'o', 'MarkerIndices', xmin);
plot(dataArray(:,1), dataArray(:,2), 'LineWidth', 2);
title(strcat('\textbf{AIC Scoring Function} [', fileName, ']'),'Interpreter','latex');
xlabel('$n$ - early classification time point [time]','Interpreter','latex');
ylabel('$AIC$ [bits]','Interpreter','latex');
% xlim([0 24]);
% xticks([0 3 6 12 18 24]);
% xticklabels({'0','3','6','12','18','24'});
grid on;
grid minor;

end

