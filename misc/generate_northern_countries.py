import csv

if __name__ == '__main__':
    with open('countries.csv', newline='') as csvfile:
        reader = csv.DictReader(csvfile, delimiter=',')
        res = []
        for row in reader:
            latitude = float(row['latitude'])
            if latitude > 0:
                res.append(row['country'])
    with open('northern_countries_list.txt', 'w') as f:
        for item in res:
            f.write(f"{item}\n")
    csvfile.close()
    f.close()
