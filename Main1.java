import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Item {
    String name;
    int weight;
    int value;

    public Item(String name, int weight, int value) {
        this.name = name;
        this.weight = weight;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }
}

class Knapsack {
    int maxWeight;
    int currentWeight;
    List<Item> items;
    List<Item> itemsForC;

    public Knapsack(int maxWeight) {
        this.maxWeight = maxWeight;
        this.currentWeight = 0;
        this.items = new ArrayList<>();
        this.itemsForC = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
        currentWeight = currentWeight + item.getWeight();
        System.out.println("Предмет добавлен: " + item.getName());
    }

    public void addItemForC(Item item) {
        if (item.getWeight() <= maxWeight) {
            itemsForC.add(item);
            System.out.println("Предмет добавлен: " + item.getName());
        } else {
            System.out.println("Невозможно добавить предмет " + item.getName() + ", превышен максимальный вес рюкзака.");
        }
    }

    public static void loadItemsFromFile(Knapsack knapsack, String filePath) {
        File file = new File(filePath);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    int weight = Integer.parseInt(parts[1].trim());
                    int value = Integer.parseInt(parts[2].trim());
                    knapsack.addItemForC(new Item(name, weight, value));
                }
            }
            System.out.println("Предметы успешно загружены из файла.");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка в формате данных: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка при чтении файла: " + e.getMessage());
        }
    }

    public void removeItem(String itemName) {
        Item itemToRemove = null;
        for (Item item : items) {
            if (item.name.equalsIgnoreCase(itemName)) {
                itemToRemove = item;
                break;
            }
        }
        if (itemToRemove != null) {
            items.remove(itemToRemove);
            currentWeight -= itemToRemove.weight;
        }
    }

    public void updateItem(String itemName, int newWeight, int newValue) {
        for (Item item : items) {
            if (item.name.equalsIgnoreCase(itemName)) {
                currentWeight -= item.weight;
                item.weight = newWeight;
                item.value = newValue;
                currentWeight += newWeight;
                break;
            }
        }
    }

    public void displayContents() {
        if (items.isEmpty()) {
            System.out.println("Рюкзак пуст.");
        } else {
            System.out.println("Содержимое рюкзака:");
            for (Item item : items) {
                System.out.println("Название: " + item.name + ", Вес: " + item.weight + ", Стоимость: " + item.value);
            }
            System.out.println("Текущий общий вес: " + currentWeight);
            int n = (int) ((currentWeight * 10) / maxWeight);
            System.out.println("Заполненность рюкзака: " + n + "/10");
        }
    }
    // Реализация методов решения задачи о рюкзаке

    // Рекурсивный метод
    public int recursiveKnapsack(int index, int weightLeft) {
        if (index == itemsForC.size() || weightLeft == 0) {
            return 0;
        }
        if (itemsForC.get(index).weight > weightLeft) {
            return recursiveKnapsack(index + 1, weightLeft);
        }
        int withoutCurrentItem = recursiveKnapsack(index + 1, weightLeft);
        int withCurrentItem = itemsForC.get(index).value + recursiveKnapsack(index + 1, weightLeft - itemsForC.get(index).weight);
        return Math.max(withoutCurrentItem, withCurrentItem);
    }

    // Метод динамического программирования
    public int dynamicKnapsack() {
        int n = itemsForC.size();
        int[][] dp = new int[n + 1][maxWeight + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 1; w <= maxWeight; w++) {
                if (itemsForC.get(i - 1).weight > w) {
                    dp[i][w] = dp[i - 1][w];
                } else {
                    dp[i][w] = Math.max(dp[i - 1][w], itemsForC.get(i - 1).value + dp[i - 1][w - itemsForC.get(i - 1).weight]);
                }
            }
        }

        int maxValue = dp[n][maxWeight];
        if (items.isEmpty()) {
            List<String> selectedItems = new ArrayList<>();
            int totalWeight = maxWeight;

            for (int i = n; i > 0 && maxValue > 0; i--) {
                if (maxValue != dp[i - 1][totalWeight]) {
                    selectedItems.add(itemsForC.get(i - 1).name);
                    items.add(itemsForC.get(i - 1));
                    currentWeight += itemsForC.get(i - 1).weight;
                    maxValue -= itemsForC.get(i - 1).value;
                    totalWeight -= itemsForC.get(i - 1).weight;
                }
            }
        }
        for (Item item : items) {
            System.out.println("Добавлен: " + item.getName());
        }
        return dp[n][maxWeight];
    }

    // Жадный алгоритм
    public int greedyKnapsack(boolean oddStrategy) {
        List<Item> sortedItems = new ArrayList<>(itemsForC);
        if (oddStrategy) {
            sortedItems.sort((a, b) -> b.weight - a.weight);
        } else {
            sortedItems.sort((a, b) -> Double.compare((double) b.value / b.weight, (double) a.value / a.weight));
        }

        int totalValue = 0;
        int currentWeight = 0;

        for (Item item : sortedItems) {
            if (currentWeight + item.weight <= maxWeight) {
                totalValue += item.value;
                currentWeight += item.weight;
            }
        }

        return totalValue;
    }
}

