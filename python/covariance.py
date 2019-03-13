import numpy as np
import matplotlib.pyplot as plt
import pandas as pd

def read_data():
  df = pd.read_csv('../data/Advertising.csv')
  return df[['TV','Sales']].as_matrix()

def mean_adjust(X):
  tv = X[:,[0]] / np.mean(X[:,[0]])
  sales = X[:,[1]] / np.mean(X[:,[1]])

  return tv, sales, np.hstack([tv, sales])

def eigen_decomposition(X):
  cov = np.matmul(np.transpose(X), X)
  return np.linalg.eig(cov)

def print_vector_span(vector, min, max):
  span = []
  for i in np.arange(0, max):
    span.append(vector * i)
  span = np.array(span)
  plt.plot(span[:,0],span[:,1])