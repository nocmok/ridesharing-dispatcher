import numpy as np
import pandas
from pandas import read_csv
import matplotlib.pyplot as plt
import sys

script_dir=sys.path[0]
ds_name=sys.argv[1]
csv_path=script_dir + "/" + ds_name + "/" + ds_name + "_metrics_per_sample.csv"
png_plot_path=script_dir + "/" + ds_name + "/" + ds_name + "_metrics_per_sample.png"
dataset = read_csv(csv_path)

# 1. service rate
# 2. distance savings relative to personal transport
# 3. distance savings relative to taxi
# 4. avg processing time

fig, axs = plt.subplots(4)
fig.set_size_inches(8,8)
axs[0].set_title("service rate (%)")
axs[1].set_title("distance savings (%) relative to personal transport")
axs[2].set_title("distance savings (%) relative to taxi")
axs[3].set_title("request processing time average (ms)")

for algo in np.unique(dataset["algo"].values):
	samples = dataset[dataset["algo"] == algo]["sample"].values

	service_rate = dataset[dataset["algo"] == algo]["service_rate"].values * 100
	line, = axs[0].plot(samples, service_rate)

	distance_savings = dataset[dataset["algo"] == algo]["distance_savings"].values * 100
	line, = axs[1].plot(samples, distance_savings)

	distance_savings_taxi = (1 - dataset[dataset["algo"] == algo]["total_travelled_distance"].values / dataset[dataset["algo"] == "TaxiSolver"]["total_travelled_distance"].values) * 100
	line, = axs[2].plot(samples, distance_savings_taxi)

	processing_time_avg = dataset[dataset["algo"] == algo]["processing_time_avg"].values
	line, = axs[3].plot(samples, processing_time_avg)
	line.set_label(algo)

plt.subplots_adjust(hspace=0.4, top=1, right=0.8)
handles, labels = axs[3].get_legend_handles_labels()
fig.legend(handles, labels, loc='upper right')
plt.savefig(png_plot_path, dpi=300, bbox_inches="tight", pad_inches=0.2)