public class Main1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Knapsack knapsack = new Knapsack(0);

        int choice = 0;
        while (choice != 9) {
            System.out.println("Меню:");
            System.out.println("1. Заполнить список предметов из файла");
            System.out.println("2. Добавить предмет");
            System.out.println("3. Изменить предмет");
            System.out.println("4. Удалить предмет");
            System.out.println("5. Установить максимальный вес рюкзака");
            System.out.println("6. Просмотреть содержимое рюкзака");
            System.out.println("7. Выбрать метод решения");
            System.out.println("8. Сравнить методы решения");
            System.out.println("9. Выйти");
            System.out.print("Введите ваш выбор: ");
            choice = scanner.nextInt();
            long t1 = 0;
            long t2 = 0;
            long t3 = 0;
            switch (choice) {
                case 1:
                    System.out.print("Введите путь к файлу: ");
                    String filePath = scanner.next();
                    Knapsack.loadItemsFromFile(knapsack, filePath);
                    break;
                case 2:
                    System.out.print("Введите название предмета: ");
                    String name = scanner.next();
                    System.out.print("Введите вес предмета: ");
                    int weight = scanner.nextInt();
                    System.out.print("Введите стоимость предмета: ");
                    int value = scanner.nextInt();
                    knapsack.addItemForC(new Item(name, weight, value));
                    break;
                case 3:
                    System.out.print("Введите название предмета для изменения: ");
                    String itemNameToUpdate = scanner.next();
                    System.out.print("Введите новый вес предмета: ");
                    int newWeight = scanner.nextInt();
                    System.out.print("Введите новую стоимость предмета: ");
                    int newValue = scanner.nextInt();
                    knapsack.updateItem(itemNameToUpdate, newWeight, newValue);
                    System.out.println("Предмет успешно изменен.");
                    break;
                case 4:
                    System.out.print("Введите название предмета для удаления: ");
                    String itemNameToRemove = scanner.next();
                    knapsack.removeItem(itemNameToRemove);
                    System.out.println("Предмет успешно удален из рюкзака.");
                    break;
                case 5:
                    System.out.print("Введите максимальный вес рюкзака: ");
                    int maxWeight = scanner.nextInt();
                    knapsack = new Knapsack(maxWeight);
                    break;
                case 6:
                    knapsack.displayContents();
                    break;
                case 7:
                    System.out.println("Выберите метод решения:");
                    System.out.println("1. Рекурсивный метод");
                    System.out.println("2. Метод динамического программирования");
                    System.out.println("3. Жадный алгоритм");
                    int solveMethod = scanner.nextInt();
                    switch (solveMethod) {
                        case 1:
                            knapsack.dynamicKnapsack();
                            long startTime = System.nanoTime();
                            System.out.println("Максимальная стоимость: " + knapsack.recursiveKnapsack(0, knapsack.maxWeight));
                            long endTime = System.nanoTime();
                            long duration = (endTime - startTime) / 1_000_000; // Перевод в миллисекунды
                            t1 = duration;
                            System.out.println("Время выполнения: " + duration + " мс");
                            break;
                        case 2:
                            long startTime1 = System.nanoTime();
                            System.out.println("Максимальная стоимость: " + knapsack.dynamicKnapsack());
                            long endTime1 = System.nanoTime();
                            long duration1 = (endTime1 - startTime1) / 1_000_000; // Перевод в миллисекунды
                            t2 = duration1;
                            System.out.println("Время выполнения: " + duration1 + " мс");

                            break;
                        case 3:
                            knapsack.dynamicKnapsack();
                            System.out.println("Выберите стратегию для жадного алгоритма:");
                            long startTime2 = System.nanoTime();
                            int maxPriceOdd = knapsack.greedyKnapsack(true);
                            long endTime2 = System.nanoTime();
                            System.out.println("Максимальная цена (стратегия с нечетным весом): " + maxPriceOdd);
                            long duration2 = (endTime2 - startTime2) / 1_000_000; // Перевод в миллисекунды
                            System.out.println("Время выполнения: " + duration2 + " мс");
                            long startTime3 = System.nanoTime();
                            int maxPriceRatio = knapsack.greedyKnapsack(false);
                            System.out.println("Максимальная цена (стратегия с отношением цена/вес): " + maxPriceRatio);
                            long endTime3 = System.nanoTime();
                            long duration3 = (endTime3 - startTime3) / 1_000_000;
                            System.out.println("Время выполнения: " + duration3 + " мс");
                            break;
                        default:
                            System.out.println("Неверный выбор метода решения.");
                            break;
                    }
                    break;
                case 8:
                    break;
                case 9:
                    System.out.println("Завершение программы. До свидания!");
                    break;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
                    break;
            }
        }

        scanner.close();
    }
}
