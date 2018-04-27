% ===================================================
% Multivariate Correlations for Early Classification
%               João Pedro Beirão
% Prof. Alexandra Carvalho e Prof. Paulo Mateus
% ===================================================
% This algorithm uses an approach based on a Bayesian network to study the
% correlation between the early states, the later states and the class
% labels of a dataset with multivariate time series for classification.
% ===================================================
% INPUT
% D : dataset, comma-separated value (CSV) file. Note that the multivariate
% time series and the respective class labels must be organized as the
% following:
%   T_i = {X_1, X_2, ..., X_L} --> multivariate time series i
%       L --> time series length
%   X_k = {X_k1, X_k2, ..., X_kN} --> component for time point k
%       N --> number of features per time point (dimensions)
%   Class(T_i) = c_i --> class label
%   (T_i, c_i) : i in {1, ..., w} --> instance i
%       w --> number of instances
%   *each row corresponds to one multivariate time series
%   *the columns contain the features grouped per time point organized
%   chronologically
%   *the last column contains the class attribute
% N : number of dimensions (number of features per time point)
% ===================================================
% OUTPUT
% 4 plots (figures):
%   *difference in entropy
%   *MDL
%   *AIC
%   *classifiers
% ===================================================
close all
clear all
classCheck = 1;
% ===================================================
% Dataset File Name
fileName = fileread('datasetFilename.txt');
% ===================================================
% DIFFERENCE IN ENTROPY
fileID = fopen('dataEntropyDiff.txt','r');
formatSpec = '%f %f';
data = fscanf(fileID,formatSpec, [2 Inf]);
data = data';
fclose(fileID);
% Plot graph
plotEntropyDiff(data, fileName)
% ===================================================
% MDL
fileID = fopen('dataMDL.txt','r');
formatSpec = '%f %f';
data = fscanf(fileID,formatSpec, [2 Inf]);
data = data';
fclose(fileID);
% Plot graph
plotMDL(data, fileName)
% ===================================================
% AIC
fileID = fopen('dataAIC.txt','r');
formatSpec = '%f %f';
data = fscanf(fileID,formatSpec, [2 Inf]);
data = data';
fclose(fileID);
% Plot graph
plotAIC(data, fileName)
% ===================================================
% LL
fileID = fopen('dataLL.txt','r');
formatSpec = '%f %f';
data = fscanf(fileID,formatSpec, [2 Inf]);
data = data';
fclose(fileID);
% Plot graph
plotLL(data, fileName)
% ===================================================
% Classification Results
if (classCheck ~=0)
    fileID = fopen('classResults.txt','r');
    formatSpec = '%d %f %f %f %f %f %f %f';
    data = fscanf(fileID,formatSpec, [8 Inf]);
    %formatSpec = '%d %f %f %f %f %f %f %f %f';
    %data = fscanf(fileID,formatSpec, [9 Inf]);
    data = data';
    fclose(fileID);
    % Plot graph
    plotClassificationResults(data, fileName)
end
