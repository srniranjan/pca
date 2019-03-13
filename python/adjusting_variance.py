import pandas as pd
from sklearn.decomposition import PCA
import matplotlib.pyplot as plt
from sklearn.preprocessing import MinMaxScaler
from sklearn.cluster import KMeans
import numpy as np
import shutil
from shutil import copyfile
import os

def read_data():
  products = pd.read_csv("../data/clothes_classification/product_attributes.csv")
  products_pivot = pd.pivot_table(products, values='attributevalue', columns='attribute_name', index='productid')
  # print(products['attribute_name'].unique())
  attrs = ['Fit', 'Sleeve Length', 'Fabric', 'Neckline', 'Category', 'Season', 'Collection']
  return products_pivot, products_pivot[attrs].fillna(0).as_matrix()

def show_correlation(pca):
  cov = pca.get_covariance()
  cor = np.corrcoef(cov)
  im = plt.imshow(cor)
  for i in range(len(cor)):
    for j in range(len(cor[i])):
        text = plt.text(j, i, '%.2f'%cor[i, j],
                       ha="center", va="center", color="w")
  plt.colorbar(im)
  plt.show()

def mark_clusters(T):
  estimator = KMeans(n_clusters=18).fit(T)
  clusters = estimator.cluster_centers_
  fig, ax = plt.subplots()
  ax.scatter(T[:,0], T[:,1])
  ax.scatter(clusters[:,0], clusters[:,1], c='red', marker='+')
  for idx, row in enumerate(clusters):
    ax.annotate(idx, (row[0], row[1]))
  plt.savefig('./cluster.png')
  return estimator

def seggregate_images(products_pivot, T, estimator):
  clusters_folder = '../data/clothes_classification/clusters'
  shutil.rmtree(clusters_folder)
  os.makedirs(clusters_folder)

  df = pd.DataFrame()
  df["products"] = pd.Series(products_pivot.index)
  principalDf = pd.DataFrame(data = T, columns = ['pc1', 'pc2'])
  finalDf = pd.concat([principalDf, df[['products']]], axis=1)

  for i, txt in enumerate(products_pivot.index):
    [cluster] = estimator.predict([[principalDf['pc1'][i], principalDf['pc2'][i]]])
    selectedDf = finalDf.loc[finalDf['pc1'].isin([principalDf['pc1'][i]]) & finalDf['pc2'].isin([principalDf['pc2'][i]])]
    productid = selectedDf['products'].values[0]
    sourceDir = '../data/clothes_classification/images/'
    targetDir = clusters_folder + '/cluster' + str(cluster)
    if not os.path.exists(targetDir):
      os.mkdir(targetDir)
    copyfile(sourceDir+str(productid)+'.jpg', targetDir+'/'+str(productid)+'.jpg')