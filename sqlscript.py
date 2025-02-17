import random
import datetime

# Configuration
total_sales_target = 500000
weeks = 26
items = [
    #ID, Name,    Category,      Price
    (1, "Classic Milk Tea", "Milk Tea", 4.50),
    (12, "Ginger Tea", "Brewed Tea", 4.50),
    (23, "Matcha with Fresh Milk", "Fresh Milk", 5.25),
    (40, "Mango Green Tea", "Fruit Tea", 4.95),
    (15, "Mango Mojito", "Tea Mojito", 5.50),
    (62, "Honey Tea", "Brewed Tea", 4.50),
    (17, "Coffee Ice Blended with Ice Cream", "Ice Blended", 6.35),
    (28, "Thai Pearl Milk Tea", "Milk Tea", 5.50),
    (19, "Creama Tea", "Creama", 5.25),
    (15, "Peach Mojito", "Tea Mojito", 5.50),
    (24, "Cocoa Lover with Fresh Milk", "Fresh Milk", 5.25),
    (4, "Oreo Ice Blended with Pearl", "Ice Blended", 6.35)
]

start_date = datetime.date.today() - datetime.timedelta(days=7 * weeks)
end_date = datetime.date.today()

output_file = "insert_sales.sql"

sales = []
total_sales = 0

while total_sales < total_sales_target:
    item = random.choice(items)
    sale_price = item[3]
    sale_date = start_date + datetime.timedelta(days=random.randint(0, (end_date - start_date).days))
    sale_time = datetime.time(random.randint(8, 22), random.randint(0, 59), random.randint(0, 59))
    customer_id = random.randint(9000, 9999)

    #if total_sales + sale_price > total_sales_target:
    #    break  # Stop if next sale exceeds target

    sales.append((item[0], item[1], item[2], round(sale_price, 4), sale_date, sale_time, customer_id))
    total_sales += sale_price

# Write to SQL file
with open(output_file, "w") as f:
    f.write("INSERT INTO Sales (ItemID, ItemName, Category, SalePrice, SaleDate, SaleTime, CustomerID) VALUES\n")
    values = [
        f"({s[0]}, '{s[1]}', '{s[2]}', {s[3]}, '{s[4]}', '{s[5]}', {s[6]})"
        for s in sales
    ]
    f.write(",\n".join(values) + ";\n")

print(f"Generated {len(sales)} sales records totaling ${total_sales:.2f} in {output_file}.")
