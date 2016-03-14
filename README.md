# DBpediaForCategory
This project is a auto category implementation to large-text without training set.

Text Categorization (TC) is continuing to one of the most researched Natural Language Processing problems
due to the ever-increasing amounts of digital documents. It is well known that the traditional supervised and
semi-supervised TC approaches need training data (i.e., a set of hand-labeled documents). However, the task of
manual data labeling is labor intensive and time consuming, especially for a complex TC task with large-scale
documents and categories. In this project, we propose a novel method for auto categorization without training set.

And then, these some tips for packages above all:
SQLServer: this package's function that implement SQL Server connect API.
base: this package introduce some base util for other function to use.
classification: implement the core algorithm.
dBpediaDataExtracter: the function of this package is extract some data we need from DBpedia.
dijkstra: this package's function is implement improved dijkstra algorithm which based on max product of route on NEO4J.
documentToVector: this package's function is pretreatment of documents(data set).
neo4JWDSN: this is a core package for this project that build a semantic graph base on DBpedia and categroies(from data set).
statistic: statistic the finally result.


