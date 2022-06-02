import networkx as nx
import osmnx as ox

import matplotlib.pyplot as plt

# <bounds minlat="55.6667200" minlon="37.2749600" maxlat="55.6727000" maxlon="37.2912300"/>
maxLatitude=55.6727000
minLatitude=55.6667200
maxLongitude=37.2912300
minLongitude=37.2749600

graph = ox.graph_from_bbox(maxLatitude,minLatitude,maxLongitude,minLongitude,'drive',False,True,False)

graph = ox.speed.add_edge_speeds(graph)
graph = ox.speed.add_edge_travel_times(graph)

fig, ax = ox.plot_graph(graph)
# fig.savefig('odintsovo.png',dpi=300,bbox_inches="tight",pad_inches=0.2)

ox.io.save_graphml(graph, 'odintsovo-graphml.xml')