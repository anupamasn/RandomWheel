# RandomWheel
The random wheel (Khan et al. 2021) classifies an observation based on the principle of wheel rotation. The present version (v1.0) of the random wheel supports only categorical attributes as input. 

Please prepare the dataset in csv format to test classification with random wheel. Presently it supports testing with splitting the dataset and k-fold cross validation. Once executed, it will take following inputs:

(1) CSV file path: The first header row should include the predictor attribute/class variable name. The data should be comma separated, escaped by double quote, and missing value should be blank or ?

(2) Columns to be discarded: The comma separated index of columns to be discarded for classification. Irrelevant columns (eg. id, name etc.) can be discarded using this option

(3) Class variable: The index of class variable in the dataset

(4) Test option: Supports two options ~ spliting the dataset and k-fold cross validation

(5) Test parameter: Split percentage or number of fold

(6) Depth: The depth of forces to be applied on the wheels

(7) Noise fraction: Fraction of factors to be considered as noisy factors

(8) Trials: The number of trials

(9) Print mode: 1 if all classifications to be printed, 0 otherwise.

Please cite the following article:

Khan, A., Ghosh, S. K., Ghosh, D., & Chattopadhyay, S. (2021). Random wheel: An algorithm for early classification of student performance with confidence. Engineering Applications of Artificial Intelligence. Elsevier.
