import java.util.Scanner;
import java.util.regex.Pattern;


public class Translator {

    public static boolean isValidColumn(String column) {
        return column.matches("[a-zA-z0-9]+") && !column.toUpperCase().equals("SELECT")  && !column.toUpperCase().equals("FROM")  && !column.toUpperCase().equals("WHERE")  && !column.toUpperCase().equals("SKIP")  && !column.toUpperCase().equals("LIMIT");
    }

    public static boolean isValidCollection(String collection) {
        return collection.matches("[a-zA-z0-9]+")  && !collection.toUpperCase().equals("SELECT")  && !collection.toUpperCase().equals("FROM")  && !collection.toUpperCase().equals("WHERE")  && !collection.toUpperCase().equals("SKIP")  && !collection.toUpperCase().equals("LIMIT");
    }

    public static boolean isValidNumeric(String num) {
        return num.matches("[0-9]+");
    }

    public static boolean isValidField(String field) {
        return field.matches("[a-zA-z0-9]+")  && !field.toUpperCase().equals("SELECT")  && !field.toUpperCase().equals("FROM")  && !field.toUpperCase().equals("WHERE")  && !field.toUpperCase().equals("SKIP")  && !field.toUpperCase().equals("LIMIT");
    }

    public static String translator(String sql){
        sql = new String(Pattern.compile(",").matcher(sql).replaceAll(" , "));
        sql = new String(Pattern.compile(">").matcher(sql).replaceAll(" > "));
        sql = new String(Pattern.compile("<").matcher(sql).replaceAll(" < "));
        sql = new String(Pattern.compile("<  >").matcher(sql).replaceAll("<>"));
        sql = new String(Pattern.compile("=").matcher(sql).replaceAll(" = "));
        String[] s = sql.split("[\\s\\n]+");

        StringBuilder mongo = new StringBuilder();
        int condition = 1;
        StringBuilder columns = new StringBuilder();
        StringBuilder conditions = new StringBuilder("");
        for (int i = 0; i < s.length; i++) {
            switch (condition) {
                case 1: {
                    if (s[i].toUpperCase().equals("SELECT")) {
                        condition = 2;
                        mongo.append("db.<collection>");
                    } else {
                        return "SQL выражение должно начинаться со слова \"SELECT\"";
                    }
                    break;
                }
                case 2: {
                    if (s[i].equals("*")) {
                        condition = 3;
                        mongo.append(".find({<conditions>})");
                    } else if (isValidColumn(s[i])) {
                        mongo.append(".find({<conditions>}, {<columns>})");
                        columns.append(s[i] + ": 1");
                        condition = 4;
                    } else {
                        return "Не верное название колонки : " + s[i];
                    }
                    break;
                }
                case 3: {
                    if (s[i].toUpperCase().equals("FROM")) {
                        condition = 6;
                    } else {
                        return "Отсутствует ключевое слово \"FROM\"";
                    }
                    break;
                }
                case 4: {
                    if (s[i].toUpperCase().equals("FROM")) {
                        mongo = new StringBuilder(Pattern.compile("<columns>").matcher(mongo).replaceAll(columns.toString()));
                        condition = 6;
                    } else if (s[i].equals(",")) {
                        columns.append(", ");
                        condition = 5;
                    } else {
                        return "Ожидался символ \",\" или ключевое слово \"FROM\"";
                    }
                    break;
                }
                case 5: {
                    if (isValidColumn(s[i])) {
                        columns.append(s[i] + ": 1");
                        condition = 4;
                    } else {
                        return "Не верное название колонки : " + s[i];
                    }
                    break;
                }
                case 6: {
                    if (isValidCollection(s[i])) {
                        mongo = new StringBuilder(Pattern.compile("<collection>").matcher(mongo).replaceAll(s[i]));
                        condition = 7;
                    } else {
                        return "Не верное название коллекции : " + s[i];
                    }
                    break;
                }
                case 7: {
                    if(s[i].toUpperCase().equals("WHERE")) {
                        condition = 8;
                    } else if(s[i].toUpperCase().equals("SKIP")) {
                        mongo.append(".skip(<skipNum>)");
                        condition = 10;
                    } else if(s[i].toUpperCase().equals("LIMIT")) {
                        mongo.append(".limit(<limitNum>)");
                        condition = 12;
                    } else {
                        return "Ожидалось одно из ключевых слов: \"WHERE\", \"SKIP\", \"LIMIT\"";
                    }
                    break;
                }
                case 8: {
                    try {
                        String term1 = s[i];
                        ++i;
                        String operator = s[i];
                        ++i;
                        String term2 = s[i];
                        if (isValidNumeric(term1)) {
                            String tmp = term2;
                            term2 = term1;
                            term1 = tmp;
                            if(isValidField(term1)) {
                                if(operator.equals("=")){
                                    conditions.append(term1 + ": " + term2);
                                    condition = 9;
                                } else if(operator.equals("<")) {
                                    conditions.append(term1 + ": { \\$gt: " + term2 + " }");
                                    condition = 9;
                                } else if(operator.equals(">")){
                                    conditions.append(term1 + ": { \\$lt: " + term2 + " }");
                                    condition = 9;
                                } else if(operator.equals("<>")){
                                    conditions.append(term1 + ": { \\$ne: " + term2 + " }");
                                    condition = 9;
                                } else {
                                    return "Не верный оператор: " + operator;
                                }
                            } else {
                                return "Не верное название поля : " + term1;
                            }
                        } else if (isValidNumeric(term2)) {
                            if(isValidField(term1)) {
                                if(operator.equals("=")){
                                    conditions.append(term1 + ": " + term2);
                                    condition = 9;
                                }  else if(operator.equals(">")) {
                                    conditions.append(term1 + ": { \\$gt: " + term2 + " }");
                                    condition = 9;
                                } else if(operator.equals("<")){
                                    conditions.append(term1 + ": { \\$lt: " + term2 + " }");
                                    condition = 9;
                                } else if(operator.equals("<>")){
                                    conditions.append(term1 + ": { \\$ne: " + term2 + " }");
                                    condition = 9;
                                } else {
                                    return "Не верный оператор: " + operator;
                                }
                            } else {
                                return "Не верное название поля : " + term1;
                            }
                        } else {
                            return "Не найдено числового значения";
                        }

                        } catch(Exception exc){
                            return "Условие не завершенно";
                        }
                    break;
                }
                case 9: {
                    if(s[i].toUpperCase().equals("SKIP")) {
                        mongo.append(".skip(<skipNum>)");
                        condition = 10;
                    } else if(s[i].toUpperCase().equals("LIMIT")) {
                        mongo.append(".limit(<limitNum>)");
                        condition = 12;
                    } else if(s[i].toUpperCase().equals("AND")) {
                        conditions.append(", ");
                        condition = 8;
                    } else {
                        System.out.println("Ошибка условия 7");
                        break;
                    }
                    break;
                }
                case 10: {
                    if(isValidNumeric(s[i])){
                        mongo = new StringBuilder(Pattern.compile("<skipNum>").matcher(mongo).replaceAll(s[i]));
                        condition = 11;
                    } else {
                        return "Значение в \"SKIP\" должно быть числовое";
                    }
                    break;
                }
                case 11: {
                    if(s[i].toUpperCase().equals("LIMIT")) {
                        mongo.append(".limit(<limitNum>)");
                        condition = 12;
                    } else {
                        return "Ожидался оператор \"LIMIT\"";
                    }
                    break;
                }
                case 12: {
                    if(isValidNumeric(s[i])){
                        mongo = new StringBuilder(Pattern.compile("<limitNum>").matcher(mongo).replaceAll(s[i]));
                        condition = 13;
                    } else {
                        return "Значение в \"LIMIT\" должно быть числовое";
                    }
                    break;
                }
                case 13: {
                    return  "Не найдена команда: " + s[i];
                }
            }
        }
        mongo = new StringBuilder(Pattern.compile("<conditions>").matcher(mongo).replaceAll(conditions.toString()));
        if(condition < 7) {
            return "Не найдено названия коллекции";
        }

        return mongo.toString();
    }


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        StringBuilder sql = new StringBuilder();
        sql.append(scan.nextLine());

        System.out.println(translator(sql.toString()));
    }
}
