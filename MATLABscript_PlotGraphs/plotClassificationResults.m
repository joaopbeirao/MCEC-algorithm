function plotClassificationResults(dataArray, fileName)

figure()
plot(dataArray(:,1), dataArray(:,2), 'LineWidth', 2);
hold on
plot(dataArray(:,1), dataArray(:,3), 'LineWidth', 2);
hold on
plot(dataArray(:,1), dataArray(:,4), 'LineWidth', 2);
hold on
plot(dataArray(:,1), dataArray(:,5), 'LineWidth', 2);
hold on
plot(dataArray(:,1), dataArray(:,6), 'LineWidth', 2);
hold on
plot(dataArray(:,1), dataArray(:,7), 'LineWidth', 2);
hold on
plot(dataArray(:,1), dataArray(:,8), 'LineWidth', 2);
%hold on
%plot(dataArray(:,1), dataArray(:,9), 'LineWidth', 2, 'Color',[0.7,0.7,0.9]);
legend('NB','BN','SMO','J48','REPTree','RandomForest','kNN');
%legend('NB','BN','Logi','SMO','J48','REPTree','RandFor','kNN');
title(strcat('\textbf{Early Classification Analysis} [', fileName, ']'),'Interpreter','latex');
xlabel('time series length [time]','Interpreter','latex');
ylabel('Classification Accuracy [\%]','Interpreter','latex');
% xlim([0 24]);
% xticks([0 3 6 12 18 24]);
% xticklabels({'0','3','6','12','18','24'});
grid on;
grid minor;
hold off

end

