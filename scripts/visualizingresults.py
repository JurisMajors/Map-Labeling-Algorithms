import matplotlib.pyplot as plt
import statistics
import argparse
import json

parser = argparse.ArgumentParser(description='')
parser.add_argument('--jsonloc',help='location of the json to visualize', required=True)

args = parser.parse_args()
file_loc = args.jsonloc

def get_dictionary(json_location):
    json_f = open(json_location)
    return json.loads(json_f.read())

def apply(func, r, x, objective):
   return [func(list(map(int, r[str(labels)][objective]))) for labels in x]

def visualize_time(r):
    x = sorted([int(labels) for labels in r.keys()])
    y = []
    y.append(apply(statistics.mean, r, x, 'Time'))
    y.append(apply(statistics.median, r, x, 'Time'))

    for y_val in y:
        plt.plot(x, y_val)

    plt.savefig("time.png")

def visualize_optimality(r):
    x = sorted([int(labels) for labels in r.keys()])
    optim_dict = {}
    y = []
    for label in x:
        optim_dict[label] = [float(h) / 10.0 for h in r[str(label)]['Height']]

    y.append([statistics.mean(optim_dict[labels]) for labels in x])
    y.append([statistics.median(optim_dict[labels]) for labels in x])
    for y_var in y:
        plt.plot(x, y_var)
    plt.savefig("optimality.png")

def visualize():
    results = get_dictionary(file_loc)
    # visualize_time(results)
    visualize_optimality(results)


visualize()