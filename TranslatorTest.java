import org.junit.Assert;
import org.junit.Test;

public class TranslatorTest {

    @Test
    public void translator() {

        Assert.assertEquals(Translator.translator("SELECT * FROM sales LIMIT 10"), "db.sales.find({}).limit(10)");
        Assert.assertEquals(Translator.translator("SELECT name, surname FROM collection"), "db.collection.find({}, {name: 1, surname: 1})");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection SKIP 5 LIMIT 10"), "db.collection.find({}).skip(5).limit(10)");
        Assert.assertEquals(Translator.translator("SELECT * FROM customers WHERE age > 22"), "db.customers.find({age: { $gt: 22 }})");

        Assert.assertEquals(Translator.translator("SELECT * "), "Не найдено названия коллекции");
        Assert.assertEquals(Translator.translator("SELECT * FROM "), "Не найдено названия коллекции");
        Assert.assertEquals(Translator.translator("SELECT * "), "Не найдено названия коллекции");
        Assert.assertEquals(Translator.translator("SELECT FROM collection"), "Не верное название колонки : FROM");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection"), "db.collection.find({})");
        Assert.assertEquals(Translator.translator("SELECT where FROM collection"), "Не верное название колонки : where");
        Assert.assertEquals(Translator.translator("SELECT login, password FROM users"), "db.users.find({}, {login: 1, password: 1})");
        Assert.assertEquals(Translator.translator("SELECT name FROM users SKIP 10"), "db.users.find({}, {name: 1}).skip(10)");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection LIMIT 5"), "db.collection.find({}).limit(5)");
        Assert.assertEquals(Translator.translator("SELECT name, surname FROM customers SKIP 3 LIMIT 1000"), "db.customers.find({}, {name: 1, surname: 1}).skip(3).limit(1000)");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection WHERE price > 1000"), "db.collection.find({price: { $gt: 1000 }})");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection age > 18"), "Ожидалось одно из ключевых слов: \"WHERE\", \"SKIP\", \"LIMIT\"");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection WHERE 100 = length"), "db.collection.find({length: 100})");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection WHERE rang < 15"), "db.collection.find({rang: { $lt: 15 }})");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection WHERE floor <> 14"), "db.collection.find({floor: { $ne: 14 }})");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection WHERE width < 100 AND height = 50"), "db.collection.find({width: { $lt: 100 }, height: 50})");
        Assert.assertEquals(Translator.translator("SELECT * FROM collection WHERE 10000<price SKIP 10"), "db.collection.find({price: { $gt: 10000 }}).skip(10)");
        Assert.assertEquals(Translator.translator("SELECT * FROM people WHERE age<>18 LIMIT 123"), "db.people.find({age: { $ne: 18 }}).limit(123)");
        Assert.assertEquals(Translator.translator("SELECT name, surname FROM players WHERE age > 20 AND score < 100 SKIP 3 LIMIT 6"),"db.players.find({age: { $gt: 20 }, score: { $lt: 100 }}, {name: 1, surname: 1}).skip(3).limit(6)");
    }
}