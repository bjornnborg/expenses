# Expenses - Batch parse and categorize your expenses


## How does it works

run **java -jar expenses.jar ./some_folder**

Expenses will look in the given folder **"some_folder"** for any **.txt** files. 
Each file found will be parsed and your transactions will be categorized and exported to a **csv** file into the same directory.

Expenses will look for a **.rules** file in the same folder where the jar is located. A .rules files content must look like this:


.\*PAO DE ACUCAR.\* => grocery  
.\*JARDIM SOPHIE.\* => food  
.\*NETMOVIES.\* => entertainment  

Each entry must contain a valid regex to match the transaction description and a category name to be bound with the matching transactions.

## Supported transactions sources

- Itaú Personnalité credit cards
- Itaú Personnalité PF accounts
- Itaú PJ accounts
