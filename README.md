# MCEC-algorithm

MCEC (Multivariate Correlations for Early Classification) is a Java implementation of an information-theoretic algorithm for examining the early classification opportunity in a dataset, which contains multivariate time series together with their respective class labels.

It receives as input a comma-separated values (CSV) file, containing the time series and the respective class labels. Each line is expected to correspond to one instance, for which the last column corresponds to the class attribute. The columns must include the features grouped per time point, chronologically organized. In addition, the number of attributes (dimensions) is also required as input.
The time series can be univariate or multivariate, however, they must be of fixed length. Features are allowed to be categorical or numeric, but the dataset cannot contain missing values, since the algorithm is not provided with any imputation procedure. Furthermore, the numeric attributes must be discretized.

The outcomes of the difference in entropy, log-likelihood, MDL score, AIC score and classification accuracy, all for n = {1, ..., L} are outputted from the Java program in text files. The implementation uses some functionalities of Weka Data Mining Software and an additional Matlab script is provided for generating the five graphs for representing the results.

The file Appendix_SyntheticExampleOfMCECalgorithm.pdf includes a detailed explanation of the proposed method applied to a synthetically
generated dataset. For clarification purposes, the functioning of the algorithm is expounded through calculation descriptions and graph analysis.

Note: The proposed implementation provides the Markov Lag, an alternative to the standard Early Classification approach. Basically, instead of analysing the correlations from the initial time point until the last, it uses the inverse order (from the last to the first one). In this case, the idea is to check of how much information from the closest past we need, in order to obtain a satisfactory prediction.
