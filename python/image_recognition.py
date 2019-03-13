import mnist

mnist.temporary_dir = lambda: '../data/MNIST/'

train_images = mnist.train_images()
train_lables = mnist.train_labels()
