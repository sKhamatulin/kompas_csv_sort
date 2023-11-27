import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Sorter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Считываем данные из консоли
        System.out.print("Введите параметры: ");  //"-srcFile=before_sort.csv -dstFile=after_sort.csv -header=1 -sortBy=14";
        String inputLine = scanner.nextLine();

        // Разбиваем строку по пробелам
        String[] tokens = inputLine.split("\\s+");
        scanner.close();


        if (tokens.length < 4) { // если длинна списка меньше 4, то заданы не все параметры или заданы не верно
            System.err.println("Не верно заданы параметры");
        }

        String header = tokens[2].substring(8); // Выбираем значение [заголовок]
        String noSortFilePath = tokens[0].substring(9); // Выбираем значение [исходный файл]
        String sortFilePath = tokens[1].substring(9); // Выбираем значение [рузультирующий]
        int columForSort = Integer.parseInt(tokens[3].substring(8)); // Выбираем значение [колонка для сортировки]
        
        // Читаем файл
        String[][] csvArray = readCSV(noSortFilePath);
        
        // Проверка на заголовок
        if (header.equals("1")) { // если заголовок есть
            String[] firstString = csvArray[0];

            // Удаление заголовка
            String[][] csvArrayWithoutHeader = new String[csvArray.length - 1][csvArray[0].length]; // пустой массив на 1 строку короче
            System.arraycopy(csvArray, 1, csvArrayWithoutHeader, 0, csvArrayWithoutHeader.length); // заполняем массив из исходного (без заголовка)
            
            // Сортировка
            sortArrayByColumn(csvArrayWithoutHeader, columForSort);
            
            // Добавление заголовка обратно
            String[][] sortedCsvArray = new String[csvArray.length][csvArray[0].length]; // пустой массив исходного размера
            sortedCsvArray[0] = firstString; // вставка заголовка на первое место массива
            System.arraycopy(csvArrayWithoutHeader, 0, sortedCsvArray, 1, csvArrayWithoutHeader.length); // заполняем массива из исходного
            
            // Запись
            writeCSV(sortedCsvArray, sortFilePath);

        } else { // если заголовка нет
            sortArrayByColumn(csvArray, columForSort);
            writeCSV(csvArray, sortFilePath);
        }
    }

    static int counter(String filePath) {
        // Проверка на существование исходного файла
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Файл не существует: " + filePath);
        }

        // Подстчет размера будущего массива для записи из исходного файла
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                count++;
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return count;
    }

    static String[][] readCSV(String filePath) {
        // получаем необходимый для записи размер массива
        int numberOfLines = counter(filePath);
        // counter проверит сущесвует ли файл
        String[][] csvArray = new String[numberOfLines][];

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                csvArray[index++] = line.split(";");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        return csvArray;
    }

    static void writeCSV(String[][] data, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Записываем каждую строку массива в файл
            for (String[] row : data) {
                writer.write(String.join(";", row));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи файла: " + e.getMessage());
        }
    }

    static void sortArrayByColumn(String[][] array, int column) {
        Arrays.sort(array, (row1, row2) -> {
            // Преобразование значений в колонке в числа для сравнения
            int value1 = Integer.parseInt(row1[column - 1]);
            int value2 = Integer.parseInt(row2[column - 1]);

            // Сравнение числовых значений в колонке
            return Integer.compare(value1, value2);
        });
    }
}
