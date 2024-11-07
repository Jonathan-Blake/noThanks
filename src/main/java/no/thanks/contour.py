%matplotlib inline
import matplotlib.pyplot as plt
plt.style.use('seaborn-white')
import numpy as np
import csv

with open('result.csv', 'r') as f:
    reader = csv.reader(f)
    data = list(reader)

data_array = np.array(data, dtype=float)

def f(x, y):
    return np.sin(x) ** 10 + np.cos(10 + y * x) * np.cos(x)