import random
import datetime
import numpy
total_sales_target = 500000
weeks = 26

#menu items we are selling with them having unique IDs, Item Name, Category they fall into, and its Price
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
    (16, "Peach Mojito", "Tea Mojito", 5.50),
    (24, "Cocoa Lover with Fresh Milk", "Fresh Milk", 5.25),
    (4, "Oreo Ice Blended with Pearl", "Ice Blended", 6.35)
]

#When we started ShareTea ordering upto today's current order
start_date = datetime.date.today() - datetime.timedelta(days = 7 * weeks)
end_date = datetime.date.today()

output_file = "insert_sales.sql"

#Keeps track of sales
sales = []
total_sales = 0

#Populates database with orders from start to current day
while total_sales < total_sales_target:
    #Randomizes database with information
    item = random.choice(items)
    sale_price = item[3]
    sale_date = start_date + datetime.timedelta(days=random.randint(0, (end_date - start_date).days))
    sale_time = datetime.time(random.randint(8, 22), random.randint(0, 59), random.randint(0, 59))
    customer_id = random.randint(9000, 9999)

    sales.append((item[0], item[1], item[2], round(sale_price, 4), sale_date, sale_time, customer_id))
    total_sales += sale_price
#Adds it to database
with open(output_file, "w") as f:
    f.write("INSERT INTO Sales (ItemID, ItemName, Category, SalePrice, SaleDate, SaleTime, CustomerID) VALUES\n")
    values = [
        f"({s[0]}, '{s[1]}', '{s[2]}', {s[3]}, '{s[4]}', '{s[5]}', {s[6]})"
        for s in sales
    ]
    f.write(",\n".join(values) + ";\n")

#Total sales and current profit made
print(f"Generated {len(sales)} sales records totaling ${total_sales:.2f} in {output_file}.")
print(f"Above are the generated sales for bought items")
