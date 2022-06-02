#!/bin/bash
if (( $# != 1 )); then
	echo "usage: bash plot.bash [dataset name]"
	exit 1
fi
python3 metrics_per_capacity.py "$1" &
python3 metrics_per_n_vehicles.py "$1" &
python3 metrics_per_rps.py "$1" &
python3 metrics_per_sample.py "$1" &
python3 metrics_per_time_lag.py "$1" &
python3 vskt_metrics_per_max_schedule_size.py "$1"
