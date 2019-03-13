import mnist
import numpy as np
from sklearn.decomposition import PCA
import matplotlib.pyplot as plt

labels = [0, 1]

def read_data(labels):
  mnist.temporary_dir = lambda: '../data/MNIST/'

  train_images = mnist.train_images()
  train_labels = mnist.train_labels()
  entries = zip(train_labels, train_images)
  label_images = {
    labels[0]: [],
    labels[1]: []
  }
  for entry in entries:
    if entry[0] in labels:
      label_images[entry[0]].append(entry[1].flatten())
  return label_images

def construct_matrix(label_images):
  X = np.array(label_images[labels[0]] + label_images[labels[1]])
  color = ['red' if idx < len(label_images[labels[0]]) else 'green' for idx, digit in enumerate(X)]
  return X, color

def show_image(img):
  plt.imshow(img.reshape((28,28)))
  plt.show()